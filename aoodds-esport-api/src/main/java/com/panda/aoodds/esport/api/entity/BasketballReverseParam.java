package com.panda.aoodds.esport.api.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BasketballReverseParam extends BasketballMarketParam{
    Double ftSup;
    Double ftGe;


    /**
     * 选择reverse数据源
     */
    String dataSourceCode;

    private Double ftAhHandicap;

    private Double ftAhOdds1;

    private Double ftAhOdds2;
    private Double ftAhExtraHandicap;
    private Double ftAhExtraOdds1;
    private Double ftAhExtraOdds2;
    /**
     * 全场独赢
     */
    private Double ftWinnerOdds1;

    private Double ftWinnerOdds2;

    /**
     * 全场大小主盘口
     */
    private Double ftOuHandicap;

    private Double ftOuOdds1;

    private Double ftOuOdds2;

    /**
     * 全场大小附加盘口
     */
    private Double ftOuExtraHandicap;

    private Double ftOuExtraOdds1;

    private Double ftOuExtraOdds2;

    /********半场盘口************************/


    private Double ht1AhHandicap;

    private Double ht1AhOdds1;

    private Double ht1AhOdds2;
    private Double ht1AhExtraHandicap;
    private Double ht1AhExtraOdds1;
    private Double ht1AhExtraOdds2;
    /**
     * 全场独赢
     */
    private Double ht1WinnerOdds1;

    private Double ht1WinnerOdds2;

    /**
     * 全场大小主盘口
     */
    private Double ht1OuHandicap;

    private Double ht1OuOdds1;

    private Double ht1OuOdds2;

    /**
     * 全场大小附加盘口
     */
    private Double ht1OuExtraHandicap;

    private Double ht1OuExtraOdds1;

    private Double ht1OuExtraOdds2;
    /************Q1 盘口*******************/

    private Double q1AhHandicap;

    private Double q1AhOdds1;

    private Double q1AhOdds2;
    private Double q1AhExtraHandicap;
    private Double q1AhExtraOdds1;
    private Double q1AhExtraOdds2;
    /**
     * 全场独赢
     */
    private Double q1WinnerOdds1;

    private Double q1WinnerOdds2;

    /**
     * 全场大小主盘口
     */
    private Double q1OuHandicap;

    private Double q1OuOdds1;

    private Double q1OuOdds2;

    /**
     * 全场大小附加盘口
     */
    private Double q1OuExtraHandicap;

    private Double q1OuExtraOdds1;

    private Double q1OuExtraOdds2;
    /************Q3 盘口*******************/

    private Double q3AhHandicap;

    private Double q3AhOdds1;

    private Double q3AhOdds2;
    private Double q3AhExtraHandicap;
    private Double q3AhExtraOdds1;
    private Double q3AhExtraOdds2;
    /**
     * 全场独赢
     */
    private Double q3WinnerOdds1;

    private Double q3WinnerOdds2;

    /**
     * 全场大小主盘口
     */
    private Double q3OuHandicap;

    private Double q3OuOdds1;

    private Double q3OuOdds2;

    /**
     * 全场大小附加盘口
     */
    private Double q3OuExtraHandicap;

    private Double q3OuExtraOdds1;

    private Double q3OuExtraOdds2;

    public Double getFtSup() {
        if(null!=ftSup){
            return new BigDecimal(String.valueOf(ftSup)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        return ftSup;
    }

    public void setFtSup(Double ftSup) {
        if(null!=ftSup){
            this.ftSup = new BigDecimal(String.valueOf(ftSup)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return;
        }
        this.ftSup = ftSup;
    }

    public Double getFtGe() {
        if(null!=ftGe){
            return new BigDecimal(String.valueOf(ftGe)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        return ftGe;
    }

    public void setFtGe(Double ftGe) {
        if(null!=ftGe){
            this.ftGe = new BigDecimal(String.valueOf(ftGe)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return;
        }
        this.ftGe = ftGe;
    }
}
