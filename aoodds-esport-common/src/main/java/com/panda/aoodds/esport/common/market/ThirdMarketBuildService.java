package com.panda.aoodds.esport.common.market;

import cn.hutool.core.util.StrUtil;
import com.panda.aoodds.esport.api.entity.BetPieceEntity;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.AoMatchMarketInfo;
import com.panda.aoodds.esport.common.entity.HomeAwayScore;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.enums.ScoreType;
import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMarketOddsDTO;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.virtual.dto.VirtualMarketDTO;
import com.panda.virtual.dto.VirtualMarketOddsDTO;
import com.panda.virtual.dto.VirtualOddsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.constant.CommonConstant.*;
import static com.panda.aoodds.esport.common.enums.ScoreType.*;
import static com.panda.aoodds.esport.common.market.MarketSelection.*;

/**
 * 盘口数据处理器
 *
 * @author Samuel
 */
@Service
@Slf4j
public class ThirdMarketBuildService {

    /**
     * 构建三方赛事盘口投递对象
     *
     * @param aoMatchMarketInfo 自定义赛事盘口对象
     * @param linkId            链路Id
     * @return 三方赛事盘口投递对象
     */
    public ThirdMatchMarketDTO processAoMatchMarket(AoMatchMarketInfo<MarketsEntity> aoMatchMarketInfo, String linkId) {
        ThirdMatchMarketDTO thirdMatchMarketDTO = new ThirdMatchMarketDTO();
        //数据源编码
        thirdMatchMarketDTO.setDataSourceCode(aoMatchMarketInfo.getDataSourceCode());
        //运动类型Id
        thirdMatchMarketDTO.setSportId(aoMatchMarketInfo.getSportId());
        //AO赛事Id
        thirdMatchMarketDTO.setThirdMatchSourceId(aoMatchMarketInfo.getMatchSourceId());
        //三方盘口集合
        long nowTime = aoMatchMarketInfo.getModifyTime();
        List<ThirdMarketDTO> thirdMarketDTOList = aoMatchMarketInfo.getMarketList().stream().map(marketsEntity -> buildThirdMarket(marketsEntity, aoMatchMarketInfo, nowTime, linkId)).filter(Objects::nonNull).collect(Collectors.toList());
        thirdMatchMarketDTO.setMarketList(thirdMarketDTOList);
        //修改时间戳
        thirdMatchMarketDTO.setModifyTime(nowTime);
        log.info("::{}::构建三方赛事盘口成功", linkId);
        return thirdMatchMarketDTO;
    }

    /**
     * 构建三方电子赛事盘口投递对象
     *
     * @param aoMatchMarketInfo 自定义赛事盘口对象
     * @param linkId            链路Id
     * @return 三方电子赛事盘口投递对象
     */
    public VirtualMarketOddsDTO processAoEsportMatchMarket(AoMatchMarketInfo<MarketsEntity> aoMatchMarketInfo, String linkId) {
        VirtualMarketOddsDTO virtualMarketOddsDTO = new VirtualMarketOddsDTO();
        //数据源编码
        virtualMarketOddsDTO.setDataSourceCode(aoMatchMarketInfo.getDataSourceCode());
        //运动类型Id
        virtualMarketOddsDTO.setSportId(aoMatchMarketInfo.getSportId());
        //AO赛事Id
        virtualMarketOddsDTO.setThirdMatchSourceId(aoMatchMarketInfo.getMatchSourceId());
        //三方盘口集合
        long nowTime = aoMatchMarketInfo.getModifyTime();
        List<VirtualMarketDTO> virtualMarketDTOList = aoMatchMarketInfo.getMarketList().stream().map(marketsEntity -> buildVirtualMarketDTO(marketsEntity, aoMatchMarketInfo, nowTime, linkId)).filter(Objects::nonNull).collect(Collectors.toList());
        virtualMarketOddsDTO.setMarketList(virtualMarketDTOList);
        //修改时间戳
        virtualMarketOddsDTO.setModifyTime(nowTime);
        log.info("::{}::构建三方赛事盘口成功", linkId);
        return virtualMarketOddsDTO;
    }

    /**
     * 15分钟玩法集合
     */
    private final List<Integer> fifteenMarketList = Arrays.asList(30001, 30002, 30003, 40022, 40023, 40024, 50029, 50030, 50031);

    /**
     * 构建三方盘口投递对象
     *
     * @param marketsEntity     自定义盘口实体
     * @param aoMatchMarketInfo 自定义赛事盘口对象
     * @param linkId            链路Id
     * @param nowTime           下发时间
     * @return 三方盘口对象
     */
    private ThirdMarketDTO buildThirdMarket(MarketsEntity marketsEntity, AoMatchMarketInfo aoMatchMarketInfo, Long nowTime, String linkId) {
        ThirdMarketDTO thirdMarketDTO = new ThirdMarketDTO();
        try {
            //兜底关盘时间必须小于，正常开盘的时间，融合是获取坑位最新时间，相同时间存在 开 关 盘口，可能会取到关盘口
            if (Objects.isNull(thirdMarketDTO.getModifyTime())) {
                thirdMarketDTO.setModifyTime(nowTime);
            }
            Map<ScoreType, HomeAwayScore<Integer>> scoreSummary = aoMatchMarketInfo.getScoreSummary();
            //盘口源Id
            Integer marketId = marketsEntity.getMarketId();
            //三方玩法源Id
            String thirdMarketCategorySourceId = AO_SOURCE_CODE + PUNCTUATION_COLON + marketId;
            thirdMarketDTO.setThirdMarketCategorySourceId(thirdMarketCategorySourceId);
            //三方盘口源Id
            String marketName = marketsEntity.getMarketName();
            String specifierValue = StrUtil.isNotBlank(marketsEntity.getHandicap()) ? marketsEntity.getHandicap() : "";
            //盘口Id中基准分盘口值转换成全场盘口值
            if (handicapList.contains(marketId)) {
                HomeAwayScore<Integer> homeAwayScore = getHandicapScore(scoreSummary, marketId);
                Integer homeScore = homeAwayScore.getHomeScore();
                Integer awayScore = homeAwayScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore) && 80002 != marketId) {
                    specifierValue = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                }
            } else if (intervalHandicapList.contains(marketId)) {
                HomeAwayScore<Integer> intervalScore = getIntervalHandicapScore(marketName, scoreSummary, marketId);
                Integer homeScore = intervalScore.getHomeScore();
                Integer awayScore = intervalScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    specifierValue = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                }
            }
            if (fifteenMarketList.contains(marketId)) {
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                specifierValue = specifierValue + PUNCTUATION_UNDERLINE + intervalArr[0] + PUNCTUATION_UNDERLINE + intervalArr[1];
            }
            String matchSourceId = aoMatchMarketInfo.getMatchSourceId();
            String thirdMarketSourceId = matchSourceId + PUNCTUATION_UNDERLINE + marketId + PUNCTUATION_UNDERLINE + specifierValue;
            thirdMarketDTO.setThirdMarketSourceId(thirdMarketSourceId);
            //盘口类型（1：赛前盘；0：滚球盘；2：冠军玩法）
            Integer liveFlag = aoMatchMarketInfo.getLiveFlag();
            if (null == liveFlag) {
                thirdMarketDTO.setMarketType(NUMBER_ONE);
            } else {
                thirdMarketDTO.setMarketType(1 == liveFlag ? NUMBER_ZERO : NUMBER_ONE);
            }
            //数据源编码
            thirdMarketDTO.setDataSourceCode(AO_SOURCE_CODE);
            //盘口状态（0-5; 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver）
            Integer marketStatus = marketsEntity.getStatus();
            thirdMarketDTO.setStatus(Objects.nonNull(marketStatus) ? marketStatus : NUMBER_ZERO);
            //盘口中文名称
            thirdMarketDTO.setOddsName(marketName);

            //设置盘口附加字段
            setMarketAddition(thirdMarketDTO, marketsEntity, scoreSummary);

            //并列-胜出数
            thirdMarketDTO.setNumberOfWinners(NUMBER_ONE);
            //盘口名称国际化
            I18nItemDTO i18nItemDTO = new I18nItemDTO();
            i18nItemDTO.setLanguageType(Locale.ENGLISH.getLanguage());
            i18nItemDTO.setText(marketName);
            thirdMarketDTO.setI18nNames(Collections.singletonList(i18nItemDTO));

            //排序位置
            thirdMarketDTO.setOfferLineId(marketsEntity.getOrder());

            //三方投注项集合
            List<BetPieceEntity> betPieceEntityList = marketsEntity.getBetPieceEntities();
            List<ThirdMarketOddsDTO> thirdMarketOddsDTOList = betPieceEntityList.stream()
                    .sorted(Comparator.comparingInt(BetPieceEntity::getOrder))
                    .map(betPieceEntity -> buildMarketOdds(betPieceEntity, aoMatchMarketInfo, thirdMarketSourceId))
                    .collect(Collectors.toList());
            thirdMarketDTO.setMarketOddsList(thirdMarketOddsDTOList);
        } catch (Exception e) {
            log.error("::" + linkId + "::构建AO三方盘口【" + thirdMarketDTO.getThirdMarketSourceId() + "】赔率数据异常：", e);
            return null;
        }
        return thirdMarketDTO;
    }

    /**
     * 构建三方电子赛事盘口投递对象
     *
     * @param marketsEntity     自定义盘口实体
     * @param aoMatchMarketInfo 自定义赛事盘口对象
     * @param linkId            链路Id
     * @param nowTime           下发时间
     * @return 三方电子赛事盘口对象
     */
    private VirtualMarketDTO buildVirtualMarketDTO(MarketsEntity marketsEntity, AoMatchMarketInfo aoMatchMarketInfo, Long nowTime, String linkId) {
        VirtualMarketDTO virtualMarketDTO = new VirtualMarketDTO();
        try {
            //兜底关盘时间必须小于，正常开盘的时间，融合是获取坑位最新时间，相同时间存在 开 关 盘口，可能会取到关盘口
            if (Objects.isNull(virtualMarketDTO.getModifyTime())) {
                virtualMarketDTO.setModifyTime(nowTime);
            }
            Map<ScoreType, HomeAwayScore<Integer>> scoreSummary = aoMatchMarketInfo.getScoreSummary();
            //盘口源Id
            Integer marketId = marketsEntity.getMarketId();
            //三方玩法源Id
            String thirdMarketCategorySourceId = AO_SOURCE_CODE + PUNCTUATION_COLON + marketId;
            virtualMarketDTO.setThirdMarketCategorySourceId(thirdMarketCategorySourceId);
            //三方盘口源Id
            String marketName = marketsEntity.getMarketName();
            String specifierValue = StrUtil.isNotBlank(marketsEntity.getHandicap()) ? marketsEntity.getHandicap() : "";
            //盘口Id中基准分盘口值转换成全场盘口值
            if (handicapList.contains(marketId)) {
                HomeAwayScore<Integer> homeAwayScore = getHandicapScore(scoreSummary, marketId);
                Integer homeScore = homeAwayScore.getHomeScore();
                Integer awayScore = homeAwayScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore) && 80002 != marketId) {
                    specifierValue = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                }
            } else if (intervalHandicapList.contains(marketId)) {
                HomeAwayScore<Integer> intervalScore = getIntervalHandicapScore(marketName, scoreSummary, marketId);
                Integer homeScore = intervalScore.getHomeScore();
                Integer awayScore = intervalScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    specifierValue = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                }
            }
            if (fifteenMarketList.contains(marketId)) {
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                specifierValue = specifierValue + PUNCTUATION_UNDERLINE + intervalArr[0] + PUNCTUATION_UNDERLINE + intervalArr[1];
            }
            String matchSourceId = aoMatchMarketInfo.getMatchSourceId();
            String thirdMarketSourceId = matchSourceId + PUNCTUATION_UNDERLINE + marketId + PUNCTUATION_UNDERLINE + specifierValue;
            virtualMarketDTO.setThirdMarketSourceId(thirdMarketSourceId);
            //盘口类型（1：赛前盘；0：滚球盘；2：冠军玩法）
            Integer liveFlag = aoMatchMarketInfo.getLiveFlag();
            if (null == liveFlag) {
                virtualMarketDTO.setMarketType(NUMBER_ONE);
            } else {
                virtualMarketDTO.setMarketType(1 == liveFlag ? NUMBER_ZERO : NUMBER_ONE);
            }
            //数据源编码
            virtualMarketDTO.setDataSourceCode(AO_SOURCE_CODE);
            //盘口状态（0-5; 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver）
            Integer marketStatus = marketsEntity.getStatus();
            virtualMarketDTO.setIsClosed(Objects.nonNull(marketStatus) ? (marketStatus.equals(0) ? Boolean.FALSE : Boolean.TRUE) : Boolean.TRUE);
            //盘口中文名称
            virtualMarketDTO.setMarketName(marketName);

            //设置盘口附加字段
            setEsportMarketAddition(virtualMarketDTO, marketsEntity, scoreSummary);

            //盘口名称国际化
            com.panda.virtual.dto.I18nItemDTO i18nItemDTO = new com.panda.virtual.dto.I18nItemDTO();
            i18nItemDTO.setLanguageType(Locale.ENGLISH.getLanguage());
            i18nItemDTO.setText(marketName);
            virtualMarketDTO.setI18nNames(Collections.singletonList(i18nItemDTO));

            //三方投注项集合
            List<BetPieceEntity> betPieceEntityList = marketsEntity.getBetPieceEntities();
            List<VirtualOddsDTO> virtualOddsDTOList = betPieceEntityList.stream()
                    .sorted(Comparator.comparingInt(BetPieceEntity::getOrder))
                    .map(betPieceEntity -> buildEsportMarketOdds(betPieceEntity, aoMatchMarketInfo, thirdMarketSourceId))
                    .collect(Collectors.toList());
            virtualMarketDTO.setMarketOddsList(virtualOddsDTOList);
        } catch (Exception e) {
            log.error("::" + linkId + "::构建AO三方盘口【" + virtualMarketDTO.getThirdMarketSourceId() + "】赔率数据异常：", e);
            return null;
        }
        return virtualMarketDTO;
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
    private final List<Integer> marketAdditionList = Arrays.asList(10004, 10005, 10006, 10007, 10009, 10010, 10011, 10012, 20004, 20005, 20006, 20007, 20009, 20010, 30001, 30002, 30003,
            10013, 10016, 10017, 10018, 10033, 10035, 20011, 40002, 40003, 40005, 40006, 40009, 40010, 40012, 40013, 50002, 50004, 50006, 50008, 60003, 60005, 10039, 10040, 10047, 10048,
            10049, 20015, 20016, 20017, 20018, 60010, 60011, 60013, 60015, 40014, 40015, 40016, 40017, 40018, 40019, 40020, 40021, 40022, 40023, 40024, 40025, 50012, 50013, 50014, 50018, 50019,
            50023, 50024, 50026, 50027, 70002, 70003, 70007, 70009, 70010, 80002, 80003, 80006, 80007, 80008, 80011, 80012, 20021, 60016, 10051, 10052, 10053, 10055, 10061, 10064, 11002, 11003,
            11005, 11006, 11008, 11009, 11011, 11012, 11014, 11015, 11017, 11018, 11020, 11021, 11023, 11024, 11026, 11027, 11029, 11030, 11032, 11033, 11035, 11036, 11038, 11039, 11041, 11042,
            50029, 50030, 50031, 60020, 60021, 11043, 11044, 11045, 11046, 11047, 11048, 11049, 11050, 11051, 11052, 11053, 11054, 11055, 11056, 11057, 11058, 11059, 11061, 11062, 11063, 11064,
            40028, 40030, 40031, 50040, 50041, 50043, 50044, 50046, 40033, 40035, 40036, 40038, 10072, 10079);

    /**
     * 让球类玩法，ad1设置基准分盘口，ad2设置全场盘口，ad3、ad4设置主客比分
     */
    private final List<Integer> handicapList = Arrays.asList(10004, 20004, 40002, 40009, 50002, 50006, 50023, 50026, 60015, 70002, 70009, 80002, 50040, 50043, 40033, 40035);

    /**
     * 区间类玩法，ad2设置区间起始值，ad3设置区间结束值
     */
    private final List<Integer> intervalList = Arrays.asList(30001, 40022, 50029);

    /**
     * 区间盘口值类玩法，ad1设置盘口值，ad2设置区间起始值，ad3设置区间结束值
     */
    private final List<Integer> intervalTotalList = Arrays.asList(30003, 40024, 50031);

    /**
     * 区间让球类玩法，ad1设置基准分盘口，ad2设置全场盘口，ad3、ad4设置区间主客比分，ad5设置逗号隔开的起止区间
     */
    private final List<Integer> intervalHandicapList = Arrays.asList(30002, 40023, 50030);

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
     * 设置盘口附加信息
     *
     * @param thirdMarketDTO 三方盘口投递对象
     * @param marketsEntity  自定义盘口实体
     * @param scoreSummary   赛事比分集合
     */
    private void setMarketAddition(ThirdMarketDTO thirdMarketDTO, MarketsEntity marketsEntity, Map<ScoreType, HomeAwayScore<Integer>> scoreSummary) {
        //盘口源Id
        Integer marketId = marketsEntity.getMarketId();
        String specifierValue = StrUtil.isNotBlank(marketsEntity.getHandicap()) ? marketsEntity.getHandicap() : "";
        //盘口名称
        String marketName = marketsEntity.getMarketName();

        if (marketAdditionList.contains(marketId)) {
            if (handicapList.contains(marketId)) {
                HomeAwayScore<Integer> homeAwayScore = getHandicapScore(scoreSummary, marketId);
                Integer homeScore = homeAwayScore.getHomeScore();
                Integer awayScore = homeAwayScore.getAwayScore();
                thirdMarketDTO.setAddition1(specifierValue);
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    String globalHandicap = (80002 == marketId) ? specifierValue : new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                    thirdMarketDTO.setAddition2(globalHandicap);
                    thirdMarketDTO.setAddition3(homeScore.toString());
                    thirdMarketDTO.setAddition4(awayScore.toString());
                } else {
                    thirdMarketDTO.setAddition2(thirdMarketDTO.getAddition1());
                }

            } else if (intervalList.contains(marketId)) {
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                thirdMarketDTO.setAddition2(intervalArr[0]);
                thirdMarketDTO.setAddition3(intervalArr[1]);

            } else if (intervalTotalList.contains(marketId)) {
                thirdMarketDTO.setAddition1(specifierValue);
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                thirdMarketDTO.setAddition2(intervalArr[0]);
                thirdMarketDTO.setAddition3(intervalArr[1]);

            } else if (intervalHandicapList.contains(marketId)) {
                thirdMarketDTO.setAddition1(specifierValue);
                HomeAwayScore<Integer> intervalScore = getIntervalHandicapScore(marketName, scoreSummary, marketId);
                Integer homeScore = intervalScore.getHomeScore();
                Integer awayScore = intervalScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    String globalHandicap = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                    thirdMarketDTO.setAddition2(globalHandicap);
                    thirdMarketDTO.setAddition3(homeScore.toString());
                    thirdMarketDTO.setAddition4(awayScore.toString());
                } else {
                    thirdMarketDTO.setAddition2(thirdMarketDTO.getAddition1());
                }
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                thirdMarketDTO.setAddition5(intervalArr[0] + PUNCTUATION_COMMA + intervalArr[1]);

            } else if (quarter1List.contains(marketId)) {
                if (11061 == marketId) {
                    thirdMarketDTO.setAddition1(String.valueOf(1));
                    thirdMarketDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketDTO.setAddition1(specifierValue);
                    thirdMarketDTO.setAddition2(String.valueOf(1));
                }

            } else if (quarter2List.contains(marketId)) {
                if (11062 == marketId) {
                    thirdMarketDTO.setAddition1(String.valueOf(2));
                    thirdMarketDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketDTO.setAddition1(specifierValue);
                    thirdMarketDTO.setAddition2(String.valueOf(2));
                }

            } else if (quarter3List.contains(marketId)) {
                if (11063 == marketId) {
                    thirdMarketDTO.setAddition1(String.valueOf(3));
                    thirdMarketDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketDTO.setAddition1(specifierValue);
                    thirdMarketDTO.setAddition2(String.valueOf(3));
                }

            } else if (quarter4List.contains(marketId)) {
                if (11064 == marketId) {
                    thirdMarketDTO.setAddition1(String.valueOf(4));
                    thirdMarketDTO.setAddition2(specifierValue);
                } else {
                    thirdMarketDTO.setAddition1(specifierValue);
                    thirdMarketDTO.setAddition2(String.valueOf(4));
                }

            } else if (handicapAdd2List.contains(marketId)) {
                thirdMarketDTO.setAddition1(specifierValue);
                thirdMarketDTO.setAddition2(specifierValue);

            } else {
                thirdMarketDTO.setAddition1(specifierValue);
            }
        }
    }

    /**
     * 设置电子赛事盘口附加信息
     *
     * @param virtualMarketDTO 三方电子赛事盘口投递对象
     * @param marketsEntity    自定义盘口实体
     * @param scoreSummary     赛事比分集合
     */
    private void setEsportMarketAddition(VirtualMarketDTO virtualMarketDTO, MarketsEntity marketsEntity, Map<ScoreType, HomeAwayScore<Integer>> scoreSummary) {
        //盘口源Id
        Integer marketId = marketsEntity.getMarketId();
        String specifierValue = StrUtil.isNotBlank(marketsEntity.getHandicap()) ? marketsEntity.getHandicap() : "";
        //盘口名称
        String marketName = marketsEntity.getMarketName();

        if (marketAdditionList.contains(marketId)) {
            if (handicapList.contains(marketId)) {
                HomeAwayScore<Integer> homeAwayScore = getHandicapScore(scoreSummary, marketId);
                Integer homeScore = homeAwayScore.getHomeScore();
                Integer awayScore = homeAwayScore.getAwayScore();
                virtualMarketDTO.setAddition1(specifierValue);
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    String globalHandicap = (80002 == marketId) ? specifierValue : new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                    virtualMarketDTO.setAddition2(globalHandicap);
                    virtualMarketDTO.setAddition3(homeScore.toString());
                    virtualMarketDTO.setAddition4(awayScore.toString());
                } else {
                    virtualMarketDTO.setAddition2(virtualMarketDTO.getAddition1());
                }

            } else if (intervalList.contains(marketId)) {
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                virtualMarketDTO.setAddition2(intervalArr[0]);
                virtualMarketDTO.setAddition3(intervalArr[1]);

            } else if (intervalTotalList.contains(marketId)) {
                virtualMarketDTO.setAddition1(specifierValue);
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                virtualMarketDTO.setAddition2(intervalArr[0]);
                virtualMarketDTO.setAddition3(intervalArr[1]);

            } else if (intervalHandicapList.contains(marketId)) {
                virtualMarketDTO.setAddition1(specifierValue);
                HomeAwayScore<Integer> intervalScore = getIntervalHandicapScore(marketName, scoreSummary, marketId);
                Integer homeScore = intervalScore.getHomeScore();
                Integer awayScore = intervalScore.getAwayScore();
                if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                    String globalHandicap = new BigDecimal(specifierValue).subtract(new BigDecimal(homeScore)).add(new BigDecimal(awayScore)).toString();
                    virtualMarketDTO.setAddition2(globalHandicap);
                    virtualMarketDTO.setAddition3(homeScore.toString());
                    virtualMarketDTO.setAddition4(awayScore.toString());
                } else {
                    virtualMarketDTO.setAddition2(virtualMarketDTO.getAddition1());
                }
                String[] intervalArr = marketName.substring(marketName.indexOf(PUNCTUATION_BRACKETS_LEFT) + 1, marketName.indexOf(PUNCTUATION_BRACKETS_RIGHT)).split(PUNCTUATION_HYPHEN);
                virtualMarketDTO.setAddition5(intervalArr[0] + PUNCTUATION_COMMA + intervalArr[1]);

            } else if (quarter1List.contains(marketId)) {
                if (11061 == marketId) {
                    virtualMarketDTO.setAddition1(String.valueOf(1));
                    virtualMarketDTO.setAddition2(specifierValue);
                } else {
                    virtualMarketDTO.setAddition1(specifierValue);
                    virtualMarketDTO.setAddition2(String.valueOf(1));
                }

            } else if (quarter2List.contains(marketId)) {
                if (11062 == marketId) {
                    virtualMarketDTO.setAddition1(String.valueOf(2));
                    virtualMarketDTO.setAddition2(specifierValue);
                } else {
                    virtualMarketDTO.setAddition1(specifierValue);
                    virtualMarketDTO.setAddition2(String.valueOf(2));
                }

            } else if (quarter3List.contains(marketId)) {
                if (11063 == marketId) {
                    virtualMarketDTO.setAddition1(String.valueOf(3));
                    virtualMarketDTO.setAddition2(specifierValue);
                } else {
                    virtualMarketDTO.setAddition1(specifierValue);
                    virtualMarketDTO.setAddition2(String.valueOf(3));
                }

            } else if (quarter4List.contains(marketId)) {
                if (11064 == marketId) {
                    virtualMarketDTO.setAddition1(String.valueOf(4));
                    virtualMarketDTO.setAddition2(specifierValue);
                } else {
                    virtualMarketDTO.setAddition1(specifierValue);
                    virtualMarketDTO.setAddition2(String.valueOf(4));
                }

            } else if (handicapAdd2List.contains(marketId)) {
                virtualMarketDTO.setAddition1(specifierValue);
                virtualMarketDTO.setAddition2(specifierValue);

            } else {
                virtualMarketDTO.setAddition1(specifierValue);
            }
        }
    }

    /**
     * 构建三方投注项投递对象
     *
     * @param betPieceEntity      自定义投注项实体
     * @param aoMatchMarketInfo   自定义赛事盘口实体
     * @param thirdMarketSourceId 三方盘口源Id
     * @return 三方投注项投递对象
     */
    private ThirdMarketOddsDTO buildMarketOdds(BetPieceEntity betPieceEntity, AoMatchMarketInfo aoMatchMarketInfo, String thirdMarketSourceId) {
        ThirdMarketOddsDTO thirdMarketOddsDTO = new ThirdMarketOddsDTO();
        //当前投注项是否被激活.1激活; 0未激活(锁盘)
        thirdMarketOddsDTO.setActive(betPieceEntity.isActive() ? NUMBER_ONE : NUMBER_ZERO);
        //三方投注项源Id
        String betPriceId = betPieceEntity.getBetPriceId();
        String thirdOddsFieldSourceId = thirdMarketSourceId + PUNCTUATION_UNDERLINE + betPriceId;
        thirdMarketOddsDTO.setThirdOddsFieldSourceId(thirdOddsFieldSourceId);
        //用于排序, 大于1, 越小越靠前
        Integer orderOdds = betPieceEntity.getOrder();
        thirdMarketOddsDTO.setOrderOdds(orderOdds);

        //投注项原始赔率（AO计算赔率）
        int originalOddsValue = new BigDecimal(betPieceEntity.getOdds()).multiply(new BigDecimal(NUMBER_HUNDRED_THOUSAND)).intValue();
        thirdMarketOddsDTO.setOriginalOddsValue(originalOddsValue);

        //投注项赔率（producer抽水后赔率）
        int modifyOddsValue = BigDecimal.valueOf(betPieceEntity.getAoOddsValue()).multiply(new BigDecimal(NUMBER_HUNDRED_THOUSAND)).intValue();
        thirdMarketOddsDTO.setOddsValue(modifyOddsValue);
        //AO百家赔赔率
        thirdMarketOddsDTO.setAoOddsValue((double) modifyOddsValue);

        //投注给哪一方（球员类玩法）: T1主队, T2客队
        thirdMarketOddsDTO.setTargetSide(null);
        //数据源编码
        thirdMarketOddsDTO.setDataSourceCode(AO_SOURCE_CODE);
        //投注项名称国际化
        I18nItemDTO selectionI18nItemDTO = new I18nItemDTO();
        selectionI18nItemDTO.setLanguageType(Locale.ENGLISH.getLanguage());
        selectionI18nItemDTO.setText(betPieceEntity.getName());
        thirdMarketOddsDTO.setI18nNames(Collections.singletonList(selectionI18nItemDTO));

        //设置附加信息、投注项模板源Id
        Integer marketId = Integer.valueOf(thirdMarketSourceId.split(PUNCTUATION_UNDERLINE)[1].trim());
        //无投注项模板处理
        if (MarketCategory.MARKET_WITH_OUT_TEMPLATE_LIST.contains(marketId)) {
            thirdMarketOddsDTO.setThirdTempletSourceId(CommonConstant.NONE);
            setAdditionWithoutTemplate(thirdMarketOddsDTO, betPieceEntity, aoMatchMarketInfo, marketId);
            //有投注项模板处理
        } else {
            String thirdTemplateSourceId = AO_SOURCE_CODE + PUNCTUATION_COLON + marketId + PUNCTUATION_COLON + betPriceId;
            thirdMarketOddsDTO.setThirdTempletSourceId(thirdTemplateSourceId);
            setAdditionWithTemplate(thirdMarketOddsDTO, betPieceEntity, aoMatchMarketInfo, marketId);
        }

        //修改时间戳
        thirdMarketOddsDTO.setModifyTime(System.currentTimeMillis());
        return thirdMarketOddsDTO;
    }

    /**
     * 构建三方电子赛事投注项投递对象
     *
     * @param betPieceEntity      自定义投注项实体
     * @param aoMatchMarketInfo   自定义赛事盘口实体
     * @param thirdMarketSourceId 三方盘口源Id
     * @return 三方电子赛事投注项投递对象
     */
    private VirtualOddsDTO buildEsportMarketOdds(BetPieceEntity betPieceEntity, AoMatchMarketInfo aoMatchMarketInfo, String thirdMarketSourceId) {
        VirtualOddsDTO virtualOddsDTO = new VirtualOddsDTO();
        //当前投注项是否被激活.1激活; 0未激活(锁盘)
        virtualOddsDTO.setIsSuspended(betPieceEntity.isActive() ? Boolean.TRUE : Boolean.FALSE);
        //三方投注项源Id
        String betPriceId = betPieceEntity.getBetPriceId();
        String thirdOddsFieldSourceId = thirdMarketSourceId + PUNCTUATION_UNDERLINE + betPriceId;
        virtualOddsDTO.setThirdOddsSourceId(thirdOddsFieldSourceId);
        //用于排序, 大于1, 越小越靠前
        Integer orderOdds = betPieceEntity.getOrder();
        virtualOddsDTO.setOddsOrder(orderOdds);

        //投注项赔率（producer抽水后赔率）
        int modifyOddsValue = BigDecimal.valueOf(betPieceEntity.getAoOddsValue()).multiply(new BigDecimal(NUMBER_HUNDRED_THOUSAND)).intValue();
        virtualOddsDTO.setOddsValue(modifyOddsValue);

        //数据源编码
        virtualOddsDTO.setDataSourceCode(AO_SOURCE_CODE);
        //投注项名称国际化
        com.panda.virtual.dto.I18nItemDTO selectionI18nItemDTO = new com.panda.virtual.dto.I18nItemDTO();
        selectionI18nItemDTO.setLanguageType(Locale.ENGLISH.getLanguage());
        selectionI18nItemDTO.setText(betPieceEntity.getName());
        virtualOddsDTO.setI18nNames(Collections.singletonList(selectionI18nItemDTO));

        //设置附加信息、投注项模板源Id
        Integer marketId = Integer.valueOf(thirdMarketSourceId.split(PUNCTUATION_UNDERLINE)[1].trim());
        //无投注项模板处理
        if (MarketCategory.MARKET_WITH_OUT_TEMPLATE_LIST.contains(marketId)) {
            virtualOddsDTO.setThirdTempletSourceId(CommonConstant.NONE);
            setEsportAdditionWithoutTemplate(virtualOddsDTO, betPieceEntity, aoMatchMarketInfo, marketId);
            //有投注项模板处理
        } else {
            String thirdTemplateSourceId = AO_SOURCE_CODE + PUNCTUATION_COLON + marketId + PUNCTUATION_COLON + betPriceId;
            virtualOddsDTO.setThirdTempletSourceId(thirdTemplateSourceId);
            setEsportAdditionWithTemplate(virtualOddsDTO, betPieceEntity, aoMatchMarketInfo, marketId);
        }

        //修改时间戳
        virtualOddsDTO.setModifyTime(System.currentTimeMillis());
        return virtualOddsDTO;
    }

    /**
     * 波胆类玩法
     */
    private final List<Integer> NoneTempCorrectScoreList = Arrays.asList(10008, 20008, 60006, 70005, 80005, 10050, 10065, 20023, 60017, 40027);
    /**
     * 净胜分类玩法
     */
    private final List<Integer> NoneTempWinMarginList = Arrays.asList(10009, 10012, 80006, 20022, 60024);
    /**
     * 进球区间类玩法
     */
    private final List<Integer> NoneTempIntervalList = Arrays.asList(10016, 40014);
    /**
     * 默认主队
     */
    private final List<Integer> NoneTempHomeList = Arrays.asList(10017, 20015, 40015, 40030);
    /**
     * 默认客队
     */
    private final List<Integer> NoneTempAwayList = Arrays.asList(10018, 20016, 40016, 40031);
    /**
     * 半/全场比分玩法
     */
    private final List<Integer> NoneTempHtFtScoreList = Arrays.asList(10020);
    /**
     * 半/全场 & 准确进球数 玩法
     */
    private final List<Integer> NoneTempHtFtAndExactGoalList = Arrays.asList(10062);
    /**
     * 篮球净胜分玩法
     */
    private final List<Integer> NoneTempBasketWinMarginList = Arrays.asList(11043, 11045, 11046, 11047, 11048, 11051);

    /**
     * 设置不存在模板三方投注项的附加信息
     *
     * @param thirdMarketOddsDTO 三方投注项对象
     * @param betPieceEntity     自定义投注项实体
     * @param aoMatchMarketInfo  自定义赛事盘口实体
     * @param marketId           玩法Id
     */
    private void setAdditionWithoutTemplate(ThirdMarketOddsDTO thirdMarketOddsDTO, BetPieceEntity betPieceEntity, AoMatchMarketInfo aoMatchMarketInfo, Integer marketId) {
        //主客队Id
        String homeId = aoMatchMarketInfo.getHomeTeamId();
        String awayId = aoMatchMarketInfo.getAwayTeamId();

        //投注项名称
        String betPieceEntityName = betPieceEntity.getName();
        //投注项类型
        String oddsType;
        if (NoneTempCorrectScoreList.contains(marketId)) {
            oddsType = betPieceEntityName.replace(PUNCTUATION_HYPHEN, PUNCTUATION_COLON).replace(OTHER_LOWER, OTHER);
            thirdMarketOddsDTO.setOddsType(oddsType);
            if (!OTHER.equals(oddsType) && 10050 != marketId) {
                thirdMarketOddsDTO.setAddition1(oddsType.split(PUNCTUATION_COLON)[0].trim());
                thirdMarketOddsDTO.setAddition2(oddsType.split(PUNCTUATION_COLON)[1].trim());
            }
        } else if (NoneTempWinMarginList.contains(marketId)) {
            if (betPieceEntityName.contains(COMPETITOR_ONE)) {
                oddsType = NUMBER_ONE + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
                thirdMarketOddsDTO.setAddition1(homeId);
            } else if (betPieceEntityName.contains(COMPETITOR_TWO)) {
                oddsType = NUMBER_TWO + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
                thirdMarketOddsDTO.setAddition1(awayId);
            } else {
                thirdMarketOddsDTO.setAddition1(String.valueOf(NUMBER_ZERO));
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
            thirdMarketOddsDTO.setOddsType(oddsType);
        } else if (NoneTempIntervalList.contains(marketId)) {
            String[] nameArr = betPieceEntityName.split(PUNCTUATION_HYPHEN);
            thirdMarketOddsDTO.setOddsType(betPieceEntityName);
            thirdMarketOddsDTO.setAddition1(nameArr[0]);
            if (nameArr.length > 1) {
                thirdMarketOddsDTO.setAddition2(nameArr[1]);
            }
        } else if (NoneTempHomeList.contains(marketId)) {
            thirdMarketOddsDTO.setOddsType(betPieceEntityName);
            thirdMarketOddsDTO.setAddition1(homeId);
        } else if (NoneTempAwayList.contains(marketId)) {
            thirdMarketOddsDTO.setOddsType(betPieceEntityName);
            thirdMarketOddsDTO.setAddition1(awayId);
        } else if (NoneTempHtFtScoreList.contains(marketId)) {
            thirdMarketOddsDTO.setOddsType(betPieceEntityName.replace(PUNCTUATION_HYPHEN, PUNCTUATION_COLON));
            String[] nameArr = betPieceEntityName.split(PUNCTUATION_SPACE);
            //半场比分
            if (nameArr[0].contains(PUNCTUATION_PLUS)) {
                thirdMarketOddsDTO.setAddition1(nameArr[0]);
                thirdMarketOddsDTO.setAddition2(nameArr[0]);
            } else {
                String[] htScoreArr = nameArr[0].split(PUNCTUATION_HYPHEN);
                thirdMarketOddsDTO.setAddition1(htScoreArr[0]);
                thirdMarketOddsDTO.setAddition2(htScoreArr[1]);
            }
            //全场比分
            if (nameArr[1].contains(PUNCTUATION_PLUS)) {
                thirdMarketOddsDTO.setAddition3(nameArr[1]);
                thirdMarketOddsDTO.setAddition4(nameArr[1]);
            } else {
                String[] ftScoreArr = nameArr[1].split(PUNCTUATION_HYPHEN);
                thirdMarketOddsDTO.setAddition3(ftScoreArr[0]);
                thirdMarketOddsDTO.setAddition4(ftScoreArr[1]);
            }
        } else if (NoneTempHtFtAndExactGoalList.contains(marketId)) {
            oddsType = betPieceEntityName.replace(COMPETITOR_ONE_WITH_BRACE, String.valueOf(NUMBER_ONE)).replace(COMPETITOR_TWO_WITH_BRACE, String.valueOf(NUMBER_TWO)).replace(DRAW.getName(), X_UPPER)
                    .replace(PUNCTUATION_AMPERSAND, AND).replace(PUNCTUATION_SPACE, "");
            thirdMarketOddsDTO.setOddsType(oddsType);
        } else if (NoneTempBasketWinMarginList.contains(marketId)) {
            if (betPieceEntityName.contains(COMPETITOR_ONE)) {
                String[] scoreArr = betPieceEntityName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    thirdMarketOddsDTO.setAddition1(String.valueOf(NUMBER_ONE));
                    thirdMarketOddsDTO.setAddition2(scoreArr[0]);
                    thirdMarketOddsDTO.setAddition3(scoreArr[1]);
                    thirdMarketOddsDTO.setAddition4(homeId);
                } else {
                    if (11043 == marketId) {
                        thirdMarketOddsDTO.setAddition1(homeId);
                    } else {
                        thirdMarketOddsDTO.setAddition1(String.valueOf(NUMBER_ONE));
                        thirdMarketOddsDTO.setAddition2(scoreArr[0]);
                        thirdMarketOddsDTO.setAddition4(homeId);
                    }
                }
                oddsType = NUMBER_ONE + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
            } else if (betPieceEntityName.contains(COMPETITOR_TWO)) {
                String[] scoreArr = betPieceEntityName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    thirdMarketOddsDTO.setAddition1(String.valueOf(NUMBER_TWO));
                    thirdMarketOddsDTO.setAddition2(scoreArr[0]);
                    thirdMarketOddsDTO.setAddition3(scoreArr[1]);
                    thirdMarketOddsDTO.setAddition4(awayId);
                } else {
                    if (11043 == marketId) {
                        thirdMarketOddsDTO.setAddition1(awayId);
                    } else {
                        thirdMarketOddsDTO.setAddition1(String.valueOf(NUMBER_TWO));
                        thirdMarketOddsDTO.setAddition2(scoreArr[0]);
                        thirdMarketOddsDTO.setAddition4(awayId);
                    }
                }
                oddsType = NUMBER_TWO + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
            } else if (betPieceEntityName.contains(BY_LOWER)) {
                String[] scoreArr = betPieceEntityName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    thirdMarketOddsDTO.setAddition1(scoreArr[0]);
                    thirdMarketOddsDTO.setAddition2(scoreArr[1]);
                } else {
                    thirdMarketOddsDTO.setAddition1(scoreArr[0]);
                }
                oddsType = betPieceEntityName.split(BY_LOWER)[1].trim();
            } else {
                if (BASKET_OTHER.getName().equals(betPieceEntityName)) {
                    thirdMarketOddsDTO.setAddition1(String.valueOf(NUMBER_ZERO));
                    oddsType = OTHER;
                } else if (BASKET_DRAW.getName().equals(betPieceEntityName)) {
                    thirdMarketOddsDTO.setAddition4(String.valueOf(NUMBER_ZERO));
                    oddsType = X_UPPER;
                } else {
                    throw new IllegalArgumentException("净胜分玩法【" + marketId + "】投注项【" + betPieceEntity.getBetPriceId() + "】名称【" + betPieceEntityName + "】匹配出错！！！");
                }
            }
            thirdMarketOddsDTO.setOddsType(oddsType);
        } else {
            thirdMarketOddsDTO.setOddsType(betPieceEntityName);
        }
    }

    /**
     * 设置电子赛事不存在模板三方投注项的附加信息
     *
     * @param virtualOddsDTO    三方电子赛事投注项对象
     * @param betPieceEntity    自定义投注项实体
     * @param aoMatchMarketInfo 自定义赛事盘口实体
     * @param marketId          玩法Id
     */
    private void setEsportAdditionWithoutTemplate(VirtualOddsDTO virtualOddsDTO, BetPieceEntity betPieceEntity, AoMatchMarketInfo aoMatchMarketInfo, Integer marketId) {
        //主客队Id
        String homeId = aoMatchMarketInfo.getHomeTeamId();
        String awayId = aoMatchMarketInfo.getAwayTeamId();

        //投注项名称
        String betPieceEntityName = betPieceEntity.getName();
        //投注项类型
        String oddsType;
        if (NoneTempCorrectScoreList.contains(marketId)) {
            oddsType = betPieceEntityName.replace(PUNCTUATION_HYPHEN, PUNCTUATION_COLON).replace(OTHER_LOWER, OTHER);
            virtualOddsDTO.setOddsType(oddsType);
            if (!OTHER.equals(oddsType) && 10050 != marketId) {
                virtualOddsDTO.setAddition1(oddsType.split(PUNCTUATION_COLON)[0].trim());
                virtualOddsDTO.setAddition2(oddsType.split(PUNCTUATION_COLON)[1].trim());
            }
        } else if (NoneTempWinMarginList.contains(marketId)) {
            if (betPieceEntityName.contains(COMPETITOR_ONE)) {
                oddsType = NUMBER_ONE + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
                virtualOddsDTO.setAddition1(homeId);
            } else if (betPieceEntityName.contains(COMPETITOR_TWO)) {
                oddsType = NUMBER_TWO + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
                virtualOddsDTO.setAddition1(awayId);
            } else {
                virtualOddsDTO.setAddition1(String.valueOf(NUMBER_ZERO));
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
            virtualOddsDTO.setOddsType(oddsType);
        } else if (NoneTempIntervalList.contains(marketId)) {
            String[] nameArr = betPieceEntityName.split(PUNCTUATION_HYPHEN);
            virtualOddsDTO.setOddsType(betPieceEntityName);
            virtualOddsDTO.setAddition1(nameArr[0]);
            if (nameArr.length > 1) {
                virtualOddsDTO.setAddition2(nameArr[1]);
            }
        } else if (NoneTempHomeList.contains(marketId)) {
            virtualOddsDTO.setOddsType(betPieceEntityName);
            virtualOddsDTO.setAddition1(homeId);
        } else if (NoneTempAwayList.contains(marketId)) {
            virtualOddsDTO.setOddsType(betPieceEntityName);
            virtualOddsDTO.setAddition1(awayId);
        } else if (NoneTempHtFtScoreList.contains(marketId)) {
            virtualOddsDTO.setOddsType(betPieceEntityName.replace(PUNCTUATION_HYPHEN, PUNCTUATION_COLON));
            String[] nameArr = betPieceEntityName.split(PUNCTUATION_SPACE);
            //半场比分
            if (nameArr[0].contains(PUNCTUATION_PLUS)) {
                virtualOddsDTO.setAddition1(nameArr[0]);
                virtualOddsDTO.setAddition2(nameArr[0]);
            } else {
                String[] htScoreArr = nameArr[0].split(PUNCTUATION_HYPHEN);
                virtualOddsDTO.setAddition1(htScoreArr[0]);
                virtualOddsDTO.setAddition2(htScoreArr[1]);
            }
            //全场比分
            if (nameArr[1].contains(PUNCTUATION_PLUS)) {
                virtualOddsDTO.setAddition3(nameArr[1]);
                virtualOddsDTO.setAddition4(nameArr[1]);
            } else {
                String[] ftScoreArr = nameArr[1].split(PUNCTUATION_HYPHEN);
                virtualOddsDTO.setAddition3(ftScoreArr[0]);
                virtualOddsDTO.setAddition4(ftScoreArr[1]);
            }
        } else if (NoneTempHtFtAndExactGoalList.contains(marketId)) {
            oddsType = betPieceEntityName.replace(COMPETITOR_ONE_WITH_BRACE, String.valueOf(NUMBER_ONE)).replace(COMPETITOR_TWO_WITH_BRACE, String.valueOf(NUMBER_TWO)).replace(DRAW.getName(), X_UPPER)
                    .replace(PUNCTUATION_AMPERSAND, AND).replace(PUNCTUATION_SPACE, "");
            virtualOddsDTO.setOddsType(oddsType);
        } else if (NoneTempBasketWinMarginList.contains(marketId)) {
            if (betPieceEntityName.contains(COMPETITOR_ONE)) {
                String[] scoreArr = betPieceEntityName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    virtualOddsDTO.setAddition1(String.valueOf(NUMBER_ONE));
                    virtualOddsDTO.setAddition2(scoreArr[0]);
                    virtualOddsDTO.setAddition3(scoreArr[1]);
                    virtualOddsDTO.setAddition4(homeId);
                } else {
                    if (11043 == marketId) {
                        virtualOddsDTO.setAddition1(homeId);
                    } else {
                        virtualOddsDTO.setAddition1(String.valueOf(NUMBER_ONE));
                        virtualOddsDTO.setAddition2(scoreArr[0]);
                        virtualOddsDTO.setAddition4(homeId);
                    }
                }
                oddsType = NUMBER_ONE + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
            } else if (betPieceEntityName.contains(COMPETITOR_TWO)) {
                String[] scoreArr = betPieceEntityName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    virtualOddsDTO.setAddition1(String.valueOf(NUMBER_TWO));
                    virtualOddsDTO.setAddition2(scoreArr[0]);
                    virtualOddsDTO.setAddition3(scoreArr[1]);
                    virtualOddsDTO.setAddition4(awayId);
                } else {
                    if (11043 == marketId) {
                        virtualOddsDTO.setAddition1(awayId);
                    } else {
                        virtualOddsDTO.setAddition1(String.valueOf(NUMBER_TWO));
                        virtualOddsDTO.setAddition2(scoreArr[0]);
                        virtualOddsDTO.setAddition4(awayId);
                    }
                }
                oddsType = NUMBER_TWO + AND + betPieceEntityName.split(BY_LOWER)[1].trim();
            } else if (betPieceEntityName.contains(BY_LOWER)) {
                String[] scoreArr = betPieceEntityName.split(BY_LOWER)[1].trim().split(PUNCTUATION_HYPHEN);
                if (scoreArr.length > 1) {
                    virtualOddsDTO.setAddition1(scoreArr[0]);
                    virtualOddsDTO.setAddition2(scoreArr[1]);
                } else {
                    virtualOddsDTO.setAddition1(scoreArr[0]);
                }
                oddsType = betPieceEntityName.split(BY_LOWER)[1].trim();
            } else {
                if (BASKET_OTHER.getName().equals(betPieceEntityName)) {
                    virtualOddsDTO.setAddition1(String.valueOf(NUMBER_ZERO));
                    oddsType = OTHER;
                } else if (BASKET_DRAW.getName().equals(betPieceEntityName)) {
                    virtualOddsDTO.setAddition4(String.valueOf(NUMBER_ZERO));
                    oddsType = X_UPPER;
                } else {
                    throw new IllegalArgumentException("净胜分玩法【" + marketId + "】投注项【" + betPieceEntity.getBetPriceId() + "】名称【" + betPieceEntityName + "】匹配出错！！！");
                }
            }
            virtualOddsDTO.setOddsType(oddsType);
        } else {
            virtualOddsDTO.setOddsType(betPieceEntityName);
        }
    }

    /**
     * 默认主队投注项
     */
    private final List<Integer> tempHomeList = Arrays.asList(10006, 20006, 10023, 10024, 10025, 10026, 10027, 40005, 40012, 10042, 10043, 20019, 60011, 60012, 50010, 50013, 50016, 50018, 50021, 20026, 60022,
            50037, 10080);
    /**
     * 默认客队投注项
     */
    private final List<Integer> tempAwayList = Arrays.asList(10007, 20007, 10028, 10029, 10030, 10031, 10032, 40006, 40013, 10045, 10046, 20020, 60013, 60014, 50011, 50014, 50017, 50019, 50022, 20027, 60023,
            50038, 10081);

    /**
     * 设置存在模板三方投注项的附加信息
     *
     * @param thirdMarketOddsDTO 三方投注项对象
     * @param betPieceEntity     自定义投注项实体
     * @param aoMatchMarketInfo  自定义赛事盘口实体
     * @param marketId           玩法Id
     */
    private void setAdditionWithTemplate(ThirdMarketOddsDTO thirdMarketOddsDTO, BetPieceEntity betPieceEntity, AoMatchMarketInfo aoMatchMarketInfo, Integer marketId) {
        //主客队Id
        String homeId = aoMatchMarketInfo.getHomeTeamId();
        String awayId = aoMatchMarketInfo.getAwayTeamId();

        //投注项Id
        String betPriceId = betPieceEntity.getBetPriceId();
        SelectionTemplate selectionTemplate = SelectionTemplate.getSelectionTemplateById(betPriceId);
        //设置投注项类型
        thirdMarketOddsDTO.setOddsType(selectionTemplate.getOddsType());

        //设置附加字段
        if (tempHomeList.contains(marketId)) {
            thirdMarketOddsDTO.setAddition1(homeId);
        } else if (tempAwayList.contains(marketId)) {
            thirdMarketOddsDTO.setAddition1(awayId);
        } else {
            if (COMPETITOR_ONE.equals(selectionTemplate.getAddition1())) {
                thirdMarketOddsDTO.setAddition1(homeId);
            } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition1())) {
                thirdMarketOddsDTO.setAddition1(awayId);
            } else {
                thirdMarketOddsDTO.setAddition1(selectionTemplate.getAddition1());
            }
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition2())) {
            thirdMarketOddsDTO.setAddition2(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition2())) {
            thirdMarketOddsDTO.setAddition2(awayId);
        } else {
            thirdMarketOddsDTO.setAddition2(selectionTemplate.getAddition2());
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition3())) {
            thirdMarketOddsDTO.setAddition3(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition3())) {
            thirdMarketOddsDTO.setAddition3(awayId);
        } else {
            thirdMarketOddsDTO.setAddition3(selectionTemplate.getAddition3());
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition4())) {
            thirdMarketOddsDTO.setAddition4(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition4())) {
            thirdMarketOddsDTO.setAddition4(awayId);
        } else {
            thirdMarketOddsDTO.setAddition4(selectionTemplate.getAddition4());
        }

        thirdMarketOddsDTO.setAddition5(selectionTemplate.getAddition5());
    }

    /**
     * 设置电子赛事存在模板三方投注项的附加信息
     *
     * @param virtualOddsDTO    三方电子赛事投注项对象
     * @param betPieceEntity    自定义投注项实体
     * @param aoMatchMarketInfo 自定义赛事盘口实体
     * @param marketId          玩法Id
     */
    private void setEsportAdditionWithTemplate(VirtualOddsDTO virtualOddsDTO, BetPieceEntity betPieceEntity, AoMatchMarketInfo aoMatchMarketInfo, Integer marketId) {
        //主客队Id
        String homeId = aoMatchMarketInfo.getHomeTeamId();
        String awayId = aoMatchMarketInfo.getAwayTeamId();

        //投注项Id
        String betPriceId = betPieceEntity.getBetPriceId();
        SelectionTemplate selectionTemplate = SelectionTemplate.getSelectionTemplateById(betPriceId);
        //设置投注项类型
        virtualOddsDTO.setOddsType(selectionTemplate.getOddsType());

        //设置附加字段
        if (tempHomeList.contains(marketId)) {
            virtualOddsDTO.setAddition1(homeId);
        } else if (tempAwayList.contains(marketId)) {
            virtualOddsDTO.setAddition1(awayId);
        } else {
            if (COMPETITOR_ONE.equals(selectionTemplate.getAddition1())) {
                virtualOddsDTO.setAddition1(homeId);
            } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition1())) {
                virtualOddsDTO.setAddition1(awayId);
            } else {
                virtualOddsDTO.setAddition1(selectionTemplate.getAddition1());
            }
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition2())) {
            virtualOddsDTO.setAddition2(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition2())) {
            virtualOddsDTO.setAddition2(awayId);
        } else {
            virtualOddsDTO.setAddition2(selectionTemplate.getAddition2());
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition3())) {
            virtualOddsDTO.setAddition3(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition3())) {
            virtualOddsDTO.setAddition3(awayId);
        } else {
            virtualOddsDTO.setAddition3(selectionTemplate.getAddition3());
        }

        if (COMPETITOR_ONE.equals(selectionTemplate.getAddition4())) {
            virtualOddsDTO.setAddition4(homeId);
        } else if (COMPETITOR_TWO.equals(selectionTemplate.getAddition4())) {
            virtualOddsDTO.setAddition4(awayId);
        } else {
            virtualOddsDTO.setAddition4(selectionTemplate.getAddition4());
        }

        virtualOddsDTO.setAddition5(selectionTemplate.getAddition5());
    }
}
