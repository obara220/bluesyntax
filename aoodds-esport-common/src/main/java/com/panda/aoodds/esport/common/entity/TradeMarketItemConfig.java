package com.panda.aoodds.esport.common.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 风控模板margin/最大最小赔率范围配置
 */
@Data
@Document(collection = "trade_market_item_config")
public class TradeMarketItemConfig implements Serializable {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "AO赛事ID")
    private String aoMatchId;

    @ApiModelProperty(value = "AO玩法")
    private Integer aoCategoryId;

    @ApiModelProperty(value = "标准赛事ID")
    private Long standardMatchInfoId;

    @ApiModelProperty(value = "标准玩法ID")
    private Long standardCategoryId;

    @ApiModelProperty(value = "子玩法ID")
    private Long childStandardCategoryId;

    @ApiModelProperty(value = "盘口位置")
    private Integer placeNum;

    @ApiModelProperty(value = "盘口类型,1:赛前盘;0:滚球盘")
    private Integer marketType;

    @ApiModelProperty(value = "margin值")
    private Double margin;

    @ApiModelProperty(value = "最大赔率")
    private Double maxOddsValue;

    @ApiModelProperty(value = "最小赔率")
    private Double minOddsValue;

    private Long createTime;
    private Long updateTime;

}
