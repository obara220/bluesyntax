package com.panda.aoodds.esport.api.entity;

import com.panda.aoodds.esport.api.enums.ScoreType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 赛事盘口信息
 *
 * @author Samuel
 */
@Slf4j
@Data
public class AoMatchMarketInfo<T> implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源编码
     */
    private final String dataSourceCode = "AO";

    /**
     * 运动类型Id
     */
    private Long sportId;

    /**
     * AO赛事Id
     */
    private String matchSourceId;

    /**
     * 赛事滚球标识（0=未开赛、1=滚球）
     */
    private Integer liveFlag;

    /**
     * 主队Id
     */
    private String homeTeamId;

    /**
     * 客队Id
     */
    private String awayTeamId;

    /**
     * 比分集合
     */
    private Map<ScoreType, HomeAwayScore<Integer>> scoreSummary;

    /**
     * 盘口集合
     */
    private List<T> marketList;
    private String marketTime;
    private Integer period;
    /**
     * 最后修改时间戳
     */
    private Long modifyTime;
    private String linkeId;
    private String requestType;
    private Long startTime;//接收到数据指令的时间
    private Long pushTime;//push到队列的时间 /作为DataSourceTime
    /**
     * 数据判断这个A01参数校验是否需要的一个条件
     */
    private Integer verifyG0;

    /**
     * 上半场剩余A01 GE参数
     */
    private Double htG0Left;

    /**
     * 全场剩余A01 GE参数
     */
    private Double ftG0Left;

    /**
     * 半场当前比分概率
     */
    private Double htScoreProb;

    /**
     * 全场当前比分概率
     */
    private Double ftScoreProb;

    @Override
    public Object clone() {
        AoMatchMarketInfo scoreParam = null;
        try {
            //克隆后需要转型
            scoreParam = (AoMatchMarketInfo) super.clone();
            //此处需要捕捉异常
        } catch (Exception e) {
            log.error("克隆对象出错，异常信息：", e);
        }
        return scoreParam;
    }
}
