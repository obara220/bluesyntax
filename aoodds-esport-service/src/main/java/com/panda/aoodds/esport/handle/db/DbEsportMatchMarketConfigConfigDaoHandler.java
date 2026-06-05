package com.panda.aoodds.esport.handle.db;

import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.entity.AoEsBasketBallTemplateConfigEntity;
import com.panda.aoodds.esport.api.entity.BasketballApplyOptParam;
import com.panda.aoodds.esport.api.entity.MarketParamEntiy;
import com.panda.aoodds.esport.common.config.RedisConfig;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.AoMatchESportEntity;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.panda.aoodds.esport.common.constant.CommonConstant.ESPORT_BK_APPLY_CONFIG;
import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.*;


@Slf4j
@Component
public class DbEsportMatchMarketConfigConfigDaoHandler {
    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;
    @Autowired
    private RedisService redisService;

    //esport_match_market_config

    public void mongoTempSave(MarketParamEntiy marketParam) {
        //id   aoMatchId
        try {
            marketParam.setModifyTime(System.currentTimeMillis());
            aoProducerMongoTemp.save(marketParam, CommonConstant.ESPORT_MATCH_MARKET_CONFIG);
        }catch (Exception e){
            log.error("异常::mongoTempFindOne::marketParam::{},Exception::",marketParam,e);
        }
        redisService.set(ESPORT_MATCH_MARKET_CONFIG_CACHE + marketParam.getAoMatchId(), JSONObject.toJSONString(marketParam), RedisConfig.REDIS_DEFAULT_TIME.longValue());
    }


    public MarketParamEntiy mongoTempFindOne(String aoMatchId) {
        Object marketParamEntiyCacheObj = redisService.get(ESPORT_MATCH_MARKET_CONFIG_CACHE + aoMatchId);
        if (!Objects.isNull(marketParamEntiyCacheObj)) {
            return JSONObject.parseObject(marketParamEntiyCacheObj.toString(), MarketParamEntiy.class);
        }
        try {
            MarketParamEntiy marketParamEntiyDB = aoProducerMongoTemp.findOne(Query.query(Criteria.where("aoMatchId").is(aoMatchId)), MarketParamEntiy.class, CommonConstant.ESPORT_MATCH_MARKET_CONFIG);
            if (null == marketParamEntiyDB) {
                return null;
            }
            redisService.set(ESPORT_MATCH_MARKET_CONFIG_CACHE + marketParamEntiyDB.getAoMatchId(), JSONObject.toJSONString(marketParamEntiyDB), RedisConfig.REDIS_DEFAULT_TIME.longValue());
            return marketParamEntiyDB;

        } catch (Exception e) {
            log.error("异常::mongoTempFindOne::aoMatchId::{},Exception::",aoMatchId,e);
        }
        return null;
    }

    public BasketballApplyOptParam mongoBkApplyOptParamFindOne(String matchId , String matchType){
        return aoProducerMongoTemp.findOne(Query.query(Criteria.where("aoMatchId").is(matchId)), BasketballApplyOptParam.class, ESPORT_BK_APPLY_CONFIG);

    }

    public AoEsBasketBallTemplateConfigEntity mongoBkTempConfigFindOne(String matchId , Integer matchType){
        String key = matchId+"_"+matchType;
        AoEsBasketBallTemplateConfigEntity aoBasketBallTemplateConfig = null;
        log.info("mongoBkTempConfigFindOne::aoMatchId::{},key::",matchId,key);
        Object aoBasketBallTemplateConfigEntity = redisService.get(ESPORT_MATCH_MARKETTEMPLATE_CONFIG_CACHE +key);
        if (!Objects.isNull(aoBasketBallTemplateConfigEntity)) {
            log.info("mongoBkTempConfigFindOne，缓存存在::aoMatchId::{},key::",matchId,key);
            aoBasketBallTemplateConfig = JSONObject.parseObject(aoBasketBallTemplateConfigEntity.toString(), AoEsBasketBallTemplateConfigEntity.class);
        }else{
            try {
                log.info("mongoBkTempConfigFindOne，查库::aoMatchId::{},key::",matchId,key);
                aoBasketBallTemplateConfig = aoProducerMongoTemp.findOne(Query.query(Criteria.where("aoMatchId").is(matchId).and("matchType").is(matchType)), AoEsBasketBallTemplateConfigEntity.class, "match_template_config");
                if (null == aoBasketBallTemplateConfig) {
                    log.info("mongoBkTempConfigFindOne，查库不存在::aoMatchId::{},key::",matchId,key);
                    return null;
                }
                log.info("mongoBkTempConfigFindOne，库存在::aoMatchId::{},key::",matchId,key);
                redisService.set(ESPORT_MATCH_MARKETTEMPLATE_CONFIG_CACHE + key, JSONObject.toJSONString(aoBasketBallTemplateConfig), RedisConfig.REDIS_DEFAULT_TIME.longValue());
            } catch (Exception e) {
                log.error("异常::mongoBkTempConfigFindOne::aoMatchId::{},Exception::",matchId,e);
            }
        }
        calcBasketballXg(aoBasketBallTemplateConfig);
        return aoBasketBallTemplateConfig;
    }

    public String mongoBkPreDataFindOne(String linkId, String matchId ){
        Object aoBasketBallPreDataEntity = redisService.get(ESPORT_MATCH_PRE_TEAM_CACHE + matchId);
        if (!Objects.isNull(aoBasketBallPreDataEntity)) {
           return (String) aoBasketBallPreDataEntity;
        }
        try {
            AoMatchESportEntity aoMatchESportEntity = aoProducerMongoTemp.findOne(Query.query(Criteria.where("aoMatchId").is(Long.valueOf(matchId))),AoMatchESportEntity.class, CommonConstant.MATCH_INFO_VS);

            if (null == aoMatchESportEntity || aoMatchESportEntity.getPreMatchTeamData() == null) {
                log.info("::linkid::{}::mongoBkPreDataFindOne::aoMatchId::{},result::{}",linkId,matchId,aoMatchESportEntity);
                return null;
            }
            redisService.set(ESPORT_MATCH_PRE_TEAM_CACHE + matchId, aoMatchESportEntity.getPreMatchTeamData() , RedisConfig.REDIS_DEFAULT_TIME.longValue());
            return aoMatchESportEntity.getPreMatchTeamData();

        } catch (Exception e) {
            log.error("::{}::异常::mongoBkPreDataFindOne::aoMatchId::{},Exception::",linkId,matchId);
        }
        return null;
    }

    void calcBasketballXg(AoEsBasketBallTemplateConfigEntity matchTemplateConfig){
        log.info("::calcBasketballXg::计算开始，::{}",matchTemplateConfig);
        if (matchTemplateConfig == null || matchTemplateConfig.getSegment0() == null || matchTemplateConfig.getSegment0() == 0.0f) {
            log.info("::calcBasketballXg::小节参数异常， 不参与计算::{}",matchTemplateConfig);
            return;
        }
        float total = matchTemplateConfig.getSegment0() + matchTemplateConfig.getSegment1() +matchTemplateConfig.getSegment2() + matchTemplateConfig.getSegment3();
        float ht = (matchTemplateConfig.getSegment0() + matchTemplateConfig.getSegment1()) / total;
        float x1 =  (matchTemplateConfig.getSegment0() / (matchTemplateConfig.getSegment0() + matchTemplateConfig.getSegment1()));
        float x3 =  (matchTemplateConfig.getSegment2() / (matchTemplateConfig.getSegment2() + matchTemplateConfig.getSegment3()));

        double xgHT = new java.math.BigDecimal(Float.toString(ht))
                .setScale(4, java.math.RoundingMode.HALF_UP)
                .doubleValue();
        double xgQ1 = new java.math.BigDecimal(Float.toString(x1))
                .setScale(4, java.math.RoundingMode.HALF_UP)
                .doubleValue();
        double xgQ3 = new java.math.BigDecimal(Float.toString(x3))
                .setScale(4, java.math.RoundingMode.HALF_UP)
                .doubleValue();

        matchTemplateConfig.setXgdHT(xgHT);
        matchTemplateConfig.setXgdQ1(xgQ1);
        matchTemplateConfig.setXgdQ3(xgQ3);
        log.info("::calcBasketballXg::计算完毕，::{}",matchTemplateConfig);
    }
}
