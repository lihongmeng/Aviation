package com.hzlz.aviation.kernel.base.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.hzlz.aviation.kernel.base.GlideCornersTranForm;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.library.widget.widget.GVideoImageView;

/**
 * media image视图
 */
public class MediaImageView extends GVideoImageView {

    /**
     * 构造函数
     */
    public MediaImageView(Context context) {
        super(context);
    }

    /**
     * 构造函数
     */
    public MediaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造函数
     */
    public MediaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 加载圆角图片
     *
     * @param url        图片url
     * @param connerSize 圆角大小
     * @return 返回对应的imageView
     */
    public MediaImageView loadImageWithCorner(String url, int connerSize) {
        if (TextUtils.isEmpty(url)) {
            return this;
        }

        Glide.with(this)
                .load(url)
                .apply(new RequestOptions().transform(new CenterCrop(),
                        new RoundedCorners(connerSize)))
                .into(this);
        return this;
    }

    /**
     * 加载圆型图片
     *
     * @param url 图片url
     * @return 返回对应的imageView
     */
    public MediaImageView loadImageWithCircleCrop(String url) {
        if (TextUtils.isEmpty(url)) {
            return this;
        }

        Glide.with(this)
                .load(url)
                .optionalCircleCrop()
                .into(this);
        return this;
    }

    /**
     * 加载部分圆角图片
     *
     * @param drawable    待加载的图片
     * @param connerSize  圆角大小
     * @param leftTop     是否无视左上角
     * @param rightTop    是否无视右上角
     * @param leftBottom  是否无视左下角
     * @param rightBottom 是否无视右下角
     * @return 返回对应的imageView
     */
    public MediaImageView loadImageWithExceptCorner(Drawable drawable, int connerSize,
                                                    boolean leftTop, boolean rightTop,
                                                    boolean leftBottom, boolean rightBottom) {
        if (drawable == null) {
            return this;
        }
        GlideCornersTranForm tranForm = new GlideCornersTranForm(getContext(), connerSize);
        tranForm.setExceptCorner(leftTop, rightTop, leftBottom, rightBottom);

        Glide.with(this)
                .load(drawable)
                .apply(new RequestOptions().transform(new CenterCrop(), tranForm))
                .into(this);
        return this;
    }

    /**
     * 加载部分圆角图片
     *
     * @param drawable    待加载的图片
     * @param leftTop     是否无视左上角
     * @param rightTop    是否无视右上角
     * @param leftBottom  是否无视左下角
     * @param rightBottom 是否无视右下角
     * @return 返回对应的imageView
     */
    public MediaImageView loadImageWithExceptCorner(Drawable drawable, boolean leftTop,
                                                    boolean rightTop,
                                                    boolean leftBottom, boolean rightBottom) {
        int connerSize = (int) getContext().getResources().getDimension(
                R.dimen.media_normal_border_radius);
        return loadImageWithExceptCorner(drawable, connerSize, leftTop, rightTop, leftBottom,
                rightBottom);
    }

}
