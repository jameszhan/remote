/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.toolkit.base;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/23/14
 *         Time: 7:37 AM
 */
public final class Decimals {
    private Decimals(){}

    private static final String DEFAULT_PARRERN = "0.00";
    private static final BigDecimal HANDRED = BigDecimal.valueOf(100);

    public static BigDecimal centToYuan(long priceInCent) {
        return BigDecimal.valueOf(priceInCent, 2);
    }

    public static long yuanToCent(double priceInYuan) {
        return BigDecimal.valueOf(priceInYuan).multiply(HANDRED).longValue();
    }

    public static String formatValue(double value) {
        return formatValue(DEFAULT_PARRERN, value);
    }

    public static String formatValue(String pattern, double value) {
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(value);
    }

    public static String formatCentToYuan(long value) {
        return formatCentToYuan(DEFAULT_PARRERN, value);
    }

    public static String formatCentToYuan(String pattern, long value) {
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(centToYuan(value).doubleValue());
    }

    public static double add(double v1, double v2) {
        return BigDecimal.valueOf(v1).add(BigDecimal.valueOf(v2)).doubleValue();
    }

    public static double subtract(double v1, double v2) {
        return BigDecimal.valueOf(v1).subtract(BigDecimal.valueOf(v2)).doubleValue();
    }

    public static double multiple(double v1, double v2) {
        return BigDecimal.valueOf(v1).multiply(BigDecimal.valueOf(v2)).doubleValue();
    }

    public static double divide(double v1, long v2) {
        return divide(v1, v2, 3);
    }

    public static double divide(double v1, double v2, int scale) {
        return BigDecimal.valueOf(v1).divide(BigDecimal.valueOf(v2), scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double divide(double v1, long v2, int scale) {
        return BigDecimal.valueOf(v1).divide(BigDecimal.valueOf(v2), scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
