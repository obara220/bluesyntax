package com.panda.aoodds.esport.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.aoodds.esport.api.entity.*;
import com.panda.aoodds.esport.common.entity.HomeAwayScore;
import com.panda.aoodds.esport.common.enums.ScoreType;
import com.panda.aoodds.esport.common.exception.ApiException;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.panda.aoodds.esport.common.constant.ConvertConstant.*;
import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.*;
import static com.panda.aoodds.esport.common.enums.ScoreType.*;

/**
 * @Author carson
 * @DATE 2022/3/11 16:54
 **/
@Slf4j
@Component
public class SupportMarketHandler {
    @Autowired
    private RedisService redisService;

    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;


    public MarketParamEntiy supportMarketScore(String requestType, MarketParamEntiy marketParamEntiy) {
        if ("1".equals(marketParamEntiy.getIsManualSocre())) {
            return marketParamEntiy;
        }else{
            marketParamEntiy.setMins0to15("0:0");
            marketParamEntiy.setMins15to30("0:0");
            marketParamEntiy.setMins30toht("0:0");
            marketParamEntiy.setMinshtto60("0:0");
            marketParamEntiy.setMins60to75("0:0");
            marketParamEntiy.setMins75toft("0:0");
        }
        Object redisScore = redisService.hGet(AO_ESPORT_SCORE_KEY,  marketParamEntiy.getAoMatchId()+"_"+supportGetScorePeriod(marketParamEntiy.getAoMatchId()));
        Map<ScoreType, HomeAwayScore<Integer>> scoreMap;
        if (null != redisScore) {
            JSONObject scoreJson = JSONObject.parseObject(redisScore.toString());
            scoreMap = SupportMarketHandler.scoreJsonToBean(scoreJson);
            switch (requestType) {
                case "g_goal":
                    marketParamEntiy.setMins0to15(assembleScore(scoreMap.get(FIFTEEN_00TO15_SCORE)));
                    marketParamEntiy.setMins15to30(assembleScore(scoreMap.get(FIFTEEN_16TO30_SCORE)));
                    marketParamEntiy.setMins30toht(assembleScore(scoreMap.get(FIFTEEN_31TO45_SCORE)));
                    marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(FIFTEEN_46TO60_SCORE)));
                    marketParamEntiy.setMins60to75(assembleScore(scoreMap.get(FIFTEEN_61TO75_SCORE)));
                    marketParamEntiy.setMins75toft(assembleScore(scoreMap.get(FIFTEEN_76TO90_SCORE)));
                    break;
                case "g_corner":
                    marketParamEntiy.setMins0to15(assembleScore(scoreMap.get(CORNER_00TO15_SCORE)));
                    marketParamEntiy.setMins15to30(assembleScore(scoreMap.get(CORNER_16TO30_SCORE)));
                    marketParamEntiy.setMins30toht(assembleScore(scoreMap.get(CORNER_31TO45_SCORE)));
                    marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(CORNER_46TO60_SCORE)));
                    marketParamEntiy.setMins60to75(assembleScore(scoreMap.get(CORNER_61TO75_SCORE)));
                    marketParamEntiy.setMins75toft(assembleScore(scoreMap.get(CORNER_76TO90_SCORE)));
                    break;
                case "g_booking":
                    marketParamEntiy.setMins0to15(assembleScore(scoreMap.get(HALF1_BOOKING)));
                    marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(HALF2_BOOKING)));

                    marketParamEntiy.setMins0to15(assembleScore(scoreMap.get(BOOKING_00TO15_SCORE)));
                    marketParamEntiy.setMins15to30(assembleScore(scoreMap.get(BOOKING_16TO30_SCORE)));
                    marketParamEntiy.setMins30toht(assembleScore(scoreMap.get(BOOKING_31TO45_SCORE)));
                    marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(BOOKING_46TO60_SCORE)));
                    marketParamEntiy.setMins60to75(assembleScore(scoreMap.get(BOOKING_61TO75_SCORE)));
                    marketParamEntiy.setMins75toft(assembleScore(scoreMap.get(BOOKING_76TO90_SCORE)));
                    break;
                case "ex_goal":
                    marketParamEntiy.setMins0to15("0:0");
                    marketParamEntiy.setMinshtto60("0:0");
                    if(Lists.newArrayList("41","42","33").contains(marketParamEntiy.getHalf1stOr2nd())){
                        marketParamEntiy.setMins0to15(assembleScore(scoreMap.get(FIFTEEN_00TO15_SCORE)));
                        marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(FIFTEEN_46TO60_SCORE)));
                    }
                    break;
                case "ex_corner":
                    marketParamEntiy.setMins0to15("0:0");
                    marketParamEntiy.setMinshtto60("0:0");
                    if(Lists.newArrayList("41","42","33").contains(marketParamEntiy.getHalf1stOr2nd())) {
                        marketParamEntiy.setMins0to15(assembleScore(scoreMap.get(CORNER_00TO15_SCORE)));
                        marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(CORNER_46TO60_SCORE)));
                    }
                    break;
                case "g_yc":
                    marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(FT_YELLOW)));
                    break;
                case "g_rc":
                    marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(FT_RED)));
                    break;
                case "pk":
                    String roundScoresStr="";
                    if(!scoreJson.containsKey(roundScores)){
                        JSONObject jsonObject = new JSONObject();
                        for (int i = 1; i <= 36; i++) {
                            jsonObject.put("home" + i, "n");
                            jsonObject.put("away" + i, "n");
                        }
                        roundScoresStr = jsonObject.toJSONString();
                    }else{
                        roundScoresStr=scoreJson.get(roundScores).toString();
                    }
                    marketParamEntiy.setScore(roundScoresStr);
                    marketParamEntiy.setPointNum(scoreJson.containsKey(POINT_NUM)?scoreJson.getInteger(POINT_NUM):0);
                    break;
                default:
                    break;
            }
        }else{
            if(requestType.equals("pk")){
                JSONObject jsonObject = new JSONObject();
                for (int i = 1; i <= 36; i++) {
                     jsonObject.put("home" + i, "n");
                     jsonObject.put("away" + i, "n");
                }
                marketParamEntiy.setScore(jsonObject.toJSONString());
                marketParamEntiy.setPointNum(0);
            }
        }
        return marketParamEntiy;
    }

    public  MarketParamEntiy supportEsportFootballScores(MarketParamEntiy marketParamEntiy){
        Object scores = redisService.hGet(AO_ESPORT_SCORE_KEY , marketParamEntiy.getAoMatchId()+"_"+supportGetScorePeriod(marketParamEntiy.getAoMatchId()));
        log.info("supportEsportFootballScores 1 {}",JSONObject.toJSONString(marketParamEntiy));

        if(Objects.isNull(scores)){
            return marketParamEntiy;
        }
        log.info("supportEsportFootballScores 2 {}",JSONObject.toJSONString(scores));
        JSONObject redisScore = JSONObject.parseObject(scores.toString());
        Map<ScoreType, HomeAwayScore<Integer>>  scoreMap = SupportMarketHandler.scoreJsonToBean(redisScore);
        log.info("supportEsportFootballScores 3 {}",JSONObject.toJSONString(scoreMap));
        marketParamEntiy.setMins0to15(assembleScore(scoreMap.get(ScoreType.HALF1_SCORE)));
        marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(ScoreType.HALF2_SCORE)));
        return marketParamEntiy;
    }

    public  Map<ScoreType, HomeAwayScore<Integer>> supportEsporScores(MarketParamEntiy marketParamEntiy){
        Object scores = redisService.hGet(AO_ESPORT_SCORE_KEY , marketParamEntiy.getAoMatchId()+"_"+supportGetScorePeriod(marketParamEntiy.getAoMatchId()));
        log.info("supportEsportFootballScores 1 {}",JSONObject.toJSONString(marketParamEntiy));

        if(Objects.isNull(scores)){
            return null;
        }
        log.info("supportEsportFootballScores 2 {}",JSONObject.toJSONString(scores));
        JSONObject redisScore = JSONObject.parseObject(scores.toString());
        Map<ScoreType, HomeAwayScore<Integer>>  scoreMap = SupportMarketHandler.scoreJsonToBean(redisScore);
        log.info("supportEsportFootballScores 3 {}",JSONObject.toJSONString(scoreMap));
        marketParamEntiy.setMins0to15(assembleScore(scoreMap.get(ScoreType.HALF1_SCORE)));
        marketParamEntiy.setMinshtto60(assembleScore(scoreMap.get(ScoreType.HALF2_SCORE)));
        return SupportMarketHandler.scoreJsonToBean(JSONObject.parseObject(scores.toString()));
    }

    public BasketBallScoresParam supportBkEspotMarketSocres(BasketballApplyParam basketballApplyParam){
        log.info("rev入参请求231:{}",  JSON.toJSONString(basketballApplyParam));
        Object scores = redisService.hGet(AO_ESPORT_SCORE_KEY , basketballApplyParam.getAoMatchId());
        if(basketballApplyParam.getQuarters()==4&&basketballApplyParam.getQuarterMin()==12&&Objects.isNull(scores)){
            basketballApplyParam.setQ1Score12to6("0:0");
            basketballApplyParam.setQ1Score6to0("0:0");

            basketballApplyParam.setQ2Score12to6("0:0");
            basketballApplyParam.setQ2Score6to0("0:0");

            basketballApplyParam.setQ3Score12to6("0:0");
            basketballApplyParam.setQ3Score6to0("0:0");

            basketballApplyParam.setQ4Score12to6("0:0");
            basketballApplyParam.setQ4Score6to0("0:0");
            return basketballApplyParam;
        }else if(Objects.isNull(scores)){

            return basketballApplyParam;
        }

        JSONObject jsonScores = JSONObject.parseObject(scores.toString());
        basketballApplyParam.setH1Score(stringScore(jsonScores.getJSONObject("1").getJSONObject("matchScore")));
        basketballApplyParam.setH2Score(stringScore(jsonScores.getJSONObject("2").getJSONObject("matchScore")));
        basketballApplyParam.setQ1Score(stringScore(jsonScores.getJSONObject("13").getJSONObject("matchScore")));
        basketballApplyParam.setQ2Score(stringScore(jsonScores.getJSONObject("14").getJSONObject("matchScore")));
        basketballApplyParam.setQ3Score(stringScore(jsonScores.getJSONObject("15").getJSONObject("matchScore")));
        basketballApplyParam.setQ4Score(stringScore(jsonScores.getJSONObject("16").getJSONObject("matchScore")));
        basketballApplyParam.setFtScore(stringScore(jsonScores.getJSONObject(BK_FULL_SCORES)));
        basketballApplyParam.setOtScore(stringScore(jsonScores.getJSONObject("40").getJSONObject("matchScore")));
        if(basketballApplyParam.getQuarters()==4&&basketballApplyParam.getQuarterMin()==12){
            basketballApplyParam.setQ1Score12to6(stringScore(jsonScores.getJSONObject("1312").getJSONObject("matchScore")));
            basketballApplyParam.setQ1Score6to0(stringScore(jsonScores.getJSONObject("1306").getJSONObject("matchScore")));

            basketballApplyParam.setQ2Score12to6(stringScore(jsonScores.getJSONObject("1412").getJSONObject("matchScore")));
            basketballApplyParam.setQ2Score6to0(stringScore(jsonScores.getJSONObject("1406").getJSONObject("matchScore")));

            basketballApplyParam.setQ3Score12to6(stringScore(jsonScores.getJSONObject("1512").getJSONObject("matchScore")));
            basketballApplyParam.setQ3Score6to0(stringScore(jsonScores.getJSONObject("1506").getJSONObject("matchScore")));

            basketballApplyParam.setQ4Score12to6(stringScore(jsonScores.getJSONObject("1612").getJSONObject("matchScore")));
            basketballApplyParam.setQ4Score6to0(stringScore(jsonScores.getJSONObject("1606").getJSONObject("matchScore")));
        }

        return basketballApplyParam;
    }

    public  Map<ScoreType, HomeAwayScore<Integer>> getScoreObj(String aoMatchId){
        Object scores = redisService.hGet(AO_ESPORT_SCORE_KEY , aoMatchId+"_"+supportGetScorePeriod(aoMatchId));
        if(Objects.isNull(scores)){
            return null;
        }
        return SupportMarketHandler.scoreJsonToBean(JSONObject.parseObject(scores.toString()));
    }
    public String stringScore(JSONObject jsonScore){
        if(jsonScore.containsKey("home")){
            Integer home =jsonScore.getInteger("home");
            Integer away = jsonScore.getInteger("away");
            return home+":"+away;
        }
        return null;
    }
    public MarketParamEntiy supportInjTime(MarketParamEntiy marketParamEntiy) {
        Object injTime1st = redisService.hGet(AO_INJTIME_KEY + marketParamEntiy.getAoMatchId(), "injTime1st");
        Object injTime2nd = redisService.hGet(AO_INJTIME_KEY + marketParamEntiy.getAoMatchId(), "injTime2nd");
        if(marketParamEntiy.getRequestType().contains("ex")){
             injTime1st = redisService.hGet(AO_INJTIME_KEY + marketParamEntiy.getAoMatchId()+"ex", "injTime1st");
             injTime2nd = redisService.hGet(AO_INJTIME_KEY + marketParamEntiy.getAoMatchId()+"ex", "injTime2nd");
        }
        if (!Objects.isNull(injTime1st)) {
            marketParamEntiy.setInjTime1st(injTime1st.toString());
        }
        if (!Objects.isNull(injTime2nd)) {
            marketParamEntiy.setInjTime2nd(injTime2nd.toString());
        }
        return marketParamEntiy;
    }

    public String supportMatchTime(MarketParamEntiy marketParamEntiy) {
        if ("pk".equals(marketParamEntiy.getRequestType()) || "g_rc".equals(marketParamEntiy.getRequestType())) {
            return "0";
        }
        String injTime1st = StringUtils.isEmpty(marketParamEntiy.getInjTime1st()) ? "2" : marketParamEntiy.getInjTime1st();
        String injTime2nd = StringUtils.isEmpty(marketParamEntiy.getInjTime2nd()) ? "4" : marketParamEntiy.getInjTime2nd();
        Integer matchTime = (Integer.valueOf(marketParamEntiy.getHalf1stPeriod()) + Integer.valueOf(injTime1st)) * 60;
        if (marketParamEntiy.getHalf1stOr2nd().equals("1") && Integer.valueOf(marketParamEntiy.getMatchClock()) >= matchTime) {
            return (Integer.valueOf(marketParamEntiy.getHalf1stPeriod()) + Integer.valueOf(injTime1st)) + "";
        }
        matchTime = (Integer.valueOf(marketParamEntiy.getHalf1stPeriod()) * 2 + Integer.valueOf(injTime1st) + Integer.valueOf(injTime2nd)) * 60;
        if (marketParamEntiy.getHalf1stOr2nd().equals("2") && Integer.valueOf(marketParamEntiy.getMatchClock()) >= matchTime) {
            return (Integer.valueOf(marketParamEntiy.getHalf1stPeriod()) * 2 + Integer.valueOf(injTime1st) + Integer.valueOf(injTime2nd)) + "";
        }
        if (marketParamEntiy.getMatchClock().equals("0")) {
            return "0";
        }
        Integer matchClock = Integer.valueOf(marketParamEntiy.getMatchClock());
        Integer second = matchClock % 60;
        Integer mins = Math.floorDiv(matchClock, 60);

        return mins + "." + second;
    }
    public MarketParamEntiy supportEsportFootballPeriod(MarketParamEntiy marketParamEntiy) {
        Object matchTimer = redisService.hGet(AO_ESPORT_BGTIME_KEY, marketParamEntiy.getAoMatchId());
        marketParamEntiy.setMatchClock("0");
        marketParamEntiy.setHalf1stOr2nd("1");

        if (null != matchTimer) {
            String[] prid = matchTimer.toString().split("#");
            if (Integer.parseInt(prid[1]) == 6) {
                marketParamEntiy.setHalf1stOr2nd("1");
            } else if (Integer.parseInt(prid[1]) == 7) {
                marketParamEntiy.setHalf1stOr2nd("2");
            } else {
                marketParamEntiy.setHalf1stOr2nd(prid[1]);
            }
            if (Integer.parseInt(prid[1]) == 31||Integer.parseInt(prid[1]) == 33) {
//                String injTime1st ="0";
//                String half1stPeriod = StringUtils.isEmpty(marketParamEntiy.getHalf1stPeriod()) ? "0" : marketParamEntiy.getHalf1stPeriod();
                marketParamEntiy.setMatchClock((45* 60) + "");
            } else {
                Long diffValue =0L;
                try {
                     diffValue = (System.currentTimeMillis()- Long.parseLong(prid[0]))/1000;
                     if(diffValue<1){
                         diffValue =0L;
                     }
                }catch (Exception ex){
                    log.info("时间计算错误:{}",marketParamEntiy.getAoMatchId());
                    diffValue =0L;
                }
                Long matchFormetTime = Integer.valueOf(prid[2])+diffValue;
                marketParamEntiy.setMatchClock( matchFormetTime+ "");
                marketParamEntiy.setMatchClock(marketParamEntiy.getMatchClock().equals("0") ? "1" : marketParamEntiy.getMatchClock());
            }

            if(Integer.valueOf(marketParamEntiy.getMatchClock())<1){
                marketParamEntiy.setMatchClock("1");
            }
        }
        return marketParamEntiy;
    }


    public Integer supportGetPeriod(String aoMatchId) {
        Object matchTimer = redisService.hGet(AO_ESPORT_BGTIME_KEY, aoMatchId);
        if (null != matchTimer) {
            String[] prid = matchTimer.toString().split("#");
            return Integer.valueOf(prid[1]);
        }
        return 0;
    }

    public Integer getMatchType(String aoMatchId){
//        Object cacheMatchType = redisService.hGet(AO_ESPORT_FORMTIME_TO_LIVE,aoMatchId+"_"+2);
//        Integer formTimeMatchType = Objects.isNull(cacheMatchType)?0:Integer.valueOf(cacheMatchType.toString());
        Integer eventPerIod = supportGetBkPeriod(aoMatchId)[1];
        Integer period = (eventPerIod>0)?1:0;
        return period;
    }

    public static void main(String[] args) {
       Integer aa=3;
       Integer bb=2;
       Integer cc=7/3;


        System.out.println(cc);
    }

    public Integer[] supportGetBkPeriod(String aoMatchId) {
        Object matchTimer = redisService.hGet(AO_ESPORT_BK_BGTIME_KEY, aoMatchId);

        Integer[] period=new Integer[2];
        if (null != matchTimer) {
            String[] prid = matchTimer.toString().split("#");
            period[0]=Integer.valueOf(prid[0]) * 2;
            period[1]=Integer.valueOf(prid[1]);
            if(prid.length==4){
                Long eventTime = Long.valueOf(prid[2]);
                Long diffTime = (System.currentTimeMillis()-eventTime)/1000;
                if(!Arrays.asList(301,302,303,100,32,110,61,80,90,31,999).contains(period[1])&&prid[3].equals("1")){
                    period[0]=period[0]-Integer.valueOf(diffTime.toString());
                }
            }
            return period;
        }
        period[0]=0;
        period[1]=0;
        return period;
    }

    /**
     * 设置篮球赛制
     */
    public BasketballApplyOptParam templateToApplyOptParam(AoEsBasketBallTemplateConfigEntity aoBasketBallTemplateConfigEntity, BasketballApplyOptParam basketballApplyOptParam){
        BeanUtils.copyProperties(aoBasketBallTemplateConfigEntity, basketballApplyOptParam);
        basketballApplyOptParam.setQuarters(4);
        basketballApplyOptParam.setQuarterMin(10);
        return basketballApplyOptParam;
    }
    public Integer supportGetScorePeriod(String aoMatchId) {
        Integer period = supportGetPeriod(aoMatchId);
        if(period==31){
            period=6;
        }else if(period == 33){
            period = 41;
        }
        return period;
    }
    public static Map<ScoreType, HomeAwayScore<Integer>> scoreJsonToBean(JSONObject jsonScore) {
        Map<ScoreType, HomeAwayScore<Integer>> scoreMap = new HashMap<>();

        jsonScore.forEach((k, v) -> {
            ScoreType scoreType = SCORE_MAP.get(k);
            if (null != scoreType) {

                JSONObject homeAndAway = JSONObject.parseObject(v.toString());
                scoreMap.put(scoreType, new HomeAwayScore<>(homeAndAway.getInteger("home"), homeAndAway.getInteger("away")));
            }
        });
        return scoreMap;
    }

    public String assembleScore(HomeAwayScore<Integer> homeAwayScore) {
        if(null==homeAwayScore){
            return "0:0";
        }

        return homeAwayScore.getHomeScore() + ":" + homeAwayScore.getAwayScore();
    }


    public void isPdScore(String aoMatchId,String isManualSocre,String requestType){
        if(Lists.newArrayList("g_goal","ex_corner","ex_goal").contains(requestType)){
            return ;
        }
        Object aodspd = redisService.hGet(AO_DS_PD,aoMatchId);
        Boolean isPdScore = Objects.isNull(aodspd);
        if(isPdScore){
            return;
        }
        if(aodspd.toString().contains("PD")&&!isManualSocre.equals("1")){
            throw new ApiException("No 15min Score，can't use Rev");
        }
    }
    //标准阶段转换
    public Integer standardProidTransition(String proid){
            if(proid.equals("1")){
                return 6;
            }else if(proid.equals("2")){
                return 7;
            }
            return Integer.valueOf(proid);
    }
    public BasketBallScoresParam supportBkMarketSocres(BasketBallScoresParam basketballApplyParam){
        log.info("rev入参请求231:{}",  JSON.toJSONString(basketballApplyParam));
        Object scores = redisService.hGet(AO_ESPORT_SCORE_KEY  , basketballApplyParam.getAoMatchId());
        if(basketballApplyParam.getQuarters()==4&&basketballApplyParam.getQuarterMin()==12&&Objects.isNull(scores)){
            basketballApplyParam.setQ1Score12to6("0:0");
            basketballApplyParam.setQ1Score6to0("0:0");

            basketballApplyParam.setQ2Score12to6("0:0");
            basketballApplyParam.setQ2Score6to0("0:0");

            basketballApplyParam.setQ3Score12to6("0:0");
            basketballApplyParam.setQ3Score6to0("0:0");

            basketballApplyParam.setQ4Score12to6("0:0");
            basketballApplyParam.setQ4Score6to0("0:0");
            return basketballApplyParam;
        }else if(Objects.isNull(scores)){

            return basketballApplyParam;
        }

        JSONObject jsonScores = JSONObject.parseObject(scores.toString());
        basketballApplyParam.setH1Score(stringScore(jsonScores.getJSONObject("1").getJSONObject("matchScore")));
        basketballApplyParam.setH2Score(stringScore(jsonScores.getJSONObject("2").getJSONObject("matchScore")));
        basketballApplyParam.setQ1Score(stringScore(jsonScores.getJSONObject("13").getJSONObject("matchScore")));
        basketballApplyParam.setQ2Score(stringScore(jsonScores.getJSONObject("14").getJSONObject("matchScore")));
        basketballApplyParam.setQ3Score(stringScore(jsonScores.getJSONObject("15").getJSONObject("matchScore")));
        basketballApplyParam.setQ4Score(stringScore(jsonScores.getJSONObject("16").getJSONObject("matchScore")));
        basketballApplyParam.setFtScore(stringScore(jsonScores.getJSONObject(BK_FULL_SCORES)));
        basketballApplyParam.setOtScore(stringScore(jsonScores.getJSONObject("40").getJSONObject("matchScore")));
        if(basketballApplyParam.getQuarters()==4&&basketballApplyParam.getQuarterMin()==12){
            basketballApplyParam.setQ1Score12to6(stringScore(jsonScores.getJSONObject("1312").getJSONObject("matchScore")));
            basketballApplyParam.setQ1Score6to0(stringScore(jsonScores.getJSONObject("1306").getJSONObject("matchScore")));

            basketballApplyParam.setQ2Score12to6(stringScore(jsonScores.getJSONObject("1412").getJSONObject("matchScore")));
            basketballApplyParam.setQ2Score6to0(stringScore(jsonScores.getJSONObject("1406").getJSONObject("matchScore")));

            basketballApplyParam.setQ3Score12to6(stringScore(jsonScores.getJSONObject("1512").getJSONObject("matchScore")));
            basketballApplyParam.setQ3Score6to0(stringScore(jsonScores.getJSONObject("1506").getJSONObject("matchScore")));

            basketballApplyParam.setQ4Score12to6(stringScore(jsonScores.getJSONObject("1612").getJSONObject("matchScore")));
            basketballApplyParam.setQ4Score6to0(stringScore(jsonScores.getJSONObject("1606").getJSONObject("matchScore")));
        }

        return basketballApplyParam;

    }

}
