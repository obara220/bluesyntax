package com.panda.aoodds.esport.service;

import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.entity.*;
import com.panda.merge.dto.PageModel;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface BkEsportCalcMarketsService {

    MatchMarketVo getMarkets(BasketballParamVo basketballApplyOptParam);
    Map<String,Object> getMarketsMap(BasketballParamVo basketballApplyOptParam);
    void applyMarketsParam(Long matchId);
    List<BasketballApplyParam> queryApplyLogParam(BasketballParamVo basketballParamVo);
    List<JSONObject> queryOperateLogParam(BasketballParamQuery basketballParamQuery);
    void insertOptionParam(BasketballParamVo basketballApplyOptParam);
    BasketballApplyOptParam getMarketsParam(Long matchId);
    List<BasketballApplyOptParam> queryMarketsParam(BasketballParamVo basketballParamVo);
    BasketballReverseParam reverseParam(BasketballParamVo basketballParamVo);
    AoEsBasketBallTemplateConfigEntity queryMatchTempConfig(String matchId);
    PageModel<List<JSONObject>> getRecordByMatchAndRequestTypePageList(RecordPageVo recordPageVo) throws ParseException;
    BasketballReverseParam initMarketReverse(BasketballParamVo basketballParamVo);
    void oneKeyAllPreMatchApply();

}