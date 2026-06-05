package com.panda.aoodds.esport.api.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * @Author carson
 * @DATE 2022/1/16 16:42
 **/
@Data
@NoArgsConstructor
public class BetPieceEntity implements Serializable {
    /**
     * 盘口ID：玩法ID_盘口值
     */
    private String marketId;
    /**
     * 投注项Id
     */
    private String betPriceId;

    /**
     * 投注项名称
     */
    private String name;

    /**
     * 赔率
     */
    private String odds = "0";

    /**
     * 投注项排序值
     */
    private Integer order;

    /**
     * 投注项是否激活
     */
    private boolean active;

    /**
     * 概率值
     */
    private Double probabilities;

    /**
     * 投注项获胜概率值
     */
    private Double winProb;

    /**
     * 投注项走水概率
     */
    private Double refundProb;

    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 水差值
     */
    private Double marketDiffValue = 0D;
    /**
     * 马来赔
     */
    private Double malayOddsValue = 0D;
    /**
     * 投注项AO赔率
     */
    private Double aoOddsValue = 0D;

    Double margin;

    /**
     * 下盘标识，用于特殊抽水需求 true:下  false:上
     */
    private Boolean oddsTypeTag = Boolean.FALSE;

    /**
     * 次要玩法重新抽水标记
     */
    private Boolean oddsTypeSecondaryTag = Boolean.FALSE;

    /**
     * 全参构造方法（将收到的赔率数据精度调整为小数点后3位）
     *
     * @param betPriceId 投注项Id
     * @param name       投注项名称
     * @param odds       赔率
     * @param order      投注项排序值
     * @param active     投注项是否激活
     */
    public BetPieceEntity(String betPriceId, String name, String odds, Integer order, boolean active) {
        this(betPriceId, name, order);
        this.active = active;
        this.setOdds(odds);
    }

    /**
     * 构造方法
     *
     * @param betPriceId    投注项Id
     * @param name          投注项名称
     * @param order         投注项排序值
     * @param probabilities 投注项原始概率
     */
    public BetPieceEntity(String betPriceId, String name, Integer order, Double probabilities) {
        this(betPriceId, name, order);
        this.setProbabilities(probabilities);
    }

    /**
     * 构造方法
     *
     * @param betPriceId 投注项Id
     * @param name       投注项名称
     * @param order      投注项排序值
     */
    public BetPieceEntity(String betPriceId, String name, Integer order) {
        this.betPriceId = betPriceId;
        this.name = name;
        this.order = order;
    }

    public void setOdds(String odds) {
        if (null != odds && !odds.isEmpty()) {
            this.odds = new BigDecimal(odds).setScale(3, RoundingMode.DOWN).toString();
        } else {
            this.odds = odds;
        }
    }

    public void setProbabilities(Double probabilities) {
        if (null == probabilities) {
            this.probabilities = (double) 0;
        } else {
            this.probabilities = probabilities;
        }
    }
}
