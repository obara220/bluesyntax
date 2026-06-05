package com.panda.aoodds.esport.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 接受前端传递参数
 */
@Data
public class ParamVo implements Serializable {
    /**
     * AO赛事ID
     */
    String aoMatchId;
    /**
     * 选择reverse数据源
     */
    String dataSourceCode;
    /**
     * calc 、 apply ,specApply
     */
    String type;
    /**
     * reverse 参数
     */
    MarketParamEntiy marketParam;
    /**
     * 全局开关
     */
    Boolean aoMatchSwitch;

    /**
     * g_goal,g_corner,g_booking,ex_goal
     ***/
    String requestType;
    /**
     * 多项盘 margin
     */
    Integer margin;
    /**
     * AO玩法ID
     */
    Integer categoryId;
    /**
     * without 0否勾选，1勾选
     */
    Integer without = 0;
    /**
     * 操作人姓名
     */
    String userName;
    /**
     * 操作人ID
     */
    Long userId;

    String matchManageId;

    public ParamVo() {
    }

    public ParamVo(String aoMatchId, String dataSourceCode, String type, MarketParamEntiy marketParam, Boolean aoMatchSwitch, String requestType, Integer margin, Integer categoryId, Integer without, String userName,
                   Long userId, String matchManageId) {
        this.aoMatchId = aoMatchId;
        this.dataSourceCode = dataSourceCode;
        this.type = type;
        this.marketParam = marketParam;
        this.aoMatchSwitch = aoMatchSwitch;
        this.requestType = requestType;
        this.margin = margin;
        this.categoryId = categoryId;
        this.without = without;
        this.userName = userName;
        this.userId = userId;
        this.matchManageId = matchManageId;
    }
}
