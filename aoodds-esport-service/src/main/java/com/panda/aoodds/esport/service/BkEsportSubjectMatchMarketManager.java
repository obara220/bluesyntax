package com.panda.aoodds.esport.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class BkEsportSubjectMatchMarketManager {

    public abstract void notifyBkMarketMessage(String linkId, String matchId, List<Integer> categoryIds);

}
