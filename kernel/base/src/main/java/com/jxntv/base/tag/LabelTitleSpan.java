package com.jxntv.base.tag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.style.ReplacementSpan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.base.R;

/**
 * 标签视图span类
 */
public class LabelTitleSpan extends ReplacementSpan {

    /** 标签文字大小--dp单位，不可改变大小 */
    private int mLabelTextSize;
    /** 文字画笔 */
    private Paint mTextPaint;
    /** 上下文 */
    private Context mContext;
    /** 当前span对应的tag类型 */
    private @TagHelper.GvideoTagType int mType;
    /** icon本地资源id */
    private int mIconRes;
    /** 对应的string tag标签 */
    private String mStringTag = "";
    /** label标签整体宽度 */
    private int mLabelWidth;
    /** label标签整体宽度 */
    private int mLabelHeight;
    /** Label背景渐变色起始颜色 */
    private int mLabelBackStartColor;
    /** Label背景渐变色结束颜色 */
    private int mLabelBackEndColor;
    /** 标签文字颜色 */
    private int mTextColor;
    /** 圆角大小 */
    private int mBackCornerRadius;
    /** icon左距离 */
    private int mIconLeftMargin;
    /** icon 和text的间隔 */
    private int mIconTextInterval;
    /** icon大小 */
    private int mIconSize;

    /**
     * 构造函数
     *
     * @param context   上下文环境
     * @param type      tag类型
     */
    public LabelTitleSpan(Context context, @TagHelper.GvideoTagType int type) {
        mContext = context;
        mType = type;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mLabelTextSize = (int) mContext.getResources().getDimension(
                R.dimen.t_font05);
        mLabelWidth = (int) mContext.getResources().getDimension(
                R.dimen.label_width);
        mLabelHeight = (int) mContext.getResources().getDimension(
                R.dimen.label_height);

        mLabelBackStartColor = mContext.getResources().getColor(
                R.color.c_gradual01_start);
        mLabelBackEndColor = mContext.getResources().getColor(
                R.color.c_gradual01_end);
        mTextColor =  mContext.getResources().getColor(
                R.color.t_color05);

        mBackCornerRadius = (int) mContext.getResources().getDimension(
                R.dimen.label_radio_size);
        mIconLeftMargin = (int) mContext.getResources().getDimension(
                R.dimen.label_icon_left_margin);
        mIconTextInterval = (int) mContext.getResources().getDimension(
                R.dimen.label_more_icon_text_left_interval);

        mIconSize = (int)  mContext.getResources().getDimension(
                R.dimen.label_item_img_size);

        int stringRes = TagHelper.getStringIdByTagType(mType);
        if (stringRes > 0) {
            mStringTag =  mContext.getResources().getString(stringRes);
        }
        mIconRes = TagHelper.getDrawableIdByTagType(mType);

        // 初始化文字画笔
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mLabelTextSize);
    }


    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                     int bottom, @NonNull Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;

        int borderStartY = (int) (y + metrics.ascent + (textHeight - mLabelHeight) / 2);

        // 1. 背景色
        RectF background = new RectF(x, borderStartY, x + mLabelWidth, borderStartY + mLabelHeight);
        LinearGradient linearGradient = new LinearGradient(mLabelWidth, mLabelHeight, 0, 0,
                mLabelBackStartColor, mLabelBackEndColor, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        canvas.drawRoundRect(background, mBackCornerRadius, mBackCornerRadius, paint);

        // 2.Icon
        if (mIconRes > 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mIconRes);
            int iconStartY = (int) (y + metrics.ascent + (metrics.descent - metrics.ascent - mIconSize) / 2);
            canvas.drawBitmap(bitmap, x + mIconLeftMargin, iconStartY, paint);
        }

        // 3. text
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mLabelTextSize);
        Paint.FontMetrics textMetrics = mTextPaint.getFontMetrics();
        float textBaseLineY = (int) (background.centerY() + (textMetrics.descent - textMetrics.ascent) / 2
                - textMetrics.descent);
        float textStartX = x + mIconLeftMargin + (mIconRes > 0
                ? mIconSize + mIconTextInterval : 0);
        canvas.drawText(mStringTag, textStartX, textBaseLineY, mTextPaint);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end,
                       @Nullable Paint.FontMetricsInt fm) {
        return mLabelWidth;
    }
}
