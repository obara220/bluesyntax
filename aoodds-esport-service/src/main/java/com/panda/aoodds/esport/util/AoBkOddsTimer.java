package com.panda.aoodds.esport.util;

import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.common.exception.ApiException;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.handle.SupportMarketHandler;
import com.panda.aoodds.esport.service.BkEsportSubjectMatchMarketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.*;
import static com.panda.aoodds.esport.common.utils.TraceIdGenerator.createTraceId;

@Slf4j
@Component
public class AoBkOddsTimer {

    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(32);
    ArrayBlockingQueue<String> blockingQueue=new ArrayBlockingQueue<String>(1000);
    ArrayBlockingQueue<String> bkBlockingQueue=new ArrayBlockingQueue<String>(1000);


    @Autowired
    BkEsportSubjectMatchMarketManager bkEsportSubjectMatchMarketManager;
    @Autowired
    RedisService redisService;
    @Autowired
    private SupportMarketHandler supportMarketHandler;
    @Autowired
    @Qualifier("aoBkMarketOddsTimerThread")
    private Executor taskExecutor;

    public static void main(String[] args) {
        Long a=System.currentTimeMillis()+1;
        Long b =System.currentTimeMillis();
        System.out.println(b-a>0);
    }
    @PostConstruct
    public void timer()throws Exception{


        /***********篮球定时任务*****************/
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try{
                    Long currentTimeMillis =System.currentTimeMillis();
                    Map<Object,Object> matchTimer = redisService.hGetAll(AO_ESPORT_BK_BGTIME_KEY);
                    //todo 是否和常规通用该key
                    Map<Object,Object> matchParam = redisService.hGetAll(AO_TIMER_PARAM);
                    Map<Object,Object> matchParamLastTime = redisService.hGetAll(AO_LASTTIME_MARKET_UPDATE);
                    for(Map.Entry<Object,Object> entity:matchTimer.entrySet()){
                        String key=entity.getKey().toString();
                        String value=entity.getValue().toString();
                        try {
                            String[] parr=value.split("#");
                            if(parr[1].equals("999")){
                                continue;
                            }
                            Integer startTime=Integer.valueOf(parr[0]);
                            Object param=matchParam.get(key);
                            Object matchMarketLastUpdateTime = matchParamLastTime.get(key);
                            if(Objects.isNull(param)&&Objects.isNull(matchMarketLastUpdateTime)){
                                continue;
                            }
                            JSONObject jsonParam = JSONObject.parseObject(param.toString());
                            Integer refreshTime = jsonParam.getInteger("timing");
                            Object cacheTempLateRefreshTime = redisService.get(ESPORT_TEMPLATE_REFRESH_TIME+key);
                            Integer timing=Objects.isNull(cacheTempLateRefreshTime) ? refreshTime : Integer.valueOf(cacheTempLateRefreshTime+"");

                            Long diffTime =currentTimeMillis-Long.valueOf(matchMarketLastUpdateTime.toString());
                            log.info("AoOddsTimer-BK::定时matchId"+key+ "currentTimeMillis:"+currentTimeMillis+"-lastTime:"+matchMarketLastUpdateTime+"="+diffTime+"#"+(timing*1000)+"::timing::"+timing);
                            if(diffTime>=(timing*1000L)&&startTime>=1){
                                if(bkBlockingQueue.size()>10){
                                    log.info("AoOddsTimer-BK::耗时:{},篮球阻塞队列大小:{}, 执行赛事:{}",(System.currentTimeMillis()-currentTimeMillis),bkBlockingQueue.size(),key);
                                }
                                redisService.hSetField(AO_LASTTIME_MARKET_UPDATE,key,System.currentTimeMillis(),AO_1DAYS_KEY_TIME);
                                bkBlockingQueue.offer(key+"#"+"2");
                            }
                        }catch (Exception ex){
                            log.error("定时matchId-BK,error:"+key);
                        }
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        },0,1000, TimeUnit.MILLISECONDS);

    }
    @PostConstruct
    public void marketsQueue(){
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        String[] queueData = bkBlockingQueue.take().split("#");
                        taskExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    String matchId =queueData[0];
                                    String sportId =queueData[1];
                                    if(sportId.equals("2")){
                                        String linkId = createTraceId();
                                        bkEsportSubjectMatchMarketManager.notifyBkMarketMessage(linkId, matchId,null);
                                    }
                                }catch (ApiException a) {
                                } catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        });

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });


    }

}
