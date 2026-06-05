package com.panda.aoodds.esport.common.calculate;


import com.panda.aoodds.esport.api.entity.BetPieceEntity;
import com.panda.aoodds.esport.api.entity.MarketsEntity;
import com.panda.aoodds.esport.common.config.InitializeComponent;
import com.panda.aoodds.esport.common.entity.MarginPlaceConfig;
import com.panda.aoodds.esport.common.market.MarketCategory;
import com.panda.aoodds.esport.common.market.SelectionTemplate;
import com.panda.merge.common.utils.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 需求 ：2269 马来抽水
 * 对足球MY计算，其他赛种忽略
 */

@Slf4j
@Component
public class BkEsportMyCalculationMarketProcessor {

    @Autowired
    private InitializeComponent initializeComponent;


    public boolean calculationMarketAuto(String linkId, String aoMatchId, MarketsEntity marketDataMessage, Integer marketType, String marketIdStr, Map<String, MarginPlaceConfig> marginPlaceConfigMap) {
        MarketCategory marketCategory = MarketCategory.getMarketCategoryById(marketDataMessage.getMarketId());
        if (null == marketCategory) {
            log.info("::{}::AO玩法未对应标准玩法,AO赛事ID:{},AO玩法:{}", linkId, aoMatchId, marketDataMessage.getMarketId());
            return false;
        }
        Long standardCategoryId = Long.valueOf(marketCategory.getStandardCategoryId());
        //15分钟进球-大小({a}-{b}) 玩法存在子玩法，特殊处理
        Long childStandardCategoryId = standardCategoryId;
        if (30003 == marketDataMessage.getMarketId()) {
            log.info("::{}::15分钟进球子玩法处理,AO赛事ID:{},盘口名:{},盘口类型:{}", linkId, aoMatchId, marketDataMessage.getMarketName(), marketType);
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
            log.info("::{}::子玩法配置不存在查询总玩法spread,球头:{},玩法:{},key:{},数据:{}", linkId, marketDataMessage.getMarketId(), marketDataMessage.getHandicap(), categoryMarginKey, marginPlaceConfig);
        }
        if (null != marginPlaceConfig) {
            spread = marginPlaceConfig.getMargin();
        }
        StringBuffer sb = new StringBuffer();
        //判断下盘
        Double marketValue = 0D;
        if (StringUtils.isNotBlank(marketDataMessage.getHandicap()) && MarketCategory.AO_CONVERT_STANDARD_CATEGORY_MY.containsKey(marketDataMessage.getMarketId())) {
            marketValue = Double.parseDouble(marketDataMessage.getHandicap());
        }
        boolean isTrue = arithmeticSpread(linkId, spread, marketDataMessage, marketIdStr, sb);
        if (!isTrue) {
            return false;
        }
        for (BetPieceEntity marketOdds : marketDataMessage.getBetPieceEntities()) {
            String oddsType = SelectionTemplate.getSelectionTemplateById(marketOdds.getBetPriceId()).getOddsType();
            marketOdds.setOddsType(oddsType);
            marketOdds.setMargin(spread);
            //找下盘 计算下盘值
            if ("Under".equals(oddsType) || "Even".equals(oddsType) || "No".equals(oddsType) || "X".equals(oddsType) || (marketValue > 0 && "1".equals(oddsType)) || (marketValue <= 0 && "2".equals(oddsType))) {
                //标记下盘投注项标记，特殊抽水计算需要
                marketOdds.setOddsTypeTag(Boolean.TRUE);
            }
        }
        log.info(sb.toString());
//        return specialCalculationMarketAgainAutoProcessor.calculationMarketAgainAuto(linkId, aoMatchId, marketDataMessage, marketIdStr, marginPlaceConfigMap);
        return true;
    }


    /**
     * 抽水赔率计算
     *
     * @param linkId
     * @param spread
     * @param
     * @param stringBuffer
     * @return
     */
    private boolean arithmeticSpread(String linkId, Double spread, MarketsEntity marketDataMessage, String marketIdStr, StringBuffer stringBuffer) {
        List<BetPieceEntity> betPieceEntities = marketDataMessage.getBetPieceEntities();
        if (CollectionUtils.isEmpty(betPieceEntities) || betPieceEntities.size() != 2) {
            return false;
        }
        BetPieceEntity odds_1st_ori_Entities = betPieceEntities.get(0);
        BetPieceEntity odds_2nd_ori_Entities = betPieceEntities.get(1);
        if (StringUtils.isBlank(odds_1st_ori_Entities.getOdds())) {
            return false;
        }
        double odds_min_maly = 0.01;
        double odds_1st_ori = Double.valueOf(BigDecimalUtils.scale(odds_1st_ori_Entities.getOdds(), 2));//第1个投注项 - 原始赔率（欧洲赔率） 1.95
        double odds_2nd_ori = Double.valueOf(BigDecimalUtils.scale(odds_2nd_ori_Entities.getOdds(), 2));//第2个投注项 - 原始赔率（欧洲赔率） 1.95
        if (odds_1st_ori == 0D || odds_2nd_ori == 0D) {
            return false;
        }
        double odds_1st_maly = 0D; //第1个投注项 - 马来抽水后赔率（马来赔率）#改了马来抽水逻辑，已经抽水了
        double odds_2nd_maly = 0D; //第2个投注项 - 马来抽水后赔率（马来赔率）#改了马来抽水逻辑，已经抽水了。后续加水差转成欧洲赔率都是原有逻辑 -
        double odds_1st_margin = 0D;//第1个投注项的抽水spread
        double odds_2nd_margin = 0D;//第2个投注项的抽水spread
        stringBuffer.append(linkId + ",盘口id：" + marketIdStr + "，AO玩法ID：" + marketDataMessage.getMarketId() + ",参与计算spread：" + spread);
        //计算
        double sp = 2 / (2 - BigDecimalUtils.divide(spread, 2)); //1/(1 - spread/2 + 1)  + 1/(1 - spread/2 + 1)   固定抽水（spread=0.3，50%，50%对应1 - spread/2,和1.85）
        if (odds_1st_ori <= 2) { //判断第1个投注项 - 原始赔率是否比2小
            odds_1st_maly = odds_1st_ori / sp - 1; //如果第1个投注项赔率比2小，计算第1个投注项赔率的马来抽水后赔率，去尾法保留两位小数（如果计算出来是0 .1875，取0 .18）
            odds_1st_maly = BigDecimalUtils.scaleCrop(Math.max(odds_min_maly, odds_1st_maly), 2);// 和最小马来赔率比较，取大的1个
            odds_1st_margin = odds_1st_ori - 1 - odds_1st_maly;
            odds_2nd_margin = spread - odds_1st_margin;
            if ((odds_1st_maly + spread) < 1) {  //：第2个投注项马来赔率，由第1个投注项马来赔率和spread计算而来
                odds_2nd_maly = -(BigDecimalUtils.add(odds_1st_maly, spread));
            } else {
                odds_2nd_maly = BigDecimalUtils.subtract(2, BigDecimalUtils.add(odds_1st_maly, spread));
            }
            stringBuffer.append(",第一个投注项赔率小于等于2，计算出maly：" + odds_1st_maly + "===" + odds_2nd_maly);
        } else { //#如果第1个投注项原始赔率大于2，先计算第2个投注项的马来赔率，计算逻辑和上面类似
            odds_2nd_maly = odds_2nd_ori / sp - 1;//#去尾法保留两位小数（如果计算出来是0 .1875，取0 .18）
            odds_2nd_maly = BigDecimalUtils.scaleCrop(Math.max(odds_min_maly, odds_2nd_maly), 2);
            odds_2nd_margin = odds_2nd_ori - 1 - odds_2nd_maly;
            odds_1st_margin = spread - odds_2nd_margin;
            if ((odds_2nd_maly + spread) < 1) {
                odds_1st_maly = -(BigDecimalUtils.add(odds_2nd_maly, spread));
            } else {
                odds_1st_maly = BigDecimalUtils.subtract(2, BigDecimalUtils.add(odds_2nd_maly, spread));
            }
            stringBuffer.append(",第一个投注项赔率大于2，计算出maly：" + odds_1st_maly + "===" + odds_2nd_maly);
        }
        odds_1st_ori_Entities.setMalayOddsValue(BigDecimalUtils.scaleCrop(odds_1st_maly, 2));
        odds_2nd_ori_Entities.setMalayOddsValue(BigDecimalUtils.scaleCrop(odds_2nd_maly, 2));
        return true;
    }
}
