package com.jxntv.android.video.ui.watchtv;

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

import com.jxntv.android.liteav.view.ControllerProgressLayout;
import com.jxntv.android.video.R;

public class WatchTvControllerProgressLayout extends RelativeLayout {

    private ImageView mIvThumbnail;
    private ProgressBar mProgressBar;
    private HideRunnable mHideRunnable;
    private final Handler mHandler = new Handler();

    public WatchTvControllerProgressLayout(Context context) {
        super(context);
        init(context);
    }

    public WatchTvControllerProgressLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_video_watch_tv_controller_progress, this);
        mIvThumbnail = findViewById(R.id.progress_iv_thumbnail);
        mProgressBar = findViewById(R.id.progress_pb_bar);
        setVisibility(GONE);
        mHideRunnable = new HideRunnable();
    }

    //显示
    public void show() {
        setVisibility(VISIBLE);
        mHandler.removeCallbacks(mHideRunnable);
        mHandler.postDelayed(mHideRunnable, 1000);
    }

    public void setTimeText(String text) {
        mIvThumbnail.setVisibility(View.GONE);
        mIvThumbnail.setImageBitmap(null);
    }

    //设置进度
    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    //设置显示图片
    public void setImageResource(int resource) {
        mIvThumbnail.setVisibility(VISIBLE);
        mIvThumbnail.setImageResource(resource);
    }

    public void setProgressVisibility(boolean enable) {
        mProgressBar.setVisibility(enable ? VISIBLE : GONE);
    }

    public void showTip(String tip) {
        mIvThumbnail.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        setVisibility(VISIBLE);
        mHandler.removeCallbacks(mHideRunnable);
    }

    //隐藏自己的Runnable
    private class HideRunnable implements Runnable {
        @Override
        public void run() {
            mIvThumbnail.setImageBitmap(null);
            mIvThumbnail.setVisibility(GONE);
            WatchTvControllerProgressLayout.this.setVisibility(GONE);
        }
    }
}
