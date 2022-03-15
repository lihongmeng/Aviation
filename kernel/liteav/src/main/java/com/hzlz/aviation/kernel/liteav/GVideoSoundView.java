package com.hzlz.aviation.kernel.liteav;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.utils.ContextUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.player.GVideoPlayerListener;
import com.hzlz.aviation.kernel.liteav.view.GVideoSoundViewWave;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.library.util.SizeUtils;
import com.hzlz.aviation.library.widget.widget.GVideoImageView;
import com.hzlz.aviation.library.widget.widget.GVideoLinearLayout;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

public class GVideoSoundView extends GVideoLinearLayout {

    //播放状态
    private Status status = Status.STATUS_STOP;
    // 播放声音的控件
    private GVideoView playGvv;
    // 语音点击生效区域，点击就播放语音
    private GVideoImageView soundGviv;
    // 播放、暂停icon
    private GVideoImageView iconGviv;
    // 波形
    private GVideoSoundViewWave waveViewGvsvw;
    // 时间
    private GVideoTextView timeGvtv;
    // 删除
    private GVideoImageView deleteGviv;
    // 语音转文字
    private GVideoTextView changeGvtv;
    // 文字显示区域
    private GVideoTextView textGvtv;
    // 语音文字
    private String soundText = "0";
    // 是否显示删除按钮
    private boolean isShowDelete = false;
    // 是否启用语音转文字功能
    private boolean isEnableTextChange = true;
    // 语音链接
    private String soundUrl;
    // 删除按钮的点击事件
    private OnClickListener deleteOnClickListener;
    // 播放按钮点击
    private OnClickListener playOnClickListener;
    // Context
    private Context context;

    //埋点数据
    private VideoModel videoModel;

    public GVideoSoundView(Context context) {
        this(context, null);
    }

    public GVideoSoundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GVideoSoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars(context);
        initViews();
    }

    private void initVars(Context context) {
        this.context = context;
    }

    private void initViews() {
        LayoutInflater.from(context).inflate(R.layout.layout_sound_view, this);
        soundGviv = findViewById(R.id.gviv_layout_sound_view_layout);
        iconGviv = findViewById(R.id.gviv_layout_sound_view_image);
        waveViewGvsvw = findViewById(R.id.gvsvw_layout_sound_view);
        timeGvtv = findViewById(R.id.gvtv_layout_sound_view_time);
        deleteGviv = findViewById(R.id.gviv_layout_sound_view_delete);
        changeGvtv = findViewById(R.id.gvtv_layout_sound_view_change_text);
        textGvtv = findViewById(R.id.gvtv_layout_sound_view_text);
        playGvv = findViewById(R.id.gvv_layout_sound_view);

        soundGviv.setOnClickListener(view -> {
            if (status == Status.STATUS_PAUSE) {
                resume();
            } else if (status == Status.STATUS_START) {
                pause();
            } else if (status == Status.STATUS_STOP) {
                iconGviv.setBackgroundResource(R.drawable.icon_sound_stop);
                if (playOnClickListener != null) {
                    playOnClickListener.onClick(soundGviv);
                } else {
                    start();
                }
            }
        });

        changeGvtv.setOnClickListener(v -> {
            if(v.getVisibility()==INVISIBLE){
                return;
            }
            changeGvtv.setVisibility(INVISIBLE);
            textGvtv.setVisibility(VISIBLE);
        });

        deleteGviv.setOnClickListener(v -> {
            if (deleteOnClickListener != null) {
                deleteOnClickListener.onClick(v);
            }
            setVisibility(GONE);
        });

        playGvv.setKSVideoPlayerListener(new GVideoPlayerListener() {

            @Override
            public void onPlayEnd() {
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME).post(null);
                status = Status.STATUS_STOP;
                waveViewGvsvw.stop();
                playGvv.clear();
                playGvv.pause();
                if (playGvv != null) {
                    playGvv.release();
                }
                iconGviv.setBackgroundResource(R.drawable.icon_sound_play);
                removeCallbacks(countDownRunnable);
                updateTimeGvtv();
            }

        });

    }

    public void start() {
        if (status == Status.STATUS_START) {
            return;
        }
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.MUTE_VIDEO).post(null);
        status = Status.STATUS_START;
        waveViewGvsvw.start();
        playGvv.startPlay(soundUrl,videoModel);
        iconGviv.setBackgroundResource(R.drawable.icon_sound_stop);
        post(countDownRunnable);
    }

    public void resume() {
        if (status == Status.STATUS_START) {
            return;
        }
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.MUTE_VIDEO).post(null);
        status = Status.STATUS_START;
        waveViewGvsvw.start();
        playGvv.resume();
        iconGviv.setBackgroundResource(R.drawable.icon_sound_stop);
        post(countDownRunnable);
    }

    public void pause() {
        if (status == Status.STATUS_PAUSE) {
            return;
        }
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME).post(null);
        status = Status.STATUS_PAUSE;
        waveViewGvsvw.pause();
        playGvv.clear();
        playGvv.pause();
        iconGviv.setBackgroundResource(R.drawable.icon_sound_play);
        removeCallbacks(countDownRunnable);
    }

    public void stop() {
        if (status == Status.STATUS_STOP) {
            return;
        }
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME).post(null);
        status = Status.STATUS_STOP;
        if (waveViewGvsvw == null || playGvv == null){
            return;
        }
        waveViewGvsvw.stop();
        playGvv.clear();
        playGvv.pause();
        playGvv.release();
        iconGviv.setBackgroundResource(R.drawable.icon_sound_play);
        removeCallbacks(countDownRunnable);
        updateTimeGvtv();
    }

    private final Runnable countDownRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimeGvtv();
            postDelayed(this, 500);
        }
    };

    public void isShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        if (isShowDelete) {
            deleteGviv.setVisibility(VISIBLE);
        } else {
            deleteGviv.setVisibility(GONE);
        }
    }

    public void updateSoundLayoutWidth(int lengthType) {
        int width = 0;
        switch (lengthType) {
            case SOUND_VIEW_LENGTH.SHORT:
                width = SizeUtils.dp2px(50);
                break;
            case SOUND_VIEW_LENGTH.MIDDLE:
                width = SizeUtils.dp2px(100);
                break;
            case SOUND_VIEW_LENGTH.LONG:
                width = SizeUtils.dp2px(200);
                break;
        }
        if (width == 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = soundGviv.getLayoutParams();
        layoutParams.width = width;
        soundGviv.setLayoutParams(layoutParams);
        requestLayout();
    }

    public void setSoundViewWidth(int dpValue) {
        ViewGroup.LayoutParams layoutParams = soundGviv.getLayoutParams();
        layoutParams.width = SizeUtils.dp2px(dpValue);
        soundGviv.setLayoutParams(layoutParams);
        requestLayout();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (!ContextUtils.isFinishing(getContext()) && (visibility == INVISIBLE || visibility == GONE)) {
            if (iconGviv!=null) {
                iconGviv.setBackgroundResource(R.drawable.icon_sound_play);
            }
            stop();
        }
        super.onVisibilityChanged(changedView, visibility);
    }

    public void setPlayOnClickListener(OnClickListener listener) {
        this.playOnClickListener = listener;
    }

    /**
     * 设置语音文字内容
     *
     * @param soundText 语音文字内容
     */
    public void setSoundText(String soundText) {
        this.soundText = soundText;
        if (TextUtils.isEmpty(soundText)) {
            textGvtv.setText("");
            changeGvtv.setVisibility(GONE);
            textGvtv.setVisibility(GONE);
        } else {
            textGvtv.setText(soundText.trim());
        }
    }

    /**
     * 设置是否启用转文字功能
     *
     * @param isEnableTextChange true 开启功能，false 关闭功能
     *                           如果为true,但是soundText为空的话，功能还是关闭
     */
    public void isEnableTextChange(boolean isEnableTextChange) {
        this.isEnableTextChange = isEnableTextChange;
        if (this.isEnableTextChange && !TextUtils.isEmpty(soundText)) {
            changeGvtv.setVisibility(VISIBLE);
        } else {
            changeGvtv.setVisibility(GONE);
            textGvtv.setVisibility(GONE);
        }
    }

    // 设置播放的总时长
    public void setTotalTime(long totalTime) {
        waveViewGvsvw.setTotalTime(totalTime);
        updateTimeGvtv();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateTimeGvtv() {
        if (waveViewGvsvw == null) {
            return;
        }
        long remainPlayTime = waveViewGvsvw.remainPlayTime();
        if (remainPlayTime <= 60 * 1000) {
            timeGvtv.setText((remainPlayTime / 1000) + "\"");
        } else {
            timeGvtv.setText(DateUtils.changeTimeLongToMinSecondDot(remainPlayTime));
        }
    }

    // ======================== 核心方法，不设置无法执行功能 ========================

    /**
     * 设置总时长
     *
     * @param secondTime 秒数
     */
    public void setTotalSecondTime(long secondTime) {
        setTotalTime(secondTime * 1000);
    }

    // 设置删除按钮的点击事件
    public void setDeleteOnClickListener(OnClickListener deleteOnClickListener) {
        this.deleteOnClickListener = deleteOnClickListener;
    }

    // 设置语音地址
    public void setSoundUrl(String soundUrl) {
        if (playGvv != null) {
            playGvv.setPreparePlayUrl(soundUrl);
        }
        this.soundUrl = soundUrl;
    }

    public enum Status {
        STATUS_START,
        STATUS_PAUSE,
        STATUS_STOP
    }

    // 语音View的长度类型
    public static class SOUND_VIEW_LENGTH {
        public static final int SHORT = 0;
        public static final int MIDDLE = 1;
        public static final int LONG = 2;
    }

    // ==========================================================================

    public void setVideoModel(VideoModel videoModel){
        this.videoModel = videoModel;
    }



}
