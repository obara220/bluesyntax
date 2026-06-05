package com.panda.aoodds.esport.common.entity;

import lombok.Data;

@Data
public class SpecialEventConfigEntiy {
    Integer awayActive;
    Integer awayActiveCount;
    Double awayGoalProb;
    Long effectiveTime;
    String eventCode;
    String eventName;
    Integer eventSwitch;
    Integer homeActive;
    Integer homeActiveCount;
    Double homeGoalProb;
    String id;
    Integer oneSideSwitch;
    Integer type;
    Integer typeVal;
    String seId;
    Long updateTime;
    String aoMatchId;
    Long createTime;
    Long modifyTime=System.currentTimeMillis();
}
