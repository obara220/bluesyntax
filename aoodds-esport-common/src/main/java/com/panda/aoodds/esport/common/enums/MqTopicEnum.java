package com.panda.aoodds.esport.common.enums;

import lombok.Getter;

/**
 * @Author carson
 * @DATE 2022/3/8 14:19
 **/
@Getter
public enum MqTopicEnum {
    /**
     * oddsFeed电竞数据topic
     */
    STANDARD_MATCH_SCORES("STANDARD_MATCH_SCORES"),
    AO_MATCH("ao_match"),
    STANDARD_AO_MARKET_ODDS("STANDARD_AO_MARKET_ODDS"),
    MATCH_EVENT_INFO("MATCH_EVENT_INFO");
    private String topicName;
    MqTopicEnum(String topicName) {
        this.topicName = topicName;
    }
}
