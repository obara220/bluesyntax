package com.panda.aoodds.esport.common.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author carson
 * @DATE 2024/1/26 13:28
 * 电子赛事
 **/
@Slf4j
@Data
public class AoMatchESportEntity implements java.io.Serializable{
    String id;
    Long aoMatchId;
    Integer sportId;
    Long beginTime;
    Long standardMatchId;
    Long standardHomeId;
    Integer matchLength;
    Long standardAwayId;
    Long standardTouId;
    String preMatchTeamData;
    String machineId;
}
