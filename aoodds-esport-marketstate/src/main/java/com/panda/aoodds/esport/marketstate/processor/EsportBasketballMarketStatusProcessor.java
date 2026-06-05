package com.panda.aoodds.esport.marketstate.processor;


import cn.hutool.core.util.ObjectUtil;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.utils.TraceIdGenerator.createTraceId;


/**
 * 篮球 事件或比分触发玩法集种类关盘
 */
@Slf4j
@Component
public class EsportBasketballMarketStatusProcessor {

    @Autowired
    private RedisService redisService;

    /**
     * 上半场 全场 阶段状态关盘
     *
     * @return
     */
    public List<MarketsEntity> marketPeriodStatusClose(List<MarketsEntity> cacheMarketList, List<Integer> categoryIds) {
        return cacheMarketList.stream().filter(m -> categoryIds.contains(m.getMarketId())).map(m -> {
            m.setStatus(CommonConstant.NUMBER_TWO);
            return m;
        }).collect(Collectors.toList());
    }

    /**
     * 篮球全部玩法关盘
     *
     * @param aoMatchId
     */
    public AoMatchMarketInfo bkMarketStatusProcessor(String aoMatchId) {
        String linkId = createTraceId();
        AoMatchMarketInfo matchMarketInfo = getCacheMarketInfo(linkId, Long.valueOf(aoMatchId));
        if (null == matchMarketInfo) {
            return null;
        }
        List<MarketsEntity> marketList = matchMarketInfo.getMarketList();
        for (MarketsEntity market : marketList) {
            market.setModifyTime(System.currentTimeMillis());
            market.setStatus(CommonConstant.NUMBER_TWO);
        }
        matchMarketInfo.setMarketList(marketList);
        log.info("::{}::篮球全部玩法关盘,AO赛事ID:{}", linkId, aoMatchId);
        matchMarketInfo.setLinkeId(linkId);
        return matchMarketInfo;
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
        String key = RedisKeyConstant.AO_ESPORT_BK_MATCH_INFO + aoMatchId;
        Object cacheMarketObj = redisService.get(key);
        if (ObjectUtil.isNull(cacheMarketObj)) {
            log.info("::{}::AO赛事ID:{},获取缓存赛事盘口,缓存KEY不存在:{}", linkId, aoMatchId, key);
            return null;
        }
        AoMatchMarketInfo matchMarketInfo = (AoMatchMarketInfo) cacheMarketObj;
        String marketKey = RedisKeyConstant.AO_ESPORT_BK_MATCH_MARKET_ODDS + aoMatchId;
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
