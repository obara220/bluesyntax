package com.panda.aoodds.esport.api.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class BetPieceEntityVo implements Serializable {
    /**
     * 投注项Id
     */
    private String betPriceId;

    /**
     * 投注项名称
     */
    private String name;

    /**
     * 原始赔率
     */
    private Double originalOdds;

    /**
     * 维持率赔率
     */
    private Double retentionRateOdds;
    /**
     * 线性抽水赔率
     */
    private Double linerMarginOdds;
}
