package com.panda.aoodds.esport.common.entity;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 线性margin配置
 */
@Data
@Document(collection = "match_template_liner_margin_config")
public class MatchTemplateLinerMarginConfig implements java.io.Serializable {
    /**
     * A0赛事ID
     */
    String id;
    /**
     * 维持率
     */
    Double retentionRate;
    /**
     * 线性margin开关  0 关 1开
     */
    String linerMarginSwitch;
    /**
     * 线性margin赔率
     */
    String linerMargin;

    private Long updateTime;
}
