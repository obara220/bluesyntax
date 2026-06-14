package com.panda.aoodds.esport.common.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author carson
 * @DATE 2022/3/10 14:45
 **/
public class RedisKeyConstant {
    public static final String EVENT_MATCH_KEY="EVENT_MATCH_KEY";
    public static final String AO_ESPORT_SCORE_KEY="AO_ESPORT_SCORE_KEY";
    public static final String AO_LASTTIME_MARKET_UPDATE="AO_LASTTIME_MARKET_UPDATE";
    public static final String AO_LASTTIME_ESPORT_MARKET_UPDATE="AO_LASTTIME_ESPORT_MARKET_UPDATE";
    public static final String AO_INJTIME_KEY="AO_INJTIME_KEY:";
    public static final String AO_ESPORT_BGTIME_KEY="AO_ESPORT_BGTIME_KEY:";
    public static final String AO_ESPORT_TIMER_PARAM="AO_ESPORT_TIMER_PARAM";
    public static final String AO_ESPORT_BK_BGTIME_KEY="AO_ESPORT_BK_BGTIME_KEY:";
    public static final String AO_ESPORT_FORMTIME_TO_LIVE="AO_ESPORT_FORMTIME_TO_LIVE";
    public static final String AO_TIMER_PARAM="AO_TIMER_PARAM";
    public static final String AO_IS_EVENT_TIME="AO_IS_EVENT_TIME";
    public static final String AO_DS_PD="AO_DS_PD";
    public static final String AO_SPECIALEVENT_SET="AO_SPECIALEVENT_SET";
    public static final String A01_MATCH_APPLY_INIT = "A01_MATCH_APPLY_INIT";
    public static final Long AO_BGTIME_KEY_TIME=4*60*60L;
    public static final Long AO_1DAYS_KEY_TIME=24*60*60L;
    public static final Long AO_7DAYS_KEY_TIME=24*60*60L*7;
    //calc赔率
    public static final String CALC_AO_MARKET_ODDS="CALC_AO_MARKET_ODDS:";
    //全局AO赛事级别开关  true 开 false 关
    public static final String AO_MATCH_SWITCH = "AO_MATCH_SWITCH";
    //AO赛事级别自动开盘  1 开 0 关
    public static final String AO_MATCH_AUTO_OPEN_MARKET = "AO_MATCH_AUTO_OPEN_MARKET";

    public static final String AO_MATCH_AUTO_GOAL_OPEN_MARKET = "AO_MATCH_AUTO_GOAL_OPEN_MARKET";
    //全局AO赛事赛种级别开关  1 开 0 关
    public static final String AO_MATCH_SPORT_SWITCH = "AO_MATCH_SPORT_SWITCH";
    //Mongo是否告警KEY
    public static final String MANGO_WARNING = "MANGO_WARNING";
    //赛事盘口缓存
    public static final String AO_ESPORT_MATCH_INFO = "ao_esport_match_info:";
    public static final String AO_ESPORT_MATCH_MARKET_ODDS = "ao_esport_match_market_odds:";

    public static final String AO_ESPORT_BK_MATCH_INFO = "ao_esport_bk_match_info:";
    public static final String AO_ESPORT_BK_MATCH_MARKET_ODDS = "ao_esport_bk_match_market_odds:";
    //赛事级别margin维持
    public static final String AO_MATCH_MAINTENANCE_RATE= "ao_match_maintenance_rate:";
    //玩法带{X}
    public static final String AO_MATCH_X_MARKET_ODDS= "ao_match_x_market_odds:";
    //特殊玩法带{X}
    public static final String AO_MATCH_SPECIAL_X_MARKET_ODDS= "ao_match_special_x_market_odds:";
    //点球大战让球
    public static final String AO_MATCH_SHOOTOUT_MARKET_ODDS= "ao_match_shootout_market_odds:";
    //带x玩法 最新 和 缓存对比
    public static final String AO_MATCH_CORNER_THREE_MARKET_ODDS= "ao_match_corner_three_market_odds:";
    //15分钟盘口缓存
    public static final String AO_MATCH_15MIN_MARKET_ODDS = "ao_match_15_min_market_odds:";
    //ao赛事锁
    public static final String MATCH_LOCK = "esport_match_lock:";
    public static final String AO_REDIS_QUEUE_ESPORT_ODDSTIMER="AO_REDIS_QUEUE_ESPORT_ODDSTIMER";
    //ao三方球头赛事锁
    public static final String MATCH_THIRD_MARKET_LOCK = "match_third_market_lock:";
    public static final String AO_ESPORT_MACHINE="AO_ESPORT_MACHINE";
    //ao三方球头次要玩法
    public static final String MATCH_THIRD_MARKET_MAINLY_NOT = "match_third_market_mainly_not:";
    //融合下发赛事live状态
    public static final String AO_ESPORT_MATCH_IN_LIVE = "ao_esport_match_in_live:";
    //赛事正常事件盘口缓存
    public static final String AO_EVENT_MATCH_MARKET_ODDS = "ao_event_match_market_odds:";
    //篮球 首先获得{X}分 所有盘口缓存
    public static final String AO_MATCH_RACE_TO_X_POINTS = "ao_match_race_to_x_points:";
    //篮球 首先获得{X}分 最新盘口缓存
    public static final String AO_MATCH_RACE_TO_X_POINTS_NEW = "ao_match_race_to_x_points_new:";
    public static final String AO_MATCH_APPLY_FROM_TIME = "ao_match_apply_from_time";
    //滚球第一次参数缓存
    public static final String APPLY_INPLAY_FIRST_PARAM = "apply_inplay_first_param";
    //缓存波胆赔率
    public static final String CORRECT_SCORE_MARKET = "CORRECT_SCORE_MARKET:";
    //记录一键相关操作总条数
    public static final String ONEKEY_APPKY_TOTAL_NUM = "ONEKEY_APPKY_TOTAL_NUM";
    //记录篮球一键相关操作总条数
    public static final String ONEKEY_BK_APPKY_TOTAL_NUM = "ONEKEY_BK_APPKY_TOTAL_NUM";
    //apply 清除水差开关
    public static final String APPLY_SWITCH_DIFF= "APPLY_SWITCH_DIFF:";
    //赔率刷新频率
    public static final String ESPORT_REFRESH_TIME= "ESPORT_REFRESH_TIME";
    public static final String ESPORT_TEMPLATE_REFRESH_TIME= "AO_ODDS_REFRESH_TIME::";
    //大小盘球头值缓存
    public static final String  AO_ESPORT_MATCH_HAND_ICAP = "AO_ESPORT_MATCH_HAND_TIME:";
    public static final String  ESPORT_MATCH_MARKET_CONFIG_CACHE = "ESPORT_MATCH_MARKET_CONFIG_CACHE:";
    public static final String  ESPORT_MATCH_MARKETTEMPLATE_CONFIG_CACHE = "ESPORT_MATCH_MARKETTEMPLATE_CONFIG_CACHE:";
    public static final String  ESPORT_MATCH_PRE_TEAM_CACHE = "ESPORT_MATCH_PRE_TEAM_CACHE:";
    public static final String MATCH_TEMPLATE_LINER_MARGIN_CONFIG_CACHE="MATCH_TEMPLATE_LINER_MARGIN_CONFIG_CACHE:";
    //赛事限频
    public static final String AO_ESPORT_MATCH_NO_ODDS_ISSUED = "AO_ESPORT_MATCH_NO_ODDS_ISSUED:";
    //滚球最近一次成功下发赔率时间
    public static final String AO_ESPORT_MARKET_LAST_PUSH_TIME = "AO_ESPORT_MARKET_LAST_PUSH_TIME";
    //赔率延迟兜底关盘/告警去重
    public static final String AO_ESPORT_ODDS_DELAY_HANDLED = "AO_ESPORT_ODDS_DELAY_HANDLED:";
    //赔率延迟兜底关盘后禁止定时器自动开盘
    public static final String AO_ESPORT_ODDS_DELAY_CLOSED = "AO_ESPORT_ODDS_DELAY_CLOSED:";
    public static Set<String> redisHashKey=new HashSet<>(5000);

    public static Set<String> getRedisHashKey() {
        return redisHashKey;
    }

    public static void setRedisHashKey(Set<String> redisHashKey) {
        RedisKeyConstant.redisHashKey = redisHashKey;
    }
}
