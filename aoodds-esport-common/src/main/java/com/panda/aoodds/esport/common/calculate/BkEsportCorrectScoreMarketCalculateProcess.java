package com.panda.aoodds.esport.common.calculate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Lists;

import com.panda.aoodds.esport.api.entity.BetPieceEntity;
import com.panda.aoodds.esport.api.entity.BetPieceEntityVo;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.api.entity.MarketsEntityVo;
import com.panda.aoodds.esport.common.constant.RedisKeyConstant;
import com.panda.aoodds.esport.common.entity.MatchTemplateLinerMarginConfig;
import com.panda.aoodds.esport.common.entity.TradeMarketItemConfig;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.common.utils.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 2663
 * 波胆计算
 */
@Slf4j
@Component
public class BkEsportCorrectScoreMarketCalculateProcess {
    @Autowired
    private BkEsportCalculateOdds bkEsportCalculateOdds;
    /**
     * 双重机会玩法
     */
    public static List<Integer> DOUBLE_CHANCE = Lists.newArrayList(MarketCategory.BASKETBALL_Q1_DOUBLE_CHANCE.getId(), MarketCategory.BASKETBALL_Q2_DOUBLE_CHANCE.getId(),
            MarketCategory.BASKETBALL_Q3_DOUBLE_CHANCE.getId(), MarketCategory.BASKETBALL_Q4_DOUBLE_CHANCE.getId());

    @Autowired
    private RedisService redisService;

    public static void main(String[] args) {

        String str = "[{\"margin\":1.01,\"odds\":\"1.5\"},{\"margin\":1.026,\"odds\":\"2\"},{\"margin\":1.15,\"odds\":\"3\"},{\"margin\":1.3,\"odds\":\"6\"},{\"margin\":2.3,\"odds\":\"500\"},{\"margin\":20,\"odds\":\"MAX\"}]";
        JSONArray array = JSONObject.parseArray(str);
        Map<String, Double> configOriMapConvert = new HashMap<>();
        array.stream().forEach(a -> {
            JSONObject object = JSONObject.parseObject(a.toString());
            configOriMapConvert.put(object.getString("odds"), object.getDoubleValue("margin"));
        });


        String str1 = "[{\"active\":true,\"aoOddsValue\":0.0,\"betPriceId\":\"126\",\"malayOddsValue\":0.0,\"marketDiffValue\":0.0,\"name\":\"other\",\"odds\":\"5.436\",\"oddsTypeSecondaryTag\":false,\"oddsTypeTag\":false,\"order\":26,\"probabilities\":0.011023560803937403}]";
        List<BetPieceEntity> betPieceEntities = JSONObject.parseArray(str1, BetPieceEntity.class);
        int disableDecimalFeature = JSON.DEFAULT_PARSER_FEATURE & ~Feature.UseBigDecimal.getMask();
        //计算赔率
        for (BetPieceEntity betPieceEntitie : betPieceEntities) {
            Map<String, Double> configOriMap = JSONObject.parseObject(JSONObject.toJSONString(configOriMapConvert), Map.class, disableDecimalFeature, Feature.OrderedField);
            //先获取到配置的max
            Double max = configOriMap.get("MAX");
            configOriMap.remove("MAX");
            Double ori = Double.valueOf(betPieceEntitie.getOdds());
            Map<Double, Double> newConfigOriMap = mapConvert(configOriMap);
            Double linearMargin = newConfigOriMap.get(ori);
            if (null == linearMargin) {
                //转换配置Map为list ,后序操作都是操作list下标
                linearMargin = getLinearInterval(newConfigOriMap, ori);
            }
            linearMargin = linearMargin < 1.01 ? 1.01 : linearMargin;
            linearMargin = Math.min(linearMargin, max);
            System.out.println(linearMargin);
            Double offerOdds = BigDecimalUtils.scale(ori / linearMargin, 2);
            betPieceEntitie.setAoOddsValue(offerOdds);
            log.info("::{}::赛事ID:{},波胆玩法计算，最终赔率：{} / {} = {} ,", "linkId", "matchId", ori, linearMargin, offerOdds);
        }

    }


    /**
     * 计算线性波胆玩法
     *
     * @param linkId
     * @param matchId
     * @param matchTemplateLinerMarginConfig 配置
     * @param marketDataMessage
     */

    public void correctScoreMarketCalculate(String linkId, String matchId, MatchTemplateLinerMarginConfig matchTemplateLinerMarginConfig, MarketsEntity marketDataMessage) {
        if (null == matchTemplateLinerMarginConfig) {
            return;
        }
        String configOriMapStr = matchTemplateLinerMarginConfig.getLinerMargin();
        List<BetPieceEntity> betPieceEntities = marketDataMessage.getBetPieceEntities();
        //没有配置默认1.01计算
        if (StringUtils.isEmpty(configOriMapStr)) {
            betPieceEntities.forEach(betPieceEntity -> {
                if (StringUtils.isNotEmpty(betPieceEntity.getOdds()))
                    betPieceEntity.setAoOddsValue(BigDecimalUtils.scale(Double.parseDouble(betPieceEntity.getOdds()) / 1.01, 2));
            });
            return;
        }
        Map<String, Double> configOriMapConvert = new HashMap<>();
        JSONArray array = JSONObject.parseArray(configOriMapStr);
        array.stream().forEach(a -> {
            JSONObject object = JSONObject.parseObject(a.toString());
            configOriMapConvert.put(object.getString("odds"), object.getDoubleValue("margin"));
        });
        //计算赔率
        for (BetPieceEntity betPieceEntitie : betPieceEntities) {
            int disableDecimalFeature = JSON.DEFAULT_PARSER_FEATURE & ~Feature.UseBigDecimal.getMask();
            Map<String, Double> configOriMap = JSONObject.parseObject(JSONObject.toJSONString(configOriMapConvert), Map.class, disableDecimalFeature, Feature.OrderedField);
            //先获取到配置的max
            Double max = configOriMap.get("MAX");
            configOriMap.remove("MAX");
            Double ori = Double.valueOf(betPieceEntitie.getOdds());
            Double probabilities = betPieceEntitie.getProbabilities();
            if (0 == ori || 0 == probabilities) {
                betPieceEntitie.setAoOddsValue(0D);
                continue;
            }
            Map<Double, Double> newConfigOriMap = mapConvert(configOriMap);
            Double linearMargin = newConfigOriMap.get(ori);
            if (null == linearMargin) {
                //转换配置Map为list ,后序操作都是操作list下标
                linearMargin = getLinearInterval(newConfigOriMap, ori);
            }
            linearMargin = linearMargin < 1.01 ? 1.01 : linearMargin;
            linearMargin = Math.min(linearMargin, max);
            System.out.println(linearMargin);
            Double offerOdds = BigDecimalUtils.scale(ori / linearMargin, 2);
            betPieceEntitie.setAoOddsValue(offerOdds);
            log.info("::{}::赛事ID:{},波胆玩法计算，最终赔率：{} / {} = {} ,", linkId, matchId, ori, linearMargin, offerOdds);
        }
    }

    /**
     * 得到获取到线性区间
     *
     * @param newConfigOriMap
     * @param ori
     * @return
     */
    private static Double getLinearInterval(Map<Double, Double> newConfigOriMap, Double ori) {
        Double x1 = 0D, x2 = 0D, y1 = 0D, y2 = 0D;
        List<Double> newConfigOriList = mapConvertList(newConfigOriMap, ori);
        //配置不全不处理
        if (newConfigOriList.size() < 3) {
            return 0D;
        }
        //获取到赔率在list下标
        int num = newConfigOriList.indexOf(ori);
        //1.第0个是盘口赔率，获取第1第2线性配置
        Double firstOriConfig = newConfigOriList.get(0);
        if (firstOriConfig.equals(ori)) {
            //第一个线性赔率
            Double firstOriLinear = newConfigOriList.get(num + 1);
            //第二个线性赔率
            Double secondOriLinear = newConfigOriList.get(num + 2);
            x1 = firstOriLinear;
            y1 = secondOriLinear;
            x2 = newConfigOriMap.get(firstOriLinear);
            y2 = newConfigOriMap.get(secondOriLinear);
            return calculateLinearMargin(ori, x1, y1, x2, y2);
        }
        //2.最后一个是盘口赔率，获取倒数第2第3线性配置
        Double lastOriConfig = newConfigOriList.get(newConfigOriList.size() - 1);
        if (lastOriConfig.equals(ori)) {
            //倒数第一个线性赔率
            Double firstOriLinear = newConfigOriList.get(num - 1);
            //倒数第二个线性赔率
            Double secondOriLinear = newConfigOriList.get(num - 2);
            x1 = secondOriLinear;
            y1 = firstOriLinear;
            x2 = newConfigOriMap.get(secondOriLinear);
            y2 = newConfigOriMap.get(firstOriLinear);
            return calculateLinearMargin(ori, x1, y1, x2, y2);
        }
        //3.盘口赔率在线性之间，用赔率list所在的下标，获取上一个，下一个线性配置
        //上一个线性赔率
        Double firstOriLinear = newConfigOriList.get(num - 1);
        //下一个线性赔率
        Double secondOriLinear = newConfigOriList.get(num + 1);

        x1 = firstOriLinear;
        x2 = secondOriLinear;
        y1 = newConfigOriMap.get(firstOriLinear);
        y2 = newConfigOriMap.get(secondOriLinear);
        return calculateLinearMargin(ori, x1, y1, x2, y2);
    }

    /**
     * 计算出最终margin
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private static Double calculateLinearMargin(Double ori, Double x1, Double y1, Double x2, Double y2) {
        return BigDecimalUtils.scale((ori - x1) * (y2 - y1) / (x2 - x1) + y1, 4);
    }

    /**
     * Map<String,Double> 转换 Map<Double,Double>
     *
     * @param configOriMap
     * @return
     */
    private static Map<Double, Double> mapConvert(Map<String, Double> configOriMap) {
        Map<Double, Double> newMap = new HashMap<>();
        configOriMap.entrySet().stream().forEach(x -> newMap.put(Double.parseDouble(x.getKey()), x.getValue()));
        return newMap;
    }

    /**
     * Map 转换 list
     *
     * @param configOriMap
     * @return
     */
    private static List<Double> mapConvertList(Map<Double, Double> configOriMap, Double ori) {
        configOriMap.put(ori, ori);
        List<Double> list = new ArrayList<>(configOriMap.keySet());
        list.sort(Comparator.naturalOrder());
        return list;

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
    public void marginCalculateFootball(String linkId, String aoMatchId, TradeMarketItemConfig tradeMarketItemConfig, MarketsEntity marketsEntity, String marketIdStr, MatchTemplateLinerMarginConfig matchTemplateLinerMarginConfig) {
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
            log.info("::{}::margin计算：{}，probabilities：{},计算出赔率：{} ,margin:{},rate:{} ,玩法：{}",
                    linkId, JSONObject.toJSONString(betPieceEntity), probabilities, NewOdd, margin, rate, marketsEntity.getMarketId());
        }
    }


    /**
     * 返回给前端  波胆原始赔率  ，线性抽水赔率  ，维持赔率
     */
    public List<MarketsEntityVo> getScoreMarketOdds(String linkId, String aoMatchId, MatchTemplateLinerMarginConfig matchTemplateLinerMarginConfig) {
        log.info("::{}::返回给前端波胆原始赔率线性抽水赔率维持赔率,赛事ID：{}，线性抽水配置:{}", linkId, aoMatchId, JSONObject.toJSONString(matchTemplateLinerMarginConfig));
        Map<String, TradeMarketItemConfig> marketMarginConfigMap = bkEsportCalculateOdds.getMarginAndMaxMinOddsByAoMatchId(linkId, aoMatchId);
        List<MarketsEntityVo> marketsEntityVos = new ArrayList<>();
        Map<String, MarketsEntity> cacheMarketMap = getCacheCorrectScoreMarket(aoMatchId);
        if (null == cacheMarketMap) {
            return marketsEntityVos;
        }
        for (Integer marketId : MarketCategory.LINEAR_MARGIN_CORRECT_SCORE) {
            MarketsEntity marketsEntity = cacheMarketMap.get(marketId.toString());
            if (null == marketsEntity) {
                continue;
            }
            //线性抽水
            correctScoreMarketCalculate(linkId, aoMatchId, matchTemplateLinerMarginConfig, marketsEntity);
            MarketsEntityVo marketsEntityVo = new MarketsEntityVo();
            marketsEntityVo.setMarketId(marketsEntity.getMarketId());
            marketsEntityVo.setMarketName(marketsEntity.getMarketName());
            //设置赔率
            setOdds(aoMatchId, marketsEntityVo, marketsEntity);

            String key = marketsEntity.getMarketId() + "_" + marketsEntity.getOrder();
            //获取模板margin
            TradeMarketItemConfig tradeMarketItemConfig = marketMarginConfigMap.get(key);
            marginCalculateFootball(linkId, aoMatchId, tradeMarketItemConfig, marketsEntity, key, matchTemplateLinerMarginConfig);
            //设置赔率
            setOdds(aoMatchId, marketsEntityVo, marketsEntity);
            marketsEntityVos.add(marketsEntityVo);
        }
        return marketsEntityVos;
    }

    public MarketsEntityVo setOdds(String aoMatchId, MarketsEntityVo vo, MarketsEntity marketsEntity) {
        List<BetPieceEntityVo> betPieceEntitiesRe = vo.getBetPieceEntities();
        List<BetPieceEntity> betPieceEntities = marketsEntity.getBetPieceEntities();
        List<BetPieceEntityVo> betPieceEntitiesNew = new ArrayList<>();
        //第一次没有数据,设置原始赔率 和 线性抽水
        if (CollectionUtils.isEmpty(betPieceEntitiesRe)) {
            betPieceEntities.forEach(odds -> {
                BetPieceEntityVo betOdds = new BetPieceEntityVo();
                betOdds.setBetPriceId(odds.getBetPriceId());
                betOdds.setOriginalOdds(Double.parseDouble(odds.getOdds()));
                betOdds.setLinerMarginOdds(odds.getAoOddsValue());
                betPieceEntitiesNew.add(betOdds);
            });
            vo.setBetPieceEntities(betPieceEntitiesNew);
            return vo;
        }

        //第二次设置margin抽水
        Map<String, BetPieceEntityVo> betPieceEntityVoMap = betPieceEntitiesRe.stream().collect(Collectors.toMap(BetPieceEntityVo::getBetPriceId, a -> a, (k1, k2) -> k1));
        betPieceEntities.forEach(odds -> {
            BetPieceEntityVo betPieceEntityVo = betPieceEntityVoMap.get(odds.getBetPriceId());
            betPieceEntityVo.setRetentionRateOdds(odds.getAoOddsValue());
        });
        return vo;
    }

    public Map<String, MarketsEntity> getCacheCorrectScoreMarket(String aoMatchId) {
        String key = RedisKeyConstant.CORRECT_SCORE_MARKET + aoMatchId;
        Map<String, MarketsEntity> marketsEntityMap = redisService.hGetAll(key);
        if (MapUtils.isEmpty(marketsEntityMap)) {
            return new HashMap<>();
        }
        return marketsEntityMap;
    }
}
