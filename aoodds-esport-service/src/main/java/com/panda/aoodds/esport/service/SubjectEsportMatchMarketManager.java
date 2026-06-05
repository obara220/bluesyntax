package com.panda.aoodds.esport.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface  SubjectEsportMatchMarketManager {
//      @Autowired
//      protected List<MarketMessageService> marketMessageService;
    public   void notifyMarketMessage(String matchId,String linkId,List<Integer> categoryIds);
}
