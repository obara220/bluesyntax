package com.panda.aoodds.esport.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 玩法盘口集合值对象
 *
 * @author Samuel
 */
@Data
public class MarketSetVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 玩法盘口集Id
     */
    private Integer marketSetId;
    /**
     * 玩法盘口margin
     */
    private Integer margin;

    /**
     * 玩法盘口集名称
     */
    private String marketSetName;

    /**
     * 玩法盘口集合
     */
    private List<MarketsEntity> marketsEntityList;

}
