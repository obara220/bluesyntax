package com.panda.aoodds.esport.common.calculate;

import com.alibaba.fastjson.JSONObject;

import com.panda.aoodds.esport.api.entity.BetPieceEntity;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.config.InitializeComponent;
import com.panda.aoodds.esport.common.config.ThreadPoolConfig;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.*;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.market.SelectionTemplate;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.common.utils.BigDecimalUtils;
import com.panda.aoodds.esport.common.utils.MatchInLiveUtil;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.calculate.BkEsportCorrectScoreMarketCalculateProcess.DOUBLE_CHANCE;


/**
 * 赔率计算
 */
@Slf4j
@Component
public class BkEsportCalculateOdds {

    @Autowired
    private InitializeComponent initializeComponent;

    @Autowired
    private AutoDiffCountMarketMalay autoDiffCountMarketMalay;

    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BkEsportMarketOddsLinkageNew bkEsportMarketOddsLinkageNew;

    @Autowired
    private BkEsportMyCalculationMarketProcessor myCalculationMarketProcessor;

    @Autowired
    private MatchInLiveUtil matchInLiveUtil;

    @Autowired
    private ThreadPoolConfig threadPoolConfig;

    @Autowired
    private BkEsportCorrectScoreMarketCalculateProcess bkEsportCorrectScoreMarketCalculateProcess;



    /**
     * AO 赔率计算 ,使用融合下发的水差 和 margin 计算
     *
     * @param linkId
     * @param aoMatchId
     * @param isManualSocre 是否手工比分 0否：1是
     * @param marketsList
     */
    public void calculate(String linkId, String aoMatchId, String isManualSocre, List<MarketsEntity> marketsList, Long sportId) {
        Map<Integer, List<MarketsEntity>> marketsEntityMap = marketsList.stream().collect(Collectors.groupingBy(MarketsEntity::getMarketId));
        if (MapUtils.isEmpty(marketsEntityMap)) {
            return;
        }
        StopWatch swConfig = new StopWatch(UUID.randomUUID().toString());
        swConfig.start("查询赔率计算配置耗时");
        //AO赛事  0=未开赛、1=滚球 融合配置状态是相反的（0=滚球、1=赛前）
        Integer marketType = matchInLiveUtil.matchInLive(aoMatchId, sportId) == 1 ? 0 : 1;
        //获取赛事全部坑位margin
        Map<String, MarginPlaceConfig> marginPlaceConfigMap = new HashMap<>();
        Query marginQuery = Query.query(Criteria.where("aoMatchId").is(aoMatchId).and("marketType").is(marketType));
        List<MarginPlaceConfig> marginPlaceConfigs = aoProducerMongoTemp.find(marginQuery, MarginPlaceConfig.class);
        if (!CollectionUtils.isEmpty(marginPlaceConfigs)) {
            marginPlaceConfigMap = marginPlaceConfigs.stream().collect(Collectors.toMap(m -> m.getChildStandardCategoryId() + "_" + m.getPlaceNum(), x -> x, (k1, k2) -> k1));
        }
        log.info("::{}::,AO赛事ID:{},滚球标识：{}", linkId, aoMatchId, marketType);
        //查询赛事玩法特殊抽水配置
        Map<Long, CategorySellConfigEntity> sellConfigEntityMap = new HashMap<>();
        Query querySpecial = Query.query(Criteria.where("aoMatchInfoId").is(aoMatchId).and("marketType").is(marketType));
        List<CategorySellConfigEntity> categorySellConfigEntities = aoProducerMongoTemp.find(querySpecial, CategorySellConfigEntity.class, CommonConstant.CATEGORY_SELL_CONFIG);
        if (!CollectionUtils.isEmpty(categorySellConfigEntities)) {
            sellConfigEntityMap = categorySellConfigEntities.stream().collect(Collectors.toMap(CategorySellConfigEntity::getAoCategoryId, a -> a, (k1, k2) -> k1));
        }
        Map<String, TradeMarketItemConfig> marketMarginAndMaxMinOddsConfigMap = getMarginAndMaxMinOddsByAoMatchId(linkId, aoMatchId);
        MatchTemplateLinerMarginConfig matchTemplateLinerMarginConfig = aoProducerMongoTemp.findOne(Query.query(Criteria.where("id").is(aoMatchId)), MatchTemplateLinerMarginConfig.class);
        log.info("::{}::matchTemplateLinerMarginConfig:{},", linkId, JSONObject.toJSONString(matchTemplateLinerMarginConfig));
        swConfig.stop();

        //异步循环执行
        List<CompletableFuture<?>> futures = new ArrayList<>();
        TaskExecutor taskExecutor = threadPoolConfig.notifyMyCalculateMarketOddsMessage();
        log.info("::{}::,AO赛事ID:{},准备开始抽水计算,", linkId, aoMatchId);
        for (Map.Entry<Integer, List<MarketsEntity>> entry : marketsEntityMap.entrySet()) {
            MarketCategory marketCategory = MarketCategory.getMarketCategoryById(entry.getKey());
            if (null == marketCategory) {
                continue;
            }
            String calculateType = marketCategory.getCalculateType();
            List<MarketsEntity> marketDataMessages = entry.getValue();
            log.info("::{}::,AO赛事ID:{},开始抽水计算,玩法ID：{},条数：{}", linkId, aoMatchId, entry.getKey(), marketDataMessages.size());
            Map<String, MarginPlaceConfig> finalMarginPlaceConfigMap = marginPlaceConfigMap;
            Map<Long, CategorySellConfigEntity> finalSellConfigEntityMap = sellConfigEntityMap;
            futures.add(CompletableFuture.supplyAsync(() -> {
                for (MarketsEntity marketDataMessage : marketDataMessages) {
                    //盘口计算
                    String marketIdStr = marketDataMessage.getMarketId() + "_" + marketDataMessage.getHandicap() + "_" + marketDataMessage.getMarketName();
                    if ("MY".equals(calculateType)) {
                        //新抽水逻辑
                        boolean isOk = myCalculationMarketProcessor.calculationMarketAuto(linkId, aoMatchId, marketDataMessage, marketType, marketIdStr, finalMarginPlaceConfigMap);
                        if (!isOk) {
                            continue;
                        }
                        //最终的马来赔转欧赔
                        for (BetPieceEntity marketOdds : marketDataMessage.getBetPieceEntities()) {
                            //设置最终的paOddsValue，将马来赔转欧赔后，计算赔率差绝对值
                            Double aoOddsValue = initializeComponent.getConvertMalayToEurope(marketOdds.getMalayOddsValue());
                            marketOdds.setAoOddsValue(aoOddsValue);
                        }
                        //特殊抽水计算
                        standardMarketPumping(linkId, aoMatchId, Long.valueOf(entry.getKey()), marketIdStr, finalSellConfigEntityMap, marketDataMessage);
                    } else {
                        String key = marketDataMessage.getMarketId() + "_" + marketDataMessage.getOrder();
                        //获取模板margin
                        TradeMarketItemConfig tradeMarketItemConfig = marketMarginAndMaxMinOddsConfigMap.get(key);
                        if (null == tradeMarketItemConfig) {
                            tradeMarketItemConfig = marketMarginAndMaxMinOddsConfigMap.get(marketDataMessage.getMarketId() + "_1");
                        }
                        //margin计算
                        if (DOUBLE_CHANCE.contains(marketDataMessage.getMarketId())) {
                            bkEsportCorrectScoreMarketCalculateProcess.marginCalculateFootball(linkId, aoMatchId, tradeMarketItemConfig, marketDataMessage, marketIdStr, matchTemplateLinerMarginConfig);
                        } else {
                            marginCalculateBasketball(linkId, aoMatchId, tradeMarketItemConfig, marketDataMessage, marketIdStr);
                        }
                    }
                }
                return null;
            }, taskExecutor));
            log.info("::{}::,AO赛事ID:{},开始抽水计算,结束，玩法ID：{}", linkId, aoMatchId, entry.getKey());
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("::{}::,AO赛事ID:{},准备开始抽水计算,结束,", linkId, aoMatchId);
        swConfig.start("联动赔率计算耗时");
        //赔率联动
        bkEsportMarketOddsLinkageNew.calculateOddsLinkage(linkId, aoMatchId, sportId, marketsList, marketMarginAndMaxMinOddsConfigMap, isManualSocre);
        swConfig.stop();
        log.info("::{}::AO赛事ID:{},赔率计算耗时:{}ms," + swConfig.prettyPrint(), linkId, aoMatchId, swConfig.getTotalTimeMillis());
    }


    /**
     * 抽水赔率
     * * 计算方式：抽水赔率转马来赔 + 水差
     *
     * @param linkId
     * @param standardMarketDataMessage
     * @return
     */
    public boolean calculationMarketAutoPa(String linkId, StandardMarketDataMessage standardMarketDataMessage, String aoMatchInfoId) {
        //转换统一盘口ID
        String addition1 = standardMarketDataMessage.getAddition1();
        //判断下盘
        Double marketValue = 0D;
        if (StringUtils.isNotBlank(standardMarketDataMessage.getAddition1()) && MarketCategory.AO_CONVERT_STANDARD_CATEGORY_MY.containsValue(standardMarketDataMessage.getMarketCategoryId())) {
            marketValue = Double.parseDouble(standardMarketDataMessage.getAddition1());
        }
        StringBuffer stringBuffer = new StringBuffer();
        Double diffValue = 0D;
        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardMarketDataMessage.getMarketOddsList()) {
            if (null == standardSportMarketOdds.getOriginalOddsValue() || 0 == standardSportMarketOdds.getOriginalOddsValue()) {
                log.error("::{}::AO赛事ID:{},盘口:{},统一盘口id:{},三方盘口源id:{},盘口赔率不正常，计算失败。三方投注项id:{}", linkId, aoMatchInfoId, standardMarketDataMessage.getId(), addition1, standardMarketDataMessage.getThirdMarketSourceId(), standardSportMarketOdds.getThirdOddsFieldSourceId());
                standardMarketDataMessage.setRemark("盘口赔率不正常，原始赔率为空，计算失败，盘口关盘");
                return false;
            }
            //计算下盘
            if (standardSportMarketOdds.getOddsType().equals("Under") || (marketValue > 0 && standardSportMarketOdds.getOddsType().equals("1")) || (marketValue <= 0 && standardSportMarketOdds.getOddsType().equals("2"))) {
                //抽水转换赔率为马来赔
                Double originalOddsValue = subDoubleTwo(BigDecimal.valueOf(standardSportMarketOdds.getOriginalOddsValue()).divide(new BigDecimal(Double.toString(100000))).doubleValue());
                standardSportMarketOdds.setMalayOddsValue(InitializeComponent.getEuropeConvertMalayMap().get(originalOddsValue));
                stringBuffer.append("AO赛事ID:" + aoMatchInfoId + ",下盘统一投注项id:" + standardSportMarketOdds.getRelationMarketOddsId() + ",下盘投注项类型:" + standardSportMarketOdds.getOddsType());
                stringBuffer.append(",下盘投注项原始赔率:" + standardSportMarketOdds.getOriginalOddsValue() + ",下盘投注项马来赔:" + standardSportMarketOdds.getMalayOddsValue());

                //获取盘口差
                Double marketAutoDiffTrade = standardSportMarketOdds.getMarketDiffValue();
                if (null != marketAutoDiffTrade && 0 != marketAutoDiffTrade) {
                    stringBuffer.append(",下盘口水差:" + marketAutoDiffTrade);
                    Double finalMalayOddsValue = autoDiffCountMarketMalay.arithmeticMALAY(marketAutoDiffTrade, 0D, standardSportMarketOdds.getMalayOddsValue(), false);
                    //如果计算失败
                    if (0 == finalMalayOddsValue) {
                        log.error("::{}::,下盘口赔率不正常，水差计算失败。盘口:{},信息:{}", linkId, addition1, stringBuffer.toString());
                        standardMarketDataMessage.setRemark("盘口赔率不正常，水差计算失败，盘口关盘");
                        return false;
                    }
                    standardSportMarketOdds.setMalayOddsValue(finalMalayOddsValue);
                    stringBuffer.append(",抽水计算后的下盘马来赔率:" + standardSportMarketOdds.getMalayOddsValue());
                    //设置水差
                    standardSportMarketOdds.setMarketDiffValue(marketAutoDiffTrade);
                    diffValue = marketAutoDiffTrade;
                } else {
                    stringBuffer.append(",下盘投注项:" + standardSportMarketOdds.getOddsType() + ",盘口水差不存在。");
                }
            }
        }
        //计算上盘
        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardMarketDataMessage.getMarketOddsList()) {
            if (standardSportMarketOdds.getOddsType().equals("Under") || (marketValue > 0 && standardSportMarketOdds.getOddsType().equals("1")) || (marketValue <= 0 && standardSportMarketOdds.getOddsType().equals("2"))) {
            } else {
                //抽水转换赔率为马来赔
                Double originalOddsValue = subDoubleTwo(BigDecimal.valueOf(standardSportMarketOdds.getOriginalOddsValue()).divide(new BigDecimal(Double.toString(100000))).doubleValue());
                standardSportMarketOdds.setMalayOddsValue(InitializeComponent.getEuropeConvertMalayMap().get(originalOddsValue));
                stringBuffer.append(",上盘统一投注项id:" + standardSportMarketOdds.getRelationMarketOddsId() + ",上盘投注项类型:" + standardSportMarketOdds.getOddsType());
                stringBuffer.append(",上盘投注项原始赔率:" + standardSportMarketOdds.getOriginalOddsValue() + ",上盘投注项马来赔:" + standardSportMarketOdds.getMalayOddsValue());
                if (diffValue != 0D) {
                    Double finalMalayOddsValue = autoDiffCountMarketMalay.arithmeticMALAY(diffValue, 0D, standardSportMarketOdds.getMalayOddsValue(), true);
                    if (0 == finalMalayOddsValue) {
                        log.error("::{}::上盘口赔率不正常，计算失败。盘口ID:{},信息:{}", linkId, addition1, stringBuffer.toString());
                        standardMarketDataMessage.setRemark("盘口赔率不正常，水差计算失败，盘口关盘");
                        return false;
                    }
                    standardSportMarketOdds.setMalayOddsValue(finalMalayOddsValue);
                    stringBuffer.append(",抽水计算后的上盘马来赔率:" + standardSportMarketOdds.getMalayOddsValue());
                }
            }
        }
        log.info("::{}::计算成功。盘口:{},信息:{}", linkId, addition1, stringBuffer.toString());
        return true;
    }


    public static double subDoubleTwo(double d) {
        DecimalFormat dFormat = new DecimalFormat();
        dFormat.setMaximumFractionDigits(2);
        dFormat.setGroupingSize(0);
        dFormat.setRoundingMode(RoundingMode.FLOOR);
        return Double.parseDouble(dFormat.format(d));
    }

    /**
     * 特殊抽水
     * 1.判断玩法是否开启特殊抽水
     * 2.区间匹配赔率：赔率最小作为下盘，相等取下盘投注项赔率，最终下盘PA赔率 + 区间赔率 为最终上盘PA赔率
     *
     * @param linkId
     * @param aoMatchId           AO赛事ID
     * @param aoCategoryId        AO玩法ID
     * @param sellConfigEntityMap 特殊抽水
     * @param marketDataMessage   AO盘口
     */
    private void standardMarketPumping(String linkId, String aoMatchId, Long aoCategoryId, String marketIdStr, Map<Long, CategorySellConfigEntity> sellConfigEntityMap, MarketsEntity marketDataMessage) {
        // step1
        CategorySellConfigEntity marketCategorySell = sellConfigEntityMap.get(aoCategoryId);
        if (marketCategorySell != null && marketCategorySell.getIsSpecialPumping() != null && marketCategorySell.getIsSpecialPumping() == 1) {
            String specialOddsInterval = marketCategorySell.getSpecialOddsInterval();
            List<BetPieceEntity> marketOddsList = marketDataMessage.getBetPieceEntities();
            // step2 根据PA赔率排序 小 - 大
            marketOddsList = marketOddsList.stream().sorted(Comparator.comparing(BetPieceEntity::getAoOddsValue)).collect(Collectors.toList());
            log.info("::{}::特殊抽水根据PA赔率倒序排序,AO赛事ID:{},AO玩法ID:{},盘口ID:{},特殊抽水赔率区间:{}", linkId, aoMatchId, aoCategoryId, marketIdStr, specialOddsInterval);
            //下盘
            BetPieceEntity downMarketOddsDataMessage = marketOddsList.get(0);
            //上盘
            BetPieceEntity upMarketOddsDataMessage = marketOddsList.get(1);
            //step2 相等取下盘投注项标识赔率，匹配赔率抽水区间，计算上盘
            if (downMarketOddsDataMessage.getAoOddsValue().equals(upMarketOddsDataMessage.getAoOddsValue())) {
                Map<Boolean, BetPieceEntity> marketOddsMap = marketOddsList.stream().collect(Collectors.toMap(BetPieceEntity::getOddsTypeTag, a -> a, (k1, k2) -> k1));
                BetPieceEntity downOddsDataMessage = marketOddsMap.get(true);
                BetPieceEntity upOddsDataMessage = marketOddsMap.get(false);
                //计算上盘
                oddsScopeCalculate(linkId, specialOddsInterval, downOddsDataMessage, upOddsDataMessage, marketIdStr);
            } else {
                //step2 赔率最小作为下盘，匹配赔率抽水区间，计算上盘
                oddsScopeCalculate(linkId, specialOddsInterval, downMarketOddsDataMessage, upMarketOddsDataMessage, marketIdStr);
            }
        } else {
//            log.info("::{}::特殊抽水,未启用特殊抽水,AO赛事ID:{},AO玩法ID:{}", linkId, aoMatchId, aoCategoryId);
        }
    }

    /**
     * 根据下赔率，计算上赔率
     *
     * @param linkId
     * @param specialOddsInterval       赔率区间配置 格式 {"1.01-1.05":0.07,"1.06-1.19":0.08}
     * @param downMarketOddsDataMessage 下赔
     * @param upMarketOddsDataMessage   上赔
     */
    private void oddsScopeCalculate(String linkId, String specialOddsInterval, BetPieceEntity downMarketOddsDataMessage, BetPieceEntity upMarketOddsDataMessage, String marketIdStr) {
        JSONObject specialOddsIntervalObj = JSONObject.parseObject(specialOddsInterval);
        for (String oddsScope : specialOddsIntervalObj.keySet()) {
            String[] odds = oddsScope.split("-");
            double minOdds = Double.parseDouble(odds[0]);
            double maxOdds = Double.parseDouble(odds[1]);
            Double diff = Double.parseDouble(specialOddsIntervalObj.get(oddsScope).toString());
            double downPaOddsValue = downMarketOddsDataMessage.getAoOddsValue();
            if (downPaOddsValue >= minOdds && downPaOddsValue <= maxOdds) {
                //根据下盘马来赔计算
                Double downMalayOddsValue = downMarketOddsDataMessage.getMalayOddsValue();
                //下盘赔率加上spread是否>=1
                Double oddsSpreadValue = subDoubleTwo(new BigDecimal(Double.toString(downMalayOddsValue)).add(new BigDecimal(Double.toString(diff))).doubleValue());
                if (oddsSpreadValue >= 1) {
                    //上盘赔率= 2-（下盘赔率+spread）
                    Double oddsValue = subDoubleTwo(new BigDecimal(2).subtract(new BigDecimal(Double.toString(oddsSpreadValue))).doubleValue());
                    log.info("::{}::特殊抽水,盘口ID:{},下盘马来赔加特殊抽水>=1,下马来赔:{},特殊水差:{},计算后上盘马来赔:{}", linkId, marketIdStr, downMalayOddsValue, diff, oddsValue);
                    upMarketOddsDataMessage.setMalayOddsValue(oddsValue);
                } else {
                    //上盘赔率= -（下盘赔率+spread）
                    Double oddsValue = oddsSpreadValue * (-1);
                    log.info("::{}::特殊抽水,盘口ID:{},下盘马来赔加特殊抽水<1,下马来赔:{},特殊水差:{},计算后上盘马来赔:{}", linkId, marketIdStr, downMalayOddsValue, diff, oddsValue);
                    upMarketOddsDataMessage.setMalayOddsValue(oddsValue);
                }
                //马来赔转欧洲赔
                double upPaOddsValue = initializeComponent.getConvertMalayToEurope(upMarketOddsDataMessage.getMalayOddsValue());
                upMarketOddsDataMessage.setAoOddsValue(upPaOddsValue);
                log.info("::{}::特殊抽水最终赔率,盘口ID:{},下赔:{},计算后新上赔:{},赔率区间:{}:{}", linkId, marketIdStr, downPaOddsValue, upPaOddsValue, oddsScope, diff);
                return;
            }
        }
    }


    /**
     * 需求：1112
     * 1.判断spread是否为单数，拆分spread,如7分水 拆分为 4 ，3
     * 2.原始赔率转马来赔
     * 3.判断马来赔是否相等：固定下盘抽水更多
     * 4.判断欧赔：赔率最小一方抽水更多
     *
     * @param linkId
     * @param spread
     * @param marketValue
     * @param aoMatchId
     * @param marketsEntity
     * @return
     */
    public Map<String, Double> spreadSingular(String linkId, Double spread, Double marketValue, String aoMatchId, MarketsEntity marketsEntity) {
        String relationMarketId = marketsEntity.getMarketId() + "_" + marketsEntity.getHandicap() + "_" + marketsEntity.getMarketName();
        //Map<投注项ID, Spread>
        Map<String, Double> map = new HashMap<>();
        int scaleNumD = com.panda.merge.common.utils.BigDecimalUtils.scaleNum(spread);
        //step1:判断spread是否为单数，拆分spread
        BigDecimal d = BigDecimal.valueOf(spread);
        BigInteger decimal = d.remainder(BigDecimal.ONE).movePointRight(d.scale()).abs().toBigInteger();
        if (decimal.intValue() % 2 != 0 && scaleNumD == 2 && spread != 0.01D) {
            spread = spread / 2;
            int scaleNum = com.panda.merge.common.utils.BigDecimalUtils.scaleNum(spread);
            //抽水多的
            Double maxSpread = com.panda.merge.common.utils.BigDecimalUtils.scale(spread, scaleNum - 1);
            //抽少水的
            Double minSpread = com.panda.merge.common.utils.BigDecimalUtils.scaleCrop(spread, scaleNum - 1);
            //step2:原始赔率转马来赔
            BetPieceEntity standardMarketOddsDataMessage0 = marketsEntity.getBetPieceEntities().get(0);
            BetPieceEntity standardMarketOddsDataMessage1 = marketsEntity.getBetPieceEntities().get(1);
            double originalOddsValue0 = subDoubleTwo(Double.parseDouble(standardMarketOddsDataMessage0.getOdds()));
            double originalOddsValue1 = subDoubleTwo(Double.parseDouble(standardMarketOddsDataMessage1.getOdds()));
            log.info("::{}::AO赛事ID:{},spreadSingular,odds0欧赔:{},odds1欧赔:{},盘口ID:{},marketOddsList:{}", linkId, aoMatchId, originalOddsValue0, originalOddsValue1, relationMarketId, JSONObject.toJSONString(marketsEntity.getBetPieceEntities()));
            //step3：判断马来赔是否相等,固定下盘抽更多水
            boolean isEqual = com.panda.merge.common.utils.BigDecimalUtils.equalTo(originalOddsValue0, originalOddsValue1);
            //赋值投注项类型
            marketsEntity.getBetPieceEntities().forEach(bet -> {
                String oddsType = SelectionTemplate.getSelectionTemplateById(bet.getBetPriceId()).getOddsType();
                bet.setOddsType(oddsType);
            });
            //按照原始赔率排序 小-大
            List<BetPieceEntity> marketOddsSortedList = marketsEntity.getBetPieceEntities().stream().sorted(Comparator.comparing(BetPieceEntity::getOdds)).collect(Collectors.toList());
            log.info("::{}::AO赛事ID:{},spreadSingular,按照原始赔率排序,盘口ID:{},排序结果:{}", linkId, aoMatchId, relationMarketId, JSONObject.toJSONString(marketOddsSortedList));
            //找出下盘
            for (BetPieceEntity standardSportMarketOdds : marketsEntity.getBetPieceEntities()) {
                String oddsType = standardSportMarketOdds.getOddsType();
                if ("Under".equals(oddsType) || "Even".equals(oddsType) || "No".equals(oddsType) || "X".equals(oddsType) || (marketValue > 0 && "1".equals(oddsType)) || (marketValue <= 0 && "2".equals(oddsType))) {
                    //固定下盘抽更多水
                    if (isEqual) {
                        map.put(oddsType, maxSpread);
                        log.info("::{}::AO赛事ID:{},spreadSingular,马来赔相等,固定下盘抽更多水,盘口ID:{},map:{}", linkId, aoMatchId, relationMarketId, JSONObject.toJSONString(map));
                    } else {
                        //step4:判断欧赔：赔率最小一方抽水更多
                        BetPieceEntity minMarketOddsDataMessage = marketOddsSortedList.get(0);
                        BetPieceEntity maxMarketOddsDataMessage = marketOddsSortedList.get(1);
                        //下盘抽水多
                        if (minMarketOddsDataMessage.getOddsType().equals(oddsType)) {
                            map.put(oddsType, maxSpread);
                            log.info("::{}::AO赛事ID:{},spreadSingular,下盘抽水多,上盘抽水少,盘口ID:{}_{},maxSpread:{}", linkId, aoMatchId, relationMarketId, oddsType, maxSpread);
                        } else if (maxMarketOddsDataMessage.getOddsType().equals(oddsType)) {
                            //下盘抽水少，上盘抽水多
                            map.put(oddsType, minSpread);
                            log.info("::{}::AO赛事ID:{},spreadSingular,下盘抽水少,上盘抽水多,盘口ID:{}_{},minSpread:{}", linkId, aoMatchId, relationMarketId, oddsType, minSpread);
                        } else {
                            log.info("::{}::AO赛事ID:{},spreadSingular,欧赔赔率最小一方抽水不匹配,盘口ID:{}_{}", linkId, aoMatchId, relationMarketId, oddsType);
                        }
                    }
                }
            }
        }
        log.info("::{}::AO赛事ID:{},spreadSingular,统一盘口ID:{},spread:{},返回Map:{}", linkId, aoMatchId, relationMarketId, spread, map);
        return map;
    }


    /**
     * 篮球margin
     *
     * @param linkId
     * @param aoMatchId
     * @param tradeMarketItemConfig
     * @param marketsEntity
     * @param marketIdStr
     */
    private void marginCalculateBasketball(String linkId, String aoMatchId, TradeMarketItemConfig tradeMarketItemConfig, MarketsEntity marketsEntity, String marketIdStr) {
        Double margin = 110D;
        if (null != tradeMarketItemConfig) {
            margin = tradeMarketItemConfig.getMargin();
        }
        if (margin <= 0) {
            margin = 110D;
        }
//        log.info("::{}::marginCalculate,AO赛事ID:{},margin:{},盘口ID:{},盘口信息:{}", linkId, aoMatchId, margin, marketIdStr, JSONObject.toJSONString(marketsEntity));
        List<BetPieceEntity> betPieceEntities = marketsEntity.getBetPieceEntities();
        for (BetPieceEntity betPieceEntity : betPieceEntities) {
            Double originalOdds = Double.valueOf(betPieceEntity.getOdds());
            if (0 != originalOdds) {
                double value = BigDecimalUtils.scale(originalOdds / ((double) margin / 100), 2);
                //计算出最终赔率小于1.01 并且 概率小于1 返回1.01
                if (value < 1.01D && originalOdds < 1D) {
                    betPieceEntity.setAoOddsValue(1.01D);
                } else {
                    betPieceEntity.setAoOddsValue(value);
                }
            }
        }
    }

    public Map<String, TradeMarketItemConfig> getMarginAndMaxMinOddsByAoMatchId(String linkId, String aoMatchId) {
        List<TradeMarketItemConfig> tradeMarketItemConfigs = aoProducerMongoTemp.find(Query.query(Criteria.where("aoMatchId").is(aoMatchId)), TradeMarketItemConfig.class);
        if (CollectionUtils.isEmpty(tradeMarketItemConfigs)) {
            log.info("::{}::AO赛事ID：{}，多项盘margin,最大最小赔率配置不存在", linkId, aoMatchId);
            return new HashMap<>();
        }
        Map<String, TradeMarketItemConfig> tradeMarketItemConfigMap = tradeMarketItemConfigs.stream().collect(Collectors.toMap(m -> m.getAoCategoryId() + "_" + m.getPlaceNum(), x -> x, (k1, k2) -> k1));
        log.info("::{}::AO赛事ID：{}，多项盘margin,最大最小赔率配置", linkId, aoMatchId);
        return tradeMarketItemConfigMap;
    }

    /**
     * @param computeValue
     * @param b1
     * @param b2
     * @return 原始赔率 拼装两投注项
     */
    public BigDecimal originaOddsCompute(BigDecimal computeValue, BigDecimal b1, BigDecimal b2) {
        if (isContainsZero(computeValue.doubleValue(), b1.doubleValue(), b2.doubleValue())) {
            return new BigDecimal(0);
        }
        BigDecimal b0 = new BigDecimal("1");
        return computeValue.multiply(new BigDecimal("2")).divide(new BigDecimal("2").subtract(((b0.divide(b1, 10, BigDecimal.ROUND_HALF_UP).add(b0.divide(b2, 10, BigDecimal.ROUND_HALF_UP))).subtract(new BigDecimal(1))).multiply(computeValue)), 10, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * @Author Carson @Description 是否有存在为0的参数 @Date 2020/12/17 10:31
     */
    public boolean isContainsZero(Double... param) {
        Double res = 1.0;
        for (int i = 0; i < param.length; i++) {
            System.out.println(res + "*" + param[i] + "=" + res * param[i]);
            res = res * param[i];
        }
        return res > 0 ? false : true;
    }


}