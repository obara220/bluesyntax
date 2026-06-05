package com.panda.aoodds.esport.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.entity.*;
import com.panda.aoodds.esport.api.entity.AoEsBasketBallTemplateConfigEntity;
import com.panda.aoodds.esport.api.entity.BasketballApplyOptParam;
import com.panda.aoodds.esport.common.calculate.BkEsportConvertMatchMarket;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.*;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.common.utils.AoCollectionUtils;
import com.panda.aoodds.esport.config.LocalDataQueue;
import com.panda.aoodds.esport.handle.MarketLoadBalanceHandler;
import com.panda.aoodds.esport.handle.SupportMarketHandler;
import com.panda.aoodds.esport.handle.db.DbBkApplyConfigDaoHandler;
import com.panda.aoodds.esport.handle.db.DbEsportMatchMarketConfigConfigDaoHandler;
import com.panda.aoodds.esport.marketstate.MatchMarketStatusTransit;
import com.panda.aoodds.esport.producer.CommonSendMessageProducer;
import com.panda.aoodds.esport.service.BkEsportCalcMarketsService;
import com.panda.aoodds.esport.service.BkEsportSubjectMatchMarketManager;
import com.panda.aoodds.esport.service.EsportMarketMessageService;
import com.panda.merge.dto.PageModel;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.panda.aoodds.esport.common.constant.CommonConstant.MODEL_CODE;
import static com.panda.aoodds.esport.common.constant.CommonConstant.SPORT_BASKETBALL;
import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.*;
import static com.panda.aoodds.esport.common.utils.TraceIdGenerator.createTraceId;


@Slf4j
@Service
@RefreshScope
public class BkEsportCalcMarketsServiceImpl implements BkEsportCalcMarketsService {
    @Autowired
    MarketLoadBalanceHandler marketLoadBalanceHandler;
    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;
    @Autowired
    private BkEsportConvertMatchMarket bkEsportConvertMatchMarket;
    @Autowired
    RedisService redisService;
    @Autowired
    private BkEsportConvertMatchMarket convertMatchMarket;
    @Autowired
    private SupportMarketHandler supportMarketHandler;

    @Autowired
    MatchMarketStatusTransit matchMarketStatusTransit;
    @Autowired
    DbBkApplyConfigDaoHandler dbBkApplyConfigDaoHandler;
    @Value("${rev.error.status:0}") //0开，1关
    private Integer revErrorStatus;
    @Autowired
    @Qualifier("esportMarketPushService")
    EsportMarketMessageService esportMarketMessageService;
    @Autowired
    CommonSendMessageProducer commonSendMessageProducer;
    @Autowired
    BkEsportSubjectMatchMarketManager subjectMatchMarketManager;
    @Autowired
    DbEsportMatchMarketConfigConfigDaoHandler dbEsportMatchMarketConfigConfigDaoHandler;

    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(32);

    static DecimalFormat formatDouble=new DecimalFormat("#.###");


    @Override
    public MatchMarketVo getMarkets(BasketballParamVo basketballApplyOptParam) {
        return (MatchMarketVo) this.getMarketsMap(basketballApplyOptParam).get("matchMarketVo");
    }
    @Override
    public Map<String, Object> getMarketsMap(BasketballParamVo basketballParamVo) {
        BasketballApplyParam basketballApplyParam = basketballParamVo.getApplyParam();
        basketballApplyParam.setModelCode(MODEL_CODE);
        Integer[] period = supportMarketHandler.supportGetBkPeriod(basketballApplyParam.getAoMatchId());
        Integer matchType=  (period[1] > 0) ? 1 : 0;
        AoEsBasketBallTemplateConfigEntity basketBallTemplateConfigEntity = dbEsportMatchMarketConfigConfigDaoHandler.mongoBkTempConfigFindOne(basketballApplyParam.getAoMatchId(), matchType);
        AoMatchInfoEntity aoMatchInfoEntity = aoProducerMongoTemp.findOne(Query.query(Criteria.where("_id").is(basketballApplyParam.getAoMatchId())), AoMatchInfoEntity.class, CommonConstant.MATCH_INFO_VS);
        BeanUtils.copyProperties(basketBallTemplateConfigEntity, basketballApplyParam);
        log.info("getMarketsMap::aoMatchId::{},matchType::{}, basketBallTemplateConfigEntity::{}, aoMatchInfoEntity::{}",basketballApplyParam.getAoMatchId(),matchType,basketBallTemplateConfigEntity,aoMatchInfoEntity);


        if(null==basketBallTemplateConfigEntity.getS1Value()||basketBallTemplateConfigEntity.getS1Value()==0){
            basketballApplyParam.setAhSd(basketBallTemplateConfigEntity.getS3Value());
            basketballApplyParam.setOuSd(basketBallTemplateConfigEntity.getS4Value());
        }else {
            basketballApplyParam.setAhSd(Double.valueOf(basketBallTemplateConfigEntity.getS1Value()));
            basketballApplyParam.setOuSd(Double.valueOf(basketBallTemplateConfigEntity.getS2Value()));
        }

        putGeAndSup("init::getMarketsMap",basketballApplyParam);

        basketballApplyParam.setPeriodId(period[1]);
        basketballApplyParam.setPeriodRemainSec(period[0]);
        BeanUtils.copyProperties(basketBallTemplateConfigEntity, basketballApplyParam);
        Integer[] matchLength= AoCollectionUtils.periodConvert(aoMatchInfoEntity.getMatchLength());
        basketballApplyParam.setQuarters(matchLength[0]);
        basketballApplyParam.setQuarterMin(matchLength[1]*2);
        if(matchType == 1){
            supportMarketHandler.supportBkMarketSocres(basketballApplyParam);
        }
        basketballParamVo.setApplyParam(basketballApplyParam);
//        Properties matchTemplateConfigProperties = JSONObject.parseObject(JSONObject.toJSONString(basketballApplyParam), Properties.class);
//        log.info("::{}::cacl计算入参:{}", basketballApplyParam.getLinkId(), JSON.toJSONString(matchTemplateConfigProperties));
//        String resultMarkets = "";
//        try {
//            resultMarkets = marketLoadBalanceHandler.getMarkets(matchTemplateConfigProperties, null, String.valueOf(SPORT_BASKETBALL));
//        } catch (IllegalArgumentException ex) {
//            throw new ApiException(ex.getMessage());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        log.info("AO篮球basketballParamVo:{},resultMarkets：{}，", JSONObject.toJSONString(basketballParamVo));
//        if (StringUtils.isEmpty(resultMarkets)) {
//            throw new ApiException("resultMarkets返回null!");
//        }
//        MarketDto marketDto = JSONObject.parseObject(resultMarkets, MarketDto.class);
//
//        MatchMarketVo matchMarketVo = convertMatchMarket.convertStandardMarketMessage("", basketballParamVo.getApplyParam().getAoMatchId(), "b_goal", marketDto.getMarketsEntityList(), Boolean.TRUE, "1", Long.valueOf(basketballParamVo.getApplyParam().getSportId()));
//
        Map<String, Object> res = new HashMap<>();
//        res.put("matchMarketVo", matchMarketVo);
        res.put("basketballApplyParam", basketballApplyParam);
        log.info("###AO篮球basketballParamVo:{}", JSONObject.toJSONString(basketballParamVo));
        return res;
    }

    @Override
    public void applyMarketsParam(Long matchId) {

        String linkId = createTraceId();
        formatDouble.setRoundingMode(RoundingMode.FLOOR);

        log.info("linkID::{},【applyMarketsParam】apply赛事赔率,AO赛事ID:{}",linkId, matchId);
        AoMatchESportEntity aoMatchESportEntity = aoProducerMongoTemp.findOne(Query.query(Criteria.where("aoMatchId").is(matchId)),AoMatchESportEntity.class, CommonConstant.MATCH_INFO_VS);
        log.info("linkID::{},【applyMarketsParam】赛事信息,AO赛事ID:{}",linkId, JSON.toJSONString(aoMatchESportEntity));
        if(Strings.isEmpty(aoMatchESportEntity.getPreMatchTeamData())){
            return;
        }

        Integer[] period = supportMarketHandler.supportGetBkPeriod(String.valueOf(matchId));
        Integer matchType=  (period[1] > 0) ? 1 : 0;
        AoEsBasketBallTemplateConfigEntity basketBallTemplateConfigEntity = dbEsportMatchMarketConfigConfigDaoHandler.mongoBkTempConfigFindOne(matchId.toString(), matchType);
//        MatchTemplateConfig  matchTemplateConfig = aoProducerMongoTemp.findOne(Query.query(Criteria.where("machineId").is(aoMatchESportEntity.getMachineId())), MatchTemplateConfig.class);
        log.info("linkID::{},【applyMarketsParam】matchType::{}, db数据：{},AO赛事ID:{}",linkId, matchType, basketBallTemplateConfigEntity, JSON.toJSONString(aoMatchESportEntity));
        if(basketBallTemplateConfigEntity == null){
            log.info("linkID::{},【applyMarketsParam】matchType{}, 111db数据：{},AO赛事ID:{}",linkId, matchType, basketBallTemplateConfigEntity, JSON.toJSONString(aoMatchESportEntity));
            return;
        }

        BasketballApplyOptParam basketballApplyOptParam = new BasketballApplyOptParam();
        BeanUtils.copyProperties(basketBallTemplateConfigEntity, basketballApplyOptParam);

        log.info("linkID::{},applyMarketsParam::模板数据复制:{}, ",linkId, JSON.toJSONString(basketballApplyOptParam) );
//        putGeAndSup(linkId,basketballApplyOptParam);
        log.info("linkID::{},applyMarketsParam::ge和sup参数处理:{}, ",linkId, JSON.toJSONString(basketballApplyOptParam) );

        basketballApplyOptParam.setId(basketballApplyOptParam.getAoMatchId());
        basketballApplyOptParam.setCreateDate(System.currentTimeMillis());

        Integer[] matchLength= AoCollectionUtils.periodConvert(aoMatchESportEntity.getMatchLength());
        basketballApplyOptParam.setQuarters(matchLength[0]);
        basketballApplyOptParam.setQuarterMin(matchLength[1] * 2);
        log.info("linkID::{},applyMarketsParam::apply完成，参数信息::aoMatchID ::{} , matchType::{},basketballApplyOptParam::{}, ",
                linkId, matchId ,matchType,basketballApplyOptParam);


//        basketballApplyOptParam = dbBkApplyConfigDaoHandler.mongoTempSave(basketballApplyOptParam);
//        ApplyParamQueryEntity applyParamQueryEntity = new ApplyParamQueryEntity(Lists.newArrayList(Long.valueOf(basketBallTemplateConfigEntity.getStandardMatchId())), "b_goal", 2, basketballApplyOptParam.getMatchUiStatus(), linkId);
//        commonSendMessageProducer.sendMarketMessage(A01_MATCH_APPLY_INIT, JSON.toJSONString(applyParamQueryEntity), linkId);
        subjectMatchMarketManager.notifyBkMarketMessage(linkId,matchId.toString(),new ArrayList<>());

    }



    /**
     * 操作日志里面获取上次apply的参数
     * @param basketballParamVo
     * @return
     */
    @Override
    public List<BasketballApplyParam> queryApplyLogParam(BasketballParamVo basketballParamVo) {
        //构建查询条件
        Query query = new Query();
        //根据赛事id，logType去查询
        query.addCriteria(Criteria.where("aoMatchId").is(basketballParamVo.getApplyParam().getAoMatchId()).and("logType").is("apply"));
        //根据创建时间排序
        query.with(Sort.by(Sort.Order.desc("createTime")));
        List<BasketballApplyParam> basketballApplyParams = aoProducerMongoTemp.find(query, BasketballApplyParam.class, "ao_operate_log");
        return basketballApplyParams;
    }

    @Override
    public List<JSONObject> queryOperateLogParam(BasketballParamQuery basketballParamQuery) {

        Query query = new Query();
        query.addCriteria(Criteria.where("aoMatchId").is(basketballParamQuery.getAoMatchId()));

        if (!Strings.isEmpty(basketballParamQuery.getLogType())) {
            query.addCriteria(Criteria.where("logType").is(basketballParamQuery.getLogType()));
        }
        query.with(Sort.by(Sort.Order.desc("createTime"))).limit(1000);
        List<JSONObject> basketballApplyParams = aoProducerMongoTemp.find(query, JSONObject.class, "ao_operate_log");
        if (CollectionUtils.isEmpty(basketballApplyParams)) {
            return basketballApplyParams;
        }
        String str = JSONObject.toJSONString(basketballApplyParams).replace("@type", "errortype");
        return JSONObject.parseArray(str, JSONObject.class);

    }

    /**
     * 操作日志
     * 插入apply
     * @param basketballApplyOptParam
     */
    @Override
    public void insertOptionParam(BasketballParamVo basketballApplyOptParam) {
        //获取请求参数中的applyParam
        BasketballApplyParam applyParam = basketballApplyOptParam.getApplyParam();
        BasketballReverseParam reverseParam = basketballApplyOptParam.getReverseParam();
        String userName = basketballApplyOptParam.getUserName() == null ? reverseParam.getUserName() : basketballApplyOptParam.getUserName();
        if (null != applyParam) {
            //获取缓存中的赛事阶段信息
            Integer[] period = supportMarketHandler.supportGetBkPeriod(applyParam.getAoMatchId());
            applyParam.setPeriodId(period[1]);
            applyParam.setPeriodRemainSec(period[0]);
            applyParam.setUserName(userName);
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(applyParam));
            jsonObject.put("logType", "apply");
            jsonObject.put("userName", userName);
            aoProducerMongoTemp.insert(jsonObject, "ao_operate_log");
        } else if (null != reverseParam) {
            //apply 操作类型日志记录
            reverseParam.setUserName(userName);
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(reverseParam));
            jsonObject.put("logType", "reverse");
            jsonObject.put("userName", userName);
            aoProducerMongoTemp.insert(jsonObject, "ao_operate_log");
        }
    }

    @Override
    public BasketballApplyOptParam getMarketsParam(Long matchId) {
        BasketballApplyOptParam basketballApplyOptParam = dbBkApplyConfigDaoHandler.mongoTempFindOne(String.valueOf(matchId));
        return basketballApplyOptParam;
    }

    @Override
    public List<BasketballApplyOptParam> queryMarketsParam(BasketballParamVo basketballParamVo) {
        return null;
    }

    @Override
    public BasketballReverseParam reverseParam(BasketballParamVo basketballParamVo) {
        return null;
    }

    @Override
    public AoEsBasketBallTemplateConfigEntity queryMatchTempConfig(String matchId) {
        return null;
    }

    @Override
    public PageModel<List<JSONObject>> getRecordByMatchAndRequestTypePageList(RecordPageVo recordPageVo) throws ParseException {
        return null;
    }

    @Override
    public BasketballReverseParam initMarketReverse(BasketballParamVo basketballParamVo) {
        return null;
    }

    @Override
    public void oneKeyAllPreMatchApply() {
        Long currentTime = System.currentTimeMillis() + (10 * 60 * 1000);
        if (LocalDataQueue.preMatchOneKeyBKApplyQueue.size() > 0) {
            log.info("一键Apply oneKeyAllPrBKMatch 当前有任务在执行");
            return;
        }

        List<AoMatchInfoEntity> aoMatchInfoEntitys = aoProducerMongoTemp.find(Query.query(Criteria.where("beginTime").gt(currentTime).and("sportId").is(SPORT_BASKETBALL)), AoMatchInfoEntity.class, CommonConstant.MATCH_INFO_VS);
        if (org.springframework.util.CollectionUtils.isEmpty(aoMatchInfoEntitys)) {
            log.info("oneKeyAllPrBKMatch 未找到赛前赛事");
            return;
        }
        redisService.hSet(ONEKEY_APPKY_TOTAL_NUM, "bkApplyTotal", aoMatchInfoEntitys.size(), AO_1DAYS_KEY_TIME);
        aoMatchInfoEntitys.stream().forEach(f -> LocalDataQueue.preMatchOneKeyBKApplyQueue.offer(f.getAoMatchId().toString()));
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (LocalDataQueue.preMatchOneKeyBKApplyQueue.size() < 1) {
                            log.info("oneKeyAllPrBKMatch 执行完成 共:{}条数据", redisService.hGet(ONEKEY_APPKY_TOTAL_NUM, "bkApplyTotal"));
                            break;
                        }
                        String matchId = LocalDataQueue.preMatchOneKeyBKApplyQueue.take();
                        //增加调用日志
                        log.info("oneKeyAllPrBKMatch::赛事id：：{}",matchId);
                        subjectMatchMarketManager.notifyBkMarketMessage("", matchId, null);
                        Thread.sleep(2000);
                    } catch (Exception ex) {
                        log.error("oneKeyAllPrBKMatch 异常", ex);
                    }

                }
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     *
     *
     * @param basketballParamVo
     * @param reverseParam
     * @param obj
     * @param period
     * @param isexistParam
     */
    private void revErrorInfoCloseMarket(BasketballParamVo basketballParamVo, BasketballReverseParam reverseParam, Object obj, Integer[] period, BasketballApplyOptParam isexistParam) {
        if (1 == revErrorStatus) {
            log.info("::{}::AO赛事ID:{},数据源：{}，自动apply/rev勾选盘口关盘触发盘口关盘,配置关不处理:{}", reverseParam.getLinkId(), basketballParamVo.getDataSourceCode(), reverseParam.getAoMatchId(), obj);
            return;
        }
        if (null == isexistParam) {
            log.info("::{}::AO赛事ID:{},数据源：{}，自动apply/rev勾选盘口关盘触发盘口关盘,没有apply过不处理:{}", reverseParam.getLinkId(), basketballParamVo.getDataSourceCode(), reverseParam.getAoMatchId(), obj);
            return;
        }
        Boolean autoRev = null == isexistParam.getAutoRev() ? Boolean.FALSE : isexistParam.getAutoRev();
        Boolean autoApply = null == isexistParam.getAutoApply() ? Boolean.FALSE : isexistParam.getAutoApply();
        if (autoRev && autoApply) {
            log.info("::{}::AO赛事ID:{},数据源：{}，自动apply/rev勾选盘口关盘触发盘口关盘:{}", reverseParam.getLinkId(), basketballParamVo.getDataSourceCode(), reverseParam.getAoMatchId(), obj);
            //下发关盘
//            marketMessagesService.sendMarketMessage(matchMarketStatusTransit.marketStatusProcessor(reverseParam.getLinkId(), Long.valueOf(reverseParam.getAoMatchId())));
            //记录错误日志
//            saveRevErrorInfo(basketballParamVo, period);
            log.info("::{}::AO赛事ID:{},数据源：{}，自动apply/rev勾选盘口关盘触发盘口关盘处理完成:{}", reverseParam.getLinkId(), basketballParamVo.getDataSourceCode(), reverseParam.getAoMatchId(), obj);
        }
    }

    public Double toDouble(Double param) {
        if (null != param) {
            return new BigDecimal(String.valueOf(param / 100000.0)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        return param;
    }

    /**
     *
     * @param basketballApplyOptParam
     * @param basketballApplyOptParam  FTGoalExpT1=56.05|FTGoalExpT2=56.725
     */
    private void putGeAndSup(String linkId,BasketballApplyParam basketballApplyOptParam) {

        String preMatchTeamData = dbEsportMatchMarketConfigConfigDaoHandler.mongoBkPreDataFindOne(linkId, basketballApplyOptParam.getAoMatchId());
        if (Strings.isBlank(preMatchTeamData)) {
            log.warn("【applyMarketsParam】preMatchTeamData 为空");
            return;
        }

        try {
            Map<String, Double> geAndSupMap = Arrays.stream(preMatchTeamData.split("\\|"))
                    .map(entry -> entry.split("="))
                    .filter(kv -> kv.length == 2 && Strings.isNotBlank(kv[0]))
                    .collect(HashMap::new,
                            (map, kv) -> map.put(kv[0], parseDoubleSafely(kv[1])),
                            HashMap::putAll);

            double ftGoalExpT1 = geAndSupMap.getOrDefault("FTGoalExpT1", 0D);
            double ftGoalExpT2 = geAndSupMap.getOrDefault("FTGoalExpT2", 0D);
            float total = 1f;
            if (basketballApplyOptParam.getSegment0() == null || basketballApplyOptParam.getSegment0() == 0.0f) {
                log.info("::putGeAndSup::小节参数异常， 不参与计算::{}",basketballApplyOptParam);
            }else {
                 total = basketballApplyOptParam.getSegment0() + basketballApplyOptParam.getSegment1() +basketballApplyOptParam.getSegment2() + basketballApplyOptParam.getSegment3();
            }
            double geT1 = ftGoalExpT1 * total;
            double geT2 = ftGoalExpT2 * total;

            BigDecimal ge = BigDecimal.valueOf((geT1 + geT2)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal sup = BigDecimal.valueOf(geT1 - geT2).setScale(2, RoundingMode.HALF_UP);

            basketballApplyOptParam.setFtGe(ge.doubleValue());
            basketballApplyOptParam.setFtSup(sup.doubleValue());

            log.info("【applyMarketsParam】封装完成: {}", JSON.toJSONString(basketballApplyOptParam));
        } catch (Exception ex) {
            log.error("【applyMarketsParam】解析 preMatchTeamData 异常，原始数据：{}", preMatchTeamData, ex);
        }
    }


    private double parseDoubleSafely(String str) {
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            log.warn("【applyMarketsParam】无法解析数值: {}", str);
            return 0D;
        }
    }


    public static void main(String[] args) {
        // 源对象：配置实体
        AoEsBasketBallTemplateConfigEntity source = new AoEsBasketBallTemplateConfigEntity();
        source.setUseOuSd(null);
        source.setRefresh(10);
        source.setClosingGe(10);
        source.setClosingTime(2);
        source.setOvertime(5);
        source.setHandicapModel(null);
        source.setAoMatchId("1946033049443500033");
        source.setStandardMatchId("1946033049347031042");
        source.setTournamentLevel(16);
        source.setXgdHT(0.5);
        source.setXgdQ1(0.5);
        source.setXgdQ3(0.5);
        source.setS1Value(1);
        source.setS2Value(0);
        source.setS3Value(0.0);
        source.setS4Value(0.0);

        // 目标对象
        BasketballApplyOptParam target = new BasketballApplyOptParam();

        // 属性拷贝
        BeanUtils.copyProperties(source, target);



    }
}
