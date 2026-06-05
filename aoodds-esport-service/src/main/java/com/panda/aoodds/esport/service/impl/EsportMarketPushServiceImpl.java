package com.panda.aoodds.esport.service.impl;

import com.panda.aoodds.esport.api.entity.Request;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.entity.VsWarnInfoDTO;
import com.panda.aoodds.esport.service.EsportMarketMessageService;
import com.panda.virtual.api.dto.VsMatchOperateStatusDTO;
import com.panda.virtual.dto.VirtualRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component("esportMarketPushService")
public class EsportMarketPushServiceImpl implements EsportMarketMessageService {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private Environment environment;

    @Override
    public void sendMarketMessage(AoMatchMarketInfo aoMatchMarketInfo) {
        if(null == aoMatchMarketInfo){
            return;
        }
        Request<AoMatchMarketInfo> request = new Request<>();
        String linkId = aoMatchMarketInfo.getLinkeId();
        request.setLinkId(linkId);
        request.setData(aoMatchMarketInfo);
        request.setDataSourceTime(System.currentTimeMillis());
        MessageBuilder<Request<AoMatchMarketInfo>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);

        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("AO_ESPORT_MARKET_ODDS:" + aoMatchMarketInfo.getMatchSourceId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,send successful", linkId);
            }
            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "STANDARD_MARKET_ODDS", throwable);
            }
        });
    }

    /**
     * C01赛事级关盘
     * @param aoMatchMarketInfo
     */
    @Override
    public void sendMatchStatusMessage(AoMatchMarketInfo aoMatchMarketInfo) {
        if(null == aoMatchMarketInfo){
            return;
        }
        String linkId = aoMatchMarketInfo.getLinkeId();
        VsMatchOperateStatusDTO vsMatchOperateStatusDTO = new VsMatchOperateStatusDTO();
        vsMatchOperateStatusDTO.setLinkId(linkId);
        vsMatchOperateStatusDTO.setDataSourceCode(aoMatchMarketInfo.getDataSourceCode());
        vsMatchOperateStatusDTO.setThirdMatchSourceId(aoMatchMarketInfo.getMatchSourceId());
        vsMatchOperateStatusDTO.setSportId(aoMatchMarketInfo.getSportId());
//        vsMatchOperateStatusDTO.setSourceTournamentId("");
        vsMatchOperateStatusDTO.setOperateMatchStatus(2);

        VirtualRequest<VsMatchOperateStatusDTO> virtualRequest = new VirtualRequest<>();
        virtualRequest.setLinkId(linkId);
        virtualRequest.setData(vsMatchOperateStatusDTO);
        MessageBuilder<VirtualRequest<VsMatchOperateStatusDTO>> builder = MessageBuilder.withPayload(virtualRequest).setHeader(MessageConst.PROPERTY_KEYS, linkId);

        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("C01_MATCH_OPERATE_STATUS:" + aoMatchMarketInfo.getMatchSourceId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                sendMatchWarnNoticeMessage(aoMatchMarketInfo);
                log.info("::{}::,C01_MATCH_OPERATE_STATUS send successful", linkId);
            }
            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "C01_MATCH_OPERATE_STATUS", throwable);
            }
        });

    }

    /**
     * C01赛事关闭 mongo告警 84157
     * @param aoMatchMarketInfo
     */
    public void sendMatchWarnNoticeMessage(AoMatchMarketInfo aoMatchMarketInfo) {
        if(null == aoMatchMarketInfo){
            return;
        }
        String linkId = aoMatchMarketInfo.getLinkeId();
        VsWarnInfoDTO vsWarnInfoDTO = new VsWarnInfoDTO();
        vsWarnInfoDTO.setLinkId(linkId);
        vsWarnInfoDTO.setDataSourceCode(aoMatchMarketInfo.getDataSourceCode());
        vsWarnInfoDTO.setThirdMatchSourceId(aoMatchMarketInfo.getMatchSourceId());
        vsWarnInfoDTO.setSportId(aoMatchMarketInfo.getSportId());
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length > 0) {
            vsWarnInfoDTO.setEnvironment(profiles[0]);
        } else {
            vsWarnInfoDTO.setEnvironment("default");
        }
        vsWarnInfoDTO.setWarnTime(dateTimeFormat());
        vsWarnInfoDTO.setWarnType("C01盘口异常");
        vsWarnInfoDTO.setMessage("C01赛事大小盘口异常，请注意!");

        VirtualRequest<VsWarnInfoDTO> virtualRequest = new VirtualRequest<>();
        virtualRequest.setLinkId(linkId);
        virtualRequest.setDataSourceCode(aoMatchMarketInfo.getDataSourceCode());
        virtualRequest.setDataSourceTime(System.currentTimeMillis());
        virtualRequest.setData(vsWarnInfoDTO);
        MessageBuilder<VirtualRequest<VsWarnInfoDTO>> builder = MessageBuilder.withPayload(virtualRequest).setHeader(MessageConst.PROPERTY_KEYS, linkId);

        //第一个参数表示topic:tag
        rocketMqTemplate.asyncSend("C01_WARN_NOTICE_TOPIC:" + aoMatchMarketInfo.getMatchSourceId(), builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,C01_WARN_NOTICE_TOPIC send successful", linkId);
            }
            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::C01_WARN_NOTICE_TOPIC TOPIC={}，send fail; ", linkId, "C01_WARN_NOTICE_TOPIC", throwable);
            }
        });

    }


    public String dateTimeFormat( ) {
        LocalDateTime now = LocalDateTime.now();  // 当前时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter);
        return formattedNow;
    }

}
