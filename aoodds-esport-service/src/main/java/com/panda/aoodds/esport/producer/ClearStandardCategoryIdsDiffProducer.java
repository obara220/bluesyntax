package com.panda.aoodds.esport.producer;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.entity.AoMatchESportEntity;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.merge.dto.StandardCategoryIdsDiffDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 下发APPLY融合清除水差集合
 */
@Slf4j
@Component
public class ClearStandardCategoryIdsDiffProducer {
    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private RedisService redisService;


    public void sendEsportCategoryIds(String linkId, String aoMatchId) {
        AoMatchESportEntity aoMatchESportEntity = aoProducerMongoTemp.findOne(Query.query(Criteria.where("aoMatchId").is(Long.valueOf(aoMatchId))),AoMatchESportEntity.class, CommonConstant.MATCH_INFO_VS);
        if (null == aoMatchESportEntity || null == aoMatchESportEntity.getStandardMatchId()) {
            log.info("::{}::,apply清除水差,AO电子赛事或标准赛事不存在:{},请求类型", linkId, aoMatchId);
            return;
        }
        Object o = redisService.get(RedisKeyConstant.APPLY_SWITCH_DIFF + aoMatchId + ":g_goal" );
        if (ObjectUtil.isNotNull(o)) {
            Integer status = (Integer) o;
            if (1 == status) {
                log.info("::{}::,apply清除水差,AO电子赛事没有勾选不清理:{},请求类型:{}", linkId, aoMatchId, status);
                return;
            }
        }
        StandardCategoryIdsDiffDTO standardCategoryIdsDiffDTO = new StandardCategoryIdsDiffDTO();
        Set<Long> standardCategoryIds = MarketCategory.MARKETMODEL_MAP.get("g_goal");
        if(CollectionUtils.isEmpty(standardCategoryIds)){
            return;
        }
        JSONObject object = new JSONObject();
        object.put("linkId",linkId);
        object.put("aoMatchId",aoMatchId);
        object.put("standardMatchId",aoMatchESportEntity.getStandardMatchId());
        object.put("sportId",aoMatchESportEntity.getSportId());
        object.put("standardCategoryIds",standardCategoryIds);
        log.info("::{}::,apply清除水差,AO电子赛事:{},请求类型,通知下发数据:{}", linkId, aoMatchId, JSONObject.toJSONString(standardCategoryIdsDiffDTO));
        MessageBuilder<JSONObject> builder = MessageBuilder.withPayload(object)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("ESPORT_STANDARD_CATEGORYID_CLEAR_DIFF", builder.build());
    }

}
