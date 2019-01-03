package com.microview.zuul.utils;

public class StringUtil {

    /**
     * 首字母转大写
     * @param string
     * @return
     */
    public static String toUpperFirstChar(String string) {
        char[] charArray = string.toCharArray();
        charArray[0] -= 32;
        return String.valueOf(charArray);
    }
}