package com.panda.aoodds.esport.common.calculate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 马来盘水差计算
 */
@Slf4j
@Component
public class AutoDiffCountMarketMalay {

    /**
     * 二项盘水差和spread计算 （减spread,加水差）
     *
     * @param diffValue
     * @param spread
     * @param malayOddsValue
     * @param isSpread
     * @return
     */
    public Double arithmeticMALAY(Double diffValue, Double spread, Double malayOddsValue, boolean isSpread) {
        //计算后马来赔
        Double finalMalayOddsValue = 0D;
        //马来赔率减去spread或者加上水差
        BigDecimal malayAndDiff;
        if (isSpread) {
            diffValue = -diffValue;
        }
        malayAndDiff = new BigDecimal(Double.toString(malayOddsValue)).add(new BigDecimal(Double.toString(diffValue)));
        if (malayOddsValue > 0 && malayOddsValue <= 1) {
            //原马来赔+自动水差>=1
            if (malayAndDiff.doubleValue() > 1) {
                //-[2-（计算后的值）]
                Double oddsValue = subDoubleTwo(new BigDecimal(2).subtract(malayAndDiff).doubleValue()) * (-1);
                finalMalayOddsValue = oddsValue;
            } else if (malayAndDiff.doubleValue() <= 0.01) {
                //原马来赔+自动水差<= 0.01)：新马来赔固定取0.01
                finalMalayOddsValue = 0.01D;
            } else {
                Double malaysia = malayAndDiff.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                finalMalayOddsValue = malaysia;
            }
        } else if (malayOddsValue > -1 && malayOddsValue < 0) {
            //原马来赔+自动水差<=-1
            if (malayAndDiff.doubleValue() <= -1) {
                //2 +（计算后的值）
                Double oddsValue = subDoubleTwo(new BigDecimal(2).add(malayAndDiff).doubleValue());
                finalMalayOddsValue = oddsValue;
            } else if (malayAndDiff.doubleValue() >= -0.01) {
                //原马来赔+自动水差>= -0.01)：新马来赔固定取-0.01
                finalMalayOddsValue = -0.01;
            } else {
                //抽水计算触发
                if (isSpread) {
                    //计算前的值为<0 && 计算后的值+spread>=0
                    Double oddsSpeadValue = subDoubleTwo(malayAndDiff.add(new BigDecimal(Double.toString(spread))).doubleValue());
                    if (oddsSpeadValue >= 0) {
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        return malaysia;
                    }
                } else {
                    //水差计算触发
                    //计算前的值为<0 && 计算后的值>0,触发封盘，前端提醒[设置水差(x)将触发封盘]
                    if (malayAndDiff.doubleValue() > 0) {
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        return malaysia;
                    }
                    //计算前的值为<0 && 计算后的值+spread>=0
                    Double oddsSpeadValue = subDoubleTwo(malayAndDiff.add(new BigDecimal(Double.toString(spread))).doubleValue());
                    if (oddsSpeadValue >= 0) {
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        return malaysia;
                    }
                }
                Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                finalMalayOddsValue = malaysia;
            }
        } else {
            return 0D;
        }
        return finalMalayOddsValue;
    }

    public static double subDoubleTwo(double d) {
        DecimalFormat dFormat = new DecimalFormat();
        dFormat.setMaximumFractionDigits(2);
        dFormat.setGroupingSize(0);
        dFormat.setRoundingMode(RoundingMode.FLOOR);
        return Double.parseDouble(dFormat.format(d));
    }

    public static double arithmeticMALAYTest(Double malayOddsValue, Double diffValue, Double spread, boolean isSpread) {
        StringBuffer sb = new StringBuffer("开始测试：");
        sb.append(isSpread ? "margin" : "水  差");
        sb.append("【");
        sb.append("malayOddsValue:" + malayOddsValue + ",");
        sb.append("diffValue:" + diffValue + ",");
        sb.append("spread:" + spread + ",");
        sb.append("isSpread:" + isSpread + ",");

        //马来赔率 malayOddsValue
        //马来赔率减去spread或者加上水差
        BigDecimal malayAndDiff;
        if (isSpread) {
            diffValue = -diffValue;
        }
        sb.append("diffValue:" + diffValue + ",");
        malayAndDiff = new BigDecimal(Double.toString(malayOddsValue)).add(new BigDecimal(Double.toString(diffValue)));
        sb.append("malayAndDiff:" + malayAndDiff + ",");
        if (malayOddsValue > 0 && malayOddsValue <= 1) {
            //原马来赔+自动水差>=1
            if (malayAndDiff.doubleValue() > 1) {
                //-[2-（计算后的值）]
                Double oddsValue = subDoubleTwo(new BigDecimal(2).subtract(malayAndDiff).doubleValue()) * (-1);
                sb.append("oddsValue1:" + oddsValue + ",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return oddsValue;
            } else if (malayAndDiff.doubleValue() <= 0.01) {
                //原马来赔+自动水差<= 0.01)：新马来赔固定取0.01
                sb.append("oddsValue2:" + 0.01 + ",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return 0.01;
            } else {
                Double malaysia = malayAndDiff.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                sb.append("oddsValue3:" + malaysia + ",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return malaysia;
            }
        } else if (malayOddsValue > -1 && malayOddsValue < 0) {
            //原马来赔+自动水差<=-1
            if (malayAndDiff.doubleValue() <= -1) {
                //2 +（计算后的值）
                Double oddsValue = subDoubleTwo(new BigDecimal(2).add(malayAndDiff).doubleValue());
                sb.append("oddsValue4:" + oddsValue + ",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return oddsValue;
            } else if (malayAndDiff.doubleValue() >= -0.01) {
                //原马来赔+自动水差>= -0.01)：新马来赔固定取-0.01
                sb.append("oddsValue5:" + -0.01 + ",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return -0.01;
            } else {
                //抽水计算触发
                if (isSpread) {
                    //计算前的值为<0 && 计算后的值+spread>=0
                    Double oddsSpeadValue = subDoubleTwo(malayAndDiff.add(new BigDecimal(Double.toString(spread))).doubleValue());
                    if (oddsSpeadValue >= 0) {
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        sb.append("oddsValue6:" + malaysia + ",");
                        sb.append("】 ");
                        System.out.println(sb.toString());
                        return malaysia;
                    }
                } else {
                    //水差计算触发
                    //计算前的值为<0 && 计算后的值>0,触发封盘，前端提醒[设置水差(x)将触发封盘]
                    if (malayAndDiff.doubleValue() > 0) {
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        sb.append("oddsValue7:" + malaysia + ",");
                        sb.append("】 ");
                        System.out.println(sb.toString());
                        return malaysia;
                    }
                    //计算前的值为<0 && 计算后的值+spread>=0
                    Double oddsSpeadValue = subDoubleTwo(malayAndDiff.add(new BigDecimal(Double.toString(spread))).doubleValue());
                    if (oddsSpeadValue >= 0) {
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        sb.append("oddsValue8:" + malaysia + ",");
                        sb.append("】 ");
                        System.out.println(sb.toString());
                        return malaysia;
                    }
                }
                Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                sb.append("oddsValue9:" + malaysia + "");
                sb.append("】 ");
                System.out.println(sb.toString());
                return malaysia;
            }
        } else {
            sb.append("oddsValue10:异常");
            sb.append("】");
            System.out.println(sb.toString());
            return -999;
        }

    }

    public static void main(String[] args) {
        double malayOddsValue = 0.95;
        double margin = 0.12;
        double diffValue = 0.02;
        double malay = arithmeticMALAYTest(malayOddsValue, margin / 2, margin, true);
        double malay2 = arithmeticMALAYTest(malay, diffValue, margin, false);
        System.out.println(malay2);
    }
}
