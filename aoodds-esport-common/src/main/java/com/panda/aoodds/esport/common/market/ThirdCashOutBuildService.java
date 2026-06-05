package com.panda.aoodds.esport.common.market;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.panda.aoodds.esport.api.entity.BetPieceEntity;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.entity.HomeAwayScore;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.enums.ScoreType;
import com.panda.merge.dto.ThirdMarketOddsPreResultDTO;
import com.panda.merge.dto.ThirdMarketPreResultDTO;
import com.panda.merge.dto.ThirdMatchPreResultDTO;
import com.panda.sports.algo.api.entity.MarketProbabilityEntity;
import com.panda.sports.algo.api.entity.OutcomeProbabilityEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.constant.CommonConstant.*;
import static com.panda.aoodds.esport.common.enums.MarketModel.*;
import static com.panda.aoodds.esport.common.enums.ScoreType.*;
import static com.panda.aoodds.esport.common.market.MarketSelection.*;

/**
 * 盘口提前结算处理器
 *
 * @author Samuel
 */
@Service
@Slf4j
@RefreshScope
public class ThirdCashOutBuildService {

    /**
     * 提前结算主盘口下发数量
     */
    @Value("${ao.coGoalMainMarketCount}")
    private Integer coGoalMainMarketCount;

    /**
     * 提前结算主盘口集合
     */
    private static final List<Integer> CASH_OUT_MAIN_MARKET_LIST = Arrays.asList(10004, 10005, 20004, 20005);

    /**
     * 提前结算玩法集合
     */
    private static final List<Integer> CASH_OUT_MARKET_LIST = Arrays.asList(10001, 10004, 10005, 20001, 20004, 20005, 10008, 20008, 30003, 10019, 10006, 10007, 10015, 10016, 10003, 10002, 30001, 30002, 10013,
            10014, 10038, 20012, 10021, 10009, 10041, 10044, 10050, 10017, 10018, 20013, 20011, 20006, 20007, 20002, 10035, 10034, 10033);

    /**
     * 构建三方赛事盘口投递对象
     *
     * @param aoMatchMarketInfo 自定义赛事盘口对象
     * @param linkId            链路Id
     * @return 三方赛事盘口投递对象
     */
    public ThirdMatchPreResultDTO processAoCashOut(AoMatchMarketInfo<MarketsEntity> aoMatchMarketInfo, String linkId) {
        log.info("ao.coGoalMainMarketCount:" + coGoalMainMarketCount);
        //组装提前结算对象
        ThirdMatchPreResultDTO thirdMatchPreResultDTO = new ThirdMatchPreResultDTO();
        thirdMatchPreResultDTO.setSportId(aoMatchMarketInfo.getSportId());
        thirdMatchPreResultDTO.setThirdMatchId(aoMatchMarketInfo.getMatchSourceId());
        thirdMatchPreResultDTO.setDataSourceCode(AO_SOURCE_CODE);

        List<ThirdMarketPreResultDTO> thirdMarketPreResultDTOList = aoMatchMarketInfo.getMarketList().stream()
                .filter(marketsEntity -> CASH_OUT_MARKET_LIST.contains(marketsEntity.getMarketId()))
                .filter(marketsEntity -> !CASH_OUT_MAIN_MARKET_LIST.contains(marketsEntity.getMarketId()) || (marketsEntity.getOrder() <= coGoalMainMarketCount))
                .map(marketsEntity -> buildThirdCashOutMarket(marketsEntity, aoMatchMarketInfo, linkId))
                .filter(Objects::nonNull).collect(Collectors.toList());
        thirdMatchPreResultDTO.setMarketResultList(thirdMarketPreResultDTOList);
        log.info("::{}::构建三方提前结算盘口成功", linkId);
        return thirdMatchPreResultDTO;
    }

    /**
     * 构建三方盘口提前结算投递对象（新）
     *
     * @param aoMatchMarketInfo 自定义赛事盘口对象
     * @param linkId            链路Id
     * @return 三方赛事盘口投递对象
     */
    public ThirdMatchPreResultDTO processAoCashOutNew(AoMatchMarketInfo<MarketProbabilityEntity> aoMatchMarketInfo, String linkId) {
        //组装提前结算对象
        ThirdMatchPreResultDTO thirdMatchPreResultDTO = new ThirdMatchPreResultDTO();
        thirdMatchPreResultDTO.setSportId(aoMatchMarketInfo.getSportId());
        thirdMatchPreResultDTO.setThirdMatchId(aoMatchMarketInfo.getMatchSourceId());
        thirdMatchPreResultDTO.setDataSourceCode(AO_SOURCE_CODE);
        //将每个提前结算盘口对象处理成统一的三方投递数据格式
        List<ThirdMarketPreResultDTO> thirdMarketPreResultDTOList = aoMatchMarketInfo.getMarketList().stream()
                .map(marketProbabilityEntity -> buildThirdCashOutMarketNew(marketProbabilityEntity, aoMatchMarketInfo, linkId))
                .filter(Objects::nonNull).collect(Collectors.toList());
        thirdMatchPreResultDTO.setMarketResultList(thirdMarketPreResultDTOList);
        log.info("::{}::构建三方提前结算盘口成功", linkId);
        return thirdMatchPreResultDTO;
    }

    /**
     * 15分钟玩法集合
     */
    private static final List<Integer> FIFTEEN_MARKET_LIST = Arrays.asList(30001, 30002, 30003, 40022, 40023, 40024, 50029, 50030, 50031);

    /**
     * 构建三方提前结算盘口投递对象
     *
     * @param marketsEntity     自定义盘口实体
     * @param aoMatchMarketInfo 自定义赛事盘口对象
     * @param linkId            链路Id
     * @return 三方提前结算盘口对象
     */
    private ThirdMarketPreResultDTO buildThirdCashOutMarket(MarketsEntity marketsEntity, AoMatchMarketInfo aoMatchMarketInfo, String linkId) {
        //组装提前结算盘口对象
        ThirdMarketPreResultDTO thirdMarketPreResultDTO = new ThirdMarketPreResultDTO();
        try {
            //三方赛事源Id
            String matchSourceId = aoMatchMarketInfo.getMatchSourceId();
            thirdMarketPreResultDTO.setThirdMatchId(matchSourceId);
            //三方盘口源Id
            Map<ScoreType, HomeAwayScore<Integer>> scoreSummary = aoMatchMarketInfo.getScoreSummary();
            Integer marketId = marketsEntity.getMarketId();
            String marketName = marketsEntity.getMarketName();
            String specifierValue = StrUtil.isNotBlank(marketsEntity.getHandicap()) ? marketsEntity.getHandicap() : "";
            //盘口Id中基准分盘口值转换成全场盘口值
            if (HANDICAP_LIST.contains(marketId)) {
                HomeAwayScore<Integer> homeAwayScore = getHandicapScore(scoreSummary, marketId);
                Integer homeScore = homeAwayScore.getHomeScore();
                Integer awayScore = homeAwayScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore) && 80002 != marketId) {
                    specifierValue = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                }
            } else if (INTERVAL_HANDICAP_LIST.contains(marketId)) {
                HomeAwayScore<Integer> intervalScore = getIntervalHandicapScore(marketName, scoreSummary, marketId);
                Integer homeScore = intervalScore.getHomeScore();
                Integer awayScore = intervalScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    specifierValue = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                }
            }
            if (FIFTEEN_MARKET_LIST.contains(marketId)) {
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                specifierValue = specifierValue + PUNCTUATION_UNDERLINE + intervalArr[0] + PUNCTUATION_UNDERLINE + intervalArr[1];
            }
            String thirdMarketSourceId = matchSourceId + PUNCTUATION_UNDERLINE + marketId + PUNCTUATION_UNDERLINE + specifierValue;
            thirdMarketPreResultDTO.setThirdMarketId(thirdMarketSourceId);
            //三方玩法源Id
            String thirdMarketCategorySourceId = AO_SOURCE_CODE + PUNCTUATION_COLON + marketId;
            thirdMarketPreResultDTO.setThirdMarketCategorySourceId(thirdMarketCategorySourceId);
            //盘口类型（1：赛前盘；0：滚球盘；2：冠军玩法）
            Integer liveFlag = aoMatchMarketInfo.getLiveFlag();
            if (null == liveFlag) {
                thirdMarketPreResultDTO.setMarketType(NUMBER_ONE);
            } else {
                thirdMarketPreResultDTO.setMarketType(1 == liveFlag ? NUMBER_ZERO : NUMBER_ONE);
            }
            //赛事阶段
            Integer matchPeriod = aoMatchMarketInfo.getPeriod();
            if (1 == matchPeriod) {
                //上半场
                if (0 == thirdMarketPreResultDTO.getMarketType()) {
                    matchPeriod = 6;
                    //未开赛
                } else {
                    matchPeriod = 0;
                }
                //下半场
            } else if (2 == matchPeriod) {
                matchPeriod = 7;
            }
            thirdMarketPreResultDTO.setMatchPeriod(matchPeriod);

            //设置提前结算附加字段
            setCashOutAddition(thirdMarketPreResultDTO, marketsEntity, scoreSummary);

            List<BetPieceEntity> betPieceEntityList = marketsEntity.getBetPieceEntities();
            if (CollUtil.isNotEmpty(betPieceEntityList)) {
                if (betPieceEntityList.stream().filter(BetPieceEntity::isActive).count() > 1) {
                    //盘口提前结算状态
                    thirdMarketPreResultDTO.setCashOutStatus(1);
                    //和局概率
                    if (Objects.nonNull(betPieceEntityList.get(0).getRefundProb())) {
                        thirdMarketPreResultDTO.setDrawCalcProb(BigDecimal.valueOf(betPieceEntityList.get(0).getRefundProb()));
                    }
                    //三方提前结算投注项集合
                    List<ThirdMarketOddsPreResultDTO> thirdMarketOddsPreResultDTOList = betPieceEntityList.stream().map(betPieceEntity -> buildCashOutOdds(betPieceEntity, aoMatchMarketInfo, thirdMarketSourceId)).collect(Collectors.toList());
                    thirdMarketPreResultDTO.setMarketOddsResultList(thirdMarketOddsPreResultDTOList);
                } else {
                    thirdMarketPreResultDTO.setCashOutStatus(-1);
                }
            } else {
                thirdMarketPreResultDTO.setCashOutStatus(-1);
            }
        } catch (Exception e) {
            log.error("::" + linkId + "::构建AO三方提前结算盘口【" + thirdMarketPreResultDTO.getThirdMarketId() + "】数据异常：", e);
            return null;
        }
        return thirdMarketPreResultDTO;
    }

    /**
     * 构建三方提前结算盘口投递对象（新）
     *
     * @param marketProbabilityEntity 自定义提前结算盘口实体
     * @param aoMatchMarketInfo       自定义赛事盘口对象
     * @param linkId                  链路Id
     * @return 三方提前结算盘口对象
     */
    private ThirdMarketPreResultDTO buildThirdCashOutMarketNew(MarketProbabilityEntity marketProbabilityEntity, AoMatchMarketInfo<MarketProbabilityEntity> aoMatchMarketInfo, String linkId) {
        //组装提前结算盘口对象
        ThirdMarketPreResultDTO thirdMarketPreResultDTO = new ThirdMarketPreResultDTO();
        try {
            //三方赛事源Id
            String matchSourceId = aoMatchMarketInfo.getMatchSourceId();
            thirdMarketPreResultDTO.setThirdMatchId(matchSourceId);
            //三方盘口源Id
            Map<ScoreType, HomeAwayScore<Integer>> scoreSummary = aoMatchMarketInfo.getScoreSummary();
            Integer marketId = marketProbabilityEntity.getMarketId();
            String marketName = marketProbabilityEntity.getMarketName();
            String specifierValue = StrUtil.isNotBlank(marketProbabilityEntity.getHandicap()) ? marketProbabilityEntity.getHandicap() : "";
            if(MarketCategory.SCORE_MARKETTYPE_CATEGORY.get(G_GOAL.toString().toLowerCase()).contains(marketId)){
                thirdMarketPreResultDTO.setVerifyG0(aoMatchMarketInfo.getVerifyG0());
                thirdMarketPreResultDTO.setFtG0Left(aoMatchMarketInfo.getFtG0Left());
                thirdMarketPreResultDTO.setHtG0Left(aoMatchMarketInfo.getHtG0Left());
            }
            //盘口Id中基准分盘口值转换成全场盘口值
            if (HANDICAP_LIST.contains(marketId)) {
                HomeAwayScore<Integer> homeAwayScore = getHandicapScore(scoreSummary, marketId);
                Integer homeScore = homeAwayScore.getHomeScore();
                Integer awayScore = homeAwayScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore) && 80002 != marketId) {
                    specifierValue = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                }
            } else if (INTERVAL_HANDICAP_LIST.contains(marketId)) {
                HomeAwayScore<Integer> intervalScore = getIntervalHandicapScore(marketName, scoreSummary, marketId);
                Integer homeScore = intervalScore.getHomeScore();
                Integer awayScore = intervalScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    specifierValue = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                }
            }
            if (FIFTEEN_MARKET_LIST.contains(marketId)) {
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                specifierValue = specifierValue + PUNCTUATION_UNDERLINE + intervalArr[0] + PUNCTUATION_UNDERLINE + intervalArr[1];
            }
            String thirdMarketSourceId = matchSourceId + PUNCTUATION_UNDERLINE + marketId + PUNCTUATION_UNDERLINE + specifierValue;
            thirdMarketPreResultDTO.setThirdMarketId(thirdMarketSourceId);
            //三方玩法源Id
            String thirdMarketCategorySourceId = AO_SOURCE_CODE + PUNCTUATION_COLON + marketId;
            thirdMarketPreResultDTO.setThirdMarketCategorySourceId(thirdMarketCategorySourceId);
            //盘口类型（1：赛前盘；0：滚球盘；2：冠军玩法）
            Integer liveFlag = aoMatchMarketInfo.getLiveFlag();
            if (null == liveFlag) {
                thirdMarketPreResultDTO.setMarketType(NUMBER_ONE);
            } else {
                thirdMarketPreResultDTO.setMarketType(1 == liveFlag ? NUMBER_ZERO : NUMBER_ONE);
            }
            //赛事阶段
            thirdMarketPreResultDTO.setMatchPeriod(aoMatchMarketInfo.getPeriod());

            //设置提前结算附加字段
            setCashOutAdditionNew(thirdMarketPreResultDTO, marketProbabilityEntity, scoreSummary);

            //设置盘口提前结算状态
            thirdMarketPreResultDTO.setCashOutStatus(marketProbabilityEntity.getCashOutStatus());

            List<OutcomeProbabilityEntity> outcomeProbabilityEntityList = marketProbabilityEntity.getOutcomeProbabilityEntityList();
            //和局概率
            if (Objects.nonNull(outcomeProbabilityEntityList.get(0).getRefundProb())) {
                thirdMarketPreResultDTO.setDrawCalcProb(BigDecimal.valueOf(outcomeProbabilityEntityList.get(0).getRefundProb()));
            }

            //设置半场当前比分概率
            Double htScoreProb = aoMatchMarketInfo.getHtScoreProb();
            if (Objects.nonNull(htScoreProb)) {
                thirdMarketPreResultDTO.setHtScoreProb(BigDecimal.valueOf(htScoreProb));
            }
            //设置全场当前比分概率
            Double ftScoreProb = aoMatchMarketInfo.getFtScoreProb();
            if (Objects.nonNull(ftScoreProb)) {
                thirdMarketPreResultDTO.setFtScoreProb(BigDecimal.valueOf(ftScoreProb));
            }

            //三方提前结算投注项集合
            List<ThirdMarketOddsPreResultDTO> thirdMarketOddsPreResultDTOList = outcomeProbabilityEntityList.stream().map(betPieceEntity -> buildCashOutOddsNew(betPieceEntity, aoMatchMarketInfo, thirdMarketSourceId)).collect(Collectors.toList());
            thirdMarketPreResultDTO.setMarketOddsResultList(thirdMarketOddsPreResultDTOList);
        } catch (Exception e) {
            log.error("::" + linkId + "::构建AO三方提前结算盘口【" + thirdMarketPreResultDTO.getThirdMarketId() + "】数据异常：", e);
            return null;
        }
        return thirdMarketPreResultDTO;
    }

    /**
     * 根据比分类型获取比分
     *
     * @param scoreSummary 比分集合
     * @param scoreType    比分类型
     * @return 比分类型对应比分
     */
    private HomeAwayScore<Integer> getHomeAwayScore(Map<ScoreType, HomeAwayScore<Integer>> scoreSummary, ScoreType scoreType) {
        if (Objects.isNull(scoreSummary) || Objects.isNull(scoreSummary.get(scoreType))) {
            return new HomeAwayScore<>(NUMBER_ZERO, NUMBER_ZERO);
        } else {
            return scoreSummary.get(scoreType);
        }
    }

    /**
     * 根据玩法Id获取让球盘需要的比分
     *
     * @param scoreSummary 比分集合
     * @param marketId     玩法Id
     * @return 让球盘需要的比分
     */
    private HomeAwayScore<Integer> getHandicapScore(Map<ScoreType, HomeAwayScore<Integer>> scoreSummary, Integer marketId) {
        ScoreType scoreType;
        //全场比分
        if (10004 == marketId || 20004 == marketId || 70002 == marketId || 70009 == marketId) {
            scoreType = FULL_TIME_SCORE;
            //全场角球比分
        } else if (40002 == marketId || 40009 == marketId || 40033 == marketId || 40035 == marketId) {
            scoreType = FT_CORNER;
            //全场罚牌比分
        } else if (50002 == marketId || 50006 == marketId || 50040 == marketId || 50043 == marketId) {
            scoreType = FT_BOOKING;
            //全场黄牌比分
        } else if (50023 == marketId || 50026 == marketId) {
            scoreType = FT_YELLOW;
            //下半场比分
        } else if (60015 == marketId) {
            scoreType = HALF2_SCORE;
            //点球大战比分
        } else if (80002 == marketId) {
            scoreType = FT_PK;
        } else {
            throw new IllegalArgumentException("AO玩法【" + marketId + "】非让球类盘口！！！");
        }
        return getHomeAwayScore(scoreSummary, scoreType);
    }

    /**
     * 根据玩法Id获取区间让球盘需要的比分
     *
     * @param marketName   盘口名称
     * @param scoreSummary 比分集合
     * @param marketId     玩法Id
     * @return 需要的比分
     */
    private HomeAwayScore<Integer> getIntervalHandicapScore(String marketName, Map<ScoreType, HomeAwayScore<Integer>> scoreSummary, Integer marketId) {
        String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
        ScoreType intervalScoreType = null;
        //进球比分
        if (30002 == marketId) {
            switch (intervalArr[0]) {
                case "1":
                    intervalScoreType = FIFTEEN_00TO15_SCORE;
                    break;
                case "16":
                    intervalScoreType = FIFTEEN_16TO30_SCORE;
                    break;
                case "31":
                    intervalScoreType = FIFTEEN_31TO45_SCORE;
                    break;
                case "46":
                    intervalScoreType = FIFTEEN_46TO60_SCORE;
                    break;
                case "61":
                    intervalScoreType = FIFTEEN_61TO75_SCORE;
                    break;
                case "76":
                    intervalScoreType = FIFTEEN_76TO90_SCORE;
                    break;
                default:
                    break;
            }
            //角球比分
        } else if (40023 == marketId) {
            switch (intervalArr[0]) {
                case "1":
                    intervalScoreType = CORNER_00TO15_SCORE;
                    break;
                case "16":
                    intervalScoreType = CORNER_16TO30_SCORE;
                    break;
                case "31":
                    intervalScoreType = CORNER_31TO45_SCORE;
                    break;
                case "46":
                    intervalScoreType = CORNER_46TO60_SCORE;
                    break;
                case "61":
                    intervalScoreType = CORNER_61TO75_SCORE;
                    break;
                case "76":
                    intervalScoreType = CORNER_76TO90_SCORE;
                    break;
                default:
                    break;
            }
            //罚牌比分
        } else if (50030 == marketId) {
            switch (intervalArr[0]) {
                case "1":
                    intervalScoreType = BOOKING_00TO15_SCORE;
                    break;
                case "16":
                    intervalScoreType = BOOKING_16TO30_SCORE;
                    break;
                case "31":
                    intervalScoreType = BOOKING_31TO45_SCORE;
                    break;
                case "46":
                    intervalScoreType = BOOKING_46TO60_SCORE;
                    break;
                case "61":
                    intervalScoreType = BOOKING_61TO75_SCORE;
                    break;
                case "76":
                    intervalScoreType = BOOKING_76TO90_SCORE;
                    break;
                default:
                    break;
            }
        }
        return getHomeAwayScore(scoreSummary, intervalScoreType);
    }

    /**
     * 需要设置盘口附加字段的玩法集合
     */
    private static final List<Integer> MARKET_ADDITION_LIST = Arrays.asList(10004, 10005, 10006, 10007, 10009, 10010, 10011, 10012, 20004, 20005, 20006, 20007, 20009, 20010, 30001, 30002, 30003,
            10013, 10016, 10017, 10018, 10033, 10035, 20011, 40002, 40003, 40005, 40006, 40009, 40010, 40012, 40013, 50002, 50004, 50006, 50008, 60003, 60005, 10039, 10040, 10047, 10048,
            10049, 20015, 20016, 20017, 20018, 60010, 60011, 60013, 60015, 40014, 40015, 40016, 40017, 40018, 40019, 40020, 40021, 40022, 40023, 40024, 40025, 50012, 50013, 50014, 50018, 50019,
            50023, 50024, 50026, 50027, 70002, 70003, 70007, 70009, 70010, 80002, 80003, 80006, 80007, 80008, 80011, 80012, 20021, 60016, 10051, 10052, 10053, 10055, 10061, 10064, 11002, 11003,
            11005, 11006, 11008, 11009, 11011, 11012, 11014, 11015, 11017, 11018, 11020, 11021, 11023, 11024, 11026, 11027, 11029, 11030, 11032, 11033, 11035, 11036, 11038, 11039, 11041, 11042,
            50029, 50030, 50031, 60020, 60021, 11043, 11044, 11045, 11046, 11047, 11048, 11049, 11050, 11051, 11052, 11053, 11054, 11055, 11056, 11057, 11058, 11059, 11061, 11062, 11063, 11064,
            40028);

    /**
     * 让球类玩法，ad1设置基准分盘口，ad2设置全场盘口，ad3、ad4设置主客比分
     */
    private static final List<Integer> HANDICAP_LIST = Arrays.asList(10004, 20004, 40002, 40009, 50002, 50006, 50023, 50026, 60015, 70002, 70009, 80002);

    /**
     * 区间类玩法，ad2设置区间起始值，ad3设置区间结束值
     */
    private static final List<Integer> INTERVAL_LIST = Arrays.asList(30001, 40022, 50029);

    /**
     * 区间盘口值类玩法，ad1设置盘口值，ad2设置区间起始值，ad3设置区间结束值
     */
    private static final List<Integer> INTERVAL_TOTAL_LIST = Arrays.asList(30003, 40024, 50031);

    /**
     * 区间让球类玩法，ad1设置基准分盘口，ad2设置全场盘口，ad3、ad4设置区间主客比分，ad5设置逗号隔开的起止区间
     */
    private static final List<Integer> INTERVAL_HANDICAP_LIST = Arrays.asList(30002, 40023, 50030);

    /**
     * 第一节玩法
     */
    private final List<Integer> quarter1List = Arrays.asList(11023, 11024, 11056, 11061);
    /**
     * 第二节玩法
     */
    private final List<Integer> quarter2List = Arrays.asList(11029, 11030, 11057, 11062);
    /**
     * 第三节玩法
     */
    private final List<Integer> quarter3List = Arrays.asList(11035, 11036, 11058, 11063);
    /**
     * 第四节玩法
     */
    private final List<Integer> quarter4List = Arrays.asList(11041, 11042, 11059, 11064);

    /**
     * 篮球上半场让分 ，下半场让分 add1 设置 为 add2
     */
    private final List<Integer> handicapAdd2List = Arrays.asList(11009, 11015);

    /**
     * 设置提前结算附加信息
     *
     * @param thirdMarketPreResultDTO 三方提前结算投递对象
     * @param marketsEntity           自定义盘口实体
     * @param scoreSummary            赛事比分集合
     */
    private void setCashOutAddition(ThirdMarketPreResultDTO thirdMarketPreResultDTO, MarketsEntity marketsEntity, Map<ScoreType, HomeAwayScore<Integer>> scoreSummary) {
        //盘口源Id
        Integer marketId = marketsEntity.getMarketId();
        String specifierValue = StrUtil.isNotBlank(marketsEntity.getHandicap()) ? marketsEntity.getHandicap() : "";
        //盘口名称
        String marketName = marketsEntity.getMarketName();

        if (MARKET_ADDITION_LIST.contains(marketId)) {
            if (HANDICAP_LIST.contains(marketId)) {
                HomeAwayScore<Integer> homeAwayScore = getHandicapScore(scoreSummary, marketId);
                Integer homeScore = homeAwayScore.getHomeScore();
                Integer awayScore = homeAwayScore.getAwayScore();
                thirdMarketPreResultDTO.setAddition1(specifierValue);
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    String globalHandicap = (80002 == marketId) ? specifierValue : new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                    thirdMarketPreResultDTO.setAddition2(globalHandicap);
                    thirdMarketPreResultDTO.setAddition3(homeScore.toString());
                    thirdMarketPreResultDTO.setAddition4(awayScore.toString());
                } else {
                    thirdMarketPreResultDTO.setAddition2(thirdMarketPreResultDTO.getAddition1());
                }

            } else if (INTERVAL_LIST.contains(marketId)) {
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                thirdMarketPreResultDTO.setAddition2(intervalArr[0]);
                thirdMarketPreResultDTO.setAddition3(intervalArr[1]);

            } else if (INTERVAL_TOTAL_LIST.contains(marketId)) {
                thirdMarketPreResultDTO.setAddition1(specifierValue);
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                thirdMarketPreResultDTO.setAddition2(intervalArr[0]);
                thirdMarketPreResultDTO.setAddition3(intervalArr[1]);

            } else if (INTERVAL_HANDICAP_LIST.contains(marketId)) {
                thirdMarketPreResultDTO.setAddition1(specifierValue);
                HomeAwayScore<Integer> intervalScore = getIntervalHandicapScore(marketName, scoreSummary, marketId);
                Integer homeScore = intervalScore.getHomeScore();
                Integer awayScore = intervalScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    String globalHandicap = (80002 == marketId) ? specifierValue : new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                    thirdMarketPreResultDTO.setAddition2(globalHandicap);
                    thirdMarketPreResultDTO.setAddition3(homeScore.toString());
                    thirdMarketPreResultDTO.setAddition4(awayScore.toString());
                } else {
                    thirdMarketPreResultDTO.setAddition2(thirdMarketPreResultDTO.getAddition1());
                }
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                thirdMarketPreResultDTO.setAddition5(intervalArr[0] + PUNCTUATION_COMMA + intervalArr[1]);

            } else if (quarter1List.contains(marketId)) {
                if (11061 == marketId) {
                    thirdMarketPreResultDTO.setAddition1(String.valueOf(1));
                    thirdMarketPreResultDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketPreResultDTO.setAddition1(specifierValue);
                    thirdMarketPreResultDTO.setAddition2(String.valueOf(1));
                }

            } else if (quarter2List.contains(marketId)) {
                if (11062 == marketId) {
                    thirdMarketPreResultDTO.setAddition1(String.valueOf(2));
                    thirdMarketPreResultDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketPreResultDTO.setAddition1(specifierValue);
                    thirdMarketPreResultDTO.setAddition2(String.valueOf(2));
                }

            } else if (quarter3List.contains(marketId)) {
                if (11063 == marketId) {
                    thirdMarketPreResultDTO.setAddition1(String.valueOf(3));
                    thirdMarketPreResultDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketPreResultDTO.setAddition1(specifierValue);
                    thirdMarketPreResultDTO.setAddition2(String.valueOf(3));
                }

            } else if (quarter4List.contains(marketId)) {
                if (11064 == marketId) {
                    thirdMarketPreResultDTO.setAddition1(String.valueOf(4));
                    thirdMarketPreResultDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketPreResultDTO.setAddition1(specifierValue);
                    thirdMarketPreResultDTO.setAddition2(String.valueOf(4));
                }

            } else if (handicapAdd2List.contains(marketId)) {
                thirdMarketPreResultDTO.setAddition1(specifierValue);
                thirdMarketPreResultDTO.setAddition2(specifierValue);

            } else {
                thirdMarketPreResultDTO.setAddition1(specifierValue);
            }
        }
    }

    /**
     * 设置提前结算附加信息（新）
     *
     * @param thirdMarketPreResultDTO 三方提前结算投递对象
     * @param marketProbabilityEntity 自定义提前结算盘口实体
     * @param scoreSummary            赛事比分集合
     */
    private void setCashOutAdditionNew(ThirdMarketPreResultDTO thirdMarketPreResultDTO, MarketProbabilityEntity marketProbabilityEntity, Map<ScoreType, HomeAwayScore<Integer>> scoreSummary) {
        //盘口源Id
        Integer marketId = marketProbabilityEntity.getMarketId();
        String specifierValue = StrUtil.isNotBlank(marketProbabilityEntity.getHandicap()) ? marketProbabilityEntity.getHandicap() : "";
        //盘口名称
        String marketName = marketProbabilityEntity.getMarketName();

        if (MARKET_ADDITION_LIST.contains(marketId)) {
            if (HANDICAP_LIST.contains(marketId)) {
                HomeAwayScore<Integer> homeAwayScore = getHandicapScore(scoreSummary, marketId);
                Integer homeScore = homeAwayScore.getHomeScore();
                Integer awayScore = homeAwayScore.getAwayScore();
                thirdMarketPreResultDTO.setAddition1(specifierValue);
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    String globalHandicap = (80002 == marketId) ? specifierValue : new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                    thirdMarketPreResultDTO.setAddition2(globalHandicap);
                    thirdMarketPreResultDTO.setAddition3(homeScore.toString());
                    thirdMarketPreResultDTO.setAddition4(awayScore.toString());
                } else {
                    thirdMarketPreResultDTO.setAddition2(thirdMarketPreResultDTO.getAddition1());
                }

            } else if (INTERVAL_LIST.contains(marketId)) {
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                thirdMarketPreResultDTO.setAddition2(intervalArr[0]);
                thirdMarketPreResultDTO.setAddition3(intervalArr[1]);

            } else if (INTERVAL_TOTAL_LIST.contains(marketId)) {
                thirdMarketPreResultDTO.setAddition1(specifierValue);
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                thirdMarketPreResultDTO.setAddition2(intervalArr[0]);
                thirdMarketPreResultDTO.setAddition3(intervalArr[1]);

            } else if (INTERVAL_HANDICAP_LIST.contains(marketId)) {
                thirdMarketPreResultDTO.setAddition1(specifierValue);
                HomeAwayScore<Integer> intervalScore = getIntervalHandicapScore(marketName, scoreSummary, marketId);
                Integer homeScore = intervalScore.getHomeScore();
                Integer awayScore = intervalScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    String globalHandicap = (80002 == marketId) ? specifierValue : new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                    thirdMarketPreResultDTO.setAddition2(globalHandicap);
                    thirdMarketPreResultDTO.setAddition3(homeScore.toString());
                    thirdMarketPreResultDTO.setAddition4(awayScore.toString());
                } else {
                    thirdMarketPreResultDTO.setAddition2(thirdMarketPreResultDTO.getAddition1());
                }
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                thirdMarketPreResultDTO.setAddition5(intervalArr[0] + PUNCTUATION_COMMA + intervalArr[1]);

            } else if (quarter1List.contains(marketId)) {
                if (11061 == marketId) {
                    thirdMarketPreResultDTO.setAddition1(String.valueOf(1));
                    thirdMarketPreResultDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketPreResultDTO.setAddition1(specifierValue);
                    thirdMarketPreResultDTO.setAddition2(String.valueOf(1));
                }

            } else if (quarter2List.contains(marketId)) {
                if (11062 == marketId) {
                    thirdMarketPreResultDTO.setAddition1(String.valueOf(2));
                    thirdMarketPreResultDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketPreResultDTO.setAddition1(specifierValue);
                    thirdMarketPreResultDTO.setAddition2(String.valueOf(2));
                }

            } else if (quarter3List.contains(marketId)) {
                if (11063 == marketId) {
                    thirdMarketPreResultDTO.setAddition1(String.valueOf(3));
                    thirdMarketPreResultDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketPreResultDTO.setAddition1(specifierValue);
                    thirdMarketPreResultDTO.setAddition2(String.valueOf(3));
                }

            } else if (quarter4List.contains(marketId)) {
                if (11064 == marketId) {
                    thirdMarketPreResultDTO.setAddition1(String.valueOf(4));
                    thirdMarketPreResultDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketPreResultDTO.setAddition1(specifierValue);
                    thirdMarketPreResultDTO.setAddition2(String.valueOf(4));
                }

            } else if (handicapAdd2List.contains(marketId)) {
                thirdMarketPreResultDTO.setAddition1(specifierValue);
                thirdMarketPreResultDTO.setAddition2(specifierValue);

            } else {
                thirdMarketPreResultDTO.setAddition1(specifierValue);
            }
        }
    }

    /**
     * 构建三方提前结算投注项投递对象
     *
     * @param betPieceEntity      自定义投注项实体
     * @param aoMatchMarketInfo   自定义赛事盘口实体
     * @param thirdMarketSourceId 三方盘口源Id
     * @return 三方提前结算投注项投递对象
     */
    private ThirdMarketOddsPreResultDTO buildCashOutOdds(BetPieceEntity betPieceEntity, AoMatchMarketInfo aoMatchMarketInfo, String thirdMarketSourceId) {
        ThirdMarketOddsPreResultDTO thirdMarketOddsPreResultDTO = new ThirdMarketOddsPreResultDTO();
        //三方投注项源Id
        String betPriceId = betPieceEntity.getBetPriceId();
        String thirdOddsFieldSourceId = thirdMarketSourceId + PUNCTUATION_UNDERLINE + betPriceId;
        thirdMarketOddsPreResultDTO.setThirdOddsFieldSourceId(thirdOddsFieldSourceId);
        //概率
        thirdMarketOddsPreResultDTO.setProbabilities(BigDecimal.valueOf(betPieceEntity.getProbabilities()));
        //计算概率
        if (Objects.nonNull(betPieceEntity.getWinProb())) {
            thirdMarketOddsPreResultDTO.setCalcProbability(BigDecimal.valueOf(betPieceEntity.getWinProb()));
        }

        //设置附加信息、投注项类型
        Integer marketId = Integer.valueOf(thirdMarketSourceId.split(PUNCTUATION_UNDERLINE)[1].trim());
        //无投注项模板处理
        if (MarketCategory.MARKET_WITH_OUT_TEMPLATE_LIST.contains(marketId)) {
            setAdditionWithoutTemplate(thirdMarketOddsPreResultDTO, betPieceEntity, aoMatchMarketInfo, marketId);
            //有投注项模板处理
        } else {
            setAdditionWithTemplate(thirdMarketOddsPreResultDTO, betPieceEntity, aoMatchMarketInfo, marketId);
        }
        return thirdMarketOddsPreResultDTO;
    }

    /**
     * 构建三方提前结算投注项投递对象（新）
     *
     * @param outcomeProbabilityEntity 自定义提前结算投注项实体
     * @param aoMatchMarketInfo        自定义赛事盘口实体
     * @param thirdMarketSourceId      三方盘口源Id
     * @return 三方提前结算投注项投递对象
     */
    private ThirdMarketOddsPreResultDTO buildCashOutOddsNew(OutcomeProbabilityEntity outcomeProbabilityEntity, AoMatchMarketInfo<MarketProbabilityEntity> aoMatchMarketInfo, String thirdMarketSourceId) {
        ThirdMarketOddsPreResultDTO thirdMarketOddsPreResultDTO = new ThirdMarketOddsPreResultDTO();
        //三方投注项源Id
        String outcomeId = outcomeProbabilityEntity.getBetPriceId();
        String thirdOddsFieldSourceId = thirdMarketSourceId + PUNCTUATION_UNDERLINE + outcomeId;
        thirdMarketOddsPreResultDTO.setThirdOddsFieldSourceId(thirdOddsFieldSourceId);
        //概率
        thirdMarketOddsPreResultDTO.setProbabilities(BigDecimal.valueOf(outcomeProbabilityEntity.getProbabilities()));
        //计算概率
        if (Objects.nonNull(outcomeProbabilityEntity.getWinProb())) {
            thirdMarketOddsPreResultDTO.setCalcProbability(BigDecimal.valueOf(outcomeProbabilityEntity.getWinProb()));
        }

        //设置附加信息、投注项类型
        Integer marketId = Integer.valueOf(thirdMarketSourceId.split(PUNCTUATION_UNDERLINE)[1].trim());
        //无投注项模板处理
        if (MarketCategory.MARKET_WITH_OUT_TEMPLATE_LIST.contains(marketId)) {
            setAdditionWithoutTemplateNew(thirdMarketOddsPreResultDTO, outcomeProbabilityEntity, aoMatchMarketInfo, marketId);
            //有投注项模板处理
        } else {
            setAdditionWithTemplateNew(thirdMarketOddsPreResultDTO, outcomeProbabilityEntity, aoMatchMarketInfo, marketId);
        }
        return thirdMarketOddsPreResultDTO;
    }

    /**
     * 波胆类玩法
     */
    private static final List<Integer> NONE_TEMP_CORRECT_SCORE_LIST = Arrays.asList(10008, 20008, 60006, 70005, 80005, 10050, 10065, 20023, 60017, 40027);
    /**
     * 净胜分类玩法
     */
    private static final List<Integer> NONE_TEMP_WIN_MARGIN_LIST = Arrays.asList(10009, 10012, 80006, 20022, 60024);
    /**
     * 进球区间类玩法
     */
    private static final List<Integer> NONE_TEMP_INTERVAL_LIST = Arrays.asList(10016, 40014);
    /**
     * 默认主队
     */
    private static final List<Integer> NONE_TEMP_HOME_LIST = Arrays.asList(10017, 20015, 40015);
    /**
     * 默认客队
     */
    private static final List<Integer> NONE_TEMP_AWAY_LIST = Arrays.asList(10018, 20016, 40016);
    /**
     * 半/全场比分玩法
     */
    private static final List<Integer> NONE_TEMP_HT_FT_SCORE_LIST = Arrays.asList(10020);
    /**
     * 半/全场 & 准确进球数 玩法
     */
    private static final List<Integer> NoneTempHtFtAndExactGoalList = Arrays.asList(10062);
    /**
     * 篮球净胜分玩法
     */
    private final List<Integer> NoneTempBasketWinMarginList = Arrays.asList(11043, 11045, 11046, 11047, 11048, 11051);

    /**
     * 设置不存在模板三方提前结算投注项的附加信息
     *
     * @param thirdMarketOddsPreResultDTO 三方提前结算投注项对象
     * @param betPieceEntity              自定义投注项实体
     * @param aoMatchMarketInfo           自定义赛事盘口实体
     * @param marketId                    玩法Id
     */
    private void setAdditionWithoutTemplate(ThirdMarketOddsPreResultDTO thirdMarketOddsPreResultDTO, BetPieceEntity betPieceEntity, AoMatchMarketInfo aoMatchMarketInfo, Integer marketId) {
        //主客队Id
        String homeId = aoMatchMarketInfo.getHomeTeamId();
        String awayId = aoMatchMarketInfo.getAwayTeamId();

        //投注项名称
        String betPieceEntityName = betPieceEntity.getName();
        //投注项类型
        String oddsType;
        if (NONE_TEMP_CORRECT_SCORE_LIST.contains(marketId)) {
            oddsType = betPieceEntityName.replace(PUNCTUATION_HYPHEN, PUNCTUATION_COLON).replace(OTHER_LOWER, OTHER);
            thirdMarketOddsPreResultDTO.setOddsType(oddsType);
            if (!OTHER.equals(oddsType) && 10050 != marketId) {
                thirdMarketOddsPreResultDTO.setAddition1(oddsType.split(PUNCTUATION_COLON)[0].trim());
                thirdMarketOddsPreResultDTO.setAddition2(oddsType.split(PUNCTUATION_COLON)[1].trim());
            }
        } else if (NONE_TEMP_WIN_MARGIN_LIST.contains(marketId)) {
            if (betPieceEntityName.contains(COMPETITOR_ONE)) {
                oddsType = NUMBER_ONE + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
                thirdMarketOddsPreResultDTO.setAddition1(homeId);
            } else if (betPieceEntityName.contains(COMPETITOR_TWO)) {
                oddsType = NUMBER_TWO + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
                thirdMarketOddsPreResultDTO.setAddition1(awayId);
            } else {
                thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_ZERO));
                if (DRAW.getName().equals(betPieceEntityName)) {
                    oddsType = X_UPPER;
                } else if (NO_GOAL.getName().equals(betPieceEntityName)) {
                    oddsType = X0_UPPER;
                } else if (SCORE_DRAW.getName().equals(betPieceEntityName)) {
                    oddsType = X1_UPPER;
                } else {
                    throw new IllegalArgumentException("净胜分玩法【" + marketId + "】投注项【" + betPieceEntity.getBetPriceId() + "】名称【" + betPieceEntityName + "】匹配出错！！！");
                }
            }
            thirdMarketOddsPreResultDTO.setOddsType(oddsType);
        } else if (NONE_TEMP_INTERVAL_LIST.contains(marketId)) {
            String[] nameArr = betPieceEntityName.split(PUNCTUATION_HYPHEN);
            thirdMarketOddsPreResultDTO.setOddsType(betPieceEntityName);
            thirdMarketOddsPreResultDTO.setAddition1(nameArr[0]);
            if (nameArr.length > 1) {
                thirdMarketOddsPreResultDTO.setAddition2(nameArr[1]);
            }
        } else if (NONE_TEMP_HOME_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setOddsType(betPieceEntityName);
            thirdMarketOddsPreResultDTO.setAddition1(homeId);
        } else if (NONE_TEMP_AWAY_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setOddsType(betPieceEntityName);
            thirdMarketOddsPreResultDTO.setAddition1(awayId);
        } else if (NONE_TEMP_HT_FT_SCORE_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setOddsType(betPieceEntityName.replace(PUNCTUATION_HYPHEN, PUNCTUATION_COLON));
            String[] nameArr = betPieceEntityName.split(PUNCTUATION_SPACE);
            //半场比分
            if (nameArr[0].contains(PUNCTUATION_PLUS)) {
                thirdMarketOddsPreResultDTO.setAddition1(nameArr[0]);
                thirdMarketOddsPreResultDTO.setAddition2(nameArr[0]);
            } else {
                String[] htScoreArr = nameArr[0].split(PUNCTUATION_HYPHEN);
                thirdMarketOddsPreResultDTO.setAddition1(htScoreArr[0]);
                thirdMarketOddsPreResultDTO.setAddition2(htScoreArr[1]);
            }
            //全场比分
            if (nameArr[1].contains(PUNCTUATION_PLUS)) {
                thirdMarketOddsPreResultDTO.setAddition3(nameArr[1]);
                thirdMarketOddsPreResultDTO.setAddition4(nameArr[1]);
            } else {
                String[] ftScoreArr = nameArr[1].split(PUNCTUATION_HYPHEN);
                thirdMarketOddsPreResultDTO.setAddition3(ftScoreArr[0]);
                thirdMarketOddsPreResultDTO.setAddition4(ftScoreArr[1]);
            }
        } else if (NoneTempHtFtAndExactGoalList.contains(marketId)) {
            oddsType = betPieceEntityName.replace(COMPETITOR_ONE_WITH_BRACE, String.valueOf(NUMBER_ONE)).replace(COMPETITOR_TWO_WITH_BRACE, String.valueOf(NUMBER_TWO)).replace(DRAW.getName(), X_UPPER)
                    .replace(PUNCTUATION_AMPERSAND, AND).replace(PUNCTUATION_SPACE, "");
            thirdMarketOddsPreResultDTO.setOddsType(oddsType);
        } else if (NoneTempBasketWinMarginList.contains(marketId)) {
            if (betPieceEntityName.contains(COMPETITOR_ONE)) {
                String[] scoreArr = betPieceEntityName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_ONE));
                    thirdMarketOddsPreResultDTO.setAddition2(scoreArr[0]);
                    thirdMarketOddsPreResultDTO.setAddition3(scoreArr[1]);
                    thirdMarketOddsPreResultDTO.setAddition4(homeId);
                } else {
                    if (11043 == marketId) {
                        thirdMarketOddsPreResultDTO.setAddition1(homeId);
                    } else {
                        thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_ONE));
                        thirdMarketOddsPreResultDTO.setAddition2(scoreArr[0]);
                        thirdMarketOddsPreResultDTO.setAddition4(homeId);
                    }
                }
                oddsType = NUMBER_ONE + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
            } else if (betPieceEntityName.contains(COMPETITOR_TWO)) {
                String[] scoreArr = betPieceEntityName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_TWO));
                    thirdMarketOddsPreResultDTO.setAddition2(scoreArr[0]);
                    thirdMarketOddsPreResultDTO.setAddition3(scoreArr[1]);
                    thirdMarketOddsPreResultDTO.setAddition4(awayId);
                } else {
                    if (11043 == marketId) {
                        thirdMarketOddsPreResultDTO.setAddition1(awayId);
                    } else {
                        thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_TWO));
                        thirdMarketOddsPreResultDTO.setAddition2(scoreArr[0]);
                        thirdMarketOddsPreResultDTO.setAddition4(awayId);
                    }
                }
                oddsType = NUMBER_TWO + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
            } else if (betPieceEntityName.contains(BY_LOWER)) {
                String[] scoreArr = betPieceEntityName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    thirdMarketOddsPreResultDTO.setAddition1(scoreArr[0]);
                    thirdMarketOddsPreResultDTO.setAddition2(scoreArr[1]);
                } else {
                    thirdMarketOddsPreResultDTO.setAddition1(scoreArr[0]);
                }
                oddsType = betPieceEntityName.split(BY_LOWER)[1].trim();
            } else {
                if (BASKET_OTHER.getName().equals(betPieceEntityName)) {
                    thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_ZERO));
                    oddsType = OTHER;
                } else if (BASKET_DRAW.getName().equals(betPieceEntityName)) {
                    thirdMarketOddsPreResultDTO.setAddition4(String.valueOf(NUMBER_ZERO));
                    oddsType = X_UPPER;
                } else {
                    throw new IllegalArgumentException("净胜分玩法【" + marketId + "】投注项【" + betPieceEntity.getBetPriceId() + "】名称【" + betPieceEntityName + "】匹配出错！！！");
                }
            }
            thirdMarketOddsPreResultDTO.setOddsType(oddsType);
        } else {
            thirdMarketOddsPreResultDTO.setOddsType(betPieceEntityName);
        }
    }

    /**
     * 设置不存在模板三方提前结算投注项的附加信息（新）
     *
     * @param thirdMarketOddsPreResultDTO 三方提前结算投注项对象
     * @param outcomeProbabilityEntity    自定义提前结算投注项实体
     * @param aoMatchMarketInfo           自定义赛事盘口实体
     * @param marketId                    玩法Id
     */
    private void setAdditionWithoutTemplateNew(ThirdMarketOddsPreResultDTO thirdMarketOddsPreResultDTO, OutcomeProbabilityEntity outcomeProbabilityEntity, AoMatchMarketInfo<MarketProbabilityEntity> aoMatchMarketInfo, Integer marketId) {
        //主客队Id
        String homeId = aoMatchMarketInfo.getHomeTeamId();
        String awayId = aoMatchMarketInfo.getAwayTeamId();

        //投注项名称
        String outcomeName = outcomeProbabilityEntity.getName();
        //投注项类型
        String oddsType;
        if (NONE_TEMP_CORRECT_SCORE_LIST.contains(marketId)) {
            oddsType = outcomeName.replace(PUNCTUATION_HYPHEN, PUNCTUATION_COLON).replace(OTHER_LOWER, OTHER);
            thirdMarketOddsPreResultDTO.setOddsType(oddsType);
            if (!OTHER.equals(oddsType) && 10050 != marketId) {
                thirdMarketOddsPreResultDTO.setAddition1(oddsType.split(PUNCTUATION_COLON)[0].trim());
                thirdMarketOddsPreResultDTO.setAddition2(oddsType.split(PUNCTUATION_COLON)[1].trim());
            }
        } else if (NONE_TEMP_WIN_MARGIN_LIST.contains(marketId)) {
            if (outcomeName.contains(COMPETITOR_ONE)) {
                oddsType = NUMBER_ONE + AND + outcomeName.split(BY_LOWER)[1].trim();
                thirdMarketOddsPreResultDTO.setAddition1(homeId);
            } else if (outcomeName.contains(COMPETITOR_TWO)) {
                oddsType = NUMBER_TWO + AND + outcomeName.split(BY_LOWER)[1].trim();
                thirdMarketOddsPreResultDTO.setAddition1(awayId);
            } else {
                thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_ZERO));
                if (DRAW.getName().equals(outcomeName)) {
                    oddsType = X_UPPER;
                } else if (NO_GOAL.getName().equals(outcomeName)) {
                    oddsType = X0_UPPER;
                } else if (SCORE_DRAW.getName().equals(outcomeName)) {
                    oddsType = X1_UPPER;
                } else {
                    throw new IllegalArgumentException("净胜分玩法【" + marketId + "】投注项【" + outcomeProbabilityEntity.getBetPriceId() + "】名称【" + outcomeName + "】匹配出错！！！");
                }
            }
            thirdMarketOddsPreResultDTO.setOddsType(oddsType);
        } else if (NONE_TEMP_INTERVAL_LIST.contains(marketId)) {
            String[] nameArr = outcomeName.split(PUNCTUATION_HYPHEN);
            thirdMarketOddsPreResultDTO.setOddsType(outcomeName);
            thirdMarketOddsPreResultDTO.setAddition1(nameArr[0]);
            if (nameArr.length > 1) {
                thirdMarketOddsPreResultDTO.setAddition2(nameArr[1]);
            }
        } else if (NONE_TEMP_HOME_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setOddsType(outcomeName);
            thirdMarketOddsPreResultDTO.setAddition1(homeId);
        } else if (NONE_TEMP_AWAY_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setOddsType(outcomeName);
            thirdMarketOddsPreResultDTO.setAddition1(awayId);
        } else if (NONE_TEMP_HT_FT_SCORE_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setOddsType(outcomeName.replace(PUNCTUATION_HYPHEN, PUNCTUATION_COLON));
            String[] nameArr = outcomeName.split(PUNCTUATION_SPACE);
            //半场比分
            if (nameArr[0].contains(PUNCTUATION_PLUS)) {
                thirdMarketOddsPreResultDTO.setAddition1(nameArr[0]);
                thirdMarketOddsPreResultDTO.setAddition2(nameArr[0]);
            } else {
                String[] htScoreArr = nameArr[0].split(PUNCTUATION_HYPHEN);
                thirdMarketOddsPreResultDTO.setAddition1(htScoreArr[0]);
                thirdMarketOddsPreResultDTO.setAddition2(htScoreArr[1]);
            }
            //全场比分
            if (nameArr[1].contains(PUNCTUATION_PLUS)) {
                thirdMarketOddsPreResultDTO.setAddition3(nameArr[1]);
                thirdMarketOddsPreResultDTO.setAddition4(nameArr[1]);
            } else {
                String[] ftScoreArr = nameArr[1].split(PUNCTUATION_HYPHEN);
                thirdMarketOddsPreResultDTO.setAddition3(ftScoreArr[0]);
                thirdMarketOddsPreResultDTO.setAddition4(ftScoreArr[1]);
            }
        } else if (NoneTempHtFtAndExactGoalList.contains(marketId)) {
            oddsType = outcomeName.replace(COMPETITOR_ONE_WITH_BRACE, String.valueOf(NUMBER_ONE)).replace(COMPETITOR_TWO_WITH_BRACE, String.valueOf(NUMBER_TWO)).replace(DRAW.getName(), X_UPPER)
                    .replace(PUNCTUATION_AMPERSAND, AND).replace(PUNCTUATION_SPACE, "");
            thirdMarketOddsPreResultDTO.setOddsType(oddsType);
        } else if (NoneTempBasketWinMarginList.contains(marketId)) {
            if (outcomeName.contains(COMPETITOR_ONE)) {
                String[] scoreArr = outcomeName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_ONE));
                    thirdMarketOddsPreResultDTO.setAddition2(scoreArr[0]);
                    thirdMarketOddsPreResultDTO.setAddition3(scoreArr[1]);
                    thirdMarketOddsPreResultDTO.setAddition4(homeId);
                } else {
                    if (11043 == marketId) {
                        thirdMarketOddsPreResultDTO.setAddition1(homeId);
                    } else {
                        thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_ONE));
                        thirdMarketOddsPreResultDTO.setAddition2(scoreArr[0]);
                        thirdMarketOddsPreResultDTO.setAddition4(homeId);
                    }
                }
                oddsType = NUMBER_ONE + AND + outcomeName.split(BY_LOWER)[1].trim();
            } else if (outcomeName.contains(COMPETITOR_TWO)) {
                String[] scoreArr = outcomeName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_TWO));
                    thirdMarketOddsPreResultDTO.setAddition2(scoreArr[0]);
                    thirdMarketOddsPreResultDTO.setAddition3(scoreArr[1]);
                    thirdMarketOddsPreResultDTO.setAddition4(awayId);
                } else {
                    if (11043 == marketId) {
                        thirdMarketOddsPreResultDTO.setAddition1(awayId);
                    } else {
                        thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_TWO));
                        thirdMarketOddsPreResultDTO.setAddition2(scoreArr[0]);
                        thirdMarketOddsPreResultDTO.setAddition4(awayId);
                    }
                }
                oddsType = NUMBER_TWO + AND + outcomeName.split(BY_LOWER)[1].trim();
            } else if (outcomeName.contains(BY_LOWER)) {
                String[] scoreArr = outcomeName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    thirdMarketOddsPreResultDTO.setAddition1(scoreArr[0]);
                    thirdMarketOddsPreResultDTO.setAddition2(scoreArr[1]);
                } else {
                    thirdMarketOddsPreResultDTO.setAddition1(scoreArr[0]);
                }
                oddsType = outcomeName.split(BY_LOWER)[1].trim();
            } else {
                if (BASKET_OTHER.getName().equals(outcomeName)) {
                    thirdMarketOddsPreResultDTO.setAddition1(String.valueOf(NUMBER_ZERO));
                    oddsType = OTHER;
                } else if (BASKET_DRAW.getName().equals(outcomeName)) {
                    thirdMarketOddsPreResultDTO.setAddition4(String.valueOf(NUMBER_ZERO));
                    oddsType = X_UPPER;
                } else {
                    throw new IllegalArgumentException("净胜分玩法【" + marketId + "】投注项【" + outcomeProbabilityEntity.getBetPriceId() + "】名称【" + outcomeName + "】匹配出错！！！");
                }
            }
            thirdMarketOddsPreResultDTO.setOddsType(oddsType);
        } else {
            thirdMarketOddsPreResultDTO.setOddsType(outcomeName);
        }
    }

    /**
     * 默认主队投注项
     */
    private static final List<Integer> TEMP_HOME_LIST = Arrays.asList(10006, 20006, 10023, 10024, 10025, 10026, 10027, 40005, 40012, 10042, 10043, 20019, 60011, 60012, 50010, 50013, 50016, 50018, 50021, 20026, 60022);
    /**
     * 默认客队投注项
     */
    private static final List<Integer> TEMP_AWAY_LIST = Arrays.asList(10007, 20007, 10028, 10029, 10030, 10031, 10032, 40006, 40013, 10045, 10046, 20020, 60013, 60014, 50011, 50014, 50017, 50019, 50022, 20027, 60023);

    /**
     * 设置存在模板三方提前结算投注项的附加信息
     *
     * @param thirdMarketOddsPreResultDTO 三方提前结算投注项对象
     * @param betPieceEntity              自定义投注项实体
     * @param aoMatchMarketInfo           自定义赛事盘口实体
     * @param marketId                    玩法Id
     */
    private void setAdditionWithTemplate(ThirdMarketOddsPreResultDTO thirdMarketOddsPreResultDTO, BetPieceEntity betPieceEntity, AoMatchMarketInfo aoMatchMarketInfo, Integer marketId) {
        //主客队Id
        String homeId = aoMatchMarketInfo.getHomeTeamId();
        String awayId = aoMatchMarketInfo.getAwayTeamId();

        //投注项Id
        String betPriceId = betPieceEntity.getBetPriceId();
        SelectionTemplate selectionTemplate = SelectionTemplate.getSelectionTemplateById(betPriceId);
        //设置投注项类型
        thirdMarketOddsPreResultDTO.setOddsType(selectionTemplate.getOddsType());

        //设置附加字段
        if (TEMP_HOME_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setAddition1(homeId);
        } else if (TEMP_AWAY_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setAddition1(awayId);
        } else {
            if (COMPETITOR_ONE.equals(selectionTemplate.getAddition1())) {
                thirdMarketOddsPreResultDTO.setAddition1(homeId);
            } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition1())) {
                thirdMarketOddsPreResultDTO.setAddition1(awayId);
            } else {
                thirdMarketOddsPreResultDTO.setAddition1(selectionTemplate.getAddition1());
            }
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition2())) {
            thirdMarketOddsPreResultDTO.setAddition2(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition2())) {
            thirdMarketOddsPreResultDTO.setAddition2(awayId);
        } else {
            thirdMarketOddsPreResultDTO.setAddition2(selectionTemplate.getAddition2());
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition3())) {
            thirdMarketOddsPreResultDTO.setAddition3(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition3())) {
            thirdMarketOddsPreResultDTO.setAddition3(awayId);
        } else {
            thirdMarketOddsPreResultDTO.setAddition3(selectionTemplate.getAddition3());
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition4())) {
            thirdMarketOddsPreResultDTO.setAddition4(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition4())) {
            thirdMarketOddsPreResultDTO.setAddition4(awayId);
        } else {
            thirdMarketOddsPreResultDTO.setAddition4(selectionTemplate.getAddition4());
        }

        thirdMarketOddsPreResultDTO.setAddition5(selectionTemplate.getAddition5());
    }

    /**
     * 设置存在模板三方提前结算投注项的附加信息（新）
     *
     * @param thirdMarketOddsPreResultDTO 三方提前结算投注项对象
     * @param outcomeProbabilityEntity    自定义提前结算投注项实体
     * @param aoMatchMarketInfo           自定义赛事盘口实体
     * @param marketId                    玩法Id
     */
    private void setAdditionWithTemplateNew(ThirdMarketOddsPreResultDTO thirdMarketOddsPreResultDTO, OutcomeProbabilityEntity outcomeProbabilityEntity, AoMatchMarketInfo<MarketProbabilityEntity> aoMatchMarketInfo, Integer marketId) {
        //主客队Id
        String homeId = aoMatchMarketInfo.getHomeTeamId();
        String awayId = aoMatchMarketInfo.getAwayTeamId();

        //投注项Id
        String betPriceId = outcomeProbabilityEntity.getBetPriceId();
        SelectionTemplate selectionTemplate = SelectionTemplate.getSelectionTemplateById(betPriceId);
        //设置投注项类型
        thirdMarketOddsPreResultDTO.setOddsType(selectionTemplate.getOddsType());

        //设置附加字段
        if (TEMP_HOME_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setAddition1(homeId);
        } else if (TEMP_AWAY_LIST.contains(marketId)) {
            thirdMarketOddsPreResultDTO.setAddition1(awayId);
        } else {
            if (COMPETITOR_ONE.equals(selectionTemplate.getAddition1())) {
                thirdMarketOddsPreResultDTO.setAddition1(homeId);
            } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition1())) {
                thirdMarketOddsPreResultDTO.setAddition1(awayId);
            } else {
                thirdMarketOddsPreResultDTO.setAddition1(selectionTemplate.getAddition1());
            }
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition2())) {
            thirdMarketOddsPreResultDTO.setAddition2(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition2())) {
            thirdMarketOddsPreResultDTO.setAddition2(awayId);
        } else {
            thirdMarketOddsPreResultDTO.setAddition2(selectionTemplate.getAddition2());
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition3())) {
            thirdMarketOddsPreResultDTO.setAddition3(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition3())) {
            thirdMarketOddsPreResultDTO.setAddition3(awayId);
        } else {
            thirdMarketOddsPreResultDTO.setAddition3(selectionTemplate.getAddition3());
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition4())) {
            thirdMarketOddsPreResultDTO.setAddition4(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition4())) {
            thirdMarketOddsPreResultDTO.setAddition4(awayId);
        } else {
            thirdMarketOddsPreResultDTO.setAddition4(selectionTemplate.getAddition4());
        }

        thirdMarketOddsPreResultDTO.setAddition5(selectionTemplate.getAddition5());
    }
}
