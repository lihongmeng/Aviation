package com.hzlz.aviation.feature.search.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.hzlz.aviation.feature.search.SearchRuntime;
import com.hzlz.aviation.feature.search.R;

/**
 * 搜索富文本辅助类
 */
public class SearchSpanTextHelper {

    /**
     * 生成搜索title
     *
     * @param textView      待处理的text view
     * @param title         标题
     * @param searchWord    搜索关键词
     * @param colorInt      标题其他字颜色int
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
        ForegroundColorSpan pendantTextSpan = new ForegroundColorSpan(SearchRuntime.getAppContext().getResources()
                .getColor(R.color.color_e4344e));

        spanText.setSpan(pendantTextSpan, start, start + searchWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanText);
        textView.setTextColor(colorInt);
    }
}
