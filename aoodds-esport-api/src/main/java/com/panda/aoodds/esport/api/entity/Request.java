package com.panda.aoodds.esport.api.entity;

import java.io.Serializable;


public class Request<T> implements Serializable {

    /***********linkId不能为空***********/
    private String linkId;

    /*****data不能为null***/
    private T data;

    /******请求时间戳*****/
    private Long dataSourceTime =System.currentTimeMillis();

    /************数据来源***********/
    private String dataSourceCode;

    /*********数据类型,可以是mq的topic**********/
    private String dataType;

    /********mq消息界面上显示的tag**********/
    private String tag;

   /************操作人ID******/
    private Long operaterId;

    /**
     * 由于风控模块通用globalId，这里增加个setter用于风控系统未传linkId是，从globalId属性中取值赋给linkId。 add_by Riben 20200923
     * @param globalId
     */
    public void setGlobalId(String globalId){
        this.linkId = globalId;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getDataSourceTime() {
        return dataSourceTime;
    }

    public void setDataSourceTime(Long dataSourceTime) {
        this.dataSourceTime = dataSourceTime;
    }

    public String getDataSourceCode() {
        return dataSourceCode;
    }

    public void setDataSourceCode(String dataSourceCode) {
        this.dataSourceCode = dataSourceCode;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getOperaterId() {
        return operaterId;
    }

    public void setOperaterId(Long operaterId) {
        this.operaterId = operaterId;
    }
}
