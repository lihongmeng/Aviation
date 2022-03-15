package com.hzlz.aviation.kernel.liteav.service.view;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.lifecycle.LifecycleOwner;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.LayoutAudioFloatViewBinding;
import com.hzlz.aviation.kernel.base.model.circle.CircleDetail;
import com.hzlz.aviation.kernel.base.utils.AnimUtils;
import com.hzlz.aviation.kernel.base.utils.SpannableStringUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.service.AudioLivePlayHelper;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.widget.widget.GVideoConstraintLayout;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author huangwei
 * date : 2021/9/26
 * desc : 悬浮音频播放控件
 **/
public class FloatAudioTipView extends GVideoConstraintLayout {

    private final LayoutAudioFloatViewBinding binding;
    private boolean isDelayedCollapse = true;
    private boolean isStartAnim = false;
    private boolean isCollapse = true;
    private long lastOperateTime = 0;

    public FloatAudioTipView(@NonNull Context context) {
        this(context, null);
    }

    public FloatAudioTipView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatAudioTipView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_audio_float_view,
                this, true);
        init();
    }

    /**
     * 因为使用navigate进行页面跳转时可能造成旋转动画停止需要再次开启
     */
    public void resumeRotate() {
        if (AudioLivePlayHelper.getInstance().isPlaying()) {
            AnimUtils.setRotateView(binding.playBg, 3000);
        }
    }

    /**
     * 如果有音频直播尝试展示控件
     */
    public void tryShowView(){
        CircleDetail circleDetail = AudioLivePlayHelper.getInstance().getCurrentPlayCircleData();
        if (circleDetail!=null){
            String title = circleDetail.broadcastDetail.getName();
            String author= "";
            if (circleDetail.broadcastDetail.getAuthor()!=null) {
                author = circleDetail.broadcastDetail.getAuthor().getName();
            }
            binding.title.setText(SpannableStringUtils.setSpanColor(title + "  " + author,
                    author, R.color.color_999999));
            setView(AudioLivePlayHelper.getInstance().isPlaying());
        }else {
            binding.rootLayout.setVisibility(GONE);
        }
    }

    public void release(){
        AudioLivePlayHelper.getInstance().removePlayListener(playListener);
        if (timeDisposable!=null){
            timeDisposable.dispose();
            timeDisposable = null;
        }
    }

    public void setDelayedCollapse(boolean isCollapse){
        this.isDelayedCollapse = isCollapse;
    }

    public void setCollapseEventBus(LifecycleOwner owner){
        GVideoEventBus.get(Constant.EVENT_MSG.COLLAPSE_AUDIO_FLOAT_VIEW).observe(owner, o -> {
            if (!isCollapse && isDelayedCollapse && !AudioLivePlayHelper.getInstance().isRelease() ) {
                collapse();
            }
        });
    }

    private void init() {

        binding.title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        binding.title.setSingleLine(true);
        binding.title.setSelected(true);
        binding.title.setFocusable(true);
        binding.title.setFocusableInTouchMode(true);

        binding.play.setOnClickListener(view -> {
            lastOperateTime = System.currentTimeMillis();
            if (AudioLivePlayHelper.getInstance().isPlaying()) {
                AudioLivePlayHelper.getInstance().pause();
            } else {
                AudioLivePlayHelper.getInstance().play();
            }
        });

        binding.rootLayout.setOnClickListener(view -> {
            if (binding.title.getVisibility() != VISIBLE) {
                expand();
            }
        });

        binding.close.setOnClickListener(view -> {
            binding.rootLayout.setVisibility(GONE);
            AudioLivePlayHelper.getInstance().close();
        });

        AudioLivePlayHelper.getInstance().setPlayListener(playListener);

    }

    private AudioLivePlayHelper.PlayListener playListener = new AudioLivePlayHelper.PlayListener() {
        @Override
        public void setPlayData(CircleDetail circleDetail) {
//                if (binding.title.getVisibility() != VISIBLE){
            lastOperateTime = System.currentTimeMillis();
            postDelayedCollapse();
            expand();
//                }
            String title = circleDetail.broadcastDetail.getName();
            String author = "";
            if (circleDetail.broadcastDetail.getAuthor()!=null) {
                author = circleDetail.broadcastDetail.getAuthor().getName();
            }
            binding.title.setText(SpannableStringUtils.setSpanColor(title + "  " + author,
                    author, R.color.color_999999));
            setView(true);
        }

        @Override
        public void play() {
            setView(true);
        }

        @Override
        public void pause() {
            setView(false);
        }

        @Override
        public void end() {
            setView(false);
            binding.rootLayout.setVisibility(GONE);
        }
    };

    private void setView(boolean isPlay) {
        if (AudioLivePlayHelper.getInstance().getCurrentPlayCircleData()==null){
            binding.rootLayout.setVisibility(GONE);
        }else {
            binding.rootLayout.setVisibility(VISIBLE);
            if (isPlay) {
                setRotateAnimation();
            } else {
                clearRotateAnimation();
            }
        }
    }


    /**
     * 停止背景旋转动画、指针回位
     */
    private void clearRotateAnimation() {
        if (binding.playBg.getAnimation() != null) {
            binding.playBg.getAnimation().cancel();
            binding.playBg.clearAnimation();
        }
        binding.play.setImageResource(R.drawable.ic_play_disk_play);

        Animation animation = new RotateAnimation(0, 30,
                Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(500);
        animation.setFillAfter(true);
        binding.stickView.startAnimation(animation);

    }

    /**
     * 指针旋转、碟片背景旋转
     */
    private void setRotateAnimation() {

        AnimUtils.setRotateView(binding.playBg, 6000);
        binding.play.setImageResource(R.drawable.ic_play_disk_pause);

        Animation stick = new RotateAnimation(30, 0,
                Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0);
        stick.setDuration(500);
        stick.setFillAfter(true);
        binding.stickView.startAnimation(stick);
    }

    /**
     * 控件展开
     */
    private void expand() {
        if (isStartAnim || !isCollapse){
            return;
        }
        isStartAnim = true;

        int d77 = getResources().getDimensionPixelSize(R.dimen.DIMEN_77DP);
        binding.expandLayout.measure(d77, ViewGroup.LayoutParams.MATCH_PARENT);
        final int viewWidth = getMeasuredWidth() - getResources().getDimensionPixelSize(R.dimen.DIMEN_10DP);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    isStartAnim = false;
                    isCollapse = false;
                    lastOperateTime = System.currentTimeMillis();
                    binding.title.setVisibility(VISIBLE);
                    binding.close.setVisibility(VISIBLE);
                    binding.play.setVisibility(VISIBLE);
                    binding.expandLayout.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    LogUtils.e("expand："+System.currentTimeMillis());
                } else {
                    if (interpolatedTime > 0.5) {
                        binding.title.setVisibility(VISIBLE);
                    }
                    int w = Math.max(d77, (int) (viewWidth * interpolatedTime));
                    binding.expandLayout.getLayoutParams().width = w;
                }
                binding.expandLayout.requestLayout();
            }
        };
        animation.setDuration(500);
        animation.setInterpolator(new FastOutLinearInInterpolator());
        binding.expandLayout.startAnimation(animation);

    }

    private Disposable timeDisposable;
    private void postDelayedCollapse(){
        if (isDelayedCollapse) {
            if (timeDisposable == null){
                timeDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            if (System.currentTimeMillis() - lastOperateTime > 3000 && !isCollapse && !isStartAnim){
                                LogUtils.e("lastOperateTime = "+System.currentTimeMillis());
                                collapse();
                            }
                        });

            }
        }
    }

    /**
     * 控件收缩
     */
    private void collapse() {

        if (isStartAnim || isCollapse) {
            return;
        }
        LogUtils.e("关闭组件："+System.currentTimeMillis());
        isStartAnim = true;
        binding.expandLayout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final int viewWidth = getMeasuredWidth() - getResources().getDimensionPixelSize(R.dimen.DIMEN_10DP);
        int d77 = getResources().getDimensionPixelSize(R.dimen.DIMEN_77DP);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    isStartAnim = false;
                    isCollapse = true;
                    binding.title.setVisibility(GONE);
                    binding.play.setVisibility(GONE);
                    binding.close.setVisibility(GONE);
                    binding.expandLayout.getLayoutParams().width = d77;

                } else {
                    binding.play.setVisibility(GONE);
                    binding.close.setVisibility(GONE);
                    int w = Math.max(d77, viewWidth - (int) (viewWidth * interpolatedTime));
                    binding.expandLayout.getLayoutParams().width = w;
                }
                binding.expandLayout.requestLayout();
            }
        };
        animation.setDuration(500);
        animation.setInterpolator(new FastOutLinearInInterpolator());
        binding.expandLayout.startAnimation(animation);

    }

}
