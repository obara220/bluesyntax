package com.panda.aoodds.esport.common.enums;

import com.google.common.collect.Lists;
import com.panda.aoodds.esport.common.entity.TradeMarketItemConfig;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.utils.BigDecimalUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.market.MarketCategory.*;


/**
 * 玩法抽水枚举
 *
 * @author Samuel
 */
@Getter
public enum MarketMargin {

    //-------------------------------------全场玩法-------------------------------------------------------------------
    /**
     * 全场独赢
     */
    MARGIN_FT_1X2(FT_1X2.getId(), 115),
    /**
     * 全场双重机会
     */
    MARGIN_FT_DOUBLE_CHANCE(FT_DOUBLE_CHANCE.getId(), 225),
    /**
     * 全场波胆
     */
    MARGIN_FT_CORRECT_SCORE(FT_CORRECT_SCORE.getId(), 115),
    /**
     * 全场净胜球（10项）
     */
    MARGIN_FT_WINNING_MARGIN(FT_WINNING_MARGIN.getId(), 125),
    /**
     * 全场净胜球（7项）
     */
    MARGIN_FT_WINNING_MARGIN_SEVEN(FT_WINNING_MARGIN_SEVEN.getId(), 125),
    /**
     * 全场让球胜平负
     */
    MARGIN_FT_THREE_WAY_HANDICAP(FT_THREE_WAY_HANDICAP.getId(), 115),
    /**
     * 准确进球数
     */
    MARGIN_FT_EXACT_GOALS(FT_EXACT_GOALS.getId(), 125),
    /**
     * 总进球数区间
     */
    MARGIN_FT_GOAL_RANGE(FT_GOAL_RANGE.getId(), 125),
    /**
     * 主队准确进球数
     */
    MARGIN_FT_TEAM1_EXACT_GOALS(FT_TEAM1_EXACT_GOALS.getId(), 125),
    /**
     * 客队准确进球数
     */
    MARGIN_FT_TEAM2_EXACT_GOALS(FT_TEAM2_EXACT_GOALS.getId(), 125),
    /**
     * 半/全场比分
     */
    MARGIN_FT_HT_FT_CORRECT_SCORE(FT_HT_FT_CORRECT_SCORE.getId(), 135),
    /**
     * 半/全场
     */
    MARGIN_FT_HT_FT(FT_HT_FT.getId(), 125),
    /**
     * 最后进球队伍
     */
    MARGIN_FT_LAST_GOAL(FT_LAST_GOAL.getId(), 115),
    /**
     * 独赢 & 进球大小
     */
    MARGIN_FT_1X2_AND_TOTAL(FT_1X2_AND_TOTAL.getId(), 125),
    /**
     * 独赢 & 两队都进球
     */
    MARGIN_FT_1X2_AND_BOTH_TEAM_SCORE(FT_1X2_AND_BOTH_TEAM_SCORE.getId(), 125),
    /**
     * 进球大小 & 两队都进球
     */
    MARGIN_FT_TOTAL_AND_BOTH_TEAM_SCORE(FT_TOTAL_AND_BOTH_TEAM_SCORE.getId(), 125),
    /**
     * 双重机会 & 两队都进球
     */
    MARGIN_FT_DOUBLE_CHANCE_AND_BOTH_TEAM_SCORE(FT_DOUBLE_CHANCE_AND_BOTH_TEAM_SCORE.getId(), 125),
    /**
     * 上/下半场两队都进球
     */
    MARGIN_FT_HALF1_HALF2_BOTH_TEAM_SCORE(FT_HALF1_HALF2_BOTH_TEAM_SCORE.getId(), 125),
    /**
     * 最多进球的半场
     */
    MARGIN_FT_HIGHEST_SCORING_HALF(FT_HIGHEST_SCORING_HALF.getId(), 115),
    /**
     * 第{X}个进球
     */
    MARGIN_FT_XTH_GOAL(FT_XTH_GOAL.getId(), 115),
    /**
     * 第{X}个进球何时发生（15分钟区间）
     */
    MARGIN_FT_TIME_XTH_GOAL_15MIN(FT_TIME_XTH_GOAL_15MIN.getId(), 120),
    /**
     * {主队}最高得分半场
     */
    MARGIN_FT_TEAM1_HIGHEST_SCORING_HALF(FT_TEAM1_HIGHEST_SCORING_HALF.getId(), 115),
    /**
     * {客队}最高得分半场
     */
    MARGIN_FT_TEAM2_HIGHEST_SCORING_HALF(FT_TEAM2_HIGHEST_SCORING_HALF.getId(), 115),

    //-------------------------------------半场玩法-------------------------------------------------------------------

    /**
     * 半场独赢
     */
    MARGIN_HT_1X2(HALF1_1X2.getId(), 115),
    /**
     * 半场双重机会
     */
    MARGIN_HT_DOUBLE_CHANCE(HALF1_DOUBLE_CHANCE.getId(), 225),
    /**
     * 半场波胆
     */
    MARGIN_HT_CORRECT_SCORE(HALF1_CORRECT_SCORE.getId(), 125),
    /**
     * 上半场准确进球数
     */
    MARGIN_HALF1_EXACT_GOALS(HALF1_EXACT_GOALS.getId(), 125),
    /**
     * 上半场独赢 & 上半场两队都进球
     */
    MARGIN_HALF1_1X2_AND_BOTH_TEAM_SCORE(HALF1_1X2_AND_BOTH_TEAM_SCORE.getId(), 125),
    /**
     * 上半场{主队}准确进球数
     */
    MARGIN_HALF1_TEAM1_EXACT_GOALS(HALF1_TEAM1_EXACT_GOALS.getId(), 120),
    /**
     * 上半场{客队}准确进球数
     */
    MARGIN_HALF1_TEAM2_EXACT_GOALS(HALF1_TEAM2_EXACT_GOALS.getId(), 120),
    /**
     * 上半场第{X}个进球
     */
    MARGIN_HALF1_XTH_GOAL(HALF1_XTH_GOAL.getId(), 115),
    /**
     * 上半场让球胜平负
     */
    MARGIN_HALF1_THREE_WAY_HANDICAP(HALF1_THREE_WAY_HANDICAP.getId(), 115),

    //-------------------------------------15分钟玩法-------------------------------------------------------------------

    /**
     * 15min独赢（a-b）
     */
    MARGIN_FIFTEEN_1X2_ATOB(FIFTEEN_1X2_ATOB.getId(), 115),

    //-------------------------------------角球玩法-------------------------------------------------------------------

    /**
     * 全场角球独赢
     */
    MARGIN_CORNER_1X2(CORNER_1X2.getId(), 115),
    /**
     * 全场最后一个角球
     */
    MARGIN_CORNER_LAST_CORNER(CORNER_LAST_CORNER.getId(), 115),
    /**
     * 半场角球独赢
     */
    MARGIN_CORNER_H1_1X2(CORNER_H1_1X2.getId(), 115),
    /**
     * 角球总数区间
     */
    MARGIN_CORNER_RANGE(CORNER_RANGE.getId(), 115),
    /**
     * {主队}角球总数区间
     */
    MARGIN_CORNER_TEAM1_RANGE(CORNER_TEAM1_RANGE.getId(), 120),
    /**
     * {客队}角球总数区间
     */
    MARGIN_CORNER_TEAM2_RANGE(CORNER_TEAM2_RANGE.getId(), 120),
    /**
     * 谁先获得{X}个角球
     */
    MARGIN_CORNER_RACE_XTH_CORNERS(CORNER_RACE_XTH_CORNERS.getId(), 115),
    /**
     * 第{X}个角球
     */
    MARGIN_CORNER_XTH_CORNER(CORNER_XTH_CORNER.getId(), 115),
    /**
     * 上半场角球总数区间
     */
    MARGIN_CORNER_H1_RANGE(CORNER_H1_RANGE.getId(), 115),
    /**
     * 上半场谁先获得{X}个角球
     */
    MARGIN_CORNER_H1_RACE_XTH_CORNERS(CORNER_H1_RACE_XTH_CORNERS.getId(), 115),
    /**
     * 上半场第{X}个角球
     */
    MARGIN_CORNER_H1_XTH_CORNER(CORNER_H1_XTH_CORNER.getId(), 115),
    /**
     * 15分钟角球-独赢({a}-{b})
     */
    MARGIN_CORNER_FIFTEEN_1X2(CORNER_FIFTEEN_1X2.getId(), 115),

    //-------------------------------------罚牌玩法-------------------------------------------------------------------

    /**
     * 全场罚牌独赢
     */
    MARGIN_BOOKING_1X2(BOOKING_1X2.getId(), 115),
    /**
     * 半场罚牌独赢
     */
    MARGIN_BOOKING_H1_1X2(BOOKING_H1_1X2.getId(), 115),
    /**
     * 第{X}张罚牌
     */
    MARGIN_BOOKING_XTH_BOOKING(BOOKING_XTH_BOOKING.getId(), 115),
    /**
     * 准确罚牌分数
     */
    MARGIN_BOOKING_EXACT_BOOKINGS(BOOKING_EXACT_BOOKINGS.getId(), 120),
    /**
     * {主队}准确罚牌分数
     */
    MARGIN_BOOKING_TEAM1_EXACT_BOOKINGS(BOOKING_TEAM1_EXACT_BOOKINGS.getId(), 120),
    /**
     * {客队}准确罚牌分数
     */
    MARGIN_BOOKING_TEAM2_EXACT_BOOKINGS(BOOKING_TEAM2_EXACT_BOOKINGS.getId(), 120),
    /**
     * 上半场准确罚牌分数
     */
    MARGIN_BOOKING_H1_EXACT_BOOKINGS(BOOKING_H1_EXACT_BOOKINGS.getId(), 120),
    /**
     * 上半场{主队}准确罚牌分数
     */
    MARGIN_BOOKING_H1_TEAM1_EXACT_BOOKINGS(BOOKING_H1_TEAM1_EXACT_BOOKINGS.getId(), 120),
    /**
     * 上半场{客队}准确罚牌分数
     */
    MARGIN_BOOKING_H1_TEAM2_EXACT_BOOKINGS(BOOKING_H1_TEAM2_EXACT_BOOKINGS.getId(), 120),
    /**
     * 黄牌独赢
     */
    MARGIN_BOOKING_1X2_YELLOW_CARDS(BOOKING_1X2_YELLOW_CARDS.getId(), 115),
    /**
     * 上半场黄牌独赢
     */
    MARGIN_BOOKING_H1_1X2_YELLOW_CARDS(BOOKING_H1_1X2_YELLOW_CARDS.getId(), 115),

    //-------------------------------------下半场玩法-------------------------------------------------------------------

    /**
     * 下半场独赢
     */
    MARGIN_HALF2_1X2(HALF2_1X2.getId(), 115),
    /**
     * 下半场双重机会
     */
    MARGIN_HALF2_DOUBLE_CHANCE(HALF2_DOUBLE_CHANCE.getId(), 225),
    /**
     * 下半场准确进球数
     */
    MARGIN_HALF2_EXACT_GOALS(HALF2_EXACT_GOALS.getId(), 125),
    /**
     * 下半场比分
     */
    MARGIN_HALF2_CORRECT_SCORE(HALF2_CORRECT_SCORE.getId(), 135),
    /**
     * 下半场独赢 & 下半场两队都进球
     */
    MARGIN_HALF2_1X2_AND_BOTH_TEAM_SCORE(HALF2_1X2_AND_BOTH_TEAM_SCORE.getId(), 125),
    /**
     * 下半场让球胜平负
     */
    MARGIN_HALF2_THREE_WAY_HANDICAP(HALF2_THREE_WAY_HANDICAP.getId(), 115),

    //-------------------------------------加时赛玩法-------------------------------------------------------------------

    /**
     * 加时赛-独赢
     */
    MARGIN_OVERTIME_1X2(OVERTIME_1X2.getId(), 115),
    /**
     * 加时赛-正确比分
     */
    MARGIN_OVERTIME_CORRECT_SCORE(OVERTIME_CORRECT_SCORE.getId(), 120),
    /**
     * 加时赛-第{X}个进球
     */
    MARGIN_OVERTIME_XTH_GOAL(OVERTIME_XTH_GOAL.getId(), 115),
    /**
     * 加时赛-上半场独赢
     */
    MARGIN_OVERTIME_H1_1X2(OVERTIME_H1_1X2.getId(), 115),

    //-------------------------------------点球大战玩法-------------------------------------------------------------------

    /**
     * 点球大战-净胜分
     */
    MARGIN_PENALTY_SHOOTOUT_WINNING_MARGIN(PENALTY_SHOOTOUT_WINNING_MARGIN.getId(), 120),
    /**
     * 点球大战-准确进球数
     */
    MARGIN_PENALTY_SHOOTOUT_EXACT_GOALS(PENALTY_SHOOTOUT_EXACT_GOALS.getId(), 120),

    //-------------------------------------特殊玩法-------------------------------------------------------------------

    /**
     * 获胜方式
     */
    MARGIN_SPECIAL_WINNING_METHOD(SPECIAL_WINNING_METHOD.getId(), 120),
    ;

    /**
     * 玩法Id
     */
    private final int marketId;

    /**
     * 玩法对应margin值
     */
    private final int margin;

    /**
     * 构造方法
     *
     * @param marketId 玩法Id
     * @param margin   玩法名称
     */
    MarketMargin(int marketId, int margin) {
        this.marketId = marketId;
        this.margin = margin;
    }

    /**
     * 根据玩法Id获取margin
     *
     * @param marketId 玩法Id
     * @return 玩法对应的margin
     */
    public static int getMarginByMarketId(int marketId) {
        for (MarketMargin marketMargin : MarketMargin.values()) {
            if (marketMargin.marketId == marketId) {
                return marketMargin.margin;
            }
        }
//        throw new IllegalArgumentException("玩法Id：【" + marketId + "】匹配margin失败！");
        return 0;
    }

    /**
     * 两项盘margin抽水玩法
     *
     */
    public static final List<Integer> MARGIN_MARKET_TWO = Lists.newArrayList(MarketCategory.FT_DRAW_NO_BET.getId(), MarketCategory.HALF1_DRAW_NO_BET.getId(), MarketCategory.HALF2_DRAW_NO_BET.getId());
    /**
     * 获取所有需要根据margin抽水的玩法Id
     *
     * @return 玩法Id集合
     */
    public static List<Integer> getMarginMarketList() {
        return Arrays.stream(MarketMargin.values()).map(MarketMargin::getMarketId).collect(Collectors.toList());
    }

    public static final List<Integer> MARGIN_MARKET_LIST = getMarginMarketList();

    /**
     * 多项盘概率调整，不同玩法的多项盘使用不同margin调整原始概率
     *
     * @param marketId     玩法Id
     * @param originalProb 原始概率
     * @return 调整后的概率
     */
    public static double modifyProbWithMargin(int marketId, double originalProb) {
        //不需要调整概率的玩法直接返回原始概率
        if (!MARGIN_MARKET_LIST.contains(marketId)) {
            return originalProb;
        }

        int margin = MarketMargin.getMarginByMarketId(marketId);
        //双重机会特殊处理
        if (marketId == MarketCategory.FT_DOUBLE_CHANCE.getId() || marketId == MarketCategory.HALF1_DOUBLE_CHANCE.getId() || marketId == MarketCategory.HALF2_DOUBLE_CHANCE.getId()) {
            return (originalProb * margin) / 200;
        }
        return (originalProb * margin) / 100;
    }

    public static void main(String[] args) {
        System.out.println(modifyOddsWithMargin(10001, 1,2.643D, 0, null));
    }

    /**
     * 多项盘赔率调整
     *
     * @param marketId     玩法Id
     * @param order        坑位
     * @param originalOdds 原始赔率
     * @param aoOddsValue  ao抽水赔率
     * @return 调整后的概率
     */
    public static double modifyOddsWithMargin(int marketId, Integer order, double originalOdds, double aoOddsValue, Map<String, TradeMarketItemConfig> marketMarginConfigMap) {
        //不需要调整概率的玩法直接返回AO抽水赔率
        if (0 == originalOdds) {
            return aoOddsValue;
        }
        int margin = MarketMargin.getMarginByMarketId(marketId);
        if (null != marketMarginConfigMap.get(marketId + "_" + order)) {
            margin = marketMarginConfigMap.get(marketId + "_" + order).getMargin().intValue();
        }
        if (0 == margin) {
            return originalOdds;
        }
        double value = 1.01D;
        //双重机会特殊处理
        if (marketId == MarketCategory.FT_DOUBLE_CHANCE.getId() || marketId == MarketCategory.HALF1_DOUBLE_CHANCE.getId() || marketId == MarketCategory.HALF2_DOUBLE_CHANCE.getId() || marketId == MarketCategory.FT_DOUBLE_CHANCE_AND_BOTH_TEAM_SCORE.getId()) {
            value = BigDecimalUtils.scale(originalOdds / ((double) margin / 200), 2);
        } else {
            value = BigDecimalUtils.scale(originalOdds / ((double) margin / 100), 2);
        }
        //计算出最终赔率小于1.01 并且 概率小于1 返回1.01
        if (value < 1.01D && originalOdds < 1D) {
            return 1.01D;
        }
        return value;
    }
}
