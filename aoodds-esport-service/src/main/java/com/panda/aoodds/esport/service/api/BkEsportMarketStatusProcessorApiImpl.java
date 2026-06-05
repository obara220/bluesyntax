package com.panda.aoodds.esport.service.api;

import com.panda.aoodds.esport.api.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.api.service.basketball.EsportBasketballMarketStatusProcessorApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;

@Slf4j
@DubboService
public class BkEsportMarketStatusProcessorApiImpl implements EsportBasketballMarketStatusProcessorApi {
    @Override
    public AoMatchMarketInfo marketStatusProcessor(String aoMatchId) {
        return null;
    }

//    @Autowired
//    EsportBasketballMarketStatusProcessor esportBasketballMarketStatusProcessor;
//
//    @Override
//    public AoMatchMarketInfo marketStatusProcessor(String aoMatchId) {
//        return esportBasketballMarketStatusProcessor.bkMarketStatusProcessor(aoMatchId);
//    }
}