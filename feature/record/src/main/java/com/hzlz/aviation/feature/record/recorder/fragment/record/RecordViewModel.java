package com.hzlz.aviation.feature.record.recorder.fragment.record;

import static com.hzlz.aviation.feature.record.RecordPluginImpl.INTENT_IS_VIDEO_TYPE;
import static com.hzlz.aviation.feature.record.recorder.helper.RecordSharedPrefs.KEY_SELECT_VIDEO_MAX_TIME;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.record.recorder.helper.IRecordTimeOutListener;
import com.hzlz.aviation.feature.record.recorder.helper.RecordSharedPrefs;
import com.hzlz.aviation.feature.record.recorder.helper.VideoRecordHelper;
import com.hzlz.aviation.feature.record.recorder.helper.VideoRecordManager;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.permission.PermissionCallback;
import com.hzlz.aviation.kernel.base.permission.PermissionManager;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.widget.widget.GVideoTextView;
import com.hzlz.aviation.feature.record.R;

import java.io.File;
import java.util.List;

/**
 * 录制界面 ViewModel
 */
public class RecordViewModel extends BaseViewModel {

    /**
     * 录制更新间隔时间
     */
    private static final int RECORD_INTERVAL_TIME = 100;
    /**
     * 选择视频默认最大时长（s）
     */
    public static final int RECORD_VIDEO_MAX_TIME_DEFAULT = 500;

    /**
     * 当前持有的surface holder
     */
    private SurfaceHolder mSurfaceHolder;
    /**
     * 录制角度
     */
    private int mDegrees;
    /**
     * 视频id
     */
    private int mCameraId;
    /**
     * 当前持有的录制 helper类
     */
    private VideoRecordHelper mRecordHelper;
    /**
     * 当前持有录制按钮点击监听事件
     */
    private OnRecordButtonClickListener mOnRecordButtonClickListener;
    /**
     * 录像时间
     */
    MutableLiveData<Integer> recordTime = new MutableLiveData<>();
    /**
     * 标记当前是录制是否改变，需要进行二次处理
     */
    private boolean mIsChangeRecord = false;
    /**
     * 当前待处理的文件
     */
    private File mRecordFile;

    /**
     * 当前页面状态细分
     */
    private static final int STATE_INIT = 0;
    private static final int STATE_RECORD = 1;
    private static final int STATE_PAUSE = 2;
    private static final int STATE_DELETE_CONFIRM_ING = 3;
    /**
     * 当前页面状态
     */
    private int mRecordState = STATE_INIT;
    /**
     * 录制最大时长
     */
    private int recordMaxTime;

    public ObservableField<String> recordMaxTimeObservable = new ObservableField<>();

    /**
     * 构造函数
     */
    public RecordViewModel(@NonNull Application application) {
        super(application);
        mRecordHelper = new VideoRecordHelper();
        VideoRecordManager.getInstance().init(VideoRecordHelper.getRecordCacheBaseDir(), new VideoRecordManager.OnRecordCacheListener() {
            @Override
            public void onAllFileRemoved() {
                mRecordState = STATE_INIT;
                if (mOnRecordButtonClickListener != null) {
                    recordTime.postValue(0);
                    mOnRecordButtonClickListener.onChangeToInit();
                }
            }
        });
        recordMaxTime = RecordSharedPrefs.getInstance().getInt(KEY_SELECT_VIDEO_MAX_TIME, RECORD_VIDEO_MAX_TIME_DEFAULT);
        recordMaxTimeObservable.set(recordMaxTime+"");
        recordMaxTime = 1000 * recordMaxTime;
    }

    public boolean hasStartRecord() {
        return mRecordState != STATE_INIT;
    }

    @Override
    protected void onCleared() {
        VideoRecordManager.release();
        mSurfaceHolder = null;
        mRecordHelper = null;
        mOnRecordButtonClickListener = null;
        mRecordState = STATE_INIT;
    }

    /**
     * 处理surface 创建事件
     *
     * @param holder   持有surface容器
     * @param degrees  录制视频角度
     * @param cameraId 对应录制的cameraId
     */
    void onSurfaceCreated(SurfaceHolder holder, int degrees, int cameraId) {
        mSurfaceHolder = holder;
        mDegrees = degrees;
        mCameraId = cameraId;
        VideoRecordManager.getInstance().onSurfaceCreated(holder, degrees, cameraId);
    }

    /**
     * 处理surface 变化事件
     *
     * @param holder 持有surface容器
     */
    void onSurfaceChanged(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        VideoRecordManager.getInstance().onSurfaceChanged(holder);
    }

    /**
     * 处理cameraId 变化事件
     *
     * @param degrees  录制视频角度
     * @param cameraId 对应录制的cameraId
     */
    void changeCameraId(int degrees, int cameraId) {
        VideoRecordManager.getInstance().updateCamera(degrees, cameraId);
    }

    /**
     * 设置录制按钮事件监听
     *
     * @param listener 对应监听器
     */
    void setOnRecordButtonClickListener(OnRecordButtonClickListener listener) {
        mOnRecordButtonClickListener = listener;
    }

    /**
     * 录制按钮点击事件
     */
    public void onRecordItemClick() {
        if (mRecordState == STATE_INIT) {
            if (mOnRecordButtonClickListener != null) {
                mOnRecordButtonClickListener.onInitStateClick();
            }
        }
        if (mRecordState == STATE_RECORD) {
            mRecordState = STATE_PAUSE;
            if (mOnRecordButtonClickListener != null) {
                mOnRecordButtonClickListener.onChangeToPause();
            }
            pauseRecord();
        } else {
            int lastState = mRecordState;
            mRecordState = STATE_RECORD;
            if (lastState == STATE_INIT) {
                startRecord();
            } else {
                resumeRecord();
            }
        }
    }

    /**
     * 删除按钮点击事件
     */
    public void onDeleteItemClick() {
        if (mRecordState == STATE_DELETE_CONFIRM_ING) {
            mRecordState = STATE_PAUSE;
            if (mOnRecordButtonClickListener != null) {
                mOnRecordButtonClickListener.onFinishDeleteConfirm(true);
            }
            handleDeleteConfirm();
        } else if (mRecordState == STATE_PAUSE) {
            mRecordState = STATE_DELETE_CONFIRM_ING;
            if (mOnRecordButtonClickListener != null) {
                mOnRecordButtonClickListener.onChangeToDeleteConfirm();
            }
            pauseRecord();
        }
    }

    /**
     * 结束按钮点击事件
     *
     * @param timeView 显示录制时间的View
     */
    public void onFinishItemClick(GVideoTextView timeView) {
        if (timeView == null) {
            return;
        }
        CharSequence content = timeView.getText();
        if (TextUtils.isEmpty(content)) {
            return;
        }

        if (mOnRecordButtonClickListener != null) {
            mOnRecordButtonClickListener.onFinishRecord();
        }
        stopRecord(Float.parseFloat(content.toString()) < 1.0f);
    }

    /**
     * 选择视频按钮点击事件
     *
     * @param v 点击的视图
     */
    public void onChooseItemClick(View v) {
        PermissionManager
                .requestPermissions(v.getContext(), new PermissionCallback() {
                    @Override
                    public void onPermissionGranted(@NonNull Context context) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(INTENT_IS_VIDEO_TYPE, true);
                        Navigation.findNavController(v).navigate(R.id.chooseImageVideoFragment, bundle);
                    }

                    @Override
                    public void onPermissionDenied(
                            @NonNull Context context,
                            @Nullable String[] grantedPermissions,
                            @NonNull String[] deniedPermission) {
                        Toast.makeText(context, "没有存储权限", Toast.LENGTH_SHORT).show();
                    }
                },Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 处理删除确认事件
     */
    private void handleDeleteConfirm() {
        mIsChangeRecord = true;
        VideoRecordManager.getInstance().removeLastFile();
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        recordTime.postValue(0);
        boolean success = VideoRecordManager.getInstance().startRecord(false);
        if (success) {
            mIsChangeRecord = true;

            startTime(recordMaxTime);
            if (mOnRecordButtonClickListener != null) {
                mOnRecordButtonClickListener.onFinishDeleteConfirm(false);
                mOnRecordButtonClickListener.onChangeToRecord(recordMaxTime);
            }
        }
    }

    /**
     * 恢复录制
     */
    private void resumeRecord() {
        boolean success = VideoRecordManager.getInstance().startRecord(true);
        if (success) {
            mIsChangeRecord = true;
            int time = recordTime.getValue() == null ? 0 : recordTime.getValue();
            int totalTime = recordMaxTime - time;
            startTime(totalTime);
            if (mOnRecordButtonClickListener != null) {
                mOnRecordButtonClickListener.onFinishDeleteConfirm(false);
                mOnRecordButtonClickListener.onChangeToRecord(totalTime);
            }
        }
    }

    /**
     * 暂停录制
     */
    private void pauseRecord() {
        VideoRecordManager.getInstance().stopRecord(true);
        if (mRecordHelper != null) {
            mRecordHelper.cancelTimer();
        }
    }

    /**
     * 停止录制
     */
    private void stopRecord(boolean isVideoToShort) {
        List<File> videoFiles = VideoRecordManager.getInstance().stopRecord(false);
        if (mRecordHelper != null) {
            mRecordHelper.cancelTimer();
//            if (VideoRecordHelper.hasStoragePermission()) {
            if (mOnRecordButtonClickListener != null && !isVideoToShort) {
                mOnRecordButtonClickListener.onNavigateToUploadStart();
            }
            if (mIsChangeRecord || mRecordFile == null || !mRecordFile.exists()) {
                mIsChangeRecord = true;
                mRecordHelper.handleRecordFile(
                        GVideoRuntime.getAppContext(),
                        videoFiles,
                        VideoRecordManager.getInstance().getTempPath(),
                        mDegrees
                ).observeOn(GVideoSchedulers.MAIN)
                        .subscribe(new BaseGVideoResponseObserver<File>() {
                            @Override
                            protected void onRequestData(File resultFile) {
                                MediaScannerConnection.scanFile(GVideoRuntime.getAppContext(),
                                        new String[]{resultFile.getPath()}, null, null);
                                mRecordFile = resultFile;
                                mIsChangeRecord = false;
                                if (mOnRecordButtonClickListener != null) {
                                    if (isVideoToShort) {
                                        Toast.makeText(GVideoRuntime.getAppContext(), "请拍摄1s以上的视频内容", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mOnRecordButtonClickListener.onNavigateToUpload(resultFile);
                                    }
                                }
                            }
                        });
            } else {
                if (mOnRecordButtonClickListener != null) {
                    if (isVideoToShort) {
                        Toast.makeText(GVideoRuntime.getAppContext(), "请拍摄1s以上的视频内容", Toast.LENGTH_SHORT).show();
                    } else {
                        mOnRecordButtonClickListener.onNavigateToUpload(mRecordFile);
                    }
                }
            }
        }
    }

    /**
     * 开始计时
     *
     * @param totalTime 计时总时长
     */
    private void startTime(int totalTime) {
        if (mRecordHelper != null) {
            mRecordHelper.startTimer(new IRecordTimeOutListener() {
                @Override
                public void timeout() {
                    recordTime.postValue(recordMaxTime);
                    AsyncUtils.runOnUIThread(() -> stopRecord(false));
                }

                @Override
                public void cancel() {

                }

                @Override
                public void onTimeInterval() {
                    int time = recordTime.getValue() == null ? 0 : recordTime.getValue();
                    int currentTime = time + RECORD_INTERVAL_TIME;
                    if (currentTime > recordMaxTime) {
                        currentTime = recordMaxTime;
                    }
                    recordTime.postValue(currentTime);
                }
            }, totalTime, RECORD_INTERVAL_TIME);
        }
    }

    /**
     * 删除所有文件
     */
    public void removeAllFile() {
        VideoRecordManager.getInstance().removeAllFile();
    }

    /**
     * fragment resume生命周期触发，设置record
     */
    void onFragmentResume() {
        if (mSurfaceHolder == null) {
            return;
        }
        VideoRecordManager.getInstance().onSurfaceCreated(mSurfaceHolder, mDegrees, mCameraId);
    }

    /**
     * fragment pause生命周期触发，暂停录制，释放资源
     */
    void onFragmentPause() {
        if (mRecordState == STATE_RECORD) {
            mRecordState = STATE_PAUSE;
            if (mOnRecordButtonClickListener != null) {
                mOnRecordButtonClickListener.onChangeToPause();
            }
            pauseRecord();
        }
        VideoRecordManager.release();
    }

    /**
     * 录制按钮点击事件监听
     */
    public interface OnRecordButtonClickListener {

        /**
         * 初始态点击
         */
        void onInitStateClick();

        /**
         * 切换到pause
         */
        void onChangeToPause();

        /**
         * 切换到record
         *
         * @param recordTime 剩余时间
         */
        void onChangeToRecord(int recordTime);

        /**
         * 切换到删除确认
         */
        void onChangeToDeleteConfirm();

        /**
         * 结束删除确认
         *
         * @param hasRealDelete 是否真正删除
         */
        void onFinishDeleteConfirm(boolean hasRealDelete);

        /**
         * 结束录制
         */
        void onFinishRecord();

        /**
         * 切换到init事件
         */
        void onChangeToInit();

        /**
         * 切换到init事件
         */
        void onNavigateToUploadStart();

        /**
         * 切换到init事件
         */
        void onNavigateToUpload(File file);

    }
}
