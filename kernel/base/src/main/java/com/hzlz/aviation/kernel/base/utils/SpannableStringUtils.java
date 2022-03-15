package com.hzlz.aviation.kernel.base.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/9/7
 * desc : 文字颜色工具类
 **/
public class SpannableStringUtils {

    /**
     * 设置部分文字颜色
     *
     * @param allString 全部文字
     * @param text      需要修改以颜色的文字
     * @param textColor 需要需改的颜色
     */
    public static SpannableStringBuilder setSpanColor(String allString, String text, @ColorRes int textColor) {
        return setSpanColor(allString, text, textColor, -1);
    }


    /**
     * 生成搜索title
     *
     * @param textView   待处理的text view
     * @param title      标题
     * @param searchWord 搜索关键词
     * @param colorInt   标题其他字颜色int
     */
    public static void createSearchTitle(TextView textView, String title,
                                         String searchWord, @ColorInt int colorInt) {
        if (textView == null || TextUtils.isEmpty(searchWord) || TextUtils.isEmpty(title)) {
            return;
        }

        int start = title.indexOf(searchWord);
        if (start < 0 || start + searchWord.length() > title.length()) {
            return;
        }
        SpannableString spanText;
        spanText = new SpannableString(title);

        // 前景色
        ForegroundColorSpan pendantTextSpan = new ForegroundColorSpan(GVideoRuntime.getAppContext().getResources()
                .getColor(R.color.color_e4344e));

        spanText.setSpan(pendantTextSpan, start, start + searchWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanText);
        textView.setTextColor(colorInt);
    }


    /**
     * 设置部分文字颜色
     *
     * @param allString 全部文字
     * @param text      需要修改以颜色的文字
     * @param textColor 需要需改的颜色
     * @param fontSize  文字大小 dp
     */
    public static SpannableStringBuilder setSpanColor(String allString, String text, @ColorRes int textColor, int fontSize) {
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(allString);
        if (TextUtils.isEmpty(text)) {
            return spannableBuilder;
        }
        Context context = GVideoRuntime.getAppContext();
        int index = allString.indexOf(text);
        index = Math.max(index, 0);
        int max = Math.min(text.length() + index, allString.length());
        //单独设置字体颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor(
                "#" + Integer.toHexString(ContextCompat.getColor(context, textColor))));
        spannableBuilder.setSpan(colorSpan, index, max, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (fontSize > 0) {
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(fontSize, true);
            spannableBuilder.setSpan(sizeSpan, index, max, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableBuilder;
    }

    /**
     * 设置部分文字颜色
     *
     * @param allString      全部文字
     * @param allStringColor 全部文字颜色
     * @param text           需要修改以颜色的文字
     * @param textColor      需要需改的颜色
     * @param fontSize       文字大小 dp
     */
    public static SpannableStringBuilder setSpanColor(
            String allString,
            @ColorRes int allStringColor,
            String text,
            @ColorRes int textColor,
            int fontSize
    ) {
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(allString);
        Context context = GVideoRuntime.getAppContext();
        if (!TextUtils.isEmpty(allString)) {
            ForegroundColorSpan allColorSpan = new ForegroundColorSpan(Color.parseColor(
                    "#" + Integer.toHexString(ContextCompat.getColor(context, allStringColor))));
            spannableBuilder.setSpan(allColorSpan, 0, allString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (TextUtils.isEmpty(text)) {
            return spannableBuilder;
        }

        int index = allString.indexOf(text);
        index = Math.max(index, 0);
        int max = Math.min(text.length() + index, allString.length());

        //单独设置字体颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor(
                "#" + Integer.toHexString(ContextCompat.getColor(context, textColor))));
        spannableBuilder.setSpan(colorSpan, index, max, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (fontSize > 0) {
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(fontSize, true);
            spannableBuilder.setSpan(sizeSpan, index, max, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableBuilder;
    }

    /**
     * 设置部分文字颜色
     *
     * @param allString        全部文字
     * @param allStringColor   全部文字颜色
     * @param content          需要修改以颜色的文字
     * @param contentTextColor 需要需改的颜色
     * @param contentTextSize  文字大小 dp
     */
    public static SpannableStringBuilder setSpanColor(
            String allString,
            @ColorRes int allStringColor,
            int allTextSize,
            int allTypeface,
            String content,
            @ColorRes int contentTextColor,
            int contentTextSize,
            int contentTypeface
    ) {
        if (TextUtils.isEmpty(allString)) {
            return new SpannableStringBuilder();
        }

        int allLength = allString.length();
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(allString);
        Context context = GVideoRuntime.getAppContext();

        // 设置全部字体的颜色
        ForegroundColorSpan allColorSpan = new ForegroundColorSpan(
                Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, allStringColor))));
        spannableBuilder.setSpan(allColorSpan, 0, allLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置全部字体的尺寸
        if (allTextSize > 0) {
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(allTextSize, true);
            spannableBuilder.setSpan(sizeSpan, 0, allLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 设置全部字体的粗细
        spannableBuilder.setSpan(new StyleSpan(allTypeface), 0, allLength, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        if (TextUtils.isEmpty(content)) {
            return spannableBuilder;
        }

        int index = allString.indexOf(content);
        index = Math.max(index, 0);
        int max = Math.min(content.length() + index, allString.length());

        //设置目标字体颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor(
                "#" + Integer.toHexString(ContextCompat.getColor(context, contentTextColor))));
        spannableBuilder.setSpan(colorSpan, index, max, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置目标字体尺寸
        if (contentTextSize > 0) {
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(contentTextSize, true);
            spannableBuilder.setSpan(sizeSpan, index, max, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 设置全部字体的粗细
        spannableBuilder.setSpan(new StyleSpan(contentTypeface), index, max, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return spannableBuilder;
    }

    /**
     * 设置部分文字颜色
     *
     * @param allString 全部文字
     * @param words     需要修改以颜色的文字
     * @param textColor 需要需改的颜色
     */
    public static SpannableStringBuilder setSpanColor(@NonNull String allString, List<String> words, @ColorRes int textColor) {
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(allString);
        if (words == null || words.size() == 0) {
            return spannableBuilder;
        }
        Context context = GVideoRuntime.getAppContext();
        @ColorInt
        int color = Color.parseColor(
                "#" + Integer.toHexString(ContextCompat.getColor(context, textColor)));
        for (String word : words) {
            if (allString.contains(word)) {
                int fromIndex = 0;
                int startIndex = allString.indexOf(word, fromIndex);
                while (startIndex != -1) {
                    int max = Math.min(word.length() + startIndex, allString.length());
                    //单独设置字体颜色
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
                    spannableBuilder.setSpan(colorSpan, startIndex, max, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    fromIndex += 1;
                    startIndex = allString.indexOf(word, fromIndex);
                }
            }
        }
        return spannableBuilder;
    }
}
