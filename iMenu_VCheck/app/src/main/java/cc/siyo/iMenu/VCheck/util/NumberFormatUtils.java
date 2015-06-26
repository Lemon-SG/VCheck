package cc.siyo.iMenu.VCheck.util;

import java.text.DecimalFormat;

/**
 * Created by Lemon on 14-7-30.
 * 数字格式化工具类
 */
public class NumberFormatUtils {

    private static final String PATTERN2 = "#######.00";
    private static final String PATTERN1 = "#######.0";
    private static final DecimalFormat decimalFormat2 = new DecimalFormat(PATTERN2);
    private static final DecimalFormat decimalFormat1 = new DecimalFormat(PATTERN1);
    

    /**
     * 数字格式化，小数点后保留一位
     *
     * @param number float
     * @return string
     */
    public static String formatToOne(float number) {
        return decimalFormat1.format(number);
    }
    
    /**
     * 数字格式化，小数点后保留两位
     *
     * @param number float
     * @return string
     */
    public static String format(float number) {
        return decimalFormat2.format(number);
    }

    /**
     * 数字格式化，小数点后保留两位
     *
     * @param number String
     * @return string
     */
    public static String format(String number) {
        if (!StringUtils.isBlank(number)) {
            return decimalFormat2.format(number);
        }
        return number;
    }

    /**
     * 数字格式化，小数点后保留两位
     *
     * @param number double
     * @return string
     */
    public static String format(double number) {
        return decimalFormat2.format(number);
    }

    /**
     * 数字格式化，小数点后保留两位
     *
     * @param number long
     * @return string
     */
    public static String format(long number) {
        return decimalFormat2.format(number);
    }
}
