package com.panda.aoodds.esport.common.entity;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class ApplyParamQueryEntity implements java.io.Serializable{
    /***需要查询的赛事Id集合***/
    List<Long> aoIdList;
    /********需要查询数据类型**************/
    String tempType;
    /*********需要查询的体种ID***************/
    Integer sportId;

    Integer isInPlay;

    String linkId;
    public ApplyParamQueryEntity(){

    }
    public ApplyParamQueryEntity(List<Long> aoIdList, String tempType, Integer sportId, Integer isInPlay, String linkId){
        this.aoIdList=aoIdList;
        this.tempType=tempType;
        this.sportId=sportId;
        this.isInPlay=isInPlay;
        this.linkId=linkId;
    }
    public static void main(String[] args) {
        ApplyParamQueryEntity applyParamQueryEntity = new ApplyParamQueryEntity();
        applyParamQueryEntity.setAoIdList(Lists.newArrayList(123L,345L));
        applyParamQueryEntity.setTempType("g_goal");
        applyParamQueryEntity.setSportId(1);
        applyParamQueryEntity.setLinkId("323434");
        System.out.println(JSON.toJSONString(applyParamQueryEntity));
    }

}
