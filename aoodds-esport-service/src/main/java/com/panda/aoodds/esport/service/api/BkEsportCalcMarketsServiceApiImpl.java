package com.panda.aoodds.esport.service.api;

import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.entity.*;
import com.panda.aoodds.esport.api.service.basketball.EsportBasketballCalcMarketsServiceApi;
import com.panda.aoodds.esport.service.BkEsportCalcMarketsService;
import com.panda.merge.dto.PageModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Slf4j
@DubboService
public class BkEsportCalcMarketsServiceApiImpl implements EsportBasketballCalcMarketsServiceApi {

    @Autowired
    BkEsportCalcMarketsService bkCalcMarketsService;
    @Override
    public MatchMarketVo getMarkets(BasketballParamVo basketballApplyOptParam) {
        return bkCalcMarketsService.getMarkets(basketballApplyOptParam);
    }

    @Override
    public Map<String, Object> getMarketsMap(BasketballParamVo basketballApplyOptParam) {
        return bkCalcMarketsService.getMarketsMap(basketballApplyOptParam);
    }

    @Override
    public void applyMarketsParam(Long matchId) {
         bkCalcMarketsService.applyMarketsParam(matchId);
    }

    @Override
    public List<BasketballApplyParam> queryApplyLogParam(BasketballParamVo basketballParamVo) {
        return bkCalcMarketsService.queryApplyLogParam(basketballParamVo);
    }

    @Override
    public List<JSONObject> queryOperateLogParam(BasketballParamQuery basketballParamQuery) {
        return bkCalcMarketsService.queryOperateLogParam(basketballParamQuery);
    }

    @Override
    public void insertOptionParam(BasketballParamVo basketballApplyOptParam) {
        bkCalcMarketsService.insertOptionParam(basketballApplyOptParam);
    }

    @Override
    public BasketballApplyOptParam getMarketsParam(Long matchId) {
        return bkCalcMarketsService.getMarketsParam(matchId);
    }

    @Override
    public List<BasketballApplyOptParam> queryMarketsParam(BasketballParamVo basketballParamVo) {
        return bkCalcMarketsService.queryMarketsParam(basketballParamVo);
    }

    @Override
    public BasketballReverseParam reverseParam(BasketballParamVo basketballParamVo) {
        return bkCalcMarketsService.reverseParam(basketballParamVo);
    }

    @Override
    public AoEsBasketBallTemplateConfigEntity queryMatchTempConfig(String matchId) {
        return bkCalcMarketsService.queryMatchTempConfig(matchId);
    }


    @Override
    public PageModel<List<JSONObject>> getRecordByMatchAndRequestTypePageList(RecordPageVo recordPageVo) throws ParseException {
        return bkCalcMarketsService.getRecordByMatchAndRequestTypePageList(recordPageVo);
    }

    @Override
    public BasketballReverseParam initMarketReverse(BasketballParamVo basketballParamVo) {
        return bkCalcMarketsService.initMarketReverse(basketballParamVo);
    }

    @Override
    public void oneKeyAllPreMatchApply() {
        bkCalcMarketsService.oneKeyAllPreMatchApply();
    }


}
