package com.panda.aoodds.esport.api.entity;

import lombok.Data;


@Data
public class BasketballParamVo implements java.io.Serializable{
    String type;

    String requestType;
    /**
     * 选择reverse数据源
     */
    String dataSourceCode;
    BasketballApplyParam applyParam;
    BasketballReverseParam reverseParam;
    MatchMarketVo matchMarketVo;
    String userName;
    String matchManageId;

}
