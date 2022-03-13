package com.jxntv.base.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.jxntv.base.R;
import com.jxntv.image.ImageLoader;
import com.jxntv.image.LoadImageOption;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片加载实现类
 *
 * @since 2020/01/25 00:55
 */
public final class ImageLoaderImpl implements ImageLoader {
  //<editor-fold desc="方法实现">
  @Override
  public void loadImage(
      @NonNull ImageView imageView,
      @NonNull String url,
      @NonNull LoadImageOption option) {
    doLoadImage(imageView, url, option);
  }

  @Override
  public void loadImage(
      @NonNull ImageView imageView,
      @NonNull Object data,
      @NonNull LoadImageOption option) {
    doLoadImage(imageView, data, option);
  }

  @Override
  public void loadImage(@NonNull ImageView imageView, @NonNull LoadImageOption option) {
    doLoadImage(imageView, null, option);
  }

  @Override
  public void preloadImage(@NonNull Context context, String url) {
    Glide.with(context).load(url).dontAnimate().diskCacheStrategy(DiskCacheStrategy.DATA).preload();
  }

  /**
   * 加载图片
   *
   * @param imageView 图片控件
   * @param data 图片数据
   * @param option 加载选项
   */
  @SuppressLint("CheckResult")
  private void doLoadImage(
      @NonNull ImageView imageView,
      @Nullable Object data,
      @NonNull LoadImageOption option) {
    // 检测 Context
    final Context context = imageView.getContext();
    if (context == null) {
      return;
    }
    if (context instanceof Activity) {
      if (((Activity) context).isFinishing()) {
        return;
      }
    }
    // 检查 tag
    if (!option.isForceLoad()) {
      Object glideImageTag = imageView.getTag(R.integer.glide_image_tag);
      if (glideImageTag != null && glideImageTag.equals(data)) {
        return;
      }
    }
    // 处理参数
    GlideRequest<?> requests = GlideApp.with(context).asBitmap();
    // 图片类型
    if (option.asDrawable()) {
      requests = GlideApp.with(context).asDrawable();
    } else if (option.asGif()) {
      requests = GlideApp.with(context).asGif();
    }
    // 数据类型
    if (data instanceof String) {
      requests = requests.load((String) data);
    } else if (data instanceof File) {
      requests = requests.load((File) data);
    } else if (data instanceof Bitmap) {
      requests = requests.load((Bitmap) data);
    } else if (data instanceof Drawable) {
      requests = requests.load((Drawable) data);
    } else if (data instanceof Uri) {
      requests = requests.load((Uri) (data));
    } else if (data instanceof Integer) {
      requests = requests.load((Integer) data);
    } else if (data instanceof byte[]) {
      requests = requests.load((byte[]) data);
    } else {
      requests = requests.load(data);
    }
    // 占位图
    // placeholder
    if (option.getPlaceholderResId() != 0) {
      requests = requests.placeholder(option.getPlaceholderResId());
    } else if (option.getPlaceholderDrawable() != null) {
      requests = requests.placeholder(option.getPlaceholderDrawable());
    }
    // error
    if (option.getErrorResId() != 0) {
      requests = requests.error(option.getErrorResId());
    } else if (option.getErrorDrawable() != null) {
      requests = requests.error(option.getErrorDrawable());
    }
    // fallback
    if (option.getFallbackResId() != 0) {
      requests = requests.fallback(option.getFallbackResId());
    } else if (option.getFallbackDrawable() != null) {
      requests = requests.fallback(option.getFallbackDrawable());
    }
    // 转换
    if (option.asBitmap()) {
      List<Transformation<Bitmap>> transformationList = new ArrayList<>();
      if (option.centerCrop()) {
        transformationList.add(new CenterCrop());
      }
      if (option.fitCenter()) {
        transformationList.add(new FitCenter());
      }
      if (option.circleCrop()) {
        transformationList.add(new CircleCrop());
      }
      if (option.centerInside()) {
        transformationList.add(new CenterInside());
      }
      if (option.getRoundedCorners() > 0) {
        transformationList.add(new RoundedCorners(option.getRoundedCorners()));
      }
      if (!transformationList.isEmpty()) {
        //noinspection unchecked
        requests = requests.transform(transformationList.toArray(new Transformation[0]));
      }
    }
    // 过渡
    if (option.crossFade()) {


    }
    // 记录 tag
    imageView.setTag(R.integer.glide_image_tag, data);
    // 是否强制加载
    if (option.isForceLoad()) {
      requests = requests.skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
    }else {
      requests.diskCacheStrategy(DiskCacheStrategy.DATA);
    }
    requests.dontAnimate();
    // 加载图片
    requests.into(imageView);
  }
  //</editor-fold>
}
