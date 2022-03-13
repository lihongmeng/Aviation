package com.jxntv.base.tag;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.TextView;
import androidx.annotation.ColorInt;

/**
 * 富文本辅助类
 */
public class TagTextHelper {

    /** feed 富文本占位string */
    private static final String PLACE_HOLDER = "holder  ";
    /** feed 富文本占位string长度 */
    private static final int PLACE_HOLDER_LENGTH = 6;
    /** 标记无需设置颜色 */
    private static final int WITHOUT_INT = -1;


    /**
     * 创建feed tag标题
     *
     * @param context   上下文
     * @param textView  文字视图
     * @param type      feed tag类型
     */
    public static void createTagTitle(Context context, TextView textView,
        @TagHelper.GvideoTagType int type) {
        createTagTitle(context, textView, "", type, WITHOUT_INT);
    }

    /**
     * 创建feed tag标题
     *
     * @param context   上下文
     * @param textView  文字视图
     * @param type      feed tag类型
     */
    public static void createTagTitle(Context context, TextView textView, String text,
                                      @TagHelper.GvideoTagType int type) {
        createTagTitle(context, textView, text, type, WITHOUT_INT);
    }

    /**
     * 创建feed tag标题
     *
     * @param context   上下文
     * @param textView  文字视图
     * @param type      feed tag类型
     * @param colorInt  文字颜色
     */
    public static void createTagTitle(Context context, TextView textView, String text,
                                      @TagHelper.GvideoTagType int type, @ColorInt int colorInt) {
        if (context == null || textView == null) {
            return;
        }
        SpannableString spanText;
        if (TagHelper.isModelValid(type)) {
            spanText = new SpannableString(PLACE_HOLDER + text);
            LabelTitleSpan titlePrefixSpan = new LabelTitleSpan(context, type);
            spanText.setSpan(titlePrefixSpan, 0, PLACE_HOLDER_LENGTH,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            spanText = new SpannableString(text);
        }
        textView.setText(spanText, TextView.BufferType.NORMAL);
        if (colorInt != WITHOUT_INT) {
            textView.setTextColor(colorInt);
        }
    }
}
