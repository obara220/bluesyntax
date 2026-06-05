package com.panda.aoodds.esport.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据源预警
 * @author   kepa
 * @since    2024年11月09日 15:39:13
 */
@Data
public class VsWarnInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 链路id
     */
    private String linkId;

    /**
     * 球种id
     */
    private Long sportId;

    /**
     * 三方联赛
     */
    private String sourceTournamentId;

    /**
     * 数据源
     */
    private String dataSourceCode;

    /**
     * 三方赛事ID
     */
    private String thirdMatchSourceId;

    /**
     * 发布环境:  prod: 生产  release: 测试环境
     */
    private String environment;

    /**
     * [类型]：C01赛事异常
     */
    private String warnType;

    /**
     * 机器id
     */
    private String machineId;

    /**
     * [时间]：2024-11-06 XXXXX
     */
    private String warnTime;

    /**
     * 预警消息
     */
    private String message;

}
