package com.panda.aoodds.esport.handle.db;

import com.alibaba.fastjson.JSONObject;

import com.panda.aoodds.esport.api.entity.BasketballApplyOptParam;
import com.panda.aoodds.esport.common.config.RedisConfig;
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
import static com.panda.aoodds.esport.common.constant.CommonConstant.ESPORT_BK_APPLY_CONFIG_CACHE;



@Slf4j
@Component
public class DbBkApplyConfigDaoHandler {
    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;
    @Autowired
    private RedisService redisService;

    public BasketballApplyOptParam mongoTempSave(BasketballApplyOptParam param) {
        //id   aoMatchId + "_"+marketParam.getMatchUiStatus()
        aoProducerMongoTemp.save(param, ESPORT_BK_APPLY_CONFIG);
        redisService.set(ESPORT_BK_APPLY_CONFIG_CACHE + param.getAoMatchId() , JSONObject.toJSONString(param), RedisConfig.REDIS_WEEK_TIME.longValue());
        return param;
    }

    public BasketballApplyOptParam mongoTempFindOne(String aoMatchId) {
        Object marketParamEntiyCache = redisService.get(ESPORT_BK_APPLY_CONFIG_CACHE + aoMatchId );
        if (!Objects.isNull(marketParamEntiyCache)) {
            return JSONObject.parseObject(marketParamEntiyCache.toString(),BasketballApplyOptParam.class);
        }
        BasketballApplyOptParam basketballApplyOptParam = aoProducerMongoTemp.findOne(Query.query(Criteria.where("aoMatchId").is(aoMatchId)), BasketballApplyOptParam.class, ESPORT_BK_APPLY_CONFIG);
        if (null == basketballApplyOptParam) {
            return null;
        }
        redisService.set(ESPORT_BK_APPLY_CONFIG_CACHE + basketballApplyOptParam.getAoMatchId(), JSONObject.toJSONString(basketballApplyOptParam), RedisConfig.REDIS_WEEK_TIME.longValue());
        return basketballApplyOptParam;
    }
}
