package com.panda.aoodds.esport.api.entity;

import lombok.Data;

@Data
public class BasketballApplyParam extends BasketballMarketParam {
    Double ftSup;
    Double ftGe;
    String refresh;

    Float segment0;
    Float segment1;
    Float segment2;
    Float segment3;
    Float segmentOt;

    String segmentWeight;

    Double percentGe;
    Integer q4SetSecond = 0;

}
