package com.panda.aoodds.esport.api.service;

import java.util.List;

public interface SubjectMatchMarketServiceApi {
    public void notifyMarketMessage(String matchId, String linkId, List<Integer> categoryIds);
}
