package com.panda.aoodds.esport.marketstate;

import com.panda.aoodds.esport.api.entity.BasketballApplyParam;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.marketstate.processor.EsportBasketballPeriodTimeProcessor;
import com.panda.aoodds.esport.marketstate.processor.FootBallPeriodTimeProcessor;
import com.panda.aoodds.esport.marketstate.processor.FootballMarketStatusProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.utils.TraceIdGenerator.createTraceId;

@Slf4j
@Component
public class MatchMarketStatusTransit {

    @Autowired
    private FootballMarketStatusProcessor footballMarketStatusProcessor;
    @Autowired
    private FootBallPeriodTimeProcessor footBallPeriodTimeProcessor;
    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;
    @Autowired
    private RedisService redisService;
    @Autowired
    private EsportBasketballPeriodTimeProcessor esportBasketballPeriodTimeProcessor;


    /**
     * 赛事比分更改盘口状态处理
     *
     * @param linkId
     * @param sportId 运动ID
     * @param matchId AO赛事ID
     * @param type    玩法集种类
     */
    public AoMatchMarketInfo upMatchMarketStatus(String linkId, Long matchId, String type) {
        return footballMarketStatusProcessor.marketStatusProcessor(linkId, matchId, type);
    }


    /**
     * 休息阶段31 ，关闭上半场玩法
     *
     * @param linkId
     * @param aoMatchId
     * @param matchPeriodId
     * @return
     */
    public AoMatchMarketInfo standardEventPeriodRestMarketClose(String linkId, Long aoMatchId, Long matchPeriodId) {
        return footBallPeriodTimeProcessor.standardEventPeriodRestMarketProcessor(linkId, aoMatchId, matchPeriodId);
    }



    public AoMatchMarketInfo upBkMatchMarketStatusByPeriod(String linkId, AoMatchMarketInfo aoMatchMarketInfo, BasketballApplyParam basketballApplyParam, Double remainGe) {
        return esportBasketballPeriodTimeProcessor.marketPeriodTimeProcessor(linkId, aoMatchMarketInfo, basketballApplyParam, remainGe);
    }

    /**
     * 休息阶段31 ，关闭上半场玩法
     *
     * @param linkId
     * @param aoMatchId
     * @param sportId
     * @param matchPeriodId
     * @return
     */
    public AoMatchMarketInfo standardEventPeriodRestMarketClose(String linkId, Long aoMatchId, Long sportId, Long matchPeriodId) {
        switch (sportId.intValue()) {
            case 1:
                return footBallPeriodTimeProcessor.standardEventPeriodRestMarketProcessor(linkId, aoMatchId, matchPeriodId);
            default:
                break;
        }
        return null;
    }

    /**
     * 全部关盘
     *
     * @param aoMatchId
     */
    public AoMatchMarketInfo marketStatusProcessor(String linkId, Long aoMatchId) {
        AoMatchMarketInfo matchMarketInfo = footballMarketStatusProcessor.getCacheMarketInfo(linkId, aoMatchId);
        if (null == matchMarketInfo) {
            return null;
        }
        List<MarketsEntity> marketsEntityList = marketStatusClose( matchMarketInfo.getMarketList());
        if (CollectionUtils.isEmpty(marketsEntityList)) {
            log.info("::{}::AO赛事ID:{},篮球全部关盘,关盘盘口不存在", linkId, aoMatchId);
            return null;
        }
        matchMarketInfo.setMarketList(marketsEntityList);
        String newLinkId = createTraceId();
        log.info("::{}::转换linkId:{},AO赛事ID:{},篮球全部关盘", linkId, newLinkId, aoMatchId);
        matchMarketInfo.setLinkeId(newLinkId);
        return matchMarketInfo;
    }

    private List<MarketsEntity> marketStatusClose( List<MarketsEntity> marketsEntities) {
        return marketsEntities.stream().map(m -> {
            m.setStatus(CommonConstant.NUMBER_ONE);
            return m;
        }).collect(Collectors.toList());
    }
}
