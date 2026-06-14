package com.panda.aoodds.esport.handle;

import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.AoMatchESportEntity;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.marketstate.MatchMarketStatusTransit;
import com.panda.aoodds.esport.service.EsportMarketMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.*;
import static com.panda.aoodds.esport.common.utils.TraceIdGenerator.createTraceId;

/**
 * C01滚球赔率延迟监控：超过 下发频率 + 宽限秒数 未成功推送则兜底关盘并告警。
 * 由 {@link com.panda.aoodds.esport.util.AoDelayOddTimer} 定时扫描；推送成功后在
 * {@link com.panda.aoodds.esport.service.impl.SubjectEsportMatchMarketImpl} 中记录最后推送时间。
 * 兜底关盘后写入 {@link RedisKeyConstant#AO_ESPORT_ODDS_DELAY_CLOSED}，阻止 {@link com.panda.aoodds.esport.util.AoOddsTimer}
 * 与 notifyMarketMessage 自动重新开盘。
 */
@Slf4j
@Component
@RefreshScope
public class MarketOddsDelayHandler {

    private static final Set<Integer> LIVE_PERIODS = new HashSet<>(Arrays.asList(6, 7, 41, 42, 50));

    @Value("${ao.esport.odds.delay.extra.seconds:5}")
    private int delayExtraSeconds;

    @Value("${ao.esport.odds.delay.default.refresh.seconds:10}")
    private int defaultRefreshSeconds;

    @Value("${ao.esport.odds.delay.warn.cooldown.seconds:60}")
    private int warnCooldownSeconds;

    @Autowired
    private RedisService redisService;
    @Autowired
    private MatchMarketStatusTransit matchMarketStatusTransit;
    @Autowired
    @Qualifier("esportMarketPushService")
    private EsportMarketMessageService esportMarketMessageService;
    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate mongoTemplate;

    public void recordLastPushTime(String matchId) {
        redisService.hSetField(AO_ESPORT_MARKET_LAST_PUSH_TIME, matchId, System.currentTimeMillis(), AO_1DAYS_KEY_TIME);
        redisService.del(AO_ESPORT_ODDS_DELAY_HANDLED + matchId);
    }

    public boolean isDelayClosed(String matchId) {
        return null != redisService.get(AO_ESPORT_ODDS_DELAY_CLOSED + matchId);
    }

    public void checkLiveMatchOddsDelay(String matchId, Integer periodId) {
        if (periodId == null || periodId == 999 || !LIVE_PERIODS.contains(periodId)) {
            return;
        }
        if (isDelayClosed(matchId)) {
            return;
        }
        if (null != redisService.get(AO_ESPORT_ODDS_DELAY_HANDLED + matchId)) {
            return;
        }
        long now = System.currentTimeMillis();
        int refreshSeconds = resolveRefreshSeconds(matchId);
        long thresholdMs = (refreshSeconds + delayExtraSeconds) * 1000L;
        Long lastPushTime = resolveLastPushTime(matchId);
        if (lastPushTime == null) {
            return;
        }
        long delayMs = now - lastPushTime;
        if (delayMs <= thresholdMs) {
            return;
        }
        handleOddsDelay(matchId, delayMs, refreshSeconds);
    }

    private int resolveRefreshSeconds(String matchId) {
        Object cacheRefreshTime = redisService.get(ESPORT_TEMPLATE_REFRESH_TIME + matchId);
        if (Objects.isNull(cacheRefreshTime)) {
            return defaultRefreshSeconds;
        }
        return Integer.parseInt(cacheRefreshTime.toString());
    }

    private Long resolveLastPushTime(String matchId) {
        Object lastPush = redisService.hGet(AO_ESPORT_MARKET_LAST_PUSH_TIME, matchId);
        if (lastPush != null) {
            return Long.valueOf(lastPush.toString());
        }
        Object cacheMarketObj = redisService.get(AO_ESPORT_MATCH_INFO + matchId);
        if (cacheMarketObj instanceof AoMatchMarketInfo) {
            AoMatchMarketInfo matchMarketInfo = (AoMatchMarketInfo) cacheMarketObj;
            if (matchMarketInfo.getPushTime() != null) {
                return matchMarketInfo.getPushTime();
            }
        }
        return null;
    }

    private void handleOddsDelay(String matchId, long delayMs, int refreshSeconds) {
        String linkId = createTraceId();
        log.error("::{}::AO赛事ID:{},赔率延迟超过阈值, delayMs:{}, refreshSeconds:{}, extraSeconds:{}",
                linkId, matchId, delayMs, refreshSeconds, delayExtraSeconds);
        redisService.set(AO_ESPORT_ODDS_DELAY_HANDLED + matchId, System.currentTimeMillis(), warnCooldownSeconds);
        redisService.set(AO_ESPORT_ODDS_DELAY_CLOSED + matchId, System.currentTimeMillis(), AO_1DAYS_KEY_TIME);
        try {
            AoMatchMarketInfo closedInfo = matchMarketStatusTransit.upMatchMarketStatus(linkId, Long.valueOf(matchId), "g_goal");
            if (closedInfo != null) {
                esportMarketMessageService.sendMarketMessage(closedInfo);
            }
        } catch (Exception ex) {
            log.error("::{}::AO赛事ID:{},赔率延迟兜底关盘异常", linkId, matchId, ex);
        }
        AoMatchMarketInfo aoMatchMarketInfo = buildMatchMarketInfo(matchId, linkId);
        esportMarketMessageService.sendMatchStatusMessage(aoMatchMarketInfo);
        esportMarketMessageService.sendOddsDelayWarnMessage(aoMatchMarketInfo, resolveMachineId(matchId), delayMs);
    }

    private AoMatchMarketInfo buildMatchMarketInfo(String matchId, String linkId) {
        Object cacheMarketObj = redisService.get(AO_ESPORT_MATCH_INFO + matchId);
        if (cacheMarketObj instanceof AoMatchMarketInfo) {
            AoMatchMarketInfo cached = (AoMatchMarketInfo) cacheMarketObj;
            cached.setLinkeId(linkId);
            cached.setLiveFlag(1);
            return cached;
        }
        AoMatchMarketInfo aoMatchMarketInfo = new AoMatchMarketInfo();
        aoMatchMarketInfo.setMatchSourceId(matchId);
        aoMatchMarketInfo.setSportId(Long.valueOf(CommonConstant.SPORT_FOOTBALL));
        aoMatchMarketInfo.setLinkeId(linkId);
        aoMatchMarketInfo.setLiveFlag(1);
        aoMatchMarketInfo.setRequestType("g_goal");
        return aoMatchMarketInfo;
    }

    private String resolveMachineId(String matchId) {
        try {
            AoMatchESportEntity entity = mongoTemplate.findOne(
                    Query.query(Criteria.where("aoMatchId").is(Long.valueOf(matchId))),
                    AoMatchESportEntity.class,
                    CommonConstant.MATCH_INFO_VS);
            if (entity != null && entity.getMachineId() != null) {
                return entity.getMachineId();
            }
        } catch (Exception ex) {
            log.warn("::AO赛事ID:{},查询machineId失败", matchId, ex);
        }
        return "NA";
    }
}
