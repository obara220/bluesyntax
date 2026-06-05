package com.panda.aoodds.esport.common.calculate;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.aoodds.esport.api.entity.BetPieceEntity;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.calculate.db.DbMatchTemplateLinerMarginConfigDaoHandler;
import com.panda.aoodds.esport.common.config.InitializeComponent;
import com.panda.aoodds.esport.common.config.ThreadPoolConfig;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.EsportTradeMarketItemConfig;
import com.panda.aoodds.esport.common.entity.MatchTemplateLinerMarginConfig;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.common.utils.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 赔率计算
 */
@Slf4j
@Component
public class EsportCalculateOdds {

    @Autowired
    private InitializeComponent initializeComponent;

    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;

    @Autowired
    private RedisService redisService;

    @Autowired
    private EsportMyCalculationMarketProcessor esportMyCalculationMarketProcessor;

    @Autowired
    private ThreadPoolConfig threadPoolConfig;
    @Autowired
    private DbMatchTemplateLinerMarginConfigDaoHandler dbMatchTemplateLinerMarginConfigDaoHandler;

    /**
     * 双重机会玩法
     */
    private static List<Integer> DOUBLE_CHANCE = Lists.newArrayList(MarketCategory.FT_DOUBLE_CHANCE.getId(), MarketCategory.HALF1_DOUBLE_CHANCE.getId(), MarketCategory.HALF2_DOUBLE_CHANCE.getId(), MarketCategory.FT_DOUBLE_CHANCE_AND_BOTH_TEAM_SCORE.getId(), MarketCategory.FT_DOUBLE_CHANCE_AND_TOTAL.getId(), MarketCategory.BOOKING_H1_DOUBLE_CHANCE.getId(), MarketCategory.CORNER_DOUBLE_CHANCE.getId(), MarketCategory.BOOKING_DOUBLE_CHANCE.getId());


    /**
     * AO 赔率计算 ,使用融合下发的水差 和 margin 计算
     *
     * @param linkId
     * @param aoMatchId
     * @param marketsList
     */
    public void calculate(String linkId, String aoMatchId, List<MarketsEntity> marketsList, Integer liveFlag, Long sportId) {
        Map<Integer, List<MarketsEntity>> marketsEntityMap = marketsList.stream().collect(Collectors.groupingBy(MarketsEntity::getMarketId));
        if (MapUtils.isEmpty(marketsEntityMap)) {
            return;
        }
        StopWatch swConfig = new StopWatch(UUID.randomUUID().toString());
        swConfig.start("查询赔率计算配置耗时");
        //AO赛事  0=未开赛、1=滚球 融合配置状态是相反的（0=滚球、1=赛前）
        Integer marketType = liveFlag == 1 ? 0 : 1;
        log.info("::{}::,AO赛事ID:{},滚球标识：{}", linkId, aoMatchId, marketType);
        List<EsportTradeMarketItemConfig> tradeMarketItemConfigs = getMarginAndMaxMinOddsByAoMatchId(linkId, aoMatchId, marketType);
        //spread
        Map<String, EsportTradeMarketItemConfig> spreadPlaceConfigMap = new HashMap<>();
        //margin
        Map<String, EsportTradeMarketItemConfig> marketMarginAndMaxMinOddsConfigMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(tradeMarketItemConfigs)) {
            spreadPlaceConfigMap = tradeMarketItemConfigs.stream().collect(Collectors.toMap(m -> m.getChildStandardCategoryId() + "_" + m.getPlaceNum(), x -> x, (k1, k2) -> k1));
            marketMarginAndMaxMinOddsConfigMap = tradeMarketItemConfigs.stream().collect(Collectors.toMap(m -> m.getAoCategoryId() + "_" + m.getPlaceNum(), x -> x, (k1, k2) -> k1));
        }
        log.info("::{}::AO赛事ID：{}，多项盘margin,最大最小赔率配置", linkId, aoMatchId);
        MatchTemplateLinerMarginConfig matchTemplateLinerMarginConfig = dbMatchTemplateLinerMarginConfigDaoHandler.mongoTempFindOne(aoMatchId + "_" + marketType);
        swConfig.stop();
        //异步循环执行
        List<CompletableFuture<?>> futures = new ArrayList<>();
        TaskExecutor taskExecutor = threadPoolConfig.notifyMyCalculateMarketOddsEsportMessage();
        log.info("::{}::,AO赛事ID:{},准备开始抽水计算,", linkId, aoMatchId);
        for (Map.Entry<Integer, List<MarketsEntity>> entry : marketsEntityMap.entrySet()) {
            MarketCategory marketCategory = MarketCategory.getMarketCategoryById(entry.getKey());
            if (null == marketCategory) {
                continue;
            }
            String calculateType = marketCategory.getCalculateType();
            List<MarketsEntity> marketDataMessages = entry.getValue();
            log.info("::{}::,AO赛事ID:{},开始抽水计算,玩法ID：{},条数：{}", linkId, aoMatchId, entry.getKey(), marketDataMessages.size());
            Map<String, EsportTradeMarketItemConfig> finalSpreadPlaceConfigMap = spreadPlaceConfigMap;
            Map<String, EsportTradeMarketItemConfig> finalMarketMarginAndMaxMinOddsConfigMap = marketMarginAndMaxMinOddsConfigMap;
            futures.add(CompletableFuture.supplyAsync(() -> {
                for (MarketsEntity marketDataMessage : marketDataMessages) {
                    //盘口计算
                    String marketIdStr = marketDataMessage.getMarketId() + "_" + marketDataMessage.getHandicap() + "_" + marketDataMessage.getMarketName();
                    if ("MY".equals(calculateType)) {
                        //新抽水逻辑
                        boolean isOk = esportMyCalculationMarketProcessor.calculationMarketAuto(linkId, aoMatchId, marketDataMessage, marketType, marketIdStr, finalSpreadPlaceConfigMap);
                        if (!isOk) {
                            continue;
                        }
                        //最终的马来赔转欧赔
                        for (BetPieceEntity marketOdds : marketDataMessage.getBetPieceEntities()) {
                            //设置最终的paOddsValue，将马来赔转欧赔后，计算赔率差绝对值
                            Double aoOddsValue = initializeComponent.getConvertMalayToEurope(marketOdds.getMalayOddsValue());
                            marketOdds.setAoOddsValue(aoOddsValue);
                        }
                    } else {
                        String key = marketDataMessage.getMarketId() + "_" + marketDataMessage.getOrder();
                        //获取模板margin
                        EsportTradeMarketItemConfig tradeMarketItemConfig = finalMarketMarginAndMaxMinOddsConfigMap.get(key);
                        //margin计算
                        if (sportId == 1L) {
                            marginCalculateFootball(linkId, aoMatchId, tradeMarketItemConfig, marketDataMessage, marketIdStr, matchTemplateLinerMarginConfig);
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
        log.info("::{}::AO赛事ID:{},赔率计算耗时:{}ms," + swConfig.prettyPrint(), linkId, aoMatchId, swConfig.getTotalTimeMillis());
        //最大最小赔赔率
        marketOddsVerify(linkId, aoMatchId, sportId, marketsList, marketMarginAndMaxMinOddsConfigMap);
    }


    /**
     * margin计算
     * <p>
     * Margin 设定为 mar   维持率为 y ％（输入值＝0-100）
     * 投注项原始概率 Prob(a) / Prob(b) / Prob(c)
     * <p>
     * (mar-1 )* y = M 1           # 不依照比例分配的margin
     * (mar-1 )* ( 1-y ) = M 2   # 按比例分配的margin
     * <p>
     * TTLProb = ( 1-Prob(a) )+( 1-Prob(b) )+( 1-Prob(c) )
     * Prob(a)m1 =M 1* Prob(a)
     * Prob(a)m2 =M 2* Prob(a) / TTLProb
     * <p>
     * Prob(Na) =Prob(a) + Prob(a)m1+Prob(a)m2
     * NewOdd  =1/Prob(Na)
     * <p>
     * 列子
     * Margin 设定为 1.33   维持率为 40 ％（输入值＝0-100）
     * 投注项原始概率 0.7 / 0.2 / 0.1
     * <p>
     * (1.33-1 )* 0.4 = 0.132         # 不依照比例分配的margin
     * (1.33-1 )* ( 1-0.4 ) = 0.198   # 按比例分配的margin
     * <p>
     * TTLProb = 投注项条数-1
     * Prob(a)m1 =0.132* 0.7
     * Prob(a)m2 =0.198* (1-0.7) / 1
     * <p>
     * Prob(Na) =0.7+ 0.7*0.132+ 0.7*0.198/2
     * NewOdd  =1/Prob(Na)
     *
     * @param marketsEntity
     */
    public void marginCalculateFootball(String linkId, String aoMatchId, EsportTradeMarketItemConfig tradeMarketItemConfig, MarketsEntity marketsEntity, String marketIdStr, MatchTemplateLinerMarginConfig matchTemplateLinerMarginConfig) {
        List<BetPieceEntity> betPieceEntities = marketsEntity.getBetPieceEntities();
        Double TTLProb = betPieceEntities.size() - 1D;
        Double rate = 0.5D;
        if (null != matchTemplateLinerMarginConfig && null != matchTemplateLinerMarginConfig.getRetentionRate()) {
            rate = matchTemplateLinerMarginConfig.getRetentionRate() / 100;
        }
        Double margin = 1.1D;
        if (null != tradeMarketItemConfig) {
            margin = tradeMarketItemConfig.getMargin() / 100;
        }
        int num = 1;
        //双重机会 特殊处理
        if (DOUBLE_CHANCE.contains(marketsEntity.getMarketId())) {
            num = 2;
            if (null == tradeMarketItemConfig) {
                margin = 2.2D;
            }
        }
        for (BetPieceEntity betPieceEntity : betPieceEntities) {
            Double probabilities = betPieceEntity.getProbabilities();
            if (0 == probabilities) {
                betPieceEntity.setAoOddsValue(0D);
                continue;
            }
            //不依照比例分配的margin
            Double marginN = (margin - num) * rate;
            //按比例分配的margin
            Double marginY = (margin - num) * (1 - rate);
            Double ProbM1 = marginN * probabilities / num;
            Double ProbM2 = marginY * (num - probabilities) / (TTLProb * num);
            Double NewOdd = BigDecimalUtils.scale(1 / (probabilities + ProbM1 + ProbM2), 2);
            betPieceEntity.setAoOddsValue(NewOdd);
            log.info("::{}::margin计算：{}，probabilities：{},计算出赔率：{} ,margin:{},rate:{} ,玩法：{}", linkId, JSONObject.toJSONString(betPieceEntity), probabilities, NewOdd, margin, rate, marketsEntity.getMarketId());
        }
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
    private void marginCalculateBasketball(String linkId, String aoMatchId, EsportTradeMarketItemConfig tradeMarketItemConfig, MarketsEntity marketsEntity, String marketIdStr) {
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

    public List<EsportTradeMarketItemConfig> getMarginAndMaxMinOddsByAoMatchId(String linkId, String aoMatchId, Integer marketType) {
        List<EsportTradeMarketItemConfig> tradeMarketItemConfigs = aoProducerMongoTemp.find(Query.query(Criteria.where("aoMatchId").is(aoMatchId).and("marketType").is(marketType)), EsportTradeMarketItemConfig.class);
        if (CollectionUtils.isEmpty(tradeMarketItemConfigs)) {
            log.info("::{}::AO赛事ID：{}，多项盘margin,最大最小赔率配置不存在", linkId, aoMatchId);
            return null;
        }
        return tradeMarketItemConfigs;
    }

    /**
     * 最大最小赔率，超过最大或者最小赔率以
     *
     * @param linkId
     * @param aoMatchId
     * @param marketsList
     * @param marketMaxMinOddsConfigMap 最大最小赔率配置
     */
    public void marketOddsVerify(String linkId, String aoMatchId, Long sportId, List<MarketsEntity> marketsList, Map<String, EsportTradeMarketItemConfig> marketMaxMinOddsConfigMap) {
        if (CollectionUtils.isEmpty(marketMaxMinOddsConfigMap)) {
            log.info("::{}::AO赛事ID:{},最大最小配置配置不存在.", linkId, aoMatchId);
            return;
        }
        marketsList.stream().forEach(market -> {
            marketMinOddsVerify(market);
            if(market.getStatus() != CommonConstant.NUMBER_ZERO){
                return;
            }
            Integer marketId = market.getMarketId();
            EsportTradeMarketItemConfig tradeMarketItemConfig = marketMaxMinOddsConfigMap.get(marketId + "_" + market.getOrder());
            if (null == tradeMarketItemConfig) {
                tradeMarketItemConfig = marketMaxMinOddsConfigMap.get(marketId + "_1");
            }
            if (null == tradeMarketItemConfig) {
                return;
            }
            Double max = null == tradeMarketItemConfig.getMaxOddsValue() ? 0D : tradeMarketItemConfig.getMaxOddsValue();
            Double min = null == tradeMarketItemConfig.getMinOddsValue() ? 0D : tradeMarketItemConfig.getMinOddsValue();
            if (0 == max || 0 == min) {
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
                        }
                        if (aoOddsValue <= min) {
                            bet.setAoOddsValue(min);
                        }
                    }
                });
            }
        });
    }

    /**
     * 最小赔率固定小于等于 1.01 盘口关盘
     *
     * @param marketsEntity
     */
    private void marketMinOddsVerify(MarketsEntity marketsEntity) {
        //投注项最大最小赔率校验
        List<BetPieceEntity> betPieceEntities = marketsEntity.getBetPieceEntities();
        if (CollectionUtils.isEmpty(betPieceEntities)) {
            return;
        }
        //投注项条数2 固定小于1.05盘口关盘
        if (betPieceEntities.size() == 2) {
            betPieceEntities.forEach(bet -> {
                Double aoOddsValue = bet.getAoOddsValue();
                if (aoOddsValue <= 1.05D) {
                    marketsEntity.setStatus(CommonConstant.NUMBER_TWO);
                }
            });
        }
    }
}