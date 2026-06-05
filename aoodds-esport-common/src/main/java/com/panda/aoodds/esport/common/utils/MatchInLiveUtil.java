package com.panda.aoodds.esport.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 判断赛事滚球阶段
 */
@Component
public class MatchInLiveUtil {

    @Autowired
    private RedisService redisService;

    /**
     * AO赛事  0=未开赛、1=滚球
     * @param aoMatchId
     * @param sportId
     * @return
     */
    public Integer matchInLive(String aoMatchId, Long sportId) {
        //0=未开赛、1=滚球 ,融合滚球标识存在就以融合为准，不存在（旧数据），以事件阶段为准
        Integer matchType = 0;
        Integer integer = supportGetBkPeriod(aoMatchId, sportId);
        matchType = integer == 0 ? 1 : 0;
        return matchType;
    }


    /**
     * 盘口标识（0=滚球、1=赛前）
     *
     * @param aoMatchId
     * @param sportId
     * @return
     */
    public Integer supportGetBkPeriod(String aoMatchId, Long sportId) {
        Integer marketType = 1;
        if (sportId == 1L) {
            Object period = redisService.hGet(RedisKeyConstant.AO_ESPORT_BGTIME_KEY, aoMatchId);
            marketType = !ObjectUtil.isNull(period) ? 0 : 1;
        } else if (sportId == 2L) {
            Object matchTimer = redisService.hGet(RedisKeyConstant.AO_ESPORT_BK_BGTIME_KEY, aoMatchId);
            Integer[] period = new Integer[2];
            if (null != matchTimer) {
                String[] prid = matchTimer.toString().split("#");
                marketType = Integer.valueOf(prid[1]) != 0 ? 0 : 1;
            }
        }
        return marketType;
    }

}
