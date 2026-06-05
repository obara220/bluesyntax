package com.panda.aoodds.esport.common.entity;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 坑位margin
 */
@Data
@Document(collection = "margin_place_config")
public class MarginPlaceConfig implements java.io.Serializable {

    String id;
    /**
     * AO赛事ID
     */
    String aoMatchId;
    /**
     * 标准赛事ID
     */
    Long standardMatchInfoId;

    /**
     * 标准玩法
     */
    Long marketCategoryId;
    /**
     * 标准子玩法
     */
    Long childStandardCategoryId;

    /**
     * AO玩法
     */
    Long aoCategoryId;
    /**
     * 盘口类型 0滚球 ，1赛前
     */
    Integer marketType;
    /**
     * 坑位
     */
    Integer placeNum;
    /**
     * 水差
     */
    Double margin;
    /**
     * 投注项类型
     */
    String oddsType;

    String linkId;

    private Long createTime;
    private Long updateTime;
}
