package com.jxntv.android.liteav.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jxntv.android.liteav.R;
import com.jxntv.android.liteav.player.GVideoPlayerListener;
import com.jxntv.base.model.anotation.PendantShowType;
import com.jxntv.base.model.video.PendantModel;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.LogUtils;
import com.jxntv.widget.GVideoFrameLayout;

/**
 * 播放器挂件
 */
public class PendantView extends GVideoFrameLayout implements View.OnClickListener {
  private static final int THOUSAND = 1000;
  private PendantModel mPendant;

  private TextView pendantText;
  private ImageView pendantImage;
  private Handler mHandler = new Handler();

  public PendantView(Context context) {
    super(context);
    init(context);
  }

  public PendantView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public PendantView(Context context,
      @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    View root = LayoutInflater.from(context).inflate(R.layout.pendant_layout, this);
    pendantText = root.findViewById(R.id.pendant_text);
    pendantImage = root.findViewById(R.id.pendant_img);

    root.findViewById(R.id.pendant_layout).setOnClickListener(this);
    setVisibility(View.GONE);
  }

  private GVideoPlayerListener mListener;
  public void setPlayerListener(GVideoPlayerListener listener) {
    mListener = listener;
  }

  public void initPendant(PendantModel pendant) {
    if (pendant == null || !pendant.isValid()) {
      setVisibility(View.GONE);
      return;
    }
    setVisibility(View.VISIBLE);

    mPendant = pendant;

    Glide.with(pendantImage)
        .load(mPendant.imageUrl)
        .apply(new RequestOptions().transform(new CenterCrop(),
            new RoundedCorners(
                getContext().getResources().getDimensionPixelSize(R.dimen.feed_pendant_img_corner))))
        .into(pendantImage);
    SpanTextHelper.createPendantTitle(getContext(),
        pendantText,
        pendant,
        getContext().getResources().getColor(R.color.big_img_title_color));


    if (pendant.isAlwaysExist != PendantShowType.ALWAYS_SHOW) {
      setVisibility(View.INVISIBLE);
    }

  }

  private ViewGroup getPendantLayout() {
    return this;
  }
  /**
   * 显示挂件
   */
  public void showPendant() {
    final PendantModel pendantModel = mPendant;
    if (pendantModel == null || pendantModel.isAlwaysExist == PendantShowType.ALWAYS_SHOW) {
      return;
    }

    getPendantLayout().setVisibility(VISIBLE);
    float width = GVideoRuntime.getAppContext().getResources().getDimension(R.dimen.feed_pendant_width);
    float marginRight = GVideoRuntime.getAppContext().getResources().getDimension(R.dimen.feed_pendant_margin_right);
    Animation translateAnimation = new TranslateAnimation(width + marginRight, 0,
        0, 0);
    translateAnimation.setDuration(1000);
    translateAnimation.setFillEnabled(true);
    translateAnimation.setFillAfter(true);
    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        getPendantLayout().clearAnimation();
        long hideTime = pendantModel.showDuration > 0 ? pendantModel.showDuration : 0;
        hideTime *= THOUSAND;
        mHandler.postDelayed(mHideRunnable, hideTime);

        if (mListener != null) {
          mListener.onPendantShow(mPendant);
        }
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
    getPendantLayout().startAnimation(translateAnimation);

  }

  /**
   * 隐藏挂件
   */
  public void hidePendant() {
    if (mPendant == null) {
      return;
    }
    if (getPendantLayout().getVisibility() == GONE) {
      return;
    }
    float width =  GVideoRuntime.getAppContext().getResources().getDimension(R.dimen.feed_pendant_width);
    float marginRight =  GVideoRuntime.getAppContext().getResources().getDimension(R.dimen.feed_pendant_margin_right);
    Animation translateAnimation = new TranslateAnimation(0, width + marginRight,
        0, 0);
    translateAnimation.setDuration(1000);
    translateAnimation.setFillEnabled(true);
    translateAnimation.setFillAfter(true);
    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        getPendantLayout().clearAnimation();
        getPendantLayout().setVisibility(GONE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
    getPendantLayout().startAnimation(translateAnimation);
  }

  public void start() {
    if (mPendant != null &&
        mPendant.isAlwaysExist != PendantShowType.ALWAYS_SHOW) {
      long showTime = mPendant.showTime > 0 ? mPendant.showTime : 0;
      showTime *= THOUSAND;
      mHandler.postDelayed(mShowRunnable, showTime);
    }
  }

  public void stop() {
    mHandler.removeCallbacks(mShowRunnable);
    mHandler.removeCallbacks(mHideRunnable);
    getPendantLayout().clearAnimation();
    getPendantLayout().setVisibility(GONE);
  }

  private Runnable mShowRunnable = new Runnable() {
    @Override public void run() {
      showPendant();
    }
  };
  private Runnable mHideRunnable = new Runnable() {
    @Override public void run() {
//      hidePendant();
    }
  };

  @Override public void onClick(View v) {
    if (mPendant == null) return;

    if (mListener != null) {
      mListener.onPendantClick(mPendant);
    }
  }
}
