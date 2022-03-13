package com.jxntv.base.utils;

import android.graphics.Typeface;
import android.widget.TextView;

import com.jxntv.runtime.GVideoRuntime;

/**
 * @author huangwei
 * date : 2021/9/9
 * desc : 字体设置
 **/
public class TypefaceUtils {

    /**
     * 设置数字字体
     */
    public static void setNumberTypeface(TextView view){
        String fontPath = "font/number.ttf";
        Typeface typeface = Typeface.createFromAsset(GVideoRuntime.getAppContext().getAssets(),fontPath);
        view.setTypeface(typeface);
    }
}
