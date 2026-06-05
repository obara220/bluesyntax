package com.panda.aoodds.esport.api.entity;

import lombok.Data;


@Data
public class BasketballApplyOptParam implements java.io.Serializable {
    String id;
    Double ftSup;
    Double ftGe;
    String aoMatchId;
    String lineVersion;
    String sportId;
    Integer useOuSd;
    Integer handicapModel;
    Double ahSd;
    Double ouSd;
    String modelCode= "111";
    Integer quarters;
    Integer quarterMin;
    String shotClock;
    Long createDate;
    String refresh;

    Integer closingGe;
    Integer closingTime;
    Integer overtime;
    String dataSourceCode;
    Integer followedStatus = 0 ; //rev-copy-apply = 1 ,自动rev = 1 ，直接apply = 0
    Double xgdHT;
    Double xgdQ1;
    Double xgdQ3;
    Integer s1Value;
    Integer s2Value;
    Double s3Value;
    Double s4Value;
    Boolean autoApply;
    Boolean autoRev;
    Integer matchUiStatus;
    Long modifyTime=System.currentTimeMillis();

    Float segment0;
    Float segment1;
    Float segment2;
    Float segment3;
    Float segmentOt;
    public static   BasketballApplyOptParam newBasketballApplyOptParam(){return new BasketballApplyOptParam();}
}
