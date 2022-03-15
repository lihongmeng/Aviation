package com.hzlz.aviation.library.widget.bindingadapter;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.hzlz.aviation.library.widget.image.ImageLoaderManager;
import com.hzlz.aviation.library.widget.image.LoadImageOption;

/**
 * ImageView 数据绑定扩展
 *
 *
 * @since 2020-01-21 17:31
 */
public final class ImageViewBindingAdapter {
  @BindingAdapter(value = {
      // 数据部分
      // 图片 url
      "imageUrl",
      // 图片数据：具体可以参考 Glide 支持的数据类型
      "imageData",
      // 强制加载
      "forceLoad",
      // 图片类型
      "asDrawable",
      "asBitmap",
      "asGif",
      // 占位符
      "placeholder",
      "error",
      "fallback",
      // 变换
      "centerCrop",
      "fitCenter",
      "circleCrop",
      "centerInside",
      "roundedCorners",
      "transforms",
      // 过渡
      "crossFade",
      "transition",
      // 额外信息，用于扩展
      "extras"
  }, requireAll = false)
  public static void loadImage(
      @NonNull ImageView imageView,
      // 数据
      @Nullable String url,
      @Nullable Object data,
      // 强制加载
      boolean forceLoad,
      // 图片类型
      boolean asDrawable,
      boolean asBitmap,
      boolean asGif,
      // 占位符
      @Nullable Object placeholder,
      @Nullable Object error,
      @Nullable Object fallback,
      // 变换
      boolean centerCrop,
      boolean fitCenter,
      boolean circleCrop,
      boolean centerInside,
      float roundedCorners,
      @Nullable Object transforms,
      // 过渡
      boolean crossFade,
      @Nullable Object transition,
      // 额外信息
      @Nullable Object extras) {
    LoadImageOption.Builder optionBuilder = new LoadImageOption.Builder();
    // 强制加载
    optionBuilder.forceLoad(forceLoad);
    // 图片类型
    if (asDrawable) {
      optionBuilder.asDrawable();
    }
    if (asBitmap) {
      optionBuilder.asBitmap();
    }
    if (asGif) {
      optionBuilder.asGif();
    }
    // 占位符
    if (placeholder instanceof Integer) {
      optionBuilder.placeholder((int) placeholder);
    } else if (placeholder instanceof Drawable) {
      optionBuilder.placeholder((Drawable) placeholder);
    } else {
      if (placeholder != null) {
        throw new IllegalArgumentException("invalid placeholder !!!");
      }
    }
    if (error instanceof Integer) {
      optionBuilder.error((int) error);
    } else if (error instanceof Drawable) {
      optionBuilder.error((Drawable) error);
    } else {
      if (error != null) {
        throw new IllegalArgumentException("invalid error !!!");
      }
    }
    if (fallback instanceof Integer) {
      optionBuilder.fallback((int) fallback);
    } else if (fallback instanceof Drawable) {
      optionBuilder.fallback((Drawable) fallback);
    } else {
      if (fallback != null) {
        throw new IllegalArgumentException("invalid fallback !!!");
      }
    }
    // 变换
    if (centerCrop) {
      optionBuilder.centerCrop();
    }
    if (fitCenter) {
      optionBuilder.fitCenter();
    }
    if (circleCrop) {
      optionBuilder.circleCrop();
    }
    if (centerInside) {
      optionBuilder.centerInside();
    }
    optionBuilder.roundedCorners((int) roundedCorners);
    if (transforms != null) {
      optionBuilder.transforms(transforms);
    }
    // 过渡
    if (crossFade) {
      optionBuilder.crossFade();
    }
    if (transition != null) {
      optionBuilder.transition(transition);
    }
    // 额外信息
    if (extras != null) {
      optionBuilder.extras(extras);
    }
    // 加载图片
    ImageLoaderManager.loadImage(imageView, url, data, optionBuilder.build());
  }
}
