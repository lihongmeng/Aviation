package com.jxntv.base.utils;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;

import com.jxntv.base.R;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.LogUtils;

/**
 * 字符串工具类
 */
public final class StringUtils {
    /**
     * 自定义数据过滤掉空白字符，统一用空格替换
     *
     * @param text
     * @return
     */
    public static String filterWhiteSpace(String text) {
        if (!TextUtils.isEmpty(text)) {
            text = text.replaceAll("[\t\r\n]", " ").trim();
        }
        return text;
    }

    public static String changeToString(int i) {
        return String.valueOf(i);
    }

    public static String showMaxLengthString(String s, int max) {
        if (s == null || s.length() <= max || max == 0) {
            return s;
        } else {
            return showMaxLength(s,max-1) + "...";
        }
    }

    public static String showMaxLength(String s, int max) {
        if (s == null || s.length() <= max || max == 0) {
            return s;
        } else {
            char[] chars = s.toCharArray();
            StringBuilder stringBuilder = new StringBuilder();
            int sum = 0;
            for (int i=0;i< chars.length;i++){
                if (sum >= max -1){
                    break;
                }
                if (isEmojiCharacter(chars[i])){
                    stringBuilder.append(chars[i]);
                    stringBuilder.append(chars[i+1]);
                    i = i + 1;
                    sum = i;
                }else {
                    stringBuilder.append(chars[i]);
                    sum = i;
                }
            }
            return stringBuilder.toString() ;
        }
    }

    private static boolean isEmojiCharacter(char codePoint) {
//        return (codePoint == 0x0) ||
//                (codePoint == 0x9) ||
//                (codePoint == 0xA) ||
//                (codePoint == 0xD) ||
//                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
//                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
//                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));

        if ((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0x29))
                || ((codePoint >= 0x2A) && (codePoint <= 0x3A))
                || ((codePoint >= 0x40) && (codePoint <= 0xA8))
                || ((codePoint >= 0xAF) && (codePoint <= 0x203B))
                || ((codePoint >= 0x203D) && (codePoint <= 0x2048))
                || ((codePoint >= 0x2050) && (codePoint <= 0x20e2))
                || ((codePoint >= 0x20e4) && (codePoint <= 0x2100))
                || ((codePoint >= 0x21AF) && (codePoint <= 0x2300))
                || ((codePoint >= 0x23FF) && (codePoint <= 0X24C1))
                || ((codePoint >= 0X24C3) && (codePoint <= 0x2500))
                || ((codePoint >= 0x2800) && (codePoint <= 0x2933))
                || ((codePoint >= 0x2936) && (codePoint <= 0x2AFF))
                || ((codePoint >= 0x2C00) && (codePoint <= 0x3029))
                || ((codePoint >= 0x3031) && (codePoint <= 0x303C))
                || ((codePoint >= 0x303E) && (codePoint <= 0x3296))
                || ((codePoint >= 0x32A0) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFE0E))
                || ((codePoint >= 0xFE10) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
            return false;
        }
        return true;
    }


    public static String getGender(int gender){
        return getGender(gender,false);
    }

    public static String getGender(int gender,boolean isMe) {
        if (isMe){
            return "我";
        }else if (gender == 0) {
            return "Ta";
        } else if (gender == 1) {
            return "他";
        } else {
            return "她";
        }
    }


    public static boolean IsZero(String count) {
        return (TextUtils.isEmpty(count) || "0".equals(count));
    }

    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(String str){
        return TextUtils.isEmpty(str);
    }

}
