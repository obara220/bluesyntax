package com.panda.aoodds.esport.util;

import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AoRedisTimeOutTime {
    private  ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(32);
    @Autowired
    RedisService redisService;
    @PostConstruct
    public void redisHashMapTimeOut(){
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Long currentTime=System.currentTimeMillis();
                try {
                    Set<String> hashKeySet = RedisKeyConstant.getRedisHashKey();
                    if(hashKeySet.size()<1){
                        return;
                       // throw new ApiException("没有需要清除的缓存");
                    }
                    hashKeySet.stream().parallel().forEach(f->{
                       Map<String,Long> timeOutMap =  redisService.hGetAll(f);
                     String redisKey =  f.replace("expire_","");
                        timeOutMap.forEach((key,value)->{
                            if(currentTime>value){
                                redisService.hDel(redisKey,key);
                                redisService.hDel(f,key);
                                log.info("AoRedisTimeOutTime 已经出缓存Key:{},hashKey:{}",redisKey,key);
                            }
                        });
                    });
                }catch (Exception ex){
                    log.error("AoRedisTimeOutTime",ex);
                }
            }
        },0,600000, TimeUnit.MILLISECONDS);
    }


}
