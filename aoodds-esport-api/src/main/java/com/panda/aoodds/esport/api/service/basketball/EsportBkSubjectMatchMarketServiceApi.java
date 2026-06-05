package com.panda.aoodds.esport.api.service.basketball;

import java.util.List;

public interface EsportBkSubjectMatchMarketServiceApi {
    public  void notifyBkMarketMessage(String linkId, String matchId, List<Integer> categoryIds);
}
