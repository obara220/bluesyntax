package com.panda.aoodds.esport.marketstate.processor;

import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.utils.TraceIdGenerator.createTraceId;


/**
 * 足球阶段时间关盘
 */
@Slf4j
@Component
public class FootBallPeriodTimeProcessor {

    @Autowired
    private FootballMarketStatusProcessor footballMarketStatusProcessor;

    @Autowired
    private RedisService redisService;

    private final static Long MILLI_1 = 1L * 60 * 1000;
    private final static Long MILLI_2 = 2L * 60 * 1000;
    private final static Long MILLI_3 = 3L * 60 * 1000;
    private final static Long MILLI_15 = 15L * 60 * 1000;
    //支持阶段提关盘类型
    private final static List<String> REQUEST_TYPE = Arrays.asList("g_goal", "g_corner", "g_booking", "g_yc", "g_rc");
//    @Autowired
//    private MarketOddsLinkageNew marketOddsLinkageNew;

    /**
     * 盘口时间关盘
     * 上半场：上半场开始时间 + 上半场时间 + 上半场补时时间 - 2
     * 全场： 下半场开始时间+上半场时间 +下半场补时 - 2
     * 15分钟：0-15 下半场开始时间 + 15 分钟  - 2 ，16-30 下半场开始时间 + 30 分钟  - 2
     * <p>
     * 6:上半场、7:下半场
     *
     * @param linkId
     * @param aoMatchMarketInfo
     * @param marketParamEntiy
     * @return
     */
//    public AoMatchMarketInfo marketPeriodTimeProcessor(String linkId, AoMatchMarketInfo<MarketsEntity> aoMatchMarketInfo, MarketParamEntiy marketParamEntiy) {
//        //阶段开始时间
//        Long aoMatchId = Long.valueOf(aoMatchMarketInfo.getMatchSourceId());
//        Object matchPeriodObj = redisService.hGet(AO_BGTIME_KEY, String.valueOf(aoMatchId));
//        if (ObjectUtil.isNull(matchPeriodObj)) {
//            return null;
//        }
//        String[] startTimeAndPeriod = matchPeriodObj.toString().split("#");
//        //阶段开始时间
//        Long periodStartTime = Long.parseLong(startTimeAndPeriod[0]);
//        //阶段
//        Long period = Long.parseLong(startTimeAndPeriod[1]);
//        if (6L != period && 7L != period && 31L != period && 34L != period) {
//            return null;
//        }
//        //上半场时间
//        Long half1stPeriod = initTime(marketParamEntiy.getHalf1stPeriod());
//        //上半场补时
//        Long injTime1st = initTime(marketParamEntiy.getInjTime1st());
//        //全场补时
//        Long injTime2nd = initTime(marketParamEntiy.getInjTime2nd());
//        //最新赛事盘口缓存数据
//        AoMatchMarketInfo cacheMarketInfo = footballMarketStatusProcessor.getCacheMarketInfo(linkId, aoMatchId);
//        if (null == cacheMarketInfo) {
//            return null;
//        }
//
//        List<MarketsEntity> finalMarketList = new ArrayList<>();
//        List<MarketsEntity> cacheMarketList = cacheMarketInfo.getMarketList();
//        long nowTime = System.currentTimeMillis();
//        Map<String, Boolean> minutesMap = new HashMap<>();
//        String requestType = marketParamEntiy.getRequestType();
//        if (requestType.equals("pk")) {
//            if (period == 34) {
//                //PK阶段直接关闭加时赛玩法
//                List<MarketsEntity> marketsFtEntities = footballMarketStatusProcessor.marketPeriodStatusClose(linkId, FootballCategorySet.OVERTIME, aoMatchId, cacheMarketList);
//                if (!CollectionUtils.isEmpty(marketsFtEntities)) {
//                    log.info("::{}::,AO赛事ID:{},阶段关盘,PK阶段关闭加时赛盘口,条数:{}", linkId, aoMatchId, marketsFtEntities.size());
//                    finalMarketList.addAll(marketsFtEntities);
//                }
//            }
//        } else if (REQUEST_TYPE.contains(requestType)) {
//            if (period == 6) {
//                //上半场分钟关盘初始化
//                minutesMap = minutesHtInit(nowTime, periodStartTime);
//                //上半场结束：关闭上半场/下半场所有类型玩法
//                boolean htIsTrue = timeEquals(nowTime, periodStartTime + half1stPeriod + injTime1st);
//                log.info("::{}::,AO赛事ID:{},阶段关盘,上半场提前两分钟关盘盘口,当前时间:{},阶段开始时间:{},上半场阶段时间:{},上半场补时:{},最终阶段关盘时间:{},htIsTrue:{},上半场15分钟:{}", linkId, aoMatchId, timeFormat(nowTime), timeFormat(periodStartTime), marketParamEntiy.getHalf1stPeriod(), marketParamEntiy.getInjTime1st(), timeFormat(periodStartTime + half1stPeriod + injTime1st - MILLI_1), htIsTrue, minutesMap);
//                if (htIsTrue) {
//                    List<MarketsEntity> marketsHtEntities = footballMarketStatusProcessor.marketPeriodStatusClose(linkId, FootballCategorySet.HT, aoMatchId, cacheMarketList);
//                    if (!CollectionUtils.isEmpty(marketsHtEntities)) {
//                        log.info("::{}::,AO赛事ID:{},阶段关盘,上半场提前两分钟关盘盘口,条数:{}", linkId, aoMatchId, marketsHtEntities.size());
//                        finalMarketList.addAll(marketsHtEntities);
//                    }
//                }
//            } else if (period == 7) {
//                //全场分钟关盘初始化
//                minutesMap = minutesFtInit(nowTime, periodStartTime);
//                //全场结束：关闭所有类型全场玩法
//                boolean ftIsTrue = timeEquals(nowTime, periodStartTime + half1stPeriod + injTime2nd);
//                log.info("::{}::,AO赛事ID:{},阶段关盘,全场提前两分钟关盘盘口,当前时间:{},阶段开始时间:{},上半场阶段时间:{},下半场补时:{},最终阶段关盘时间:{},ftIsTrue:{},下半场15分钟:{}", linkId, aoMatchId, timeFormat(nowTime), timeFormat(periodStartTime), marketParamEntiy.getHalf1stPeriod(), marketParamEntiy.getInjTime2nd(), timeFormat(periodStartTime + half1stPeriod + injTime2nd - MILLI_1), ftIsTrue, minutesMap);
//                if (ftIsTrue) {
//                    List<MarketsEntity> marketsFtEntities = footballMarketStatusProcessor.marketPeriodStatusClose(linkId, FootballCategorySet.FT, aoMatchId, cacheMarketList);
//                    if (!CollectionUtils.isEmpty(marketsFtEntities)) {
//                        log.info("::{}::,AO赛事ID:{},阶段关盘,全场提前两分钟关盘盘口,条数:{}", linkId, aoMatchId, marketsFtEntities.size());
//                        finalMarketList.addAll(marketsFtEntities);
//                    }
//                }
//            }
//            //中场休息和下半场期间：下半场波胆，下半场反波胆 处理
//            List<MarketsEntity> marketScorrectScoreEntities = correctScoreClose(linkId, aoMatchId, period, cacheMarketList);
//            if (!CollectionUtils.isEmpty(marketScorrectScoreEntities)) {
//                log.info("::{}::,AO赛事ID:{},阶段关盘,中场休息和下半场期间下半场波胆下半场反波胆:{}", linkId, aoMatchId, JSONObject.toJSONString(marketScorrectScoreEntities));
//                finalMarketList.addAll(marketScorrectScoreEntities);
//            }
//            List<MarketsEntity> marketsMinutesEntities = footballMarketStatusProcessor.marketPeriodMinutesStatusClose(linkId, FootballCategorySet.MINUTES, aoMatchId, minutesMap, cacheMarketList);
//            if (!CollectionUtils.isEmpty(marketsMinutesEntities)) {
//                log.info("::{}::,AO赛事ID:{},阶段关盘,15分钟类玩法关盘信息:{}", linkId, aoMatchId, JSONObject.toJSONString(minutesMap));
//                finalMarketList.addAll(marketsMinutesEntities);
//            }
//        } else {
//            log.info("::{}::AO赛事ID:{},阶段关盘,阶段不匹配:{}", linkId, aoMatchId, requestType);
//            return null;
//        }
//        if (CollectionUtils.isEmpty(finalMarketList)) {
//            log.info("::{}::AO赛事ID:{},阶段关盘,最终下发关盘盘口不存在", linkId, aoMatchId);
//            return null;
//        }
//        log.info("::{}::AO赛事ID:{},阶段关盘,最终下发关盘盘口:{}", linkId, aoMatchId, finalMarketList.size());
//        // 根据玩法替换盘口
//        Map<Integer, List<MarketsEntity>> marketListMap = aoMatchMarketInfo.getMarketList().stream().collect(Collectors.groupingBy(MarketsEntity::getMarketId));
//        Map<Integer, List<MarketsEntity>> finalMarketListMap = finalMarketList.stream().collect(Collectors.groupingBy(MarketsEntity::getMarketId));
//        marketListMap.putAll(finalMarketListMap);
//
//        List<MarketsEntity> newMarketList = new ArrayList<>();
//        marketListMap.values().forEach(newMarketList::addAll);
//
//        aoMatchMarketInfo.setMarketList(newMarketList);
//        return aoMatchMarketInfo;
//    }


    /**
     * 上半场15分钟阶段时间
     *
     * @param nowTime
     * @param periodStartTime
     */
    private Map<String, Boolean> minutesHtInit(long nowTime, long periodStartTime) {
        Map<String, Boolean> map = new TreeMap<>();
        map.put("15", minuteTimeEquals(nowTime, periodStartTime + MILLI_15));
        map.put("30", minuteTimeEquals(nowTime, periodStartTime + MILLI_15 * 2));
        map.put("60", false);
        map.put("75", false);
        return map;
    }

    /**
     * 下半场15分钟阶段时间
     *
     * @param nowTime
     * @param periodStartTime
     */
    private Map<String, Boolean> minutesFtInit(long nowTime, long periodStartTime) {
        Map<String, Boolean> map = new TreeMap<>();
        map.put("15", true);
        map.put("30", true);
        map.put("60", minuteTimeEquals(nowTime, periodStartTime + MILLI_15));
        map.put("75", minuteTimeEquals(nowTime, periodStartTime + MILLI_15 * 2));
        return map;
    }

    /**
     * 当前时间 + 阶段时间 与 已知的最终关盘实现进行对比 1分钟内返回TRUE
     *
     * @param time1
     * @param time2
     * @return
     */
    private boolean timeEquals(Long time1, Long time2) {
        return time1 >= (time2 - MILLI_1);
    }

    /**
     * 15分钟
     * 当前时间 + 阶段时间 与 已知的最终关盘实现进行对比 1分钟内返回TRUE
     *
     * @param time1
     * @param time2
     * @return
     */
    private boolean minuteTimeEquals(Long time1, Long time2) {
        return time1 >= (time2 - MILLI_1);
    }

    /**
     * 分钟 转换为 毫秒
     *
     * @param time
     * @return
     */
    private Long initTime(String time) {
        return StringUtils.isEmpty(time) ? 0L : Long.valueOf(time) * 60 * 1000;
    }

    /**
     * 转时间
     *
     * @return
     */
    private static String timeFormat(Long time) {
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }


    /**
     * 休息或常规赛结束 ，关闭玩法
     */
    public AoMatchMarketInfo standardEventPeriodRestMarketProcessor(String linkId, Long aoMatchId, Long matchPeriodId) {
        String newLinkId = createTraceId() + "_REST";
        log.info("::{}::转换linkId:{},AO赛事ID:{},休息或常规赛结束阶段:{},关盘进入", linkId, newLinkId, aoMatchId, matchPeriodId);
        //最新赛事盘口缓存数据
        AoMatchMarketInfo cacheMarketInfo = footballMarketStatusProcessor.getCacheMarketInfo(linkId, aoMatchId);
        if (null == cacheMarketInfo) {
            log.info("::{}::转换linkId:{},AO赛事ID:{},休息或常规赛结束阶段,最新赛事盘口缓存数据不存在", linkId, newLinkId, aoMatchId);
            return null;
        }
        log.info("::{}::转换linkId:{},AO赛事ID:{},休息阶段关盘,缓存赛事信息", linkId, newLinkId, aoMatchId);
        List<MarketsEntity> cacheMarketList = cacheMarketInfo.getMarketList();
        if (CollectionUtils.isEmpty(cacheMarketList)) {
            log.info("::{}::转换linkId:{},AO赛事ID:{},休息或常规赛结束阶段,盘口不存在", linkId, newLinkId, aoMatchId);
            return null;
        }
        if (matchPeriodId == 31L) {
            cacheMarketList = cacheMarketList.stream().filter(m -> MarketCategory.ALL_SCORE_HALF1_MARKET_LIST.contains(m.getMarketId())).collect(Collectors.toList());
        }
        cacheMarketList.forEach(k -> k.setStatus(CommonConstant.NUMBER_TWO));
        cacheMarketInfo.setMarketList(cacheMarketList);
        log.info("::{}::转换linkId:{},AO赛事ID:{},休息或常规赛结束阶段关盘条数:{},成功。", linkId, newLinkId, aoMatchId, cacheMarketList.size());
        cacheMarketInfo.setLinkeId(newLinkId);
        return cacheMarketInfo;
    }

//    /**
//     * 中场休息和下半场期间：
//     * 1、全场比分，主队得分<=4且者客队得分<=4，把这两个玩法数据商关掉，不显示，不开出去
//     * 2、全场比分，主队得分>4或者客队得分>4，正常打开放出来，能卖出去，正常的主逻辑开关封锁
//     *
//     * @param linkId
//     * @param aoMatchId
//     * @param period
//     * @param cacheMarketList
//     * @return
//     */
//    private List<MarketsEntity> correctScoreClose(String linkId, Long aoMatchId, Long period, List<MarketsEntity> cacheMarketList) {
//        if (period != 31L && period != 7L) {
//            return null;
//        }
//        JSONObject goalObj = marketOddsLinkageNew.nowScore(linkId, aoMatchId.toString());
//        JSONObject goal = goalObj.getJSONObject("goal");
//        Integer homeScore = goal.getInteger("home");
//        Integer awayScore = goal.getInteger("away");
//        log.info("::{}::,中场休息和下半场期间,主客比分:{}-{},", linkId, homeScore, awayScore);
//        if (homeScore * awayScore == 0) {
//            return cacheMarketList.stream().filter(m -> 60008 == m.getMarketId()).map(m -> {
//                m.setStatus(CommonConstant.NUMBER_TWO);
//                return m;
//            }).collect(Collectors.toList());
//        }
//
//        if (homeScore <= 4 && awayScore <= 4) {
//            return cacheMarketList.stream().filter(m -> 60006 == m.getMarketId() || 60017 == m.getMarketId()).map(m -> {
//                m.setStatus(CommonConstant.NUMBER_TWO);
//                return m;
//            }).collect(Collectors.toList());
//        }
//        return null;
//    }
}
