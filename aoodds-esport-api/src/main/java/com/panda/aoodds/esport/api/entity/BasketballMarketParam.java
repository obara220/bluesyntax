package com.panda.aoodds.esport.api.entity;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class BasketballMarketParam extends BasketBallScoresParam implements java.io.Serializable {
    String modelCode;




    String id;

    String userName;
    String userid;
    Integer periodRemainSec;

    String lineVersion;
    Integer closingGe;
    Integer closingTime;
    Integer overtime;
    Integer useOuSd=0;
    Integer handicapModel;
    Double ahSd;
    Double ouSd;
    String linkId;
    String mode;
    Long createTime;
    String dataSourceCode;
    Integer followedStatus = 0 ; //rev-copy-apply = 1 ,自动rev = 1 ，直接apply = 0
    Double xgdHT;
    Double xgdQ1;
    Double xgdQ3;
    Boolean autoApply = Boolean.FALSE;
    Boolean autoRev = Boolean.FALSE;
    Integer matchUiStatus;
    /**
     * 1勾选 0未勾选，new
     */
    private String newVersion="0";
    public Long getCreateTime() {
        return System.currentTimeMillis();
    }

    public void setCreateTime(Long createTime) {
        this.createTime = System.currentTimeMillis();
    }

    public String getModelCode() {
        if(null!=quarters&&quarters==2){
            modelCode= "1";
        }else{
            modelCode= "111";
        }
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        if(null!=quarters&&quarters==2){
            modelCode= "1";
        }else{
            modelCode= "111";
        }
    }

    public Double getAhSd() {
        if(null!=ahSd){
            return new BigDecimal(String.valueOf(ahSd)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        return ahSd;
    }

    public void setAhSd(Double ahSd) {
        if(null!=ahSd){
            this.ahSd = new BigDecimal(String.valueOf(ahSd)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return;
        }
        this.ahSd = ahSd;
    }

    public Double getOuSd() {
        if(null!=ouSd){
            return new BigDecimal(String.valueOf(ouSd)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        return ouSd;
    }

    public void setOuSd(Double ouSd) {
        if(null!=ouSd){
            this.ouSd = new BigDecimal(String.valueOf(ouSd)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return ;
        }
        this.ouSd = ouSd;
    }
}
