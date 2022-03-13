package com.jxntv.image;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.widget.R;

/**
 * 图片加载
 *
 *
 * @since 2020-01-21 19:55
 */
public final class ImageLoaderManager {
  //<editor-fold desc="属性">
  @Nullable
  private static ImageLoader sImageLoader;
  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private ImageLoaderManager() {
    throw new IllegalStateException("no instance !!!");
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 设置 ImageLoader {@link ImageLoader}
   *
   * @param loader ImageLoader
   */
  public static void setImageLoader(@NonNull ImageLoader loader) {
    sImageLoader = loader;
  }

  public static void loadImage(
      @NonNull ImageView imageView,
      @Nullable String url,
      @Nullable Object data,
      @NonNull LoadImageOption option) {
    if (sImageLoader != null) {
      if (url != null) {
        sImageLoader.loadImage(imageView, url, option);
      } else if (data != null) {
        sImageLoader.loadImage(imageView, data, option);
      } else {
        sImageLoader.loadImage(imageView, option);
      }
    }
  }

  public static void loadImage(@NonNull ImageView imageView, @Nullable String url,boolean centerCrop) {
    loadImage(imageView,url,R.drawable.default_imageload_bg,centerCrop);
  }

  public static void loadHeadImage(@NonNull ImageView imageView, @Nullable String url) {
    loadImage(imageView,url,R.drawable.ic_default_avatar,true);
  }

  public static void loadImage(@NonNull ImageView imageView,@Nullable String url,@DrawableRes int resId,boolean centerCrop) {
    LoadImageOption.Builder optionBuilder = new LoadImageOption.Builder();
    // 占位符
    optionBuilder.placeholder(resId);
    if (centerCrop){
      optionBuilder.centerCrop();
    }
    // 加载图片
    loadImage(imageView, url, null, optionBuilder.build());
  }


  /**
   * 预加载
   * @param context 上下文
   * @param url     地址
   */
  public static void preload(@NonNull Context context,@NonNull String url){
   sImageLoader.preloadImage(context,url);
  }
  //</editor-fold>
}
