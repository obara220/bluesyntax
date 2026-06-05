package com.panda.aoodds.esport.common.calculate.db;

import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.common.config.RedisConfig;
import com.panda.aoodds.esport.common.entity.MatchTemplateLinerMarginConfig;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.MATCH_TEMPLATE_LINER_MARGIN_CONFIG_CACHE;


@Slf4j
@Component
public class DbMatchTemplateLinerMarginConfigDaoHandler {
    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;
    @Autowired
    private RedisService redisService;

    //match_template_liner_margin_config
    public MatchTemplateLinerMarginConfig mongoTempFindOne(String id) {
        Object MatchTemplateLinerMarginConfigObj = redisService.get(MATCH_TEMPLATE_LINER_MARGIN_CONFIG_CACHE + id);
        if (Objects.isNull(MatchTemplateLinerMarginConfigObj)) {
            try {
                MatchTemplateLinerMarginConfig matchTemplateLinerMarginConfig = aoProducerMongoTemp.findOne(Query.query(Criteria.where("id").is(id)), MatchTemplateLinerMarginConfig.class);
                if (null != matchTemplateLinerMarginConfig) {
                    redisService.set(MATCH_TEMPLATE_LINER_MARGIN_CONFIG_CACHE + matchTemplateLinerMarginConfig.getId(), JSONObject.toJSONString(matchTemplateLinerMarginConfig), RedisConfig.REDIS_WEEK_TIME.longValue());
                }
                return matchTemplateLinerMarginConfig;
            } catch (Exception e) {
            }
            return null;
        }
        return JSONObject.parseObject(MatchTemplateLinerMarginConfigObj.toString(), MatchTemplateLinerMarginConfig.class);
    }
}
