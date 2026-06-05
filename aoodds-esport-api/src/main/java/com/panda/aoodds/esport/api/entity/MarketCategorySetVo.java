package com.panda.aoodds.esport.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 玩法集返回值对象
 *
 * @author Samuel
 */
@Data
public class MarketCategorySetVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 玩法集名称
     */
    private String setName;

    /**
     * 盘口集合
     */
    private List<MarketSetVo> marketSetVoList;
}
