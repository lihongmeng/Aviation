package com.jxntv.android.liteav.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jxntv.android.liteav.R;

/**
 * 播放器进度条
 */
public class ControllerProgressLayout extends RelativeLayout {
  private static final String TAG = "ControllerProgressLayout";
  private ImageView mIvThumbnail;
  private TextView mTvTime;
  private ProgressBar mProgressBar;
  private View mRetryButton;
  private HideRunnable mHideRunnable;
  private int duration = 1000;
  private Handler mHandler = new Handler();
  public ControllerProgressLayout(Context context) {
    super(context);
    init(context);
  }

  public ControllerProgressLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  private void init(Context context) {
    LayoutInflater.from(context).inflate(R.layout.liteav_scroll_progress_layout, this);
    mIvThumbnail = findViewById(R.id.progress_iv_thumbnail);
    mProgressBar = findViewById(R.id.progress_pb_bar);
    mTvTime = findViewById(R.id.progress_tv_time);
    mRetryButton = findViewById(R.id.retry);
    mRetryButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mRetryRunnable != null) {
          mRetryRunnable.run();
        }
      }
    });
    setVisibility(GONE);
    mHideRunnable = new HideRunnable();
  }
  private Runnable mRetryRunnable;
  public void setRetryRunnable(Runnable runnable) {
    mRetryRunnable = runnable;
  }

  //显示
  public void show() {
    setVisibility(VISIBLE);
    mHandler.removeCallbacks(mHideRunnable);
    mHandler.postDelayed(mHideRunnable, duration);
  }

  public void setTimeText(String text) {
    mTvTime.setVisibility(View.VISIBLE);
    mTvTime.setText(text);

    mIvThumbnail.setVisibility(View.GONE);
    mIvThumbnail.setImageBitmap(null);
  }

  //设置进度
  public void setProgress(int progress) {
    mProgressBar.setProgress(progress);
    mProgressBar.setVisibility(View.VISIBLE);
  }

  //设置持续时间
  public void setDuration(int duration) {
    this.duration = duration;
  }

  //设置显示图片
  public void setThumbnail(Bitmap bitmap) {
    mIvThumbnail.setVisibility(VISIBLE);
    mIvThumbnail.setImageBitmap(bitmap);

    mTvTime.setVisibility(View.GONE);
    mTvTime.setText("");
  }
  //设置显示图片
  public void setImageResource(int resource) {
    mIvThumbnail.setVisibility(VISIBLE);
    mIvThumbnail.setImageResource(resource);

    mTvTime.setVisibility(View.GONE);
    mTvTime.setText("");
  }

  public void setProgressVisibility(boolean enable) {
    mProgressBar.setVisibility(enable ? VISIBLE : GONE);
  }

  public void showTip(String tip) {
    mIvThumbnail.setVisibility(View.GONE);
    mProgressBar.setVisibility(View.GONE);
    mTvTime.setVisibility(View.VISIBLE);
    mTvTime.setText(tip);

    setVisibility(VISIBLE);
    mHandler.removeCallbacks(mHideRunnable);
  }

  public void showError(boolean show) {
    mRetryButton.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  //隐藏自己的Runnable
  private class HideRunnable implements Runnable {
    @Override
    public void run() {
      mIvThumbnail.setImageBitmap(null);
      mIvThumbnail.setVisibility(GONE);
      ControllerProgressLayout.this.setVisibility(GONE);
    }
  }
}
