package com.panda.aoodds.esport.api.entity;

import lombok.Data;

@Data
public class BasketBallScoresParam implements java.io.Serializable {
    String q1Score="0:0";
    String q2Score="0:0";
    String q3Score="0:0";
    String  q4Score="0:0";
    String  h1Score="0:0";
    String h2Score="0:0";
    String ftScore="0:0";
    String otScore="0:0";
    /**
     * 第一节12-7比分（倒计时）
     */
    private String q1Score12to6;
    /**
     * 第一节6-0比分（倒计时）
     */
    private String q1Score6to0;
    /**
     * 第二节12-7比分（倒计时）
     */
    private String q2Score12to6;
    /**
     * 第二节6-0比分（倒计时）
     */
    private String q2Score6to0;
    /**
     * 第三节12-7比分（倒计时）
     */
    private String q3Score12to6;
    /**
     * 第三节6-0比分（倒计时）
     */
    private String q3Score6to0;
    /**
     * 第四节12-7比分（倒计时）
     */
    private String q4Score12to6;
    /**
     * 第四节6-0比分（倒计时）
     */
    private String q4Score6to0;
    Integer periodId;
    String aoMatchId;
    String sportId;
    Integer quarters;
    Integer quarterMin;
    Integer shotClock;
    String matchManageId;
    public String getH1Score() {
        if(null!=quarters&&quarters==4){
           return (Integer.valueOf(q1Score.split(":")[0])+Integer.valueOf(q2Score.split(":")[0]))+":"+(Integer.valueOf(q1Score.split(":")[1])+Integer.valueOf(q2Score.split(":")[1]));
        }
        return h1Score;
    }

    public void setH1Score(String h1Score) {
        if(null!=quarters&&quarters==4){
            this.h1Score = (Integer.valueOf(q1Score.split(":")[0])+Integer.valueOf(q2Score.split(":")[0]))+":"+(Integer.valueOf(q1Score.split(":")[1])+Integer.valueOf(q2Score.split(":")[1]));
        }
        this.h1Score = h1Score;
    }

    public String getH2Score() {
        if(null!=quarters&&quarters==4){
            return (Integer.valueOf(q3Score.split(":")[0])+Integer.valueOf(q4Score.split(":")[0]))+":"+(Integer.valueOf(q3Score.split(":")[1])+Integer.valueOf(q4Score.split(":")[1]));
        }
        return h2Score;
    }

    public void setH2Score(String h2Score) {
        if(null!=quarters&&quarters==4){
            this.h2Score = (Integer.valueOf(q3Score.split(":")[0])+Integer.valueOf(q4Score.split(":")[0]))+":"+(Integer.valueOf(q3Score.split(":")[1])+Integer.valueOf(q4Score.split(":")[1]));
        }
        this.h2Score = h2Score;
    }
}
