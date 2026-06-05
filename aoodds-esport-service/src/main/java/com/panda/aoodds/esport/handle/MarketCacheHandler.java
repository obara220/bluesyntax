package com.panda.aoodds.esport.handle;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.entity.BasketballApplyParam;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.config.RedisConfig;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.entity.HomeAwayScore;
import com.panda.aoodds.esport.common.enums.ScoreType;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.service.EsportMarketMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Component
public class MarketCacheHandler {
    @Autowired
    private RedisService redisService;

    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;

    @Autowired
    @Qualifier("esportMarketPushService")
    EsportMarketMessageService esportMarketMessageService;

    /**
     * 1.缓存赛事滚球盘口
     *
     * @param matchId
     * @param matchInfo
     */
    public void cacheLiveMarketOdds(String linkId, String matchId, AoMatchMarketInfo<MarketsEntity> matchInfo) {
        log.info("::{}::AO赛事ID:{},开始缓存赛事滚球盘口", linkId, matchId);
        String key = RedisKeyConstant.AO_ESPORT_MATCH_INFO + matchId;
        //刷新赛事缓存
        redisService.set(key, matchInfo);
        //缓存盘口
        List<MarketsEntity> marketList = matchInfo.getMarketList();
        Map<String, MarketsEntity> listMap = marketList.stream().collect(Collectors.toMap(t -> t.getMarketId() + "-" + t.getHandicap(), Function.identity(), (v1, v2) -> v2));
        String marketKey = RedisKeyConstant.AO_ESPORT_MATCH_MARKET_ODDS + matchId;
        //刷新赛事赔率缓存
        redisService.hSetAll(marketKey, listMap, RedisConfig.REDIS_DEFAULT_TIME);
        log.info("::{}::AO赛事ID:{},结束缓存赛事滚球盘口:{}", linkId, matchId, listMap.size());
    }


    /**
     * 1.缓存赛事滚球盘口 - 篮球
     *
     * @param matchId
     * @param matchInfo
     */
    public void cacheBkLiveMarketOdds(String linkId, String matchId, AoMatchMarketInfo<MarketsEntity> matchInfo) {
        log.info("::{}::AO赛事ID:{},开始缓存赛事滚球盘口", linkId, matchId);
        String key = RedisKeyConstant.AO_ESPORT_BK_MATCH_INFO + matchId;
        //刷新赛事缓存
        redisService.set(key, matchInfo);
        //缓存盘口
        List<MarketsEntity> marketList = matchInfo.getMarketList();
        Map<String, MarketsEntity> listMap = marketList.stream().collect(Collectors.toMap(t -> t.getMarketId() + "-" + t.getHandicap(), Function.identity(), (v1, v2) -> v2));
        String marketKey = RedisKeyConstant.AO_ESPORT_BK_MATCH_MARKET_ODDS + matchId;
        //刷新赛事赔率缓存
        redisService.hSetAll(marketKey, listMap, RedisConfig.REDIS_DEFAULT_TIME);
        log.info("::{}::AO赛事ID:{},结束缓存赛事滚球盘口:{}", linkId, matchId, listMap.size());
    }
    /**
     * 盘口缓存与上一批不同的盘口兜底
     *
     * @param linkId
     * @param matchId
     * @param matchInfo
     */
    public void marketDifferentClose(String linkId, String matchId, AoMatchMarketInfo<MarketsEntity> matchInfo) {
        String marketKey = RedisKeyConstant.AO_ESPORT_MATCH_MARKET_ODDS + matchId;
        //刷新赛事赔率缓存
        Map<String, MarketsEntity> cacheMarketOdds = redisService.hGetAll(marketKey);
        if (MapUtils.isEmpty(cacheMarketOdds) || cacheMarketOdds.size() == 0) {
            return;
        }
        List<String> delKeys = new ArrayList<>();
        List<MarketsEntity> marketList = matchInfo.getMarketList();
        Map<String, MarketsEntity> newMarketOdds = marketList.stream().collect(Collectors.toMap(t -> t.getMarketId() + "-" + t.getHandicap(), Function.identity(), (v1, v2) -> v2));
        for (Map.Entry<String, MarketsEntity> entry : cacheMarketOdds.entrySet()) {
            String key = entry.getKey();
            if (null == newMarketOdds.get(key)) {
                MarketsEntity cacheMarketsEntity = entry.getValue();
                cacheMarketsEntity.setStatus(CommonConstant.NUMBER_TWO);
                cacheMarketsEntity.setOrder(999);
                cacheMarketsEntity.setModifyTime(matchInfo.getModifyTime());
                marketList.add(cacheMarketsEntity);
                delKeys.add(key);
            }
        }
        if (CollectionUtils.isNotEmpty(delKeys)) {
            redisService.hDel(marketKey, delKeys.toArray());
            log.info("::{}::marketDifferentClose,ao赛事ID：{},delKeys：{} ", linkId, matchId, JSONObject.toJSONString(delKeys));
        }
    }

    /**
     * 78892 大小盘口规律验证 : 当前大小盘玩法与缓存对比，主盘口盘口差值大于0.25 则全部玩法关盘
     *
     * @param linkId
     * @param matchId
     * @param newMatchInfo
     */
    public void marketHandicapDiffVerify(String linkId, String matchId, AoMatchMarketInfo<MarketsEntity> newMatchInfo, Map<ScoreType, HomeAwayScore<Integer>> scoreSummary) {
       try {
           log.info("::{}::AO赛事ID::{}, 大小盘口验证开始！", linkId, matchId);
           long currentTime = System.currentTimeMillis();
           boolean isFreashCache = true;
           String key = RedisKeyConstant.AO_ESPORT_MATCH_HAND_ICAP + matchId;
           if (linkId.contains(CommonConstant.EVENT_STATUS) || newMatchInfo.getPeriod() == CommonConstant.PERIOD_31) {
               redisService.del(key);
               log.info("::{}::AO赛事ID::{},所属阶段::{} 大小盘验证--当前为阶段事件，清空缓存球头！", linkId, matchId, newMatchInfo.getPeriod());
               return;
           }
           if (scoreSummary == null) {
               log.warn("::{}::AO赛事ID::{}, 大小盘口验证,scoreSummary为空，跳过验证", linkId, matchId);
               return;
           }
           Object cacheHandIcap = redisService.get(key);
           HomeAwayScore<Integer> integerHomeAwayScore = scoreSummary.get(ScoreType.FULL_TIME_SCORE);
           if (integerHomeAwayScore == null) {
               log.warn("::{}::AO赛事ID::{}, 大小盘口验证,FULL_TIME_SCORE为空，跳过验证", linkId, matchId);
               return;
           }
           if (integerHomeAwayScore.getHomeScore() == null || integerHomeAwayScore.getAwayScore() == null) {
               log.warn("::{}::AO赛事ID::{}, 大小盘口验证,比分数据不完整(homeScore或awayScore为空)，跳过验证", linkId, matchId);
               return;
           }
           Integer homeAwayScore = integerHomeAwayScore.getHomeScore() + integerHomeAwayScore.getAwayScore();

           MarketsEntity newMarketsEntity = findMarketEntity(newMatchInfo.getMarketList());
           if (newMarketsEntity != null) {
               double newNum = 0;
               double cacheNum = 0;
               try {
                   newNum = parseHandicap(newMarketsEntity.getHandicap()) - homeAwayScore;
                   if (ObjectUtil.isNull(cacheHandIcap)) {
                       log.info("::{}::AO赛事ID:{},大小盘口验证,获取缓存赛事盘口,缓存KEY不存在:{}， 新盘口值::{}", linkId, matchId, key, newNum);
                   } else {
                       String[] split = cacheHandIcap.toString().split(CommonConstant.PUNCTUATION_COLON);
                       if (split.length != 2) {
                           log.info("::{}::AO赛事ID:{},缓存数据不对，不予处理，cacheObj::{}", linkId, matchId, cacheHandIcap);
                       } else {
                           cacheNum = parseHandicap(split[0]);
                           if (Math.abs(cacheNum - newNum) > 0.25 || Double.compare(cacheNum, newNum) == -1) {
                               log.info("::{}::AO赛事ID:{},缓存比分和::{},大小盘变化大于0.25,  缓存球头值::{} ，当前球头值::{} 赛事所有盘口关闭处理。", linkId, matchId, homeAwayScore, cacheNum, newNum);
                               newMatchInfo.getMarketList().forEach(m -> m.setStatus(CommonConstant.NUMBER_TWO));
                               isFreashCache = false;
                               //判断盘口错误时间是否小于60秒
                               if (currentTime - Long.parseLong(split[1]) > 60 * 1000) {
                                   log.info("::{}::AO赛事ID:{},盘口错误时间过长，赛事级关盘，超时时间::{}", linkId, matchId, cacheHandIcap);
                                   redisService.hSetField(CommonConstant.AO_ESPORT_SCORE_ERROR_KEY, matchId, 0, RedisKeyConstant.AO_BGTIME_KEY_TIME);
                                   esportMarketMessageService.sendMatchStatusMessage(newMatchInfo);
                               }
                           }
                       }
                   }
                   if (isFreashCache) {
                       redisService.set(key, newNum + CommonConstant.PUNCTUATION_COLON + currentTime, RedisKeyConstant.AO_BGTIME_KEY_TIME);
                   }
                   log.info("::{}::AO赛事ID::{}, 大小盘口验证结束！", linkId, matchId);
               } catch (NumberFormatException e) {
                   log.error("::{}::AO赛事ID:{},大小盘口验证,无法解析盘口，缓存球头值::{} ，当前球头值::{} ,  msg:: {}",
                           linkId, matchId, cacheNum, newNum, e.getMessage());

               }
           }
       }catch (Exception ex){
           log.error("::{}::AO赛事ID:{},大小盘口验证异常,  msg::",linkId, matchId, ex);
       }
    }

    /**
     * 过滤大小盘的住盘口
     *
     * @param marketList
     * @return
     */
    private MarketsEntity findMarketEntity(List<MarketsEntity> marketList) {
        return marketList.stream()
                .filter(obj -> obj.getMarketId() == 10005 && obj.getOrder() == 1)
                .findFirst()
                .orElse(null);
    }

    /**
     * 盘口值转换
     *
     * @param handicap
     * @return
     */
    private double parseHandicap(String handicap) {
        if (handicap == null || handicap.isEmpty()) {
            throw new NumberFormatException();
        }
        return Double.parseDouble(handicap);
    }

    /**
     * 篮球 跟随rev数据状态 兜底关盘
     *
     * @param linkId
     * @param matchId
     * @param marketsEntityList
     */
    public void cacheSpecialPlayBk(String linkId, String matchId, List<MarketsEntity> marketsEntityList, BasketballApplyParam basketballApplyParam) {
        if (null == basketballApplyParam.getFollowedStatus() || basketballApplyParam.getFollowedStatus() == 0) {
            log.info("::{}::AO赛事ID:{},cacheSpecialPlayBk，followedStatus不存在", linkId, matchId);
            return;
        }
        //记录三方状态
        String statusKey = CommonConstant.THIRD_BASKETBALL_STATUS_KEY + matchId + "_" + basketballApplyParam.getDataSourceCode();
        Map<String, Integer> categoryIdStatusMap = redisService.hGetAll(statusKey);
        if (MapUtils.isEmpty(categoryIdStatusMap)) {
            return;
        }
        Boolean autoRev = null == basketballApplyParam.getAutoRev() ? Boolean.FALSE : basketballApplyParam.getAutoRev();
        Boolean autoApply = null == basketballApplyParam.getAutoApply() ? Boolean.FALSE : basketballApplyParam.getAutoApply();
        if (!autoRev && !autoApply) {
            log.info("::{}::AO赛事ID:{},cacheSpecialPlayBk没有勾选", linkId, matchId);
            return;
        }
        for (Map.Entry<String, Integer> entry : categoryIdStatusMap.entrySet()) {
            String categoryId = entry.getKey();
            List<Integer> aoCategoryIds = MarketCategory.STANDARD_CATEGORY_ID_AND_AO_CATEGORY.get(categoryId);
            Integer status = entry.getValue();
            if (status == CommonConstant.NUMBER_ZERO) {
                continue;
            }
            log.info("::{}::AO赛ID:{},篮球兜底关盘,标准玩法:{},AO玩法:{},状态:{}", linkId, matchId, categoryId, aoCategoryIds, status);
            List<MarketsEntity> marketsEntitys = marketsEntityList.stream().filter(m -> aoCategoryIds.contains(m.getMarketId())).collect(Collectors.toList());
            //关盘
            if (org.springframework.util.CollectionUtils.isEmpty(marketsEntitys)) {
                continue;
            }
            marketsEntitys.forEach(m -> {
                m.setStatus(status);
            });
        }
    }

    /**
     * 滚球盘口兜底关
     *
     * @param linkId
     * @param aoMatchId
     * @param
     */
    public void cacheBkLiveMarketProcessor(String linkId, String aoMatchId, AoMatchMarketInfo<MarketsEntity> matchInfo) {
        String marketKey = RedisKeyConstant.AO_ESPORT_BK_MATCH_MARKET_ODDS + aoMatchId;
        //刷新赛事赔率缓存
        Map<String, MarketsEntity> cacheMarketOdds = redisService.hGetAll(marketKey);
        if (MapUtils.isEmpty(cacheMarketOdds) || cacheMarketOdds.size() == 0) {
            return;
        }
        List<MarketsEntity> marketList = matchInfo.getMarketList();
        List<String> delKeys = new ArrayList<>();
        Map<String, MarketsEntity> newMarketOdds = marketList.stream().collect(Collectors.toMap(t -> t.getMarketId() + "-" + t.getHandicap(), Function.identity(), (v1, v2) -> v2));
        for (Map.Entry<String, MarketsEntity> entry : cacheMarketOdds.entrySet()) {
            String key = entry.getKey();
            if (null == newMarketOdds.get(key)) {
                MarketsEntity cacheMarketsEntity = entry.getValue();
                cacheMarketsEntity.setStatus(CommonConstant.NUMBER_TWO);
                cacheMarketsEntity.setOrder(999);
                cacheMarketsEntity.setModifyTime(matchInfo.getModifyTime());
                marketList.add(cacheMarketsEntity);
                delKeys.add(key);
            }
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(delKeys)) {
            redisService.hDel(marketKey, delKeys.toArray());
            log.info("::{}::marketDifferentClose,ao赛事ID：{},delKeys：{} ", linkId, aoMatchId, JSONObject.toJSONString(delKeys));
        }
        //6分钟滚球标识，事件还是赛前兜底关
//        cacheBkLiveMarketSixTotalClose(linkId, aoMatchId, marketList);
        //有效盘口兜底
        Map<Integer, List<MarketsEntity>> listMap = marketList.stream().collect((Collectors.groupingBy(MarketsEntity::getMarketId)));
        //排序
        marketOrderSort(listMap);
    }

    /**
     * 排序
     * 开盘为有效排序，关盘排序位置需要在开盘有效盘口最大位置叠加
     *
     * @param listMap
     */
    public void marketOrderSort(Map<Integer, List<MarketsEntity>> listMap) {
        for (Map.Entry<Integer, List<MarketsEntity>> marketsEntityMap : listMap.entrySet()) {
            //有效盘口
            List<MarketsEntity> marketsEntity = marketsEntityMap.getValue();
            List<MarketsEntity> open = marketsEntity.stream().filter(m -> m.getStatus() == 0).collect(Collectors.toList());
            //关盘盘口
            List<MarketsEntity> close = marketsEntity.stream().filter(m -> m.getStatus() != 0).collect(Collectors.toList());
            int order = 1;
            if (!org.springframework.util.CollectionUtils.isEmpty(open)) {
                order = open.stream().max(Comparator.comparing(MarketsEntity::getOrder)).get().getOrder() + 1;
            }
            if (!org.springframework.util.CollectionUtils.isEmpty(close)) {
                for (MarketsEntity entity : close) {
                    entity.setOrder(order);
                    order++;
                }
            }
        }
    }
}
