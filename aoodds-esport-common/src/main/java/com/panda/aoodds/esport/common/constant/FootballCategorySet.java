package com.panda.aoodds.esport.common.constant;

import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.common.market.MarketCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 足球种类玩法
 */
public class FootballCategorySet {


    /**
     * 比分时间关盘集合
     */
    public final static Map<String, List<Integer>> TYPE = new HashMap<>();
    /**
     * 进球类
     */
    public final static List<Integer> G_GOAL_CATEGORY = new ArrayList<>();
    /**
     * 角球类
     */
    public final static List<Integer> G_CORNER_CATEGORY = new ArrayList<>();
    /**
     * 罚牌类
     */
    public final static List<Integer> G_FA_CARD_CATEGORY = new ArrayList<>();


    /**
     * 阶段时间关盘玩法
     */
    public final static Map<String, List<Integer>> PERIOD_CATEGORY = new HashMap<>();

    /**
     * 上半场/下半场
     */
    public final static String HT = "ht";
    /**
     * 全场
     */
    public final static String FT = "ft";
    /**
     * 15分钟
     */
    public final static String MINUTES = "minutes";
    /**
     * 加时赛
     */
    public final static String OVERTIME = "overtime";

    static {
        /** 进球类玩法 */
        G_GOAL_CATEGORY.addAll(MarketCategory.ALL_SCORE_REGULAR_MARKET_LIST);//进球类全场盘口
        G_GOAL_CATEGORY.addAll(MarketCategory.ALL_SCORE_HALF1_MARKET_LIST);//进球类上半场盘口
        G_GOAL_CATEGORY.addAll(MarketCategory.ALL_SCORE_HALF2_MARKET_LIST);//进球类下半场盘口
        G_GOAL_CATEGORY.addAll(MarketCategory.FIFTEEN_ATOB_MARKET_LIST);//15分钟

        /** 角球类玩法 */
        G_CORNER_CATEGORY.addAll(MarketCategory.CORNER_REGULAR_MARKET_LIST);//角球类全场盘口
        G_CORNER_CATEGORY.addAll(MarketCategory.CORNER_HALF1_MARKET_LIST);//角球类上半场盘口
        G_CORNER_CATEGORY.addAll(MarketCategory.CORNER_HALF2_MARKET_LIST);//角球类下半场盘口
        /** 罚牌类玩法 */
        G_FA_CARD_CATEGORY.addAll(MarketCategory.BOOKING_REGULAR_MARKET_LIST);//罚牌类全场盘口
        G_FA_CARD_CATEGORY.addAll(MarketCategory.BOOKING_HALF1_MARKET_LIST);//罚牌类上半场盘口
        G_FA_CARD_CATEGORY.addAll(MarketCategory.BOOKING_HALF2_MARKET_LIST);//罚牌类下半场盘口

        TYPE.put("g_goal", G_GOAL_CATEGORY);//全场进球类玩法
        TYPE.put("ex_goal", MarketCategory.OVERTIME_GOAL_ALL_LIST);//加时进球类玩法
        TYPE.put("g_corner", G_CORNER_CATEGORY);//全场角球类玩法
        TYPE.put("ex_corner", MarketCategory.OVERTIME_CORNERS_ALL_LIST);//加时角球类玩法
        TYPE.put("g_booking", G_FA_CARD_CATEGORY);//全场罚牌类玩法
        TYPE.put("ex_booking", new ArrayList<>());//加时罚牌类玩法
        TYPE.put("pk", MarketCategory.PK_ALL_LIST);//pk类玩法
        TYPE.put("ht_goal", MarketCategory.ALL_SCORE_HALF1_MARKET_LIST);//上半场玩法


        //上半场结束：关闭上半场/下半场所有类型玩法 ，全场结束：关闭所有类型全场玩法
        List<Integer> allHalf1AndHalf2Category = new ArrayList<>();
        allHalf1AndHalf2Category.addAll(MarketCategory.ALL_SCORE_HALF1_MARKET_LIST);
        allHalf1AndHalf2Category.addAll(MarketCategory.ALL_SCORE_HALF2_MARKET_LIST);
        PERIOD_CATEGORY.put(HT, allHalf1AndHalf2Category);//所有进球类上半场/下半场盘口
        PERIOD_CATEGORY.put(FT, MarketCategory.ALL_SCORE_REGULAR_MARKET_LIST);//所有进球类全场盘口
        PERIOD_CATEGORY.put(MINUTES, MarketCategory.FIFTEEN_ATOB_MARKET_LIST);//15分钟玩法
        PERIOD_CATEGORY.put(OVERTIME, MarketCategory.OVERTIME_ALL_LIST);//PK阶段关闭加时赛
    }

    public static void main(String[] args) {
        System.out.println(JSONObject.toJSONString(TYPE));
    }
}
