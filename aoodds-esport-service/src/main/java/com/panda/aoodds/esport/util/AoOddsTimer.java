package com.panda.aoodds.esport.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.handle.SupportMarketHandler;
import com.panda.aoodds.esport.service.SubjectEsportMatchMarketManager;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.*;
import static com.panda.aoodds.esport.common.utils.TraceIdGenerator.createTraceId;


/* * @Author carson
 * @DATE 2022/3/8 21:08*/





@Slf4j
@Component
public class AoOddsTimer implements ApplicationListener<ApplicationReadyEvent> {
    private static  ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(32);
    ArrayBlockingQueue<String> blockingQueue=new ArrayBlockingQueue<String>(1000);
    @Autowired
    SubjectEsportMatchMarketManager subjectEsportMatchMarketManager;

    @Autowired
    RedisService redisService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SupportMarketHandler supportMarketHandler;
    @Autowired
    @Qualifier("aoMarketOddsTimerThread")
    private Executor taskExecutor;

    public static void main(String[] args) {
        Long a=System.currentTimeMillis()+1;
        Long b =System.currentTimeMillis();
        System.out.println(b-a>0);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("marketsQueue启动中 1---------------------------");
    String redisLocKey = RedisKeyConstant.MATCH_LOCK +"AoOddsTimer";
        redisTemplate.delete(AO_REDIS_QUEUE_ESPORT_ODDSTIMER);
/*******足球定时任务*************/
        ScheduledFuture scheduledFuture = executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
            try {
             //   Object cacheRefreshTime = redisService.get(ESPORT_REFRESH_TIME);

//                Integer timing=Objects.isNull(cacheRefreshTime)?10:Integer.valueOf(cacheRefreshTime.toString());
               // List<String>  aoMatchIds=Lists.newArrayList();
               redisService.tryLock(redisLocKey, redisLocKey, 10, 10);
                Long a=System.currentTimeMillis();
                Map<Object,Object> matchTimer = redisService.hGetAll(AO_ESPORT_BGTIME_KEY);

                Map<Object,Object> matchParamLastTime = redisService.hGetAll(AO_LASTTIME_ESPORT_MARKET_UPDATE);
                Long currentTimeSys = System.currentTimeMillis();
                for(Map.Entry<Object,Object> entity:matchTimer.entrySet()){

                    String key=entity.getKey().toString();
                    String value=entity.getValue().toString();
                    log.info("index 1 matchId:{} ### AoOddsTimer:{}",key,value);
                    String[] parr=value.split("#");
                    Long startTime=Long.parseLong(parr[0]);
                    Integer proidid=Integer.parseInt(parr[1]);
                    log.info("index 2 matchId:{} ### AoOddsTimer:{}",key,value);
                    if(proidid==999){
                        continue;
                    }
                    log.info("index 3 matchId:{} ### AoOddsTimer:{}",key,value);
                    Object matchMarketLastUpdateTime = matchParamLastTime.get(key);
                    Long diffTime=14400000L;
                    log.info("index 4 matchId:{} ### AoOddsTimer:{}",key,value);
                    if(!Objects.isNull(matchMarketLastUpdateTime)){
                        log.info("index 5 matchId:{} ### AoOddsTimer:{}",key,value);
                        diffTime =currentTimeSys-Long.valueOf(matchMarketLastUpdateTime.toString());
                    }
                    log.info("index 6 matchId:{} ### AoOddsTimer:{}",key,value);
                    Object cacheTempLateRefreshTime = redisService.get(ESPORT_TEMPLATE_REFRESH_TIME+key);
                    log.info("index 7 matchId:{} ### AoOddsTimer:{}",key,value);
                    Integer timing=Objects.isNull(cacheTempLateRefreshTime) ? 10:Integer.valueOf(cacheTempLateRefreshTime.toString()) ;

                    log.info("差值ID:{},diffTime:{},timing:{},is:{}, 刷新时间::{}, cacheRefreshTime::{},cacheTempLateRefreshTime::{}"
                            ,key,diffTime,(timing*1000L),diffTime>=(timing*1000L),timing,cacheTempLateRefreshTime.toString(),cacheTempLateRefreshTime);
                    if((proidid==6||proidid==7||proidid==41||proidid==42||proidid==50)&&diffTime>=(timing*1000L)&&currentTimeSys-startTime<14400000){
                        if(blockingQueue.size()>10){
                            log.info("耗时:{},足球阻塞队列大小:{},执行赛事:{}",(System.currentTimeMillis()-a),blockingQueue.size(),key);
                        }
                        log.info("AoOddsTimer::football::赛事ID:{}",key);
                        redisTemplate.opsForList().leftPush(AO_REDIS_QUEUE_ESPORT_ODDSTIMER,key);
                        redisService.hSetField(AO_LASTTIME_ESPORT_MARKET_UPDATE,key,currentTimeSys,AO_1DAYS_KEY_TIME);

                    }
                }



            }catch (Exception ex){
                ex.printStackTrace();
            }finally {
               redisService.unLock(redisLocKey, redisLocKey);

            }

            }
        },0,1000, TimeUnit.MILLISECONDS);
        marketsQueue();
    }

    public void marketsQueue(){
        log.info("marketsQueue启动中 2---------------------------");
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
//                      Object aoMatchId = redisTemplate.opsForList().rightPop(AO_REDIS_QUEUE_ESPORT_ODDSTIMER);
                        Object aoMatchId = redisTemplate.boundListOps(AO_REDIS_QUEUE_ESPORT_ODDSTIMER).rightPop();
                        if(null==aoMatchId){
                            continue;
                        }
                        log.info("AoOddsTimer::football2::赛事ID:{}",aoMatchId.toString());
                        String matchId = aoMatchId.toString();
                        Integer proid = supportMarketHandler.supportGetPeriod(matchId);
                        if (proid < 1) {
                            continue;
                        }
                        String linkId = createTraceId();
                        Long startTime=System.currentTimeMillis();
                        log.info("marketsQueue::football::startTime:{}---------------------------{}",startTime,aoMatchId);
                        subjectEsportMatchMarketManager.notifyMarketMessage(matchId,linkId,new ArrayList<>());
                        log.info("marketsQueue::football::endTime:{}---------------------------{}",(System.currentTimeMillis()-startTime),aoMatchId);
                    } catch (Exception ex) {
                        log.error("marketsQueue AoOddsTimer 异常",ex);
                    }
                }
            }
        });


    }

}
