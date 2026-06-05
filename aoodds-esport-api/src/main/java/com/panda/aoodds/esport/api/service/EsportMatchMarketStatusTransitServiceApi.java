package com.panda.aoodds.esport.api.service;


public interface EsportMatchMarketStatusTransitServiceApi {
    void upMatchMarketStatus(String linkId,Long matchId, String type);

    void standardEventPeriodRestMarketClose(String linkId, Long aoMatchId, Long matchPeriodId);
}
