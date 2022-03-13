package com.jxntv.android.liteav.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import com.jxntv.android.liteav.R;
import com.jxntv.base.model.video.PendantModel;
import com.jxntv.widget.VerticalImageSpan;

/**
 * 富文本辅助类
 */
public class SpanTextHelper {

    /** feed 富文本占位string */
    private static final String PLACE_HOLDER = "holder  ";
    /** feed 富文本占位string长度 */
    private static final int PLACE_HOLDER_LENGTH = 6;
    /** 用于拼接的省略号 */
    private static final String ELLIPSIS_STRING = "...";

    /**
     * 创建feed 挂件标题
     *
     * @param context   上下文
     * @param textView  文字视图
     * @param model     feed挂件数据
     * @param colorInt  文字颜色
     */
    public static void createPendantTitle(Context context, TextView textView,
                                          @NonNull PendantModel model,
                                          @ColorInt int colorInt) {
        SpannableString spanText;

        String callWord = model.preTitle;
        String title = model.title;

        int drawableSize = (int) context.getResources().getDimension(
                R.dimen.t_font05);
        int textWidth = (int) context.getResources().getDimension(
                R.dimen.pendant_item_width);
        int maxWidth = textWidth * 2 - drawableSize;

        String text = callWord + title;
        int length = text.length();

        int measureNumbers = getAvailableNum(context, text, maxWidth);

        if (measureNumbers < length) {
            text = text.substring(0, measureNumbers - 1);
            text += ELLIPSIS_STRING;
        }

        spanText = new SpannableString(text + PLACE_HOLDER);

        // 前景色
        ForegroundColorSpan pendantTextSpan = new ForegroundColorSpan(context.getResources()
                .getColor(R.color.t_color06));

        spanText.setSpan(pendantTextSpan, 0, callWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 后缀图片
        Drawable drawable = context.getResources().getDrawable(R.drawable.pendant_more);
        drawable.setBounds(0, 0, drawableSize, drawableSize);
        VerticalImageSpan imageSpan = new VerticalImageSpan(drawable, VerticalImageSpan.TYPE_VERTICAL_WITH_TEXT);

        spanText.setSpan(imageSpan, text.length(), text.length() + PLACE_HOLDER_LENGTH,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spanText, TextView.BufferType.NORMAL);
        textView.setTextColor(colorInt);
    }

    /**
     * 获取可容纳的字符数量
     *
     * @param context   上下文
     * @param str       文字视图
     * @param width     视图宽度
     */
    private static int getAvailableNum(Context context, String str, int width){
        if(TextUtils.isEmpty(str) || width <= 0){
            return 0;
        }
        //字符串长度
        Paint paint = new Paint();
        paint.setTextSize(context.getResources().getDimension(R.dimen.pendant_dimen_size));
        //根据宽度得到字符数量
        return paint.breakText(str, true, width, null);
    }
}
