package com.panda.aoodds.esport.common.entity;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @Author carson
 * @DATE 2022/3/7 11:26
 **/
@Data
@Document(collection = "match_template_config")
public class MatchTemplateConfig implements java.io.Serializable{
    String id;
    /**上半场常规时间 例:half1stPeriod = 45(分钟)**/
    Integer half1stPeriod;
    /**00-15 例:dis0to151H =0.295**/
    Double dis0to151H;
    /**同上**/
    Double dis15to301H;
    /**同上**/
    Double dis30toHT;
    /**同上**/
    Double dis45to602H;
    /**同上**/
    Double dis60to752H;
    /**同上**/
    Double dis75toFT;
    /**标准赛事ID**/
    Long standardMatchId;
    String tempType;
    /***联赛分级. 1: 一级联赛; 2:二级联赛; 3: 三级联赛; 以此类推; 0: 未分级***/
    Integer tournamentLevel;
    Integer sportId;
    String machineId;
    Long modifyTime = System.currentTimeMillis();

    /**间隔刷新时长 例:refresh=10 (秒)**/
    Integer refresh;


}
