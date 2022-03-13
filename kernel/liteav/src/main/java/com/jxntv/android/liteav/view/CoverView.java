package com.jxntv.android.liteav.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jxntv.android.liteav.BuildConfig;
import com.jxntv.android.liteav.R;
import com.jxntv.base.utils.ContextUtils;
import com.jxntv.image.ImageLoaderManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 播放器封面
 */
public class CoverView extends FrameLayout {
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final String TAG = CoverView.class.getSimpleName();
  public static final int SOUND_NO = 0;
  public static final int SOUND_FM = 3;
  public static final int SOUND_MUSIC = 4;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ SOUND_NO, SOUND_FM, SOUND_MUSIC })
  public @interface SOUND_TYPE {
  }

  private SoundWaveView mSoundWaveView;
  private ImageView mSoundCover;
  private ImageView mBgCover;
  private ImageView mSoundBackCover;

  private boolean mIsAudio;

  public CoverView(@NonNull Context context) {
    super(context);
    init(context);
  }

  public CoverView(@NonNull Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  private void init(Context context) {
    View rootView = LayoutInflater.from(context)
        .inflate(R.layout.liteav_custom_view, this);
    mSoundWaveView = rootView.findViewById(R.id.sound_wave_anim);
    mSoundCover = rootView.findViewById(R.id.sound_cover);
    mBgCover = rootView.findViewById(R.id.bg_cover);
    mSoundBackCover = rootView.findViewById(R.id.sound_back_cover);
  }

  public void setSoundCover(String coverUrl, @SOUND_TYPE int soundType) {
    if (soundType == SOUND_FM) {
      mIsAudio = true;
      mSoundCover.setImageResource(R.drawable.liteav_fm_cover);
      mSoundCover.setVisibility(View.VISIBLE);
      mSoundBackCover.setVisibility(View.VISIBLE);
      showCoverWithDefaultConner(mSoundBackCover, coverUrl, 0);
    } else if (soundType == SOUND_MUSIC) {
      mIsAudio = true;
      mSoundCover.setImageResource(R.drawable.liteav_music_cover);
      mSoundCover.setVisibility(View.VISIBLE);
      mSoundBackCover.setVisibility(View.VISIBLE);
      showCoverWithDefaultConner(mSoundBackCover, coverUrl, 0);
    } else {
      mIsAudio = false;
      mSoundCover.setImageDrawable(null);
      mSoundCover.setVisibility(View.GONE);
      mSoundBackCover.setVisibility(View.GONE);
    }
  }

  public void release() {
    if (mSoundWaveView != null) {
      mSoundWaveView.releaseWaveAnim();
    }
  }

  public void showWave(boolean show) {
    if (mIsAudio) {
      mSoundWaveView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
  }

  public void startWave(boolean start) {
    if (mIsAudio) {
      if (start) {
        mSoundWaveView.startWaveAnim();
      } else {
        mSoundWaveView.stopWaveAnim();
      }
    }
  }

  public void hideCover() {
    if (DEBUG) {
      Log.d(TAG, "hideCover");
    }
    if (mIsAudio) {

    } else {
      mBgCover.setImageResource(0);
      //复用上层player后，会先播放并hideCover，然后showCover异步回调才回来；这里隐藏掉；
      mBgCover.setVisibility(View.GONE);
    }
  }

  /**
   * 展示封面
   */
  public void showCover(Drawable drawable) {
    if (DEBUG) {
      Log.d(TAG, "showCover");
    }
    mBgCover.setImageDrawable(drawable);
  }

  /**
   * 展示封面
   */
  public void showCover(String coverUrl) {
    if (DEBUG) {
      Log.d(TAG, "showCover coverUrl=" + coverUrl);
    }
    showCover(coverUrl, true);
  }

  public void showCover(String coverUrl, boolean centerCrop) {
    if (!centerCrop) {
      mBgCover.setAdjustViewBounds(true);
    }
    ImageLoaderManager.loadImage(mBgCover,coverUrl,centerCrop);
  }

  public void showCoverWithDefaultConner(String coverUrl) {
    int connerSize = (int) getContext().getResources().getDimension(
        R.dimen.r_r01);
    showCoverWithDefaultConner(mBgCover, coverUrl, connerSize);
  }

  private void showCoverWithDefaultConner(ImageView imageView, String coverUrl, int connerSize) {

    if (imageView == null || imageView.getContext() == null ){
      return;
    }
    if (ContextUtils.isFinishing(imageView.getContext())){
      return;
    }

    RequestOptions options;
    if (connerSize > 0) {
      options = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(connerSize));
    } else {
      options = new RequestOptions().transform(new CenterCrop());
    }
    Glide.with(this)
        .load(coverUrl)
        .apply(options)
        .into(imageView);
  }

}
