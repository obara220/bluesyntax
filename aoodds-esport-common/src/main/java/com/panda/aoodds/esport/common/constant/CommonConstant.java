package com.panda.aoodds.esport.common.constant;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AO常量类
 *
 * @author Samuel
 */
public class CommonConstant {

    /**
     * AO数据源编码
     */
    public static final String AO_SOURCE_CODE = "AO";

    //------------------------------------常用数值----------------------------------------------------

    public static final int NUMBER_ZERO = 0;

    public static final int NUMBER_ONE = 1;

    public static final int NUMBER_TWO = 2;

    public static final int NUMBER_HUNDRED_THOUSAND = 100000;

    public static final String MODEL_CODE="111";
    public static final String MODEL_CODE_HT="1";
    //------------------------------------体种----------------------------------------------------
    public static final int SPORT_FOOTBALL = 1;

    public static final Integer SPORT_BASKETBALL = 2;
    //------------------------------------常用符号----------------------------------------------------

    public static final String PUNCTUATION_UNDERLINE = "_";

    public static final String PUNCTUATION_COLON = ":";

    public static final String PUNCTUATION_BRACKETS_LEFT = "(";

    public static final String PUNCTUATION_BRACKETS_RIGHT = ")";

    public static final String PUNCTUATION_HYPHEN = "-";

    public static final String PUNCTUATION_COMMA = ",";

    public static final String PUNCTUATION_SPACE = " ";

    public static final String PUNCTUATION_PLUS = "+";

    public static final String PUNCTUATION_AMPERSAND = "&";

    //***********************************************/
    /**
     * 未开赛
     */
    public static final int PERIOD_0 = 0;
    /**
     * 第一节
     */
    public static final int PERIOD_13 = 13;
    /**
     * 第一节休息
     */
    public static final int PERIOD_301 = 301;
    /**
     * 第二节
     */
    public static final int PERIOD_14 = 14;
    /**
     * 第二节休息
     */
    public static final int PERIOD_302 = 302;
    /**
     * 第三节
     */
    public static final int PERIOD_15 = 15;
    /**
     * 第三节休息
     */
    public static final int PERIOD_303 = 303;
    /**
     * 第四节
     */
    public static final int PERIOD_16 = 16;
    /**
     * 加时赛
     */
    public static final int PERIOD_40 = 40;
    /**
     * 加时赛
     */
    public static final int PERIOD_100 = 100;
    /**
     * 等待加时开始
     */
    public static final int PERIOD_32 = 32;
    public static final int PERIOD_110 = 110;
    public static final int PERIOD_1 = 1;
    public static final int PERIOD_31 = 31;
    public static final int PERIOD_2 = 2;
    //------------------------------------常用字符串----------------------------------------------------


    public static final String NONE = "None";

    public static final String OTHER = "Other";

    public static final String OTHER_LOWER = "other";

    public static final String BY_LOWER = "by";

    public static final String HOME_LOWER = "home";

    public static final String AWAY_LOWER = "away";

    public static final String AND = "And";

    public static final String COMPETITOR_ONE = "competitor1";

    public static final String COMPETITOR_TWO = "competitor2";

    public static final String COMPETITOR_ONE_WITH_BRACE = "{$competitor1}";

    public static final String COMPETITOR_TWO_WITH_BRACE = "{$competitor2}";

    public static final String X_UPPER = "X";

    public static final String X0_UPPER = "X0";

    public static final String X1_UPPER = "X1";

    public static final String EVENT_STATUS = "EVENT_STATUS";

    //------------------------------------producer 常用字符串 ----------------------------------------------------

    public static final List<String> EVNETS = Arrays.asList("match_status");
    public static final List<Long> EVNETS_PERIOD = Arrays.asList(6L, 7L, 31L,32L,41L,42L,33L,50L, 100L,999L);
    public static final String OPERATION_MODE_CALC = "calc";
    public static final String OPERATION_MODE_APPLY = "apply";
    public static final String DOUBLE_CHECK_APPLY = "DoubleCheckApply";
    public static final String OPERATION_MODE_SPEC_APPLY = "specApply";
    public static final String OPERATION_MODE_INIT = "init";
    public static final String OPERATING_MODE_SWITCH= "SWITCH";

    public static final String OPERATING_PRE_ONE_APPLY= "preOneApply";

    public static final String OPERATING_PRE_ONE_REV_AND_APPLY= "preOneRevAndApply";
    public static final String OPERATION_MODE_REVERSE = "reverse";
    public static final String TIMER_DATA_FORMAT = "{\"aoMatchId\":%s,\"startTime\":%s,\"timing\":%s,\"currentTime\":%s}";
    public static final String APPLY_PARAM_FORMAT = "{\"aoMatchId\":%s,\"applyTime\":%s,\"message\":\"%s\"}";
    public static final String APPLY_INPLAY_FIRST_PARAM_FORMAT = "{\"aoMatchId\":%s,\"period\":%s,\"message\":\"%s\"}";
    /** 赛事模板 + reverse接口返回参数 配置 */
    public static final String MATCH_MARKET_CONFIG = "match_market_config";
    /** 赛事模板 + reverse接口返回参数 配置 */
    public static final String ESPORT_MATCH_MARKET_CONFIG = "esport_match_market_config";
    public static final String SPECIAL_EVENT_CONFIG = "special_event_config";

    /** 赛事模板 + reverse接口返回参数 配置 */
    public static final String ESPORT_BK_APPLY_CONFIG = "esport_bk_apply_config";

    public static final String ESPORT_BK_APPLY_CONFIG_CACHE = "ESPORT_BK_APPLY_CONFIG_CACHE:";


    /** 操盘手修改配置 赛事模板 + reverse接口返回参数 */
    public static final String MATCH_MARKET_PARAM = "match_market_param";
    /** 三方赛事信息 */
//    public static final String MATCH_INFO = "match_info";
    /** 三方电子赛事赛事信息 */
    public static final String MATCH_INFO_VS = "match_info_vs";
    /** 三方范特西赛事信息 */
    public static final String MATCH_FTS_INFO = "match_fts_info";

    /** 赛事自动开盘赛事 */
    public static final String AO_MATCH_AUTO_OPEN_MARKET = "match_auto_open_market";

    /** 比分 */
    public static final String SCORE_CENTER = "score_center";
    /** 盘口水差 */
    public static final String MARKET_DIFF_CONFIG = "market_diff_config";
    /** 坑位margin */
    public static final String MARGIN_PLACE_CONFIG = "margin_place_config";
    /** 标准事件 */
    public static final String STANDARD_EVENT = "standard_event";
    public static final String PRE_APPLY_SUCCESS_MATCH = "pre_apply_success_match";
    /** 融合玩法配置 */
    public static final String CATEGORY_SELL_CONFIG = "category_sell_config";
    /** 坑位margin、最大最小赔率 */
    public static final String TRADE_MARKET_ITEM_CONFIG = "trade_market_item_config";
    /** 坑位margin、最大最小赔率 */
    public static final String ESPORT_TRADE_MARKET_ITEM_CONFIG = "esport_trade_market_item_config";



    /********************篮球******************************/
    public static final List<String> BK_EVNETS = Arrays.asList("match_status","time_start");
    //------------------------------------producer 常用字符串  end----------------------------------------------------

    /** 三方赔率球头 REDIS KEY  常规比分*/
    public static final String THIRD_AO_MARKET_ODDS_KEY = "ThirdAoMarketOdds:";
    /** 三方赔率球头 REDIS KEY  常规角球*/
    public static final String THIRD_AO_MARKET_ODDS_CORNER_KEY = "ThirdAoMarketOdds:corner:";
    /** 三方赔率球头 REDIS KEY  常规罚牌*/
    public static final String THIRD_AO_MARKET_ODDS_BOOKINGS_KEY = "ThirdAoMarketOdds:bookings:";
    /** 三方赔率球头 REDIS KEY  常规红牌牌*/
    public static final String THIRD_AO_MARKET_ODDS_RC_KEY = "ThirdAoMarketOdds:rc:";
    /** 三方赔率球头 REDIS KEY  常规黄牌牌*/
    public static final String THIRD_AO_MARKET_ODDS_YC_KEY = "ThirdAoMarketOdds:yc:";
    /** 三方赔率球头 REDIS KEY  常规加时赛*/
    public static final String THIRD_AO_MARKET_ODDS_OVERTIME_KEY = "ThirdAoMarketOdds:overTime:";
    /** 三方赔率球头 REDIS KEY  加时赛角球大小*/
    public static final String THIRD_AO_MARKET_ODDS_OVERTIME_TOTAL_CORNERS_KEY = "ThirdAoMarketOdds:overTime:";
    /** 独赢1x2盘口*/
    public static final String FX_1X2_PROBABILITIES_KEY = "fx:1x2:probabilities:";

    /** 三方赔率球头 REDIS KEY  常规比分*/
    public static final String THIRD_BASKETBALL_AO_MARKET_ODDS_KEY = "ThirdBasketballAoMarketOdds:";

    /** 三方赔率球头玩法状态 REDIS KEY */
    public static final String THIRD_BASKETBALL_STATUS_KEY = "ThirdBasketballStatus:";
    /** 三方赔率球头玩法状态，次要玩法 REDIS KEY */
    public static final String THIRD_BASKETBALL_MAINLY_NOT_STATUS_KEY = "ThirdBasketballMainlyNotStatus:";
    /**  reverse code 对应 缓存KEY */
    public static Map<String, String> CATEGORY_TYPE_KEY = new HashMap<String, String>() {{
        //足球
        put("g_goal", THIRD_AO_MARKET_ODDS_KEY);
        put("g_corner", THIRD_AO_MARKET_ODDS_CORNER_KEY);
        put("g_booking", THIRD_AO_MARKET_ODDS_BOOKINGS_KEY);
        put("ex_goal", THIRD_AO_MARKET_ODDS_OVERTIME_KEY);
        put("ex_corner", THIRD_AO_MARKET_ODDS_OVERTIME_TOTAL_CORNERS_KEY);
        put("g_yc", THIRD_AO_MARKET_ODDS_YC_KEY);
        put("g_rc", THIRD_AO_MARKET_ODDS_RC_KEY);
        //篮球
        put("b_goal", THIRD_BASKETBALL_AO_MARKET_ODDS_KEY);
    }};
    /** 赔率变动记录 */
    public static final String ODDS_CHANGE_RECORD = "ODDS_CHANGE_RECORD:";

    /** 风控触发模版所有赛事变动集合  MAP<赛事ID,JSONObject(sportId,当前时间)> */
    public static final String CHANGE_MATCH_MAP = "CHANGE_MATCH_MAP";
    /** 风控触发模版所有赛事对应玩法变动集合  MAP<玩法ID,请求类型>*/
    public static final String CHANGE_MATCH_CATEGORY_MAP = "CHANGE_MATCH_CATEGORY_MAP:";

    /**
     * 足球标准玩法ID集  ，根据玩法查找类型
     */
    public static List<Long> FOOTBALL_STANDARD_CATEGORY_SCORE = Lists.newArrayList(2L, 4L, 18L, 19L);
    public static List<Long> FOOTBALL_STANDARD_CATEGORY_CORNER = Lists.newArrayList(113L, 114L, 121L, 122L);
    public static List<Long> FOOTBALL_STANDARD_CATEGORY_BOOKINGS = Lists.newArrayList(306L, 307L, 308L, 309L);
    public static List<Long> FOOTBALL_STANDARD_CATEGORY_OVERTIME = Lists.newArrayList(128L, 127L, 130L, 332L);
    public static List<Long> FOOTBALL_STANDARD_CATEGORY_OVERTIME_TOTAL_CORNERS = Lists.newArrayList(331L);
    public static List<Long> FOOTBALL_STANDARD_CATEGORY_YC = Lists.newArrayList(324L, 325L, 327L, 328L);
    public static Map<String, List<Long>> FOOTBALL_STANDARD_CATEGORY_SET = new HashMap<String, List<Long>>() {{
        put("g_goal", FOOTBALL_STANDARD_CATEGORY_SCORE);
        put("g_corner", FOOTBALL_STANDARD_CATEGORY_CORNER);
        put("g_booking", FOOTBALL_STANDARD_CATEGORY_BOOKINGS);
        put("ex_goal", FOOTBALL_STANDARD_CATEGORY_OVERTIME);
        put("ex_corner", FOOTBALL_STANDARD_CATEGORY_OVERTIME_TOTAL_CORNERS);
        put("g_yc", FOOTBALL_STANDARD_CATEGORY_YC);
        put("g_rc", FOOTBALL_STANDARD_CATEGORY_YC);
    }};

    /**
     * 篮球
     */
    public static List<Long> BASKETBALL_STANDARD_CATEGORY_SCORE = Lists.newArrayList(37L, 38L, 39L, 40L);
    public static Map<String, List<Long>> BASKETBALL_STANDARD_CATEGORY_SET = new HashMap<String, List<Long>>() {{
        put("b_goal", BASKETBALL_STANDARD_CATEGORY_SCORE);
    }};

    public static Map<Long, Map<String, List<Long>>> SPORT_STANDARD_CATEGORY_SET = new HashMap<Long, Map<String, List<Long>>>() {{
        put(1L,FOOTBALL_STANDARD_CATEGORY_SET);
        put(2L,BASKETBALL_STANDARD_CATEGORY_SET);
    }};

    /**
     * AO界面展示玩法
     */
    public static Map<String, List<Integer>> AO_CATEGORY_SET_SHOW = new HashMap<String, List<Integer>>() {{
        //足球
        put("g_goal", Lists.newArrayList(10001, 10008, 10004, 10005, 30003, 20001, 20008, 20004, 20005));
        put("g_corner", Lists.newArrayList(40001, 40002, 40003, 40008, 40009, 40010, 40024,40027));
        put("g_booking", Lists.newArrayList(50002, 50004, 50006, 50008, 50001, 50005, 50031));
        put("ex_goal", Lists.newArrayList(70001, 70003, 70002, 70008, 70009, 70010));
        put("ex_corner", Lists.newArrayList(40025));
        put("g_yc", Lists.newArrayList(50023,50024,50025,50026,50027,50028));
        put("g_rc", Lists.newArrayList(50009,50010,50011));
        put("pk", Lists.newArrayList(80010, 80011, 80005, 80003, 80002));
        //篮球
        put("b_goal", Lists.newArrayList(11001,11003,11002,11005,11006, 11007, 11009, 11008, 11011, 11012, 11019, 11021, 11020, 11023, 11024,
                11025, 11027, 11026, 11029, 11030, 11031, 11033, 11032, 11035, 11036, 11037, 11039, 11038, 11041, 11042));
    }};

    /**
     * 记录 FT 1X2两组胜平负概率
     */
    public static List<Integer> FX_1X2_PROBABILITIES_CATEGORY = Lists.newArrayList(10001, 70001);
    public static Map<Long,String> AO_MATCH_PERIOD_IDF= ImmutableMap.<Long, String>builder()
            .put(0L,"g")
            .put(6L,"g")
            .put(31L,"g")
            .put(7L,"g")
            .put(100L,"g")

            .put(32L,"ex")
            .put(41L,"ex")
            .put(33L,"ex")
            .put(42L,"ex")
            .put(110L,"ex")

            .put(50L,"pk")
            .put(120L,"pk")
            .build();
    public static Map<String,List<String>> AO_MATCH_PERIOD_MAP= ImmutableMap.<String, List<String>>builder()
            .put("g_goal",Lists.newArrayList("0","6","31","7","100"))
            .put("g_corner",Lists.newArrayList("0","6","31","7","100"))
            .put("g_booking",Lists.newArrayList("0","6","31","7","100"))

            .put("g_yc",Lists.newArrayList("0","6","31","7","100"))
            .put("g_rc",Lists.newArrayList("0","6","31","7","100"))

            .put("ex_goal",Lists.newArrayList("32","41","33","42","110"))
            .put("ex_corner",Lists.newArrayList("32","41","33","42","110"))

            .put("pk",Lists.newArrayList("50","120"))

            .build();
    public static final String[] FT_FULL_PROID={"0","6","31","7","100","32","41","33","42","110","34","50","120"};
    public  static  final String[] BK_INPLAY_PROID={"13","14","15","16","40","1","2"};

    //异常比分，不触发赔率下发
    public static final String AO_ESPORT_SCORE_ERROR_KEY="AO_ESPORT_SCORE_ERROR_KEY:";
}
