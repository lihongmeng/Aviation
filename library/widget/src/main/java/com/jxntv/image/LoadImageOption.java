package com.jxntv.image;

import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 加载图片选项
 *
 *
 * @since 2020-01-21 18:48
 */
public final class LoadImageOption {
  //<editor-fold desc="属性">
  // 强制加载
  private boolean mForceLoad;
  // 图片类型
  private boolean mAsDrawable;
  private boolean mAsBitmap;
  private boolean mAsGif;
  // 占位符
  private int mPlaceholderResId;
  @Nullable
  private Drawable mPlaceholderDrawable;
  private int mErrorResId;
  @Nullable
  private Drawable mErrorDrawable;
  private int mFallbackResId;
  @Nullable
  private Drawable mFallbackDrawable;
  // 变换
  private boolean mCenterCrop;
  private boolean mFitCenter;
  private boolean mCircleCrop;
  private boolean mCenterInside;
  private int mRoundedCorners;
  @Nullable
  private Object mTransforms;
  // 过渡
  private boolean mCrossFade;
  @Nullable
  private Object mTransition;
  // 额外信息，用于扩展
  @Nullable
  private Object mExtras;
  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private LoadImageOption(@NonNull Builder builder) {
    mForceLoad = builder.mForceLoad;

    mAsDrawable = builder.mAsDrawable;
    mAsBitmap = builder.mAsBitmap;
    mAsGif = builder.mAsGif;

    mPlaceholderResId = builder.mPlaceholderResId;
    mPlaceholderDrawable = builder.mPlaceholderDrawable;
    mErrorResId = builder.mErrorResId;
    mErrorDrawable = builder.mErrorDrawable;
    mFallbackResId = builder.mFallbackResId;
    mFallbackDrawable = builder.mFallbackDrawable;

    mCenterCrop = builder.mCenterCrop;
    mFitCenter = builder.mFitCenter;
    mCircleCrop = builder.mCircleCrop;
    mCenterInside = builder.mCenterInside;
    mRoundedCorners = builder.mRoundedCorners;
    mTransforms = builder.mTransforms;

    mCrossFade = builder.mCrossFade;
    mTransition = builder.mTransition;

    mExtras = builder.mExtras;
  }
  //</editor-fold>

  //<editor-fold desc="Builder">
  public static final class Builder {
    //<editor-fold desc="属性">
    // 强制加载
    private boolean mForceLoad;
    // 图片类型
    private boolean mAsDrawable;
    private boolean mAsBitmap;
    private boolean mAsGif;
    // 占位符
    private int mPlaceholderResId;
    @Nullable
    private Drawable mPlaceholderDrawable;
    private int mErrorResId;
    @Nullable
    private Drawable mErrorDrawable;
    private int mFallbackResId;
    @Nullable
    private Drawable mFallbackDrawable;
    // 变换
    private boolean mCenterCrop;
    private boolean mFitCenter;
    private boolean mCircleCrop;
    private boolean mCenterInside;
    private int mRoundedCorners;
    @Nullable
    private Object mTransforms;
    // 过渡
    private boolean mCrossFade;
    @Nullable
    private Object mTransition;
    // 额外信息，用于扩展
    @Nullable
    private Object mExtras;
    //</editor-fold>

    //<editor-fold desc="构造函数">
    public Builder() {
      mAsBitmap = true;
      mCenterCrop = true;
    }
    //</editor-fold>

    //<editor-fold desc="Setter">
    @NonNull
    public Builder forceLoad(boolean forceLoad) {
      mForceLoad = forceLoad;
      return this;
    }

    @NonNull
    public Builder asDrawable() {
      mAsDrawable = true;
      mAsBitmap = false;
      mAsGif = false;
      return this;
    }

    @NonNull
    public Builder asBitmap() {
      mAsBitmap = true;
      mAsDrawable = false;
      mAsGif = false;
      return this;
    }

    @NonNull
    public Builder asGif() {
      mAsGif = true;
      mAsDrawable = false;
      mAsBitmap = false;
      return this;
    }

    @NonNull
    public Builder placeholder(@DrawableRes int resId) {
      mPlaceholderResId = resId;
      return this;
    }

    @NonNull
    public Builder placeholder(@NonNull Drawable drawable) {
      mPlaceholderDrawable = drawable;
      return this;
    }

    @NonNull
    public Builder error(@DrawableRes int resId) {
      mErrorResId = resId;
      return this;
    }

    @NonNull
    public Builder error(@NonNull Drawable drawable) {
      mErrorDrawable = drawable;
      return this;
    }

    @NonNull
    public Builder fallback(@DrawableRes int resId) {
      mFallbackResId = resId;
      return this;
    }

    @NonNull
    public Builder fallback(@NonNull Drawable drawable) {
      mFallbackDrawable = drawable;
      return this;
    }

    @NonNull
    public Builder centerCrop() {
      mCenterCrop = true;
      return this;
    }

    @NonNull
    public Builder fitCenter() {
      mFitCenter = true;
      return this;
    }

    @NonNull
    public Builder circleCrop() {
      mCircleCrop = true;
      return this;
    }

    @NonNull
    public Builder centerInside() {
      mCenterInside = true;
      return this;
    }

    @NonNull
    public Builder roundedCorners(int roundedCorners) {
      mRoundedCorners = roundedCorners;
      return this;
    }

    @NonNull
    public Builder transforms(@NonNull Object transforms) {
      mTransforms = transforms;
      return this;
    }

    @NonNull
    public Builder crossFade() {
      mCrossFade = true;
      return this;
    }

    @NonNull
    public Builder transition(@NonNull Object transform) {
      mTransition = transform;
      return this;
    }

    @NonNull
    public Builder extras(@NonNull Object extras) {
      mExtras = extras;
      return this;
    }

    @NonNull
    public LoadImageOption build() {
      return new LoadImageOption(this);
    }
    //</editor-fold>
  }
  //</editor-fold>

  //<editor-fold desc="Getter">

  public boolean isForceLoad() {
    return mForceLoad;
  }

  public boolean asDrawable() {
    return mAsDrawable;
  }

  public boolean asBitmap() {
    return mAsBitmap;
  }

  public boolean asGif() {
    return mAsGif;
  }

  public int getPlaceholderResId() {
    return mPlaceholderResId;
  }

  @Nullable
  public Drawable getPlaceholderDrawable() {
    return mPlaceholderDrawable;
  }

  public int getErrorResId() {
    return mErrorResId;
  }

  @Nullable
  public Drawable getErrorDrawable() {
    return mErrorDrawable;
  }

  public int getFallbackResId() {
    return mFallbackResId;
  }

  @Nullable
  public Drawable getFallbackDrawable() {
    return mFallbackDrawable;
  }

  public boolean centerCrop() {
    return mCenterCrop;
  }

  public boolean fitCenter() {
    return mFitCenter;
  }

  public boolean circleCrop() {
    return mCircleCrop;
  }

  public boolean centerInside() {
    return mCenterInside;
  }

  public int getRoundedCorners() {
    return mRoundedCorners;
  }

  @Nullable
  public Object getTransforms() {
    return mTransforms;
  }

  public boolean crossFade() {
    return mCrossFade;
  }

  @Nullable
  public Object getTransition() {
    return mTransition;
  }

  @Nullable
  public Object getExtras() {
    return mExtras;
  }

  //</editor-fold>
}
