package com.panda.aoodds.esport.service.impl;

import com.panda.aoodds.esport.api.service.SubjectMatchMarketServiceApi;
import com.panda.aoodds.esport.service.SubjectEsportMatchMarketManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
@DubboService
public class SubjectMatchMarketServiceApiImpl implements SubjectMatchMarketServiceApi {
    @Autowired
    private SubjectEsportMatchMarketManager subjectEsportMatchMarketManager;
    @Override
    public void notifyMarketMessage(String matchId, String linkId, List<Integer> categoryIds){

        subjectEsportMatchMarketManager.notifyMarketMessage(matchId,linkId,categoryIds);
    }
}
