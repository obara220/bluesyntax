package com.panda.aoodds.esport.api.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class RecordPageVo implements Serializable {
    String objectId;
    String objectName;
    String behavior;
    String operateStartTime;
    String operateEndTime;
    String userName;
    String extObjectId;
    String extObjectName;
    String operatePageCode;
    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 每页显示条数，默认 10
     */
    private Integer pageSize = 20;
}
