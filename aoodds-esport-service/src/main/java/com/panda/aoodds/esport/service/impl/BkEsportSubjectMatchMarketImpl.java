package com.panda.aoodds.esport.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.entity.*;
import com.panda.aoodds.esport.common.calculate.EsportCalculateOdds;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.*;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.service.RedisService;

import com.panda.aoodds.esport.handle.MarketCacheHandler;
import com.panda.aoodds.esport.handle.MarketLoadBalanceHandler;
import com.panda.aoodds.esport.handle.SupportMarketHandler;
import com.panda.aoodds.esport.handle.db.DbBkApplyConfigDaoHandler;
import com.panda.aoodds.esport.handle.db.DbEsportMatchMarketConfigConfigDaoHandler;
import com.panda.aoodds.esport.marketstate.MatchMarketStatusTransit;
import com.panda.aoodds.esport.service.BkEsportSubjectMatchMarketManager;
import com.panda.aoodds.esport.service.EsportMarketMessageService;

import com.panda.aoodds.esport.common.utils.MatchInLiveUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.panda.aoodds.esport.common.constant.CommonConstant.SPORT_BASKETBALL;
import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.*;


@Slf4j
@Component
public class BkEsportSubjectMatchMarketImpl extends BkEsportSubjectMatchMarketManager {

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
    @Lazy
    private MarketCacheHandler marketCacheHandler;
    @Autowired
    private MatchMarketStatusTransit matchMarketStatusTransit;
    @Autowired
    private MatchInLiveUtil matchInLiveUtil;
    @Autowired
    private RedisService redisService;

    @Autowired
    DbEsportMatchMarketConfigConfigDaoHandler dbEsportMatchMarketConfigConfigDaoHandler;

    @Autowired
    private DbBkApplyConfigDaoHandler dbBkApplyConfigDaoHandler;

    @Autowired
    @Qualifier("esportMarketPushService")
    EsportMarketMessageService esportMarketMessageService;

    List<Integer> categoryNotIds = new ArrayList<Integer>() {{  add(11049); }};
    @Override
    public void notifyBkMarketMessage(String linkId, String matchId, List<Integer> categoryIds) {
        log.info("::{}::notifyBkMarketMessage::A01赛事id{}",linkId,matchId);
        Long startTime = System.currentTimeMillis();
        Integer isLive = matchInLiveUtil.matchInLive(matchId, Long.valueOf(SPORT_BASKETBALL));
        log.info("::{}::notifyBkMarketMessage::A01赛事id{}，isLive::{}",linkId,matchId,isLive);

        if(isLive==1){
            redisService.hSetField(AO_LASTTIME_MARKET_UPDATE,matchId,startTime,AO_1DAYS_KEY_TIME);
        }
        if (isLive == 1 && null != redisService.get(AO_ESPORT_MATCH_NO_ODDS_ISSUED)) {
            log.info("::{}::notifyBkMarketMessage::A01赛事id{}，赛事限频不下发::{}", linkId, matchId, isLive);
            return;
        }
        Integer[] matchPeriodAndTime = supportMarketHandler.supportGetBkPeriod(matchId);
        Integer matchTime = matchPeriodAndTime[0];
        Integer matchPeriod = matchPeriodAndTime[1];

        AoEsBasketBallTemplateConfigEntity basketBallTemplateConfigEntity = dbEsportMatchMarketConfigConfigDaoHandler.mongoBkTempConfigFindOne(matchId, isLive);

        BasketballApplyOptParam basketballApplyOptParam = new BasketballApplyOptParam();
        basketballApplyOptParam = supportMarketHandler.templateToApplyOptParam(basketBallTemplateConfigEntity,basketballApplyOptParam);
        basketballApplyOptParam = getApplyOptParam(linkId, matchId,basketBallTemplateConfigEntity, basketballApplyOptParam,matchPeriod);

        log.info("::{}::notifyMarketMessageBk,111A0赛事:{}，matchType：：{}, asketballApplyOptParam::[{}] or 模板参数]::[{}]",linkId, matchId,isLive,basketballApplyOptParam,basketBallTemplateConfigEntity);
        if(basketballApplyOptParam == null || basketBallTemplateConfigEntity == null){
            log.info("::{}::notifyMarketMessageBk,A0赛事:{}，matchType：：{}，basketballApplyOptParam::[{}] or 模板参数不存在::[{}]", linkId, matchId,isLive,basketballApplyOptParam,basketBallTemplateConfigEntity);
            return;
        }

        BasketballApplyParam basketballApplyParam = new BasketballApplyParam();
        BeanUtils.copyProperties(basketballApplyOptParam, basketballApplyParam);

        if(null==basketBallTemplateConfigEntity.getS1Value()||basketBallTemplateConfigEntity.getS1Value()==0){
            basketballApplyParam.setAhSd(basketBallTemplateConfigEntity.getS3Value());
            basketballApplyParam.setOuSd(basketBallTemplateConfigEntity.getS4Value());
        }else {
            basketballApplyParam.setAhSd(Double.valueOf(basketBallTemplateConfigEntity.getS1Value()));
            basketballApplyParam.setOuSd(Double.valueOf(basketBallTemplateConfigEntity.getS2Value()));
        }

        basketballApplyParam.setPeriodRemainSec(matchTime);
        basketballApplyParam.setPeriodId(matchPeriod);
        supportMarketHandler.supportBkEspotMarketSocres(basketballApplyParam);
        basketballApplyParam.setUseOuSd(1);
        basketballApplyParam.setSportId(String.valueOf(SPORT_BASKETBALL));
        Properties matchTemplateConfigProperties = JSONObject.parseObject(JSONObject.toJSONString(basketballApplyParam), Properties.class);
        //滚球给了指定玩法按照全量玩法下发，只处理赛前指定玩法
        if (!CollectionUtils.isEmpty(categoryIds)&& 0 == isLive) {
            matchTemplateConfigProperties.put("markets", categoryIds);
        }
        log.info("::{}::notifyMarketMessageBk,AO赛事ID:{},获取盘口赔率入参:{}", linkId, matchId, JSONObject.toJSONString(matchTemplateConfigProperties));
        String markets = marketLoadBalanceHandler.getMarkets(matchTemplateConfigProperties, null, String.valueOf(SPORT_BASKETBALL));
        MarketDto marketDto = JSONObject.parseObject(markets).toJavaObject(MarketDto.class);
        log.info("::{}::notifyMarketMessageBk,AO赛事ID:{},获取盘口赔率入参返回", linkId, matchId);

        List<MarketsEntity> marketsEntityList = marketDto.getMarketsEntityList();
        AoMatchMarketInfo aoMatchMarketInfo = new AoMatchMarketInfo();
        aoMatchMarketInfo.setModifyTime(startTime);
        aoMatchMarketInfo.setSportId(Long.valueOf(SPORT_BASKETBALL));
        aoMatchMarketInfo.setScoreSummary(null);
        aoMatchMarketInfo.setMatchSourceId(matchId);
        aoMatchMarketInfo.setLiveFlag(isLive);
//        aoMatchMarketInfo.setAwayTeamId(aoMatchInfoEntity.getStandardAwayId() + "");
//        aoMatchMarketInfo.setHomeTeamId(aoMatchInfoEntity.getStandardHomeId() + "");
        aoMatchMarketInfo.setMarketTime(matchTime + "");
        aoMatchMarketInfo.setPeriod(matchPeriod);
        aoMatchMarketInfo.setLinkeId(linkId);
        aoMatchMarketInfo.setStartTime(System.currentTimeMillis());
        aoMatchMarketInfo.setPushTime(System.currentTimeMillis());
        aoMatchMarketInfo.setRequestType("es_bk_goal");
        if (CollectionUtils.isEmpty(marketsEntityList)) {
            log.info("::{}::notifyMarketMessageBk,AO赛事ID:{},获取盘口赔率返回为空", linkId, matchId);
            //阶段关盘
            matchMarketStatusTransit.upBkMatchMarketStatusByPeriod(linkId, aoMatchMarketInfo, basketballApplyParam, Double.valueOf(marketDto.getRemainGe()));
            esportMarketMessageService.sendMarketMessage(aoMatchMarketInfo);
            return;
        }
        marketsEntityList.stream().filter(m -> categoryNotIds.contains(m.getMarketId())).forEach(m -> {
            m.setStatus(CommonConstant.NUMBER_TWO);
        });
        //赔率计算，赔率联动
        esportCalculateOdds.calculate(linkId, matchId,  marketsEntityList,isLive, aoMatchMarketInfo.getSportId());
        aoMatchMarketInfo.setMarketList(marketsEntityList);
        if (aoMatchMarketInfo.getLiveFlag() == 1) {
            //缓存赛事盘口缓存
            marketCacheHandler.cacheBkLiveMarketOdds(linkId, matchId, aoMatchMarketInfo);
            //兜底关盘
            marketCacheHandler.cacheBkLiveMarketProcessor(linkId, matchId, aoMatchMarketInfo);
            //阶段关盘
            matchMarketStatusTransit.upBkMatchMarketStatusByPeriod(linkId, aoMatchMarketInfo, basketballApplyParam, Double.valueOf(marketDto.getRemainGe()));
        }
        esportMarketMessageService.sendMarketMessage(aoMatchMarketInfo);

    }


    /**
     * 根据模板缓存
     */
    public BasketballApplyOptParam getApplyOptParam(String linkId, String matchId, AoEsBasketBallTemplateConfigEntity basketBallTemplateConfigEntity, BasketballApplyOptParam basketballApplyOptParam,Integer matchPeriod ){
        String preDataparam = dbEsportMatchMarketConfigConfigDaoHandler.mongoBkPreDataFindOne(linkId, matchId);
        BasketballApplyParam basketballApplyParam = new BasketballApplyParam();
        basketballApplyParam.setAoMatchId(basketballApplyOptParam.getAoMatchId());
        basketballApplyParam.setQuarters(basketballApplyOptParam.getQuarters());
        basketballApplyParam.setQuarterMin(basketballApplyOptParam.getQuarterMin());
        basketballApplyParam.setFtScore("0:0");
        supportMarketHandler.supportBkEspotMarketSocres(basketballApplyParam);
        log.info("getApplyOptParam 模板参数:{}",JSON.toJSONString(basketBallTemplateConfigEntity));

        Float ftSegment = basketballApplyOptParam.getSegment0() + basketballApplyOptParam.getSegment1() +basketballApplyOptParam.getSegment2()+basketballApplyOptParam.getSegment3();
        Float xgdHt = (basketballApplyOptParam.getSegment0() + basketballApplyOptParam.getSegment1())/ftSegment;
        Float xgdQ1 =  basketballApplyOptParam.getSegment0()/(basketballApplyOptParam.getSegment0() + basketballApplyOptParam.getSegment1());
        Float xgdQ3 =  basketballApplyOptParam.getSegment2()/(basketballApplyOptParam.getSegment2() + basketballApplyOptParam.getSegment3());
        basketballApplyOptParam.setXgdHT(xgdHt.doubleValue());
        basketballApplyOptParam.setXgdQ1(xgdQ1.doubleValue());
        basketballApplyOptParam.setXgdQ3(xgdQ3.doubleValue());
        log.info("getApplyOptParam 模板参数 计算后:{}",JSON.toJSONString(basketballApplyOptParam));
        putGeAndSup(basketballApplyOptParam,preDataparam);
        log.info("linkID::{},getApplyOptParam::ge和sup参数处理:{}, ",linkId, JSON.toJSONString(basketballApplyOptParam) );

        return basketballApplyOptParam;

    }

    /**
     *
     * @param basketballApplyOptParam
     * @param preMatchTeamData  FTGoalExpT1=56.05|FTGoalExpT2=56.725
     */
    private void putGeAndSup(BasketballApplyOptParam basketballApplyOptParam, String preMatchTeamData) {
        if (Strings.isBlank(preMatchTeamData)) {
            log.warn("【applyMarketsParam】preMatchTeamData 为空");
            return;
        }
        try {
            Map<String, Double> geAndSupMap = Arrays.stream(preMatchTeamData.split("\\|"))
                    .map(entry -> entry.split("="))
                    .filter(kv -> kv.length == 2 && Strings.isNotBlank(kv[0]))
                    .collect(HashMap::new,
                            (map, kv) -> map.put(kv[0], parseDoubleSafely(kv[1])),
                            HashMap::putAll);

            double ftGoalExpT1 = geAndSupMap.getOrDefault("FTGoalExpT1", 0D);
            double ftGoalExpT2 = geAndSupMap.getOrDefault("FTGoalExpT2", 0D);

            float total = 1f;
            if (basketballApplyOptParam.getSegment0() == null || basketballApplyOptParam.getSegment0() == 0.0f) {
                log.info("::putGeAndSup::小节参数异常， 不参与计算::{}",basketballApplyOptParam);
            }else {
                total = basketballApplyOptParam.getSegment0() + basketballApplyOptParam.getSegment1() +basketballApplyOptParam.getSegment2() + basketballApplyOptParam.getSegment3();
            }
            double geT1 = ftGoalExpT1 * total;
            double geT2 = ftGoalExpT2 * total;

            BigDecimal ge = BigDecimal.valueOf((geT1 + geT2)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal sup = BigDecimal.valueOf(geT1 - geT2).setScale(2, RoundingMode.HALF_UP);

            basketballApplyOptParam.setFtGe(ge.doubleValue());
            basketballApplyOptParam.setFtSup(sup.doubleValue());

            log.info("【applyMarketsParam】封装完成: {}", JSON.toJSONString(basketballApplyOptParam));
        } catch (Exception ex) {
            log.error("【applyMarketsParam】解析 preMatchTeamData 异常，原始数据：{}", preMatchTeamData, ex);
        }
    }


    private double parseDoubleSafely(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            log.warn("【applyMarketsParam】无法解析数值: {}", str);
            return 0D;
        }
    }
}
