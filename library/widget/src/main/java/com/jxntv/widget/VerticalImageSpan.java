package com.jxntv.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 竖直视图imageSpan
 */
public class VerticalImageSpan extends ImageSpan {

    /** 以旁边的字体为参照居中，用于图文混排 */
    public static final int TYPE_VERTICAL_WITH_TEXT = 0;
    /** 无需考虑字体，自身居中即可 */
    public static final int TYPE_VERTICAL_NORMAL = 1;

    @IntDef({TYPE_VERTICAL_WITH_TEXT, TYPE_VERTICAL_NORMAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VerticalType {
    }

    /** 当前竖直模板类型 */
    private int mVerticalType;

    /**
     * 构造函数
     */
    public VerticalImageSpan(Drawable drawable, @VerticalType int type) {
        super(drawable);
        mVerticalType = type;
    }
    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fontMetricsInt != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fontMetricsInt.ascent = -bottom;
            fontMetricsInt.top = -bottom;
            fontMetricsInt.bottom = top;
            fontMetricsInt.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom,  @NonNull Paint paint) {

        Drawable drawable = getDrawable();
        // 以字体为中心居中
        int iconStartY;
        if (mVerticalType == TYPE_VERTICAL_WITH_TEXT) {
            Paint.FontMetrics metrics = paint.getFontMetrics();
            iconStartY = (int) (y + metrics.ascent +
                    (metrics.descent - metrics.ascent - drawable.getBounds().bottom) / 2);
        } else {
            iconStartY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;

        }
        canvas.save();
        canvas.translate(x, iconStartY);
        drawable.draw(canvas);
        canvas.restore();
    }
}
