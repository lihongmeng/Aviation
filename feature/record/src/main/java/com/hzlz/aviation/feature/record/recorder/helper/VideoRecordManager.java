package com.hzlz.aviation.feature.record.recorder.helper;

import static android.hardware.Camera.Parameters.FLASH_MODE_AUTO;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.FileUtils;
import com.hzlz.aviation.library.util.ScreenUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 视频录制类
 */
public class VideoRecordManager {

  /** 持有的单例 */
  private volatile static VideoRecordManager sInstance;
  /** 取理想拉伸比阈值 */
  private static final float IDEA_RADIO_THRESHOLD = 0.2f;

  /** 持有的media recorder 用于录制 */
  private MediaRecorder mMediaRecorder;
  /** camera实例 camera2 api需21以上，故此处依旧使用camera api */
  private Camera mCamera;
  /** 录制角度 */
  private int mDegrees;
  /** 录制camera id */
  private int mCameraId;
  /** 持有的surface holder */
  private SurfaceHolder mHolder;
  /** 持有的surface holder */
  private String mTargetFlashMode = FLASH_MODE_AUTO;
  /** 标记camera是否已经初始化 */
  private boolean mIsCameraInit = false;

  /** 标记当前是否正在处理删除事件 */
  private boolean mIsDelete = false;
  /** 录制cache处理事件监听 */
  private OnRecordCacheListener mCacheListener;

  /** 持有的文件夹地址 */
  private File mBaseFile;
  /** 保存视频的路径 */
  private File mCurrentVideoPath;
  /** 视频格式--前缀 */
  public static final String VIDEO_PREFIX = "KGV_";
  /** 视频格式--后缀 */
  public static final String VIDEO_SUFFIX = ".mp4";
  /** 当前录制文件名集合 */
  private List<File> mFileNames = new ArrayList<>();

  /** 当前录制状态 */
  private int mRecordState = STATE_STOP;
  /** 录制状态：细分 */
  public static final int STATE_STOP = 0;
  public static final int STATE_RECORD = 1;
  public static final int STATE_PAUSE = 2;
  public static final int STATE_DELETE_CONFIRM = 3;

  /**
   * 获取manager单例
   */
  public static VideoRecordManager getInstance() {
    if (sInstance == null) {
      synchronized (VideoRecordManager.class) {
        if (sInstance == null) {
          sInstance = new VideoRecordManager();
        }
      }
    }
    return sInstance;
  }

  /**
   * 私有构造器
   */
  private VideoRecordManager() {
  }

  /**
   * 初始化
   *
   * @param baseFile  文件根目录
   * @param listener  录制cache处理监听
   */
  public void init(File baseFile, OnRecordCacheListener listener) {
    mBaseFile = baseFile;
    mCacheListener = listener;
  }

  /**
   * 获取摄像头实例
   */
  private Camera getCameraInstance() {
    if (mCamera != null) {
      mCamera.stopPreview();
      mCamera.release();
      mCamera = null;
    }
    try {
      // 打开摄像头
      mCamera = Camera.open(mCameraId);
      // 闪光灯
      changeFlashMode(mTargetFlashMode);
      Camera.Parameters parameters = mCamera.getParameters();
      int width = ScreenUtils.getScreenWidth(GVideoRuntime.getAppContext());
      int height = ScreenUtils.getScreenHeight(GVideoRuntime.getAppContext());
      Camera.Size previewSize = getBestPreviewSize(parameters.getSupportedPreviewSizes(), width, height);
      if (previewSize != null) {
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
      }
      mCamera.setParameters(parameters);
    } catch (Exception e) {

    }
    return mCamera;
  }

  /**
   * 处理surface 创建事件
   *
   * @param holder    持有surface容器
   * @param degrees   录制视频角度
   * @param cameraId  对应录制的cameraId
   */
  public void onSurfaceCreated(SurfaceHolder holder, int degrees, int cameraId) {
    mCameraId = cameraId;
    mDegrees = degrees;
    mHolder = holder;
    initCamera();
  }

  /**
   * camera初始化
   */
  private void initCamera() {
    if (mIsCameraInit) {
      return;
    }
    try {
      getCameraInstance();
      if (mCamera == null) {
        return;
      }
      mCamera.setPreviewDisplay(mHolder);
      mCamera.setDisplayOrientation(mDegrees);
      mCamera.startPreview();
      mCamera.unlock();
      mIsCameraInit = true;
    } catch (IOException e) {
    }
  }

  /**
   * 处理surface 变化事件
   *
   * @param holder    持有surface容器
   */
  public void onSurfaceChanged(SurfaceHolder holder) {
    mHolder = holder;
    updateCamera();
  }

  /**
   * 处理cameraId 变化事件
   *
   * @param degree    录制视频角度
   * @param cameraId  对应录制的cameraId
   */
  public void updateCamera(int degree, int cameraId) {
    mDegrees = degree;
    mCameraId = cameraId;
    updateCamera();
  }

  /**
   * 更新本地camera
   */
  private void updateCamera() {
    try {
      if (mCamera != null) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
      }
      // 更新摄像头
      getCameraInstance();
      if (mCamera != null) {
        mCamera.setPreviewDisplay(mHolder);
        mCamera.setDisplayOrientation(mDegrees);
        mCamera.startPreview();
      }
      mIsCameraInit = true;
    } catch (IOException e) {

    }
  }

  /**
   * 处理闪光灯
   *
   * @param targetFlashMode 目标flash模式
   */
  private void changeFlashMode(String targetFlashMode) {
    if (mCamera == null) {
      return;
    }
    Camera.Parameters parameters = null;
    try {
      parameters = mCamera.getParameters();
    } catch (Exception e) {

    }
    if (parameters == null) {
      return;
    }
    List<String> flashModeList = parameters.getSupportedFlashModes();
    if (flashModeList == null || flashModeList.isEmpty()) {
      return;
    }
    String curtMode = parameters.getFlashMode();
    if (!TextUtils.equals(targetFlashMode, curtMode)) {
      if (flashModeList.contains(targetFlashMode)) {
        mTargetFlashMode = targetFlashMode;
        parameters.setFlashMode(targetFlashMode);
        if (mCamera != null) {
          mCamera.setParameters(parameters);
        }
      }
    }
  }

  /**
   * 开始录像
   *
   * @param isResume  是否为resume状态
   */
  public boolean startRecord(boolean isResume) {
    if (mBaseFile == null) {
      return false;
    }
    if (!isResume) {
      mFileNames = new ArrayList<>();
    }
    setSavePath(mBaseFile);
    initCamera();
    try {
      if (prepareVideoRecorder(mHolder)) {
        mRecordState = STATE_RECORD;
        mMediaRecorder.start();
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      mRecordState = STATE_STOP;
      return false;
    }
  }

  /**
   * 停止录像
   *
   * @param isPause 是否为暂停事件
   * @return 当前缓存文件列表
   */
  public List<File> stopRecord(boolean isPause) {
    if (mRecordState != STATE_RECORD) {
      return mFileNames;
    }
    mRecordState = isPause ? STATE_PAUSE : STATE_STOP;
    releaseVideoRecord();

    if (mCamera != null && !isPause) {
      mCamera.lock();
    }
    return mFileNames;
  }

  /**
   * 移除最后一个缓存文件
   */
  public void removeLastFile() {
    removeFile(mFileNames.get(mFileNames.size() - 1), false);
  }

  /**
   * 移除所有缓存文件
   */
  public void removeAllFile() {
    removeFile(mBaseFile, true);
  }

  /**
   * 移除文件
   *
   * @param file            待移除文件
   * @param isRemoveAllFile 是否移除所有文件
   */
  private void removeFile(File file, boolean isRemoveAllFile) {
    if (file == null) {
      return;
    }
    mIsDelete = true;
    Observable.create(new ObservableOnSubscribe<Boolean>() {
      @Override
      public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
        boolean result = FileUtils.delete(file);
        e.onNext(result);
      }
    }).subscribeOn(GVideoSchedulers.IO_PRIORITY_USER).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Boolean>() {
          @Override
          public void onSubscribe(Disposable d) {
            mIsDelete = false;
          }

          @Override
          public void onNext(Boolean isSuccess) {
            mIsDelete = false;
            if (isSuccess) {
              if (isRemoveAllFile) {
                mFileNames = new ArrayList<>();
                if (mCacheListener != null) {
                  mCacheListener.onAllFileRemoved();
                }
              } else {
                mFileNames.remove(file);
                if (mFileNames.size() == 0) {
                  if (mCacheListener != null) {
                    mCacheListener.onAllFileRemoved();
                  }
                }
              }
            }
          }

          @Override
          public void onError(Throwable e) {
            mIsDelete = false;
          }

          @Override
          public void onComplete() {
            mIsDelete = false;
          }
        });
  }

  /**
   * 准备录像
   *
   * @param holder  是否为surface holder
   */
  private boolean prepareVideoRecorder(@NonNull SurfaceHolder holder) {
    releaseVideoRecord();

    mMediaRecorder = new MediaRecorder();
    mCamera.lock();
    mCamera.unlock();
    mMediaRecorder.setCamera(mCamera);

    if (VideoRecordHelper.hasRecordAudioPermission()) {
      mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
    }
    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
    CamcorderProfile profile = getCamcorderProfile();
    if (VideoRecordHelper.hasRecordAudioPermission()) {
      mMediaRecorder.setProfile(getCamcorderProfile());
    } else {
      mMediaRecorder.setOutputFormat(profile.fileFormat);
      mMediaRecorder.setVideoFrameRate(profile.videoFrameRate);
      mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
      mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
      mMediaRecorder.setVideoEncoder(profile.videoCodec);
    }
    mMediaRecorder.setOutputFile(mCurrentVideoPath.getPath());
    mMediaRecorder.setPreviewDisplay(holder.getSurface());
    mMediaRecorder.setOrientationHint(mDegrees);
    try {
      mMediaRecorder.prepare();
    } catch (IllegalStateException e) {
      return false;
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * 释放录音资源
   */
  private void releaseVideoRecord() {
    if (mMediaRecorder == null) {
      return;
    }

    try {
      mMediaRecorder.stop();
    } catch (Exception e) {

    } finally {
      mMediaRecorder.reset();
      mMediaRecorder.release();
      mMediaRecorder = null;
    }
  }

  /**
   * 获取拍摄质量CamcorderProfile
   */
  private CamcorderProfile getCamcorderProfile() {
    int quality = CamcorderProfile.QUALITY_720P;
    return CamcorderProfile.get(quality);
  }

  /**
   * 设置存储路径
   *
   * @param baseFile  目标地址
   */
  private void setSavePath(File baseFile) {
    mCurrentVideoPath = new File(baseFile,File.separator
        + VIDEO_PREFIX + Calendar.getInstance().getTimeInMillis() + VIDEO_SUFFIX);
    mFileNames.add(mCurrentVideoPath);
    FileUtils.createNewFileSafely(mCurrentVideoPath);
  }

  /**
   * 获取临时文件地址
   *
   * @return 临时文件地址
   */
  public File getTempPath() {
    return new File(mBaseFile, File.separator + VIDEO_PREFIX + "temp" + VIDEO_SUFFIX);
  }

  /**
   * 释放资源
   */
  public static void release() {
    if (sInstance != null) {
      sInstance.realRelease();
    }
  }

  /**
   * 释放资源
   */
  private void realRelease() {
    releaseVideoRecord();
    releaseCamera();
  }

  /**
   * 释放camera
   */
  private void releaseCamera() {
    if (mCamera != null) {
      mCamera.setPreviewCallback(null);
      mCamera.stopPreview();
      mCamera.release();
      mCamera = null;
      mIsCameraInit = false;
    }
  }

  /**
   * 获取最相近的尺寸
   *
   * @param supportedSizeList 支持的尺寸列表
   * @param width             View 宽度
   * @param height            View 高度
   */
  private Camera.Size getBestPreviewSize(List<Camera.Size> supportedSizeList, int width, int height) {
    if (supportedSizeList == null || supportedSizeList.isEmpty()) {
      return null;
    }

    float idealRatio = (float) width / height;
    Camera.Size targetSize = null;
    Camera.Size matchSize = null;
    float tmpDiff;
    float minDiff = -1f;
    boolean verticalCamera = mDegrees % 180 == 0;
    for (Camera.Size size : supportedSizeList) {
      float curtRatio;
      if (verticalCamera) {
        curtRatio = (float) size.width / size.height;
      } else {
        curtRatio = (float) size.height / size.width;
      }
      tmpDiff = Math.abs(curtRatio - idealRatio);
      if (minDiff < 0) {
        minDiff = tmpDiff;
        targetSize = size;
      }
      if (tmpDiff < minDiff) {
        minDiff = tmpDiff;
        targetSize = size;
      }

      // 宽度匹配
      int basic = !verticalCamera ? size.height : size.width;
      if (basic == width) {
        if (matchSize == null) {
          matchSize = size;
        } else {
          float matchRatio;
          if (verticalCamera) {
            matchRatio = (float) matchSize.width / matchSize.height;
          } else {
            matchRatio = (float) matchSize.height / matchSize.width;
          }
          float matchDiff = Math.abs(matchRatio - idealRatio);
          float curtDiff = Math.abs(curtRatio - idealRatio);
          if (curtDiff < matchDiff) {
            matchSize = size;
          }
        }
      }
    }

    if (matchSize != null && targetSize.width != matchSize.width) {
      float matchRatio = (float) matchSize.width / matchSize.height;
      float targetRatio = (float) targetSize.width / targetSize.height;
      if (Math.abs(matchRatio - targetRatio) < IDEA_RADIO_THRESHOLD) {
        return matchSize;
      }
    }

    return targetSize;
  }

  /**
   * 录制cache处理监听
   */
  public interface OnRecordCacheListener {
    /**
     * 所有临时文件删除回调
     */
    void onAllFileRemoved();
  }
}
