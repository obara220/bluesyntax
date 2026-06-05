package com.panda.aoodds.esport.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.service.EsportFootballCalcMarketsServiceApi;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.entity.AoMatchESportEntity;
import com.panda.aoodds.esport.api.entity.MarketParamEntiy;
import com.panda.aoodds.esport.common.entity.MatchTemplateConfig;
import com.panda.aoodds.esport.common.exception.ApiException;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.handle.db.DbEsportMatchMarketConfigConfigDaoHandler;
import com.panda.aoodds.esport.producer.ClearStandardCategoryIdsDiffProducer;
import com.panda.aoodds.esport.service.SubjectEsportMatchMarketManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.*;
import static com.panda.aoodds.esport.common.utils.TraceIdGenerator.createTraceId;


@Slf4j
@DubboService
@RefreshScope
public class EsportFootballCalcMarketsServiceImpl implements EsportFootballCalcMarketsServiceApi {
    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    SubjectEsportMatchMarketManager subjectEsportMatchMarketManager;
    @Autowired
    RedisService redisService;
    @Autowired
    private ClearStandardCategoryIdsDiffProducer clearStandardCategoryIdsDiffProducer;
    @Autowired
    private DbEsportMatchMarketConfigConfigDaoHandler dbEsportMatchMarketConfigConfigDaoHandler;

    static DecimalFormat formatDouble=new DecimalFormat("#.###");


    @Override
    public void applyOrcacl(Long matchId) {
        String linkId = createTraceId();
        formatDouble.setRoundingMode(RoundingMode.FLOOR);
        log.info("【applyOrcacl】apply赛事赔率,AO赛事ID:{}", matchId);
        AoMatchESportEntity aoMatchESportEntity = mongoTemplate.findOne(Query.query(Criteria.where("aoMatchId").is(matchId)),AoMatchESportEntity.class, CommonConstant.MATCH_INFO_VS);
        log.info("【applyOrcacl】赛事信息,AO赛事ID:{}", JSON.toJSONString(aoMatchESportEntity));
        if(Strings.isEmpty(aoMatchESportEntity.getPreMatchTeamData())){
            return;
        }
        MatchTemplateConfig matchTemplateConfig =new MatchTemplateConfig();

        Object matchTemplateConfigCache = redisService.get(AO_ESPORT_MACHINE+"_"+matchId);
        if(Objects.isNull(matchTemplateConfigCache)){
            matchTemplateConfig = mongoTemplate.findOne(Query.query(Criteria.where("machineId").is(aoMatchESportEntity.getMachineId())), MatchTemplateConfig.class);
        }else{
            matchTemplateConfig=JSON.parseObject(JSON.toJSONString(matchTemplateConfigCache),MatchTemplateConfig.class);
        }
        log.info("【applyOrcacl】redis数据：{},AO赛事ID:{},模板数据::{}", matchTemplateConfigCache, JSON.toJSONString(aoMatchESportEntity),matchTemplateConfig);
        clearStandardCategoryIdsDiffProducer.sendEsportCategoryIds(linkId, matchId.toString());
        log.info("【applyOrcacl】mq处理完毕,AO赛事ID:{}",   JSON.toJSONString(aoMatchESportEntity));

        MarketParamEntiy marketParamEntiy=new MarketParamEntiy();
        putGeAndSup(marketParamEntiy,aoMatchESportEntity.getPreMatchTeamData());

        log.info("【applyOrcacl】模板数据,AO赛事ID:{}", JSON.toJSONString(matchTemplateConfig));

       Double disHelfTotle = matchTemplateConfig.getDis0to151H()+matchTemplateConfig.getDis15to301H()+matchTemplateConfig.getDis30toHT();
       Double disFullTotle = matchTemplateConfig.getDis45to602H()+matchTemplateConfig.getDis60to752H()+matchTemplateConfig.getDis75toFT();
       Double verifyTmp = (1.1-disHelfTotle-disFullTotle);
       if(verifyTmp.compareTo(0D)<0){
           throw new ApiException("模板参数配置错误!");
       }

       Double dis0to151H = matchTemplateConfig.getDis0to151H()/disHelfTotle;
       Double dis15to301H = matchTemplateConfig.getDis0to151H()/disHelfTotle;
       Double dis30toHT = matchTemplateConfig.getDis0to151H()/disHelfTotle;

        Double dis45to602H = matchTemplateConfig.getDis45to602H()/disFullTotle;
        Double dis60to752H = matchTemplateConfig.getDis60to752H()/disFullTotle;
        Double dis75toFT = matchTemplateConfig.getDis75toFT()/disFullTotle;

       marketParamEntiy.setHtge(formatDouble.format(disHelfTotle*Double.valueOf(marketParamEntiy.getFtge())));
       marketParamEntiy.setHtsup(formatDouble.format(disHelfTotle*Double.valueOf(marketParamEntiy.getFtsup())));

        marketParamEntiy.setHalf1stPeriod(String.valueOf(matchTemplateConfig.getHalf1stPeriod()));
        marketParamEntiy.setDis0to151H(formatDouble.format(dis0to151H));
        marketParamEntiy.setDis15to301H(formatDouble.format(dis15to301H));
        marketParamEntiy.setDis45to602H(formatDouble.format(dis45to602H));
        marketParamEntiy.setDis60to752H(formatDouble.format(dis60to752H));
        marketParamEntiy.setAoMatchId(matchId.toString());
        marketParamEntiy.setId(matchId.toString());
        log.info("applyOrcacl 保存参数:{}",JSON.toJSONString(marketParamEntiy));
        dbEsportMatchMarketConfigConfigDaoHandler.mongoTempSave(marketParamEntiy);
        subjectEsportMatchMarketManager.notifyMarketMessage(matchId.toString(),linkId,new ArrayList<>());
    }

    private void putGeAndSup(MarketParamEntiy marketParamEntiy,String preMatchTeamData) {
        try {
            String[] preMatchTeamDataArray = preMatchTeamData.split("\\|");
            Map<String, Double> geAndSupMap = new HashMap<>();
            Arrays.stream(preMatchTeamDataArray).forEach(f -> {
                String[] keyAndValue = f.split("=");
                geAndSupMap.put(keyAndValue[0],Double.valueOf(keyAndValue[1]));
            });
            Double htGoalExpT1 = geAndSupMap.getOrDefault("HTGoalExpT1",0D);
            Double htGoalExpT2 = geAndSupMap.getOrDefault("HTGoalExpT2",0D);
            Double ftGoalExpT1 = geAndSupMap.getOrDefault("FTGoalExpT1",0D);
            Double ftGoalExpT2 = geAndSupMap.getOrDefault("FTGoalExpT2",0D);
            Double htHandicapExp = geAndSupMap.getOrDefault("HTHandicapExpT1",0D);
            Double ftHandicapExp = geAndSupMap.getOrDefault("FTHandicapExpT1",0D);
            Double htGe = htGoalExpT1+htGoalExpT2;
            Double ftGe = ftGoalExpT1+ftGoalExpT2;
            marketParamEntiy.setHtge(String.valueOf(htGe));
            marketParamEntiy.setFtge(String.valueOf(ftGe));
            marketParamEntiy.setHtsup(String.valueOf(htHandicapExp));
            marketParamEntiy.setFtsup(String.valueOf(ftHandicapExp));
            log.info("applyOrcacl 封装参数:{}",JSON.toJSONString(marketParamEntiy));
        }catch (Exception ex){
            log.error("【applyOrcacl】赛事信息",ex);
        }

    }

    public static void main(String[] args) {
//        String aa="HTGoalExpT1=0.7419823008849556|HTGoalExpT2=0.7058761061946902|FTGoalExpT1=1.6626902654867257|FTGoalExpT2=1.2149734513274335|HTHandicapExpT1=0.036106194690265436|HTHandicapExpT2=-0.036106194690265436|FTHandicapExpT1=0.4477168141592922|FTHandicapExpT2=-0.4477168141592922";
//        String[] preMatchTeamDataArray = aa.split("\\|");

      try {
     //   throw   new ApiException("aaa");
      }catch (ApiException ex){
          System.out.println(ex.getMessage());
      }

    }

}
