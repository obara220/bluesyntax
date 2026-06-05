package com.panda.aoodds.esport.common.market;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 投注项枚举
 *
 * @author Samuel
 */
@Getter
public enum MarketSelection {

    /**
     * 独赢主胜
     */
    WINNER_HOME("1", "{competitor1}", 1),
    /**
     * 独赢和局
     */
    WINNER_DRAW("2", "draw", 3),
    /**
     * 独赢客胜
     */
    WINNER_AWAY("3", "{competitor2}", 2),
    /**
     * 大
     */
    OVER("4", "over", 1),
    /**
     * 小
     */
    UNDER("5", "under", 2),
    /**
     * 让球主
     */
    AH_HOME("6", "{competitor1}", 1),
    /**
     * 让球客
     */
    AH_AWAY("7", "{competitor2}", 2),
    /**
     * 主胜、和局
     */
    HOME_DRAW("8", "{competitor1} or draw", 1),
    /**
     * 主胜、客胜
     */
    HOME_AWAY("9", "{competitor1} or {competitor2}", 2),
    /**
     * 客胜、和局
     */
    AWAY_DRAW("10", "{competitor2} or draw", 3),
    /**
     * 奇
     */
    ODD("11", "odd", 1),
    /**
     * 偶
     */
    EVEN("12", "even", 2),
    /**
     * 是
     */
    YES("13", "yes", 1),
    /**
     * 否
     */
    NO("14", "no", 2),
    /**
     * 主队
     */
    COMPETITOR1("15", "{competitor1}", 1),
    /**
     * 没有
     */
    NONE("16", "none", 2),
    /**
     * 客队
     */
    COMPETITOR2("17", "{competitor2}", 3),
    /**
     * 上半场
     */
    HALF1("18", "1st half", 1),
    /**
     * 下半场
     */
    HALF2("19", "2nd half", 2),
    /**
     * 相等
     */
    EQUAL("20", "equal", 3),
    /**
     * 两个半场均为进球
     */
    HALF_NO_GOAL("21", "no goal", 4),

    /**
     * 正确比分投注项
     */
    SCORE_0_0("101", "0-0", 1),
    SCORE_0_1("102", "0-1", 2),
    SCORE_0_2("103", "0-2", 3),
    SCORE_0_3("104", "0-3", 4),
    SCORE_0_4("105", "0-4", 5),
    SCORE_1_0("106", "1-0", 6),
    SCORE_1_1("107", "1-1", 7),
    SCORE_1_2("108", "1-2", 8),
    SCORE_1_3("109", "1-3", 9),
    SCORE_1_4("110", "1-4", 10),
    SCORE_2_0("111", "2-0", 11),
    SCORE_2_1("112", "2-1", 12),
    SCORE_2_2("113", "2-2", 13),
    SCORE_2_3("114", "2-3", 14),
    SCORE_2_4("115", "2-4", 15),
    SCORE_3_0("116", "3-0", 16),
    SCORE_3_1("117", "3-1", 17),
    SCORE_3_2("118", "3-2", 18),
    SCORE_3_3("119", "3-3", 19),
    SCORE_3_4("120", "3-4", 20),
    SCORE_4_0("121", "4-0", 21),
    SCORE_4_1("122", "4-1", 22),
    SCORE_4_2("123", "4-2", 23),
    SCORE_4_3("124", "4-3", 24),
    SCORE_4_4("125", "4-4", 25),
    SCORE_OTHER("126", "other", 26),
    /**
     * 净胜分投注项
     */
    HOME_WIN_1("127", "{competitor1} win by 1", 1),
    HOME_WIN_2("128", "{competitor1} win by 2", 2),
    HOME_WIN_3("129", "{competitor1} win by 3", 3),
    HOME_WIN_4_PLUS("130", "{competitor1} win by 4+", 6),
    AWAY_WIN_1("131", "{competitor2} win by 1", 7),
    AWAY_WIN_2("132", "{competitor2} win by 2", 8),
    AWAY_WIN_3("133", "{competitor2} win by 3", 9),
    AWAY_WIN_4_PLUS("134", "{competitor2} win by 4+", 12),
    SCORE_DRAW("135", "score draw", 12),
    NO_GOAL("136", "no goal", 14),
    HOME_WIN_3_PLUS("137", "{competitor1} win by 3+", 5),
    AWAY_WIN_3_PLUS("138", "{competitor2} win by 3+", 11),
    DRAW("139", "draw", 15),
    HOME_WIN_2_PLUS("367", "{competitor1} win by 2+", 4),
    AWAY_WIN_2_PLUS("368", "{competitor2} win by 2+", 10),
    /**
     * 准确进球数投注项
     */
    EXACT_0("140", "0", 1),
    EXACT_1("141", "1", 2),
    EXACT_2("142", "2", 3),
    EXACT_3("143", "3", 4),
    EXACT_4("144", "4", 5),
    EXACT_5("145", "5", 6),
    EXACT_3_PLUS("146", "3+", 7),
    EXACT_4_PLUS("147", "4+", 8),
    EXACT_6_PLUS("148", "6+", 9),
    /**
     * 独赢并且大小 投注项
     */
    HOME_AND_OVER("149", "{competitor1} & over {total}", 1),
    HOME_AND_UNDER("150", "{competitor1} & under {total}", 2),
    DRAW_AND_OVER("151", "draw & over {total}", 3),
    DRAW_AND_UNDER("152", "draw & under {total}", 4),
    AWAY_AND_OVER("153", "{competitor2} & over {total}", 5),
    AWAY_AND_UNDER("154", "{competitor2} & under {total}", 6),
    /**
     * 进球数区间
     */
    RANGE_0_1("155", "0-1", 1),
    RANGE_2_3("156", "2-3", 2),
    RANGE_4_6("157", "4-6", 3),
    RANGE_7_PLUS("158", "7+", 4),
    /**
     * 独赢并且两队都进球 投注项
     */
    HOME_AND_YES("159", "{competitor1} & yes", 1),
    HOME_AND_NO("160", "{competitor1} & no", 2),
    DRAW_AND_YES("161", "draw & yes", 3),
    DRAW_AND_NO("162", "draw & no", 4),
    AWAY_AND_YES("163", "{competitor2} & yes", 5),
    AWAY_AND_NO("164", "{competitor2} & no", 6),
    /**
     * 大小并且两队都进球 投注项
     */
    OVER_AND_YES("165", "over {total} & yes", 1),
    OVER_AND_NO("166", "over {total} & no", 2),
    UNDER_AND_YES("167", "under {total} & yes", 3),
    UNDER_AND_NO("168", "under {total} & no", 4),
    /**
     * 半场、全场比分 投注项
     */
    SCORE_0_0_0_0("169", "0-0 0-0", 1),
    SCORE_0_0_0_1("170", "0-0 0-1", 2),
    SCORE_0_0_0_2("171", "0-0 0-2", 3),
    SCORE_0_0_0_3("172", "0-0 0-3", 4),
    SCORE_0_0_1_0("173", "0-0 1-0", 5),
    SCORE_0_0_1_1("174", "0-0 1-1", 6),
    SCORE_0_0_1_2("175", "0-0 1-2", 7),
    SCORE_0_0_2_0("176", "0-0 2-0", 8),
    SCORE_0_0_2_1("177", "0-0 2-1", 9),
    SCORE_0_0_3_0("178", "0-0 3-0", 10),
    SCORE_0_0_5_PLUS("179", "0-0 5+", 11),
    SCORE_0_1_0_1("180", "0-1 0-1", 12),
    SCORE_0_1_0_2("181", "0-1 0-2", 13),
    SCORE_0_1_0_3("182", "0-1 0-3", 14),
    SCORE_0_1_1_1("183", "0-1 1-1", 15),
    SCORE_0_1_1_2("184", "0-1 1-2", 16),
    SCORE_0_1_2_1("185", "0-1 2-1", 17),
    SCORE_0_1_5_PLUS("186", "0-1 5+", 18),
    SCORE_0_2_0_2("187", "0-2 0-2", 19),
    SCORE_0_2_0_3("188", "0-2 0-3", 20),
    SCORE_0_2_1_2("189", "0-2 1-2", 21),
    SCORE_0_2_5_PLUS("190", "0-2 5+", 22),
    SCORE_0_3_0_3("191", "0-3 0-3", 23),
    SCORE_0_3_5_PLUS("192", "0-3 5+", 24),
    SCORE_1_0_1_0("193", "1-0 1-0", 25),
    SCORE_1_0_1_1("194", "1-0 1-1", 26),
    SCORE_1_0_1_2("195", "1-0 1-2", 27),
    SCORE_1_0_2_0("196", "1-0 2-0", 28),
    SCORE_1_0_2_1("197", "1-0 2-1", 29),
    SCORE_1_0_3_0("198", "1-0 3-0", 30),
    SCORE_1_0_5_PLUS("199", "1-0 5+", 31),
    SCORE_1_1_1_1("200", "1-1 1-1", 32),
    SCORE_1_1_1_2("201", "1-1 1-2", 33),
    SCORE_1_1_2_1("202", "1-1 2-1", 34),
    SCORE_1_1_5_PLUS("203", "1-1 5+", 35),
    SCORE_1_2_1_2("204", "1-2 1-2", 36),
    SCORE_1_2_5_PLUS("205", "1-2 5+", 37),
    SCORE_2_0_2_0("206", "2-0 2-0", 38),
    SCORE_2_0_2_1("207", "2-0 2-1", 39),
    SCORE_2_0_3_0("208", "2-0 3-0", 40),
    SCORE_2_0_5_PLUS("209", "2-0 5+", 41),
    SCORE_2_1_2_1("210", "2-1 2-1", 42),
    SCORE_2_1_5_PLUS("211", "2-1 5+", 43),
    SCORE_3_0_3_0("212", "3-0 3-0", 44),
    SCORE_3_0_5_PLUS("213", "3-0 5+", 45),
    SCORE_4_PLUS_5_PLUS("214", "4+ 5+", 46),
    /**
     * 半场、全场 投注项
     */
    WINNER_HOME_HOME("215", "{$competitor1}/{$competitor1}", 1),
    WINNER_HOME_DRAW("216", "{$competitor1}/draw", 2),
    WINNER_HOME_AWAY("217", "{$competitor1}/{$competitor2}", 3),
    WINNER_DRAW_HOME("218", "draw/{$competitor1}", 4),
    WINNER_DRAW_DRAW("219", "draw/draw", 5),
    WINNER_DRAW_AWAY("220", "draw/{$competitor2}", 6),
    WINNER_AWAY_HOME("221", "{$competitor2}/{$competitor1}", 7),
    WINNER_AWAY_DRAW("222", "{$competitor2}/draw", 8),
    WINNER_AWAY_AWAY("223", "{$competitor2}/{$competitor2}", 9),
    /**
     * 双重机会并且两队都进球 投注项
     */
    HOME_DRAW_AND_YES("224", "{competitor1}/draw & yes", 1),
    HOME_DRAW_AND_NO("225", "{competitor1}/draw & no", 2),
    HOME_AWAY_AND_YES("226", "{competitor1}/{competitor2} & yes", 3),
    HOME_AWAY_AND_NO("227", "{competitor1}/{competitor2} & no", 4),
    AWAY_DRAW_AND_YES("228", "draw/{competitor2} & yes", 5),
    AWAY_DRAW_AND_NO("229", "draw/{competitor2} & no", 6),
    /**
     * 上/下半场两队都进球 投注项
     */
    NO_NO("230", "no/no", 1),
    YES_NO("231", "yes/no", 2),
    YES_YES("232", "yes/yes", 3),
    NO_YES("233", "no/yes", 4),
    /**
     * 进球时间区间 投注项
     */
    INTERVAL_1_15("234", "1-15", 1),
    INTERVAL_16_30("235", "16-30", 2),
    INTERVAL_31_45("236", "31-45", 3),
    INTERVAL_46_60("237", "46-60", 4),
    INTERVAL_61_75("238", "61-75", 5),
    INTERVAL_76_90("239", "76-90", 6),
    INTERVAL_NONE("240", "none", 7),
    /**
     * 主队获胜退款 投注项
     */
    HOME_NO_BET_DRAW("241", "draw", 1),
    HOME_NO_BET_AWAY("242", "{competitor2}", 2),
    /**
     * 客队获胜退款 投注项
     */
    AWAY_NO_BET_HOME("243", "{competitor1}", 1),
    AWAY_NO_BET_DRAW("244", "draw", 2),
    /**
     * 角球总数区间
     */
    CORNER_RANGE_0_2("245", "0-2", 1),
    CORNER_RANGE_3_4("246", "3-4", 2),
    CORNER_RANGE_0_4("247", "0-4", 3),
    CORNER_RANGE_5_6("248", "5-6", 4),
    CORNER_RANGE_7_PLUS("249", "7+", 5),
    CORNER_RANGE_0_8("250", "0-8", 6),
    CORNER_RANGE_9_11("251", "9-11", 7),
    CORNER_RANGE_12_PLUS("252", "12+", 8),
    /**
     * 准确罚牌数
     */
    BOOKING_EXACT_0("253", "0", 1),
    BOOKING_EXACT_0_1("254", "0-1", 2),
    BOOKING_EXACT_0_3("255", "0-3", 3),
    BOOKING_EXACT_1("256", "1", 4),
    BOOKING_EXACT_2("257", "2", 5),
    BOOKING_EXACT_3("258", "3", 6),
    BOOKING_EXACT_4("259", "4", 7),
    BOOKING_EXACT_5("260", "5", 8),
    BOOKING_EXACT_6("261", "6", 9),
    BOOKING_EXACT_7("262", "7", 10),
    BOOKING_EXACT_8("263", "8", 11),
    BOOKING_EXACT_9("264", "9", 12),
    BOOKING_EXACT_10("265", "10", 13),
    BOOKING_EXACT_11("266", "11", 14),
    BOOKING_EXACT_3_PLUS("267", "3+", 15),
    BOOKING_EXACT_4_PLUS("268", "4+", 16),
    BOOKING_EXACT_6_PLUS("269", "6+", 17),
    BOOKING_EXACT_12_PLUS("270", "12+", 18),
    /**
     * 加时赛正确比分
     */
    OT_SCORE_0_0("271", "0-0", 1),
    OT_SCORE_1_0("272", "1-0", 2),
    OT_SCORE_2_0("273", "2-0", 3),
    OT_SCORE_3_0("274", "3-0", 4),
    OT_SCORE_0_1("275", "0-1", 5),
    OT_SCORE_1_1("276", "1-1", 6),
    OT_SCORE_2_1("277", "2-1", 7),
    OT_SCORE_0_2("278", "0-2", 8),
    OT_SCORE_1_2("279", "1-2", 9),
    OT_SCORE_0_3("280", "0-3", 10),
    /**
     * 点球大战正确比分
     */
    PEN_SO_SCORE_0_1("281", "0-1", 1),
    PEN_SO_SCORE_0_2("282", "0-2", 2),
    PEN_SO_SCORE_0_3("283", "0-3", 3),
    PEN_SO_SCORE_1_0("284", "1-0", 4),
    PEN_SO_SCORE_1_2("285", "1-2", 5),
    PEN_SO_SCORE_1_3("286", "1-3", 6),
    PEN_SO_SCORE_1_4("287", "1-4", 7),
    PEN_SO_SCORE_2_0("288", "2-0", 8),
    PEN_SO_SCORE_2_1("289", "2-1", 9),
    PEN_SO_SCORE_2_3("290", "2-3", 10),
    PEN_SO_SCORE_2_4("291", "2-4", 11),
    PEN_SO_SCORE_3_0("292", "3-0", 12),
    PEN_SO_SCORE_3_1("293", "3-1", 13),
    PEN_SO_SCORE_3_2("294", "3-2", 14),
    PEN_SO_SCORE_3_4("295", "3-4", 15),
    PEN_SO_SCORE_3_5("296", "3-5", 16),
    PEN_SO_SCORE_4_1("297", "4-1", 17),
    PEN_SO_SCORE_4_2("298", "4-2", 18),
    PEN_SO_SCORE_4_3("299", "4-3", 19),
    PEN_SO_SCORE_4_5("300", "4-5", 20),
    PEN_SO_SCORE_5_3("301", "5-3", 21),
    PEN_SO_SCORE_5_4("302", "5-4", 22),
    PEN_SO_SCORE_OTHER("303", "other", 23),
    /**
     * 点球大战准确进球数
     */
    PEN_SO_EXACT_0_4("304", "0-4", 1),
    PEN_SO_EXACT_5("305", "5", 2),
    PEN_SO_EXACT_6("306", "6", 3),
    PEN_SO_EXACT_7("307", "7", 4),
    PEN_SO_EXACT_8("308", "8", 5),
    PEN_SO_EXACT_9("309", "9", 6),
    PEN_SO_EXACT_10_PLUS("310", "10+", 7),
    /**
     * 获胜方式
     */
    HOME_BY_REGULAR_TIME("311", "{$competitor1} regular time", 1),
    AWAY_BY_REGULAR_TIME("312", "{$competitor2} regular time", 2),
    HOME_BY_OVER_TIME("313", "{$competitor1} overtime", 3),
    AWAY_BY_OVER_TIME("314", "{$competitor2} overtime", 4),
    HOME_BY_PENALTY("315", "{$competitor1} penalties", 5),
    AWAY_BY_PENALTY("316", "{$competitor2} penalties", 6),
    /**
     * 全场正确比分（多重投注）
     */
    SCORE_MULTI_BET_HOME_1("317", "1-0/2-0/3-0", 1),
    SCORE_MULTI_BET_HOME_2("318", "4-0/5-0/6-0", 2),
    SCORE_MULTI_BET_HOME_3("319", "2-1/3-1/4-1", 3),
    SCORE_MULTI_BET_HOME_4("320", "3-2/4-2/4-3/5-1", 4),
    SCORE_MULTI_BET_HOME_OTHER("321", "HomeOther", 5),
    SCORE_MULTI_BET_DRAW_OTHER("322", "DrawOther", 6),
    SCORE_MULTI_BET_AWAY_1("323", "0-1/0-2/0-3", 7),
    SCORE_MULTI_BET_AWAY_2("324", "0-4/0-5/0-6", 8),
    SCORE_MULTI_BET_AWAY_3("325", "1-2/1-3/1-4", 9),
    SCORE_MULTI_BET_AWAY_4("326", "2-3/2-4/3-4/1-5", 10),
    SCORE_MULTI_BET_AWAY_OTHER("327", "AwayOther", 11),
    /**
     * 双重机会 & 进球大小
     */
    HOME_DRAW_AND_OVER("328", "{competitor1}/draw & over {total}", 1),
    HOME_DRAW_AND_UNDER("329", "{competitor1}/draw & under {total}", 2),
    HOME_AWAY_AND_OVER("330", "{competitor1}/{competitor2} & over {total}", 3),
    HOME_AWAY_AND_UNDER("331", "{competitor1}/{competitor2} & under {total}", 4),
    AWAY_DRAW_AND_OVER("332", "{competitor2}/draw & over {total}", 5),
    AWAY_DRAW_AND_UNDER("333", "{competitor2}/draw & under {total}", 6),
    /**
     * 半/全场 & 进球大小
     */
    HT_HOME_FT_HOME_AND_OVER("334", "{$competitor1}/{$competitor1} & over {total}", 1),
    HT_HOME_FT_HOME_AND_UNDER("335", "{$competitor1}/{$competitor1} & under {total}", 2),
    HT_HOME_FT_DRAW_AND_OVER("336", "{$competitor1}/draw & over {total}", 3),
    HT_HOME_FT_DRAW_AND_UNDER("337", "{$competitor1}/draw & under {total}", 4),
    HT_HOME_FT_AWAY_AND_OVER("338", "{$competitor1}/{$competitor2} & over {total}", 5),
    HT_HOME_FT_AWAY_AND_UNDER("339", "{$competitor1}/{$competitor2} & under {total}", 6),
    HT_DRAW_FT_HOME_AND_OVER("340", "draw/{$competitor1} & over {total}", 7),
    HT_DRAW_FT_HOME_AND_UNDER("341", "draw/{$competitor1} & under {total}", 8),
    HT_DRAW_FT_DRAW_AND_OVER("342", "draw/draw & over {total}", 9),
    HT_DRAW_FT_DRAW_AND_UNDER("343", "draw/draw & under {total}", 10),
    HT_DRAW_FT_AWAY_AND_OVER("344", "draw/{$competitor2} & over {total}", 11),
    HT_DRAW_FT_AWAY_AND_UNDER("345", "draw/{$competitor2} & under {total}", 12),
    HT_AWAY_FT_HOME_AND_OVER("346", "{$competitor2}/{$competitor1} & over {total}", 13),
    HT_AWAY_FT_HOME_AND_UNDER("347", "{$competitor2}/{$competitor1} & under {total}", 14),
    HT_AWAY_FT_DRAW_AND_OVER("348", "{$competitor2}/draw & over {total}", 15),
    HT_AWAY_FT_DRAW_AND_UNDER("349", "{$competitor2}/draw & under {total}", 16),
    HT_AWAY_FT_AWAY_AND_OVER("350", "{$competitor2}/{$competitor2} & over {total}", 17),
    HT_AWAY_FT_AWAY_AND_UNDER("351", "{$competitor2}/{$competitor2} & under {total}", 18),
    /**
     * 半/全场 单双
     */
    HT_ODD_FT_ODD("352", "odd/odd", 1),
    HT_ODD_FT_EVEN("353", "odd/even", 2),
    HT_EVEN_FT_ODD("354", "even/odd", 3),
    HT_EVEN_FT_EVEN("355", "even/even", 4),
    /**
     * 进球单双 & 进球大小
     */
    ODD_AND_OVER("356", "odd & over {total}", 1),
    ODD_AND_UNDER("357", "odd & under {total}", 2),
    EVEN_AND_OVER("358", "even & over {total}", 3),
    EVEN_AND_UNDER("359", "even & under {total}", 4),
    /**
     * 独赢 & 最先进球球队
     */
    HOME_AND_HOME("360", "{$competitor1} & {$competitor1}", 1),
    HOME_AND_AWAY("361", "{$competitor1} & {$competitor2}", 2),
    DRAW_AND_HOME("362", "draw & {$competitor1}", 3),
    DRAW_AND_AWAY("363", "draw & {$competitor2}", 4),
    AWAY_AND_HOME("364", "{$competitor2} & {$competitor1}", 5),
    AWAY_AND_AWAY("365", "{$competitor2} & {$competitor2}", 6),
    DRAW_AND_NO_GOAL("366", "No Goal", 7),
    /**
     * 半/全场 & 准确进球数
     */
    HT_HOME_FT_HOME_AND_EXACT_0("369", "{$competitor1}/{$competitor1} & 0", 1),
    HT_HOME_FT_HOME_AND_EXACT_1("370", "{$competitor1}/{$competitor1} & 1", 2),
    HT_HOME_FT_HOME_AND_EXACT_2("371", "{$competitor1}/{$competitor1} & 2", 3),
    HT_HOME_FT_HOME_AND_EXACT_3("372", "{$competitor1}/{$competitor1} & 3", 4),
    HT_HOME_FT_HOME_AND_EXACT_4("373", "{$competitor1}/{$competitor1} & 4", 5),
    HT_HOME_FT_HOME_AND_EXACT_5_PLUS("374", "{$competitor1}/{$competitor1} & 5+", 6),
    HT_HOME_FT_DRAW_AND_EXACT_0("375", "{$competitor1}/draw & 0", 7),
    HT_HOME_FT_DRAW_AND_EXACT_1("376", "{$competitor1}/draw & 1", 8),
    HT_HOME_FT_DRAW_AND_EXACT_2("377", "{$competitor1}/draw & 2", 9),
    HT_HOME_FT_DRAW_AND_EXACT_3("378", "{$competitor1}/draw & 3", 10),
    HT_HOME_FT_DRAW_AND_EXACT_4("379", "{$competitor1}/draw & 4", 11),
    HT_HOME_FT_DRAW_AND_EXACT_5_PLUS("380", "{$competitor1}/draw & 5+", 12),
    HT_HOME_FT_AWAY_AND_EXACT_0("381", "{$competitor1}/{$competitor2} & 0", 13),
    HT_HOME_FT_AWAY_AND_EXACT_1("382", "{$competitor1}/{$competitor2} & 1", 14),
    HT_HOME_FT_AWAY_AND_EXACT_2("383", "{$competitor1}/{$competitor2} & 2", 15),
    HT_HOME_FT_AWAY_AND_EXACT_3("384", "{$competitor1}/{$competitor2} & 3", 16),
    HT_HOME_FT_AWAY_AND_EXACT_4("385", "{$competitor1}/{$competitor2} & 4", 17),
    HT_HOME_FT_AWAY_AND_EXACT_5_PLUS("386", "{$competitor1}/{$competitor2} & 5+", 18),
    HT_DRAW_FT_HOME_AND_EXACT_0("387", "draw/{$competitor1} & 0", 19),
    HT_DRAW_FT_HOME_AND_EXACT_1("388", "draw/{$competitor1} & 1", 20),
    HT_DRAW_FT_HOME_AND_EXACT_2("389", "draw/{$competitor1} & 2", 21),
    HT_DRAW_FT_HOME_AND_EXACT_3("390", "draw/{$competitor1} & 3", 22),
    HT_DRAW_FT_HOME_AND_EXACT_4("391", "draw/{$competitor1} & 4", 23),
    HT_DRAW_FT_HOME_AND_EXACT_5_PLUS("392", "draw/{$competitor1} & 5+", 24),
    HT_DRAW_FT_DRAW_AND_EXACT_0("393", "draw/draw & 0", 25),
    HT_DRAW_FT_DRAW_AND_EXACT_1("394", "draw/draw & 1", 26),
    HT_DRAW_FT_DRAW_AND_EXACT_2("395", "draw/draw & 2", 27),
    HT_DRAW_FT_DRAW_AND_EXACT_3("396", "draw/draw & 3", 28),
    HT_DRAW_FT_DRAW_AND_EXACT_4("397", "draw/draw & 4", 29),
    HT_DRAW_FT_DRAW_AND_EXACT_5_PLUS("398", "draw/draw & 5+", 30),
    HT_DRAW_FT_AWAY_AND_EXACT_0("399", "draw/{$competitor2} & 0", 31),
    HT_DRAW_FT_AWAY_AND_EXACT_1("400", "draw/{$competitor2} & 1", 32),
    HT_DRAW_FT_AWAY_AND_EXACT_2("401", "draw/{$competitor2} & 2", 33),
    HT_DRAW_FT_AWAY_AND_EXACT_3("402", "draw/{$competitor2} & 3", 34),
    HT_DRAW_FT_AWAY_AND_EXACT_4("403", "draw/{$competitor2} & 4", 35),
    HT_DRAW_FT_AWAY_AND_EXACT_5_PLUS("404", "draw/{$competitor2} & 5+", 36),
    HT_AWAY_FT_HOME_AND_EXACT_0("405", "{$competitor2}/{$competitor1} & 0", 37),
    HT_AWAY_FT_HOME_AND_EXACT_1("406", "{$competitor2}/{$competitor1} & 1", 38),
    HT_AWAY_FT_HOME_AND_EXACT_2("407", "{$competitor2}/{$competitor1} & 2", 39),
    HT_AWAY_FT_HOME_AND_EXACT_3("408", "{$competitor2}/{$competitor1} & 3", 40),
    HT_AWAY_FT_HOME_AND_EXACT_4("409", "{$competitor2}/{$competitor1} & 4", 41),
    HT_AWAY_FT_HOME_AND_EXACT_5_PLUS("410", "{$competitor2}/{$competitor1} & 5+", 42),
    HT_AWAY_FT_DRAW_AND_EXACT_0("411", "{$competitor2}/draw & 0", 43),
    HT_AWAY_FT_DRAW_AND_EXACT_1("412", "{$competitor2}/draw & 1", 44),
    HT_AWAY_FT_DRAW_AND_EXACT_2("413", "{$competitor2}/draw & 2", 45),
    HT_AWAY_FT_DRAW_AND_EXACT_3("414", "{$competitor2}/draw & 3", 46),
    HT_AWAY_FT_DRAW_AND_EXACT_4("415", "{$competitor2}/draw & 4", 47),
    HT_AWAY_FT_DRAW_AND_EXACT_5_PLUS("416", "{$competitor2}/draw & 5+", 48),
    HT_AWAY_FT_AWAY_AND_EXACT_0("417", "{$competitor2}/{$competitor2} & 0", 49),
    HT_AWAY_FT_AWAY_AND_EXACT_1("418", "{$competitor2}/{$competitor2} & 1", 50),
    HT_AWAY_FT_AWAY_AND_EXACT_2("419", "{$competitor2}/{$competitor2} & 2", 51),
    HT_AWAY_FT_AWAY_AND_EXACT_3("420", "{$competitor2}/{$competitor2} & 3", 52),
    HT_AWAY_FT_AWAY_AND_EXACT_4("421", "{$competitor2}/{$competitor2} & 4", 53),
    HT_AWAY_FT_AWAY_AND_EXACT_5_PLUS("422", "{$competitor2}/{$competitor2} & 5+", 54),
    /**
     * 进球时间区间（5分钟区间） 投注项
     */
    INTERVAL_1_5("423", "1H Start-4:59", 1),
    INTERVAL_6_10("424", "5:00-9:59", 2),
    INTERVAL_11_15("425", "10:00-14:59", 3),
    INTERVAL_16_20("426", "15:00-19:59", 4),
    INTERVAL_21_25("427", "20:00-24:59", 5),
    INTERVAL_26_30("428", "25:00-29:59", 6),
    INTERVAL_31_35("429", "30:00-34:59", 7),
    INTERVAL_36_40("430", "35:00-39:59", 8),
    INTERVAL_41_45("431", "40:00-45:00(excluded inj.)", 9),
    INTERVAL_46_50("432", "2H Start-49:59", 10),
    INTERVAL_51_55("433", "50:00-54:59", 11),
    INTERVAL_56_60("434", "55:00-59:59", 12),
    INTERVAL_61_65("435", "60:00-64:59", 13),
    INTERVAL_66_70("436", "65:00-69:59", 14),
    INTERVAL_71_75("437", "70:00-74:59", 15),
    INTERVAL_76_80("438", "75:00-79:59", 16),
    INTERVAL_81_85("439", "80:00-84:59", 17),
    INTERVAL_86_90("440", "85:00-90:00(excluded inj.)", 18),
    INTERVAL_NO_GOAL("441", "No goal", 19),
    INTERVAL_CLUTCH("442", "clutch goal(inj.)", 20),
    /**
     * 篮球净胜分 投注项
     */
    BASKET_HOME_WIN_3_PLUS("443", "{competitor1} win by 3+", 1),
    BASKET_HOME_WIN_6_PLUS("444", "{competitor1} win by 6+", 2),
    BASKET_AWAY_WIN_3_PLUS("445", "{competitor2} win by 3+", 3),
    BASKET_AWAY_WIN_6_PLUS("446", "{competitor2} win by 6+", 4),
    BASKET_OTHER("447", "Other", 5),

    BASKET_HOME_WIN_1_5("448", "{competitor1} win by 1-5", 1),
    BASKET_HOME_WIN_6_10("449", "{competitor1} win by 6-10", 2),
    BASKET_HOME_WIN_11_PLUS("450", "{competitor1} win by 11+", 3),
    BASKET_HOME_WIN_11_15("451", "{competitor1} win by 11-15", 4),
    BASKET_HOME_WIN_16_20("452", "{competitor1} win by 16-20", 5),
    BASKET_HOME_WIN_21_25("453", "{competitor1} win by 21-25", 6),
    BASKET_HOME_WIN_26_PLUS("454", "{competitor1} win by 26+", 7),
    BASKET_AWAY_WIN_1_5("455", "{competitor2} win by 1-5", 8),
    BASKET_AWAY_WIN_6_10("456", "{competitor2} win by 6-10", 9),
    BASKET_AWAY_WIN_11_PLUS("457", "{competitor2} win by 11+", 10),
    BASKET_AWAY_WIN_11_15("458", "{competitor2} win by 11-15", 11),
    BASKET_AWAY_WIN_16_20("459", "{competitor2} win by 16-20", 12),
    BASKET_AWAY_WIN_21_25("460", "{competitor2} win by 21-25", 13),
    BASKET_AWAY_WIN_26_PLUS("461", "{competitor2} win by 26+", 14),
    BASKET_DRAW("462", "draw", 15),

    BASKET_HOME_WIN_1_2("463", "{competitor1} win by 1-2", 1),
    BASKET_HOME_WIN_3_6("464", "{competitor1} win by 3-6", 2),
    BASKET_HOME_WIN_7_9("465", "{competitor1} win by 7-9", 3),
    BASKET_HOME_WIN_10_13("466", "{competitor1} win by 10-13", 4),
    BASKET_HOME_WIN_14_16("467", "{competitor1} win by 14-16", 5),
    BASKET_HOME_WIN_17_20("468", "{competitor1} win by 17-20", 6),
    BASKET_HOME_WIN_21_PLUS("469", "{competitor1} win by 21+", 7),
    BASKET_AWAY_WIN_1_2("470", "{competitor2} win by 1-2", 8),
    BASKET_AWAY_WIN_3_6("471", "{competitor2} win by 3-6", 9),
    BASKET_AWAY_WIN_7_9("472", "{competitor2} win by 7-9", 10),
    BASKET_AWAY_WIN_10_13("473", "{competitor2} win by 10-13", 11),
    BASKET_AWAY_WIN_14_16("474", "{competitor2} win by 14-16", 12),
    BASKET_AWAY_WIN_17_20("475", "{competitor2} win by 17-20", 13),
    BASKET_AWAY_WIN_21_PLUS("476", "{competitor2} win by 21+", 14),

    BASKET_WIN_1_5("477", "win by 1-5", 1),
    BASKET_WIN_6_10("478", "win by 6-10", 2),
    BASKET_WIN_11_15("479", "win by 11-15", 3),
    BASKET_WIN_16_20("480", "win by 16-20", 4),
    BASKET_WIN_21_25("481", "win by 21-25", 5),
    BASKET_WIN_26_30("482", "win by 26-30", 6),
    BASKET_WIN_31_PLUS("483", "win by 31+", 7),
    /**
     * 第一节
     */
    QUARTER1("484", "1st quarter", 1),
    /**
     * 第二节
     */
    QUARTER2("485", "2nd quarter", 2),
    /**
     * 第三节
     */
    QUARTER3("486", "3rd quarter", 3),
    /**
     * 第四节
     */
    QUARTER4("487", "4th quarter", 4),
    /**
     * 相等
     */
    BASKET_EQUALS("488", "equals", 5),

    /**
     * 独赢 & 单双 投注项
     */
    HOME_AND_ODD("489", "{competitor1} & odd", 1),
    HOME_AND_EVEN("490", "{competitor1} & even", 2),
    DRAW_AND_EVEN("491", "draw & even", 3),
    AWAY_AND_ODD("492", "{competitor2} & odd", 4),
    AWAY_AND_EVEN("493", "{competitor2} & even", 5),

    /**
     * 让分 & 大小 投注项
     */
    HDP1_AND_OVER("494", "{competitor1} & over {total}", 1),
    HDP1_AND_UNDER("495", "{competitor1} & under {total}", 2),
    HDP2_AND_OVER("496", "{competitor2} & over {total}", 3),
    HDP2_AND_UNDER("497", "{competitor2} & under {total}", 4),

    /**
     * 总分末位数 投注项
     */
    LAST_DIGIT_0("498", "0", 1),
    LAST_DIGIT_1("499", "1", 2),
    LAST_DIGIT_2("500", "2", 3),
    LAST_DIGIT_3("501", "3", 4),
    LAST_DIGIT_4("502", "4", 5),
    LAST_DIGIT_5("503", "5", 6),
    LAST_DIGIT_6("504", "6", 7),
    LAST_DIGIT_7("505", "7", 8),
    LAST_DIGIT_8("506", "8", 9),
    LAST_DIGIT_9("507", "9", 10),

    /**
     * 角球总数区间（补充） 投注项
     */
    CORNER_RANGE_5_PLUS("508", "5+", 9),

    /**
     * 点球大战第几轮结束
     */
    PEN_SO_ROUND_3("529", "3", 1),
    PEN_SO_ROUND_4("530", "4", 2),
    PEN_SO_ROUND_5("531", "5", 3),
    PEN_SO_ROUND_SD("532", "Sudden Death", 4),

    /**
     * 进球大小 & 首次进球队伍
     */
    OVER_AND_HOME("533", "over {total} & {$competitor1}", 1),
    OVER_AND_AWAY("534", "over {total} & {$competitor2}", 2),
    UNDER_AND_HOME("535", "under {total} & {$competitor1}", 3),
    UNDER_AND_AWAY("536", "under {total} & {$competitor2}", 4),
    UNDER_AND_NONE("537", "None", 5),

    /**
     * 双重机会 & 首次进球队伍
     */
    HOME_DRAW_AND_HOME("540", "{competitor1}/draw & {$competitor1}", 1),
    HOME_DRAW_AND_AWAY("541", "{competitor1}/draw & {$competitor2}", 2),
    HOME_AWAY_AND_HOME("542", "{competitor1}/{competitor2} & {$competitor1}", 3),
    HOME_AWAY_AND_AWAY("543", "{competitor1}/{competitor2} & {$competitor2}", 4),
    AWAY_DRAW_AND_HOME("544", "draw/{competitor2} & {$competitor1}", 5),
    AWAY_DRAW_AND_AWAY("545", "draw/{competitor2} & {$competitor2}", 6),
    DC_DRAW_AND_NONE("546", "None", 7),

    /**
     * 独赢 或 任何零失球
     */
    HOME_OR_YES("550", "{competitor1} or yes", 1),
    HOME_OR_NO("551", "{competitor1} or no", 2),
    DRAW_OR_YES("552", "draw or yes", 3),
    DRAW_OR_NO("553", "draw or no", 4),
    AWAY_OR_YES("554", "{competitor2} or yes", 5),
    AWAY_OR_NO("555", "{competitor2} or no", 6),

    /**
     * 双方/一方/两者皆不得分
     */
    TEAM_BOTH("556", "Both", 1),
    TEAM_ONLY("557", "Only", 2),
    TEAM_NONE("558", "None", 3),

    /**
     * 获胜球队
     */
    TEAM1_AND_YES("559", "{competitor1} & yes", 1),
    TEAM1_AND_NO("560", "{competitor1} & no", 2),
    TEAM2_AND_YES("561", "{competitor2} & yes", 3),
    TEAM2_AND_NO("562", "{competitor2} & no", 4),
    ANY_TEAM_AND_YES("563", "{competitor1}/{competitor2} & yes", 5),
    ANY_TEAM_AND_NO("564", "{competitor1}/{competitor2} & no", 6),

    /**
     * 球队投注项（补充）
     */
    BOTH_COMPETITOR("565", "Both", 4),

    /**
     * 最先进球 / 最后进球
     */
    FLG_1FIRST("570", "1First", 1),
    FLG_1LAST("571", "1Last", 2),
    FLG_2FIRST("572", "2First", 3),
    FLG_2LAST("573", "2Last", 4),
    FLG_NONE("574", "None", 5),

    /**
     * 首次进球时间三项
     */
    INTERVAL_FGT_0_24("575", "0:00-24:59", 1),
    INTERVAL_FGT_25_UP("576", "25Up", 2),
    INTERVAL_FGT_NO_GOAL("577", "No goal", 3),
    ;

    //--------------------------------------------------投注项集合---------------------------------------------------------------

    public static final List<MarketSelection> SELECTIONS_WINNER_1X2 = Arrays.asList(WINNER_HOME, WINNER_AWAY, WINNER_DRAW);

    public static final List<MarketSelection> SELECTIONS_DOUBLE_CHANCE = Arrays.asList(HOME_DRAW, HOME_AWAY, AWAY_DRAW);

    public static final List<MarketSelection> SELECTIONS_ODD_EVEN = Arrays.asList(ODD, EVEN);

    public static final List<MarketSelection> SELECTIONS_HOME_AWAY = Arrays.asList(AH_HOME, AH_AWAY);

    public static final List<MarketSelection> SELECTIONS_OVER_UNDER = Arrays.asList(OVER, UNDER);

    public static final List<MarketSelection> SELECTIONS_OVER_UNDER_3 = Arrays.asList(OVER, UNDER, EQUAL);

    public static final List<MarketSelection> SELECTIONS_CORRECT_SCORE_4 = Arrays.asList(SCORE_0_0, SCORE_0_1, SCORE_0_2, SCORE_0_3, SCORE_0_4, SCORE_1_0, SCORE_1_1, SCORE_1_2, SCORE_1_3, SCORE_1_4, SCORE_2_0, SCORE_2_1, SCORE_2_2, SCORE_2_3, SCORE_2_4, SCORE_3_0, SCORE_3_1, SCORE_3_2, SCORE_3_3, SCORE_3_4, SCORE_4_0, SCORE_4_1, SCORE_4_2, SCORE_4_3, SCORE_4_4, SCORE_OTHER);
    public static final List<MarketSelection> SELECTIONS_CORRECT_SCORE_3 = Arrays.asList(SCORE_0_0, SCORE_0_1, SCORE_0_2, SCORE_0_3, SCORE_1_0, SCORE_1_1, SCORE_1_2, SCORE_1_3, SCORE_2_0, SCORE_2_1, SCORE_2_2, SCORE_2_3, SCORE_3_0, SCORE_3_1, SCORE_3_2, SCORE_3_3, SCORE_OTHER);

    public static final List<MarketSelection> SELECTIONS_WINNING_MARGIN_4 = Arrays.asList(HOME_WIN_1, HOME_WIN_2, HOME_WIN_3, HOME_WIN_4_PLUS, SCORE_DRAW, NO_GOAL, AWAY_WIN_1, AWAY_WIN_2, AWAY_WIN_3, AWAY_WIN_4_PLUS);
    public static final List<MarketSelection> SELECTIONS_WINNING_MARGIN_3 = Arrays.asList(HOME_WIN_1, HOME_WIN_2, HOME_WIN_3_PLUS, DRAW, AWAY_WIN_1, AWAY_WIN_2, AWAY_WIN_3_PLUS);
    public static final List<MarketSelection> SELECTIONS_WINNING_MARGIN_3_WITHOUT_DRAW = Arrays.asList(HOME_WIN_1, HOME_WIN_2, HOME_WIN_3_PLUS, AWAY_WIN_1, AWAY_WIN_2, AWAY_WIN_3_PLUS);
    public static final List<MarketSelection> SELECTIONS_WINNING_MARGIN_2 = Arrays.asList(HOME_WIN_1, HOME_WIN_2_PLUS, SCORE_DRAW, NO_GOAL, AWAY_WIN_1, AWAY_WIN_2_PLUS);

    public static final List<MarketSelection> SELECTIONS_EXACT_GOALS_3_PLUS = Arrays.asList(EXACT_0, EXACT_1, EXACT_2, EXACT_3_PLUS);
    public static final List<MarketSelection> SELECTIONS_EXACT_GOALS_4_PLUS = Arrays.asList(EXACT_0, EXACT_1, EXACT_2, EXACT_3, EXACT_4_PLUS);
    public static final List<MarketSelection> SELECTIONS_EXACT_GOALS_6_PLUS = Arrays.asList(EXACT_0, EXACT_1, EXACT_2, EXACT_3, EXACT_4, EXACT_5, EXACT_6_PLUS);

    public static final List<MarketSelection> SELECTIONS_YES_NO = Arrays.asList(YES, NO);

    public static final List<MarketSelection> SELECTIONS_1X2_AND_TOTAL = Arrays.asList(HOME_AND_OVER, HOME_AND_UNDER, DRAW_AND_OVER, DRAW_AND_UNDER, AWAY_AND_OVER, AWAY_AND_UNDER);

    public static final List<MarketSelection> SELECTIONS_GOAL_RANGE_6_PLUS = Arrays.asList(RANGE_0_1, RANGE_2_3, RANGE_4_6, RANGE_7_PLUS);

    public static final List<MarketSelection> SELECTIONS_1X2_AND_BOOLEAN = Arrays.asList(HOME_AND_YES, HOME_AND_NO, DRAW_AND_YES, DRAW_AND_NO, AWAY_AND_YES, AWAY_AND_NO);

    public static final List<MarketSelection> SELECTIONS_TOTAL_AND_BOOLEAN = Arrays.asList(OVER_AND_YES, OVER_AND_NO, UNDER_AND_YES, UNDER_AND_NO);

    public static final List<MarketSelection> SELECTIONS_HT_FT_CORRECT_SCORE = Arrays.asList(SCORE_0_0_0_0, SCORE_0_0_0_1, SCORE_0_0_0_2, SCORE_0_0_0_3, SCORE_0_0_1_0, SCORE_0_0_1_1, SCORE_0_0_1_2, SCORE_0_0_2_0, SCORE_0_0_2_1, SCORE_0_0_3_0, SCORE_0_0_5_PLUS,
            SCORE_0_1_0_1, SCORE_0_1_0_2, SCORE_0_1_0_3, SCORE_0_1_1_1, SCORE_0_1_1_2, SCORE_0_1_2_1, SCORE_0_1_5_PLUS, SCORE_0_2_0_2, SCORE_0_2_0_3, SCORE_0_2_1_2, SCORE_0_2_5_PLUS, SCORE_0_3_0_3, SCORE_0_3_5_PLUS,
            SCORE_1_0_1_0, SCORE_1_0_1_1, SCORE_1_0_1_2, SCORE_1_0_2_0, SCORE_1_0_2_1, SCORE_1_0_3_0, SCORE_1_0_5_PLUS, SCORE_1_1_1_1, SCORE_1_1_1_2, SCORE_1_1_2_1, SCORE_1_1_5_PLUS, SCORE_1_2_1_2, SCORE_1_2_5_PLUS,
            SCORE_2_0_2_0, SCORE_2_0_2_1, SCORE_2_0_3_0, SCORE_2_0_5_PLUS, SCORE_2_1_2_1, SCORE_2_1_5_PLUS, SCORE_3_0_3_0, SCORE_3_0_5_PLUS, SCORE_4_PLUS_5_PLUS);

    public static final List<MarketSelection> SELECTIONS_HT_FT = Arrays.asList(WINNER_HOME_HOME, WINNER_HOME_DRAW, WINNER_HOME_AWAY, WINNER_DRAW_HOME, WINNER_DRAW_DRAW, WINNER_DRAW_AWAY, WINNER_AWAY_HOME, WINNER_AWAY_DRAW, WINNER_AWAY_AWAY);

    public static final List<MarketSelection> SELECTIONS_DOUBLE_CHANCE_AND_BOOLEAN = Arrays.asList(HOME_DRAW_AND_YES, HOME_DRAW_AND_NO, HOME_AWAY_AND_YES, HOME_AWAY_AND_NO, AWAY_DRAW_AND_YES, AWAY_DRAW_AND_NO);

    public static final List<MarketSelection> SELECTIONS_BOOLEAN_BOOLEAN = Arrays.asList(NO_NO, YES_NO, YES_YES, NO_YES);

    public static final List<MarketSelection> SELECTIONS_TEAM = Arrays.asList(COMPETITOR1, NONE, COMPETITOR2);

    public static final List<MarketSelection> SELECTIONS_TEAM_WITHOUT_NONE = Arrays.asList(COMPETITOR1, COMPETITOR2);

    public static final List<MarketSelection> SELECTIONS_TEAM_WITH_BOTH = Arrays.asList(COMPETITOR1, NONE, COMPETITOR2, BOTH_COMPETITOR);

    public static final List<MarketSelection> SELECTIONS_HALF = Arrays.asList(HALF1, HALF2, EQUAL);

    public static final List<MarketSelection> SELECTIONS_HALF_NO_GOAL = Arrays.asList(HALF1, HALF2, HALF_NO_GOAL);

    public static final List<MarketSelection> SELECTIONS_BASKET_HALF = Arrays.asList(HALF1, HALF2);

    public static final List<MarketSelection> SELECTIONS_TIME_INTERVAL = Arrays.asList(INTERVAL_1_15, INTERVAL_16_30, INTERVAL_31_45, INTERVAL_46_60, INTERVAL_61_75, INTERVAL_76_90, INTERVAL_NONE);

    public static final List<MarketSelection> SELECTIONS_HOME_NO_BET = Arrays.asList(HOME_NO_BET_DRAW, HOME_NO_BET_AWAY);

    public static final List<MarketSelection> SELECTIONS_AWAY_NO_BET = Arrays.asList(AWAY_NO_BET_HOME, AWAY_NO_BET_DRAW);

    public static final List<MarketSelection> SELECTIONS_CORNER_RANGE_5_PLUS = Arrays.asList(CORNER_RANGE_0_2, CORNER_RANGE_3_4, CORNER_RANGE_5_PLUS);

    public static final List<MarketSelection> SELECTIONS_CORNER_RANGE_7_PLUS = Arrays.asList(CORNER_RANGE_0_2, CORNER_RANGE_3_4, CORNER_RANGE_5_6, CORNER_RANGE_7_PLUS);

    public static final List<MarketSelection> SELECTIONS_CORNER_RANGE_7_PLUS_2 = Arrays.asList(CORNER_RANGE_0_4, CORNER_RANGE_5_6, CORNER_RANGE_7_PLUS);

    public static final List<MarketSelection> SELECTIONS_CORNER_RANGE_12_PLUS = Arrays.asList(CORNER_RANGE_0_8, CORNER_RANGE_9_11, CORNER_RANGE_12_PLUS);

    public static final List<MarketSelection> SELECTIONS_EXACT_BOOKINGS_12_PLUS = Arrays.asList(BOOKING_EXACT_0_3, BOOKING_EXACT_4, BOOKING_EXACT_5, BOOKING_EXACT_6, BOOKING_EXACT_7, BOOKING_EXACT_8, BOOKING_EXACT_9, BOOKING_EXACT_10, BOOKING_EXACT_11, BOOKING_EXACT_12_PLUS);

    public static final List<MarketSelection> SELECTIONS_EXACT_BOOKINGS_4_PLUS = Arrays.asList(BOOKING_EXACT_0_1, BOOKING_EXACT_2, BOOKING_EXACT_3, BOOKING_EXACT_4_PLUS);

    public static final List<MarketSelection> SELECTIONS_EXACT_BOOKINGS_6_PLUS = Arrays.asList(BOOKING_EXACT_0, BOOKING_EXACT_1, BOOKING_EXACT_2, BOOKING_EXACT_3, BOOKING_EXACT_4, BOOKING_EXACT_5, BOOKING_EXACT_6_PLUS);

    public static final List<MarketSelection> SELECTIONS_EXACT_BOOKINGS_3_PLUS = Arrays.asList(BOOKING_EXACT_0, BOOKING_EXACT_1, BOOKING_EXACT_2, BOOKING_EXACT_3_PLUS);

    public static final List<MarketSelection> SELECTIONS_OT_CORRECT_SCORE = Arrays.asList(OT_SCORE_0_0, OT_SCORE_1_0, OT_SCORE_2_0, OT_SCORE_3_0, OT_SCORE_0_1, OT_SCORE_1_1, OT_SCORE_2_1, OT_SCORE_0_2, OT_SCORE_1_2, OT_SCORE_0_3);

    public static final List<MarketSelection> SELECTIONS_PEN_SO_CORRECT_SCORE = Arrays.asList(PEN_SO_SCORE_0_1, PEN_SO_SCORE_0_2, PEN_SO_SCORE_0_3, PEN_SO_SCORE_1_0, PEN_SO_SCORE_1_2, PEN_SO_SCORE_1_3, PEN_SO_SCORE_1_4, PEN_SO_SCORE_2_0, PEN_SO_SCORE_2_1, PEN_SO_SCORE_2_3, PEN_SO_SCORE_2_4,
            PEN_SO_SCORE_3_0, PEN_SO_SCORE_3_1, PEN_SO_SCORE_3_2, PEN_SO_SCORE_3_4, PEN_SO_SCORE_3_5, PEN_SO_SCORE_4_1, PEN_SO_SCORE_4_2, PEN_SO_SCORE_4_3, PEN_SO_SCORE_4_5, PEN_SO_SCORE_5_3, PEN_SO_SCORE_5_4, PEN_SO_SCORE_OTHER);

    public static final List<MarketSelection> SELECTIONS_PEN_SO_EXACT_GOALS_10_PLUS = Arrays.asList(PEN_SO_EXACT_0_4, PEN_SO_EXACT_5, PEN_SO_EXACT_6, PEN_SO_EXACT_7, PEN_SO_EXACT_8, PEN_SO_EXACT_9, PEN_SO_EXACT_10_PLUS);

    public static final List<MarketSelection> SELECTIONS_WINNING_METHOD = Arrays.asList(HOME_BY_REGULAR_TIME, AWAY_BY_REGULAR_TIME, HOME_BY_OVER_TIME, AWAY_BY_OVER_TIME, HOME_BY_PENALTY, AWAY_BY_PENALTY);

    public static final List<MarketSelection> SELECTIONS_CORRECT_SCORE_MULTI_BET = Arrays.asList(SCORE_MULTI_BET_HOME_1, SCORE_MULTI_BET_HOME_2, SCORE_MULTI_BET_HOME_3, SCORE_MULTI_BET_HOME_4, SCORE_MULTI_BET_HOME_OTHER, SCORE_MULTI_BET_DRAW_OTHER, SCORE_MULTI_BET_AWAY_1, SCORE_MULTI_BET_AWAY_2, SCORE_MULTI_BET_AWAY_3, SCORE_MULTI_BET_AWAY_4, SCORE_MULTI_BET_AWAY_OTHER);

    public static final List<MarketSelection> SELECTIONS_DOUBLE_CHANCE_AND_TOTAL = Arrays.asList(HOME_DRAW_AND_OVER, HOME_DRAW_AND_UNDER, HOME_AWAY_AND_OVER, HOME_AWAY_AND_UNDER, AWAY_DRAW_AND_OVER, AWAY_DRAW_AND_UNDER);

    public static final List<MarketSelection> SELECTIONS_HT_FT_AND_TOTAL = Arrays.asList(HT_HOME_FT_HOME_AND_OVER, HT_HOME_FT_HOME_AND_UNDER, HT_HOME_FT_DRAW_AND_OVER, HT_HOME_FT_DRAW_AND_UNDER,
            HT_HOME_FT_AWAY_AND_OVER, HT_HOME_FT_AWAY_AND_UNDER, HT_DRAW_FT_HOME_AND_OVER, HT_DRAW_FT_HOME_AND_UNDER, HT_DRAW_FT_DRAW_AND_OVER, HT_DRAW_FT_DRAW_AND_UNDER, HT_DRAW_FT_AWAY_AND_OVER,
            HT_DRAW_FT_AWAY_AND_UNDER, HT_AWAY_FT_HOME_AND_OVER, HT_AWAY_FT_HOME_AND_UNDER, HT_AWAY_FT_DRAW_AND_OVER, HT_AWAY_FT_DRAW_AND_UNDER, HT_AWAY_FT_AWAY_AND_OVER, HT_AWAY_FT_AWAY_AND_UNDER);

    public static final List<MarketSelection> SELECTIONS_HT_FT_ODD_EVEN = Arrays.asList(HT_ODD_FT_ODD, HT_ODD_FT_EVEN, HT_EVEN_FT_ODD, HT_EVEN_FT_EVEN);

    public static final List<MarketSelection> SELECTIONS_ODD_EVEN_AND_TOTAL = Arrays.asList(ODD_AND_OVER, ODD_AND_UNDER, EVEN_AND_OVER, EVEN_AND_UNDER);

    public static final List<MarketSelection> SELECTIONS_1X2_AND_FIRST_SCORE_TEAM = Arrays.asList(HOME_AND_HOME, HOME_AND_AWAY, DRAW_AND_HOME, DRAW_AND_AWAY, AWAY_AND_HOME, AWAY_AND_AWAY, DRAW_AND_NO_GOAL);

    public static final List<MarketSelection> SELECTIONS_HT_FT_AND_EXACT_GOALS = Arrays.asList(
            HT_HOME_FT_HOME_AND_EXACT_0, HT_HOME_FT_HOME_AND_EXACT_1, HT_HOME_FT_HOME_AND_EXACT_2, HT_HOME_FT_HOME_AND_EXACT_3, HT_HOME_FT_HOME_AND_EXACT_4, HT_HOME_FT_HOME_AND_EXACT_5_PLUS,
            HT_HOME_FT_DRAW_AND_EXACT_0, HT_HOME_FT_DRAW_AND_EXACT_1, HT_HOME_FT_DRAW_AND_EXACT_2, HT_HOME_FT_DRAW_AND_EXACT_3, HT_HOME_FT_DRAW_AND_EXACT_4, HT_HOME_FT_DRAW_AND_EXACT_5_PLUS,
            HT_HOME_FT_AWAY_AND_EXACT_0, HT_HOME_FT_AWAY_AND_EXACT_1, HT_HOME_FT_AWAY_AND_EXACT_2, HT_HOME_FT_AWAY_AND_EXACT_3, HT_HOME_FT_AWAY_AND_EXACT_4, HT_HOME_FT_AWAY_AND_EXACT_5_PLUS,
            HT_DRAW_FT_HOME_AND_EXACT_0, HT_DRAW_FT_HOME_AND_EXACT_1, HT_DRAW_FT_HOME_AND_EXACT_2, HT_DRAW_FT_HOME_AND_EXACT_3, HT_DRAW_FT_HOME_AND_EXACT_4, HT_DRAW_FT_HOME_AND_EXACT_5_PLUS,
            HT_DRAW_FT_DRAW_AND_EXACT_0, HT_DRAW_FT_DRAW_AND_EXACT_1, HT_DRAW_FT_DRAW_AND_EXACT_2, HT_DRAW_FT_DRAW_AND_EXACT_3, HT_DRAW_FT_DRAW_AND_EXACT_4, HT_DRAW_FT_DRAW_AND_EXACT_5_PLUS,
            HT_DRAW_FT_AWAY_AND_EXACT_0, HT_DRAW_FT_AWAY_AND_EXACT_1, HT_DRAW_FT_AWAY_AND_EXACT_2, HT_DRAW_FT_AWAY_AND_EXACT_3, HT_DRAW_FT_AWAY_AND_EXACT_4, HT_DRAW_FT_AWAY_AND_EXACT_5_PLUS,
            HT_AWAY_FT_HOME_AND_EXACT_0, HT_AWAY_FT_HOME_AND_EXACT_1, HT_AWAY_FT_HOME_AND_EXACT_2, HT_AWAY_FT_HOME_AND_EXACT_3, HT_AWAY_FT_HOME_AND_EXACT_4, HT_AWAY_FT_HOME_AND_EXACT_5_PLUS,
            HT_AWAY_FT_DRAW_AND_EXACT_0, HT_AWAY_FT_DRAW_AND_EXACT_1, HT_AWAY_FT_DRAW_AND_EXACT_2, HT_AWAY_FT_DRAW_AND_EXACT_3, HT_AWAY_FT_DRAW_AND_EXACT_4, HT_AWAY_FT_DRAW_AND_EXACT_5_PLUS,
            HT_AWAY_FT_AWAY_AND_EXACT_0, HT_AWAY_FT_AWAY_AND_EXACT_1, HT_AWAY_FT_AWAY_AND_EXACT_2, HT_AWAY_FT_AWAY_AND_EXACT_3, HT_AWAY_FT_AWAY_AND_EXACT_4, HT_AWAY_FT_AWAY_AND_EXACT_5_PLUS);

    public static final List<MarketSelection> SELECTIONS_TIME_INTERVAL_FIVE = Arrays.asList(INTERVAL_1_5, INTERVAL_6_10, INTERVAL_11_15, INTERVAL_16_20, INTERVAL_21_25, INTERVAL_26_30,
            INTERVAL_31_35, INTERVAL_36_40, INTERVAL_41_45, INTERVAL_46_50, INTERVAL_51_55, INTERVAL_56_60, INTERVAL_61_65, INTERVAL_66_70, INTERVAL_71_75, INTERVAL_76_80, INTERVAL_81_85,
            INTERVAL_86_90, INTERVAL_NO_GOAL, INTERVAL_CLUTCH);

    public static final List<MarketSelection> SELECTIONS_BASKET_WINNING_MARGIN_6 = Arrays.asList(BASKET_HOME_WIN_6_PLUS, BASKET_AWAY_WIN_6_PLUS, BASKET_OTHER);

    public static final List<MarketSelection> SELECTIONS_BASKET_WINNING_MARGIN_11 = Arrays.asList(BASKET_HOME_WIN_1_5, BASKET_HOME_WIN_6_10, BASKET_HOME_WIN_11_PLUS, BASKET_AWAY_WIN_1_5, BASKET_AWAY_WIN_6_10, BASKET_AWAY_WIN_11_PLUS);

    public static final List<MarketSelection> SELECTIONS_BASKET_ANY_TEAM_WINNING_MARGIN_31 = Arrays.asList(BASKET_WIN_1_5, BASKET_WIN_6_10, BASKET_WIN_11_15, BASKET_WIN_16_20, BASKET_WIN_21_25, BASKET_WIN_26_30, BASKET_WIN_31_PLUS);

    public static final List<MarketSelection> SELECTIONS_BASKET_WINNING_MARGIN_26 = Arrays.asList(BASKET_HOME_WIN_1_5, BASKET_HOME_WIN_6_10, BASKET_HOME_WIN_11_15, BASKET_HOME_WIN_16_20, BASKET_HOME_WIN_21_25, BASKET_HOME_WIN_26_PLUS,
            BASKET_AWAY_WIN_1_5, BASKET_AWAY_WIN_6_10, BASKET_AWAY_WIN_11_15, BASKET_AWAY_WIN_16_20, BASKET_AWAY_WIN_21_25, BASKET_AWAY_WIN_26_PLUS);

    public static final List<MarketSelection> SELECTIONS_BASKET_HALF_WINNING_MARGIN_26 = Arrays.asList(BASKET_HOME_WIN_1_5, BASKET_HOME_WIN_6_10, BASKET_HOME_WIN_11_15, BASKET_HOME_WIN_16_20, BASKET_HOME_WIN_21_25, BASKET_HOME_WIN_26_PLUS,
            BASKET_AWAY_WIN_1_5, BASKET_AWAY_WIN_6_10, BASKET_AWAY_WIN_11_15, BASKET_AWAY_WIN_16_20, BASKET_AWAY_WIN_21_25, BASKET_AWAY_WIN_26_PLUS, BASKET_DRAW);

    public static final List<MarketSelection> SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_26 = SELECTIONS_BASKET_HALF_WINNING_MARGIN_26;

    public static final List<MarketSelection> SELECTIONS_BASKET_WINNING_MARGIN_21 = Arrays.asList(BASKET_HOME_WIN_1_2, BASKET_HOME_WIN_3_6, BASKET_HOME_WIN_7_9, BASKET_HOME_WIN_10_13, BASKET_HOME_WIN_14_16, BASKET_HOME_WIN_17_20, BASKET_HOME_WIN_21_PLUS,
            BASKET_AWAY_WIN_1_2, BASKET_AWAY_WIN_3_6, BASKET_AWAY_WIN_7_9, BASKET_AWAY_WIN_10_13, BASKET_AWAY_WIN_14_16, BASKET_AWAY_WIN_17_20, BASKET_AWAY_WIN_21_PLUS);

    public static final List<MarketSelection> SELECTIONS_WINNER_AND_TOTAL = Arrays.asList(HOME_AND_OVER, HOME_AND_UNDER, AWAY_AND_OVER, AWAY_AND_UNDER);

    public static final List<MarketSelection> SELECTIONS_BASKET_HALF_WINNING_MARGIN_11 = Arrays.asList(BASKET_HOME_WIN_1_5, BASKET_HOME_WIN_6_10, BASKET_HOME_WIN_11_PLUS, BASKET_AWAY_WIN_1_5, BASKET_AWAY_WIN_6_10, BASKET_AWAY_WIN_11_PLUS, BASKET_DRAW);

    public static final List<MarketSelection> SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_3 = Arrays.asList(BASKET_HOME_WIN_3_PLUS, BASKET_AWAY_WIN_3_PLUS, BASKET_OTHER);

    public static final List<MarketSelection> SELECTIONS_QUARTER = Arrays.asList(QUARTER1, QUARTER2, QUARTER3, QUARTER4, BASKET_EQUALS);

    public static final List<MarketSelection> SELECTIONS_1X2_AND_ODD_EVEN = Arrays.asList(HOME_AND_ODD, HOME_AND_EVEN, DRAW_AND_EVEN, AWAY_AND_ODD, AWAY_AND_EVEN);

    public static final List<MarketSelection> SELECTIONS_BASKET_HT_FT = Arrays.asList(WINNER_HOME_HOME, WINNER_HOME_AWAY, WINNER_DRAW_HOME, WINNER_DRAW_AWAY, WINNER_AWAY_HOME, WINNER_AWAY_AWAY);

    public static final List<MarketSelection> SELECTIONS_BASKET_HDP_TOTAL = Arrays.asList(HDP1_AND_OVER, HDP1_AND_UNDER, HDP2_AND_OVER, HDP2_AND_UNDER);

    public static final List<MarketSelection> SELECTIONS_BASKET_LAST_DIGIT = Arrays.asList(LAST_DIGIT_0, LAST_DIGIT_1, LAST_DIGIT_2, LAST_DIGIT_3, LAST_DIGIT_4, LAST_DIGIT_5, LAST_DIGIT_6, LAST_DIGIT_7, LAST_DIGIT_8, LAST_DIGIT_9);

    public static final List<MarketSelection> SELECTIONS_PEN_SO_END_ROUND = Arrays.asList(PEN_SO_ROUND_3, PEN_SO_ROUND_4, PEN_SO_ROUND_5, PEN_SO_ROUND_SD);

    public static final List<MarketSelection> SELECTIONS_TOTAL_AND_TEAM = Arrays.asList(OVER_AND_HOME, OVER_AND_AWAY, UNDER_AND_HOME, UNDER_AND_AWAY, UNDER_AND_NONE);

    public static final List<MarketSelection> SELECTIONS_DOUBLE_CHANCE_AND_TEAM = Arrays.asList(HOME_DRAW_AND_HOME, HOME_DRAW_AND_AWAY, HOME_AWAY_AND_HOME, HOME_AWAY_AND_AWAY, AWAY_DRAW_AND_HOME, AWAY_DRAW_AND_AWAY, DC_DRAW_AND_NONE);

    public static final List<MarketSelection> SELECTIONS_1X2_OR_BOOLEAN = Arrays.asList(HOME_OR_YES, HOME_OR_NO, DRAW_OR_YES, DRAW_OR_NO, AWAY_OR_YES, AWAY_OR_NO);

    public static final List<MarketSelection> SELECTIONS_TEAM_TO_SCORE = Arrays.asList(TEAM_BOTH, TEAM_ONLY, TEAM_NONE);

    public static final List<MarketSelection> SELECTIONS_TEAM_TO_WIN = Arrays.asList(TEAM1_AND_YES, TEAM1_AND_NO, TEAM2_AND_YES, TEAM2_AND_NO, ANY_TEAM_AND_YES, ANY_TEAM_AND_NO);

    public static final List<MarketSelection> SELECTIONS_FIRST_LAST_GOAL = Arrays.asList(FLG_1FIRST, FLG_1LAST, FLG_2FIRST, FLG_2LAST, FLG_NONE);

    public static final List<MarketSelection> SELECTIONS_1ST_GOAL_TIME_3WAY = Arrays.asList(INTERVAL_FGT_0_24, INTERVAL_FGT_25_UP, INTERVAL_FGT_NO_GOAL);

    /**
     * 投注项Id
     */
    private final String id;

    /**
     * 投注项名称
     */
    private final String name;

    /**
     * 序号
     */
    private final int order;

    /**
     * 构造方法
     *
     * @param id    玩法Id
     * @param name  投注项名称
     * @param order 序号
     */
    MarketSelection(String id, String name, int order) {
        this.id = id;
        this.name = name;
        this.order = order;
    }
}
