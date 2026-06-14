package com.panda.aoodds.esport.util;

import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.handle.MarketOddsDelayHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.AO_ESPORT_BGTIME_KEY;

/**
 * C01电子足球滚球赔率延迟独立监听器。
 * 与 {@link AoOddsTimer} 解耦：AoOddsTimer 负责赔率下发调度，本类仅负责延迟检测与兜底关盘告警。
 */
@Slf4j
@Component
public class AoDelayOddTimer implements ApplicationListener<ApplicationReadyEvent> {

    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(2);

    @Autowired
    private RedisService redisService;
    @Autowired
    private MarketOddsDelayHandler marketOddsDelayHandler;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("AoDelayOddTimer 启动中 ---------------------------");
        String redisLocKey = RedisKeyConstant.MATCH_LOCK + "AoDelayOddTimer";
        EXECUTOR.scheduleWithFixedDelay(() -> {
            try {
                redisService.tryLock(redisLocKey, redisLocKey, 10, 10);
                Map<Object, Object> matchTimer = redisService.hGetAll(AO_ESPORT_BGTIME_KEY);
                for (Map.Entry<Object, Object> entity : matchTimer.entrySet()) {
                    String matchId = entity.getKey().toString();
                    String[] periodArr = entity.getValue().toString().split("#");
                    Integer periodId = Integer.parseInt(periodArr[1]);
                    if(periodId==6||periodId==7){
                        marketOddsDelayHandler.checkLiveMatchOddsDelay(matchId, periodId);
                    }
                }
            } catch (Exception ex) {
                log.error("AoDelayOddTimer 异常", ex);
            } finally {
                redisService.unLock(redisLocKey, redisLocKey);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }
}
