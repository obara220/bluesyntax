package com.panda.aoodds.esport.common.constant;

/**
 * 静态字段
 */
public class ConstantSystem {

    /** 标准赔率球头topic */
    public static final String STANDARD_AO_MARKET_ODDS = "STANDARD_AO_MARKET_ODDS";
    /** 三方球头赔率topic */
    public static final String THIRD_MARKET_BALL_HEAD = "THIRD_MARKET_BALL_HEAD";
    /** 赛前转滚球删除球头 */
    public static final String THIRD_MARKET_UP_STATUS = "THIRD_MARKET_UP_STATUS";
    public static final String THIRD_MARKET_BASKETBALL_HEAD = "THIRD_MARKET_BASKETBALL_HEAD";
    public static final String THIRD_MARKET_BASKETBALL_MAINLY_NOT_HEAD = "THIRD_MARKET_BASKETBALL_MAINLY_NOT_HEAD";
    /** 比分中心比分topic */
    public static final String STANDARD_MATCH_SCORES = "STANDARD_MATCH_SCORES";
    /** 特殊事件topic */
    public static final String RCS_DATA_AO_SPECIAL_EVENT_CONFIG = "RCS_DATA_AO_SPECIAL_EVENT_CONFIG";
    /** 入库AO赛事topic */
    public static final String AO_MATCH = "ao_match";
    /** 入库AO 电子赛事topic */
    public static final String AO_MATCH_VS = "ao_match_vs";
    /**足球apply参数Topic**/
    public static final String AO_APPLy_FOOTBALL_PARAM ="AO_APPLy_FOOTBALL_PARAM";
    /** 入库AO 范特西赛事topic */
    public static final String FTS_AO_MATCH = "FTS_AO_MATCH";
    /** 标准事件topic */
    public static final String MATCH_EVENT_INFO = "MATCH_EVENT_INFO";
    /** 事件源切换topic */
    public static final String LIVE_BUSINESS_EVENT_UPDATE_MESSAGE = "LIVE_BUSINESS_EVENT_UPDATE_MESSAGE";
    /**
     * 赛事回退，进球
     */
    public static final String MATCH_CANCEL_END = "MATCH_CANCEL_END";
    /**
     * 风控需要的赛事重推
     */
    public static final String RCS_CASH_OUT_MATCH_NOTIFY_DATA ="RCS_CASH_OUT_MATCH_NOTIFY_DATA";


    /**接收风控关盘通知topic**/
    public static final String RCS_MATCH_GOAL_TRADE_STATUS = "RCS_MATCH_GOAL_TRADE_STATUS";

    /** 清除赛事级别/玩法级别水差topic */
    public static final String AO_DIFF_CONFIG_CLEAR = "AO_DIFF_CONFIG_CLEAR";
    /** AO赛事模板参数topic */
    public static final String AO_DATA_REALTIME_CONFIG_TOPIC = "AO_DATA_REALTIME_CONFIG_TOPIC";
    /** 水差 margin 配置topic */
    public static final String AO_MATCH_DIFF_MARGIN_CONFIGS = "AO_MATCH_DIFF_MARGIN_CONFIGS";
    public static final String STANDARD_MATCH_SWITCH_STATUS = "STANDARD_MATCH_SWITCH_STATUS";
    /** 模板参数topic */
    public static final String TOURNAMENT_TEMPLATE_PLAY = "Tournament_Template_Play";
    /** 三方赛事取消关联topic */
    public static final String UNBIND_AOMATCH_DATA = "Unbind_AOMatch_Data";
    /** 小飞机和mango 预警topic */
    public static final String PA_SERVICE_WARN_INFO = "PA_SERVICE_WARN_INFO";
    /*****发送给风控开盘标识*****/
    public static final String RCS_MARKET_FOOTBALL_GOAL_STATUS = "RCS_MARKET_FOOTBALL_GOAL_STATUS";
    /**  AO赔率Topic */
    public static final String AO_MARKET_ODDS = "AO_MARKET_ODDS";
    /** margin/最大最小赔率 */
    public static final String MARKET_ITEM_MARGIN_CONFIG = "MARKET_ITEM_MARGIN_CONFIG";
    /** margin/最大最小赔率 */
    public static final String ESPORT_MARKET_ITEM_MARGIN_CONFIG = "ESPORT_MARKET_ITEM_MARGIN_CONFIG";
    /** 马来转欧赔 、欧赔转马来 配置对应表 */
    public static final String EUROPE_CONVERT_MALAY = "europe_convert_malay";
    public static final String MALAY_CONVERT_EUROPE = "malay_convert_europe";
    /** 赛程通知AO标准赛事对应AO赛事ID */
    public static final String STANDARD_AO_MATCH_SELL = "STANDARD_AO_MATCH_SELL";
    /** 通知风控同步模板 */
    public static final String STANDARD_MATCH_TEMPLATE = "STANDARD_MATCH_TEMPLATE";
    //自动操盘异常赔率赛事
    public static final String AO_PRE_REVERSE_MATCHS = "AO_PRE_REVERSE_MATCHS";

    /** 通知融合清除水差玩法集合 */
    public static final String STANDARD_CATEGORYID_CLEAR_DIFF = "STANDARD_CATEGORYID_CLEAR_DIFF";
    public static final String WARN_TGANDMANGO_MSG = "赛事ID:%s 数据源:%s 延迟:%s linkId:%s";
    /*******向风控发送日志消息*******/
    public static final String RCS_BUSINESS_LOG_SAVE = "rcs_business_log_save";




}
