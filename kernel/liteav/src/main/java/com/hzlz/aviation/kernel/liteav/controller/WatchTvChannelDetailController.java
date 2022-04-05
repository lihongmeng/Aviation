package com.hzlz.aviation.kernel.liteav.controller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.hzlz.aviation.kernel.base.tag.TagTextHelper;
import com.hzlz.aviation.kernel.liteav.LiteavConstants;
import com.hzlz.aviation.kernel.liteav.R;
import com.hzlz.aviation.kernel.liteav.databinding.LiteavControllerSmallNoProcessBinding;

/**
 * 小窗控制器
 */
public class WatchTvChannelDetailController extends GVideoControllerBase {

    private LiteavControllerSmallNoProcessBinding mBinding;

    public WatchTvChannelDetailController(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVideoGestureListener();
    }

    public WatchTvChannelDetailController(Context context) {
        super(context);
        setVideoGestureListener();
    }

    @Override
    protected View makeControllerView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mBinding = LiteavControllerSmallNoProcessBinding.inflate(inflater);
        mRoot = mBinding.getRoot();
        mGestureVideoProgressLayout = mBinding.videoProgressLayout;
        mReplay = mBinding.ivReplay;
        mBack = mBinding.ivBack;
        screenProjection = mBinding.screenProjection;
        initControllerView(mRoot);
        return mRoot;
    }

    public void setVideoGestureListener() {
        setVideoGestureListener(new VideoGestureUtil.VideoGestureListener() {
            @Override
            public void onBrightnessGesture(float newBrightness) {
                if (mGestureVideoProgressLayout != null) {
                    mGestureVideoProgressLayout.setProgress((int) (newBrightness * 100));
                    mGestureVideoProgressLayout.setImageResource(R.drawable.liteav_light_max);
                    mGestureVideoProgressLayout.show();
                }
            }

            @Override
            public void onVolumeGesture(float volumeProgress) {
                if (mGestureVideoProgressLayout != null) {
                    mGestureVideoProgressLayout.setImageResource(R.drawable.liteav_volume_max);
                    mGestureVideoProgressLayout.setProgress((int) volumeProgress);
                    mGestureVideoProgressLayout.show();
                }
            }

            @Override
            public void onSeekGesture(int progress) {
                mDragging = true;
            }

        });
    }

    @Override
    public void setTitleVisible(@LiteavConstants.TitleMode int mode) {
        if (mode == LiteavConstants.TITLE_MODE_ONLY_BACK) {
            mBinding.ivBack.setVisibility(View.VISIBLE);
            mBinding.tvTitle.setVisibility(View.GONE);
        } else if (mode == LiteavConstants.TITLE_MODE_ONLY_TITLE) {
            mBinding.ivBack.setVisibility(View.GONE);
            mBinding.tvTitle.setVisibility(View.VISIBLE);
        } else if (mode == LiteavConstants.TITLE_MODE_NONE) {
            mBinding.ivBack.setVisibility(View.GONE);
            mBinding.tvTitle.setVisibility(View.GONE);
            mBinding.layoutTop.setVisibility(View.GONE);
        } else {
            mBinding.ivBack.setVisibility(View.VISIBLE);
            mBinding.tvTitle.setVisibility(View.VISIBLE);
            mBinding.layoutTop.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateTitle(String title, int tag) {
        TagTextHelper.createTagTitle(getContext(), mBinding.tvTitle, title, tag,
                getContext().getResources().getColor(R.color.color_ffffff));
    }

    public LiteavControllerSmallNoProcessBinding getBinding() {
        return mBinding;
    }

    protected void createGestureDetector() {
        mGestureDetector =
                new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        doPauseResume();
                        show();
                        return true;
                    }

                    //如果双击的话，则onSingleTapConfirmed不会执行
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        toggleControllerView();
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX,
                                            float distanceY) {
                        if (downEvent == null || moveEvent == null) {
                            return false;
                        }
                        if (mVideoGestureUtil != null && mGestureVideoProgressLayout != null) {
                            mVideoGestureUtil.check(
                                    mGestureVideoProgressLayout.getHeight(),
                                    downEvent,
                                    moveEvent,
                                    distanceX,
                                    distanceY
                            );
                        }
                        show();
                        return true;
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        if (mVideoGestureUtil != null) {
                            mVideoGestureUtil.reset(getWidth());
                        }
                        return true;
                    }
                });
        mGestureDetector.setIsLongpressEnabled(false);
    }

}
