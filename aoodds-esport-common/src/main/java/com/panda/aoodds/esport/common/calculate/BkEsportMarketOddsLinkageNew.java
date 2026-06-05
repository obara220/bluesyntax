package com.panda.aoodds.esport.common.calculate;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import com.panda.aoodds.esport.api.entity.BetPieceEntity;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.entity.TradeMarketItemConfig;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.market.SelectionTemplate;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.common.utils.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.AO_ESPORT_BGTIME_KEY;
import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.AO_ESPORT_SCORE_KEY;


/**
 * 盘口赔率联动
 */
@Slf4j
@Component
public class BkEsportMarketOddsLinkageNew {

    @Autowired
    private RedisService redisService;


    private static BkEsportMarketOddsLinkageNew marketOddsLinkage = null;

    public static BkEsportMarketOddsLinkageNew getInstance() {
        if (marketOddsLinkage == null) {
            marketOddsLinkage = new BkEsportMarketOddsLinkageNew();
        }
        return marketOddsLinkage;
    }

    /**
     * 需要联动的玩法
     */
    public static final List<Integer> LINKAGE_CATEGORY_IDS = Lists.newArrayList(10001, 10003, 10004, 10005, 10006, 10007, 10008, 10009, 10012, 10013,
            10014, 10015, 10016, 10062, 20001, 10017, 10018, 60003, 10019, 10058, 20003, 20004, 20014, 10020, 20021, 20005, 20006, 20007, 20008, 10024, 10025, 20011, 20012, 20023, 10029, 10030,
            60015, 20015, 20016, 10033, 10034, 10039, 10040, 10022, 10063, 10064, 10065, 40001, 40002, 40003, 40005, 40006, 40008, 40009, 40010, 40014, 40015, 40016, 50001, 50002, 40018, 40019,
            50004, 50005, 40021, 50006, 50008, 50012, 50013, 50014, 50015, 50016, 50017, 50018, 50019, 50020, 50021, 50022, 70001, 70003, 70002, 70005, 70007,
            70006, 70009, 70008);
    /**
     * 独赢类 多个投注项联动保留最低赔率
     */
    public static final List<Integer> HT_FT_CATEGORY_IDS = Lists.newArrayList(10001, 20001, 40001, 40008, 50001, 50005, 70001, 70008);

    public static void main(String[] args) throws IOException {
        String path = System.getProperty("user.dir") + "\\ao-common\\src\\main\\java\\com\\panda\\aoodds\\common\\calculate\\OddsLinkageTest.json";
        String str = (new String(Files.readAllBytes(Paths.get(path))));
        List<MarketsEntity> markets = JSONObject.parseArray(str, MarketsEntity.class);
        //只处理需要联动的玩法
        List<MarketsEntity> marketsList = markets.stream().filter(m -> LINKAGE_CATEGORY_IDS.contains(m.getMarketId())).collect(Collectors.toList());
        //比分
        JSONObject goalObj = JSONObject.parseObject("{\"goal\":{\"away\":0,\"home\":0},\"corner\":{\"away\":0,\"home\":0},\"faCard\":{\"away\":0,\"home\":0}}");

        marketsList.forEach(marketsEntity -> marketsEntity.getBetPieceEntities().forEach(betPiece -> {
            if (MarketCategory.MARKET_WITH_OUT_TEMPLATE_LIST.contains(marketsEntity.getMarketId())) {
                betPiece.setOddsType(betPiece.getName());
            } else {
                betPiece.setOddsType(SelectionTemplate.getSelectionTemplateById(betPiece.getBetPriceId()).getOddsType());
            }
        }));
        //只处理需要联动的玩法
        List<MarketsEntity> linkageMarkets = marketsList.stream().filter(m -> LINKAGE_CATEGORY_IDS.contains(m.getMarketId())).collect(Collectors.toList());
        //计算盘口未联动前实时margin值
        BkEsportMarketOddsLinkageNew.getInstance().calculateMarketOddsMargin(linkageMarkets);
        //单独拷贝一份需要联动的玩法，和联动后的赔率进行对比
        List<MarketsEntity> linkageMarketsBefore = JSONObject.parseArray(JSONObject.toJSONString(linkageMarkets), MarketsEntity.class);
        //主玩法匹配联动次要玩法处理
        BkEsportMarketOddsLinkageNew.getInstance().calculateOddsLinkageProcessor(linkageMarkets, goalObj);
        //全场独赢、上半场独赢 当某个1x2多个投注项被联动之后，只保留发生概率大的1个赔率联动（原始赔率低的那个），另外的投注项不做联动
        BkEsportMarketOddsLinkageNew.getInstance().ftHt1X2Processor(linkageMarketsBefore, linkageMarkets);
        //计算盘口联动后实时margin值
        BkEsportMarketOddsLinkageNew.getInstance().calculateMarketOddsMargin(linkageMarkets);
        //盘口未联动前的实时margin 与 联动后的margin进行对比
        BkEsportMarketOddsLinkageNew.getInstance().marketMarginCompare("", "aoMatchId", linkageMarketsBefore, linkageMarkets);

    }


    /**
     * 赔率联动
     *
     * @param linkId                             linkid
     * @param aoMatchId                          赛事id
     * @param marketsList                        盘口集合
     * @param marketMarginAndMaxMinOddsConfigMap 最大最小赔率
     * @param isManualSocre                      是否手工比分 0否：1是
     */
    public void calculateOddsLinkage(String linkId, String aoMatchId, Long sportId, List<MarketsEntity> marketsList, Map<String, TradeMarketItemConfig> marketMarginAndMaxMinOddsConfigMap, String isManualSocre) {
        //最大最小赔赔率
        marketOddsVerify(linkId, aoMatchId, sportId, marketsList, marketMarginAndMaxMinOddsConfigMap);
    }

    /**
     * 处理联动
     *
     * @param marketsList 盘口集合
     * @param goalObj     比分
     */
    public void calculateOddsLinkageProcessor(List<MarketsEntity> marketsList, JSONObject goalObj) {
        Map<Integer, List<MarketsEntity>> marketsMap = marketsList.stream().collect(Collectors.groupingBy(MarketsEntity::getMarketId));
        for (Map.Entry<Integer, List<MarketsEntity>> entry : marketsMap.entrySet()) {
            Integer categoryId = entry.getKey();
            List<MarketsEntity> marketsEntity = entry.getValue();
            switch (categoryId) {
                case 10012://净胜分3+
                case 10009://净胜分4+
                    winningMarginProcessor(marketsEntity, marketsMap);
                    break;
                case 10001://全场独赢
                    fT1X2Processor(marketsEntity, marketsMap);
                    break;
                case 10004://全场让球
                    ftAsianHandicapProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 20004://半场让球
                    half1AsianHandicapProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 60015://下半场让球
                    half2AsianHandicapProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 10005://全场大小
                    ftOverUnderProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 20005://半场大小
                    half1OverUnderProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 60003://下半场大小
                    half2TotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 10019://两队都进球
                    ftBothTeamScoreProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 10006://{主队}进球大小
                    ftTeam1OddEvenProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 20006://上半场{主队}进球大小
                    half1Team1OverUnderProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 10007://{客队}进球大小
                    ftTeam2OverUnderProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 20007://上半场{客队}进球大小
                    half1Team2OverUnderProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 10008://全场比分
                    fTCorrectScoreProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 20008://半场比分
                    half1CorrectScoreProcessor(marketsEntity, marketsMap, goalObj);
                    break;

                case 40002://全场角球让分
                    cornerHandicapProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 40009://半场角球让分
                    cornerH1HandicapProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 40003://全场角球大小
                    cornerTotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 40010://半场角球大小
                    cornerH1TotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 40005://主队全场角球大小
                    cornerTeam1TotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 40006://客队全场角球大小
                    cornerTeam2TotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;

                case 50002://全场罚牌让分
                    bookingHandicapProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 50006://半场罚牌让分
                    bookingH1HandicapProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 50004://全场罚牌大小
                    bookingTotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 50008://半场罚牌大小
                    bookingH1TotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 50013://{主队}罚牌大小
                    bookingTeam1TotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 50014://{客队}罚牌大小
                    bookingTeam2TotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 50018://上半场{主队}罚牌大小
                    bookingH1Team1TotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 50019://上半场{客队}罚牌大小
                    bookingH1Team2TotalTotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;

                case 70002://加时赛-让球
                    overtimeHandicapProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 70009://加时赛-上半场让球
                    overtimeH1HandicapProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 70003://加时赛-大小
                    overtimeTotalProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                case 70005://加时赛-正确比分
                    overtimeCorrectScoreProcessor(marketsEntity, marketsMap, goalObj);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 净胜分3+、 净胜分4+ 联动 全场让球胜平负
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     */
    private void winningMarginProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap) {
        AtomicInteger x = new AtomicInteger();
        Map<String, BetPieceEntity> betPiecesEntityMap = marketEntityByOddsType(marketsEntity);
        betPiecesEntityMap.forEach((k, v) -> {
            if (k.contains("{competitor1} win by")) {
                x.incrementAndGet();
            }
        });
        x.decrementAndGet();

        List<MarketsEntity> marketsEntities = marketsMap.get(10013);
        if (CollectionUtils.isEmpty(marketsEntities)) {
            return;
        }
        Map<String, MarketsEntity> handicapMarketsEntityMap = marketsEntities.stream().collect(Collectors.toMap(MarketsEntity::getHandicap, a -> a, (k1, k2) -> k1));

        for (int i = 1; i <= x.get(); i++) {
            //主胜 匹配主胜投注项条数为X： {competitor1} win by，匹配（10013全场让球胜平负）盘口：-(X-1)
            BetPieceEntity betPieceEntities1x = betPiecesEntityMap.get("{competitor1} win by " + i);
            MarketsEntity marketsEntityHome = handicapMarketsEntityMap.get("-" + i + ".0");
            if (null != marketsEntityHome && null != betPieceEntities1x) {
                marketsEntityHome.getBetPieceEntities().stream().filter(bet -> bet.getOddsType().equals("X")).forEach(bet -> bet.setAoOddsValue(betPieceEntities1x.getAoOddsValue()));
            }
            //客胜 匹配客胜投注项条数为X： {competitor2} win by，匹配（10013全场让球胜平负）盘口：+(X-1)
            BetPieceEntity betPieceEntities2x = betPiecesEntityMap.get("{competitor2} win by " + i);
            MarketsEntity marketsEntityAway = handicapMarketsEntityMap.get(i + ".0");
            if (null != marketsEntityAway && null != betPieceEntities2x) {
                marketsEntityAway.getBetPieceEntities().stream().filter(bet -> bet.getOddsType().equals("X")).forEach(bet -> bet.setAoOddsValue(betPieceEntities2x.getAoOddsValue()));
            }
        }
    }

    /**
     * 全场独赢 联动 净胜分3+
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     */
    private void fT1X2Processor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap) {
        //全场独赢和局(X)
        Map<String, BetPieceEntity> betPiecesEntityMap = marketEntityByOddsType(marketsEntity);
        BetPieceEntity mainBetPieceEntity = betPiecesEntityMap.get("X");

        //匹配（10012匹配净胜分3+）和局(draw)
        betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10012, "draw");
    }

    /**
     * 全场让球 联动 全场独赢
     * 全场让球 联动 平局退款
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param
     */
    private void ftAsianHandicapProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
            double value = Double.parseDouble(marketEntity.getHandicap()) - (homeScore - awayScore);
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (value == -0.5) {
                //全场盘口值 = -0.5主（1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("1");
                //匹配（10001全场独赢/）1x2主胜（1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10001, "1");
            } else if (value == 0.5) {
                //全场盘口值 =  0.5客（2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("2");
                //匹配（10001全场独赢/）1x2客胜（2）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10001, "2");
            } else if (value == 0) {
                //全场盘口值 =  0主（1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("1");
                //匹配（10014平局退款）主（1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10014, "1");

                //全场盘口值 =  0客（2）
                BetPieceEntity betPieceEntityAway = betPieceHandicapMap.get("2");
                //匹配（10014平局退款）客（2）
                betPiecesOddsLinkage(betPieceEntityAway, marketsMap, 10014, "2");
            }
        }
    }

    /**
     * 半场让球 联动 半场独赢
     * 半场让球 联动 上半场平局退款
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void half1AsianHandicapProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
            double value = Double.parseDouble(marketEntity.getHandicap()) - (homeScore - awayScore);
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (value == -0.5) {
                //全场盘口值 = -0.5主
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("1");
                //匹配（20001半场独赢/）1x2主胜（1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20001, "1");
            } else if (value == 0.5) {
                //全场盘口值 =  0.5客
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("2");
                //匹配（20001半场独赢/）1x2客胜（2）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20001, "2");
            } else if (value == 0) {
                //全场盘口值 =  0主 （1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("1");
                //匹配（20012上半场平局退款）主（1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20012, "1");
                //全场盘口值 =  0客 （2）
                BetPieceEntity betPieceEntityAway = betPieceHandicapMap.get("2");
                //匹配（20012上半场平局退款）客（2）
                betPiecesOddsLinkage(betPieceEntityAway, marketsMap, 20012, "2");
            }
        }
    }

    /**
     * 下半场让球 匹配 下半场独赢
     * 下半场让球 匹配 下半场平局退款
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void half2AsianHandicapProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
            double value = Double.parseDouble(marketEntity.getHandicap()) - (homeScore - awayScore);
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (value == -0.5) {
                //全场盘口值 = -0.5主 （1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("1");
                //匹配（60001下半场独赢/）1x2主胜（1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60001, "1");
            } else if (value == 0.5) {
                //全场盘口值 =  0.5客
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("2");
                //匹配（60001下半场独赢/）1x2客胜（2）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60001, "2");
            } else if (value == 0) {
                //全场盘口值 =  0主 （1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("1");
                //匹配（60007下半场平局退款）主（1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60007, "1");

                //全场盘口值 =  0客 （2）
                BetPieceEntity betPieceEntityAway = betPieceHandicapMap.get("2");
                //匹配（60007下半场平局退款）客（2）
                betPiecesOddsLinkage(betPieceEntityAway, marketsMap, 60007, "2");

            }
        }
    }

    /**
     * 全场大小 匹配 进球单双
     * 全场大小 匹配 总进球数区间
     * 全场大小 匹配 全场比分
     * 全场大小 匹配 准确进球数
     * 全场大小 匹配 第{X}个进球
     * 全场大小 匹配 第{X}个进球何时发生？
     * 全场大小 匹配 净胜分4+
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void ftOverUnderProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //总分
            int sum = homeScore + awayScore;
            String x_Handicap = sum + ".5";
            if (marketEntity.getHandicap().equals(x_Handicap)) {
                //比分总分为x，x.5小（Under）匹配（10003进球单双），x＝单数时，连动单（Odd）; 双数时，连动双（Even）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                String oddsType = sum % 2 == 0 ? "Even" : "Odd";
                //betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10003, oddsType);
            }
            if (sum <= 1 && "1.5".equals(marketEntity.getHandicap())) {
                //比分总分<=1，1.5小（Under）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                //匹配（10016总进球数区间）（0-1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10016, "0-1");
            }
            if ("6.5".equals(marketEntity.getHandicap())) {
                //球头6.5大（Over）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Over");
                //匹配（10016总进球数区间）（7+）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10016, "7+");
            }
            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //比分0-0，0.5小（Under）匹配（10008全场比分）（0-0）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "0-0");
                //比分0-0，0.5小（Under）匹配（10015准确进球数）（0）
                //betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10015, "0");
                //比分0-0，0.5小（Under）匹配（10020半/全场比分）（0-0 0-0）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10020, "0-0 0-0");
                //比分0-0，0.5小（Under）匹配（10039第{X}个进球）（no goal）
                //betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10039, "no goal");
                //比分0-0，0.5小（Under）匹配（10040第{X}个进球何时发生？）（no goal）
                //betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10040, "no goal");
                //比分0-0，0.5小（Under）匹配（10009净胜分4+）（no goal）
                //betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10009, "no goal");
            }

            if (homeScore == 0 && awayScore == 1 && "1.5".equals(marketEntity.getHandicap())) {
                //比分0-1，1.5小（Under）匹配（10008全场比分）（0-1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "0-1");
            }
            if (homeScore == 1 && awayScore == 0 && "1.5".equals(marketEntity.getHandicap())) {
                //比分1-0，1.5小（Under）匹配（10008全场比分）（1-0）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "1-0");
            }
            if (homeScore == 0 && awayScore == 2 && "2.5".equals(marketEntity.getHandicap())) {
                //比分0-2，2.5小（Under）匹配（10008全场比分）（0-2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "0-2");
            }
            if (homeScore == 2 && awayScore == 0 && "2.5".equals(marketEntity.getHandicap())) {
                //比分2-0，2.5小（Under）匹配（10008全场比分）（2-0）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "2-0");
            }
            if (homeScore == 1 && awayScore == 1 && "2.5".equals(marketEntity.getHandicap())) {
                //比分2-0，2.5小（Under）匹配（10008全场比分）（1-1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "1-1");
            }
            if (homeScore == 2 && awayScore == 1 && "3.5".equals(marketEntity.getHandicap())) {
                //比分2-1，3.5小（Under）匹配（10008全场比分）（2-1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "2-1");
            }
            if (homeScore == 1 && awayScore == 2 && "3.5".equals(marketEntity.getHandicap())) {
                //比分1-2，3.5小（Under）匹配（10008全场比分）（1-2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "1-2");
            }
            if (homeScore == 0 && awayScore == 3 && "3.5".equals(marketEntity.getHandicap())) {
                //比分0-3，3.5小（Under）匹配（10008全场比分）（0-3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "0-3");
            }
            if (homeScore == 3 && awayScore == 0 && "3.5".equals(marketEntity.getHandicap())) {
                //比分3-0，3.5小（Under）匹配（10008全场比分）（3-0）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "3-0");
            }
            if (homeScore == 3 && awayScore == 1 && "4.5".equals(marketEntity.getHandicap())) {
                //比分3-1，4.5小（Under）匹配（10008全场比分）（3-1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "3-1");
            }
            if (homeScore == 1 && awayScore == 3 && "4.5".equals(marketEntity.getHandicap())) {
                //比分1-3，4.5小（Under）匹配（10008全场比分）（1-3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "1-3");
            }
            if (homeScore == 2 && awayScore == 2 && "4.5".equals(marketEntity.getHandicap())) {
                //比分2-2，4.5小（Under）匹配（10008全场比分）（2-2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "2-2");
            }
            if (homeScore == 3 && awayScore == 2 && "5.5".equals(marketEntity.getHandicap())) {
                //比分3-2，5.5小（Under）匹配（10008全场比分）（3-2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "3-2");
            }
            if (homeScore == 2 && awayScore == 3 && "5.5".equals(marketEntity.getHandicap())) {
                //比分2-3，4.5小（Under）匹配（10008全场比分）（2-3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "2-3");
            }
            if (homeScore == 3 && awayScore == 3 && "6.5".equals(marketEntity.getHandicap())) {
                //比分3-3，6.5小（Under）匹配（10008全场比分）（3-3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "3-3");
            }
            //--------------------
            if (homeScore == 4 && awayScore == 0 && "4.5".equals(marketEntity.getHandicap())) {
                //比分4-0，4.5小（Under）匹配（10008全场比分）（4-0）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "4-0");
            }
            if (homeScore == 0 && awayScore == 4 && "4.5".equals(marketEntity.getHandicap())) {
                //比分0-4，4.5小（Under）匹配（10008全场比分）（0-4）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "0-4");
            }
            if (homeScore == 4 && awayScore == 1 && "5.5".equals(marketEntity.getHandicap())) {
                //比分0-4，5.5小（Under）匹配（10008全场比分）（0-4）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "4-1");
            }
            if (homeScore == 1 && awayScore == 4 && "5.5".equals(marketEntity.getHandicap())) {
                //比分1-4，5.5小（Under）匹配（10008全场比分）（1-4）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "1-4");
            }
            if (homeScore == 4 && awayScore == 2 && "6.5".equals(marketEntity.getHandicap())) {
                //比分4-2，6.5小（Under）匹配（10008全场比分）（4-2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "4-2");
            }
            if (homeScore == 2 && awayScore == 4 && "6.5".equals(marketEntity.getHandicap())) {
                //比分2-4，6.5小（Under）匹配（10008全场比分）（2-4）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "2-4");
            }
            if (homeScore == 4 && awayScore == 3 && "7.5".equals(marketEntity.getHandicap())) {
                //比分4-3，7.5小（Under）匹配（10008全场比分）（4-3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "4-3");
            }
            if (homeScore == 3 && awayScore == 4 && "7.5".equals(marketEntity.getHandicap())) {
                //比分3-4，7.5小（Under）匹配（10008全场比分）（3-4）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "3-4");
            }
            if (homeScore == 4 && awayScore == 4 && "8.5".equals(marketEntity.getHandicap())) {
                //比分4-4，7.5小（Under）匹配（10008全场比分）（4-4）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10008, "4-4");
            }

            if (sum == 1 && "1.5".equals(marketEntity.getHandicap())) {
                //总分为1，1.5小（Under）匹配（10015准确进球数）（1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10015, "1");
                //总分为1，1.5小（Under）匹配（10016总进球数区间）（0-1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10016, "0-1");
            }
            if (sum == 3 && "3.5".equals(marketEntity.getHandicap())) {
                //总分为3，3.5小（Under）匹配（10015准确进球数）（3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10015, "3");
                //总分为3，3.5小（Under）匹配（10016总进球数区间）（2-3）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10016, "2-3");
            }
//            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
//                //球头0.5大（Over）
//                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Over");
//                //匹配（10065全场反波胆）（0:0）
//                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10065, "0-0");
//            }
            if (homeScore <= 4 && awayScore <= 4 && marketEntity.getHandicap().equals(x_Handicap)) {
                //球头0.5大（Over）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Over");
                //匹配（10065全场反波胆）（homeScore:awayScore）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10065, homeScore + "-" + awayScore);
            }
        }
    }

    /**
     * 半场大小 匹配 上半场进球单/双
     * 半场大小 匹配 上半场比分
     * 半场大小 匹配 上半场准确进球数
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void half1OverUnderProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //总分
            int sum = homeScore + awayScore;
            String x_Handicap = sum + ".5";
            if (marketEntity.getHandicap().equals(x_Handicap)) {
                //比分总分为x，x.5小（Under）匹配（20003上半场进球单/双），x＝单数时，连动单（Odd）; 双数时，连动双（Even）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                String oddsType = sum % 2 == 0 ? "Even" : "Odd";
                //betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20003, oddsType);
            }
            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //比分0-0，0.5小（Under）匹配（20008上半场比分）（0-0）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "0-0");
                //比分0-0，0.5小（Under）匹配（20011上半场准确进球数）（0）
                //betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20011, "0");
//                //比分0-0，球头0.5大（Over） 匹配（20023上半场反波胆）（0:0）
//                BetPieceEntity mainBetPieceEntityMax = betPieceHandicapMap.get("Over");
//                betPiecesOddsLinkage(mainBetPieceEntityMax, marketsMap, 20023, "0-0");
            }
            if (homeScore <= 3 && awayScore <= 3 && marketEntity.getHandicap().equals(x_Handicap)) {
                //球头0.5大（Over）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Over");
                //匹配（20023上半场反波胆）（homeScore:awayScore）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20023, homeScore + "-" + awayScore);
            }
            if (homeScore == 0 && awayScore == 1 && "1.5".equals(marketEntity.getHandicap())) {
                //比分0-1，1.5小（Under）匹配（20008全场比分）（0-1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "0-1");
            }
            if (homeScore == 1 && awayScore == 0 && "1.5".equals(marketEntity.getHandicap())) {
                //比分1-0，1.5小（Under）匹配（20008全场比分）（1-0）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "1-0");
            }
            if (homeScore == 0 && awayScore == 2 && "2.5".equals(marketEntity.getHandicap())) {
                //比分0-2，2.5小（Under）匹配（20008全场比分）（0-2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "0-2");
            }
            if (homeScore == 2 && awayScore == 0 && "2.5".equals(marketEntity.getHandicap())) {
                //比分2-0，2.5小（Under）匹配（20008全场比分）（2-0）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "2-0");
            }
            if (homeScore == 1 && awayScore == 1 && "2.5".equals(marketEntity.getHandicap())) {
                //比分2-0，2.5小（Under）匹配（20008全场比分）（1-1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "1-1");
            }
            if (homeScore == 2 && awayScore == 1 && "3.5".equals(marketEntity.getHandicap())) {
                //比分2-1，3.5小（Under）匹配（20008全场比分）（2-1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "2-1");
            }
            if (homeScore == 1 && awayScore == 2 && "3.5".equals(marketEntity.getHandicap())) {
                //比分1-2，3.5小（Under）匹配（20008全场比分）（1-2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "1-2");
            }
            if (homeScore == 0 && awayScore == 3 && "3.5".equals(marketEntity.getHandicap())) {
                //比分0-3，3.5小（Under）匹配（20008全场比分）（0-3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "0-3");
            }
            if (homeScore == 3 && awayScore == 0 && "3.5".equals(marketEntity.getHandicap())) {
                //比分3-0，3.5小（Under）匹配（20008全场比分）（3-0）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "3-0");
            }
            if (homeScore == 3 && awayScore == 1 && "4.5".equals(marketEntity.getHandicap())) {
                //比分3-1，4.5小（Under）匹配（20008全场比分）（3-1）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "3-1");
            }
            if (homeScore == 1 && awayScore == 3 && "4.5".equals(marketEntity.getHandicap())) {
                //比分1-3，4.5小（Under）匹配（20008全场比分）（1-3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "1-3");
            }
            if (homeScore == 2 && awayScore == 2 && "4.5".equals(marketEntity.getHandicap())) {
                //比分2-2，4.5小（Under）匹配（20008全场比分）（2-2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "2-2");
            }
            if (homeScore == 3 && awayScore == 2 && "5.5".equals(marketEntity.getHandicap())) {
                //比分3-2，5.5小（Under）匹配（20008全场比分）（3-2）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "3-2");
            }
            if (homeScore == 2 && awayScore == 3 && "5.5".equals(marketEntity.getHandicap())) {
                //比分2-3，4.5小（Under）匹配（20008全场比分）（2-3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "2-3");
            }
            if (homeScore == 3 && awayScore == 3 && "6.5".equals(marketEntity.getHandicap())) {
                //比分3-3，6.5小（Under）匹配（20008全场比分）（3-3）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20008, "3-3");
            }

            if (sum == 1 && "1.5".equals(marketEntity.getHandicap())) {
                //总分为1，1.5小（Under）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                //匹配（20011上半场准确进球数）（1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20011, "1");
            }
            if (sum == 3 && "3.5".equals(marketEntity.getHandicap())) {
                //总分为3，3.5小（Under）
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                //匹配（20011上半场准确进球数）（3）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20011, "3");
            }
        }
    }

    /**
     * 下半场大小 匹配 下半场进球单/双
     * 下半场大小 匹配 下半场比分
     * 下半场大小 匹配 下半场准确进球数
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void half2TotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //总分
            int sum = homeScore + awayScore;
            String x_Handicap = sum + ".5";
            //主玩法投注项小
            BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
            if (marketEntity.getHandicap().equals(x_Handicap)) {
                //比分总分为x，x.5小（Under）匹配（60004下半场进球单双），x＝单数时，连动单（Odd）; 双数时，连动双（Even）
                String oddsType = sum % 2 == 0 ? "Even" : "Odd";
                //betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60004, oddsType);
            }
            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //比分0-0，0.5小（Under）匹配（60006下半场比分）（0-0）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "0-0");
                //比分0-0，0.5小（Under）匹配（60005下半场准确进球数）（0）
                //betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60005, "0");
            }
            if (homeScore == 0 && awayScore == 1 && "1.5".equals(marketEntity.getHandicap())) {
                //比分0-1，1.5小（Under）匹配（60006全场比分）（0-1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "0-1");
            }
            if (homeScore == 1 && awayScore == 0 && "1.5".equals(marketEntity.getHandicap())) {
                //比分1-0，1.5小（Under）匹配（60006全场比分）（1-0）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "1-0");
            }
            if (homeScore == 0 && awayScore == 2 && "2.5".equals(marketEntity.getHandicap())) {
                //比分0-2，2.5小（Under）匹配（60006全场比分）（0-2）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "0-2");
            }
            if (homeScore == 2 && awayScore == 0 && "2.5".equals(marketEntity.getHandicap())) {
                //比分2-0，2.5小（Under）匹配（60006全场比分）（2-0）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "2-0");
            }
            if (homeScore == 1 && awayScore == 1 && "2.5".equals(marketEntity.getHandicap())) {
                //比分2-0，2.5小（Under）匹配（60006全场比分）（1-1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "1-1");
            }
            if (homeScore == 2 && awayScore == 1 && "3.5".equals(marketEntity.getHandicap())) {
                //比分2-1，3.5小（Under）匹配（60006全场比分）（2-1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "2-1");
            }
            if (homeScore == 1 && awayScore == 2 && "3.5".equals(marketEntity.getHandicap())) {
                //比分1-2，3.5小（Under）匹配（60006全场比分）（1-2）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "1-2");
            }
            if (homeScore == 0 && awayScore == 3 && "3.5".equals(marketEntity.getHandicap())) {
                //比分0-3，3.5小（Under）匹配（60006全场比分）（0-3）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "0-3");
            }
            if (homeScore == 3 && awayScore == 0 && "3.5".equals(marketEntity.getHandicap())) {
                //比分3-0，3.5小（Under）匹配（60006全场比分）（3-0）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "3-0");
            }
            if (homeScore == 3 && awayScore == 1 && "4.5".equals(marketEntity.getHandicap())) {
                //比分3-1，4.5小（Under）匹配（60006全场比分）（3-1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "3-1");
            }
            if (homeScore == 1 && awayScore == 3 && "4.5".equals(marketEntity.getHandicap())) {
                //比分1-3，4.5小（Under）匹配（60006全场比分）（1-3）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "1-3");
            }
            if (homeScore == 2 && awayScore == 2 && "4.5".equals(marketEntity.getHandicap())) {
                //比分2-2，4.5小（Under）匹配（60006全场比分）（2-2）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "2-2");
            }
            if (homeScore == 3 && awayScore == 2 && "5.5".equals(marketEntity.getHandicap())) {
                //比分3-2，5.5小（Under）匹配（60006全场比分）（3-2）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "3-2");
            }
            if (homeScore == 2 && awayScore == 3 && "5.5".equals(marketEntity.getHandicap())) {
                //比分2-3，4.5小（Under）匹配（60006全场比分）（2-3）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "2-3");
            }
            if (homeScore == 3 && awayScore == 3 && "6.5".equals(marketEntity.getHandicap())) {
                //比分3-3，6.5小（Under）匹配（60006全场比分）（3-3）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60006, "3-3");
            }

            if (sum == 1 && "1.5".equals(marketEntity.getHandicap())) {
                //总分为1，1.5小（Under）匹配（60005下半场准确进球数）（1）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60005, "1");
            }
            if (sum == 3 && "3.5".equals(marketEntity.getHandicap())) {
                //总分为3，3.5小（Under）匹配（60005下半场准确进球数）（3）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 60005, "3");
            }
        }
    }

    /**
     * 两队都进球 匹配 {主队}零失球
     * 两队都进球 匹配 {主队}零失球获胜
     * 两队都进球 匹配 {客队}零失球
     * 两队都进球 匹配 {客队}零失球获胜
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void ftBothTeamScoreProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //主玩法投注项Yes
            BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Yes");
            if (homeScore == 1 && awayScore == 0) {
                //比分：1-0，（Yes）匹配（10024{主队}零失球）（No）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10024, "No");
                //比分：1-0，（Yes）匹配（10025{主队}零失球获胜）(No)
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10025, "No");
            } else if (homeScore == 0 && awayScore == 1) {
                //比分：0-1，（Yes）匹配（10029{客队}零失球）(No)
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10029, "No");
                //比分：0-1，（Yes）匹配（10030{客队}零失球获胜）(No)
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10030, "No");

            }
        }
    }

    /**
     * {主队}进球大小 匹配 {主队}准确进球数
     * {主队}进球大小 匹配 {主队}准确进球数
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void ftTeam1OddEvenProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (homeScore == 0 && awayScore == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //比分：0-0，0.5(Under)小
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                //匹配（10017{主队}准确进球数）（0）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10017, "0");
            } else if ("2.5".equals(marketEntity.getHandicap())) {
                //2.5大(Over)
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Over");
                //匹配（10017{主队}准确进球数）（3+）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10017, "3+");
            }
        }
    }

    /**
     * 上半场{主队}进球大小 匹配 上半场{主队}准确进球数
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void half1Team1OverUnderProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (homeScore == 0 && awayScore == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //比分：0-0，0.5(Under)小
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                //匹配（20015上半场{主队}准确进球数）（0）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20015, "0");
            } else if ("2.5".equals(marketEntity.getHandicap())) {
                //2.5大(Over)
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Over");
                //匹配（20015上半场{主队}准确进球数）（3+）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20015, "3+");
            }
        }
    }

    /**
     * {客队}进球大小 匹配 {客队}准确进球数
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void ftTeam2OverUnderProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (homeScore == 0 && awayScore == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //比分：0-0，0.5(Under)小
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                //匹配（10018{客队}准确进球数）（0）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10018, "0");
            } else if ("2.5".equals(marketEntity.getHandicap())) {
                //2.5(Over)大
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Over");
                //匹配（10018{客队}准确进球数）（3+）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 10018, "3+");
            }
        }
    }

    /**
     * /上半场{客队}进球大小 匹配 上半场{客队}准确进球数
     *
     * @param marketsEntity 当前玩法所有盘口
     * @param marketsMap    Map<玩法，list<盘口>>
     * @param goalObj       比分数据
     */
    private void half1Team2OverUnderProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (homeScore == 0 && awayScore == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //比分：0-0，0.5(Under)小
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Under");
                //匹配（20016上半场{客队}准确进球数）（0）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20016, "0");
            } else if ("2.5".equals(marketEntity.getHandicap())) {
                //2.5(Over)大
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("Over");
                //匹配（20016上半场{客队}准确进球数）（3+）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 20016, "3+");
            }
        }
    }

    /**
     * 全场比分 匹配
     * 全场比分 匹配
     * 全场比分 匹配
     * 全场比分 匹配
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void fTCorrectScoreProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if (sum == 0) {
                BetPieceEntity betPieceEntity = betPieceHandicapMap.get("0-0");
                //比分0-0，10008全场比分（0-0）匹配（10039第{X}个进球）（None）
//                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10039, "None");
                //比分0-0，10008全场比分（0-0）匹配（10040第{X}个进球何时发生？）（None）
//                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10040, "None");
                //比分0-0，10008全场比分（0-0）匹配（10022最后进球队伍）（None）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10022, "None");
                //比分0-0，10008全场比分（0-0）匹配（10009净胜分4+）（no goal）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10009, "no goal");
                //比分0-0，10008全场比分（0-0）匹配（10020半/全场比分）（0-0 0-0）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10020, "0-0 0-0");
                //比分0-0，10008全场比分（0-0）匹配（10063任何进球时分(5min)）（NoGoal）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10063, "NoGoal");
                //比分0-0，10008全场比分（0-0）匹配（半/全场 & 准确进球数）（NoGoal）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10062, "draw/draw & 0");
                //比分0-0，10008全场比分（0-0） 匹配（10015准确进球数）（0）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10015, "0");
                //比分0-0，10008全场比分（0-0） 匹配（10058首个进球的半场）（no goal）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10058, "NoGoal");

                //比分0-0，10008全场比分（0-0） 匹配（10034独赢 & 两队都进球）（XAndNo）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10034, "XAndNo");
                //比分0-0，10008全场比分（0-0） 匹配（10033独赢 & 进球大小）（XAndUnder）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 10033, "XAndUnder");
            }
            //比分类型组装
            String oddsType = homeScore + "-" + awayScore;
            //{X}球头
            String x_Handicap = sum + ".5";
            BetPieceEntity betPieceEntity = betPieceHandicapMap.get(oddsType);
            BetPieceEntity betPieceEntity10005 = betPiecesOddsCorrectScoreLinkage(marketsMap, 10005, x_Handicap, "Under");
            //当前波胆比分联动 10039 投注项None ，需判断波胆投注项赔率是否有效，是：联动 ，否：用10005{X.5}小联动，10039投注项None
            if (!betPiecesOddsLinkage(betPieceEntity, marketsMap, 10039, "None")) {
                if (null != betPieceEntity10005) {
                    betPiecesOddsLinkage(betPieceEntity10005, marketsMap, 10039, "None");
                }
            }
            //当前波胆比分联动 10040 投注项 None ，需判断波胆投注项赔率是否有效，是：联动 ，否：用10005{X.5}小联动，10040投注项None
            if (!betPiecesOddsLinkage(betPieceEntity, marketsMap, 10040, "None")) {
                if (null != betPieceEntity10005) {
                    betPiecesOddsLinkage(betPieceEntity10005, marketsMap, 10040, "None");
                }
            }
            //当前波胆比分联动 10064 投注项NoGoal ，需判断波胆投注项赔率是否有效，是：联动 ，否：用10005{X.5}小联动，10064投注项 NoGoal
            if (!betPiecesOddsLinkage(betPieceEntity, marketsMap, 10064, "NoGoal")) {
                if (null != betPieceEntity10005) {
                    betPiecesOddsLinkage(betPieceEntity10005, marketsMap, 10064, "NoGoal");
                }
            }
        }
    }


    /**
     * 半场比分 匹配  上半场准确进球数
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void half1CorrectScoreProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject goal = goalObj.getJSONObject("goal");
        Integer homeScore = goal.getInteger("home");
        Integer awayScore = goal.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if (sum == 0) {
                BetPieceEntity betPieceEntity = betPieceHandicapMap.get("0-0");
                //比分0-0，半场比分(0-0) ,匹配（20011上半场准确进球数）（0）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 20011, "0");

                //比分0-0，半场比分(0-0) ,匹配（20014上半场独赢 & 上半场两队都进球）（XAndNo）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 20014, "XAndNo");
                //比分0-0，半场比分(0-0) ,匹配（20021上半场 独赢 & 进球大小）（XAndUnder）
                betPiecesOddsLinkage(betPieceEntity, marketsMap, 20021, "XAndUnder");
            }
        }
    }

    /**
     * 40002 匹配 40001
     * 全场角球让分 匹配  全场角球独赢
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void cornerHandicapProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject corner = goalObj.getJSONObject("corner");
        Integer homeScore = corner.getInteger("home");
        Integer awayScore = corner.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
            double value = Double.parseDouble(marketEntity.getHandicap()) - (homeScore - awayScore);
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (value == -0.5) {
                //全场盘口值 = -0.5主（1） 匹配（40001全场角球独赢/）1x2主胜（1）
                betPiecesOddsLinkage(betPieceHandicapMap.get("1"), marketsMap, 40001, "1");
            } else if (value == 0.5) {
                //全场盘口值 = 0.5客（2） 匹配（40001全场角球独赢/）1x2客胜（2）
                betPiecesOddsLinkage(betPieceHandicapMap.get("2"), marketsMap, 40001, "2");
            }
        }
    }


    /**
     * 40009 匹配 40008
     * 半场角球让分 匹配  半场角球独赢
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void cornerH1HandicapProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject corner = goalObj.getJSONObject("corner");
        Integer homeScore = corner.getInteger("home");
        Integer awayScore = corner.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
            double value = Double.parseDouble(marketEntity.getHandicap()) - (homeScore - awayScore);
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (value == -0.5) {
                //全场盘口值 = -0.5主（1） 匹配（40001全场角球独赢）1x2主胜（1）
                betPiecesOddsLinkage(betPieceHandicapMap.get("1"), marketsMap, 40008, "1");
            } else if (value == 0.5) {
                //全场盘口值 = 0.5客（2） 匹配（40001全场角球独赢）1x2客胜（2）
                betPiecesOddsLinkage(betPieceHandicapMap.get("2"), marketsMap, 40008, "2");
            }
        }
    }

    /**
     * 40003 全场角球大小 匹配 40014 角球总数区间
     * 40003 全场角球大小 匹配 40018 第{X}个角球
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void cornerTotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject corner = goalObj.getJSONObject("corner");
        Integer homeScore = corner.getInteger("home");
        Integer awayScore = corner.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if ("11.5".equals(marketEntity.getHandicap())) {
                //11.5(Over)大 匹配（40014角球总数区间）（0-8）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 40014, "12+");
            }
            if (sum <= 8 && "8.5".equals(marketEntity.getHandicap())) {
                //8.5(Under)小 匹配（40014角球总数区间）（0-8）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 40014, "0-8");
            }
            if ("0.5".equals(marketEntity.getHandicap())) {
                //0.5(Under)小 匹配（40018 第{X}个角球）（None）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 40018, "None");
            }
        }
    }

    /**
     * 40010 半场角球大小  匹配 40019 上半场角球总数区间
     * 40010 半场角球大小  匹配 40021 上半场第{X}个角球
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void cornerH1TotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject corner = goalObj.getJSONObject("corner");
        Integer homeScore = corner.getInteger("home");
        Integer awayScore = corner.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if ("6.5".equals(marketEntity.getHandicap())) {
                //6.5(Over)大 匹配（40019 上半场角球总数区间）（7+）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 40019, "7+");
            }
            if (sum <= 4 && "4.5".equals(marketEntity.getHandicap())) {
                //4.5(Under)小 匹配（40019 上半场角球总数区间）（0-4）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 40019, "0-4");
            }
            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //0.5(Under)小 匹配（40021 上半场第{X}个角球）（None）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 40021, "None");
            }
        }
    }

    /**
     * 40005 主队全场角球大小 匹配 40015 {主队}角球总数区间
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void cornerTeam1TotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject corner = goalObj.getJSONObject("corner");
        Integer homeScore = corner.getInteger("home");
//        Integer awayScore = corner.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore;
            if ("6.5".equals(marketEntity.getHandicap())) {
                //6.5(Over)大 匹配（40015 {主队} 角球总数区间）（7+）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 40015, "7+");
            }
            if (sum <= 2 && "2.5".equals(marketEntity.getHandicap())) {
                //2.5(Under)小 匹配（40015 {主队} 角球总数区间）（0-2）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 40015, "0-2");
            }
        }
    }

    /**
     * 40006 客队全场角球大小 匹配 40016 {客队}角球总数区间
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void cornerTeam2TotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject corner = goalObj.getJSONObject("corner");
//        Integer homeScore = corner.getInteger("home");
        Integer awayScore = corner.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = awayScore;
            if (sum <= 2 && "2.5".equals(marketEntity.getHandicap())) {
                //2.5(Under)小 匹配（40016 {客队}角球总数区间）（0-2）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 40016, "0-2");
            }
            if ("6.5".equals(marketEntity.getHandicap())) {
                //6.5(Over)大 匹配（40016 {客队}角球总数区间）（7+）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 40016, "7+");
            }
        }
    }


    /**
     * 50002 全场罚牌让分 匹配 50001 全场罚牌独赢
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void bookingHandicapProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject faCard = goalObj.getJSONObject("faCard");
        Integer homeScore = faCard.getInteger("home");
        Integer awayScore = faCard.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
            double value = Double.parseDouble(marketEntity.getHandicap()) - (homeScore - awayScore);
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (-0.5 == value) {
                //-0.5主（1） 匹配（50001 全场罚牌独赢1x2）（1主）
                betPiecesOddsLinkage(betPieceHandicapMap.get("1"), marketsMap, 50001, "1");
            }
            if (0.5 == value) {
                //0.5(2)客  匹配（50001 全场罚牌独赢1x2）（2客）
                betPiecesOddsLinkage(betPieceHandicapMap.get("2"), marketsMap, 50001, "2");
            }
        }
    }

    /**
     * 50006 半场罚牌让分 匹配 50005 半场罚牌独赢
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void bookingH1HandicapProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject faCard = goalObj.getJSONObject("faCard");
        Integer homeScore = faCard.getInteger("home");
        Integer awayScore = faCard.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
            double value = Double.parseDouble(marketEntity.getHandicap()) - (homeScore - awayScore);
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (-0.5 == value) {
                //-0.5主（1） 匹配（50005 半场罚牌独赢1x2）（1主）
                betPiecesOddsLinkage(betPieceHandicapMap.get("1"), marketsMap, 50005, "1");
            }
            if (0.5 == value) {
                //0.5(2)客  匹配（50005 半场罚牌独赢1x2）（2客）
                betPiecesOddsLinkage(betPieceHandicapMap.get("2"), marketsMap, 50005, "2");
            }
        }
    }

    /**
     * 50004 全场罚牌大小 匹配 50015 准确罚牌分数
     * 50004 全场罚牌大小 匹配 50012 第{X}张罚牌
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void bookingTotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject faCard = goalObj.getJSONObject("faCard");
        Integer homeScore = faCard.getInteger("home");
        Integer awayScore = faCard.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //0.5(Under)小 匹配（50012 第{X}张罚牌）（None）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 50012, "None");
            }
            if (sum <= 3 && "3.5".equals(marketEntity.getHandicap())) {
                //3.5(Under)小 匹配（50015 准确罚牌分数）（0-3）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 50015, "0-3");
            }
            if ("11.5".equals(marketEntity.getHandicap())) {
                //11.5(Over)大 匹配（50015 准确罚牌分数）（12+）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 50015, "12+");
            }
        }
    }

    /**
     * 50008 半场罚牌大小 匹配 50020 上半场准确罚牌分数
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void bookingH1TotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject faCard = goalObj.getJSONObject("faCard");
        Integer homeScore = faCard.getInteger("home");
        Integer awayScore = faCard.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //0.5(Under)小 匹配（50020 上半场准确罚牌分数）（0）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 50020, "0");
            }
            if ("5.5".equals(marketEntity.getHandicap())) {
                //5.5(Over)大 匹配（50020 上半场准确罚牌分数）（6+）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 50020, "6+");
            }
        }
    }

    /**
     * 50013 {主队}罚牌大小 匹配 50016 {主队}准确罚牌分数
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void bookingTeam1TotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject faCard = goalObj.getJSONObject("faCard");
        Integer homeScore = faCard.getInteger("home");
//        Integer awayScore = faCard.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore;
            if (sum <= 1 && "1.5".equals(marketEntity.getHandicap())) {
                //1.5(Under)小 匹配（50016 {主队}准确罚牌分数）（0）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 50016, "0-1");
            }
            if ("3.5".equals(marketEntity.getHandicap())) {
                //3.5(Over)大 匹配（50016 {主队}准确罚牌分数）（6+）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 50016, "4+");
            }
        }
    }


    /**
     * 50014 {客队}罚牌大小 匹配 50017 {客队}准确罚牌分数
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void bookingTeam2TotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject faCard = goalObj.getJSONObject("faCard");
//        Integer homeScore = faCard.getInteger("home");
        Integer awayScore = faCard.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = awayScore;
            if (sum <= 1 && "1.5".equals(marketEntity.getHandicap())) {
                //1.5(Under)小 匹配（50017 {客队}准确罚牌分数）（0-1）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 50017, "0-1");
            }
            if ("3.5".equals(marketEntity.getHandicap())) {
                //3.5(Over)大 匹配（50017 {客队}准确罚牌分数）（4+）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 50017, "4+");
            }
        }
    }

    /**
     * 50018 上半场{主队}罚牌大小 匹配 50021 上半场{主队}准确罚牌分数
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void bookingH1Team1TotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject faCard = goalObj.getJSONObject("faCard");
        Integer homeScore = faCard.getInteger("home");
        Integer awayScore = faCard.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //0.5(Under)小 匹配（50021 上半场{主队}准确罚牌分数）（0）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 50021, "0");
            }
            if ("2.5".equals(marketEntity.getHandicap())) {
                //2.5(Over)大 匹配（50021 上半场{主队}准确罚牌分数）（3+）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 50021, "3+");
            }
        }
    }


    /**
     * 50019 上半场{客队}罚牌大小 匹配 50022 上半场{客队}准确罚牌分数
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void bookingH1Team2TotalTotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        JSONObject faCard = goalObj.getJSONObject("faCard");
        Integer homeScore = faCard.getInteger("home");
        Integer awayScore = faCard.getInteger("away");
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //0.5(Under)小 匹配（50022 上半场{客队}准确罚牌分数）（0）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 50022, "0");
            }
            if ("2.5".equals(marketEntity.getHandicap())) {
                //2.5(Over)大 匹配（50022 上半场{客队}准确罚牌分数）（3+）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Over"), marketsMap, 50022, "3+");
            }
        }
    }


    /**
     * 70002 加时赛-让球 匹配 70001 加时赛-独赢
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void overtimeHandicapProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        Integer period = goalObj.getInteger("period");
        Integer homeScore = 0;
        Integer awayScore = 0;
        if (period == 41 || period == 42) {//加时赛上半场 下半场
            JSONObject goal = goalObj.getJSONObject("goal");
            homeScore = goal.getInteger("home");
            awayScore = goal.getInteger("away");
        }
        for (MarketsEntity marketEntity : marketsEntity) {
            //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
            double value = Double.parseDouble(marketEntity.getHandicap()) - (homeScore - awayScore);
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (value == -0.5) {
                //全场盘口值 = -0.5主（1） 匹配（ 70001 加时赛-独赢）1x2主胜（1）
                betPiecesOddsLinkage(betPieceHandicapMap.get("1"), marketsMap, 70001, "1");
            } else if (value == 0.5) {
                //全场盘口值 = 0.5客（2） 匹配（ 70001 加时赛-独赢）1x2客胜（2）
                betPiecesOddsLinkage(betPieceHandicapMap.get("2"), marketsMap, 70001, "2");
            }
        }
    }

    /**
     * 70009 加时赛-上半场让球 匹配 70008 加时赛-上半场独赢
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void overtimeH1HandicapProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        Integer period = goalObj.getInteger("period");
        Integer homeScore = 0;
        Integer awayScore = 0;
        if (period == 41 || period == 42) {//加时赛上半场 下半场
            JSONObject goal = goalObj.getJSONObject("goal");
            homeScore = goal.getInteger("home");
            awayScore = goal.getInteger("away");
        }
        for (MarketsEntity marketEntity : marketsEntity) {
            //全场盘口值 = 基准分盘口值 - (主队比分 - 客队比分)
            double value = Double.parseDouble(marketEntity.getHandicap()) - (homeScore - awayScore);
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            if (value == -0.5) {
                //全场盘口值 = -0.5主（1） 匹配（ 70008 加时赛-上半场独赢）1x2主胜（1）
                betPiecesOddsLinkage(betPieceHandicapMap.get("1"), marketsMap, 70008, "1");
            } else if (value == 0.5) {
                //全场盘口值 = 0.5客（2） 匹配（ 70008 加时赛-上半场独赢）1x2客胜（2）
                betPiecesOddsLinkage(betPieceHandicapMap.get("2"), marketsMap, 70008, "2");
            }
        }
    }


    /**
     * 70003 加时赛-大小 匹配 70005 加时赛-正确比分
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void overtimeTotalProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        Integer period = goalObj.getInteger("period");
        Integer homeScore = 0;
        Integer awayScore = 0;
        if (period == 41 || period == 42) {//加时赛上半场 下半场
            JSONObject goal = goalObj.getJSONObject("goal");
            homeScore = goal.getInteger("home");
            awayScore = goal.getInteger("away");
        }
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if (sum == 0 && "0.5".equals(marketEntity.getHandicap())) {
                //0.5(Under)小 匹配（70005 加时赛-正确比分）（0-0）
                betPiecesOddsLinkage(betPieceHandicapMap.get("Under"), marketsMap, 70005, "0-0");
            }
        }
    }


    /**
     * 70005 加时赛-正确比分 匹配 70006 加时赛是否进球
     * 70005 加时赛-正确比分 匹配 70007 加时赛-第{X}个进球
     *
     * @param marketsEntity
     * @param marketsMap
     * @param goalObj       比分数据
     */
    private void overtimeCorrectScoreProcessor(List<MarketsEntity> marketsEntity, Map<Integer, List<MarketsEntity>> marketsMap, JSONObject goalObj) {
        Integer period = goalObj.getInteger("period");
        Integer homeScore = 0;
        Integer awayScore = 0;
        if (period == 41 || period == 42) {//加时赛上半场 下半场
            JSONObject goal = goalObj.getJSONObject("goal");
            homeScore = goal.getInteger("home");
            awayScore = goal.getInteger("away");
        }
        for (MarketsEntity marketEntity : marketsEntity) {
            //主玩法投注项类型分组
            Map<String, BetPieceEntity> betPieceHandicapMap = betPiecesGroupingByOddsType(marketEntity.getBetPieceEntities());
            //比分总分
            int sum = homeScore + awayScore;
            if (sum == 0) {
                BetPieceEntity mainBetPieceEntity = betPieceHandicapMap.get("0-0");
                //0-0 匹配（70006 加时赛是否进球）（No）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 70006, "No");
                //0-0 匹配（70007 加时赛-第{X}个进球）（None）
                betPiecesOddsLinkage(mainBetPieceEntity, marketsMap, 70007, "None");
            }
        }

    }


    /**
     * 当前比分
     *
     * @param aoMatchId
     */
    public JSONObject nowScore(String linkId, String aoMatchId) {
        Integer period = supportGetScorePeriod(aoMatchId);
        JSONObject initScore = initScore();
        Object aoScoreObj = redisService.hGet(AO_ESPORT_SCORE_KEY, aoMatchId + "_" + period);
        //查询Redis当前比分
        if (aoScoreObj != null) {
            JSONObject goalObj = JSONObject.parseObject(aoScoreObj.toString()).getJSONObject("goal");
            if (null != goalObj) {
                JSONObject goal = initScore.getJSONObject("goal");
                goal.put("home", goalObj.containsKey("home") ? goalObj.getInteger("home") : 0);
                goal.put("away", goalObj.containsKey("away") ? goalObj.getInteger("away") : 0);
            }
            //角球
            JSONObject cornerObj = JSONObject.parseObject(aoScoreObj.toString()).getJSONObject("corner");
            if (null != cornerObj) {
                JSONObject corner = initScore.getJSONObject("corner");
                corner.put("home", cornerObj.containsKey("home") ? cornerObj.getInteger("home") : 0);
                corner.put("away", cornerObj.containsKey("away") ? cornerObj.getInteger("away") : 0);
            }
            //罚牌
            JSONObject faCardObj = JSONObject.parseObject(aoScoreObj.toString()).getJSONObject("faCard");
            if (null != faCardObj) {
                JSONObject faCard = initScore.getJSONObject("faCard");
                faCard.put("home", faCardObj.containsKey("home") ? faCardObj.getInteger("home") : 0);
                faCard.put("away", faCardObj.containsKey("away") ? faCardObj.getInteger("away") : 0);
            }
        }
        initScore.put("period", period);
        log.info("::{}::赔率联动,AO赛事ID:{},当前阶段：{}，当前比分原数据:{},处理后比分数据:{}", linkId, aoMatchId, period, aoScoreObj, initScore.toJSONString());
        return initScore;
    }

    private JSONObject initScore() {
        return JSONObject.parseObject("{ \"goal\":{\"away\":0,\"home\":0}, \"corner\":{\"away\":0,\"home\":0}, \"faCard\":{\"away\":0,\"home\":0}}");
    }

    public Integer supportGetScorePeriod(String aoMatchId) {
        Integer period = supportGetPeriod(aoMatchId);
        if (period == 31) {
            period = 6;
        } else if (period == 33) {
            period = 41;
        }
        return period;
    }

    public Integer supportGetPeriod(String aoMatchId) {
        Object matchTimer = redisService.hGet(AO_ESPORT_BGTIME_KEY, aoMatchId);
        if (null != matchTimer) {
            String[] prid = matchTimer.toString().split("#");
            return Integer.valueOf(prid[1]);
        }
        return 0;
    }

    /**
     * 投注项分组
     *
     * @param marketEntity 当前玩法所有盘口
     */
    private Map<String, BetPieceEntity> marketEntityByOddsType(List<MarketsEntity> marketEntity) {
        return marketEntity.stream().flatMap(market -> market.getBetPieceEntities().stream()).collect(Collectors.toMap(e -> e.getOddsType(), e -> e, (oldValue, newValue) -> newValue));
    }

    /**
     * 投注项分组
     *
     * @param betPieceEntities 盘口投注项
     */
    private Map<String, BetPieceEntity> betPiecesGroupingByOddsType(List<BetPieceEntity> betPieceEntities) {
        return betPieceEntities.stream().collect(Collectors.toMap(e -> e.getOddsType(), e -> e, (oldValue, newValue) -> newValue));
    }


    /**
     * 赔率联动赋值
     *
     * @param mainBetPieceEntity   主玩法盘口
     * @param marketsMap           玩法分组
     * @param secondaryCategoryIds 次要玩法
     * @param secondaryOddsType    次要投注项
     */
    private Boolean betPiecesOddsLinkage(BetPieceEntity mainBetPieceEntity, Map<Integer, List<MarketsEntity>> marketsMap, Integer secondaryCategoryIds, String secondaryOddsType) {
        List<MarketsEntity> secondaryMarketsEntities = marketsMap.get(secondaryCategoryIds);
        if (null != mainBetPieceEntity && 0 != mainBetPieceEntity.getAoOddsValue() && null != secondaryMarketsEntities) {
            BetPieceEntity secondaryBetPieceEntity = marketEntityByOddsType(secondaryMarketsEntities).get(secondaryOddsType);
            if (null != secondaryBetPieceEntity) {
                secondaryBetPieceEntity.setAoOddsValue(mainBetPieceEntity.getAoOddsValue());
                if (HT_FT_CATEGORY_IDS.contains(secondaryCategoryIds)) {
                    secondaryBetPieceEntity.setOdds(mainBetPieceEntity.getOdds());
                }
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 获取盘口投注项
     *
     * @param marketsMap           盘口分组
     * @param secondaryCategoryIds 玩法
     * @param x_Handicap           盘口值
     * @param secondaryOddsType    投注项类型
     * @return
     */
    private BetPieceEntity betPiecesOddsCorrectScoreLinkage(Map<Integer, List<MarketsEntity>> marketsMap, Integer secondaryCategoryIds, String x_Handicap, String secondaryOddsType) {
        List<MarketsEntity> marketsEntitiesNew = marketsMap.get(secondaryCategoryIds);
        List<MarketsEntity> handicapMarketsEntityNew = marketsEntitiesNew.stream().filter(m -> m.getHandicap().equals(x_Handicap)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(handicapMarketsEntityNew)) {
            Map<String, BetPieceEntity> betPieceHandicapMapNew = betPiecesGroupingByOddsType(handicapMarketsEntityNew.get(0).getBetPieceEntities());
            return betPieceHandicapMapNew.get(secondaryOddsType);
        }
        return null;
    }

    /**
     * 最大最小赔率，超过最大或者最小赔率以
     *
     * @param linkId
     * @param aoMatchId
     * @param marketsList
     * @param marketMaxMinOddsConfigMap 最大最小赔率配置
     */
    public void marketOddsVerify(String linkId, String aoMatchId, Long sportId, List<MarketsEntity> marketsList, Map<String, TradeMarketItemConfig> marketMaxMinOddsConfigMap) {
        if (CollectionUtils.isEmpty(marketMaxMinOddsConfigMap)) {
            log.info("::{}::AO赛事ID:{},最大最小配置配置不存在.", linkId, aoMatchId);
            return;
        }
        Set<String> maxMinOddsCategoryId = marketMaxMinOddsConfigMap.keySet();
        log.info("::{}::AO赛事ID:{},最大最小配置配置玩法:{}", linkId, aoMatchId, maxMinOddsCategoryId);
        marketsList.stream().forEach(market -> {
            Integer marketId = market.getMarketId();
            TradeMarketItemConfig tradeMarketItemConfig = marketMaxMinOddsConfigMap.get(marketId + "_" + market.getOrder());
            if (null == tradeMarketItemConfig) {
                tradeMarketItemConfig = marketMaxMinOddsConfigMap.get(marketId + "_1");
            }
            if (null == tradeMarketItemConfig) {
//                log.info("::{}::AO赛事ID:{},玩法ID:{},最大最小赔率未配置1.", linkId, aoMatchId, marketId);
                return;
            }
            Double max = null == tradeMarketItemConfig.getMaxOddsValue() ? 0D : tradeMarketItemConfig.getMaxOddsValue();
            Double min = null == tradeMarketItemConfig.getMinOddsValue() ? 0D : tradeMarketItemConfig.getMinOddsValue();
            if (0 == max || 0 == min) {
//                log.info("::{}::AO赛事ID:{},玩法ID:{},最大最小赔率未配置.", linkId, aoMatchId, marketId);
                return;
            }
            //投注项最大最小赔率校验
            List<BetPieceEntity> betPieceEntities = market.getBetPieceEntities();
            if (!CollectionUtils.isEmpty(betPieceEntities)) {
                betPieceEntities.forEach(bet -> {
                    Double aoOddsValue = bet.getAoOddsValue();
                    if (aoOddsValue != 0) {
                        if (aoOddsValue >= max) {
                            bet.setAoOddsValue(max);
//                            log.info("::{}::AO赛事ID:{},玩法ID:{},AoOddsValue赔率:{},大于最大配置下发最大配置赔率:{}", linkId, aoMatchId, marketId, aoOddsValue, max);
                        }
                        if (aoOddsValue <= min) {
                            bet.setAoOddsValue(min);
//                            log.info("::{}::AO赛事ID:{},玩法ID:{},AoOddsValue赔率:{},小于最小配置下发最小配置赔率:{}", linkId, aoMatchId, marketId, aoOddsValue, min);
                        }
                    }
                });
            }
        });
    }

    /**
     * 全场独赢、上半场独赢
     * 当某个1x2多个投注项被联动之后，只保留发生概率大的1个赔率联动（原始赔率低的那个），另外的投注项不做联动
     * <p>
     * 进球1x2：10001/20001
     * 角球1x2：40001/40008
     * 罚牌1x2：50001/50005
     * 加时进球1x2：70001/70008
     * <p>
     * 联动前与联动后的投注项赔率进行对比，有多个联动只联动最小赔率，其他改为未联动前赔率
     *
     * @param linkageMarketsBefore 联动前
     * @param linkageMarketsAfter  联动后
     */

    private void ftHt1X2Processor(List<MarketsEntity> linkageMarketsBefore, List<MarketsEntity> linkageMarketsAfter) {
        //过滤需要处理的玩法
        Map<Integer, MarketsEntity> linkageMarketsBeforeMap = linkageMarketsBefore.stream().filter(marketsEntity -> HT_FT_CATEGORY_IDS.contains(marketsEntity.getMarketId())).collect(Collectors.toMap(MarketsEntity::getMarketId, e -> e, (oldValue, newValue) -> newValue));
        Map<Integer, MarketsEntity> linkageMarketsAfterMap = linkageMarketsAfter.stream().filter(marketsEntity -> HT_FT_CATEGORY_IDS.contains(marketsEntity.getMarketId())).collect(Collectors.toMap(MarketsEntity::getMarketId, e -> e, (oldValue, newValue) -> newValue));
        for (Map.Entry<Integer, MarketsEntity> marketBeforeMap : linkageMarketsBeforeMap.entrySet()) {
            Integer marketId = marketBeforeMap.getKey();
            MarketsEntity marketsEntityBefore = marketBeforeMap.getValue();
            MarketsEntity marketsEntityAfter = linkageMarketsAfterMap.get(marketId);
            if (null == marketsEntityAfter) {
                continue;
            }
            //联动前联动后投注项分组
            Map<String, BetPieceEntity> betPieceEntityBeforeMap = marketsEntityBefore.getBetPieceEntities().stream().collect(Collectors.toMap(BetPieceEntity::getOddsType, e -> e, (oldValue, newValue) -> newValue));
            Map<String, BetPieceEntity> betPieceEntityAfterMap = marketsEntityAfter.getBetPieceEntities().stream().collect(Collectors.toMap(BetPieceEntity::getOddsType, e -> e, (oldValue, newValue) -> newValue));

            //home联动后原始赔率  小于 away联动前原始赔率 away为不联动，不联动需要把联动后的赔率 改为未联动前的赔率
            Double aoOddsValueAfterHome = Double.valueOf(betPieceEntityAfterMap.get("1").getOdds());
            //away联动前赔率
            Double aoOddsValueAway = Double.valueOf(betPieceEntityBeforeMap.get("2").getOdds());
            if (aoOddsValueAfterHome < aoOddsValueAway) {
                BetPieceEntity betPieceEntity = betPieceEntityAfterMap.get("2");
                betPieceEntity.setAoOddsValue(aoOddsValueAway);
            }
        }
    }

    /**
     * 查询map 最小的值 对应 key
     *
     * @param oddsAfterMap
     * @return
     */
    private Map<String, Double> findMInMap(Map<String, Double> oddsAfterMap) {
        Double value = oddsAfterMap.values().stream().sorted().findFirst().get();
        List<String> keyList = oddsAfterMap.entrySet().stream().filter(e -> value.equals(e.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());
        Map<String, Double> minMap = new HashMap<>();
        minMap.put(keyList.get(0), oddsAfterMap.get(keyList.get(0)));
        return minMap;
    }

    /**
     * 计算盘口赔率实时margin
     *
     * @param linkageMarkets
     */
    private void calculateMarketOddsMargin(List<MarketsEntity> linkageMarkets) {
        for (MarketsEntity market : linkageMarkets) {
            double realTimeMargin = 0;
            for (BetPieceEntity betOdds : market.getBetPieceEntities()) {
                Double aoOddsValue = betOdds.getAoOddsValue();
                if (0 == aoOddsValue) {
                    continue;
                }
                realTimeMargin += 1 / aoOddsValue;
            }
            market.setRealTimeMargin(BigDecimalUtils.scale(realTimeMargin * 100, 2));
        }
    }

    /**
     * 1.只处理三项以上的盘口
     * 2.对比margin是否一致,联动后盘口margin 小于 联动前进行赔率计算
     *
     * @param linkageMarketsBefore 联动前盘口信息
     * @param linkageMarketsAfter  联动后盘口信息
     */

    private void marketMarginCompare(String linkId, String aoMatchId, List<MarketsEntity> linkageMarketsBefore, List<MarketsEntity> linkageMarketsAfter) {
        Map<String, MarketsEntity> marketsBeforeMap = linkageMarketsBefore.stream().filter(m -> 0 != m.getRealTimeMargin()).collect(Collectors.toMap(m -> m.getMarketId() + "_" + m.getOrder(), x -> x, (k1, k2) -> k1));
        Map<String, MarketsEntity> marketsAfterMap = linkageMarketsAfter.stream().filter(m -> 0 != m.getRealTimeMargin()).collect(Collectors.toMap(m -> m.getMarketId() + "_" + m.getOrder(), x -> x, (k1, k2) -> k1));
        if (MapUtils.isEmpty(marketsBeforeMap) || MapUtils.isEmpty(marketsAfterMap)) {
            return;
        }
        marketsBeforeMap.values().forEach(marketBefore -> {
            Integer marketId = marketBefore.getMarketId();
            MarketsEntity marketAfter = marketsAfterMap.get(marketId + "_" + marketBefore.getOrder());
            if (null == marketAfter) {
                return;
            }
            //对比margin是否一致，联动后的赔率小于联动前赔率
            if (!marketBefore.getRealTimeMargin().equals(marketAfter.getRealTimeMargin()) && marketAfter.getRealTimeMargin() < marketBefore.getRealTimeMargin()) {
                //计算出联动后所有赔率概率
                double marginProdAfter = 0;
                Map<String, BetPieceEntity> betPieceAfterMap = marketAfter.getBetPieceEntities().stream().collect(Collectors.toMap(e -> e.getBetPriceId(), e -> e, (oldValue, newValue) -> newValue));
                List<BetPieceEntity> betPieceBeforeList = marketBefore.getBetPieceEntities();
                for (BetPieceEntity betPieceBefore : betPieceBeforeList) {
                    String betPriceId = betPieceBefore.getBetPriceId();
                    BetPieceEntity betPieceAfter = betPieceAfterMap.get(betPriceId);
                    //联动后的赔率
                    if (marketId == 10064) {
                        if (!betPieceBefore.getAoOddsValue().equals(betPieceAfter.getAoOddsValue()) && betPieceBefore.getAoOddsValue() != 0D) {
                            marginProdAfter += 1 / betPieceAfter.getAoOddsValue();
                        }
                    } else {
                        if (!betPieceBefore.getAoOddsValue().equals(betPieceAfter.getAoOddsValue()) && betPieceBefore.isActive()) {
                            marginProdAfter += 1 / betPieceAfter.getAoOddsValue();
                        }
                    }

                }
                if (0 == marginProdAfter || Double.isInfinite(marginProdAfter)) {
                    return;
                }
                log.info("::{}::,AO赛事ID:{},联动后实时margin不匹配,玩法ID:{}_{},marginProdAfter:{}", linkId, aoMatchId, marginProdAfter, marketBefore.getMarketId(), marketBefore.getOrder());
                //计算未联动赔率
                //margin1 = 设定的margin  - (1/联动1+1/联动2…..)
                //margin2=联动后的margin  -（1/联动1+1/联动2…..）
                Double margin1 = (marketBefore.getRealTimeMargin() / 100) - marginProdAfter;
                Double margin2 = (marketAfter.getRealTimeMargin() / 100) - marginProdAfter;
                //新未联动赔率 = 未联动赔率*margin2 / margin1
                for (BetPieceEntity betPieceBefore : betPieceBeforeList) {
                    String betPriceId = betPieceBefore.getBetPriceId();
                    BetPieceEntity betPieceAfter = betPieceAfterMap.get(betPriceId);
                    //未联动的赔率计算
                    if (marketId == 10064) {
                        if (betPieceBefore.getAoOddsValue().equals(betPieceAfter.getAoOddsValue()) && betPieceBefore.getAoOddsValue() != 0D) {
                            betPieceAfter.setAoOddsValue(BigDecimalUtils.scale(betPieceAfter.getAoOddsValue() * margin2 / margin1, 2));
                        }
                    } else {
                        if (betPieceBefore.getAoOddsValue().equals(betPieceAfter.getAoOddsValue()) && betPieceBefore.isActive()) {
                            betPieceAfter.setAoOddsValue(BigDecimalUtils.scale(betPieceAfter.getAoOddsValue() * margin2 / margin1, 2));
                        }
                    }

                }
            }
        });
    }
}