package com.hzlz.aviation.feature.record.recorder.view.soundrecord;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.record.recorder.helper.AudioRecordManager;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.callback.AudioRecordListener;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.base.utils.ContextUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.widget.widget.GVideoImageView;
import com.hzlz.aviation.library.widget.widget.GVideoLinearLayout;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;

import java.io.IOException;

public class GVideoSoundRecordView extends GVideoLinearLayout {

    // 当期是否正在录制
    private boolean isRecording = false;

    // 波形View、时间
    private GVideoSoundRecordWaveView waveView;

    // 录音、暂停按钮
    private GVideoImageView startStopGviv;

    // 按钮标题
    private GVideoTextView startStopGvtv;

    // 上下文Context
    private Context context;

    // 波形颜色
    private int waveColor = 0;

    // 向上层传递结果
    private OperationListener operationListener;

    // 录音时长
    private long operationTime;

    // 录制时长限制
    private long maxRecordTime = 5 * 60 * 1000;

    /**
     * 网络中断后，录音控件内部流程不会因为网络中断而立刻停止
     * 所以{@link AudioRecordListener#error(String message)}
     * 会调用多次
     * <p>
     * 但是上层也许只需要关注第一次回调，例如显示Toast
     * 故此值用于做标记，在每次startRecord时重置
     */
    private boolean isFirstCallBack;

    // <editor-folder desc="构造方法">

    public GVideoSoundRecordView(Context context) {
        this(context, null);
    }

    public GVideoSoundRecordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GVideoSoundRecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVars(context);
        initAttrs(attrs);
        initViews();
    }

    // </editor-folder>

    private void initVars(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_sound_record, this);
        waveView = findViewById(R.id.gvsrwv_layout_sound_record);
        startStopGviv = findViewById(R.id.gvyv_layout_sound_record);
        startStopGvtv = findViewById(R.id.gvtv_layout_sound_record_title);
    }

    private void initViews() {
        startStopGviv.setOnClickListener(v -> {
            if (isRecording) {
                stopRecord();
            } else {
                startRecord();
            }
        });
        waveView.setWaveColor(waveColor);
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.GVideoSoundRecordView);
        waveColor = mTypedArray.getColor(R.styleable.GVideoSoundRecordView_waveColor, Color.parseColor("#fc284d"));
        mTypedArray.recycle();
    }

    private void startRecord() {

        // 此时可能正在申请权限,可以做一些收起软键盘之类的工作
        // 以免有的手机，键盘会挡住权限申请弹窗
        if (operationListener != null) {
            operationListener.startRequestPermission();
        }

        PermissionManager
                .requestPermissions(getContext(), new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        isFirstCallBack = true;

                        if (operationListener != null) {
                            operationListener.start();
                        }

                        isRecording = true;
                        waveView.setVisibility(View.VISIBLE);
                        startStopGviv.setBackgroundResource(R.drawable.icon_sound_record_stop);
                        startStopGvtv.setText(context.getText(R.string.click_stop));

                        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.MUTE_VIDEO).post(null);

                        AudioRecordManager audioRecordManager = AudioRecordManager.getInstance();
                        audioRecordManager.setListener(audioRecordListener);
                        audioRecordManager.startRecord();

                        waveView.setStartRecordTime(System.currentTimeMillis());
                        waveView.startTimeCountDown();

                        postDelayed(maxTimeRunnable, maxRecordTime + 500);
                    }

                    @Override
                    public void onPermissionDenied(
                            @NonNull Context context,
                            @Nullable String[] grantedPermissions,
                            @NonNull String[] deniedPermission) {
                        Toast.makeText(context, "没有相应权限", Toast.LENGTH_SHORT).show();
                    }
                },Manifest.permission.RECORD_AUDIO);

    }

    private final Runnable maxTimeRunnable = new Runnable() {
        @Override
        public void run() {
            @SuppressLint("DefaultLocale")
            String tip = String.format("最长支持%d分钟语音", maxRecordTime / (60 * 1000));
            Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
            stopRecord();
        }
    };

    private void stopRecord() {
        if (operationListener != null) {
            operationListener.stop();
        }
        isRecording = false;
        if(waveView != null && startStopGviv != null) {
            waveView.setVisibility(View.INVISIBLE);
            startStopGviv.setBackgroundResource(R.drawable.icon_sound_record_start);
            startStopGvtv.setText(context.getText(R.string.click_record_sound));
            AudioRecordManager.getInstance().stopRecord();
            removeCallbacks(maxTimeRunnable);
            waveView.removeCallbacks();
        }
    }

    public void destroy() {
        waveView.destroy();
        AudioRecordManager.getInstance().destroy();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (!ContextUtils.isFinishing(getContext()) && (visibility == GONE || visibility == INVISIBLE)){
            stopRecord();
        }
    }

    public void setCompleteListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    public void setMaxRecordTime(long maxRecordTime) {
        this.maxRecordTime = maxRecordTime;
    }

    private final AudioRecordListener audioRecordListener = new AudioRecordListener() {

        @Override
        public void volumeSize(double volume) {
            waveView.post(() -> waveView.updateVolume(volume));
        }

        @Override
        public void result(String filePath, String contentTxt) {
            waveView.removeCallbacks();
            if (operationListener == null) {
                return;
            }
            try {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepare();
                operationTime = mediaPlayer.getDuration();
                mediaPlayer.release();
            } catch (IOException e) {
                error(e.getMessage());
                return;
            }
            operationListener.result(filePath, contentTxt, operationTime);
        }

        @Override
        public void error(String errorMessage) {
            waveView.removeCallbacks();
            if (operationListener != null) {
                operationListener.errorOnBackgroundThread(errorMessage, isFirstCallBack);
                isFirstCallBack = false;
            }
        }

        @Override
        public void onStop() {
            if (operationListener != null) {
                operationListener.stop();
            }
            isRecording = false;
            waveView.setVisibility(View.INVISIBLE);
            startStopGviv.setBackgroundResource(R.drawable.icon_sound_record_start);
            startStopGvtv.setText(context.getText(R.string.click_record_sound));
        }

        @Override
        public void onLoadingStart(String loadingMessage) {
            if (operationListener != null) {
                operationListener.onLoadingStart(loadingMessage);
            }
        }

        @Override
        public void onLoadingEnd() {
            if (operationListener != null) {
                operationListener.onLoadingEnd();
            }
        }

    };

    public interface OperationListener {

        // 点击开始录制按钮
        // 此时可能正在申请权限,可以做一些收起软键盘之类的工作
        // 以免有的手机，键盘会挡住权限申请弹窗
        void startRequestPermission();


        // 开始回调
        void start();

        // 结束回调
        void stop();

        /**
         * 异常回调,回调在工作线程中
         *
         * @param message         错误信息
         * @param isFirstCallBack 当前方法是不是首次回调
         */
        void errorOnBackgroundThread(String message, boolean isFirstCallBack);

        /**
         * 录音结果
         *
         * @param soundFilePath 录音文件本地地址
         * @param soundText     录音文字
         * @param operationTime 录时长，单位毫秒
         */
        void result(String soundFilePath, String soundText, long operationTime);

        /**
         * 某些环节正在加载
         *
         * @param loadingMessage 显示加载文本
         */
        void onLoadingStart(String loadingMessage);

        /**
         * 某些环节结束加载
         */
        void onLoadingEnd();

    }

}
