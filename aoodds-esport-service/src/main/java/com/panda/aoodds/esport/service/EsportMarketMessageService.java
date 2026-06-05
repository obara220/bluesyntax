package com.panda.aoodds.esport.service;

import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;

public interface EsportMarketMessageService {
    public  void sendMarketMessage(AoMatchMarketInfo aoMatchMarketInfo );


    public  void sendMatchStatusMessage(AoMatchMarketInfo aoMatchMarketInfo );

}
