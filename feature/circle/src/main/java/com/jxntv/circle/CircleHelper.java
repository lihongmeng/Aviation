package com.jxntv.circle;

import android.content.Context;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jxntv.base.transformation.GlideRoundStrokeTransform;
import com.jxntv.utils.SizeUtils;

public class CircleHelper {

    private volatile static CircleHelper singleInstance = null;

    private CircleHelper() {
    }

    public static CircleHelper getInstance() {
        if (singleInstance == null) {
            synchronized (CircleHelper.class) {
                if (singleInstance == null) {
                    singleInstance = new CircleHelper();
                }
            }
        }
        return singleInstance;
    }

    public void loadCircleCover(Fragment fragment, String url, ImageView targetView) {
        Glide.with(fragment)
                .load(url)
                .apply(new RequestOptions().transform(
                        new CenterCrop(),
                        new RoundedCorners(SizeUtils.dp2px(10)
                        )
                ))
                .into(targetView);
    }

    public void loadCircleCover(Context context, String url, ImageView targetView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().transform(
                        new CenterCrop(),
                        new RoundedCorners(SizeUtils.dp2px(5)
                        )
                ))
                .into(targetView);
    }

    public void loadCircleCover(Context context, String borderColor, int borderWidth,String url, ImageView targetView) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().transform(
                        new CenterCrop(),
                        new GlideRoundStrokeTransform(context,5,borderColor,borderWidth)
                ))
                .into(targetView);
    }

}
