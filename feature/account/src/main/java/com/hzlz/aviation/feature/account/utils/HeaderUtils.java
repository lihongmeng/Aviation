package com.hzlz.aviation.feature.account.utils;

import android.content.Context;
import android.graphics.Bitmap;

public class HeaderUtils {

    public Bitmap headerBitmap = null;

    private volatile static HeaderUtils singleInstance = null;

    private HeaderUtils() {
    }

    public static HeaderUtils getInstance() {
        if (singleInstance == null) {
            synchronized (HeaderUtils.class) {
                if (singleInstance == null) {
                    singleInstance = new HeaderUtils();
                }
            }
        }
        return singleInstance;
    }

    public void preHeaderImage(Context context) {
        // headerBitmap = null;
        // Glide.with(context)
        //         .asBitmap()
        //         .load(UserManager.getCurrentUser().getAvatarUrl())
        //         .diskCacheStrategy(DiskCacheStrategy.ALL)
        //         .placeholder(ContextCompat.getDrawable(context, R.drawable.media_default_cover_bg))
        //         .into(new CustomTarget<Bitmap>() {
        //             @Override
        //             public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        //                 headerBitmap = resource;
        //             }
        //
        //             @Override
        //             public void onLoadCleared(@Nullable Drawable placeholder) {
        //
        //             }
        //         });
    }

}
