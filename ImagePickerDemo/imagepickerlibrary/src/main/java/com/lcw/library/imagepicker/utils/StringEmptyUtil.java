package com.lcw.library.imagepicker.utils;

import java.text.DecimalFormat;

/**
 * @describe: 字符串操作封装
 * @author: yyh
 * @createTime: 2018/5/29 10:56
 * @className: StringEmptyUtil
 */
public final class StringEmptyUtil {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() < 1;
    }

    public static String getNotNullString(String str, String ret) {
        return str == null ? ret : str;
    }

    public static String getNotNullString(String str) {
        return getNotNullString(str, "");
    }

    /**
     * 是否相等(不区分大小写)
     * 如果两个都为null, 或者两个的值一样(不区分大小写), 则返回true
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 如果两个都为null, 或者两个的值一样(不区分大小写), 则返回true, 否则返回false.
     */
    private static boolean equalsIgnore(String str1, String str2) {
        return str1 == str2 || equalsIgnoreNotNull(str1, str2);
    }

    /**
     * 是否相等(不区分大小写)
     * 如果两个的值一样, 则返回true
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 如果两个的值一样(不区分大小写), 则返回true, 否则返回false.
     */
    public static boolean equalsIgnoreNotNull(String str1, String str2) {
        return str1 != null && str1.equalsIgnoreCase(str2);
    }

    /**
     * 获取文件大小
     *
     * @param size file.length()
     */
    public static String formetFileSize(long size) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String result = "";
        if (size < 1024) {
            result = df.format((double) size) + "B";
        } else if (size < 1048576) {
            result = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            result = df.format((double) size / 1048576) + "M";
        } else {
            result = df.format((double) size / 1073741824) + "G";
        }
        return result;
    }
}