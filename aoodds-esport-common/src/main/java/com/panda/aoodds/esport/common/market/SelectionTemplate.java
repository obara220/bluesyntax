package com.panda.aoodds.esport.common.market;

import lombok.Getter;

/**
 * 投注项模板枚举
 *
 * @author Samuel
 */
@Getter
public enum SelectionTemplate {

    /**
     * 独赢主胜
     */
    TEMP_1X2_1("1", "1", "competitor1", null, null, null, null),
    /**
     * 独赢和局
     */
    TEMP_1X2_X("2", "X", "0", null, null, null, null),
    /**
     * 独赢客胜
     */
    TEMP_1X2_2("3", "2", "competitor2", null, null, null, null),
    /**
     * 总分大
     */
    TEMP_OU_OVER("4", "Over", null, null, null, null, null),
    /**
     * 总分小
     */
    TEMP_OU_UNDER("5", "Under", null, null, null, null, null),
    /**
     * 让球主队
     */
    TEMP_AH_1("6", "1", "competitor1", null, null, null, null),
    /**
     * 让球客队
     */
    TEMP_AH_2("7", "2", "competitor2", null, null, null, null),
    /**
     * 双重机会主和
     */
    TEMP_DC_1X("8", "1X", "1", "competitor1", "X", "0", null),
    /**
     * 双重机会主客
     */
    TEMP_DC_12("9", "12", "1", "competitor1", "2", "competitor2", null),
    /**
     * 双重机会客和
     */
    TEMP_DC_2X("10", "X2", "X", "0", "2", "competitor2", null),
    /**
     * 奇数
     */
    TEMP_OE_ODD("11", "Odd", null, null, null, null, null),
    /**
     * 偶数
     */
    TEMP_OE_EVEN("12", "Even", null, null, null, null, null),
    /**
     * 是
     */
    TEMP_BOOL_YES("13", "Yes", null, null, null, null, null),
    /**
     * 否
     */
    TEMP_BOOL_NO("14", "No", null, null, null, null, null),
    /**
     * 主队
     */
    TEMP_TEAM_1("15", "1", "competitor1", null, null, null, null),
    /**
     * 没有
     */
    TEMP_TEAM_NONE("16", "None", "0", null, null, null, null),
    /**
     * 客队
     */
    TEMP_TEAM_2("17", "2", "competitor2", null, null, null, null),
    /**
     * 上半场
     */
    TEMP_HALF1("18", "FirstHalf", null, null, null, null, null),
    /**
     * 下半场
     */
    TEMP_HALF2("19", "SecondHalf", null, null, null, null, null),
    /**
     * 相等
     */
    TEMP_EQUAL("20", "Equals", null, null, null, null, null),
    /**
     * 两个半场均为进球
     */
    TEMP_HALF_NO_GOAL("21", "NoGoal", null, null, null, null, null),
    /**
     * 半/全场 主/主
     */
    TEMP_HFT_11("215", "11", "1", "competitor1", "1", "competitor1", null),
    /**
     * 半/全场 主/和
     */
    TEMP_HFT_1X("216", "1X", "1", "competitor1", "X", "0", null),
    /**
     * 半/全场 主/客
     */
    TEMP_HFT_12("217", "12", "1", "competitor1", "2", "competitor2", null),
    /**
     * 半/全场 和/主
     */
    TEMP_HFT_X1("218", "X1", "X", "0", "1", "competitor1", null),
    /**
     * 半/全场 和/和
     */
    TEMP_HFT_XX("219", "XX", "X", "0", "X", "0", null),
    /**
     * 半/全场 和/客
     */
    TEMP_HFT_X2("220", "X2", "X", "0", "2", "competitor2", null),
    /**
     * 半/全场 客/主
     */
    TEMP_HFT_21("221", "21", "2", "competitor2", "1", "competitor1", null),
    /**
     * 半/全场 客/和
     */
    TEMP_HFT_2X("222", "2X", "2", "competitor2", "X", "0", null),
    /**
     * 半/全场 客/客
     */
    TEMP_HFT_22("223", "22", "2", "competitor2", "2", "competitor2", null),
    /**
     * 独赢 & 进球大小 主&大
     */
    TEMP_1_AND_OVER("149", "1AndOver", "1", "Over", "competitor1", null, null),
    /**
     * 独赢 & 进球大小 主&小
     */
    TEMP_1_AND_UNDER("150", "1AndUnder", "1", "Under", "competitor1", null, null),
    /**
     * 独赢 & 进球大小 和&大
     */
    TEMP_X_AND_OVER("151", "XAndOver", "X", "Over", "0", null, null),
    /**
     * 独赢 & 进球大小 和&小
     */
    TEMP_X_AND_UNDER("152", "XAndUnder", "X", "Under", "0", null, null),
    /**
     * 独赢 & 进球大小 客&大
     */
    TEMP_2_AND_OVER("153", "2AndOver", "2", "Over", "competitor2", null, null),
    /**
     * 独赢 & 进球大小 客&小
     */
    TEMP_2_AND_UNDER("154", "2AndUnder", "2", "Under", "competitor2", null, null),
    /**
     * 独赢 & 两队都进球 主&是
     */
    TEMP_1_AND_YES("159", "1AndYes", "1", "competitor1", "Yes", null, null),
    /**
     * 独赢 & 两队都进球 主&否
     */
    TEMP_1_AND_NO("160", "1AndNo", "1", "competitor1", "No", null, null),
    /**
     * 独赢 & 两队都进球 和&是
     */
    TEMP_X_AND_YES("161", "XAndYes", "X", "0", "Yes", null, null),
    /**
     * 独赢 & 两队都进球 和&否
     */
    TEMP_X_AND_NO("162", "XAndNo", "X", "0", "No", null, null),
    /**
     * 独赢 & 两队都进球 客&是
     */
    TEMP_2_AND_YES("163", "2AndYes", "2", "competitor2", "Yes", null, null),
    /**
     * 独赢 & 两队都进球 客&否
     */
    TEMP_2_AND_NO("164", "2AndNo", "2", "competitor2", "No", null, null),
    /**
     * 进球大小 & 两队都进球 大&是
     */
    TEMP_OVER_AND_YES("165", "OverAndYes", "Over", "Yes", null, null, null),
    /**
     * 进球大小 & 两队都进球 大&否
     */
    TEMP_OVER_AND_NO("166", "OverAndNo", "Over", "No", null, null, null),
    /**
     * 进球大小 & 两队都进球 小&是
     */
    TEMP_UNDER_AND_YES("167", "UnderAndYes", "Under", "Yes", null, null, null),
    /**
     * 进球大小 & 两队都进球 小&否
     */
    TEMP_UNDER_AND_NO("168", "UnderAndNo", "Under", "No", null, null, null),
    /**
     * 双重机会 & 两队都进球 主和&是
     */
    TEMP_1X_AND_YES("224", "1XAndYes", "1", "competitor1", "X", "0", "Yes"),
    /**
     * 双重机会 & 两队都进球 主和&否
     */
    TEMP_1X_AND_NO("225", "1XAndNo", "1", "competitor1", "X", "0", "No"),
    /**
     * 双重机会 & 两队都进球 主客&是
     */
    TEMP_12_AND_YES("226", "12AndYes", "1", "competitor1", "2", "competitor2", "Yes"),
    /**
     * 双重机会 & 两队都进球 主客&否
     */
    TEMP_12_AND_NO("227", "12AndNo", "1", "competitor1", "2", "competitor2", "No"),
    /**
     * 双重机会 & 两队都进球 客和&是
     */
    TEMP_2X_AND_YES("228", "X2AndYes", "X", "0", "2", "competitor2", "Yes"),
    /**
     * 双重机会 & 两队都进球 客和&否
     */
    TEMP_2X_AND_NO("229", "X2AndNo", "X", "0", "2", "competitor2", "No"),
    /**
     * 上/下半场两队都进球 是/是
     */
    TEMP_YES_YES("232", "YesYes", "Yes", "Yes", null, null, null),
    /**
     * 上/下半场两队都进球 是/是
     */
    TEMP_YES_NO("231", "YesNo", "Yes", "No", null, null, null),
    /**
     * 上/下半场两队都进球 是/是
     */
    TEMP_NO_YES("233", "NoYes", "No", "Yes", null, null, null),
    /**
     * 上/下半场两队都进球 是/是
     */
    TEMP_NO_NO("230", "NoNo", "No", "No", null, null, null),
    /**
     * 时间区间 01-15
     */
    TEMP_TIME_01_15("234", "01-15", null, null, null, null, null),
    /**
     * 时间区间 01-15
     */
    TEMP_TIME_16_30("235", "16-30", null, null, null, null, null),
    /**
     * 时间区间 01-15
     */
    TEMP_TIME_31_45("236", "31-45", null, null, null, null, null),
    /**
     * 时间区间 01-15
     */
    TEMP_TIME_46_60("237", "46-60", null, null, null, null, null),
    /**
     * 时间区间 01-15
     */
    TEMP_TIME_61_75("238", "61-75", null, null, null, null, null),
    /**
     * 时间区间 01-15
     */
    TEMP_TIME_76_90("239", "76-90", null, null, null, null, null),
    /**
     * 时间区间 01-15
     */
    TEMP_TIME_NONE("240", "None", null, null, null, null, null),
    /**
     * 主队获胜退款 和局
     */
    TEMP_HOME_NO_BET_DRAW("241", "X", "competitor1", "0", null, null, null),
    /**
     * 主队获胜退款 客胜
     */
    TEMP_HOME_NO_BET_AWAY("242", "2", "competitor1", "competitor2", null, null, null),
    /**
     * 客队获胜退款 主胜
     */
    TEMP_AWAY_NO_BET_HOME("243", "1", "competitor2", "competitor1", null, null, null),
    /**
     * 客队获胜退款 和局
     */
    TEMP_AWAY_NO_BET_DRAW("244", "X", "competitor2", "0", null, null, null),
    /**
     * 准确罚牌分数 0
     */
    TEMP_BOOKING_EXACT_0("253", "0", null, null, null, null, null),
    /**
     * 准确罚牌分数 0-1
     */
    TEMP_BOOKING_EXACT_0_1("254", "0-1", null, null, null, null, null),
    /**
     * 准确罚牌分数 0-3
     */
    TEMP_BOOKING_EXACT_0_3("255", "0-3", null, null, null, null, null),
    /**
     * 准确罚牌分数 1
     */
    TEMP_BOOKING_EXACT_1("256", "1", null, null, null, null, null),
    /**
     * 准确罚牌分数 2
     */
    TEMP_BOOKING_EXACT_2("257", "2", null, null, null, null, null),
    /**
     * 准确罚牌分数 3
     */
    TEMP_BOOKING_EXACT_3("258", "3", null, null, null, null, null),
    /**
     * 准确罚牌分数 4
     */
    TEMP_BOOKING_EXACT_4("259", "4", null, null, null, null, null),
    /**
     * 准确罚牌分数 5
     */
    TEMP_BOOKING_EXACT_5("260", "5", null, null, null, null, null),
    /**
     * 准确罚牌分数 6
     */
    TEMP_BOOKING_EXACT_6("261", "6", null, null, null, null, null),
    /**
     * 准确罚牌分数 7
     */
    TEMP_BOOKING_EXACT_7("262", "7", null, null, null, null, null),
    /**
     * 准确罚牌分数 8
     */
    TEMP_BOOKING_EXACT_8("263", "8", null, null, null, null, null),
    /**
     * 准确罚牌分数 9
     */
    TEMP_BOOKING_EXACT_9("264", "9", null, null, null, null, null),
    /**
     * 准确罚牌分数 10
     */
    TEMP_BOOKING_EXACT_10("265", "10", null, null, null, null, null),
    /**
     * 准确罚牌分数 11
     */
    TEMP_BOOKING_EXACT_11("266", "11", null, null, null, null, null),
    /**
     * 准确罚牌分数 3+
     */
    TEMP_BOOKING_EXACT_3_PLUS("267", "3+", null, null, null, null, null),
    /**
     * 准确罚牌分数 4+
     */
    TEMP_BOOKING_EXACT_4_PLUS("268", "4+", null, null, null, null, null),
    /**
     * 准确罚牌分数 6+
     */
    TEMP_BOOKING_EXACT_6_PLUS("269", "6+", null, null, null, null, null),
    /**
     * 准确罚牌分数 12+
     */
    TEMP_BOOKING_EXACT_12_PLUS("270", "12+", null, null, null, null, null),
    /**
     * 获胜方式 主队常规时间获胜
     */
    TEMP_HOME_REGULAR_TIME("311", "1RT", "competitor1", null, null, null, null),
    /**
     * 获胜方式 客队常规时间获胜
     */
    TEMP_AWAY_REGULAR_TIME("312", "2RT", "competitor2", null, null, null, null),
    /**
     * 获胜方式 主队通过加时赛获胜
     */
    TEMP_HOME_OVER_TIME("313", "1OT", "competitor1", null, null, null, null),
    /**
     * 获胜方式 客队通过加时赛获胜
     */
    TEMP_AWAY_OVER_TIME("314", "2OT", "competitor2", null, null, null, null),
    /**
     * 获胜方式 主队通过点球大战获胜
     */
    TEMP_HOME_PENALTY("315", "1P", "competitor1", null, null, null, null),
    /**
     * 获胜方式 客队通过点球大战获胜
     */
    TEMP_AWAY_PENALTY("316", "2P", "competitor2", null, null, null, null),
    /**
     * 双重机会 & 进球大小
     */
    TEMP_1X_AND_OVER("328", "1XAndOver", "1", "competitor1", "X", "0", null),
    TEMP_1X_AND_under("329", "1XAndUnder", "1", "competitor1", "X", "0", null),
    TEMP_12_AND_OVER("330", "12AndOver", "1", "competitor1", "2", "competitor2", null),
    TEMP_12_AND_under("331", "12AndUnder", "1", "competitor1", "2", "competitor2", null),
    TEMP_X2_AND_OVER("332", "X2AndOver", "X", "0", "2", "competitor2", null),
    TEMP_X2_AND_under("333", "X2AndUnder", "X", "0", "2", "competitor2", null),
    /**
     * 半/全场 & 进球大小
     */
    TEMP_HFT_11_AND_OVER("334", "1/1AndOver", "1", "competitor1", "1", "competitor1", null),
    TEMP_HFT_11_AND_UNDER("335", "1/1AndUnder", "1", "competitor1", "1", "competitor1", null),
    TEMP_HFT_1X_AND_OVER("336", "1/XAndOver", "1", "competitor1", "X", "0", null),
    TEMP_HFT_1X_AND_UNDER("337", "1/XAndUnder", "1", "competitor1", "X", "0", null),
    TEMP_HFT_12_AND_OVER("338", "1/2AndOver", "1", "competitor1", "2", "competitor2", null),
    TEMP_HFT_12_AND_UNDER("339", "1/2AndUnder", "1", "competitor1", "2", "competitor2", null),
    TEMP_HFT_X1_AND_OVER("340", "X/1AndOver", "X", "0", "1", "competitor1", null),
    TEMP_HFT_X1_AND_UNDER("341", "X/1AndUnder", "X", "0", "1", "competitor1", null),
    TEMP_HFT_XX_AND_OVER("342", "X/XAndOver", "X", "0", "X", "0", null),
    TEMP_HFT_XX_AND_UNDER("343", "X/XAndUnder", "X", "0", "X", "0", null),
    TEMP_HFT_X2_AND_OVER("344", "X/2AndOver", "X", "0", "2", "competitor2", null),
    TEMP_HFT_X2_AND_UNDER("345", "X/2AndUnder", "X", "0", "2", "competitor2", null),
    TEMP_HFT_21_AND_OVER("346", "2/1AndOver", "2", "competitor2", "1", "competitor1", null),
    TEMP_HFT_21_AND_UNDER("347", "2/1AndUnder", "2", "competitor2", "1", "competitor1", null),
    TEMP_HFT_2X_AND_OVER("348", "2/XAndOver", "2", "competitor2", "X", "0", null),
    TEMP_HFT_2X_AND_UNDER("349", "2/XAndUnder", "2", "competitor2", "X", "0", null),
    TEMP_HFT_22_AND_OVER("350", "2/2AndOver", "2", "competitor2", "2", "competitor2", null),
    TEMP_HFT_22_AND_UNDER("351", "2/2AndUnder", "2", "competitor2", "2", "competitor2", null),
    /**
     * 半/全场 单双
     */
    TEMP_HFT_ODD_ODD("352", "Odd/Odd", null, null, null, null, null),
    TEMP_HFT_ODD_EVEN("353", "Odd/Even", null, null, null, null, null),
    TEMP_HFT_EVEN_ODD("354", "Even/Odd", null, null, null, null, null),
    TEMP_HFT_EVEN_EVEN("355", "Even/Even", null, null, null, null, null),
    /**
     * 进球单双 & 进球大小
     */
    TEMP_ODD_AND_OVER("356", "OddAndOver", null, null, null, null, null),
    TEMP_ODD_AND_UNDER("357", "OddAndUnder", null, null, null, null, null),
    TEMP_EVEN_AND_OVER("358", "EvenAndOver", null, null, null, null, null),
    TEMP_EVEN_AND_UNDER("359", "EvenAndUnder", null, null, null, null, null),
    /**
     * 独赢 & 最先进球球队
     */
    TEMP_1_AND_1("360", "1And1", null, null, null, null, null),
    TEMP_1_AND_2("361", "1And2", null, null, null, null, null),
    TEMP_X_AND_1("362", "XAnd1", null, null, null, null, null),
    TEMP_X_AND_2("363", "XAnd2", null, null, null, null, null),
    TEMP_2_AND_1("364", "2And1", null, null, null, null, null),
    TEMP_2_AND_2("365", "2And2", null, null, null, null, null),
    TEMP_X_AND_NO_GOAL("366", "NoGoal", null, null, null, null, null),
    /**
     * 进球时间区间(5min)
     */
    TEMP_INTERVAL_1_5("423", "1-5", null, null, null, null, null),
    TEMP_INTERVAL_6_10("424", "6-10", null, null, null, null, null),
    TEMP_INTERVAL_11_15("425", "11-15", null, null, null, null, null),
    TEMP_INTERVAL_16_20("426", "16-20", null, null, null, null, null),
    TEMP_INTERVAL_21_25("427", "21-25", null, null, null, null, null),
    TEMP_INTERVAL_26_30("428", "26-30", null, null, null, null, null),
    TEMP_INTERVAL_31_35("429", "31-35", null, null, null, null, null),
    TEMP_INTERVAL_36_40("430", "36-40", null, null, null, null, null),
    TEMP_INTERVAL_41_45("431", "41-45", null, null, null, null, null),
    TEMP_INTERVAL_46_50("432", "46-50", null, null, null, null, null),
    TEMP_INTERVAL_51_55("433", "51-55", null, null, null, null, null),
    TEMP_INTERVAL_56_60("434", "56-60", null, null, null, null, null),
    TEMP_INTERVAL_61_65("435", "61-65", null, null, null, null, null),
    TEMP_INTERVAL_66_70("436", "66-70", null, null, null, null, null),
    TEMP_INTERVAL_71_75("437", "71-75", null, null, null, null, null),
    TEMP_INTERVAL_76_80("438", "76-80", null, null, null, null, null),
    TEMP_INTERVAL_81_85("439", "81-85", null, null, null, null, null),
    TEMP_INTERVAL_86_90("440", "86-90", null, null, null, null, null),
    TEMP_INTERVAL_NO_GOAL("441", "NoGoal", null, null, null, null, null),
    TEMP_INTERVAL_CLUTCH("442", "ClutchGoal", null, null, null, null, null),
    /**
     * 篮球单节净胜分
     */
    TEMP_WINNING_1_AND_3_PLUS("443", "1And3+", "1", null, null, null, null),
    TEMP_WINNING_2_AND_3_PLUS("445", "2And3+", "2", null, null, null, null),
    TEMP_WINNING_OTHER("447", "Other", "0", null, null, null, null),
    /**
     * 第一节
     */
    TEMP_QUARTER1("484", "1stQuarter", null, null, null, null, null),
    /**
     * 第二节
     */
    TEMP_QUARTER2("485", "2ndQuarter", null, null, null, null, null),
    /**
     * 第三节
     */
    TEMP_QUARTER3("486", "3rdQuarter", null, null, null, null, null),
    /**
     * 第四节
     */
    TEMP_QUARTER4("487", "4thQuarter", null, null, null, null, null),
    /**
     * 相等
     */
    TEMP_BASKET_EQUAL("488", "Equals", null, null, null, null, null),
    /**
     * 独赢 & 单双
     */
    TEMP_HOME_AND_ODD("489", "1AndOdd", "competitor1", null, null, null, null),
    TEMP_HOME_AND_EVEN("490", "1AndEven", "competitor1", null, null, null, null),
    TEMP_DRAW_AND_EVEN("491", "XAndEven", "0", null, null, null, null),
    TEMP_AWAY_AND_ODD("492", "2AndOdd", "competitor2", null, null, null, null),
    TEMP_AWAY_AND_EVEN("493", "2AndEven", "competitor2", null, null, null, null),
    /**
     * 进球大小 & 首次进球队伍
     */
    TEMP_OVER_AND_1("533", "1AndOver", "competitor1", null, null, null, null),
    TEMP_OVER_AND_2("534", "2AndOver", "competitor2", null, null, null, null),
    TEMP_UNDER_AND_1("535", "1AndUnder", "competitor1", null, null, null, null),
    TEMP_UNDER_AND_2("536", "2AndUnder", "competitor2", null, null, null, null),
    TEMP_UNDER_AND_NONE("537", "None", "0", null, null, null, null),
    /**
     * 双重机会 & 首次进球队伍
     */
    TEMP_1X_AND_1("540", "1XAnd1", "1", "competitor1", "X", "0", null),
    TEMP_1X_AND_2("541", "1XAnd2", "1", "competitor1", "X", "0", null),
    TEMP_12_AND_1("542", "12And1", "1", "competitor1", "2", "competitor2", null),
    TEMP_12_AND_2("543", "12And2", "1", "competitor1", "2", "competitor2", null),
    TEMP_X2_AND_1("544", "X2And1", "X", "0", "2", "competitor2", null),
    TEMP_X2_AND_2("545", "X2And2", "X", "0", "2", "competitor2", null),
    TEMP_DC_X_AND_NONE("546", "None", null, null, null, null, null),
    /**
     * 独赢 或 任何零失球
     */
    TEMP_1_OR_YES("550", "1OrYes", "competitor1", null, null, null, null),
    TEMP_1_OR_NO("551", "1OrNo", "competitor1", null, null, null, null),
    TEMP_X_OR_YES("552", "XOrYes", "0", null, null, null, null),
    TEMP_X_OR_NO("553", "XOrNo", "0", null, null, null, null),
    TEMP_2_OR_YES("554", "2OrYes", "competitor2", null, null, null, null),
    TEMP_2_OR_NO("555", "2OrNo", "competitor2", null, null, null, null),
    /**
     * 双方/一方/两者皆不得分
     */
    TEMP_TEAMS_BOTH("556", "Both", null, null, null, null, null),
    TEMP_TEAMS_ONLY("557", "Only", null, null, null, null, null),
    TEMP_TEAMS_NONE("558", "None", null, null, null, null, null),
    /**
     * 获胜球队
     */
    TEMP_T1_AND_YES("559", "1AndYes", "competitor1", null, null, null, null),
    TEMP_T1_AND_NO("560", "1AndNo", "competitor1", null, null, null, null),
    TEMP_T2_AND_YES("561", "2AndYes", "competitor2", null, null, null, null),
    TEMP_T2_AND_NO("562", "2AndNo", "competitor2", null, null, null, null),
    TEMP_AT_AND_YES("563", "12AndYes", "0", null, null, null, null),
    TEMP_AT_AND_NO("564", "12AndNo", "0", null, null, null, null),
    /**
     * 球队投注项（补充）
     */
    TEMP_TEAM_BOTH("565", "Both", null, null, null, null, null),
    /**
     * 最先进球 / 最后进球
     */
    TEMP_FLG_1FIRST("570", "1First", "competitor1", null, null, null, null),
    TEMP_FLG_1LAST("571", "1Last", "competitor1", null, null, null, null),
    TEMP_FLG_2FIRST("572", "2First", "competitor2", null, null, null, null),
    TEMP_FLG_2LAST("573", "2Last", "competitor2", null, null, null, null),
    TEMP_FLG_NONE("574", "None", "0", null, null, null, null),
    /**
     * 首次进球时间三项
     */
    TEMP_INTERVAL_FGT_0_24("575", "0:00-24:59", null, null, null, null, null),
    TEMP_INTERVAL_FGT_25_UP("576", "25Up", null, null, null, null, null),
    TEMP_INTERVAL_FGT_NO_GOAL("577", "None", null, null, null, null, null),
    ;

    /**
     * 投注项Id
     */
    private final String id;

    /**
     * 投注项类型
     */
    private final String oddsType;

    /**
     * 附加字段1
     */
    private final String addition1;

    /**
     * 附加字段2
     */
    private final String addition2;

    /**
     * 附加字段3
     */
    private final String addition3;

    /**
     * 附加字段4
     */
    private final String addition4;

    /**
     * 附加字段5
     */
    private final String addition5;

    /**
     * 构造器
     *
     * @param id        投注项Id
     * @param oddsType  投注项类型
     * @param addition1 附加字段1
     * @param addition2 附加字段2
     * @param addition3 附加字段3
     * @param addition4 附加字段4
     * @param addition5 附加字段5
     */
    SelectionTemplate(String id, String oddsType, String addition1, String addition2, String addition3, String addition4, String addition5) {
        this.id = id;
        this.oddsType = oddsType;
        this.addition1 = addition1;
        this.addition2 = addition2;
        this.addition3 = addition3;
        this.addition4 = addition4;
        this.addition5 = addition5;
    }

    /**
     * 根据Id获取投注项模板数据
     *
     * @param id 投注项Id
     * @return 投注项模板数据
     */
    public static SelectionTemplate getSelectionTemplateById(String id) {
        for (SelectionTemplate selectionTemplate : SelectionTemplate.values()) {
            if (selectionTemplate.id.equals(id)) {
                return selectionTemplate;
            }
        }
        throw new IllegalArgumentException("【" + id + "】对应投注项模板不存在！！！");
    }
}
