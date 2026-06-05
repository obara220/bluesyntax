package com.panda.aoodds.esport.api.entity;


import lombok.Data;

import java.util.List;


@Data
public class MatchMarketVo implements java.io.Serializable{

    private String aoMatchId;
    private String marketTime;
    private Integer period;
    private Long dataSoureTime;
    private List<MarketCategorySetVo> marketCategorySetVos;
}
