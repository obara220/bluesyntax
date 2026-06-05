package com.panda.aoodds.esport.marketstate.processor;

import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.entity.BasketballApplyParam;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.market.MarketCategory.*;


/**
 * 篮球阶段时间关盘
 */
@Slf4j
@Component
public class EsportBasketballPeriodTimeProcessor {
    @Autowired
    private EsportBasketballMarketStatusProcessor basketballMarketStatusProcessor;
    @Autowired
    private RedisService redisService;

    public AoMatchMarketInfo marketPeriodTimeProcessor(String linkId, AoMatchMarketInfo<MarketsEntity> aoMatchMarketInfo, BasketballApplyParam basketballApplyParam, Double remainGe) {
        Long aoMatchId = Long.valueOf(aoMatchMarketInfo.getMatchSourceId());
        if (CollectionUtils.isEmpty(aoMatchMarketInfo.getMarketList())) {
            AoMatchMarketInfo<MarketsEntity> cacheMarketInfo = basketballMarketStatusProcessor.getCacheMarketInfo(linkId, aoMatchId);
            if (null != cacheMarketInfo) {
                aoMatchMarketInfo.setMarketList(cacheMarketInfo.getMarketList());
                log.info("::{}AO赛事ID:{},阶段关盘盘口不存在获取缓存最新", linkId, aoMatchId);
            }
        }
        List<MarketsEntity> marketList = aoMatchMarketInfo.getMarketList();
        if (CollectionUtils.isEmpty(marketList)) {
            return null;
        }
        Map<Integer, List<MarketsEntity>> marketListMap = marketList.stream().collect(Collectors.groupingBy(MarketsEntity::getMarketId));
        int nowPeriod = basketballApplyParam.getPeriodId();
        Integer periodRemainSec = basketballApplyParam.getPeriodRemainSec();
        Integer closingTime = basketballApplyParam.getClosingTime() * 60;
        Double closingGe = Double.valueOf(basketballApplyParam.getClosingGe());
        log.info("::{}::,aoMatchId:{} ,marketPeriodTimeProcessor阶段：{}，closingGe：{} ，remainGe：{},periodRemainSec:{},closingTime:{} ",
                linkId, aoMatchId, nowPeriod, closingGe, remainGe, periodRemainSec, closingTime);

        if (remainGe <= closingGe) {
            List<MarketsEntity> allMarketClose2 = allMarketClose(linkId, aoMatchId, marketList, remainGe, closingGe, nowPeriod);
            if (!CollectionUtils.isEmpty(allMarketClose2)) {
                log.info("::{}::,AO赛事ID:{},allMarketClose,条数:{}", linkId, aoMatchId, allMarketClose2.size());
            }
            aoMatchMarketInfo.setMarketList(allMarketClose2);
            return aoMatchMarketInfo;
        }

        switch (nowPeriod) {
            case 13://第一节
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsQ1Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_Q1_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsQ1Entities)) {
                        log.info("::{}::,AO赛事ID:{},阶段关盘第一节盘口,条数:{}", linkId, aoMatchId, marketsQ1Entities.size());
                    }
                }
                //第一节关闭 小于等于4分钟关盘
                if (periodRemainSec <= 4 * 60) {
                    List<MarketsEntity> marketsStableQ1Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_STABLE_Q1_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsStableQ1Entities)) {
                        log.info("::{}::,AO赛事ID:{},固定阶段关盘第一节盘口,条数:{}", linkId, aoMatchId, marketsStableQ1Entities.size());
                    }
                }
                String[] q1ScoreStr = basketballApplyParam.getQ1Score().split(":");
                Integer homeQ1Score = Integer.parseInt(q1ScoreStr[0]) + 6;
                Integer awayQ1Score = Integer.parseInt(q1ScoreStr[1]) + 6;
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, homeQ1Score, marketListMap.get(BASKETBALL_Q1_HOME_X_PTS.getId()));
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, awayQ1Score, marketListMap.get(BASKETBALL_Q1_AWAY_X_PTS.getId()));
                break;
            case 14://第二节
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsQ2Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_Q2_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsQ2Entities)) {
                        log.info("::{}::,AO赛事ID:{},阶段关盘第二节盘口,条数:{}", linkId, aoMatchId, marketsQ2Entities.size());
                    }
                }
                //第二节关闭 小于等于4分钟关盘
                if (periodRemainSec <= 4 * 60) {
                    List<MarketsEntity> marketsStableQ2Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_STABLE_Q2_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsStableQ2Entities)) {
                        log.info("::{}::,AO赛事ID:{},固定阶段关盘第二节盘口,条数:{}", linkId, aoMatchId, marketsStableQ2Entities.size());
                    }
                }
                String[] q2ScoreStr = basketballApplyParam.getQ2Score().split(":");
                Integer homeQ2Score = Integer.parseInt(q2ScoreStr[0]) + 6;
                Integer awayQ2Score = Integer.parseInt(q2ScoreStr[1]) + 6;
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, homeQ2Score, marketListMap.get(BASKETBALL_Q2_HOME_X_PTS.getId()));
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, awayQ2Score, marketListMap.get(BASKETBALL_Q2_HOME_X_PTS.getId()));
                break;
            case 15://第三节
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsQ3Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_Q3_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsQ3Entities)) {
                        log.info("::{}::,AO赛事ID:{},阶段关盘第三节盘口,条数:{}", linkId, aoMatchId, marketsQ3Entities.size());
                    }
                }
                //第三节关闭 小于等于4分钟关盘
                if (periodRemainSec <= 4 * 60) {
                    List<MarketsEntity> marketsStableQ3Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_STABLE_Q3_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsStableQ3Entities)) {
                        log.info("::{}::,AO赛事ID:{},固定阶段关盘第三节盘口,条数:{}", linkId, aoMatchId, marketsStableQ3Entities.size());
                    }
                }
                String[] q3ScoreStr = basketballApplyParam.getQ3Score().split(":");
                Integer homeQ3Score = Integer.parseInt(q3ScoreStr[0]) + 6;
                Integer awayQ3Score = Integer.parseInt(q3ScoreStr[1]) + 6;
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, homeQ3Score, marketListMap.get(BASKETBALL_Q3_HOME_X_PTS.getId()));
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, awayQ3Score, marketListMap.get(BASKETBALL_Q3_HOME_X_PTS.getId()));
                break;
            case 16://第四节
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsQ4Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_Q4_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsQ4Entities)) {
                        log.info("::{}::,AO赛事ID:{},阶段关盘第四节盘口,条数:{}", linkId, aoMatchId, marketsQ4Entities.size());
                    }
                }
                //第4节关闭 小于等于4分钟关盘
                if (periodRemainSec <= 4 * 60) {
                    List<MarketsEntity> marketsStableQ4Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_STABLE_Q4_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsStableQ4Entities)) {
                        log.info("::{}::,AO赛事ID:{},固定阶段关盘第四节盘口,条数:{}", linkId, aoMatchId, marketsStableQ4Entities.size());
                    }
                }
                String[] q4ScoreStr = basketballApplyParam.getQ4Score().split(":");
                Integer homeQ4Score = Integer.parseInt(q4ScoreStr[0]) + 6;
                Integer awayQ4Score = Integer.parseInt(q4ScoreStr[1]) + 6;
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, homeQ4Score, marketListMap.get(BASKETBALL_Q4_HOME_X_PTS.getId()));
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, awayQ4Score, marketListMap.get(BASKETBALL_Q4_HOME_X_PTS.getId()));
                break;
            case 1://上半场
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsHalf1Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_HALF1_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsHalf1Entities)) {
                        log.info("::{}::,AO赛事ID:{},阶段关盘上半场,条数:{}", linkId, aoMatchId, marketsHalf1Entities.size());
                    }
                }
                break;
            case 2://下半场 全场
                //判断时间 小于2分钟关盘
                if (periodRemainSec <= closingTime) {
                    List<Integer> marketCategoryIds = new ArrayList<>();
                    marketCategoryIds.addAll(MarketCategory.BASKETBALL_ALL_HALF2_MARKET_LIST);
                    marketCategoryIds.addAll(MarketCategory.BASKETBALL_ALL_FT_MARKET_LIST);
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsFTEntities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, marketCategoryIds);
                    if (!CollectionUtils.isEmpty(marketsFTEntities)) {
                        log.info("::{}::,AO赛事ID:{},阶段关盘全场与下半场,条数:{}", linkId, aoMatchId, marketsFTEntities.size());
                    }
                }
                break;
            case 40:
                break;
            case 301:  //第一节休息

                break;
            case 302: //第二节休息

                break;
            case 303:  //第三节休息

                break;
            case 100:  //常规时间结束
                List<Integer> newMarketCategoryQ4Ids = new ArrayList<>();
                newMarketCategoryQ4Ids.addAll(MarketCategory.BASKETBALL_ALL_Q4_MARKET_LIST);
                newMarketCategoryQ4Ids.addAll(MarketCategory.marketQ4);
                List<MarketsEntity> marketsEntities100 = restPeriodMarketClose(linkId, aoMatchId, marketList, newMarketCategoryQ4Ids);
                if (!CollectionUtils.isEmpty(marketsEntities100)) {
                    log.info("::{}::,AO赛事ID:{},常规时间结束关第四节,条数:{}", linkId, aoMatchId, JSONObject.toJSONString(marketsEntities100));
                }
                break;
            default:
                break;
        }
        aoMatchMarketInfo.setMarketList(marketList);
        return aoMatchMarketInfo;

    }

    /**
     * 休息阶段兜底关盘
     *
     * @param linkId
     * @param aoMatchId
     */
    private List<MarketsEntity> restPeriodMarketClose(String linkId, Long aoMatchId, List<MarketsEntity> marketList, List<Integer> marketIds) {
        Map<Integer, List<MarketsEntity>> marketListMap = marketList.stream().collect(Collectors.groupingBy(MarketsEntity::getMarketId));
        List<MarketsEntity> marketsEntities = new ArrayList<>();
        marketIds.forEach(marketId -> {
            List<MarketsEntity> marketsEntitiesList = marketListMap.get(marketId);
            if (!CollectionUtils.isEmpty(marketsEntitiesList)) {
                marketsEntitiesList.forEach(market -> {
                    market.setStatus(CommonConstant.NUMBER_TWO);
                    market.setModifyTime(System.currentTimeMillis());
                    marketsEntities.add(market);
                });
            }
        });
        return marketsEntities;
    }

    /**
     * 关闭所有盘口
     *
     * @param linkId
     * @param aoMatchId
     * @param marketList
     * @return
     */
    private List<MarketsEntity> allMarketClose(String linkId, Long aoMatchId, List<MarketsEntity> marketList, Double remainGe, Double closingGe, Integer nowPeriod) {
        List<MarketsEntity> marketsEntities = new ArrayList<>();
        if (CollectionUtils.isEmpty(marketList)) {
            return marketsEntities;
        }
        if (remainGe <= closingGe) {
            for (MarketsEntity market : marketList) {
                market.setModifyTime(System.currentTimeMillis());
                market.setStatus(CommonConstant.NUMBER_TWO);
            }
            marketsEntities.addAll(marketList);
            log.info("::{}::,AO赛事ID:{},阶段:{},关闭所有盘口,条数:{}", linkId, aoMatchId, nowPeriod, marketsEntities.size());
        }
        return marketsEntities;
    }

    /**
     * 带x兜底关盘
     *
     * @param linkId
     * @param aoMatchId
     * @param score
     * @return
     */
    public List<MarketsEntity> cacheBkLiveMarketByPeriodClose(String linkId, Long aoMatchId, Integer score, List<MarketsEntity> marketsEntities) {
        if (CollectionUtils.isEmpty(marketsEntities)) {
            return marketsEntities;
        }
        //比分+6  >= 盘口比分关盘
        marketsEntities.forEach(market -> {
            if (score >= Integer.parseInt(market.getHandicap())) {
                market.setStatus(CommonConstant.NUMBER_TWO);
                log.info("::{}::cacheBkLiveMarketByPeriodClose,AO赛事ID：{}，玩法ID：{}，比分：{}大于等于盘口：{}", linkId, aoMatchId, market.getMarketId(), score, market.getHandicap());
            }
        });
        return marketsEntities;
    }


    public AoMatchMarketInfo bkMarketPeriodTimeProcessor(String linkId, AoMatchMarketInfo<MarketsEntity> aoMatchMarketInfo, BasketballApplyParam basketballApplyParam, Double remainGe) {
        Long aoMatchId = Long.valueOf(aoMatchMarketInfo.getMatchSourceId());
        if (CollectionUtils.isEmpty(aoMatchMarketInfo.getMarketList())) {
            AoMatchMarketInfo<MarketsEntity> cacheMarketInfo = basketballMarketStatusProcessor.getCacheMarketInfo(linkId, aoMatchId);
            if (null != cacheMarketInfo) {
                aoMatchMarketInfo.setMarketList(cacheMarketInfo.getMarketList());
            }
        }
        List<MarketsEntity> marketList = aoMatchMarketInfo.getMarketList();
        if (CollectionUtils.isEmpty(marketList)) {
            return null;
        }
        Map<Integer, List<MarketsEntity>> marketListMap = marketList.stream().collect(Collectors.groupingBy(MarketsEntity::getMarketId));
        int nowPeriod = basketballApplyParam.getPeriodId();
        Integer periodRemainSec = basketballApplyParam.getPeriodRemainSec();
        Integer closingTime = basketballApplyParam.getClosingTime() * 60;
        Double closingGe = Double.valueOf(basketballApplyParam.getClosingGe());

        if (remainGe <= closingGe) {
            List<MarketsEntity> allMarketClose2 = allMarketClose(linkId, aoMatchId, marketList, remainGe, closingGe, nowPeriod);
            if (!CollectionUtils.isEmpty(allMarketClose2)) {
            }
            aoMatchMarketInfo.setMarketList(allMarketClose2);
            return aoMatchMarketInfo;
        }

        switch (nowPeriod) {
            case 13://第一节
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsQ1Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_Q1_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsQ1Entities)) {
                    }
                }
                //第一节关闭 小于等于4分钟关盘
                if (periodRemainSec <= 4 * 60) {
                    List<MarketsEntity> marketsStableQ1Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_STABLE_Q1_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsStableQ1Entities)) {
                    }
                }
                String[] q1ScoreStr = basketballApplyParam.getQ1Score().split(":");
                Integer homeQ1Score = Integer.parseInt(q1ScoreStr[0]) + 6;
                Integer awayQ1Score = Integer.parseInt(q1ScoreStr[1]) + 6;
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, homeQ1Score, marketListMap.get(BASKETBALL_Q1_HOME_X_PTS.getId()));
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, awayQ1Score, marketListMap.get(BASKETBALL_Q1_AWAY_X_PTS.getId()));
                break;
            case 14://第二节
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsQ2Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_Q2_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsQ2Entities)) {
                    }
                }
                //第二节关闭 小于等于4分钟关盘
                if (periodRemainSec <= 4 * 60) {
                    List<MarketsEntity> marketsStableQ2Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_STABLE_Q2_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsStableQ2Entities)) {
                    }
                }
                String[] q2ScoreStr = basketballApplyParam.getQ2Score().split(":");
                Integer homeQ2Score = Integer.parseInt(q2ScoreStr[0]) + 6;
                Integer awayQ2Score = Integer.parseInt(q2ScoreStr[1]) + 6;
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, homeQ2Score, marketListMap.get(BASKETBALL_Q2_HOME_X_PTS.getId()));
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, awayQ2Score, marketListMap.get(BASKETBALL_Q2_HOME_X_PTS.getId()));
                break;
            case 15://第三节
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsQ3Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_Q3_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsQ3Entities)) {
                    }
                }
                //第三节关闭 小于等于4分钟关盘
                if (periodRemainSec <= 4 * 60) {
                    List<MarketsEntity> marketsStableQ3Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_STABLE_Q3_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsStableQ3Entities)) {
                    }
                }
                String[] q3ScoreStr = basketballApplyParam.getQ3Score().split(":");
                Integer homeQ3Score = Integer.parseInt(q3ScoreStr[0]) + 6;
                Integer awayQ3Score = Integer.parseInt(q3ScoreStr[1]) + 6;
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, homeQ3Score, marketListMap.get(BASKETBALL_Q3_HOME_X_PTS.getId()));
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, awayQ3Score, marketListMap.get(BASKETBALL_Q3_HOME_X_PTS.getId()));
                break;
            case 16://第四节
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsQ4Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_Q4_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsQ4Entities)) {
                    }
                }
                //第4节关闭 小于等于4分钟关盘
                if (periodRemainSec <= 4 * 60) {
                    List<MarketsEntity> marketsStableQ4Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_STABLE_Q4_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsStableQ4Entities)) {
                    }
                }
                String[] q4ScoreStr = basketballApplyParam.getQ4Score().split(":");
                Integer homeQ4Score = Integer.parseInt(q4ScoreStr[0]) + 6;
                Integer awayQ4Score = Integer.parseInt(q4ScoreStr[1]) + 6;
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, homeQ4Score, marketListMap.get(BASKETBALL_Q4_HOME_X_PTS.getId()));
                cacheBkLiveMarketByPeriodClose(linkId, aoMatchId, awayQ4Score, marketListMap.get(BASKETBALL_Q4_HOME_X_PTS.getId()));
                break;
            case 1://上半场
                //判断时间 小于等于1分钟关盘
                if (periodRemainSec <= 60) {
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsHalf1Entities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, MarketCategory.BASKETBALL_ALL_HALF1_MARKET_LIST);
                    if (!CollectionUtils.isEmpty(marketsHalf1Entities)) {
                    }
                }
                break;
            case 2://下半场 全场
                //判断时间 小于2分钟关盘
                if (periodRemainSec <= closingTime) {
                    List<Integer> marketCategoryIds = new ArrayList<>();
                    marketCategoryIds.addAll(MarketCategory.BASKETBALL_ALL_HALF2_MARKET_LIST);
                    marketCategoryIds.addAll(MarketCategory.BASKETBALL_ALL_FT_MARKET_LIST);
                    //关盘玩法集玩法
                    List<MarketsEntity> marketsFTEntities = basketballMarketStatusProcessor.marketPeriodStatusClose(marketList, marketCategoryIds);
                    if (!CollectionUtils.isEmpty(marketsFTEntities)) {
                    }
                }
                break;
            case 40:
                break;
            case 301:  //第一节休息

                break;
            case 302: //第二节休息

                break;
            case 303:  //第三节休息

                break;
            case 100:  //常规时间结束
                List<Integer> newMarketCategoryQ4Ids = new ArrayList<>();
                newMarketCategoryQ4Ids.addAll(MarketCategory.BASKETBALL_ALL_Q4_MARKET_LIST);
                newMarketCategoryQ4Ids.addAll(MarketCategory.marketQ4);
                List<MarketsEntity> marketsEntities100 = restPeriodMarketClose(linkId, aoMatchId, marketList, newMarketCategoryQ4Ids);
                if (!CollectionUtils.isEmpty(marketsEntities100)) {
                }
                break;
            default:
                break;
        }
        aoMatchMarketInfo.setMarketList(marketList);
        return aoMatchMarketInfo;

    }

}
