package com.hzlz.aviation.library.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public static String match(String content, String patternValue) {
        if(TextUtils.isEmpty(content)){
            return "";
        }
        Pattern pattern = Pattern.compile(patternValue);
        Matcher matcher = pattern.matcher(content);
        String result = "";
        while (matcher.find()) {
            result = content.substring(matcher.start(), matcher.end());
        }
        return result;
    }

}
