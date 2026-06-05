package com.panda.aoodds.esport.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 属性验证
 */
@Slf4j
public class verifyParam {

    /**
     * 全场足球校验
     */
    public final static List<String> BALL_HEAD_VERIFY = Arrays.asList("ftOuHandicap", "ftOuOdds1", "ftOuOdds2", "ftAhHandicap", "ftAhOdds1", "ftAhOdds2"
            ,"htOuHandicap", "htOuOdds1", "htOuOdds2", "htAhHandicap", "htAhOdds1", "htAhOdds2" );

    /**
     * 上半场足球校验
     */
    public final static List<String> BALL_HEAD_VERIFY_HT_PERIOD = Arrays.asList("ftOuHandicap", "ftOuOdds1", "ftOuOdds2", "ftAhHandicap", "ftAhOdds1", "ftAhOdds2");


    /**
     * 篮球校验
     */
    public final static List<String> BASKETBALL_HEAD_VERIFY = Arrays.asList("ftAhHandicap", "ftAhOdds1", "ftAhOdds2", "ftOuHandicap"
            , "ftOuOdds1", "ftOuOdds2");

    /**
     * 忽略
     */
    public final static List<String> IGNORE = Arrays.asList("aoMatchId", "standardMatchId","matchType");
    public static final Map<String, List<Integer>> REQUEST_TYPE_PERIOD = ImmutableMap.<String, List<Integer>> builder()
            .put( "g_goal", Arrays.asList(0,1, 2, 31,8,100,999))
            .put("g_corner",Arrays.asList(0,1, 2, 31,8,100,999))
            .put("g_booking", Arrays.asList(0,1, 2, 31,8,100,999))
            .put("g_yc", Arrays.asList(0,1, 2, 31,8,100,999))
            .put("g_rc", Arrays.asList(0,1, 2, 31,8,100,999))
            .put("ex_goal", Arrays.asList(32,41, 33, 42,43,110,999))
            .put("ex_corner", Arrays.asList(32,41, 33, 42,43,110,999))
            .put("pk", Arrays.asList(34,50, 120,999))
            .build();

    public static List<String> MATCH_TEMPLATE_CONFIG_VERIFY = new ArrayList<>();
    /**
     * 上半场不校验下半场球头的阶段
     */
    public static List<String> period = Arrays.asList("2", "42", "33", "31");

    /**
     * 足球 球头属性 校验
     *
     * @return
     */
    public static Boolean ballHeadVerify(String linkId, Properties properties) {
        String half1stOr2nd = properties.getProperty("half1stOr2nd");
        Boolean isTrue = Boolean.TRUE;
        //满足阶段，只校验上半场球头，不满足所有球头都校验
        List<String> BALL_HEAD = period.contains(half1stOr2nd) ? BALL_HEAD_VERIFY_HT_PERIOD : BALL_HEAD_VERIFY;
        for (int i = 0; i < BALL_HEAD.size(); i++) {
            String key = BALL_HEAD.get(i);
            if (!IGNORE.contains(key) && !properties.containsKey(key)) {
                isTrue = Boolean.FALSE;
                log.info("::{}::【reverseParam】Ao赛事ID:{},球头KEY缺失:{},阶段：{}", linkId, properties.get("aoMatchId"), key, half1stOr2nd);
            }
        }
        return isTrue;
    }
    public static Boolean periodVerify(Integer period,String requestType){
        return REQUEST_TYPE_PERIOD.get(requestType).contains(period);

    }
    /**
     * 篮球球头属性 校验
     *
     * @return
     */
    public static Boolean basketballHeadVerify(String linkId, Properties properties) {
        Boolean isTrue = Boolean.TRUE;
        for (int i = 0; i < BASKETBALL_HEAD_VERIFY.size(); i++) {
            String key = BASKETBALL_HEAD_VERIFY.get(i);
            if (!IGNORE.contains(key) && !properties.containsKey(key)) {
                isTrue = Boolean.FALSE;
                log.info("::{}::【reverseParam】Ao赛事ID:{},球头KEY缺失:{}", linkId, properties.get("aoMatchId"), key);
            }
        }
        return isTrue;
    }


    /**
     * 模板参数 校验
     *
     * @return
     */
    public static Boolean matchTemplateConfigVerify(Properties properties) {
        Boolean isTrue = Boolean.TRUE;
        if (MATCH_TEMPLATE_CONFIG_VERIFY.size() == 0) {
            initParam();
        }
        for (int i = 0; i < MATCH_TEMPLATE_CONFIG_VERIFY.size(); i++) {
            String key = MATCH_TEMPLATE_CONFIG_VERIFY.get(i);
            if (!IGNORE.contains(key) && !properties.containsKey(key)) {
                isTrue = Boolean.FALSE;
                log.info("Ao赛事ID:{},模板参数KEY缺失:{}", properties.get("aoMatchId"), key);
            }
        }
        return isTrue;
    }

    public static void initParam() {
        try {
            Class<?> clazz = Class.forName("com.panda.aoodds.common.entity.MatchTemplateConfig");
            Field[] field = clazz.getDeclaredFields();
            for (int i = 0; i < field.length; i++) {
                String name = field[i].getName();
                MATCH_TEMPLATE_CONFIG_VERIFY.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Double DNAN(Double d1){

        return d1.isNaN()||d1.isInfinite()?0.0:d1;
    }
    public static String getScoreType(String requestType) {
        if (requestType.contains("goal")) {
            return "goal";
        }
        if (requestType.contains("corner")) {
            return "corner";
        }
        if (requestType.contains("book")) {
            return "booking";
        }
        if (requestType.contains("rc")) {
            return "rc";
        }
        if (requestType.contains("yc")) {
            return "yc";
        }
        if (requestType.contains("pk")) {
            return "pk";
        }
        return "goal";
    }

    /**
     * 组装rev球头数据
     *
     * @param linkId
     * @param aoMatchId      赛事ID
     * @param dataSourceCode 数据源
     * @param propertiesMap  缓存数据
     * @return
     */
    public static Properties fusionBallData(String linkId, String aoMatchId, String dataSourceCode, Map<String, Properties> propertiesMap) {
        Properties propertiesNew = new Properties();
        if (MapUtils.isEmpty(propertiesMap)) {
            log.info("::{}::组装rev球头数据，赛事ID：{}，数据源：{}，缓存不存在不处理", linkId, aoMatchId, dataSourceCode);
            return propertiesNew;
        }
        log.info("::{}::组装rev球头数据，赛事ID：{}，数据源：{}，缓存数据：{}", linkId, aoMatchId, dataSourceCode, JSONObject.toJSONString(propertiesMap));
        for (Properties p : propertiesMap.values()) {
            propertiesNew.putAll(p);
        }
        log.info("::{}::组装rev球头数据，赛事ID：{}，数据源：{}，最终返回数据：{}", linkId, aoMatchId, dataSourceCode, JSONObject.toJSONString(propertiesNew));
        return propertiesNew;
    }

}
