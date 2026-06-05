package com.panda.aoodds.esport.marketstate.processor;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.constant.FootballCategorySet;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.utils.TraceIdGenerator.createTraceId;


/**
 * 足球 事件或比分触发玩法集种类关盘
 */
@Slf4j
@Component
public class FootballMarketStatusProcessor {

    @Autowired
    private RedisService redisService;

    /**
     * 足球种类关盘
     *
     * @param type
     * @param aoMatchId
     */
    public AoMatchMarketInfo marketStatusProcessor(String linkId, Long aoMatchId, String type) {
        AoMatchMarketInfo matchMarketInfo = getCacheMarketInfo(linkId, aoMatchId);
        if (null == matchMarketInfo) {
            return null;
        }
        List<MarketsEntity> marketsEntityList = marketStatusClose(aoMatchId, matchMarketInfo.getMarketList());
        if (CollectionUtils.isEmpty(marketsEntityList)) {
            log.info("::{}::AO赛事ID:{},比分关盘,足球玩法集种类关盘:{},关盘盘口不存在", linkId, type, aoMatchId);
            return null;
        }
        matchMarketInfo.setMarketList(marketsEntityList);
        String newLinkId = createTraceId();
        log.info("::{}::转换linkId:{},AO赛事ID:{},比分关盘,足球玩法集种类关盘:{}", linkId, newLinkId, aoMatchId, type);
        matchMarketInfo.setLinkeId(newLinkId);
        return matchMarketInfo;
    }

    /**
     * 玩法集对应的玩法盘口进行下发关盘处理
     *
     * @param type
     * @param aoMatchId
     * @param marketsEntities
     * @return
     */
    private List<MarketsEntity> marketStatusClose(Long aoMatchId, List<MarketsEntity> marketsEntities) {
        return marketsEntities.stream().map(m -> {
            m.setStatus(CommonConstant.NUMBER_TWO);
            return m;
        }).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        System.out.println(FootballCategorySet.TYPE.get("ht_goal"));

    }

    /**
     * 上半场 全场 阶段状态关盘
     *
     * @param linkId
     * @param type
     * @param aoMatchId
     * @return
     */
    public List<MarketsEntity> marketPeriodStatusClose(String linkId, String type, Long aoMatchId, List<MarketsEntity> cacheMarketList) {
        return cacheMarketList.stream().filter(m -> FootballCategorySet.PERIOD_CATEGORY.get(type).contains(m.getMarketId())).map(m -> {
            //下半场波胆，下半场反波胆 不处理
            if (m.getMarketId() == 60006 || m.getMarketId() == 60017) {
                return m;
            }
            m.setStatus(CommonConstant.NUMBER_TWO);
            return m;
        }).collect(Collectors.toList());
    }

    /**
     * 15分钟 阶段状态关盘
     *
     * @param linkId
     * @param type
     * @param aoMatchId
     * @param minutesMap
     * @return
     */
    public List<MarketsEntity> marketPeriodMinutesStatusClose(String linkId, String type, Long aoMatchId, Map<String, Boolean> minutesMap, List<MarketsEntity> cacheMarketList) {
        List<MarketsEntity> marketsEntityList = cacheMarketList.stream().filter(m -> MarketCategory.FIFTEEN_ATOB_MARKET_LIST.contains(m.getMarketId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(marketsEntityList)) {
            log.info("::{}::,AO赛事ID:{},阶段关盘,未找到15分钟类玩法,minutesMap:{}", linkId, aoMatchId, JSONObject.toJSONString(minutesMap));
            return null;
        }
        List<MarketsEntity> markets = new ArrayList<>();
        for (MarketsEntity minMarket : marketsEntityList) {
            String time = minMarket.getMarketName().substring(minMarket.getMarketName().indexOf("(") + 1, minMarket.getMarketName().indexOf(")")).split("-")[1];
            Boolean isTrue = minutesMap.get(time);
            if (null != isTrue && isTrue) {
                minMarket.setStatus(CommonConstant.NUMBER_TWO);
            }
            markets.add(minMarket);
        }
        if (CollectionUtils.isEmpty(markets)) {
            log.info("::{}::,AO赛事ID:{},阶段关盘,没有15分钟类关盘玩法,minutesMap:{}", linkId, aoMatchId, JSONObject.toJSONString(minutesMap));
            return null;
        }
        return markets;
    }

    /**
     * 最新赛事盘口数据
     *
     * @param linkId
     * @param aoMatchId
     * @return
     */
    public AoMatchMarketInfo getCacheMarketInfo(String linkId, Long aoMatchId) {
        Long nowTime = System.currentTimeMillis();
        log.info("::{}::AO赛事ID:{},获取缓存赛事盘口，时间：{}", linkId, aoMatchId, nowTime);
        String key = RedisKeyConstant.AO_ESPORT_MATCH_INFO + aoMatchId;
        Object cacheMarketObj = redisService.get(key);
        if (ObjectUtil.isNull(cacheMarketObj)) {
            log.info("::{}::AO赛事ID:{},获取缓存赛事盘口,缓存KEY不存在:{}", linkId, aoMatchId, key);
            return null;
        }
        AoMatchMarketInfo matchMarketInfo = (AoMatchMarketInfo) cacheMarketObj;
        String marketKey = RedisKeyConstant.AO_ESPORT_MATCH_MARKET_ODDS + aoMatchId;
        Map<String, MarketsEntity> cacheMarketOdds = redisService.hGetAll(marketKey);
        if (MapUtils.isEmpty(cacheMarketOdds) || cacheMarketOdds.size() == 0) {
            return null;
        }
        log.info("::{}::AO赛事ID:{},获取缓存赛事盘口，cacheMarketOdds：{}", linkId, aoMatchId, cacheMarketOdds.size());
        List<MarketsEntity> marketsEntities = new ArrayList<>();
        for (Map.Entry<String, MarketsEntity> entry : cacheMarketOdds.entrySet()) {
            marketsEntities.add(entry.getValue());
        }
        matchMarketInfo.setMarketList(marketsEntities);
        matchMarketInfo.setModifyTime(nowTime);
        matchMarketInfo.setStartTime(nowTime);
        matchMarketInfo.setPushTime(nowTime);
        return matchMarketInfo;
    }
}
