package com.jxntv.image;

import android.content.Context;
import android.widget.ImageView;
import androidx.annotation.NonNull;

/**
 * 图片加载接口
 *
 *
 * @since 2020-01-21 19:58
 */
public interface ImageLoader {
  /**
   * 加载图片
   *
   * @param imageView Image View
   * @param url 图片 url
   * @param option 加载图片选项
   */
  void loadImage(
      @NonNull ImageView imageView,
      @NonNull String url,
      @NonNull LoadImageOption option
  );

  /**
   * 加载图片
   *
   * @param imageView Image View
   * @param data 图片数据
   * @param option 加载图片选项
   */
  void loadImage(
      @NonNull ImageView imageView,
      @NonNull Object data,
      @NonNull LoadImageOption option
  );

  /**
   * 加载图片
   *
   * @param imageView Image View
   * @param option 记载图片选项
   */
  void loadImage(@NonNull ImageView imageView, @NonNull LoadImageOption option);


  /**
   * 预加载图片
   */
  void preloadImage(@NonNull Context context, String url);
}
