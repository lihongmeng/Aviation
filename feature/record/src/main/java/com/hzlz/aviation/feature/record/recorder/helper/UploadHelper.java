package com.hzlz.aviation.feature.record.recorder.helper;

import com.alibaba.sdk.android.vod.upload.VODUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODUploadClient;
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.UploadFileInfo;
import com.alibaba.sdk.android.vod.upload.model.VodInfo;
import com.hzlz.aviation.feature.record.recorder.model.VodVideoCreateModel;
import com.hzlz.aviation.feature.record.recorder.repository.VideoRecordRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.plugin.CompositionPlugin;
import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.base.plugin.ICompositionRepository;
import com.hzlz.aviation.kernel.base.plugin.IFileRepository;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 上传辅助类
 */
public class UploadHelper {

  /** 文件仓库接口 */
  private IFileRepository mFileRepository;
  /** 录制仓库接口 */
  private VideoRecordRepository mRecordRepository;
  /** 作品仓库接口 */
  private ICompositionRepository mCompositionRepository;

  /** 上传事件结果监听 */
  private UploadListener mListener;

  /** 待上传的image Id */
  private String mImageId;
  /** 待上传的video Id */
  private String mVideoId;
  /** 待上传的简介 */
  private String mIntroduction;
  /** 待上传资源是否公开 */
  private boolean mIsPublic;

  /** 标记：是否已经上传图片 */
  private boolean mHasUploadImage = false;
  /** 标记：是否已经上传视频 */
  private boolean mHasUploadVideo = false;

  /** 视频上传后缀 */
  private static final String VOD_VIDEO_CREATE_FILE_SUFFIX = ".mp4";
  /** 超时总时长限制 */
  private static final long UPLOAD_TIME_LIMIT = 2 * 60 * 1000;
  /** 超时总时长错误 */
  public static final String ERROR_TIME_LIMIT = "error_time_limit";

  /**
   * 构造函数
   *
   * @param listener      上传事件监听
   * @param introduction  上传简介
   * @param isPublic      上传是否为公开
   */
  public UploadHelper(UploadListener listener, String introduction, boolean isPublic) {
    mFileRepository = PluginManager.get(FilePlugin.class).getFileRepository();
    mCompositionRepository = PluginManager.get(CompositionPlugin.class).getCompositionRepository();
    mRecordRepository = new VideoRecordRepository();
    mListener = listener;
    mIntroduction = introduction == null ? "" : introduction;
    mIsPublic = isPublic;
  }

  /**
   * 上传文件
   *
   * @param image 待上传的图片文件
   * @param video 待上传的视频文件
   */
  public void uploadFile(File image, File video) {
    if (image == null || video == null
        || !image.exists() || !video.exists()) {
      if (mListener != null) {
        mListener.onError("file_error", "file_error");
      }
      return;
    }
    if (mListener != null) {
      mListener.onStart();
    }
    long startTime = System.currentTimeMillis();
    // 1. 上传图片
    mFileRepository.doUploadImageFile(IFileRepository.FILE_BUSINESS_TYPE_MEDIA_COVER, image, GVideoSchedulers.IO_PRIORITY_USER)
        .timeout(UPLOAD_TIME_LIMIT, TimeUnit.MILLISECONDS)
        .subscribe(new BaseViewModel.BaseGVideoResponseObserver<String>() {
          @Override
          protected void onRequestData(String imgId) {
            mHasUploadImage = true;
            mImageId = imgId;
            checkCouldPublish(startTime);
          }

          @Override
          protected void onRequestError(Throwable throwable) {
            if (mListener != null) {
              if (throwable instanceof TimeoutException ||
                  throwable instanceof SocketTimeoutException) {
                mListener.onError(ERROR_TIME_LIMIT, ERROR_TIME_LIMIT);
                return;
              }
              mListener.onError("image_error", throwable.getMessage());
            }
          }
        });


    // 2. 上传video
    // 首先需要查询对应的id，需要用时间戳生成一个带后缀名的文件名即可
    mRecordRepository.createVodVideoModel(System.currentTimeMillis() + VOD_VIDEO_CREATE_FILE_SUFFIX)
        .timeout(UPLOAD_TIME_LIMIT, TimeUnit.MILLISECONDS)
        .subscribe(new BaseViewModel.BaseGVideoResponseObserver<VodVideoCreateModel>() {
          @Override
          protected void onRequestData(VodVideoCreateModel model) {
            // 获取id后再进行上传
            mVideoId = model.getVideoId();
            handleVODUpdate(model, video, startTime);
          }

          @Override
          protected void onRequestError(Throwable throwable) {
            if (mListener != null) {
              if (throwable instanceof TimeoutException ||
                  throwable instanceof SocketTimeoutException) {
                mListener.onError(ERROR_TIME_LIMIT, ERROR_TIME_LIMIT);
                return;
              }
              mListener.onError("create_video_error", throwable.getMessage());
            }
          }
        });

  }

  /**
   * 处理上传vod视频
   *
   * @param vodVideoCreateModel  对应Vod数据模型
   * @param video                待上传的视频文件
   */
  private void handleVODUpdate(VodVideoCreateModel vodVideoCreateModel, File video, long startTime) {
    long updateStartTime = System.currentTimeMillis();
    long limitTime = UPLOAD_TIME_LIMIT - (updateStartTime - startTime);
    VODUploadClient uploadClient = new VODUploadClientImpl(GVideoRuntime.getAppContext());
    uploadClient.init(new VODUploadCallback() {
      @Override
      public void onUploadSucceed(UploadFileInfo info) {
        super.onUploadSucceed(info);
        mHasUploadVideo = true;
        checkCouldPublish(startTime);
      }

      @Override
      public void onUploadFailed(UploadFileInfo info, String code, String message) {
        super.onUploadFailed(info, code, message);
        if (mListener != null) {
          mListener.onError(code, message);
        }
      }

      @Override
      public void onUploadProgress(UploadFileInfo info, long uploadedSize, long totalSize) {
        super.onUploadProgress(info, uploadedSize, totalSize);
        if (mListener != null) {
          // 超时
          if (System.currentTimeMillis() - updateStartTime > limitTime) {
            if (uploadClient.listFiles() != null) {
              int index = uploadClient.listFiles().size() - 1;
              uploadClient.cancelFile(index);
            }
            mListener.onError(ERROR_TIME_LIMIT, ERROR_TIME_LIMIT);
            return;
          }
          if (totalSize == 0) {
            return;
          }
          int rate = (int) ((float) uploadedSize / (float) totalSize * 100);
          if (rate > 99) {
            rate = 99;
          } else if (rate < 0) {
            rate = 0;
          }
          mListener.onProgress(rate);
        }
      }

      @Override
      public void onUploadTokenExpired() {
        super.onUploadTokenExpired();
      }

      @Override
      public void onUploadRetry(String code, String message) {
        super.onUploadRetry(code, message);
      }

      @Override
      public void onUploadRetryResume() {
        super.onUploadRetryResume();
      }

      @Override
      public void onUploadStarted(UploadFileInfo uploadFileInfo) {
        super.onUploadStarted(uploadFileInfo);
        uploadClient.setUploadAuthAndAddress(uploadFileInfo,
            vodVideoCreateModel.getUploadAuth(),
            vodVideoCreateModel.getUploadAddress());
      }
    });
    uploadClient.addFile(video.getAbsolutePath(), new VodInfo());
    uploadClient.start();
  }

  /**
   * 检查是否可以进入发布流程
   */
  private void checkCouldPublish(long startTime) {
    // 只有当图片和视频上传均完成，才进入发布流程
    if (!mHasUploadImage || !mHasUploadVideo) {
      return;
    }
    long limitTime = UPLOAD_TIME_LIMIT - (System.currentTimeMillis() - startTime);
    if (limitTime <= 0) {
      if (mListener != null) {
        mListener.onError(ERROR_TIME_LIMIT, ERROR_TIME_LIMIT);
      }
      return;
    }
    handlePublish(limitTime);
  }

  /**
   * 处理发布逻辑
   */
  private void handlePublish(long limitTime) {
    mCompositionRepository.publish(mIsPublic, mIntroduction, mImageId, mVideoId,"")
        .timeout(limitTime, TimeUnit.MILLISECONDS)
        .subscribe(new BaseViewModel.BaseGVideoResponseObserver<Integer>() {
          @Override
          protected void onRequestData(Integer result) {
            if (mListener != null) {
              mListener.onSuccess();
            }
          }

          @Override
          protected void onRequestError(Throwable throwable) {
            if (mListener != null) {
              if (throwable instanceof TimeoutException ||
                  throwable instanceof SocketTimeoutException) {
                mListener.onError(ERROR_TIME_LIMIT, ERROR_TIME_LIMIT);
                return;
              }
              mListener.onError("error_in_publish", throwable.getMessage());
            }
          }
        });
  }


  /**
   * 上传接口
   */
  public interface UploadListener {
    /**
     * 上传进度回调
     *
     * @param rate 进度比例
     */
    void onProgress(int rate);
    /**
     * 上传成功回调
     */
    void onSuccess();
    /**
     * 上传失败回调
     *
     * @param code    失败code
     * @param message 失败message
     */
    void onError(String code, String message);
    /**
     * 上传开始回调
     */
    void onStart();
  }
}
