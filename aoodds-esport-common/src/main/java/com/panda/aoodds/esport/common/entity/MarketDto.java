package com.panda.aoodds.esport.common.entity;

import com.panda.aoodds.esport.api.entity.MarketsEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 盘口集
 *
 * @author Samuel
 */
@Data
public class MarketDto implements Serializable {
    /**
     * 链路Id
     */
    private String linkId;

    /**
     * 比赛时钟
     */
    private Integer matchClock;

    private Integer remainGe;

    /**
     * 盘口集合
     */
    private List<MarketsEntity> marketsEntityList;
}
