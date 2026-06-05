package com.panda.aoodds.esport.common.calculate;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import com.panda.aoodds.esport.api.entity.*;
import com.panda.aoodds.esport.common.constant.CommonConstant;
import com.panda.aoodds.esport.common.entity.*;
import com.panda.aoodds.esport.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.aoodds.esport.common.constant.RedisKeyConstant.AO_ESPORT_BGTIME_KEY;


/**
 * 组装返回盘口结构
 */
@Slf4j
@Component
public class BkEsportConvertMatchMarket {

    @Autowired
    private BkEsportCalculateOdds bkEsportCalculateOdds;

    @Autowired
    private RedisService redisService;

    @Autowired
    @Qualifier("producerMongoTemplate")
    private MongoTemplate aoProducerMongoTemp;

    //全场
    public static List<Integer> FT_SHOW = Lists.newArrayList(10001, 10008, 10004, 10005, 40001, 40002, 40003, 50002, 50004, 50001, 70001, 70003, 70002, 40025, 50023, 50024, 50025, 80010, 80011, 80005, 80003, 80002, 50009, 50010, 50011,40027);
    //半场
    public static List<Integer> HT_SHOW = Lists.newArrayList(20001, 20004, 20005, 20008, 40008, 40009, 40010, 50006, 50008, 50005, 70008, 70009, 70010, 50026, 50027, 50028);
    //15分钟玩法
    public static List<Integer> MIN_SHOW = Lists.newArrayList(30003, 40024, 50031);


    //玩法对应展示盘口名称
    public static Map<Integer, String> ALIAS = new HashMap<Integer, String>() {{
        put(50009, "Sending off");
        put(50010, "Home Sending off");
        put(50011, "Away Sending off");
    }};


    public static List<Integer> BASKETBALL_FT_SHOW = Lists.newArrayList(11001, 11003, 11002, 11005, 11006);
    public static List<Integer> BASKETBALL_HT_SHOW = Lists.newArrayList(11007, 11009, 11008, 11011, 11012);
    public static List<Integer> BASKETBALL_Q1_SHOW = Lists.newArrayList(11019, 11021, 11020, 11023, 11024);
    public static List<Integer> BASKETBALL_Q2_SHOW = Lists.newArrayList(11025, 11027, 11026, 11029, 11030);
    public static List<Integer> BASKETBALL_Q3_SHOW = Lists.newArrayList(11031, 11033, 11032, 11035, 11036);
    public static List<Integer> BASKETBALL_Q4_SHOW = Lists.newArrayList(11037, 11039, 11038, 11041, 11042);


    /**
     * 组装返回前端参数
     *
     * @param linkId
     * @param aoMatchId
     * @param marketsList
     * @param isTrue        false:不计算赔率WS true:计算赔率
     * @param isManualSocre 是否手工比分 0否：1是
     * @return
     */
    public MatchMarketVo convertStandardMarketMessage(String linkId, String aoMatchId, String requestType, List<MarketsEntity> marketsList, Boolean isTrue, String isManualSocre, Long sportId) {
        //AO赔率计算
        if (isTrue) {
            bkEsportCalculateOdds.calculate(linkId, aoMatchId, isManualSocre, marketsList, sportId);
        }
        return basketballConvertProcessor(linkId, aoMatchId, requestType, marketsList);
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
     * 转换投注项名称
     *
     * @param marketId
     * @param marketsEntityList
     */
    private static List<MarketsEntity> convertOddsType(Integer marketId, List<MarketsEntity> marketsEntityList, MarketSetVo vo) {
        marketsEntityList.forEach(marketsEntity -> {
            marketsEntity.getBetPieceEntities().forEach(betPieceEntity -> {
                String name = betPieceEntity.getName();
                if ("{competitor1}".equals(name)) {
                    betPieceEntity.setName("home");
                }
                if ("{competitor2}".equals(name)) {
                    betPieceEntity.setName("away");
                }
            });
            if (null != ALIAS.get(marketsEntity.getMarketId())) {
                vo.setMarketSetName(ALIAS.get(marketsEntity.getMarketId()));
            }
            //投注项顺序
            if (50009 == marketsEntity.getMarketId() || 50010 == marketsEntity.getMarketId() || 50011 == marketsEntity.getMarketId()) {
                marketsEntity.setBetPieceEntities(marketsEntity.getBetPieceEntities().stream().sorted(Comparator.comparing(BetPieceEntity::getBetPriceId)).collect(Collectors.toList()));
            }
        });
        return marketsEntityList;
    }

    /**
     * 篮球组装AO界面展示数据
     *
     * @param aoMatchId
     * @param requestType
     * @param marketsList
     */
    private MatchMarketVo basketballConvertProcessor(String linkId, String aoMatchId, String requestType, List<MarketsEntity> marketsList) {
        MatchMarketVo matchMarketVo = new MatchMarketVo();
        matchMarketVo.setAoMatchId(aoMatchId);
        Map<String, List<MarketsEntity>> marketsGroupingMap = marketsList.stream().filter(m ->
                null != m && CommonConstant.AO_CATEGORY_SET_SHOW.get(requestType).contains(m.getMarketId())).collect(Collectors.groupingBy(MarketsEntity::getMarketName));
        Map<String, TradeMarketItemConfig> marketMarginConfigMap = bkEsportCalculateOdds.getMarginAndMaxMinOddsByAoMatchId(linkId, aoMatchId);

        //全场玩法集
        MarketCategorySetVo ftCategorySetVo = new MarketCategorySetVo();
        ftCategorySetVo.setSetName("FT");
        List<MarketSetVo> ftMarketSetVoList = new ArrayList<>();
        //半场玩法集
        MarketCategorySetVo htCategorySetVo = new MarketCategorySetVo();
        htCategorySetVo.setSetName("HT");
        List<MarketSetVo> htMarketSetVoList = new ArrayList<>();

        MarketCategorySetVo q1CategorySetVo = new MarketCategorySetVo();
        q1CategorySetVo.setSetName("Q1");
        List<MarketSetVo> q1MarketSetVoList = new ArrayList<>();

        MarketCategorySetVo q3CategorySetVo = new MarketCategorySetVo();
        q3CategorySetVo.setSetName("Q3");
        List<MarketSetVo> q3MarketSetVoList = new ArrayList<>();


        //遍历玩法分组集合，设置盘口集对象
        marketsGroupingMap.forEach((name, marketsGroupingList) -> {
            List<MarketsEntity> marketsEntityList = JSONObject.parseArray(JSONObject.toJSONString(marketsGroupingList), MarketsEntity.class);
            //盘口位置排序 小-》大
            marketsEntityList = marketsEntityList.stream().sorted(Comparator.comparing(MarketsEntity::getOrder)).collect(Collectors.toList());
            Integer marketId = marketsEntityList.get(0).getMarketId();
            Integer margin = setMargin(marketId, marketMarginConfigMap);
            if (BASKETBALL_FT_SHOW.contains(marketId)) {
                MarketSetVo marketSetVo = new MarketSetVo();
                marketSetVo.setMargin(margin);
                marketSetVo.setMarketSetId(marketId);
                marketSetVo.setMarketSetName(name);
                marketSetVo.setMarketsEntityList(convertOddsType(marketId, marketsEntityList, marketSetVo).stream().limit(5).collect(Collectors.toList()));
                ftMarketSetVoList.add(marketSetVo);
            } else if (BASKETBALL_HT_SHOW.contains(marketId)) {
                MarketSetVo marketSetVo = new MarketSetVo();
                marketSetVo.setMargin(margin);
                marketSetVo.setMarketSetId(marketId);
                marketSetVo.setMarketSetName(name);
                marketSetVo.setMarketsEntityList(convertOddsType(marketId, marketsEntityList, marketSetVo).stream().limit(5).collect(Collectors.toList()));
                htMarketSetVoList.add(marketSetVo);
            } else if (BASKETBALL_Q1_SHOW.contains(marketId)) {
                MarketSetVo marketSetVo = new MarketSetVo();
                marketSetVo.setMargin(margin);
                marketSetVo.setMarketSetId(marketId);
                marketSetVo.setMarketSetName(name);
                marketSetVo.setMarketsEntityList(convertOddsType(marketId, marketsEntityList, marketSetVo).stream().limit(3).collect(Collectors.toList()));
                q1MarketSetVoList.add(marketSetVo);
            } else if (BASKETBALL_Q3_SHOW.contains(marketId)) {
                MarketSetVo marketSetVo = new MarketSetVo();
                marketSetVo.setMargin(margin);
                marketSetVo.setMarketSetId(marketId);
                marketSetVo.setMarketSetName(name);
                marketSetVo.setMarketsEntityList(convertOddsType(marketId, marketsEntityList, marketSetVo).stream().limit(3).collect(Collectors.toList()));
                q3MarketSetVoList.add(marketSetVo);
            }

        });
        //添加到玩法集集合
        List<MarketCategorySetVo> marketCategorySetVoList = new LinkedList<>();
        //设置玩法集中的盘口集对象集合
        if (!ftMarketSetVoList.isEmpty()) {
            ftCategorySetVo.setMarketSetVoList(ftMarketSetVoList);
            marketCategorySetVoList.add(ftCategorySetVo);
        }
        if (!htMarketSetVoList.isEmpty()) {
            htCategorySetVo.setMarketSetVoList(htMarketSetVoList);
            marketCategorySetVoList.add(htCategorySetVo);
        }
        if (!q1MarketSetVoList.isEmpty()) {
            q1CategorySetVo.setMarketSetVoList(q1MarketSetVoList);
            marketCategorySetVoList.add(q1CategorySetVo);
        }
        if (!q3MarketSetVoList.isEmpty()) {
            q3CategorySetVo.setMarketSetVoList(q3MarketSetVoList);
            marketCategorySetVoList.add(q3CategorySetVo);
        }
        matchMarketVo.setMarketCategorySetVos(marketCategorySetVoList);
        return matchMarketVo;
    }

    private Integer setMargin(Integer marketId, Map<String, TradeMarketItemConfig> marketMarginConfigMap) {
        TradeMarketItemConfig tradeMarketItemConfig = marketMarginConfigMap.get(marketId + "_1");
        if (null != tradeMarketItemConfig) {
            return tradeMarketItemConfig.getMargin().intValue();
        }
        return 110;
    }
}
