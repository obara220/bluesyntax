package com.panda.aoodds.esport.common.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 融合玩法配置
 */
@Data
@Document(collection = "category_sell_config")
public class CategorySellConfigEntity {

    private String id;
    /**
     * 标准赛事ID
     */
    private Long standardMatchInfoId;
    /**
     * AO赛事ID
     */
    private String aoMatchInfoId;
    /**
     * 标准玩法ID
     */
    private Long marketCategoryId;
    /**
     * AO玩法ID
     */
    private Long aoCategoryId;
    /**
     * 1：赛前 0：滚球
     */
    private Integer marketType;
    /**
     * 1：开启 0：关闭
     */
    private Integer isSpecialPumping = 0;
    /**
     * 区间 json
     */
    private String specialOddsInterval;

    private Long createTime;
    private Long updateTime;

    private String linkId;
}
