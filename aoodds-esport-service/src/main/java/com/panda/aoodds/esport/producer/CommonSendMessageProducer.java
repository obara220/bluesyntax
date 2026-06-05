package com.panda.aoodds.esport.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommonSendMessageProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    public void sendMarketMessage(String topicName,String msg,String linkId) {
        if (null == msg) {
            return;
        }

        MessageBuilder<String> builder = MessageBuilder.withPayload(msg).setHeader(MessageConst.PROPERTY_KEYS, linkId);

        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend(topicName + ":" + linkId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, topicName, throwable);
            }
        });
    }

}
