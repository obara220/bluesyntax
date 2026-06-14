package com.panda.aoodds.esport.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.panda.aoodds.esport.api.service.EsportMatchMarketStatusTransitServiceApi;
import com.panda.aoodds.esport.common.calculate.EsportCalculateOdds;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.constant.FootballCategorySet;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.entity.AoMatchESportEntity;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.entity.HomeAwayScore;
import com.panda.aoodds.esport.common.entity.MarketDto;
import com.panda.aoodds.esport.api.entity.MarketParamEntiy;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.enums.ScoreType;
import com.panda.aoodds.esport.common.exception.ApiException;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.handle.MarketCacheHandler;
import com.panda.aoodds.esport.handle.MarketLoadBalanceHandler;
import com.panda.aoodds.esport.handle.MarketOddsDelayHandler;
import com.panda.aoodds.esport.handle.SupportMarketHandler;
import com.panda.aoodds.esport.handle.db.DbEsportMatchMarketConfigConfigDaoHandler;
import com.panda.aoodds.esport.service.EsportMarketMessageService;
import com.panda.aoodds.esport.service.SubjectEsportMatchMarketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.constant.CommonConstant.SPORT_FOOTBALL;
import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.*;

/**
 * @Author carson
 * @DATE 2024/1/28 14:09
 **/
@Slf4j
@Component
@RefreshScope
public class SubjectEsportMatchMarketImpl implements SubjectEsportMatchMarketManager {
    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;
    @Autowired
    MarketLoadBalanceHandler marketLoadBalanceHandler;
    @Autowired
    private SupportMarketHandler supportMarketHandler;
    @Autowired
    private EsportCalculateOdds esportCalculateOdds;

    @Autowired
    private MarketCacheHandler marketCacheHandler;
    @Autowired
    @Qualifier("esportMarketPushService")
    EsportMarketMessageService esportMarketMessageService;
    @Autowired
    EsportMatchMarketStatusTransitServiceApi esportMatchMarketStatusTransitServiceApi;
    @Autowired
    RedisService redisService;
    @Autowired
    DbEsportMatchMarketConfigConfigDaoHandler dbEsportMatchMarketConfigConfigDaoHandler;
    @Autowired
    private MarketOddsDelayHandler marketOddsDelayHandler;
    private static final String MATCH_LOCK_PREFIX = RedisKeyConstant.MATCH_LOCK + "_notifyMarketMessage_";

    @Override
    public void notifyMarketMessage(String matchId, String linkId, List<Integer> categoryIds) {
        log.info("::{}::SubjectEsportMatchMarketImpl 请求入参:{},categoryIds:{}", linkId, matchId, categoryIds);
        List<Integer> categoryNotIds = new ArrayList<>();
        MarketParamEntiy marketParamEntiy = dbEsportMatchMarketConfigConfigDaoHandler.mongoTempFindOne(matchId);
        if (null == marketParamEntiy) {
            log.info("::{}::notifyMarketMessage,APPLY未配置:{}", linkId, matchId);
            throw new ApiException("APPLY未配置");
        }
        String redisLocKey = MATCH_LOCK_PREFIX + matchId;
        String lockValue = MATCH_LOCK_PREFIX + linkId;
        List<MarketsEntity> marketsEntityList = null;
        AoMatchMarketInfo aoMatchMarketInfo = null;
        String markets;
        try {
            redisService.tryLock(redisLocKey, lockValue, 5, 5);
            //比分异常不处理
            Object errorScoreObj = redisService.hGet(CommonConstant.AO_ESPORT_SCORE_ERROR_KEY, matchId);
            if (!Objects.isNull(errorScoreObj) ) {
                log.info("::{}::notifyMarketMessage,比分异常不处理:{}", linkId, matchId);
                return;
            }
            Long startTime = System.currentTimeMillis();
            supportMarketHandler.supportEsportFootballPeriod(marketParamEntiy);
            if (Integer.valueOf(marketParamEntiy.getMatchClock()) >= 2700) {
                String half1stOr2nd = marketParamEntiy.getHalf1stOr2nd();
                if ("1".equals(half1stOr2nd)) {
                    categoryNotIds.addAll(FootballCategorySet.TYPE.get("ht_goal"));
                    marketParamEntiy.setMatchClock("2700");
                    //bug 100336 还需要将阶段设置为中场休息
                    marketParamEntiy.setPeriod(31);
                    log.error("::{}::上半场超出比赛时长关闭上半场玩法:{}", linkId, categoryNotIds);
                } else if ("2".equals(half1stOr2nd)) {
                    esportMatchMarketStatusTransitServiceApi.upMatchMarketStatus(linkId, Long.valueOf(matchId), "g_goal");
                    return;
                }
            }

            Map<ScoreType, HomeAwayScore<Integer>> scoreTypeHomeAwayScoreMap = supportMarketHandler.supportEsporScores(marketParamEntiy);
            marketParamEntiy.setLinkId(linkId);
            int liveFlag = marketParamEntiy.getHalf1stOr2nd().equals("1") && marketParamEntiy.getMatchClock().equals("0") ? 0 : 1;
            if (liveFlag == 1 && marketOddsDelayHandler.isDelayClosed(matchId)) {
                log.warn("::{}::notifyMarketMessage,赔率延迟已兜底关盘,禁止自动开盘:{}", linkId, matchId);
                return;
            }
            if (liveFlag == 1 && null != redisService.get(AO_ESPORT_MATCH_NO_ODDS_ISSUED)) {
                log.info("::{}::notifyMarketMessage,限频不下发:{}", linkId, matchId);
                return;
            }
            Properties properties = marketParamEntiy.toProperties();
            properties.put("half1stPeriod", "45");
            log.info("::{}::SubjectEsportMatchMarketImpl 请求入参:{}", linkId, properties);
            markets = marketLoadBalanceHandler.getEsportMarkets(properties, "goal", "VERSION1");
            log.info("::{}::SubjectEsportMatchMarketImpl 返回结果", linkId);
            marketsEntityList = JSONObject.parseObject(markets).toJavaObject(MarketDto.class).getMarketsEntityList();
            markets = null;
            aoMatchMarketInfo = new AoMatchMarketInfo();
            aoMatchMarketInfo.setRequestType("g_goal");
            aoMatchMarketInfo.setModifyTime(startTime);
            aoMatchMarketInfo.setStartTime(startTime);
            aoMatchMarketInfo.setSportId(Long.valueOf(SPORT_FOOTBALL));
            aoMatchMarketInfo.setScoreSummary(scoreTypeHomeAwayScoreMap);
            aoMatchMarketInfo.setMatchSourceId(matchId);
            aoMatchMarketInfo.setLiveFlag(liveFlag);
            //aoMatchMarketInfo.setRequestType(modelType);
//        aoMatchMarketInfo.setAwayTeamId(aoMatchInfoEntity.getStandardAwayId() + "");
//        aoMatchMarketInfo.setHomeTeamId(aoMatchInfoEntity.getStandardHomeId() + "");
            aoMatchMarketInfo.setMarketTime(supportMarketHandler.supportMatchTime(marketParamEntiy));
            aoMatchMarketInfo.setPeriod(Integer.valueOf(marketParamEntiy.getHalf1stOr2nd()));
            aoMatchMarketInfo.setLinkeId(linkId);
            log.info("::{}::,marketsEntityList:{}", linkId, JSONObject.toJSONString(marketsEntityList));
            if (CollectionUtils.isEmpty(marketsEntityList)) {
                return;
            }
            if (!CollectionUtils.isEmpty(categoryIds)) {
                marketsEntityList = marketsEntityList.stream().filter(m -> categoryIds.contains(m.getMarketId())).collect(Collectors.toList());
            }
            if (!CollectionUtils.isEmpty(categoryNotIds)) {
                marketsEntityList.stream().filter(m -> categoryNotIds.contains(m.getMarketId())).forEach(m -> {
                    m.setStatus(CommonConstant.NUMBER_TWO);
                });
            }
            //赔率计算
            esportCalculateOdds.calculate(linkId, matchId, marketsEntityList, aoMatchMarketInfo.getLiveFlag(), aoMatchMarketInfo.getSportId());
            aoMatchMarketInfo.setMarketList(marketsEntityList);
            aoMatchMarketInfo.setPushTime(System.currentTimeMillis());
            redisService.hSetField(AO_LASTTIME_ESPORT_MARKET_UPDATE, matchId, System.currentTimeMillis(), AO_1DAYS_KEY_TIME);
            if (1 == aoMatchMarketInfo.getLiveFlag()) {
                //78892 大小盘口规律验证
                marketCacheHandler.marketHandicapDiffVerify(linkId, matchId, aoMatchMarketInfo, aoMatchMarketInfo.getScoreSummary());
                //缓存盘口
                marketCacheHandler.cacheLiveMarketOdds(linkId, matchId, aoMatchMarketInfo);
                //盘口兜底
                marketCacheHandler.marketDifferentClose(linkId, matchId, aoMatchMarketInfo);
            }
            esportMarketMessageService.sendMarketMessage(aoMatchMarketInfo);
            if (1 == aoMatchMarketInfo.getLiveFlag()) {
                marketOddsDelayHandler.recordLastPushTime(matchId);
            }
        }catch (Exception ex){
            log.error("::{}::notifyMarketMessage，回调入参AO赛事ID,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisLocKey, lockValue,ex);
        }finally {
            redisService.unLock(redisLocKey, lockValue);
            log.info("::{}::notifyMarketMessage，回调入参AO赛事ID,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
            if (marketsEntityList != null) {
                marketsEntityList.clear();
            }
            if (aoMatchMarketInfo != null) {
                aoMatchMarketInfo.setMarketList(null);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(FootballCategorySet.TYPE.get("ht_goal"));
    }
}
