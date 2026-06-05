package com.panda.aoodds.esport.service.impl;

import com.panda.aoodds.esport.api.service.EsportMatchMarketStatusTransitServiceApi;
import com.panda.aoodds.esport.marketstate.MatchMarketStatusTransit;
import com.panda.aoodds.esport.service.EsportMarketMessageService;
import com.panda.aoodds.esport.service.SubjectEsportMatchMarketManager;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@DubboService
public class MatchMarketStatusTransitImplApi implements EsportMatchMarketStatusTransitServiceApi {
    @Autowired
    MatchMarketStatusTransit matchMarketStatusTransit1;
    @Autowired
    @Qualifier("esportMarketPushService")
    EsportMarketMessageService esportMarketMessageService;
    @Override
    public void upMatchMarketStatus(String linkId, Long matchId, String type) {
        esportMarketMessageService.sendMarketMessage(matchMarketStatusTransit1.upMatchMarketStatus(linkId,matchId,type));
    }



    @Override
    public void standardEventPeriodRestMarketClose(String linkId, Long aoMatchId,  Long matchPeriodId) {
        esportMarketMessageService.sendMarketMessage(matchMarketStatusTransit1.standardEventPeriodRestMarketClose(linkId,aoMatchId,matchPeriodId));
    }
}
