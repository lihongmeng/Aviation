package com.jxntv.record.recorder.fragment.record;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import com.jxntv.base.BackPressHandler;
import com.jxntv.base.BaseActivity;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.dialog.DefaultEnsureCancelDialog;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.record.R;
import com.jxntv.record.databinding.FragmentRecordBinding;
import com.jxntv.record.recorder.data.ImageVideoEntity;
import com.jxntv.record.recorder.dialog.ProcessVideoDialog;
import com.jxntv.record.recorder.helper.VideoChooseHelper;
import com.jxntv.record.recorder.model.ChooseVideoModel;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.ScreenUtils;

import java.io.File;

import static com.jxntv.base.plugin.RecordPlugin.EVENT_BUS_VIDEO_ADDRESS;

/**
 * 录制界面
 */
public class RecordFragment extends BaseFragment<FragmentRecordBinding>
        implements SurfaceHolder.Callback {

    /**
     * 动画时长
     */
    private static final int ANIM_DURATION = 200;
    /**
     * 当前fragment持有的view model
     */
    private RecordViewModel mRecordViewModel;
    /**
     * 当前fragment持有的surface
     */
    private SurfaceHolder mSurfaceHolder;
    /**
     * 当前持有的camera id
     */
    private int mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    /**
     * 注册back处理
     */
    private final BackPressHandler mBackPressHandler = () -> {
        handleBack();
        return true;
    };
    private ProcessVideoDialog mVideoProcessDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    protected void initView() {
        mSurfaceHolder = mBinding.cameraPreview.getHolder();
        mSurfaceHolder.addCallback(this);
        mBinding.cameraCancel.setOnClickListener(v -> handleBack());
        mBinding.cameraChange.setOnClickListener(v -> {
            if (mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            mRecordViewModel.changeCameraId(getDegree(), mCurrentCameraId);
        });
        mBinding.finishRecordImg.setOnClickListener(clickListener);
        mBinding.finishRecordText.setOnClickListener(clickListener);


    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mRecordViewModel.onFinishItemClick(mBinding.recordTime);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ImmersiveUtils.disableImmersive(activity, true, true);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((BaseActivity) activity).registerBackPressHandler(mBackPressHandler);
        }
        mRecordViewModel.onFragmentResume();
        ImmersiveUtils.enterImmersive(this, Color.BLACK, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).unregisterBackPressHandler(mBackPressHandler);
            ScreenUtils.setNonFullScreen(activity);
        }
        mRecordViewModel.onFragmentPause();
    }

    /**
     * 处理回退事件
     */
    private void handleBack() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (!mRecordViewModel.hasStartRecord()) {
            activity.finish();
            return;
        }

        DefaultEnsureCancelDialog dialog = new DefaultEnsureCancelDialog(activity);
        dialog.init(v -> dialog.cancel(),
                v -> {
                    mRecordViewModel.removeAllFile();
                    dialog.cancel();
                    activity.finish();
                }, GVideoRuntime.getAppContext().getResources().getString(R.string.cancel_record_title),
                GVideoRuntime.getAppContext().getResources().getString(R.string.cancel_record_text));
        dialog.show();
    }

    @Override
    protected void bindViewModels() {
        mRecordViewModel = bingViewModel(RecordViewModel.class);
        mBinding.setViewModel(mRecordViewModel);

        mRecordViewModel.recordTime.observe(this, time -> {
            Context context = getContext();
            if (context == null) {
                return;
            }
            if (time == null || time < 0) {
                time = 0;
            }
            float currentTime = time / 1000f;
            String format = context.getResources().getString(R.string.record_time_text);
            mBinding.recordTime.setText(String.format(format, currentTime));
        });

        mRecordViewModel.setOnRecordButtonClickListener(new RecordViewModel.OnRecordButtonClickListener() {
            @Override
            public void onInitStateClick() {
                handleInitStateClick();
            }

            @Override
            public void onChangeToPause() {
                handleChangeToPause();
            }

            @Override
            public void onChangeToRecord(int recordTime) {
                handleChangeToRecord(recordTime);
            }

            @Override
            public void onChangeToDeleteConfirm() {
                handleChangeToDeleteConfirm();
            }

            @Override
            public void onFinishDeleteConfirm(boolean hasRealDelete) {
                handleFinishDeleteConfirm(hasRealDelete);
            }

            @Override
            public void onFinishRecord() {
            }

            @Override
            public void onChangeToInit() {
                handleChangeToInit();
            }

            @Override
            public void onNavigateToUploadStart() {
                Context context = getContext();
                if (context == null) {
                    return;
                }
                if (mVideoProcessDialog == null) {
                    mVideoProcessDialog = new ProcessVideoDialog(context);
                }
                mVideoProcessDialog.show();
            }

            @Override
            public void onNavigateToUpload(File file) {
                if (mVideoProcessDialog != null) {
                    mVideoProcessDialog.dismiss();
                }
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                ImageVideoEntity imageVideoEntity = new ImageVideoEntity();
                imageVideoEntity.path = file.getAbsolutePath();
                imageVideoEntity.isVideo = true;
                VideoChooseHelper.getInstance().setPreviewVideoEntity(imageVideoEntity);
                GVideoEventBus.get(EVENT_BUS_VIDEO_ADDRESS, ChooseVideoModel.class)
                        .post(new ChooseVideoModel(file.getAbsolutePath()));
                activity.finish();
            }
        });
    }

    @Override
    protected void loadData() {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        mRecordViewModel.onSurfaceCreated(holder, getDegree(), mCurrentCameraId);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
        mRecordViewModel.onSurfaceChanged(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * 处理初始化状态下的 record按钮点击事件
     */
    private void handleInitStateClick() {
        mBinding.recordMainButton.setBackground(GVideoRuntime.getAppContext().getResources().getDrawable(R.drawable.fragment_record_button_recording));
        mBinding.recordSign.setVisibility(View.VISIBLE);

        int startSize = GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.fragment_record_main_button_size);
        int endSize = GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.fragment_record_main_button_background_size);
        ValueAnimator animator = ValueAnimator.ofInt(startSize, endSize);
        animator.addUpdateListener(animation -> {
            int size = (int) animation.getAnimatedValue();
            mBinding.recordMainButtonBackground.getLayoutParams().height = size;
            mBinding.recordMainButtonBackground.getLayoutParams().width = size;
            mBinding.recordMainButtonBackground.requestLayout();
        });
        animator.setDuration(ANIM_DURATION);
        mBinding.recordMainButtonBackground.setVisibility(View.VISIBLE);
        mBinding.recordInitText.setVisibility(View.GONE);
        mBinding.recordInitView.setVisibility(View.GONE);
        mBinding.uploadFile.setVisibility(View.GONE);
        mBinding.uploadFileText.setVisibility(View.GONE);
        mBinding.finishRecordText.setVisibility(View.VISIBLE);
        mBinding.finishRecordImg.setVisibility(View.VISIBLE);
        mBinding.recordTime.setVisibility(View.VISIBLE);
        animator.start();
    }

    /**
     * 处理切换到pause事件
     */
    private void handleChangeToPause() {
        mBinding.recordProgress.pauseRecord();
        mBinding.recordSign.startChangeStateAnim(true);
        mBinding.recordDeleteText.setVisibility(View.VISIBLE);
        mBinding.recordDeleteImg.setVisibility(View.VISIBLE);
        mBinding.cameraChange.setVisibility(View.VISIBLE);
        mBinding.cameraChangeText.setVisibility(View.VISIBLE);
        mBinding.cameraCancel.setVisibility(View.VISIBLE);
    }

    /**
     * 处理切换到录制状态事件
     */
    private void handleChangeToRecord(int recordTime) {
        mBinding.recordProgress.startRecord(recordTime);
        mBinding.recordSign.startChangeStateAnim(false);
        mBinding.recordDeleteText.setText(GVideoRuntime.getAppContext().getResources().getString(R.string.delete_record_text));
        mBinding.recordDeleteText.setVisibility(View.GONE);
        mBinding.recordDeleteImg.setVisibility(View.GONE);
        mBinding.cameraChange.setVisibility(View.GONE);
        mBinding.cameraChangeText.setVisibility(View.GONE);
        mBinding.cameraCancel.setVisibility(View.GONE);
    }

    /**
     * 处理切换到删除确认状态
     */
    private void handleChangeToDeleteConfirm() {
        mBinding.recordDeleteText.setText(GVideoRuntime.getAppContext().getResources().getString(
                R.string.delete_record_verify_text));
        mBinding.recordSign.startChangeStateAnim(true);
        mBinding.recordProgress.showDeleteConfirmAnim();
    }

    /**
     * 处理切换到删除确认状态
     */
    private void handleFinishDeleteConfirm(boolean hasRealDelete) {
        mBinding.recordDeleteText.setText(GVideoRuntime.getAppContext().getResources().getString(R.string.delete_record_text));
        mBinding.recordProgress.handleDeleteConfirm(hasRealDelete);
    }

    /**
     * 处理切换到回归初始化事件
     */
    private void handleChangeToInit() {
        mBinding.recordMainButtonBackground.setVisibility(View.GONE);
        mBinding.recordInitText.setVisibility(View.VISIBLE);
        mBinding.recordInitView.setVisibility(View.VISIBLE);
        mBinding.recordDeleteImg.setVisibility(View.GONE);
        mBinding.recordDeleteText.setVisibility(View.GONE);
        mBinding.uploadFile.setVisibility(View.VISIBLE);
        mBinding.uploadFileText.setVisibility(View.VISIBLE);
        mBinding.finishRecordText.setVisibility(View.GONE);
        mBinding.finishRecordImg.setVisibility(View.GONE);
        mBinding.recordSign.setVisibility(View.GONE);
        mBinding.recordTime.setVisibility(View.GONE);
        mBinding.recordMainButton.setBackground(GVideoRuntime.getAppContext().getResources().getDrawable(R.drawable.fragment_record_button));
        mBinding.recordProgress.reset();
    }

    /**
     * 获取旋转角度
     *
     * @return 获取当前的初始化角度
     */
    private int getDegree() {
        Context context = getContext();
        if (!(context instanceof Activity)) {
            return 0;
        }
        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCurrentCameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // 前置摄像头
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            // 后置摄像头
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
}
