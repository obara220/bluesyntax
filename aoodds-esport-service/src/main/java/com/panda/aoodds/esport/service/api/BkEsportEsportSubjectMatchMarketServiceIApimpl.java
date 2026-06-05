package com.panda.aoodds.esport.service.api;

import com.panda.aoodds.esport.api.service.basketball.EsportBkSubjectMatchMarketServiceApi;
import com.panda.aoodds.esport.service.BkEsportSubjectMatchMarketManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Slf4j
@DubboService
public class BkEsportEsportSubjectMatchMarketServiceIApimpl implements EsportBkSubjectMatchMarketServiceApi {
    @Autowired
    BkEsportSubjectMatchMarketManager subjectBkMatchMarketManager;
    @Override
    public void notifyBkMarketMessage(String linkId, String matchId, List<Integer> categoryIds) {
        subjectBkMatchMarketManager.notifyBkMarketMessage(linkId,matchId,categoryIds);
    }
}
