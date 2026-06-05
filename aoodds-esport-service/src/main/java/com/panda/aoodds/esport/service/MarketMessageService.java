package com.panda.aoodds.esport.service;


import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.sports.algo.api.entity.MarketProbabilityEntity;

/**
 * @Author carson
 * @DATE 2022/3/9 14:09
 **/
public interface MarketMessageService {
  public  void sendMarketMessage(AoMatchMarketInfo aoMatchMarketInfo );
  public  void sendMarketCashOutMessage(AoMatchMarketInfo<MarketProbabilityEntity> aoMatchMarketInfo );
}
