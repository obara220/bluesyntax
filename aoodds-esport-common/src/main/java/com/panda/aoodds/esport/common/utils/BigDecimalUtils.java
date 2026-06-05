package com.panda.aoodds.esport.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class BigDecimalUtils {

//    /**
//     * 小数点截取
//     *
//     * @param value
//     * @return
//     */
//    public static String scaleStr(String value, int scale) {
//        if(StringUtils.isBlank(value)){
//            return "";
//        }
//        return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_DOWN).toString();
//    }
//
//    public static double scaleStr(double value, int scale) {
//        return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
//    }


    public static double subDoubleTwo(double d) {
        DecimalFormat dFormat = new DecimalFormat();
        dFormat.setMaximumFractionDigits(2);
        dFormat.setGroupingSize(0);
        dFormat.setRoundingMode(RoundingMode.FLOOR);
        return Double.parseDouble(dFormat.format(d));
    }


    private static final int DEF_DIV_SCALE = 10;

    private BigDecimalUtils() {
    }

    public static double add(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.add(b2).doubleValue();
    }

    public static double add(double value1, double value2, double value3) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        BigDecimal b3 = BigDecimal.valueOf(value3);
        return b1.add(b2).add(b3).doubleValue();
    }

    public static double add(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.add(b2).doubleValue();
    }

    public static double subtract(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.subtract(b2).doubleValue();
    }

    public static double subtract(double value1, double value2, double value3) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        BigDecimal b3 = BigDecimal.valueOf(value3);
        return b1.subtract(b2).subtract(b3).doubleValue();
    }

    public static double subtract(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.subtract(b2).doubleValue();
    }

    public static Double multiply(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.multiply(b2).doubleValue();
    }

    public static Double multiply(double value1, double value2, int scale) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return scale(b1.multiply(b2).doubleValue(), scale);
    }

    public static double multiply(String value1, String value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.multiply(b2).doubleValue();
    }

    public static Double divide(double value1, double value2) {
        return divide(value1, value2, 10);
    }

    public static double divide(String value1, String value2) {
        return divide(value1, value2, 10);
    }

    public static double divide(double value1, double value2, int scale) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.divide(b2, scale, 1).doubleValue();
    }

    public static double divideUP(double value1, double value2, int scale) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.divide(b2, scale, 4).doubleValue();
    }

    public static double divide(String value1, String value2, int scale) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.divide(b2, scale, 4).doubleValue();
    }

    public static double scale(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, 4).doubleValue();
    }

    public static double scaleCrop(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, 1).doubleValue();
    }

    public static double scale(String value, int scale) {
        return (new BigDecimal(value)).setScale(scale, 4).doubleValue();
    }

    public static boolean equalTo(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return 0 == b1.compareTo(b2);
    }

    public static Double changeZero(Double value1) {
        return value1 == null ? 0.0 : value1;
    }

    public static Double changeZero(BigDecimal value1) {
        return value1 == null ? 0.0 : value1.doubleValue();
    }

    public static Integer changeZero(Integer value1) {
        return value1 == null ? 0 : value1;
    }

    public static int scaleNum(double value1) {
        BigDecimal num = BigDecimal.valueOf(value1);
        return num.scale();
    }
}