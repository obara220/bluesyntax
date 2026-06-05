package com.panda.aoodds.esport.common.calculate;

import com.alibaba.fastjson.JSONObject;
import com.panda.aoodds.esport.api.entity.BetPieceEntity;
import com.panda.aoodds.esport.common.config.InitializeComponent;
import com.panda.aoodds.esport.common.entity.MarginPlaceConfig;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.utils.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 特殊抽水
 */
@Slf4j
@Component
public class SpecialCalculationMarketAgainAutoProcessor {

    @Autowired
    private InitializeComponent initializeComponent;

    @Autowired
    private AutoDiffCountMarketMalay autoDiffCountMarketMalay;

    /**
     * 当两个马来赔率都为负数时，欧洲赔率最小为下盘，重新计算 上盘
     *
     * @param linkId
     * @param aoMatchId
     * @param marketDataMessage
     * @param marketIdStr
     * @param marginPlaceConfigMap
     * @return
     */
    public boolean calculationMarketAgainAuto(String linkId, String aoMatchId, MarketsEntity marketDataMessage, String marketIdStr, Map<String, MarginPlaceConfig> marginPlaceConfigMap) {
        //判断马来赔率是否都为负数
        List<BetPieceEntity> betPieceEntities = marketDataMessage.getBetPieceEntities();
        BetPieceEntity betPieceEntity1 = betPieceEntities.get(0);
        BetPieceEntity betPieceEntity2 = betPieceEntities.get(1);
        if (betPieceEntity1.getMalayOddsValue() < 0 && betPieceEntity2.getMalayOddsValue() < 0) {
            MarketCategory marketCategory = MarketCategory.getMarketCategoryById(marketDataMessage.getMarketId());
            if (null == marketCategory) {
                log.info("::{}::重新计算赔率,AO玩法未对应标准玩法,AO赛事ID:{},AO玩法:{}", linkId, aoMatchId, marketDataMessage.getMarketId());
                return false;
            }
            Long standardCategoryId = Long.valueOf(marketCategory.getStandardCategoryId());
            //15分钟进球-大小({a}-{b}) 玩法存在子玩法，特殊处理
            Long childStandardCategoryId = standardCategoryId;
            if (30003 == marketDataMessage.getMarketId()) {
                log.info("::{}::重新计算赔率,15分钟进球子玩法处理,AO赛事ID:{},盘口名:{}", linkId, aoMatchId, marketDataMessage.getMarketName());
                String marketName = marketDataMessage.getMarketName();
                String time15 = marketName.substring(marketName.indexOf("(") + 1, marketName.indexOf(")"));
                String tndTime = time15.split("-")[1];
                childStandardCategoryId = standardCategoryId * 100 + (Long.parseLong(tndTime) / 15);
            }
            //获取坑位margin
            String childMarginKey = childStandardCategoryId + "_" + marketDataMessage.getOrder();
            MarginPlaceConfig marginPlaceConfig = marginPlaceConfigMap.get(childMarginKey);
            Double spread = 0.1D;
            //子玩法margin不存在，获取总玩法margin
            if (null == marginPlaceConfig) {
                String categoryMarginKey = standardCategoryId + "_1";
                marginPlaceConfig = marginPlaceConfigMap.get(categoryMarginKey);
                log.info("::{}::重新计算赔率,子玩法配置不存在查询总玩法spread,球头:{},玩法:{},key:{}", linkId, marketDataMessage.getMarketId(), marketDataMessage.getHandicap(), categoryMarginKey);
            }
            if (null != marginPlaceConfig) {
                spread = marginPlaceConfig.getMargin();
            }
            StringBuffer sb = new StringBuffer();
            //赔率只取下盘或者受让盘的赔率，通过马来赔取反得出上盘或者让球盘马来赔率
            Double underOriginalMalayOdds = 0D;
            //计算后的下盘值
            Double underMalayOdds = 0D;
            //单数抽水
            Map<String, Double> spreadSingularMap = spreadSingularAgain(linkId, spread, aoMatchId, marketDataMessage);
            List<BetPieceEntity> betPieceEntityList = marketDataMessage.getBetPieceEntities().stream().sorted(Comparator.comparing(BetPieceEntity::getOdds)).collect(Collectors.toList());
            log.info("::{}::重新计算赔率,AO赛事ID:{},盘口ID:{},重新计算赔率盘口:{}", linkId, aoMatchId, marketIdStr, JSONObject.toJSONString(betPieceEntityList));
            BetPieceEntity marketOddsMin = betPieceEntityList.get(0);
            BetPieceEntity marketOddsMax = betPieceEntityList.get(1);
            String oddsType = marketOddsMin.getOddsType();
//            String oddsType = SelectionTemplate.getSelectionTemplateById(marketOddsMin.getBetPriceId()).getOddsType();
//            marketOddsMin.setOddsType(oddsType);
            marketOddsMin.setMargin(spread / 2);
            sb.append("::" + linkId + "::重新计算赔率,盘口ID:" + marketIdStr + ",AO赛事ID:" + aoMatchId + ",AO玩法ID:" + marketDataMessage.getMarketId() + ",标准子玩法玩法ID:" + childStandardCategoryId);
            sb.append(",盘口位置:" + marketDataMessage.getOrder() + ",参与计算spread:" + spread / 2 + ",下盘原始赔率:" + marketOddsMin.getOdds() + ",投注项类型:" + oddsType + ",投注项状态:" + marketOddsMin.isActive());
            //标记下盘投注项标记，特殊抽水计算需要
            marketOddsMin.setOddsTypeTag(Boolean.TRUE);
            //转换赔率为马来赔
            if (StringUtils.isBlank(marketOddsMin.getOdds())) {
                sb.append(",盘口赔率不正常1,赔率不存在计算失败。");
                log.info(sb.toString());
                return false;
            }

            Double originalOddsValue = Double.valueOf(BigDecimalUtils.scale(marketOddsMin.getOdds(), 2));
            underOriginalMalayOdds = initializeComponent.getEuropeConvertMalayMap().get(originalOddsValue);
            if (underOriginalMalayOdds == null) {
                sb.append(",盘口赔率不正常2,计算失败：" + originalOddsValue);
                log.info(sb.toString());
                return false;
            }
            //设置马来赔
            marketOddsMin.setMalayOddsValue(underOriginalMalayOdds);
            Double diffValue = spreadSingularMap.get(oddsType) == null ? spread / 2 : spreadSingularMap.get(oddsType);
            sb.append(",计算前下盘马来赔率:" + underOriginalMalayOdds + ",spread：" + diffValue);
            double finalMalayOddsValue = autoDiffCountMarketMalay.arithmeticMALAY(diffValue, spread, underOriginalMalayOdds, true);
            if (0 == finalMalayOddsValue) {
                sb.append(",盘口赔率不正常3,计算失败。");
                log.info(sb.toString());
                return false;
            }
            marketOddsMin.setMalayOddsValue(finalMalayOddsValue);
            sb.append(",抽水计算后的下盘马来赔率:" + marketOddsMin.getMalayOddsValue());
            underMalayOdds = marketOddsMin.getMalayOddsValue();
            if (underOriginalMalayOdds > 1 && marketOddsMin.getMalayOddsValue() > 0 && marketOddsMin.getMalayOddsValue() < 1) {
                marketOddsMin.setMalayOddsValue(1.01);
                sb.append(",源赔率> 1，抽完水后赔率小于1。AO赔率设置为 1.01:" + marketOddsMin.getMalayOddsValue());
            }

            //判断 新的下盘值加上spread是否>=1
            Double oddsSpreadValue = BigDecimalUtils.subDoubleTwo(new BigDecimal(Double.toString(underMalayOdds)).add(new BigDecimal(Double.toString(spread))).doubleValue());
            sb.append(",盘口ID:" + marketIdStr + ",上盘投注项类型:" + oddsType + ",上盘投注项原始赔率:" + marketOddsMax.getOdds());
            sb.append(",开始计算上盘,下盘计算后马来赔率:" + underMalayOdds + ",加上spread:" + spread + ",后等于：" + oddsSpreadValue);
            if (oddsSpreadValue >= 1) {
                //上盘= 2-（新的下盘值+spread）
                Double oddsValue = BigDecimalUtils.subDoubleTwo(new BigDecimal(2).subtract(new BigDecimal(Double.toString(oddsSpreadValue))).doubleValue());
                marketOddsMax.setMalayOddsValue(oddsValue);
                sb.append(",上盘马来赔率>=1:" + oddsValue);
            } else {
                //上盘= -（新的下盘值+spread）
                marketOddsMax.setMalayOddsValue(oddsSpreadValue * (-1));
                sb.append(",上盘= -（新的下盘值+spread）:" + marketOddsMax.getMalayOddsValue());
            }
            log.info(sb.toString());
        }

        return true;
    }


    /**
     * 需求：1112  赔率重新计算
     * 1.判断spread是否为单数，拆分spread,如7分水 拆分为 4 ，3
     * 3.固定下盘抽水更多
     *
     * @param linkId
     * @param spread
     * @param aoMatchId
     * @param marketsEntity
     * @return
     */
    public Map<String, Double> spreadSingularAgain(String linkId, Double spread, String aoMatchId, MarketsEntity marketsEntity) {
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
            //赋值投注项类型
//            marketsEntity.getBetPieceEntities().forEach(bet -> {
//                String oddsType = SelectionTemplate.getSelectionTemplateById(bet.getBetPriceId()).getOddsType();
//                bet.setOddsType(oddsType);
//            });
            //按照原始赔率排序 小-大
            List<BetPieceEntity> marketOddsSortedList = marketsEntity.getBetPieceEntities().stream().sorted(Comparator.comparing(BetPieceEntity::getOdds)).collect(Collectors.toList());
            log.info("::{}::赔率重新计算,AO赛事ID:{},spreadSingular,按照原始赔率排序,盘口ID:{},排序结果:{}", linkId, aoMatchId, relationMarketId, JSONObject.toJSONString(marketOddsSortedList));
            //step4:赔率最小一方抽水更多
            BetPieceEntity minMarketOddsDataMessage = marketOddsSortedList.get(0);
            //下盘抽水多
            map.put(minMarketOddsDataMessage.getOddsType(), maxSpread);
        }
        log.info("::{}::赔率重新计算,AO赛事ID:{},spreadSingular,统一盘口ID:{},spread:{},返回Map:{}", linkId, aoMatchId, relationMarketId, spread, map);
        return map;
    }

}
