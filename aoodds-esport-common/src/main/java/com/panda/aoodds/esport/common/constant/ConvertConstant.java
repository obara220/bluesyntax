package com.panda.aoodds.esport.common.constant;


import com.panda.aoodds.esport.common.enums.ScoreType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author carson
 * @DATE 2022/3/10 18:15
 **/
public class ConvertConstant {
    /****比分中心 阶段比分*****/
    public final static String goalMins0To15="60899";
    public final static String goalMins15To30="61799";
    public final static String goalMins30ToHt="62699";
    public final static String goalMinsHtTo60="73599";
    public final static String goalMins60To75="74499";
    public final static String goalMins75ToFt="75399";

    public final static String corner0To15="corner_60899";
    public final static String corner15To30="corner_61799";
    public final static String corner30ToHt="corner_62699";
    public final static String cornerHtTo60="corner_73599";
    public final static String corner60To75="corner_74499";
    public final static String corner75ToFt="corner_75399";

    public final static String fa0To15="fa_60899";
    public final static String fa15To30="fa_61799";
    public final static String fa30ToHt="fa_62699";
    public final static String faHtTo60="fa_73599";
    public final static String fa60To75="fa_74499";
    public final static String fa75ToFt="fa_75399";

    public final static String yellow0To15="yellow_60899";
    public final static String yellow15To30="yellow_61799";
    public final static String yellow30ToHt="yellow_62699";
    public final static String yellowHtTo60="yellow_73599";
    public final static String yellow60To75="yellow_74499";
    public final static String yellow75ToFt="yellow_75399";

    public final static String red0To15="red_60899";
    public final static String red15To30="red_61799";
    public final static String red30ToHt="red_62699";
    public final static String redHtTo60="red_73599";
    public final static String red60To75="red_74499";
    public final static String red75ToFt="red_75399";

    public final static String roundScores="roundScores";
    public final static String round36Scores="round36Scores";
    public final static String SHOOT_FIRST = "shootFirst";
    public final static String POINT_NUM = "pointNum";

    /** 进球 */
    public final static String ht1goal = "ht1goal";
    public final static String ht2goal = "ht2goal";
    public final static String goal = "goal";
    /** 角球 */
    public final static String corner = "corner";
    public final static String ht1corner = "ht1corner";
    /** 罚牌类：红牌、黄牌、罚牌*/
    public final static String redCard = "redCard";
    public final static String yellowCard = "yellowCard";
    public final static String faCard = "faCard";
    public final static String ht1faCard="ht1faCard";
    public final static String ht2faCard="ht2faCard";
//    public final static List<String> listKey= Arrays.asList(mins0To15,mins15To30,mins30ToHt,minsHtTo60,mins60To75,mins75ToFt,goal,corner,redCard,yellowCard,faCard,
//            corner0To15,corner15To30,corner30ToHt,cornerHtTo60,corner60To75,corner75ToFt,ht1faCard,ht2faCard,round36Scores);
    /**当进球总数发生变化时需要更新相对应的玩法赔率**/
    public final static List<String> listKey= Arrays.asList(goalMins0To15,goalMins15To30,goalMins30ToHt,goalMinsHtTo60,goalMins60To75,goalMins75ToFt,corner0To15,corner15To30,corner30ToHt,cornerHtTo60,corner60To75,corner75ToFt,yellow0To15,yellow15To30,yellow30ToHt,yellowHtTo60,yellow60To75,yellow75ToFt,red0To15,red15To30,red30ToHt,redHtTo60,red60To75,red75ToFt,round36Scores);
    public final static String MODEL_GOAL="goal";
    public final static String MODEL_CORNER="corner";
    public final static String MODEL_BOOKING="booking";
    public final static String MODEL_YC="yc";
    public final static String MODEL_RC="rc";
    public final static String MODEL_PK="pk";
    public final static String MODEL_MAIN="MODEL_MAIN";
    public final static String MODEL_BIG_DATA="MODEL_BIG_DATA";
    public final static String MODEL_0TO15="MODEL_0TO15";
    public final static String MODEL_15TO30="MODEL_15TO30";
    public final static String MODEL_30TOHT="MODEL_30TOHT";
    public final static String MODEL_HTTO60="MODEL_HTTO60";
    public final static String MODEL_60TO75="MODEL_60TO75";
    public final static String MODEL_75TOFT="MODEL_75TOFT";
    /*******************篮球比分Key****************************/
    public final static String BK_FULL_SCORES="BK_FULL_SCORES";
    public final static String BK_PERIOD_SCORES_13="BK_PERIOD_SCORES_13";
    public final static String BK_PERIOD_SCORES_14="BK_PERIOD_SCORES_14";
    public final static String BK_PERIOD_SCORES_15="BK_PERIOD_SCORES_15";
    public final static String BK_PERIOD_SCORES_16="BK_PERIOD_SCORES_16";
    public final static String BK_PERIOD_SCORES_40="BK_PERIOD_SCORES_40";
    public final static String BK_PERIOD_SCORES_1="BK_PERIOD_SCORES_1";
    public final static String BK_PERIOD_SCORES_2="BK_PERIOD_SCORES_2";
    public static final Map<String,String> CHOICE_MARKET_MODEL=new HashMap<String,String>(){{
        put(goal,MODEL_MAIN);
        put("none",MODEL_BIG_DATA);
        put(goalMins0To15,MODEL_0TO15);
        put(goalMins15To30,MODEL_15TO30);
        put(goalMins30ToHt,MODEL_30TOHT);
        put(goalMinsHtTo60,MODEL_HTTO60);
        put(goalMins60To75,MODEL_60TO75);
        put(goalMins75ToFt,MODEL_75TOFT);

    }};
    public static final Map<String, ScoreType> SCORE_MAP=new HashMap<String,ScoreType>(){{
        /********进球类*******/
        put(goalMins0To15,ScoreType.FIFTEEN_00TO15_SCORE);
        put(goalMins15To30,ScoreType.FIFTEEN_16TO30_SCORE);
        put(goalMins30ToHt,ScoreType.FIFTEEN_31TO45_SCORE);
        put(goalMinsHtTo60, ScoreType.FIFTEEN_46TO60_SCORE);
        put(goalMins60To75,ScoreType.FIFTEEN_61TO75_SCORE);
        put(goalMins75ToFt,ScoreType.FIFTEEN_76TO90_SCORE);
        put(goal,ScoreType.FULL_TIME_SCORE);
        put(ht1goal, ScoreType.HALF1_SCORE);
        put(ht2goal, ScoreType.HALF2_SCORE);
        /*******角球类*********/
        put(corner0To15,ScoreType.CORNER_00TO15_SCORE);
        put(corner15To30,ScoreType.CORNER_16TO30_SCORE);
        put(corner30ToHt,ScoreType.CORNER_31TO45_SCORE);
        put(cornerHtTo60,ScoreType.CORNER_46TO60_SCORE);
        put(corner60To75,ScoreType.CORNER_61TO75_SCORE);
        put(corner75ToFt,ScoreType.CORNER_76TO90_SCORE);
        put(ht1corner,ScoreType.HALF1_CORNER);
        put(corner,ScoreType.FT_CORNER);
        /*******罚牌类*********/
        put(faCard,ScoreType.FT_BOOKING);
        put(ht1faCard,ScoreType.HALF1_BOOKING);
        put(ht2faCard,ScoreType.HALF2_BOOKING);

        put(fa0To15,ScoreType.BOOKING_00TO15_SCORE);
        put(fa15To30,ScoreType.BOOKING_16TO30_SCORE);
        put(fa30ToHt,ScoreType.BOOKING_31TO45_SCORE);
        put(faHtTo60,ScoreType.BOOKING_46TO60_SCORE);
        put(fa60To75,ScoreType.BOOKING_61TO75_SCORE);
        put(fa75ToFt,ScoreType.BOOKING_76TO90_SCORE);

        /************红牌**********************/
        put(redCard,ScoreType.FT_RED);

        put(red0To15,ScoreType.RED_00TO15_SCORE);
        put(red15To30,ScoreType.RED_16TO30_SCORE);
        put(red30ToHt,ScoreType.RED_31TO45_SCORE);
        put(redHtTo60,ScoreType.RED_46TO60_SCORE);
        put(red60To75,ScoreType.RED_61TO75_SCORE);
        put(red75ToFt,ScoreType.RED_76TO90_SCORE);
        /*************黄牌***************/
        put(yellowCard,ScoreType.FT_YELLOW);

        put(yellow0To15,ScoreType.YELLOW_00TO15_SCORE);
        put(yellow15To30,ScoreType.YELLOW_16TO30_SCORE);
        put(yellow30ToHt,ScoreType.YELLOW_31TO45_SCORE);
        put(yellowHtTo60,ScoreType.YELLOW_46TO60_SCORE);
        put(yellow60To75,ScoreType.YELLOW_61TO75_SCORE);
        put(yellow75ToFt,ScoreType.YELLOW_76TO90_SCORE);
        /*********加时赛*********/
        put(round36Scores,ScoreType.FT_PK);
    }};

}
