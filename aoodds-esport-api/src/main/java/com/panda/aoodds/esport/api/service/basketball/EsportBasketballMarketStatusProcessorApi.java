package com.panda.aoodds.esport.api.service.basketball;


import com.panda.aoodds.esport.api.entity.AoMatchMarketInfo;

/**
 * 处理篮球盘口关盘
 */
public interface EsportBasketballMarketStatusProcessorApi {
    public AoMatchMarketInfo marketStatusProcessor(String aoMatchId);
}
