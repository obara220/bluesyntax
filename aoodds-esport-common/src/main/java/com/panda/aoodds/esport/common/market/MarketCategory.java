package com.panda.aoodds.esport.common.market;

import com.google.common.collect.Lists;
import com.panda.aoodds.esport.common.enums.MarketModel;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.enums.MarketModel.*;
import static com.panda.aoodds.esport.common.market.MarketSelection.*;
import static com.panda.sports.algo.api.enums.SportEnum.*;


/**
 * 玩法枚举
 * <p>
 * 玩法Id规则：
 * 1、全场玩法 - 1****
 * 2、上半场玩法 - 2****
 * 3、15分钟玩法 - 3****
 * 4、角球玩法 - 4****（10010、10011、20009、20010除外）
 * 5、罚牌玩法 - 5****
 * 6、下半场玩法 - 6****
 * 7、加时赛玩法 - 7****
 * 8、点球大战玩法 - 8****
 * 9、特殊玩法 - 9****
 * 10、篮球玩法 - 11***
 *
 * @author Samuel
 */
@Getter
public enum MarketCategory {

    //--------------------------------------------------------------------------------------足球--------------------------------------------------------------------------------------
    //-------------------------------------全场玩法-------------------------------------------------------------------
    /**
     * 全场独赢
     */
    FT_1X2(10001, "FT - 1X2", 3, SELECTIONS_WINNER_1X2, "all|score|regular_play", 1, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 全场双重机会
     */
    FT_DOUBLE_CHANCE(10002, "FT - Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|score|regular_play", 6, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 全场单双
     */
    FT_ODD_EVEN(10003, "FT - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|regular_play", 15, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 全场让分
     */
    FT_ASIAN_HANDICAP(10004, "FT - Asian Handicap", 2, SELECTIONS_HOME_AWAY, "all|score|regular_play", 4, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 全场大小
     */
    FT_OVER_UNDER(10005, "FT - Over/Under", 2, SELECTIONS_OVER_UNDER, "all|score|regular_play", 2, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 全场主队大小
     */
    FT_TEAM1_OVER_UNDER(10006, "FT - Team1 Over/Under", 2, SELECTIONS_OVER_UNDER, "all|score|regular_play", 10, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 全场客队大小
     */
    FT_TEAM2_OVER_UNDER(10007, "FT - Team2 Over/Under", 2, SELECTIONS_OVER_UNDER, "all|score|regular_play", 11, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 全场正确比分
     */
    FT_CORRECT_SCORE(10008, "FT - CorrectScore", 26, SELECTIONS_CORRECT_SCORE_4, "all|score|regular_play", 7, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 全场净胜分（10项）
     */
    FT_WINNING_MARGIN(10009, "FT - Winning Margin(4+)", 10, SELECTIONS_WINNING_MARGIN_4, "all|score|regular_play", 340, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 全场角球让分
     */
//    FT_CORNER_AH(10010, "FT - Corner Handicap", 2, SELECTIONS_HOME_AWAY),
    /**
     * 全场角球大小
     */
//    FT_CORNER_OU(10011, "FT - Corner Over/Under", 2, SELECTIONS_OVER_UNDER),
    /**
     * 全场净胜分（7项）
     */
    FT_WINNING_MARGIN_SEVEN(10012, "FT - Winning Margin(3+)", 7, SELECTIONS_WINNING_MARGIN_3, "all|score|regular_play", 141, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 全场让球胜平负
     */
    FT_THREE_WAY_HANDICAP(10013, "FT - 3-Way Handicap", 3, SELECTIONS_WINNER_1X2, "all|score|regular_play", 3, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 全场平局退款
     */
    FT_DRAW_NO_BET(10014, "FT - Draw No Bet", 2, SELECTIONS_HOME_AWAY, "all|score|regular_play", 5, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 准确进球数
     */
    FT_EXACT_GOALS(10015, "FT - Exact Goals", 7, SELECTIONS_EXACT_GOALS_6_PLUS, "all|score|regular_play", 14, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 总进球数区间
     */
    FT_GOAL_RANGE(10016, "FT - Goal Range", 4, SELECTIONS_GOAL_RANGE_6_PLUS, "all|score|regular_play", 68, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 主队准确进球数
     */
    FT_TEAM1_EXACT_GOALS(10017, "FT - Team1 Exact Goals", 4, SELECTIONS_EXACT_GOALS_3_PLUS, "all|score|regular_play", 8, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 客队准确进球数
     */
    FT_TEAM2_EXACT_GOALS(10018, "FT - Team2 Exact Goals", 4, SELECTIONS_EXACT_GOALS_3_PLUS, "all|score|regular_play", 9, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 两队都进球
     */
    FT_BOTH_TEAM_SCORE(10019, "FT - Both Teams To Score", 2, SELECTIONS_YES_NO, "all|score|regular_play", 12, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 半/全场比分
     */
    FT_HT_FT_CORRECT_SCORE(10020, "FT - Halftime/Fulltime Correct Score", 46, SELECTIONS_HT_FT_CORRECT_SCORE, "all|score|regular_play", 103, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 半/全场
     */
    FT_HT_FT(10021, "FT - Halftime/Fulltime", 9, SELECTIONS_HT_FT, "all|score|regular_play", 104, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 最后进球队伍
     */
    FT_LAST_GOAL(10022, "FT - Last Goal", 3, SELECTIONS_TEAM, "all|score|regular_play", 149, "EU", G_GOAL, SOCCER.getId()),
    /**
     * {主队}进球单/双
     */
    FT_TEAM1_ODD_EVEN(10023, "FT - Team1 Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|regular_play", 78, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {主队}零失球
     */
    FT_TEAM1_CLEAN_SHEET(10024, "FT - Team1 Clean Sheet", 2, SELECTIONS_YES_NO, "all|score|regular_play", 81, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {主队}零失球获胜
     */
    FT_TEAM1_WIN_TO_NIL(10025, "FT - Team1 Win To Nil", 2, SELECTIONS_YES_NO, "all|score|regular_play", 82, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {主队}上/下半场全胜
     */
    FT_TEAM1_WIN_BOTH_HALVES(10026, "FT - Team1 To Win Both Halves", 2, SELECTIONS_YES_NO, "all|score|regular_play", 83, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {主队}任意半场获胜
     */
    FT_TEAM1_WIN_EITHER_HALF(10027, "FT - Team1 To Win Either Half", 2, SELECTIONS_YES_NO, "all|score|regular_play", 84, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {客队}进球单/双
     */
    FT_TEAM2_ODD_EVEN(10028, "FT - Team2 Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|regular_play", 92, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {客队}零失球
     */
    FT_TEAM2_CLEAN_SHEET(10029, "FT - Team2 Clean Sheet", 2, SELECTIONS_YES_NO, "all|score|regular_play", 79, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {客队}零失球获胜
     */
    FT_TEAM2_WIN_TO_NIL(10030, "FT - Team2 Win To Nil", 2, SELECTIONS_YES_NO, "all|score|regular_play", 80, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {客队}上/下半场全胜
     */
    FT_TEAM2_WIN_BOTH_HALVES(10031, "FT - Team2 To Win Both Halves", 2, SELECTIONS_YES_NO, "all|score|regular_play", 93, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {客队}任意半场获胜
     */
    FT_TEAM2_WIN_EITHER_HALF(10032, "FT - Team2 To Win Either Half", 2, SELECTIONS_YES_NO, "all|score|regular_play", 94, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 独赢 & 进球大小
     */
    FT_1X2_AND_TOTAL(10033, "FT - 1x2 & Total", 6, SELECTIONS_1X2_AND_TOTAL, "all|combo|regular_play", 13, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 独赢 & 两队都进球
     */
    FT_1X2_AND_BOTH_TEAM_SCORE(10034, "FT - 1x2 & Both Teams To Score", 6, SELECTIONS_1X2_AND_BOOLEAN, "all|combo|regular_play", 101, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 进球大小 & 两队都进球
     */
    FT_TOTAL_AND_BOTH_TEAM_SCORE(10035, "FT - Total & Both Teams To Score", 4, SELECTIONS_TOTAL_AND_BOOLEAN, "all|combo|regular_play", 102, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 双重机会 & 两队都进球
     */
    FT_DOUBLE_CHANCE_AND_BOTH_TEAM_SCORE(10036, "FT - Double Chance & Both Teams To Score", 6, SELECTIONS_DOUBLE_CHANCE_AND_BOOLEAN, "all|combo|regular_play", 107, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上/下半场两队都进球
     */
    FT_HALF1_HALF2_BOTH_TEAM_SCORE(10037, "FT - 1st/2nd half Both Teams To Score", 4, SELECTIONS_BOOLEAN_BOOLEAN, "all|combo|regular_play", 108, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 最多进球的半场
     */
    FT_HIGHEST_SCORING_HALF(10038, "FT - Highest Scoring Half", 3, SELECTIONS_HALF, "all|score|regular_play", 16, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 第{X}个进球
     */
    FT_XTH_GOAL(10039, "FT - {!goalnr} goal", 3, SELECTIONS_TEAM, "all|score|regular_play", 28, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 第{X}个进球何时发生（15分钟区间）
     */
    FT_TIME_XTH_GOAL_15MIN(10040, "FT - When will the {!goalnr} goal be scored (15 min interval)", 7, SELECTIONS_TIME_INTERVAL, "all|score|regular_play", 31, "EU", G_GOAL, SOCCER.getId()),
    /**
     * {主队}获胜退款
     */
    FT_TEAM1_NO_BET(10041, "FT - {$competitor1} no bet", 2, SELECTIONS_HOME_NO_BET, "all|score|regular_play", 77, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {主队}最高得分半场
     */
    FT_TEAM1_HIGHEST_SCORING_HALF(10042, "FT - {$competitor1} Highest Scoring Half", 3, SELECTIONS_HALF, "all|score|regular_play", 85, "EU", G_GOAL, SOCCER.getId()),
    /**
     * {主队}上/下半场均进球
     */
    FT_TEAM1_SCORE_BOTH_HALVES(10043, "FT - {$competitor1} to score in both halves", 2, SELECTIONS_YES_NO, "all|score|regular_play", 86, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {客队}获胜退款
     */
    FT_TEAM2_NO_BET(10044, "FT - {$competitor2} no bet", 2, SELECTIONS_AWAY_NO_BET, "all|score|regular_play", 91, "MY", G_GOAL, SOCCER.getId()),
    /**
     * {客队}最高得分半场
     */
    FT_TEAM2_HIGHEST_SCORING_HALF(10045, "FT - {$competitor2} Highest Scoring Half", 3, SELECTIONS_HALF, "all|score|regular_play", 95, "EU", G_GOAL, SOCCER.getId()),
    /**
     * {客队}上/下半场均进球
     */
    FT_TEAM2_SCORE_BOTH_HALVES(10046, "FT - {$competitor2} to score in both halves", 2, SELECTIONS_YES_NO, "all|score|regular_play", 96, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上/下半场进球数均大于{X.5}
     */
    FT_BOTH_HALVES_OVER(10047, "FT - Both halves over {total}", 2, SELECTIONS_YES_NO, "all|score|regular_play", 109, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上/下半场进球数均小于{X.5}
     */
    FT_BOTH_HALVES_UNDER(10048, "FT - Both halves under {total}", 2, SELECTIONS_YES_NO, "all|score|regular_play", 110, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 第{X}进球
     */
    FT_XTH_GOAL_WITHOUT_NONE(10049, "FT - {!goalnr} goal (without none)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|regular_play", 336, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 全场正确比分（多重投注）
     */
    FT_CORRECT_SCORE_MULTI_BET(10050, "FT - CorrectScore (multiBet)", 11, SELECTIONS_CORRECT_SCORE_MULTI_BET, "all|score|regular_play", 344, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 双重机会 & 进球大小
     */
    FT_DOUBLE_CHANCE_AND_TOTAL(10051, "FT - Double Chance & Total", 6, SELECTIONS_DOUBLE_CHANCE_AND_TOTAL, "all|combo|regular_play", 347, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 半/全场 & 进球大小
     */
    FT_HT_FT_AND_TOTAL(10052, "FT - Halftime/Fulltime & Total", 18, SELECTIONS_HT_FT_AND_TOTAL, "all|combo|regular_play", 348, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 半/全场 & 上半场进球大小
     */
    FT_HT_FT_AND_HALF1_TOTAL(10053, "FT - Halftime/Fulltime & 1st half Total", 18, SELECTIONS_HT_FT_AND_TOTAL, "all|combo|regular_play", 349, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 半/全场 单双
     */
    FT_HT_FT_ODD_EVEN(10054, "FT - Halftime/Fulltime Odd/Even", 4, SELECTIONS_HT_FT_ODD_EVEN, "all|score|regular_play", 350, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 进球单双 & 进球大小
     */
    FT_ODD_EVEN_AND_TOTAL(10055, "FT - Odd/Even & Total", 4, SELECTIONS_ODD_EVEN_AND_TOTAL, "all|combo|regular_play", 351, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 落后反超获胜
     */
    FT_WIN_FROM_BEHIND(10056, "FT - Win From Behind", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|regular_play", 352, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 独赢 & 最先进球球队
     */
    FT_1X2_AND_FIRST_SCORE_TEAM(10057, "FT - 1X2 & First Team To Score", 7, SELECTIONS_1X2_AND_FIRST_SCORE_TEAM, "all|combo|regular_play", 353, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 首个进球的半场
     */
    FT_FIRST_GOAL_HALF(10058, "FT - First Goal Half", 3, SELECTIONS_HALF_NO_GOAL, "all|score|regular_play", 354, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 主队首个进球半场
     */
    FT_TEAM1_FIRST_GOAL_HALF(10059, "FT - {$competitor1} First Goal Half", 3, SELECTIONS_HALF_NO_GOAL, "all|score|regular_play", 355, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 客队首个进球半场
     */
    FT_TEAM2_FIRST_GOAL_HALF(10060, "FT - {$competitor2} First Goal Half", 3, SELECTIONS_HALF_NO_GOAL, "all|score|regular_play", 356, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 先进{X}球的一方
     */
    FT_RACE_TO_XTH_GOAL(10061, "FT - Race To {!goalnr} Goal", 3, SELECTIONS_TEAM, "all|score|regular_play", 357, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 半/全场 & 准确进球数
     */
    FT_HT_FT_AND_EXACT_GOALS(10062, "FT - Halftime/Fulltime & Exact Goals", 54, SELECTIONS_HT_FT_AND_EXACT_GOALS, "all|combo|regular_play", 360, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 任何进球时分(5min)
     */
    FT_TIME_ANY_GOAL_5MIN(10063, "FT - When will any goal be scored (5 min interval)", 20, SELECTIONS_TIME_INTERVAL_FIVE, "all|score|regular_play", 361, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 第{X}个进球时分(5min)
     */
    FT_TIME_XTH_GOAL_5MIN(10064, "FT - When will the {!goalnr} goal be scored (5 min interval)", 20, SELECTIONS_TIME_INTERVAL_FIVE, "all|score|regular_play", 362, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 全场反波胆
     */
    FT_INVERSE_CORRECT_SCORE(10065, "FT - Inverse Correct Score", 26, SELECTIONS_CORRECT_SCORE_4, "all|score|regular_play", 367, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 独赢 & 进球单/双
     */
    FT_1X2_AND_ODD_EVEN(10066, "FT - 1x2 & Odd/Even", 5, SELECTIONS_1X2_AND_ODD_EVEN, "all|combo|regular_play", 384, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 赢得任意半场
     */
    FT_WIN_EITHER_HALF(10067, "FT - Win Either Half", 3, SELECTIONS_TEAM, "all|score|regular_play", 391, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 赢得所有半场
     */
    FT_WIN_BOTH_HALVES(10068, "FT - Win Both Halves", 3, SELECTIONS_TEAM, "all|score|regular_play", 392, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上/下半场双方均进球（两项）
     */
    FT_BOTH_TEAM_SCORE_BOTH_HALVES(10069, "FT - Both Teams To Score In Both Halves", 2, SELECTIONS_YES_NO, "all|score|regular_play", 393, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 双重机会 & 上半场双方都进球
     */
    FT_DOUBLE_CHANCE_H1_BOTH_TEAM_SCORE(10070, "FT - Double Chance & half1 Both Teams To Score", 6, SELECTIONS_DOUBLE_CHANCE_AND_BOOLEAN, "all|combo|regular_play", 1100421, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 双重机会 & 下半场双方都进球
     */
    FT_DOUBLE_CHANCE_H2_BOTH_TEAM_SCORE(10071, "FT - Double Chance & half2 Both Teams To Score", 6, SELECTIONS_DOUBLE_CHANCE_AND_BOOLEAN, "all|combo|regular_play", 1100422, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 进球大小 & 首次进球队伍
     */
    FT_TOTAL_AND_FIRST_SCORE_TEAM(10072, "FT - Total & First Team To Score", 5, SELECTIONS_TOTAL_AND_TEAM, "all|combo|regular_play", 1100423, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 双重机会 & 首次进球队伍
     */
    FT_DOUBLE_CHANCE_AND_FIRST_SCORE_TEAM(10073, "FT - Double Chance & First Team To Score", 7, SELECTIONS_DOUBLE_CHANCE_AND_TEAM, "all|combo|regular_play", 1100424, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 独赢 或 任何零失球
     */
    FT_1X2_OR_ANY_CLEAN_SHEET(10074, "FT - 1X2 or Any Clean Sheet", 6, SELECTIONS_1X2_OR_BOOLEAN, "all|combo|regular_play", 1100428, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 双方/一方/两者皆不得分
     */
    FT_TEAM_TO_SCORE(10075, "FT - Both/One/Neither Team To Score", 3, SELECTIONS_TEAM_TO_SCORE, "all|score|regular_play", 1100429, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 获胜球队
     */
    FT_TEAM_TO_WIN(10076, "FT - Team To Win", 6, SELECTIONS_TEAM_TO_WIN, "all|score|regular_play", 1100431, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 进球球队
     */
    FT_WHICH_TEAM_TO_SCORE(10077, "FT - Which Team To Score", 4, SELECTIONS_TEAM_WITH_BOTH, "all|score|regular_play", 1100432, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 首次进球时间三项
     */
    FT_1ST_GOAL_TIME_3WAY(10078, "FT - Time Of 1st Goal 3-way", 3, SELECTIONS_1ST_GOAL_TIME_3WAY, "all|score|regular_play", 1100434, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 全场大小 - 大/小 0.5
     */
    FT_TOTAL_HALF_GOAL(10079, "FT - 0.5 Total", 2, SELECTIONS_OVER_UNDER, "all|score|regular_play", 1100439, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 主队获胜
     */
    FT_HOME_WIN(10080, "FT - {$competitor1} Win", 2, SELECTIONS_YES_NO, "all|score|regular_play", 1100440, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 客队获胜
     */
    FT_AWAY_WIN(10081, "FT - {$competitor2} Win", 2, SELECTIONS_YES_NO, "all|score|regular_play", 1100441, "MY", G_GOAL, SOCCER.getId()),


    //-------------------------------------半场玩法-------------------------------------------------------------------
    /**
     * 半场独赢
     */
    HALF1_1X2(20001, "1st half - 1X2", 3, SELECTIONS_WINNER_1X2, "all|score|1st_half", 17, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 半场双重机会
     */
    HALF1_DOUBLE_CHANCE(20002, "1st half - Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|score|1st_half", 70, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 半场单双
     */
    HALF1_ODD_EVEN(20003, "1st half - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|1st_half", 42, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 半场让分
     */
    HALF1_ASIAN_HANDICAP(20004, "1st half - Asian Handicap", 2, SELECTIONS_HOME_AWAY, "all|score|1st_half", 19, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 半场大小
     */
    HALF1_OVER_UNDER(20005, "1st half - Over/Under", 2, SELECTIONS_OVER_UNDER, "all|score|1st_half", 18, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 半场主队大小
     */
    HALF1_TEAM1_OVER_UNDER(20006, "1st half - Team1 Over/Under", 2, SELECTIONS_OVER_UNDER, "all|score|1st_half", 87, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 半场客队大小
     */
    HALF1_TEAM2_OVER_UNDER(20007, "1st half - Team2 Over/Under", 2, SELECTIONS_OVER_UNDER, "all|score|1st_half", 97, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 半场正确比分
     */
    HALF1_CORRECT_SCORE(20008, "1st half - CorrectScore", 17, SELECTIONS_CORRECT_SCORE_3, "all|score|1st_half", 341, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 半场角球让分
     */
//    HALF1_CORNER_AH(20009, "1st half - Corner Handicap", 2, SELECTIONS_HOME_AWAY),
    /**
     * 半场角球大小
     */
//    HALF1_CORNER_OU(20010, "1st half - Corner Over/Under", 2, SELECTIONS_OVER_UNDER),
    /**
     * 上半场准确进球数
     */
    HALF1_EXACT_GOALS(20011, "1st half - Exact Goals", 4, SELECTIONS_EXACT_GOALS_3_PLUS, "all|score|1st_half", 23, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场平局退款
     */
    HALF1_DRAW_NO_BET(20012, "1st half - Draw No Bet", 2, SELECTIONS_HOME_AWAY, "all|score|1st_half", 43, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场两队都进球
     */
    HALF1_BOTH_TEAM_SCORE(20013, "1st half - Both Team to Score", 2, SELECTIONS_YES_NO, "all|score|1st_half", 24, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上半场独赢 & 上半场两队都进球
     */
    HALF1_1X2_AND_BOTH_TEAM_SCORE(20014, "1st half - 1x2 & Both Teams To Score", 6, SELECTIONS_1X2_AND_BOOLEAN, "all|combo|1st_half", 105, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场{主队}准确进球数
     */
    HALF1_TEAM1_EXACT_GOALS(20015, "1st half - {$competitor1} exact goals", 4, SELECTIONS_EXACT_GOALS_3_PLUS, "all|score|1st_half", 21, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场{客队}准确进球数
     */
    HALF1_TEAM2_EXACT_GOALS(20016, "1st half - {$competitor2} exact goals", 4, SELECTIONS_EXACT_GOALS_3_PLUS, "all|score|1st_half", 22, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场第{X}个进球
     */
    HALF1_XTH_GOAL(20017, "1st half - {!goalnr} goal", 3, SELECTIONS_TEAM, "all|score|1st_half", 30, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场让球胜平负
     */
    HALF1_THREE_WAY_HANDICAP(20018, "1st half - 3-Way Handicap", 3, SELECTIONS_WINNER_1X2, "all|score|1st_half", 69, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场{主队}零失球
     */
    HALF1_TEAM1_CLEAN_SHEET(20019, "1st half - {$competitor1} clean sheet", 2, SELECTIONS_YES_NO, "all|score|1st_half", 90, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上半场{客队}零失球
     */
    HALF1_TEAM2_CLEAN_SHEET(20020, "1st half - {$competitor2} clean sheet", 2, SELECTIONS_YES_NO, "all|score|1st_half", 100, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上半场 独赢 & 进球大小
     */
    HALF1_1X2_AND_TOTAL(20021, "1st half - 1x2 & Total", 6, SELECTIONS_1X2_AND_TOTAL, "all|combo|1st_half", 345, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场 净胜分
     */
    HALF1_WINNING_MARGIN(20022, "1st half - Winning Margin", 6, SELECTIONS_WINNING_MARGIN_2, "all|score|1st_half", 359, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场反波胆
     */
    HALF1_INVERSE_CORRECT_SCORE(20023, "1st half - Inverse Correct Score", 17, SELECTIONS_CORRECT_SCORE_3, "all|score|1st_half", 368, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场{主队}进球单/双
     */
    HALF1_TEAM1_ODD_EVEN(20024, "1st half - {$competitor1} Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|1st_half", 373, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上半场{客队}进球单/双
     */
    HALF1_TEAM2_ODD_EVEN(20025, "1st half - {$competitor2} Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|1st_half", 374, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上半场{主队}零失球获胜
     */
    HALF1_TEAM1_WIN_TO_NIL(20026, "1st half - {$competitor1} Win To Nil", 2, SELECTIONS_YES_NO, "all|score|1st_half", 375, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上半场{客队}零失球获胜
     */
    HALF1_TEAM2_WIN_TO_NIL(20027, "1st half - {$competitor2} Win To Nil", 2, SELECTIONS_YES_NO, "all|score|1st_half", 376, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上半场最后进球球队
     */
    HALF1_LAST_GOAL(20028, "1st half - Last Goal", 3, SELECTIONS_TEAM, "all|score|1st_half", 396, null, G_GOAL, SOCCER.getId()),
    /**
     * 上半场 - 双重机会 & 双方都进球
     */
    HALF1_DOUBLE_CHANCE_BOTH_TEAM_SCORE(20029, "1st half - Double Chance & Both Teams To Score", 6, SELECTIONS_DOUBLE_CHANCE_AND_BOOLEAN, "all|combo|1st_half", 1100425, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场 - 最先进球 / 最后进球
     */
    HALF1_FIRST_LAST_GOAL(20030, "1st half - First Goal/Last Goal", 5, SELECTIONS_FIRST_LAST_GOAL, "all|combo|1st_half", 1100433, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 上半场 - 第一颗进球（两项）
     */
    HALF1_1ST_GOAL_WITHOUT_NONE(20031, "1st half - 1st goal (without none)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|1st_half", 1100436, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 上半场 - 第一颗进球（三项）
     */
    HALF1_1ST_GOAL(20032, "1st half - 1st goal", 3, SELECTIONS_TEAM, "all|score|1st_half", 1100435, "EU", G_GOAL, SOCCER.getId()),


    //-------------------------------------15分钟玩法-------------------------------------------------------------------
    /**
     * 15min独赢（a-b）
     */
    FIFTEEN_1X2_ATOB(30001, "15min - 1X2({from}-{to})", 3, SELECTIONS_WINNER_1X2, "all|score|15_min", 32, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 15min让分（a-b）
     */
    FIFTEEN_AH_ATOB(30002, "15min - AH({from}-{to})", 2, SELECTIONS_HOME_AWAY, "all|score|15_min", 33, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 15min大小（a-b）
     */
    FIFTEEN_OU_ATOB(30003, "15min - OU({from}-{to})", 2, SELECTIONS_OVER_UNDER, "all|score|15_min", 34, "MY", G_GOAL, SOCCER.getId()),


    //-------------------------------------角球玩法-------------------------------------------------------------------
    /**
     * 全场角球独赢
     */
    CORNER_1X2(40001, "Corner - 1X2", 3, SELECTIONS_WINNER_1X2, "all|regular_play|corners", 111, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 全场角球让分
     */
    CORNER_HANDICAP(40002, "Corner - Handicap", 2, SELECTIONS_HOME_AWAY, "all|regular_play|corners", 113, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 全场角球大小
     */
    CORNER_TOTAL(40003, "Corner - Total", 2, SELECTIONS_OVER_UNDER, "all|regular_play|corners", 114, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 全场角球单双
     */
    CORNER_OE(40004, "Corner - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|regular_play|corners", 118, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 主队全场角球大小
     */
    CORNER_TEAM1_TOTAL(40005, "Corner - Team1 Total", 2, SELECTIONS_OVER_UNDER, "all|regular_play|corners", 115, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 客队全场角球大小
     */
    CORNER_TEAM2_TOTAL(40006, "Corner - Team2 Total", 2, SELECTIONS_OVER_UNDER, "all|regular_play|corners", 116, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 全场最后一个角球
     */
    CORNER_LAST_CORNER(40007, "Corner - Last Corner", 3, SELECTIONS_TEAM, "all|regular_play|corners", 112, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 半场角球独赢
     */
    CORNER_H1_1X2(40008, "Corner - 1st half 1X2", 3, SELECTIONS_WINNER_1X2, "all|1st_half|corners", 119, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 半场角球让分
     */
    CORNER_H1_HANDICAP(40009, "Corner - 1st half Handicap", 2, SELECTIONS_HOME_AWAY, "all|1st_half|corners", 121, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 半场角球大小
     */
    CORNER_H1_TOTAL(40010, "Corner - 1st half Total", 2, SELECTIONS_OVER_UNDER, "all|1st_half|corners", 122, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 半场角球单双
     */
    CORNER_H1_OE(40011, "Corner - 1st half Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|1st_half|corners", 229, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 主队半场角球大小
     */
    CORNER_H1_TEAM1_TOTAL(40012, "Corner - 1st half Team1 Total", 2, SELECTIONS_OVER_UNDER, "all|1st_half|corners", 123, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 客队半场角球大小
     */
    CORNER_H1_TEAM2_TOTAL(40013, "Corner - 1st half Team2 Total", 2, SELECTIONS_OVER_UNDER, "all|1st_half|corners", 124, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 角球总数区间
     */
    CORNER_RANGE(40014, "Corner - range", 3, SELECTIONS_CORNER_RANGE_12_PLUS, "all|regular_play|corners", 117, "EU", G_CORNER, SOCCER.getId()),
    /**
     * {主队}角球总数区间
     */
    CORNER_TEAM1_RANGE(40015, "Corner - {$competitor1} range", 4, SELECTIONS_CORNER_RANGE_7_PLUS, "all|regular_play|corners", 226, "EU", G_CORNER, SOCCER.getId()),
    /**
     * {客队}角球总数区间
     */
    CORNER_TEAM2_RANGE(40016, "Corner - {$competitor2} range", 4, SELECTIONS_CORNER_RANGE_7_PLUS, "all|regular_play|corners", 227, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 谁先获得{X}个角球
     */
    CORNER_RACE_XTH_CORNERS(40017, "Corner - Race to {!cornernr} corners", 3, SELECTIONS_TEAM, "all|regular_play|corners", 125, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 第{X}个角球
     */
    CORNER_XTH_CORNER(40018, "Corner - {!cornernr} corner", 3, SELECTIONS_TEAM, "all|regular_play|corners", 225, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 上半场角球总数区间
     */
    CORNER_H1_RANGE(40019, "Corner - 1st half range", 3, SELECTIONS_CORNER_RANGE_7_PLUS_2, "all|1st_half|corners", 228, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 上半场谁先获得{X}个角球
     */
    CORNER_H1_RACE_XTH_CORNERS(40020, "Corner - 1st half race to {!cornernr} corners", 3, SELECTIONS_TEAM, "all|1st_half|corners", 230, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 上半场第{X}个角球
     */
    CORNER_H1_XTH_CORNER(40021, "Corner - 1st half {!cornernr} corner", 3, SELECTIONS_TEAM, "all|1st_half|corners", 120, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 15分钟角球-独赢({a}-{b})
     */
    CORNER_FIFTEEN_1X2(40022, "Corner - 15min 1x2({from}-{to})", 3, SELECTIONS_WINNER_1X2, "all|15_min|corners", 231, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 15分钟角球-让球({a}-{b})
     */
    CORNER_FIFTEEN_AH(40023, "Corner - 15min handicap({from}-{to})", 2, SELECTIONS_HOME_AWAY, "all|15_min|corners", 232, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 15分钟角球-大小({a}-{b})
     */
    CORNER_FIFTEEN_TOTAL(40024, "Corner - 15min total({from}-{to})", 2, SELECTIONS_OVER_UNDER, "all|15_min|corners", 233, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 加时赛-角球大小
     */
    CORNER_OT_TOTAL(40025, "Corner - overtime total", 2, SELECTIONS_OVER_UNDER, "all|ot|corners", 331, "MY", EX_CORNER, SOCCER.getId()),
    /**
     * 角球-双重机会
     */
    CORNER_DOUBLE_CHANCE(40026, "Corner - Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|regular_play|corners", 385, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 角球-波胆
     */
    CORNER_CORRECT_SCORE(40027, "Corner - CorrectScore", 26, SELECTIONS_CORRECT_SCORE_4, "all|regular_play|corners", 386, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 角球大小盘三项
     */
    CORNER_THREE_WAY_TOTAL(40028, "Corner - 3-Way Total", 3, SELECTIONS_OVER_UNDER_3, "all|regular_play|corners", 387, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 上半场最后角球
     */
    CORNER_H1_LAST_CORNER(40029, "Corner - 1st half Last Corner", 3, SELECTIONS_TEAM, "all|1st_half|corners", 394, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 上半场{主队}角球总数区间
     */
    CORNER_H1_TEAM1_RANGE(40030, "Corner - 1st half {$competitor1} range", 4, SELECTIONS_CORNER_RANGE_5_PLUS, "all|1st_half|corners", 1100400, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 上半场{客队}角球总数区间
     */
    CORNER_H1_TEAM2_RANGE(40031, "Corner - 1st half {$competitor2} range", 4, SELECTIONS_CORNER_RANGE_5_PLUS, "all|1st_half|corners", 1100401, "EU", G_CORNER, SOCCER.getId()),
    /**
     * 加时赛角球独赢
     */
    CORNER_OT_1X2(40032, "Corner - overtime 1X2", 3, SELECTIONS_WINNER_1X2, "all|ot|corners", 1100413, "EU", EX_CORNER, SOCCER.getId()),
    /**
     * 加时赛角球让分
     */
    CORNER_OT_HANDICAP(40033, "Corner - overtime Handicap", 2, SELECTIONS_HOME_AWAY, "all|ot|corners", 1100414, "MY", EX_CORNER, SOCCER.getId()),
    /**
     * 加时赛上半场角球独赢
     */
    CORNER_OT_H1_1X2(40034, "Corner - overtime 1st half 1X2", 3, SELECTIONS_WINNER_1X2, "all|ot|corners", 1100415, "EU", EX_CORNER, SOCCER.getId()),
    /**
     * 加时赛上半场角球让分
     */
    CORNER_OT_H1_HANDICAP(40035, "Corner - overtime 1st half Handicap", 2, SELECTIONS_HOME_AWAY, "all|ot|corners", 1100416, "MY", EX_CORNER, SOCCER.getId()),
    /**
     * 加时赛上半场角球大小
     */
    CORNER_OT_H1_TOTAL(40036, "Corner - overtime 1st half Total", 2, SELECTIONS_OVER_UNDER, "all|ot|corners", 1100417, "MY", EX_CORNER, SOCCER.getId()),
    /**
     * 加时赛角球单双
     */
    CORNER_OT_OE(40037, "Corner - overtime Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|ot|corners", 1100418, "MY", EX_CORNER, SOCCER.getId()),
    /**
     * 加时赛第{X}个角球
     */
    CORNER_OT_XTH_CORNER(40038, "Corner - overtime {!cornernr} corner", 3, SELECTIONS_TEAM, "all|ot|corners", 1100419, "EU", EX_CORNER, SOCCER.getId()),
    /**
     * 下半场第一颗角球（两项）
     */
    CORNER_H2_1ST_CORNER_WITHOUT_NONE(40039, "Corner - 2nd half 1st corner (without none)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|2nd_half|corners", 1100442, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 全场最后一个角球（两项）
     */
    CORNER_LAST_CORNER_WITHOUT_NONE(40040, "Corner - Last Corner (without none)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|regular_play|corners", 1100443, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 第一颗角球（两项）
     */
    CORNER_1ST_CORNER_WITHOUT_NONE(40041, "Corner - 1st corner (without none)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|regular_play|corners", 1100445, "MY", G_CORNER, SOCCER.getId()),
    /**
     * 第一颗角球（三项）
     */
    CORNER_1ST_CORNER(40042, "Corner - 1st corner", 3, SELECTIONS_TEAM, "all|regular_play|corners", 1100444, "EU", G_CORNER, SOCCER.getId()),


    //-------------------------------------罚牌玩法-------------------------------------------------------------------
    /**
     * 全场罚牌独赢
     */
    BOOKING_1X2(50001, "Booking - 1X2", 3, SELECTIONS_WINNER_1X2, "all|regular_play|bookings", 310, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 全场罚牌让分
     */
    BOOKING_HANDICAP(50002, "Booking - Handicap", 2, SELECTIONS_HOME_AWAY, "all|regular_play|bookings", 306, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 全场罚牌单双
     */
    BOOKING_OE(50003, "Booking - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|regular_play|bookings", 312, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 全场罚牌大小
     */
    BOOKING_TOTAL(50004, "Booking - Total", 2, SELECTIONS_OVER_UNDER, "all|regular_play|bookings", 307, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 半场罚牌独赢
     */
    BOOKING_H1_1X2(50005, "Booking - 1st half 1X2", 3, SELECTIONS_WINNER_1X2, "all|1st_half|bookings", 311, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 半场罚牌让分
     */
    BOOKING_H1_HANDICAP(50006, "Booking - 1st half Handicap", 2, SELECTIONS_HOME_AWAY, "all|1st_half|bookings", 308, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 半场罚牌单双
     */
    BOOKING_H1_OE(50007, "Booking - 1st half Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|1st_half|bookings", 313, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 半场罚牌大小
     */
    BOOKING_H1_TOTAL(50008, "Booking - 1st half Total", 2, SELECTIONS_OVER_UNDER, "all|1st_half|bookings", 309, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 比赛中出现红牌
     */
    BOOKING_SENDING_OFF(50009, "Booking - sending off", 2, SELECTIONS_YES_NO, "all|regular_play|bookings", 138, "MY", G_RC, SOCCER.getId()),
    /**
     * {主队}获得红牌
     */
    BOOKING_TEAM1_SENDING_OFF(50010, "Booking - {$competitor1} sending off", 2, SELECTIONS_YES_NO, "all|regular_play|bookings", 139, "MY", G_RC, SOCCER.getId()),
    /**
     * {客队}获得红牌
     */
    BOOKING_TEAM2_SENDING_OFF(50011, "Booking - {$competitor2} sending off", 2, SELECTIONS_YES_NO, "all|regular_play|bookings", 140, "MY", G_RC, SOCCER.getId()),
    /**
     * 第{X}张罚牌
     */
    BOOKING_XTH_BOOKING(50012, "Booking - {!bookingnr} booking", 3, SELECTIONS_TEAM, "all|regular_play|bookings", 224, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * {主队}罚牌大小
     */
    BOOKING_TEAM1_TOTAL(50013, "Booking - {$competitor1} total", 2, SELECTIONS_OVER_UNDER, "all|regular_play|bookings", 314, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * {客队}罚牌大小
     */
    BOOKING_TEAM2_TOTAL(50014, "Booking - {$competitor2} total", 2, SELECTIONS_OVER_UNDER, "all|regular_play|bookings", 315, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 准确罚牌分数
     */
    BOOKING_EXACT_BOOKINGS(50015, "Booking - exact bookings", 10, SELECTIONS_EXACT_BOOKINGS_12_PLUS, "all|regular_play|bookings", 318, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * {主队}准确罚牌分数
     */
    BOOKING_TEAM1_EXACT_BOOKINGS(50016, "Booking - {$competitor1} exact bookings", 4, SELECTIONS_EXACT_BOOKINGS_4_PLUS, "all|regular_play|bookings", 320, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * {客队}准确罚牌分数
     */
    BOOKING_TEAM2_EXACT_BOOKINGS(50017, "Booking - {$competitor2} exact bookings", 4, SELECTIONS_EXACT_BOOKINGS_4_PLUS, "all|regular_play|bookings", 321, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 上半场{主队}罚牌大小
     */
    BOOKING_H1_TEAM1_TOTAL(50018, "Booking - 1st half {$competitor1} total", 2, SELECTIONS_OVER_UNDER, "all|1st_half|bookings", 316, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 上半场{客队}罚牌大小
     */
    BOOKING_H1_TEAM2_TOTAL(50019, "Booking - 1st half {$competitor2} total", 2, SELECTIONS_OVER_UNDER, "all|1st_half|bookings", 317, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 上半场准确罚牌分数
     */
    BOOKING_H1_EXACT_BOOKINGS(50020, "Booking - 1st half exact bookings", 7, SELECTIONS_EXACT_BOOKINGS_6_PLUS, "all|1st_half|bookings", 319, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 上半场{主队}准确罚牌分数
     */
    BOOKING_H1_TEAM1_EXACT_BOOKINGS(50021, "Booking - 1st half {$competitor1} exact bookings", 4, SELECTIONS_EXACT_BOOKINGS_3_PLUS, "all|1st_half|bookings", 322, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 上半场{客队}准确罚牌分数
     */
    BOOKING_H1_TEAM2_EXACT_BOOKINGS(50022, "Booking - 1st half {$competitor2} exact bookings", 4, SELECTIONS_EXACT_BOOKINGS_3_PLUS, "all|1st_half|bookings", 323, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 黄牌让分
     */
    BOOKING_HANDICAP_YELLOW_CARDS(50023, "Booking - yellow cards handicap", 2, SELECTIONS_HOME_AWAY, "all|regular_play|bookings", 324, "MY", G_YC, SOCCER.getId()),
    /**
     * 黄牌大小
     */
    BOOKING_TOTAL_YELLOW_CARDS(50024, "Booking - yellow cards total", 2, SELECTIONS_OVER_UNDER, "all|regular_play|bookings", 325, "MY", G_YC, SOCCER.getId()),
    /**
     * 黄牌独赢
     */
    BOOKING_1X2_YELLOW_CARDS(50025, "Booking - yellow cards 1X2", 3, SELECTIONS_WINNER_1X2, "all|regular_play|bookings", 326, "EU", G_YC, SOCCER.getId()),
    /**
     * 上半场黄牌让分
     */
    BOOKING_H1_HANDICAP_YELLOW_CARDS(50026, "Booking - 1st half yellow cards handicap", 2, SELECTIONS_HOME_AWAY, "all|1st_half|bookings", 327, "MY", G_YC, SOCCER.getId()),
    /**
     * 上半场黄牌大小
     */
    BOOKING_H1_TOTAL_YELLOW_CARDS(50027, "Booking - 1st half yellow cards total", 2, SELECTIONS_OVER_UNDER, "all|1st_half|bookings", 328, "MY", G_YC, SOCCER.getId()),
    /**
     * 上半场黄牌独赢
     */
    BOOKING_H1_1X2_YELLOW_CARDS(50028, "Booking - 1st half yellow cards 1X2", 3, SELECTIONS_WINNER_1X2, "all|1st_half|bookings", 329, "EU", G_YC, SOCCER.getId()),
    /**
     * 15分钟罚牌-独赢({a}-{b})
     */
    BOOKING_FIFTEEN_1X2(50029, "Booking - 15min 1x2({from}-{to})", 3, SELECTIONS_WINNER_1X2, "all|15_min|bookings", 370, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 15分钟罚牌-让球({a}-{b})
     */
    BOOKING_FIFTEEN_AH(50030, "Booking - 15min handicap({from}-{to})", 2, SELECTIONS_HOME_AWAY, "all|15_min|bookings", 371, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 15分钟罚牌-大小({a}-{b})
     */
    BOOKING_FIFTEEN_TOTAL(50031, "Booking - 15min total({from}-{to})", 2, SELECTIONS_OVER_UNDER, "all|15_min|bookings", 372, "MY", G_BOOKING, SOCCER.getId()),
    /**
     * 罚牌双重机会
     */
    BOOKING_DOUBLE_CHANCE(50032, "Booking - Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|regular_play|bookings", 389, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 上半场最后得牌
     */
    BOOKING_H1_LAST_BOOKING(50033, "Booking - 1st half Last Booking", 3, SELECTIONS_TEAM, "all|1st_half|bookings", 395, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 上半场罚牌双重机会
     */
    BOOKING_H1_DOUBLE_CHANCE(50034, "Booking - 1st half Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|1st_half|bookings", 397, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 全场最后得牌
     */
    BOOKING_LAST_BOOKING(50035, "Booking - Last Booking", 3, SELECTIONS_TEAM, "all|regular_play|bookings", 398, "EU", G_BOOKING, SOCCER.getId()),
    /**
     * 上半场比赛中出现红牌
     */
    BOOKING_H1_SENDING_OFF(50036, "Booking - 1st half sending off", 2, SELECTIONS_YES_NO, "all|1st_half|bookings", 1100402, "MY", G_RC, SOCCER.getId()),
    /**
     * 上半场{主队}获得红牌
     */
    BOOKING_H1_TEAM1_SENDING_OFF(50037, "Booking - 1st half {$competitor1} sending off", 2, SELECTIONS_YES_NO, "all|1st_half|bookings", 1100403, "MY", G_RC, SOCCER.getId()),
    /**
     * 上半场{客队}获得红牌
     */
    BOOKING_H1_TEAM2_SENDING_OFF(50038, "Booking - 1st half {$competitor2} sending off", 2, SELECTIONS_YES_NO, "all|1st_half|bookings", 1100404, "MY", G_RC, SOCCER.getId()),
    /**
     * 加时赛罚牌独赢
     */
    BOOKING_OT_1X2(50039, "Booking - overtime 1X2", 3, SELECTIONS_WINNER_1X2, "all|ot|bookings", 1100405, "EU", EX_BOOKING, SOCCER.getId()),
    /**
     * 加时赛罚牌让分
     */
    BOOKING_OT_HANDICAP(50040, "Booking - overtime Handicap", 2, SELECTIONS_HOME_AWAY, "all|ot|bookings", 1100406, "MY", EX_BOOKING, SOCCER.getId()),
    /**
     * 加时赛罚牌大小
     */
    BOOKING_OT_TOTAL(50041, "Booking - overtime Total", 2, SELECTIONS_OVER_UNDER, "all|ot|bookings", 1100407, "MY", EX_BOOKING, SOCCER.getId()),
    /**
     * 加时赛上半场罚牌独赢
     */
    BOOKING_OT_H1_1X2(50042, "Booking - overtime 1st half 1X2", 3, SELECTIONS_WINNER_1X2, "all|ot|bookings", 1100408, "EU", EX_BOOKING, SOCCER.getId()),
    /**
     * 加时赛上半场罚牌让分
     */
    BOOKING_OT_H1_HANDICAP(50043, "Booking - overtime 1st half Handicap", 2, SELECTIONS_HOME_AWAY, "all|ot|bookings", 1100409, "MY", EX_BOOKING, SOCCER.getId()),
    /**
     * 加时赛上半场罚牌大小
     */
    BOOKING_OT_H1_TOTAL(50044, "Booking - overtime 1st half Total", 2, SELECTIONS_OVER_UNDER, "all|ot|bookings", 1100410, "MY", EX_BOOKING, SOCCER.getId()),
    /**
     * 加时赛罚牌单双
     */
    BOOKING_OT_OE(50045, "Booking - overtime Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|ot|bookings", 1100411, "MY", EX_BOOKING, SOCCER.getId()),
    /**
     * 加时赛第{X}张罚牌
     */
    BOOKING_OT_XTH_BOOKING(50046, "Booking - overtime {!bookingnr} booking", 3, SELECTIONS_TEAM, "all|ot|bookings", 1100412, "EU", EX_BOOKING, SOCCER.getId()),


    //-------------------------------------下半场玩法-------------------------------------------------------------------
    /**
     * 下半场独赢
     */
    HALF2_1X2(60001, "2nd half - 1X2", 3, SELECTIONS_WINNER_1X2, "all|score|2nd_half", 25, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场双重机会
     */
    HALF2_DOUBLE_CHANCE(60002, "2nd half - Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|score|2nd_half", 72, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场大小
     */
    HALF2_TOTAL(60003, "2nd half - Total", 2, SELECTIONS_OVER_UNDER, "all|score|2nd_half", 26, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场进球单/双
     */
    HALF2_ODD_EVEN(60004, "2nd half - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|2nd_half", 75, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场准确进球数
     */
    HALF2_EXACT_GOALS(60005, "2nd half - Exact Goals", 4, SELECTIONS_EXACT_GOALS_3_PLUS, "all|score|2nd_half", 73, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场正确比分
     */
    HALF2_CORRECT_SCORE(60006, "2nd half - Correct Score", 17, SELECTIONS_CORRECT_SCORE_3, "all|score|2nd_half", 342, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场平局退款
     */
    HALF2_DRAW_NO_BET(60007, "2nd half - Draw No Bet", 2, SELECTIONS_HOME_AWAY, "all|score|2nd_half", 142, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场两队都进球
     */
    HALF2_BOTH_TEAM_SCORE(60008, "2nd half - Both Teams To Score", 2, SELECTIONS_YES_NO, "all|score|2nd_half", 76, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场独赢 & 下半场两队都进球
     */
    HALF2_1X2_AND_BOTH_TEAM_SCORE(60009, "2nd half - 1x2 & Both Teams To Score", 6, SELECTIONS_1X2_AND_BOOLEAN, "all|combo|2nd_half", 106, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场让球胜平负
     */
    HALF2_THREE_WAY_HANDICAP(60010, "2nd half - 3-Way Handicap", 3, SELECTIONS_WINNER_1X2, "all|score|2nd_half", 71, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{主队}进球大小
     */
    HALF2_TEAM1_TOTAL(60011, "2nd half - {$competitor1} total", 2, SELECTIONS_OVER_UNDER, "all|score|2nd_half", 88, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{主队}零失球
     */
    HALF2_TEAM1_CLEAN_SHEET(60012, "2nd half - {$competitor1} clean sheet", 2, SELECTIONS_YES_NO, "all|score|2nd_half", 89, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{客队}进球大小
     */
    HALF2_TEAM2_TOTAL(60013, "2nd half - {$competitor2} total", 2, SELECTIONS_OVER_UNDER, "all|score|2nd_half", 98, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{客队}零失球
     */
    HALF2_TEAM2_CLEAN_SHEET(60014, "2nd half - {$competitor2} clean sheet", 2, SELECTIONS_YES_NO, "all|score|2nd_half", 99, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场让球
     */
    HALF2_ASIAN_HANDICAP(60015, "2nd half - Asian Handicap", 2, SELECTIONS_HOME_AWAY, "all|score|2nd_half", 143, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场 独赢 & 进球大小
     */
    HALF2_1X2_AND_TOTAL(60016, "2nd half - 1x2 & Total", 6, SELECTIONS_1X2_AND_TOTAL, "all|combo|2nd_half", 346, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场反波胆
     */
    HALF2_INVERSE_CORRECT_SCORE(60017, "2nd half - Inverse Correct Score", 17, SELECTIONS_CORRECT_SCORE_3, "all|score|2nd_half", 369, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{主队}进球单/双
     */
    HALF2_TEAM1_ODD_EVEN(60018, "2nd half - {$competitor1} Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|2nd_half", 377, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{客队}进球单/双
     */
    HALF2_TEAM2_ODD_EVEN(60019, "2nd half - {$competitor2} Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|2nd_half", 378, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{主队}准确进球数
     */
    HALF2_TEAM1_EXACT_GOALS(60020, "2nd half - {$competitor1} exact goals", 4, SELECTIONS_EXACT_GOALS_3_PLUS, "all|score|2nd_half", 379, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{客队}准确进球数
     */
    HALF2_TEAM2_EXACT_GOALS(60021, "2nd half - {$competitor2} exact goals", 4, SELECTIONS_EXACT_GOALS_3_PLUS, "all|score|2nd_half", 380, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{主队}零失球获胜
     */
    HALF2_TEAM1_WIN_TO_NIL(60022, "2nd half - {$competitor1} Win To Nil", 2, SELECTIONS_YES_NO, "all|score|2nd_half", 381, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场{客队}零失球获胜
     */
    HALF2_TEAM2_WIN_TO_NIL(60023, "2nd half - {$competitor2} Win To Nil", 2, SELECTIONS_YES_NO, "all|score|2nd_half", 382, "MY", G_GOAL, SOCCER.getId()),
    /**
     * 下半场 净胜分
     */
    HALF2_WINNING_MARGIN(60024, "2nd half - Winning Margin", 6, SELECTIONS_WINNING_MARGIN_2, "all|score|2nd_half", 383, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场 - 双重机会 & 双方都进球
     */
    HALF2_DOUBLE_CHANCE_BOTH_TEAM_SCORE(60025, "2nd half - Double Chance & Both Teams To Score", 6, SELECTIONS_DOUBLE_CHANCE_AND_BOOLEAN, "all|combo|2nd_half", 1100426, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场 - 第一颗进球（三项）
     */
    HALF2_1ST_GOAL(60026, "2nd half - 1st goal", 3, SELECTIONS_TEAM, "all|score|2nd_half", 1100437, "EU", G_GOAL, SOCCER.getId()),
    /**
     * 下半场 - 第一颗进球（两项）
     */
    HALF2_1ST_GOAL_WITHOUT_NONE(60027, "2nd half - 1st goal (without none)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|2nd_half", 1100438, "MY", G_GOAL, SOCCER.getId()),


    //-------------------------------------加时赛玩法-------------------------------------------------------------------
    /**
     * 加时赛-独赢
     */
    OVERTIME_1X2(70001, "Overtime - 1x2", 3, SELECTIONS_WINNER_1X2, "all|score|ot", 126, "EU", EX_GOAL, SOCCER.getId()),
    /**
     * 加时赛-让球
     */
    OVERTIME_HANDICAP(70002, "Overtime - handicap", 2, SELECTIONS_HOME_AWAY, "all|score|ot", 128, "MY", EX_GOAL, SOCCER.getId()),
    /**
     * 加时赛-大小
     */
    OVERTIME_TOTAL(70003, "Overtime - total", 2, SELECTIONS_OVER_UNDER, "all|score|ot", 127, "MY", EX_GOAL, SOCCER.getId()),
    /**
     * 加时赛-单/双
     */
    OVERTIME_ODD_EVEN(70004, "Overtime - odd/even", 2, SELECTIONS_ODD_EVEN, "all|score|ot", 330, "MY", EX_GOAL, SOCCER.getId()),
    /**
     * 加时赛-正确比分
     */
    OVERTIME_CORRECT_SCORE(70005, "Overtime - correct score", 17, SELECTIONS_CORRECT_SCORE_3, "all|score|ot", 343, "EU", EX_GOAL, SOCCER.getId()),
    /**
     * 加时赛是否进球
     */
    OVERTIME_GOAL_OR_NOT(70006, "Overtime - goal or not", 2, SELECTIONS_YES_NO, "all|score|ot", 234, "MY", EX_GOAL, SOCCER.getId()),
    /**
     * 加时赛-第{X}个进球
     */
    OVERTIME_XTH_GOAL(70007, "Overtime - {!goalnr} goal", 3, SELECTIONS_TEAM, "all|score|ot", 235, "EU", EX_GOAL, SOCCER.getId()),
    /**
     * 加时赛-上半场独赢
     */
    OVERTIME_H1_1X2(70008, "Overtime 1st half - 1x2", 3, SELECTIONS_WINNER_1X2, "all|score|ot_1st_half", 129, "EU", EX_GOAL, SOCCER.getId()),
    /**
     * 加时赛-上半场让球
     */
    OVERTIME_H1_HANDICAP(70009, "Overtime 1st half - handicap", 2, SELECTIONS_HOME_AWAY, "all|score|ot_1st_half", 130, "MY", EX_GOAL, SOCCER.getId()),
    /**
     * 加时赛-上半场大小
     */
    OVERTIME_H1_TOTAL(70010, "Overtime 1st half - total", 2, SELECTIONS_OVER_UNDER, "all|score|ot_1st_half", 332, "MY", EX_GOAL, SOCCER.getId()),


    //-------------------------------------点球大战玩法-------------------------------------------------------------------
    /**
     * 点球大战-独赢
     */
    PENALTY_SHOOTOUT_WINNER(80001, "Penalty shootout - winner", 2, SELECTIONS_HOME_AWAY, "all|score|pen_so", 132, "MY", PK, SOCCER.getId()),
    /**
     * 点球大战-让球
     */
    PENALTY_SHOOTOUT_HANDICAP(80002, "Penalty shootout - handicap", 2, SELECTIONS_HOME_AWAY, "all|score|pen_so", 334, "MY", PK, SOCCER.getId()),
    /**
     * 点球大战-大小
     */
    PENALTY_SHOOTOUT_TOTAL(80003, "Penalty shootout - total", 2, SELECTIONS_OVER_UNDER, "all|score|pen_so", 134, "MY", PK, SOCCER.getId()),
    /**
     * 点球大战-单/双
     */
    PENALTY_SHOOTOUT_ODD_EVEN(80004, "Penalty shootout - odd/even", 2, SELECTIONS_ODD_EVEN, "all|score|pen_so", 240, "MY", PK, SOCCER.getId()),
    /**
     * 点球大战-正确比分
     */
    PENALTY_SHOOTOUT_CORRECT_SCORE(80005, "Penalty shootout - correct score", 2, SELECTIONS_PEN_SO_CORRECT_SCORE, "all|score|pen_so", 241, "EU", PK, SOCCER.getId()),
    /**
     * 点球大战-净胜分
     */
    PENALTY_SHOOTOUT_WINNING_MARGIN(80006, "Penalty shootout - winning margin", 6, SELECTIONS_WINNING_MARGIN_3_WITHOUT_DRAW, "all|score|pen_so", 238, "EU", PK, SOCCER.getId()),
    /**
     * 点球大战-准确进球数
     */
    PENALTY_SHOOTOUT_EXACT_GOALS(80007, "Penalty shootout - exact goals", 7, SELECTIONS_PEN_SO_EXACT_GOALS_10_PLUS, "all|score|pen_so", 239, "EU", PK, SOCCER.getId()),
    /**
     * 点球大战-第{X}个进球
     */
    PENALTY_SHOOTOUT_XTH_GOAL(80008, "Penalty shootout - {!goalnr} goal", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|pen_so", 237, "EU", PK, SOCCER.getId()),
    /**
     * 是否进行点球大战
     */
    PENALTY_SHOOTOUT_OR_NOT(80009, "Will there be a penalty shootout", 2, SELECTIONS_YES_NO, "all|score|incl_ot", 131, "MY", PK, SOCCER.getId()),
    /**
     * 点球大战-前5轮独赢
     */
    PENALTY_SHOOTOUT_1X2_IN_5_ROUNDS(80010, "Penalty shootout - 1X2(1st 5 rounds)", 3, SELECTIONS_WINNER_1X2, "all|score|pen_so", 333, "EU", PK, SOCCER.getId()),
    /**
     * 点球大战-前5轮大小
     */
    PENALTY_SHOOTOUT_TOTAL_IN_5_ROUNDS(80011, "Penalty shootout - total(1st 5 rounds)", 2, SELECTIONS_OVER_UNDER, "all|score|pen_so", 335, "MY", PK, SOCCER.getId()),
    /**
     * 点球大战-第{X}个点球是否射进
     */
    PENALTY_SHOOTOUT_XTH_PENALTY_SCORED(80012, "Penalty shootout - {!penaltynr} penalty scored", 2, SELECTIONS_YES_NO, "all|score|pen_so", 133, "MY", PK, SOCCER.getId()),
    /**
     * 点球大战-第几轮结束
     */
    PENALTY_SHOOTOUT_END_ROUND(80013, "Penalty shootout - end in which round", 4, SELECTIONS_PEN_SO_END_ROUND, "all|score|pen_so", 1100420, "EU", PK, SOCCER.getId()),


    //-------------------------------------特殊玩法-------------------------------------------------------------------
    /**
     * 晋级球队
     */
    SPECIAL_TO_QUALIFY(90001, "Special - To qualify", 2, SELECTIONS_HOME_AWAY, "all|score|cup_tie", 135, "MY", PK, SOCCER.getId()),
    /**
     * 获胜方式
     */
    SPECIAL_WINNING_METHOD(90002, "Special - Winning method", 6, SELECTIONS_WINNING_METHOD, "all|score|cup_tie", 137, "EU", PK, SOCCER.getId()),


    //--------------------------------------------------------------------------------------篮球--------------------------------------------------------------------------------------
    /**
     * 全场独赢（含加时）
     */
    BASKETBALL_WINNER(11001, "Winner(incl.overtime)", 2, SELECTIONS_HOME_AWAY, "all|score|incl_ot", 37, "EU", null, BASKETBALL.getId()),
    /**
     * 全场大小（含加时）
     */
    BASKETBALL_TOTAL(11002, "Total(incl.overtime)", 2, SELECTIONS_OVER_UNDER, "all|score|incl_ot", 38, "MY", null, BASKETBALL.getId()),
    /**
     * 全场让分（含加时）
     */
    BASKETBALL_HANDICAP(11003, "Handicap(incl.overtime)", 2, SELECTIONS_HOME_AWAY, "all|score|incl_ot", 39, "MY", null, BASKETBALL.getId()),
    /**
     * 全场单双（含加时）
     */
    BASKETBALL_ODD_EVEN(11004, "Odd/Even(incl.overtime)", 2, SELECTIONS_ODD_EVEN, "all|score|incl_ot", 40, "MY", null, BASKETBALL.getId()),
    /**
     * 全场{主队}大小（含加时）
     */
    BASKETBALL_HOME_TOTAL(11005, "{$competitor1} Total(incl.overtime)", 2, SELECTIONS_OVER_UNDER, "all|score|incl_ot", 198, "MY", null, BASKETBALL.getId()),
    /**
     * 全场{客队}大小（含加时）
     */
    BASKETBALL_AWAY_TOTAL(11006, "{$competitor2} Total(incl.overtime)", 2, SELECTIONS_OVER_UNDER, "all|score|incl_ot", 199, "MY", null, BASKETBALL.getId()),
    /**
     * 上半场独赢
     */
    BASKETBALL_HALF1_WINNER(11007, "1st half - Winner", 2, SELECTIONS_HOME_AWAY, "all|score|1st_half", 43, "EU", null, BASKETBALL.getId()),
    /**
     * 上半场大小
     */
    BASKETBALL_HALF1_TOTAL(11008, "1st half - Total", 2, SELECTIONS_OVER_UNDER, "all|score|1st_half", 18, "MY", null, BASKETBALL.getId()),
    /**
     * 上半场让分
     */
    BASKETBALL_HALF1_HANDICAP(11009, "1st half - Handicap", 2, SELECTIONS_HOME_AWAY, "all|score|1st_half", 19, "MY", null, BASKETBALL.getId()),
    /**
     * 上半场单双
     */
    BASKETBALL_HALF1_ODD_EVEN(11010, "1st half - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|1st_half", 42, "MY", null, BASKETBALL.getId()),
    /**
     * 上半场{主队}大小
     */
    BASKETBALL_HALF1_HOME_TOTAL(11011, "1st half - {$competitor1} Total", 2, SELECTIONS_OVER_UNDER, "all|score|1st_half", 87, "MY", null, BASKETBALL.getId()),
    /**
     * 上半场{客队}大小
     */
    BASKETBALL_HALF1_AWAY_TOTAL(11012, "1st half - {$competitor2} Total", 2, SELECTIONS_OVER_UNDER, "all|score|1st_half", 97, "MY", null, BASKETBALL.getId()),
    /**
     * 下半场独赢（含加时）
     */
    BASKETBALL_HALF2_WINNER(11013, "2nd half - Winner(incl.overtime)", 2, SELECTIONS_HOME_AWAY, "all|score|2nd_half", 142, "EU", null, BASKETBALL.getId()),
    /**
     * 下半场大小（含加时）
     */
    BASKETBALL_HALF2_TOTAL(11014, "2nd half - Total(incl.overtime)", 2, SELECTIONS_OVER_UNDER, "all|score|2nd_half", 26, "MY", null, BASKETBALL.getId()),
    /**
     * 下半场让分（含加时）
     */
    BASKETBALL_HALF2_HANDICAP(11015, "2nd half - Handicap(incl.overtime)", 2, SELECTIONS_HOME_AWAY, "all|score|2nd_half", 143, "MY", null, BASKETBALL.getId()),
    /**
     * 下半场单双（含加时）
     */
    BASKETBALL_HALF2_ODD_EVEN(11016, "2nd half - Odd/Even(incl.overtime)", 2, SELECTIONS_ODD_EVEN, "all|score|2nd_half", 75, "MY", null, BASKETBALL.getId()),
    /**
     * 下半场{主队}大小（含加时）
     */
    BASKETBALL_HALF2_HOME_TOTAL(11017, "2nd half - {$competitor1} Total(incl.overtime)", 2, SELECTIONS_OVER_UNDER, "all|score|2nd_half", 88, "MY", null, BASKETBALL.getId()),
    /**
     * 下半场{客队}大小（含加时）
     */
    BASKETBALL_HALF2_AWAY_TOTAL(11018, "2nd half - {$competitor2} Total(incl.overtime)", 2, SELECTIONS_OVER_UNDER, "all|score|2nd_half", 98, "MY", null, BASKETBALL.getId()),
    /**
     * 第一节独赢
     */
    BASKETBALL_QUARTER1_WINNER(11019, "1st quarter - Winner", 2, SELECTIONS_HOME_AWAY, "all|score|1st_quarter", 48, "EU", null, BASKETBALL.getId()),
    /**
     * 第一节大小
     */
    BASKETBALL_QUARTER1_TOTAL(11020, "1st quarter - Total", 2, SELECTIONS_OVER_UNDER, "all|score|1st_quarter", 45, "MY", null, BASKETBALL.getId()),
    /**
     * 第一节让分
     */
    BASKETBALL_QUARTER1_HANDICAP(11021, "1st quarter - Handicap", 2, SELECTIONS_HOME_AWAY, "all|score|1st_quarter", 46, "MY", null, BASKETBALL.getId()),
    /**
     * 第一节单双
     */
    BASKETBALL_QUARTER1_ODD_EVEN(11022, "1st quarter - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|1st_quarter", 47, "MY", null, BASKETBALL.getId()),
    /**
     * 第一节{主队}大小
     */
    BASKETBALL_QUARTER1_HOME_TOTAL(11023, "1st quarter - {$competitor1} Total", 2, SELECTIONS_OVER_UNDER, "all|score|1st_quarter", 145, "MY", null, BASKETBALL.getId()),
    /**
     * 第一节{客队}大小
     */
    BASKETBALL_QUARTER1_AWAY_TOTAL(11024, "1st quarter - {$competitor2} Total", 2, SELECTIONS_OVER_UNDER, "all|score|1st_quarter", 146, "MY", null, BASKETBALL.getId()),
    /**
     * 第二节独赢
     */
    BASKETBALL_QUARTER2_WINNER(11025, "2nd quarter - Winner", 2, SELECTIONS_HOME_AWAY, "all|score|2nd_quarter", 54, "EU", null, BASKETBALL.getId()),
    /**
     * 第二节大小
     */
    BASKETBALL_QUARTER2_TOTAL(11026, "2nd quarter - Total", 2, SELECTIONS_OVER_UNDER, "all|score|2nd_quarter", 51, "MY", null, BASKETBALL.getId()),
    /**
     * 第二节让分
     */
    BASKETBALL_QUARTER2_HANDICAP(11027, "2nd quarter - Handicap", 2, SELECTIONS_HOME_AWAY, "all|score|2nd_quarter", 52, "MY", null, BASKETBALL.getId()),
    /**
     * 第二节单双
     */
    BASKETBALL_QUARTER2_ODD_EVEN(11028, "2nd quarter - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|2nd_quarter", 53, "MY", null, BASKETBALL.getId()),
    /**
     * 第二节{主队}大小
     */
    BASKETBALL_QUARTER2_HOME_TOTAL(11029, "2nd quarter - {$competitor1} Total", 2, SELECTIONS_OVER_UNDER, "all|score|2nd_quarter", 145, "MY", null, BASKETBALL.getId()),
    /**
     * 第二节{客队}大小
     */
    BASKETBALL_QUARTER2_AWAY_TOTAL(11030, "2nd quarter - {$competitor2} Total", 2, SELECTIONS_OVER_UNDER, "all|score|2nd_quarter", 146, "MY", null, BASKETBALL.getId()),
    /**
     * 第三节独赢
     */
    BASKETBALL_QUARTER3_WINNER(11031, "3rd quarter - Winner", 2, SELECTIONS_HOME_AWAY, "all|score|3rd_quarter", 60, "EU", null, BASKETBALL.getId()),
    /**
     * 第三节大小
     */
    BASKETBALL_QUARTER3_TOTAL(11032, "3rd quarter - Total", 2, SELECTIONS_OVER_UNDER, "all|score|3rd_quarter", 57, "MY", null, BASKETBALL.getId()),
    /**
     * 第三节让分
     */
    BASKETBALL_QUARTER3_HANDICAP(11033, "3rd quarter - Handicap", 2, SELECTIONS_HOME_AWAY, "all|score|3rd_quarter", 58, "MY", null, BASKETBALL.getId()),
    /**
     * 第三节单双
     */
    BASKETBALL_QUARTER3_ODD_EVEN(11034, "3rd quarter - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|3rd_quarter", 59, "MY", null, BASKETBALL.getId()),
    /**
     * 第三节{主队}大小
     */
    BASKETBALL_QUARTER3_HOME_TOTAL(11035, "3rd quarter - {$competitor1} Total", 2, SELECTIONS_OVER_UNDER, "all|score|3rd_quarter", 145, "MY", null, BASKETBALL.getId()),
    /**
     * 第三节{客队}大小
     */
    BASKETBALL_QUARTER3_AWAY_TOTAL(11036, "3rd quarter - {$competitor2} Total", 2, SELECTIONS_OVER_UNDER, "all|score|3rd_quarter", 146, "MY", null, BASKETBALL.getId()),
    /**
     * 第四节独赢
     */
    BASKETBALL_QUARTER4_WINNER(11037, "4th quarter - Winner", 2, SELECTIONS_HOME_AWAY, "all|score|4th_quarter", 66, "EU", null, BASKETBALL.getId()),
    /**
     * 第四节大小
     */
    BASKETBALL_QUARTER4_TOTAL(11038, "4th quarter - Total", 2, SELECTIONS_OVER_UNDER, "all|score|4th_quarter", 63, "MY", null, BASKETBALL.getId()),
    /**
     * 第四节让分
     */
    BASKETBALL_QUARTER4_HANDICAP(11039, "4th quarter - Handicap", 2, SELECTIONS_HOME_AWAY, "all|score|4th_quarter", 64, "MY", null, BASKETBALL.getId()),
    /**
     * 第四节单双
     */
    BASKETBALL_QUARTER4_ODD_EVEN(11040, "4th quarter - Odd/Even", 2, SELECTIONS_ODD_EVEN, "all|score|4th_quarter", 65, "MY", null, BASKETBALL.getId()),
    /**
     * 第四节{主队}大小
     */
    BASKETBALL_QUARTER4_HOME_TOTAL(11041, "4th quarter - {$competitor1} Total", 2, SELECTIONS_OVER_UNDER, "all|score|4th_quarter", 145, "MY", null, BASKETBALL.getId()),
    /**
     * 第四节{客队}大小
     */
    BASKETBALL_QUARTER4_AWAY_TOTAL(11042, "4th quarter - {$competitor2} Total", 2, SELECTIONS_OVER_UNDER, "all|score|4th_quarter", 146, "MY", null, BASKETBALL.getId()),
    /**
     * 全场净胜分（3项）（含加时）
     */
    BASKETBALL_WINNING_MARGIN_6(11043, "Winning Margin(6+)", 3, SELECTIONS_BASKET_WINNING_MARGIN_6, "all|score|incl_ot", 200, "EU", null, BASKETBALL.getId()),
    /**
     * 首先获得{X}分（含加时）
     */
    BASKETBALL_RACE_TO_X_POINTS(11044, "Race To {!pointnr} Points(incl.overtime)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|incl_ot", 201, "EU", null, BASKETBALL.getId()),
    /**
     * 全场净胜分（6项）（含加时）
     */
    BASKETBALL_WINNING_MARGIN_11(11045, "Winning Margin(11+)", 6, SELECTIONS_BASKET_WINNING_MARGIN_11, "all|score|incl_ot", 209, "EU", null, BASKETBALL.getId()),
    /**
     * 全场净胜分（7项）（含加时）
     */
    BASKETBALL_ANY_TEAM_WINNING_MARGIN_31(11046, "Any Team Winning Margin(31+)", 7, SELECTIONS_BASKET_ANY_TEAM_WINNING_MARGIN_31, "all|score|incl_ot", 210, "EU", null, BASKETBALL.getId()),
    /**
     * 全场净胜分（12项）（含加时）
     */
    BASKETBALL_WINNING_MARGIN_26(11047, "Winning Margin(26+)", 12, SELECTIONS_BASKET_WINNING_MARGIN_26, "all|score|incl_ot", 211, "EU", null, BASKETBALL.getId()),
    /**
     * 全场净胜分（14项）（含加时）
     */
    BASKETBALL_WINNING_MARGIN_21(11048, "Winning Margin(21+)", 14, SELECTIONS_BASKET_WINNING_MARGIN_21, "all|score|incl_ot", 212, "EU", null, BASKETBALL.getId()),
    /**
     * 谁先获得第{X}分（含加时）
     */
    BASKETBALL_XTH_POINT_WITHOUT_NONE(11049, "{!pointnr} Point Without None(incl.overtime)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|incl_ot", 214, "EU", null, BASKETBALL.getId()),
    /**
     * 独赢 & 总分（含加时）
     */
    BASKETBALL_WINNER_AND_TOTAL(11050, "Winner & Total(incl.overtime)", 4, SELECTIONS_WINNER_AND_TOTAL, "all|score|incl_ot", 216, "EU", null, BASKETBALL.getId()),
    /**
     * 上半场净胜分
     */
    BASKETBALL_HALF1_WINNING_MARGIN_11(11051, "1st half - Winning Margin(11+)", 7, SELECTIONS_BASKET_HALF_WINNING_MARGIN_11, "all|score|incl_ot", 219, "EU", null, BASKETBALL.getId()),
    /**
     * 第一节净胜分
     */
    BASKETBALL_QUARTER1_WINNING_MARGIN_3(11052, "1st quarter - Winning Margin(3+)", 3, SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_3, "all|score|incl_ot", 49, "EU", null, BASKETBALL.getId()),
    /**
     * 第二节净胜分
     */
    BASKETBALL_QUARTER2_WINNING_MARGIN_3(11053, "2nd quarter - Winning Margin(3+)", 3, SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_3, "all|score|incl_ot", 55, "EU", null, BASKETBALL.getId()),
    /**
     * 第三节净胜分
     */
    BASKETBALL_QUARTER3_WINNING_MARGIN_3(11054, "3rd quarter - Winning Margin(3+)", 3, SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_3, "all|score|incl_ot", 61, "EU", null, BASKETBALL.getId()),
    /**
     * 第四节净胜分
     */
    BASKETBALL_QUARTER4_WINNING_MARGIN_3(11055, "4th quarter - Winning Margin(3+)", 3, SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_3, "all|score|incl_ot", 67, "EU", null, BASKETBALL.getId()),
    /**
     * 第一节首先获得{X}分（三项）
     */
    BASKETBALL_QUARTER1_RACE_TO_X_POINTS_3(11056, "1st quarter - Race To {!pointnr} Points", 3, SELECTIONS_TEAM, "all|score|incl_ot", 147, "EU", null, BASKETBALL.getId()),
    /**
     * 第二节首先获得{X}分（三项）
     */
    BASKETBALL_QUARTER2_RACE_TO_X_POINTS_3(11057, "2nd quarter - Race To {!pointnr} Points", 3, SELECTIONS_TEAM, "all|score|incl_ot", 147, "EU", null, BASKETBALL.getId()),
    /**
     * 第三节首先获得{X}分（三项）
     */
    BASKETBALL_QUARTER3_RACE_TO_X_POINTS_3(11058, "3rd quarter - Race To {!pointnr} Points", 3, SELECTIONS_TEAM, "all|score|incl_ot", 147, "EU", null, BASKETBALL.getId()),
    /**
     * 第四节首先获得{X}分（三项）
     */
    BASKETBALL_QUARTER4_RACE_TO_X_POINTS_3(11059, "4th quarter - Race To {!pointnr} Points", 3, SELECTIONS_TEAM, "all|score|incl_ot", 147, "EU", null, BASKETBALL.getId()),
    /**
     * 得分最高的赛节
     */
    BASKETBALL_HIGHEST_SCORING_QUARTER(11060, "Highest Scoring Quarter", 5, SELECTIONS_QUARTER, "all|score|incl_ot", 213, "EU", null, BASKETBALL.getId()),
    /**
     * 第一节首先获得{X}分
     */
    BASKETBALL_QUARTER1_RACE_TO_X_POINTS(11061, "1st quarter - Race To {!pointnr} Points", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|1st_quarter", 215, "EU", null, BASKETBALL.getId()),
    /**
     * 第二节首先获得{X}分
     */
    BASKETBALL_QUARTER2_RACE_TO_X_POINTS(11062, "2nd quarter - Race To {!pointnr} Points", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|2nd_quarter", 215, "EU", null, BASKETBALL.getId()),
    /**
     * 第三节首先获得{X}分
     */
    BASKETBALL_QUARTER3_RACE_TO_X_POINTS(11063, "3rd quarter - Race To {!pointnr} Points", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|3rd_quarter", 215, "EU", null, BASKETBALL.getId()),
    /**
     * 第四节首先获得{X}分
     */
    BASKETBALL_QUARTER4_RACE_TO_X_POINTS(11064, "4th quarter - Race To {!pointnr} Points", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|4th_quarter", 215, "EU", null, BASKETBALL.getId()),
    /**
     * 是否有加时赛
     */
    BASKETBALL_OT_EXIST(11065, "Will there be overtime", 2, SELECTIONS_YES_NO, "all|score|incl_ot", 41, "MY", null, BASKETBALL.getId()),
    /**
     * 半场/全场
     */
    BASKETBALL_DOUBLE_RESULT(11066, "Double Result(incl.overtime)", 6, SELECTIONS_BASKET_HT_FT, "all|score|incl_ot", 400, "EU", null, BASKETBALL.getId()),
    /**
     * 首个进球队伍
     */
    BASKETBALL_FIRST_SCORE_TEAM(11067, "First Team To Score(incl.overtime)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|incl_ot", 401, "MY", null, BASKETBALL.getId()),
    /**
     * 最高得分的半场
     */
    BASKETBALL_HIGHEST_SCORING_HALF(11068, "Highest Scoring Half(incl.overtime)", 2, SELECTIONS_BASKET_HALF, "all|score|incl_ot", 402, "MY", null, BASKETBALL.getId()),
    /**
     * 最后进球队伍
     */
    BASKETBALL_LAST_SCORE_TEAM(11069, "Last Team To Score(incl.overtime)", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|incl_ot", 403, "MY", null, BASKETBALL.getId()),
    /**
     * 下半場首先获得{Y}分
     */
    BASKETBALL_H2_RTX_PTS(11070, "2nd Half - Race To {!pointnr} Points", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|2nd_half", 404, "MY", null, BASKETBALL.getId()),
    /**
     * {主队}第一节获得{Y}分
     */
    BASKETBALL_Q1_HOME_X_PTS(11071, "1st quarter - Home Team to Score {!pointnr} Points", 2, SELECTIONS_YES_NO, "all|score|1st_quarter", 405, "MY", null, BASKETBALL.getId()),
    /**
     * {主队}第二节获得{Y}分
     */
    BASKETBALL_Q2_HOME_X_PTS(11072, "2nd quarter - Home Team to Score {!pointnr} Points", 2, SELECTIONS_YES_NO, "all|score|2nd_quarter", 405, "MY", null, BASKETBALL.getId()),
    /**
     * {主队}第三节获得{Y}分
     */
    BASKETBALL_Q3_HOME_X_PTS(11073, "3rd quarter - Home Team to Score {!pointnr} Points", 2, SELECTIONS_YES_NO, "all|score|3rd_quarter", 405, "MY", null, BASKETBALL.getId()),
    /**
     * {主队}第四节获得{Y}分
     */
    BASKETBALL_Q4_HOME_X_PTS(11074, "4th quarter - Home Team to Score {!pointnr} Points", 2, SELECTIONS_YES_NO, "all|score|4th_quarter", 405, "MY", null, BASKETBALL.getId()),
    /**
     * {客队}第一节获得{Y}分
     */
    BASKETBALL_Q1_AWAY_X_PTS(11075, "1st quarter - Away Team to Score {!pointnr} Points", 2, SELECTIONS_YES_NO, "all|score|1st_quarter", 406, "MY", null, BASKETBALL.getId()),
    /**
     * {客队}第二节获得{Y}分
     */
    BASKETBALL_Q2_AWAY_X_PTS(11076, "2nd quarter - Away Team to Score {!pointnr} Points", 2, SELECTIONS_YES_NO, "all|score|2nd_quarter", 406, "MY", null, BASKETBALL.getId()),
    /**
     * {客队}第三节获得{Y}分
     */
    BASKETBALL_Q3_AWAY_X_PTS(11077, "3rd quarter - Away Team to Score {!pointnr} Points", 2, SELECTIONS_YES_NO, "all|score|3rd_quarter", 406, "MY", null, BASKETBALL.getId()),
    /**
     * {客队}第四节获得{Y}分
     */
    BASKETBALL_Q4_AWAY_X_PTS(11078, "4th quarter - Away Team to Score {!pointnr} Points", 2, SELECTIONS_YES_NO, "all|score|4th_quarter", 406, "MY", null, BASKETBALL.getId()),
    /**
     * 第一节双重机会
     */
    BASKETBALL_Q1_DOUBLE_CHANCE(11079, "1st quarter - Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|score|1st_quarter", 407, "EU", null, BASKETBALL.getId()),
    /**
     * 第二节双重机会
     */
    BASKETBALL_Q2_DOUBLE_CHANCE(11080, "2nd quarter - Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|score|2nd_quarter", 407, "EU", null, BASKETBALL.getId()),
    /**
     * 第三节双重机会
     */
    BASKETBALL_Q3_DOUBLE_CHANCE(11081, "3rd quarter - Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|score|3rd_quarter", 407, "EU", null, BASKETBALL.getId()),
    /**
     * 第四节双重机会
     */
    BASKETBALL_Q4_DOUBLE_CHANCE(11082, "4th quarter - Double Chance", 3, SELECTIONS_DOUBLE_CHANCE, "all|score|4th_quarter", 407, "EU", null, BASKETBALL.getId()),
    /**
     * 上半场首先获得{Y}分
     */
    BASKETBALL_H1_RTX_PTS(11083, "1st half - Race To {!pointnr} Points", 2, SELECTIONS_TEAM_WITHOUT_NONE, "all|score|1st_half", 3100409, "MY", null, BASKETBALL.getId()),
    /**
     * 上半场-让分&大小
     */
    BASKETBALL_H1_HDP_TOTAL(11084, "1st half - Handicap & Over/Under", 4, SELECTIONS_BASKET_HDP_TOTAL, "all|score|1st_half", 3100410, "EU", null, BASKETBALL.getId()),
    /**
     * 第一节-让分&大小
     */
    BASKETBALL_Q1_HDP_TOTAL(11085, "1st quarter - Handicap & Over/Under", 4, SELECTIONS_BASKET_HDP_TOTAL, "all|score|1st_quarter", 3100411, "EU", null, BASKETBALL.getId()),
    /**
     * 让分&大小
     */
    BASKETBALL_HDP_TOTAL(11086, "Handicap & Over/Under", 4, SELECTIONS_BASKET_HDP_TOTAL, "all|score|incl_ot", 3100412, "EU", null, BASKETBALL.getId()),
    /**
     * 主队获胜节数大小
     */
    BASKETBALL_HOME_WIN_QUARTER(11087, "Home Team Quarters Win Over/Under", 2, SELECTIONS_OVER_UNDER, "all|score|incl_ot", 3100414, "MY", null, BASKETBALL.getId()),
    /**
     * 客队获胜节数大小
     */
    BASKETBALL_AWAY_WIN_QUARTER(11088, "Away Team Quarters Win Over/Under", 2, SELECTIONS_OVER_UNDER, "all|score|incl_ot", 3100415, "MY", null, BASKETBALL.getId()),
    /**
     * 上半场净胜分（13项）
     */
    BASKETBALL_H1_WINNING_MARGIN_26(11089, "1st half - Winning Margin(26+)", 13, SELECTIONS_BASKET_HALF_WINNING_MARGIN_26, "all|score|1st_half", 3100417, "EU", null, BASKETBALL.getId()),
    /**
     * 下半场净胜分（13项）（含加时）
     */
    BASKETBALL_H2_WINNING_MARGIN_26(11090, "2nd half - Winning Margin(26+)", 13, SELECTIONS_BASKET_HALF_WINNING_MARGIN_26, "all|score|2nd_half", 3100418, "EU", null, BASKETBALL.getId()),
    /**
     * 第一节净胜分（13项）
     */
    BASKETBALL_Q1_WINNING_MARGIN_26(11091, "1st quarter - Winning Margin(26+)", 13, SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_26, "all|score|1st_quarter", 3100419, "EU", null, BASKETBALL.getId()),
    /**
     * 第二节净胜分（13项）
     */
    BASKETBALL_Q2_WINNING_MARGIN_26(11092, "2nd quarter - Winning Margin(26+)", 13, SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_26, "all|score|2nd_quarter", 3100420, "EU", null, BASKETBALL.getId()),
    /**
     * 第三节净胜分（13项）
     */
    BASKETBALL_Q3_WINNING_MARGIN_26(11093, "3rd quarter - Winning Margin(26+)", 13, SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_26, "all|score|3rd_quarter", 3100421, "EU", null, BASKETBALL.getId()),
    /**
     * 第四节净胜分（13项）（不含加时）
     */
    BASKETBALL_Q4_WINNING_MARGIN_26(11094, "4th quarter - Winning Margin(26+)", 13, SELECTIONS_BASKET_QUARTER_WINNING_MARGIN_26, "all|score|4th_quarter", 3100422, "EU", null, BASKETBALL.getId()),
    /**
     * 总分末位数
     */
    BASKETBALL_LAST_DIGIT(11095, "Last Digit Score", 10, SELECTIONS_BASKET_LAST_DIGIT, "all|score|incl_ot", 3100423, "EU", null, BASKETBALL.getId()),
    /**
     * 上半场总分末位数
     */
    BASKETBALL_H1_LAST_DIGIT(11096, "1st half - Last Digit Score", 10, SELECTIONS_BASKET_LAST_DIGIT, "all|score|1st_half", 3100424, "EU", null, BASKETBALL.getId()),
    /**
     * 上半场主队总分末位数
     */
    BASKETBALL_H1_HOME_LAST_DIGIT(11097, "1st half - Home Last Digit Score", 10, SELECTIONS_BASKET_LAST_DIGIT, "all|score|1st_half", 3100425, "EU", null, BASKETBALL.getId()),
    /**
     * 上半场客队总分末位数
     */
    BASKETBALL_H1_AWAY_LAST_DIGIT(11098, "1st half - Away Last Digit Score", 10, SELECTIONS_BASKET_LAST_DIGIT, "all|score|1st_half", 3100426, "EU", null, BASKETBALL.getId()),
    /**
     * 下半场总分末位数
     */
    BASKETBALL_H2_LAST_DIGIT(11099, "2nd half - Last Digit Score", 10, SELECTIONS_BASKET_LAST_DIGIT, "all|score|2nd_half", 3100427, "EU", null, BASKETBALL.getId()),
    /**
     * 下半场主队总分末位数
     */
    BASKETBALL_H2_HOME_LAST_DIGIT(11101, "2nd half - Home Last Digit Score", 10, SELECTIONS_BASKET_LAST_DIGIT, "all|score|2nd_half", 3100428, "EU", null, BASKETBALL.getId()),
    /**
     * 下半场客队总分末位数
     */
    BASKETBALL_H2_AWAY_LAST_DIGIT(11102, "2nd half - Away Last Digit Score", 10, SELECTIONS_BASKET_LAST_DIGIT, "all|score|2nd_half", 3100429, "EU", null, BASKETBALL.getId()),
    ;

    /**
     * 玩法Id
     */
    private final int id;

    /**
     * 玩法名称
     */
    private final String name;

    /**
     * 投注项数量（0表示投注项数量无法确认）
     */
    private final int selectionCount;

    /**
     * 投注项集合
     */
    private final List<MarketSelection> selectionList;

    /**
     * 玩法分组
     */
    private final String groups;

    /**
     * 标准玩法ID
     */
    private final Integer standardCategoryId;

    /**
     * 玩法计算类型（对应融合） ：MY:spread ，EU:margin
     */
    private final String calculateType;

    /**
     * 玩法类型：GOAL、CORNER、BOOKING、YC、RC、PK
     */
    private final MarketModel marketModel;

    /**
     * 运动类型ID
     */
    private final int sportId;

    /**
     * 构造方法
     *
     * @param id                 玩法Id
     * @param name               玩法名称
     * @param selectionCount     投注项数量
     * @param selectionList      投注项集合
     * @param groups             玩法分组
     * @param standardCategoryId 标准玩法ID
     * @param calculateType      玩法计算类型
     */
    MarketCategory(int id, String name, int selectionCount, List<MarketSelection> selectionList, String groups, Integer standardCategoryId, String calculateType, MarketModel marketModel, int sportId) {
        this.id = id;
        this.name = name;
        this.selectionCount = selectionCount;
        this.selectionList = selectionList;
        this.groups = groups;
        this.standardCategoryId = standardCategoryId;
        this.calculateType = calculateType;
        this.marketModel = marketModel;
        this.sportId = sportId;
    }

    //-------------------------------------常用玩法枚举集合---------------------------------------------------------------
    /**
     * 角球大小玩法集合
     */
    public static List<MarketCategory> CORNER_TOTAL_MARKET_LIST = Arrays.asList(CORNER_TOTAL, CORNER_TEAM1_TOTAL, CORNER_TEAM2_TOTAL, CORNER_H1_TOTAL, CORNER_H1_TEAM1_TOTAL, CORNER_H1_TEAM2_TOTAL, CORNER_FIFTEEN_TOTAL, CORNER_OT_TOTAL);
    /**
     * 进球类主客队大小玩法集合
     */
    public static List<MarketCategory> GOAL_TOTAL_MARKET_LIST = Arrays.asList(FT_TEAM1_OVER_UNDER, FT_TEAM2_OVER_UNDER, HALF1_TEAM1_OVER_UNDER, HALF1_TEAM2_OVER_UNDER, HALF2_TEAM1_TOTAL, HALF2_TEAM2_TOTAL);
    /**
     * 点球大战大小玩法集合
     */
    public static List<MarketCategory> PENALTY_SHOOTOUT_TOTAL_MARKET_LIST = Arrays.asList(PENALTY_SHOOTOUT_TOTAL, PENALTY_SHOOTOUT_TOTAL_IN_5_ROUNDS);

    //-------------------------------------玩法对应关系-------------------------------------------------------------------

    /**
     * 所有标准玩法对应AO玩法
     */
    public static Map<Long, Integer> ALL_STANDARD_CONVERT_AO_CATEGORY = new HashMap<>();

    public static Map<Long, Integer> ALL_FOOTBALL_STANDARD_CONVERT_AO_CATEGORY = new HashMap<>();
    public static Map<Long, Integer> ALL_BASKETBALL_STANDARD_CONVERT_AO_CATEGORY = new HashMap<>();


    /**
     * spread抽水玩法 AO玩法 对应 标准玩法
     */
    public static Map<Integer, Long> AO_CONVERT_STANDARD_CATEGORY_MY = new HashMap<>();
    /**
     * margin抽水玩法 AO玩法 对应 标准玩法
     */
    public static Map<Integer, Long> AO_CONVERT_STANDARD_CATEGORY_EU = new HashMap<>();

    //-------------------------------------玩法对应关系end-------------------------------------------------------------------

    /**
     * 所有盘口
     */
    public static final List<Integer> ALL_MARKET_LIST = new ArrayList<>();
    /**
     * 全场   主列表玩法
     */
    public static final List<Integer> MAIN_FT_MARKET_LIST = Lists.newArrayList(10001, 10005, 10004);
    /**
     * 全场全部玩法 去除主列表玩法
     */
    public static final List<Integer> SCORE_ALL_FT_MARKET_LIST = new ArrayList<>();
    /**
     * 上半场  主列表玩法
     */
    public static final List<Integer> MAIN_HT_MARKET_LIST = Lists.newArrayList(20001, 20004, 20005);
    /**
     * 上半场全部玩法 去除主列表玩法
     */
    public static final List<Integer> SCORE_ALL_HT_MARKET_LIST = new ArrayList<>();
    /**
     * 进球类全场盘口
     */
    public static final List<Integer> SCORE_REGULAR_MARKET_LIST = new ArrayList<>();
    /**
     * 进球类上半场盘口
     */
    public static final List<Integer> SCORE_HALF1_MARKET_LIST = new ArrayList<>();

    /**
     * 足球按照盘口类型分类(角球、发牌、进球)
     */

    public static final Map<String, List<Integer>> SCORE_MARKETTYPE_CATEGORY = new HashMap<>();
    /**
     * 进球类下半场盘口 主列表玩法
     */
    public static final List<Integer> MAIN_SCORE_HALF2_MARKET_LIST = Lists.newArrayList(60001, 60003);
    /**
     * 进球类下半场全部玩法 去除主列表玩法
     */
    public static final List<Integer> SCORE_ALL_HALF2_MARKET_LIST = new ArrayList<>();
    /**
     * 进球类下半场盘口
     */
    public static final List<Integer> SCORE_HALF2_MARKET_LIST = new ArrayList<>();
    /**
     * 角球类全场盘口
     */
    public static final List<Integer> CORNER_REGULAR_MARKET_LIST = new ArrayList<>();
    /**
     * 角球类上半场盘口
     */
    public static final List<Integer> CORNER_HALF1_MARKET_LIST = new ArrayList<>();
    /**
     * 角球类下半场盘口
     */
    public static final List<Integer> CORNER_HALF2_MARKET_LIST = new ArrayList<>();
    /**
     * 罚牌类全场盘口
     */
    public static final List<Integer> BOOKING_REGULAR_MARKET_LIST = new ArrayList<>();
    /**
     * 罚牌类上半场盘口
     */
    public static final List<Integer> BOOKING_HALF1_MARKET_LIST = new ArrayList<>();
    /**
     * 罚牌类下半场盘口
     */
    public static final List<Integer> BOOKING_HALF2_MARKET_LIST = new ArrayList<>();

    /**
     * 所有全场玩法
     */
    public static final List<Integer> ALL_SCORE_REGULAR_MARKET_LIST = new ArrayList<>();
    /**
     * 所有上半场玩法 去除加时
     */
    public static final List<Integer> ALL_SCORE_HALF1_MARKET_LIST = new ArrayList<>();
    /**
     * 所有下半场玩法
     */
    public static final List<Integer> ALL_SCORE_HALF2_MARKET_LIST = new ArrayList<>();
    /**
     * 15分钟
     */
    public static final List<Integer> FIFTEEN_ATOB_MARKET_LIST = new ArrayList<>();
    /**
     * 加时赛所有玩法
     */
    public static final List<Integer> OVERTIME_ALL_LIST = new ArrayList<>();
    /**
     * 加时赛进球
     */
    public static final List<Integer> OVERTIME_GOAL_ALL_LIST = new ArrayList<>();
    /**
     * 加时赛角球
     */
    public static final List<Integer> OVERTIME_CORNERS_ALL_LIST = new ArrayList<>();
    /**
     * PK所有玩法
     */
    public static final List<Integer> PK_ALL_LIST = new ArrayList<>();

//    ---------篮球----------
    /**
     * 全场
     */
    public static final List<Integer> BASKETBALL_ALL_FT_MARKET_LIST = new ArrayList<>();
    /**
     * 上半场
     */
    public static final List<Integer> BASKETBALL_ALL_HALF1_MARKET_LIST = new ArrayList<>();
    /**
     * 下半场
     */
    public static final List<Integer> BASKETBALL_ALL_HALF2_MARKET_LIST = new ArrayList<>();
    /**
     * 第一节 -  第四节
     */
    public static final List<Integer> BASKETBALL_ALL_Q1_MARKET_LIST = new ArrayList<>();
    public static final List<Integer> BASKETBALL_ALL_Q2_MARKET_LIST = new ArrayList<>();
    public static final List<Integer> BASKETBALL_ALL_Q3_MARKET_LIST = new ArrayList<>();
    public static final List<Integer> BASKETBALL_ALL_Q4_MARKET_LIST = new ArrayList<>();

    /**
     * 第一节-  第四节 开始固定关盘
     */
    public static final List<Integer> BASKETBALL_STABLE_Q1_MARKET_LIST = Lists.newArrayList(11071, 11075, 11079);
    public static final List<Integer> BASKETBALL_STABLE_Q2_MARKET_LIST = Lists.newArrayList(11072, 11076, 11080);
    public static final List<Integer> BASKETBALL_STABLE_Q3_MARKET_LIST = Lists.newArrayList(11073, 11077, 11081);
    public static final List<Integer> BASKETBALL_STABLE_Q4_MARKET_LIST = Lists.newArrayList(11074, 11078, 11082);

    /**
     * 没有投注模板的玩法集合
     */
    public static final List<Integer> MARKET_WITH_OUT_TEMPLATE_LIST = Arrays.asList(10008, 10009, 10012, 20008, 10015, 10016, 10017, 10018, 10020, 20011, 60005, 60006, 20015, 20016, 40014, 40015, 40016, 40019,
            70005, 80005, 80006, 80007, 10050, 10062, 20022, 10065, 20023, 60017, 60020, 60021, 60024, 11043, 11045, 11046, 11047, 11048, 11051, 40027, 40030, 40031, 80013, 11089, 11090, 11091, 11092, 11093,
            11094);

    /**
     * 带X玩法
     */
    public static final List<Integer> CATEGORY_X = Lists.newArrayList(10039, 20017, 10040, 40021, 40017, 50012, 40018, 40020, 70007, 80008, 10049, 10064);

    /**
     * 盘口投注项未激活<=一半 ，封盘
     */
    public static final List<Integer> MARKET_BET_PIECE_ACTIVE_LIST = Lists.newArrayList(10033, 10034, 10035, 10036, 10051, 10055, 10057, 20014, 20021, 60009, 60016);

    /**
     * 休息阶段 关闭上半场玩法
     */
    public static final List<Integer> ALL_HT_MARKET_CLOSE = Lists.newArrayList(10026, 10027, 10042, 10043, 10031, 10032, 10045, 10046, 10020, 10021, 10037, 10047, 10048, 10052, 10053, 10054, 10058, 10059, 10060, 10062, 20001, 20005, 20004, 20015, 20016, 20011, 20013, 20017, 20003, 20012, 20018, 20002, 20006, 20019, 20007, 20020, 20014, 20008, 20021, 20022, 60001, 60003, 60010, 60002, 60005, 60004, 60008, 60011, 60012, 60013, 60014, 60009, 60007, 60015, 60006, 60016,
            20023, 20024, 20025, 20026, 20027);
    /**
     * 常规赛结束 兜底关盘玩法
     */
    public static final List<Integer> REGULAR_SEASON_MARKET_CLOSE = Lists.newArrayList(40001, 40002, 40003, 40005, 40006, 40014, 40004, 40018, 40015, 40016, 40022, 40023, 40024);

    /**
     * 玩法Map
     */
    public static final Map<Integer, MarketCategory> MARKET_CATEGORY_MAP = new HashMap<>();

    /**
     * 玩法类型 marketModel 分组 ，apply下发玩法集合给下游清除水差
     */
    public static final Map<String, Set<Long>> MARKETMODEL_MAP = new HashMap<>();

    public static Map<String, List<Integer>> STANDARD_CATEGORY_ID_AND_AO_CATEGORY = new LinkedHashMap<String, List<Integer>>() {{
        //独赢类玩法
        put("37", Lists.newArrayList(11001, 11007, 11013, 11019, 11025, 11031, 11037));
        //大小类玩法
        put("38", Lists.newArrayList(11002, 11008, 11014, 11020, 11026, 11032, 11038));
        //让分类玩法
        put("39", Lists.newArrayList(11003, 11009, 11015, 11021, 11027, 11033, 11039));
        //单双类玩法
        put("40", Lists.newArrayList(11004, 11010, 11022, 11028, 11034, 11040, 11016));
    }};

    /**
     * 全场比分最大主客队比分 兜底关
     */
    public static final List<Integer> marketTotal = Lists.newArrayList(11044);
    /**
     * 全场比分总分 兜底关
     */
    public static final List<Integer> marketSum = Lists.newArrayList(11049);
    /**
     * 第一节最大主客队比分 兜底关
     */
    public static final List<Integer> marketQ1 = Lists.newArrayList(11056, 11061);
    /**
     * 第二节最大主客队比分 兜底关
     */
    public static final List<Integer> marketQ2 = Lists.newArrayList(11057, 11062);
    /**
     * 第三节最大主客队比分 兜底关
     */
    public static final List<Integer> marketQ3 = Lists.newArrayList(11058, 11063);
    /**
     * 第4节最大主客队比分 兜底关
     */
    public static final List<Integer> marketQ4 = Lists.newArrayList(11059, 11064);
    public static final List<Integer> ALL_MARKET = new ArrayList<Integer>() {{
        addAll(marketTotal);
        addAll(marketSum);
        addAll(marketQ1);
        addAll(marketQ2);
        addAll(marketQ3);
        addAll(marketQ4);
    }};
    /**
     * 15分钟进球类玩法,单独兜底
     */
    public static final List<Integer> FIFTEEN_ATOB_MARKET_SPECIAL_LIST = Lists.newArrayList(30001, 30002, 30003);

    /**
     * 足球线性计算
     * 全场波胆
     * 上半场波胆
     * 下半场波胆
     * 加时赛波胆
     */
    public static final List<Integer> LINEAR_MARGIN_CORRECT_SCORE = Lists.newArrayList(MarketCategory.FT_CORRECT_SCORE.getId(), MarketCategory.HALF1_CORRECT_SCORE.getId(), MarketCategory.HALF2_CORRECT_SCORE.getId(), MarketCategory.OVERTIME_CORRECT_SCORE.getId());


    static {
        List<Integer> g_goalList = Arrays.asList(MarketCategory.values()).stream().filter(l -> l.getSportId() == 1).map(f -> {
            if (f.getMarketModel().toString().equals(G_GOAL.toString())) {
                return f.getId();
            }
            return null;
        }).filter(m -> null != m).collect(Collectors.toList());
        SCORE_MARKETTYPE_CATEGORY.put(G_GOAL.toString().toLowerCase(), g_goalList);
        for (MarketCategory marketCategory : MarketCategory.values()) {
            Long standardCategoryId = Long.valueOf(marketCategory.getStandardCategoryId());
            Integer aoCategoryId = marketCategory.getId();
            //所有玩法 Map<标准，ao玩法>
            ALL_STANDARD_CONVERT_AO_CATEGORY.put(standardCategoryId, aoCategoryId);
            if ("MY".equals(marketCategory.getCalculateType())) {
                AO_CONVERT_STANDARD_CATEGORY_MY.put(aoCategoryId, standardCategoryId);
            }
            String groups = marketCategory.getGroups();
            //足球
            if (marketCategory.getSportId() == SOCCER.getId()) {
                ALL_FOOTBALL_STANDARD_CONVERT_AO_CATEGORY.put(standardCategoryId, aoCategoryId);
                MARKET_CATEGORY_MAP.put(marketCategory.getId(), marketCategory);
                if (groups.contains("all")) {
                    ALL_MARKET_LIST.add(marketCategory.getId());
                }
                if (!groups.contains("ot")) {
                    if (groups.contains("score")) {
                        if (groups.contains("regular_play")) {
                            SCORE_REGULAR_MARKET_LIST.add(marketCategory.getId());
                        } else if (groups.contains("1st_half")) {
                            SCORE_HALF1_MARKET_LIST.add(marketCategory.getId());
                        } else if (groups.contains("2nd_half")) {
                            SCORE_HALF2_MARKET_LIST.add(marketCategory.getId());
                        } else if (groups.contains("15_min")) {
                            FIFTEEN_ATOB_MARKET_LIST.add(marketCategory.getId());
                        }
                    }
                    if (groups.contains("corner")) {
                        if (groups.contains("regular_play")) {
                            CORNER_REGULAR_MARKET_LIST.add(marketCategory.getId());
                        } else if (groups.contains("1st_half")) {
                            CORNER_HALF1_MARKET_LIST.add(marketCategory.getId());
                        } else if (groups.contains("2nd_half")) {
                            CORNER_HALF2_MARKET_LIST.add(marketCategory.getId());
                        } else if (groups.contains("15_min")) {
                            FIFTEEN_ATOB_MARKET_LIST.add(marketCategory.getId());
                        }
                    }
                    if (groups.contains("booking")) {
                        if (groups.contains("regular_play")) {
                            BOOKING_REGULAR_MARKET_LIST.add(marketCategory.getId());
                        } else if (groups.contains("1st_half")) {
                            BOOKING_HALF1_MARKET_LIST.add(marketCategory.getId());
                        } else if (groups.contains("2nd_half")) {
                            BOOKING_HALF2_MARKET_LIST.add(marketCategory.getId());
                        } else if (groups.contains("15_min")) {
                            FIFTEEN_ATOB_MARKET_LIST.add(marketCategory.getId());
                        }
                    }
                    if (groups.contains("regular_play")) {
                        ALL_SCORE_REGULAR_MARKET_LIST.add(marketCategory.getId());
                    } else if (groups.contains("1st_half")) {
                        ALL_SCORE_HALF1_MARKET_LIST.add(marketCategory.getId());
                    } else if (groups.contains("2nd_half")) {
                        ALL_SCORE_HALF2_MARKET_LIST.add(marketCategory.getId());
                    }
                }
                //加时赛
                if (groups.contains("ot")) {
                    if (groups.contains("score")) {
                        OVERTIME_GOAL_ALL_LIST.add(marketCategory.getId());
                    }
                    if (groups.contains("corners")) {
                        OVERTIME_CORNERS_ALL_LIST.add(marketCategory.getId());
                    }
                    OVERTIME_ALL_LIST.add(marketCategory.getId());
                }
                //PK
                if (groups.contains("all|score|pen_so") || groups.contains("all|score|incl_ot")) {
                    PK_ALL_LIST.add(marketCategory.getId());
                }
                //玩法对应
                if (null != marketCategory.getMarketModel()) {
                    String marketModel = marketCategory.getMarketModel().toString().toLowerCase();
                    //特殊玩法单独分组
                    if (groups.equals("all|score|cup_tie")) {
                        marketModel = "specApply";
                    }
                    if (MARKETMODEL_MAP.containsKey(marketModel)) {
                        MARKETMODEL_MAP.get(marketModel).add(marketCategory.getStandardCategoryId().longValue());
                    } else {
                        Set<Long> standardCategoryIds = new HashSet<>();
                        standardCategoryIds.add(marketCategory.getStandardCategoryId().longValue());
                        MARKETMODEL_MAP.put(marketModel, standardCategoryIds);
                    }
                }
                //全场全部玩法
                SCORE_ALL_FT_MARKET_LIST.addAll(SCORE_REGULAR_MARKET_LIST);
                //上半场全部玩法
                SCORE_ALL_HT_MARKET_LIST.addAll(SCORE_HALF1_MARKET_LIST);
                //下半场全部玩法
                SCORE_ALL_HALF2_MARKET_LIST.addAll(SCORE_HALF2_MARKET_LIST);
                //去除主列表玩法
                SCORE_ALL_FT_MARKET_LIST.removeAll(MAIN_FT_MARKET_LIST);
                SCORE_ALL_HT_MARKET_LIST.removeAll(MAIN_HT_MARKET_LIST);
                SCORE_ALL_HALF2_MARKET_LIST.removeAll(MAIN_SCORE_HALF2_MARKET_LIST);


            } else if (marketCategory.getSportId() == BASKETBALL.getId()) {
                ALL_BASKETBALL_STANDARD_CONVERT_AO_CATEGORY.put(standardCategoryId, aoCategoryId);
                if (groups.contains("all|score|incl_ot")) {
                    BASKETBALL_ALL_FT_MARKET_LIST.add(marketCategory.getId());
                }
                if (groups.contains("all|score|1st_half")) {
                    BASKETBALL_ALL_HALF1_MARKET_LIST.add(marketCategory.getId());
                }
                if (groups.contains("all|score|2nd_half")) {
                    BASKETBALL_ALL_HALF2_MARKET_LIST.add(marketCategory.getId());
                }
                if (groups.contains("all|score|1st_quarter")) {
                    BASKETBALL_ALL_Q1_MARKET_LIST.add(marketCategory.getId());
                }
                if (groups.contains("all|score|2nd_quarter")) {
                    BASKETBALL_ALL_Q2_MARKET_LIST.add(marketCategory.getId());
                }
                if (groups.contains("all|score|3rd_quarter")) {
                    BASKETBALL_ALL_Q3_MARKET_LIST.add(marketCategory.getId());
                }
                if (groups.contains("all|score|4th_quarter")) {
                    BASKETBALL_ALL_Q4_MARKET_LIST.add(marketCategory.getId());
                }
            }
        }
    }

    /**
     * 根据玩法Id获取数据
     *
     * @param id 玩法ID
     * @return 玩法数据
     */
    public static MarketCategory getMarketCategoryById(Integer id) {
        for (MarketCategory marketCategory : MarketCategory.values()) {
            if (marketCategory.getId() == id) {
                return marketCategory;
            }
        }
        return null;
    }

    /**
     * 根据玩法Id获取玩法类型
     *
     * @param id 玩法ID
     * @return 玩法类型
     */
    public static String getMarketModelById(int id) {
        for (MarketCategory marketCategory : MarketCategory.values()) {
            if (marketCategory.getId() == id) {
                return marketCategory.getMarketModel().toString().toLowerCase();
            }
        }
        return null;
    }

    public static String getMarketCalculateTypeById(int id) {
        for (MarketCategory marketCategory : MarketCategory.values()) {
            if (marketCategory.getId() == id) {
                return marketCategory.getCalculateType();
            }
        }
        return null;
    }

    /**
     * 根据标准玩法Id获取数据
     *
     * @param standardMarketId 玩法ID
     * @return 玩法数据
     */
    public static List<Integer> getMarketCategoryByStandardMarketId(Integer standardMarketId, int sportId) {
        List<Integer> ids = new ArrayList<>();
        for (MarketCategory marketCategory : MarketCategory.values()) {
            if (marketCategory.getStandardCategoryId().equals(standardMarketId) && marketCategory.getSportId() == sportId) {
                ids.add(marketCategory.getId());
            }
        }
        return ids;
    }


    public static void main(String[] args) {
//        System.out.println("--------------marketID---------------");
//        for (MarketCategory marketCategory : MarketCategory.values()) {
//            System.out.println(marketCategory.getId());
//        }
//        System.out.println("--------------marketName---------------");
//        for (MarketCategory marketCategory : MarketCategory.values()) {
//            System.out.println(marketCategory.getName());
//        }
//        System.out.println("--------------AllMarketId---------------");
//        List<Integer> marketIdList = SCORE_ALL_FT_MARKET_LIST;
//        for (Integer integer : marketIdList) {
//            System.out.println(integer);
//        }
//        System.out.println("list.size:" + marketIdList.size());

        if (MarketCategory.SCORE_MARKETTYPE_CATEGORY.get(G_GOAL.toString().toLowerCase()).contains(10001)) {
            System.out.println(SCORE_MARKETTYPE_CATEGORY);
        }
        System.out.println(SCORE_MARKETTYPE_CATEGORY);
    }
}
