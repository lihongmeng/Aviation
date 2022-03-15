package com.hzlz.aviation.feature.record.recorder.helper;

import static com.hzlz.aviation.feature.record.recorder.helper.RecordSharedPrefs.KEY_SELECT_VIDEO_MAX_TIME;
import static com.hzlz.aviation.feature.record.recorder.helper.VideoRecordManager.VIDEO_PREFIX;
import static com.hzlz.aviation.feature.record.recorder.helper.VideoRecordManager.VIDEO_SUFFIX;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.hzlz.aviation.feature.record.recorder.data.ImageVideoEntity;
import com.hzlz.aviation.feature.record.recorder.process.VideoComposer;
import com.hzlz.aviation.feature.record.recorder.process.VideoScaleHelper;
import com.hzlz.aviation.kernel.base.utils.StorageUtils;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;

/**
 * 视频录制辅助类
 */
public class VideoRecordHelper implements IVideoRecordHelper  {

  private static final int MARSHMALLOW = 23;
  /** 视频type */
  private static final String VIDEO_MIME = "video/mp4";
  /** 视频type */
  private static final String VIDEO_FOLDER = "jinshipin";
  /** 存放文件地址 */
  private static final String FILE_RECORD = "record";

  /** 需要上传的单个文件 */
  public static final String FILE_UPLOAD_PATH = "file_upload_path";

  /** 需要上传的多个文件 */
  public static final String FILE_UPLOAD_PATH_LIST = "file_upload_path_list";

  /** 计时器 */
  private Timer mTimer;
  /** 计时结束事件回调 */
  private IRecordTimeOutListener mTimeOutListener;
  /** 视频缩放辅助类 */
  private VideoScaleHelper mVideoScaleHelper;

  /**
   * 构造方法
   */
  public VideoRecordHelper() {
    mVideoScaleHelper = new VideoScaleHelper();
  }

  @Override
  public void startTimer(IRecordTimeOutListener listener, int totalTime, int intervalTime) {
    mTimeOutListener = listener;
    if (mTimer != null) {
      mTimer.cancel();
    }
    mTimer = new Timer();
    mTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (mTimeOutListener != null) {
          mTimeOutListener.timeout();
        }
        stopTimer();
      }
    }, totalTime);
    mTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (mTimeOutListener != null) {
          mTimeOutListener.onTimeInterval();
        }
      }
    }, intervalTime, intervalTime);
  }

  @Override
  public void stopTimer() {
    mTimeOutListener = null;
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
  }

  @Override
  public void cancelTimer() {
    if (mTimeOutListener != null) {
      mTimeOutListener.cancel();
    }
    stopTimer();
  }

  @Override
  public void onError() {
  }

  /**
   * 处理录制文件
   *
   * @param context     上下文环境
   * @param videoFiles  录制完成的视频列表
   * @param tempFile    临时文件目录
   */
  public Observable<File> handleRecordFile(Context context, @NonNull List<File> videoFiles, File tempFile,
                                           int rotation) {
    return Observable.create(new ObservableOnSubscribe<File>() {
      @Override public void subscribe(ObservableEmitter<File> e)
          throws Exception {
        if (videoFiles.size() == 0) {
          return;
        }
        File targetFile;
        // 如果录制完成的视频只有一段，则无需进行合成
        if (videoFiles.size() == 1) {
          targetFile = videoFiles.get(0);
        } else {
          targetFile = tempFile;
          if (targetFile.exists()) {
            FileUtils.delete(targetFile);
          }
          VideoComposer composer = new VideoComposer(videoFiles, targetFile.getPath(), rotation);
          composer.joinVideo();
        }

        File resultFile = copyToTarget(targetFile);
        e.onNext(resultFile);
      }
    }).subscribeOn(GVideoSchedulers.IO_PRIORITY_USER);
  }

  /**
   * 广播插入相册逻辑
   *
   * @param sourceFile  源文件
   */
  public static File copyToTarget(File sourceFile)  {
    if (sourceFile == null || !sourceFile.exists()) {
      return sourceFile;
    }
    long createTime = System.currentTimeMillis();

    File targetFile;
    if (VideoRecordHelper.hasStoragePermission()) {

      targetFile = new File(Environment.getExternalStorageDirectory(),
              VIDEO_FOLDER + File.separator + VIDEO_PREFIX + createTime + VIDEO_SUFFIX);
    }else {
      targetFile = new File(StorageUtils.getVideoDirectory(), VIDEO_PREFIX + createTime + VIDEO_SUFFIX);
    }
    try {
      FileUtils.createNewFileSafely(targetFile);
      FileUtils.copyFile(sourceFile, targetFile);
    } catch (Exception e) {

    }
    return targetFile.exists() ? targetFile : sourceFile;
  }

  /**
   * 是否有录音权限
   *
   * @return 是否有相关权限
   */
  public static boolean hasRecordAudioPermission() {
    if (Build.VERSION.SDK_INT < MARSHMALLOW) {
      return true;
    }
    return ActivityCompat.checkSelfPermission(GVideoRuntime.getAppContext(), Manifest.permission.RECORD_AUDIO)
        == PackageManager.PERMISSION_GRANTED;
  }

  /**
   * 是否有存储权限
   *
   * @return 是否有相关权限
   */
  public static boolean hasStoragePermission() {
    if (Build.VERSION.SDK_INT < MARSHMALLOW) {
      return true;
    }
    return ActivityCompat.checkSelfPermission(GVideoRuntime.getAppContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(GVideoRuntime.getAppContext(),
        Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED;
  }

  /**
   * 获取录制根目录
   *
   * @return 录制根目录
   */
  public static File getRecordCacheBaseDir() {
    // 首先尝试获取外部目录文件夹
    File externalFile = GVideoRuntime.getAppContext().getExternalFilesDir(null);
    File file;
    if (externalFile != null) {
      file = new File(externalFile.getPath() + File.separator + FILE_RECORD);
      if (file.exists() || file.mkdirs()) {
        return file;
      }
    }

    // 获取私有目录文件夹
    file = new File(GVideoRuntime.getAppContext().getFilesDir().getPath() + File.separator + FILE_RECORD);
    if (file.exists() || FileUtils.createNewFileSafely(file)) {
      return file;
    }
    return null;
  }

  public Observable<File> tryProcessUploadVideo () {
    return Observable.create(new ObservableOnSubscribe<File>() {
      @Override public void subscribe(ObservableEmitter<File> e)
          throws Exception {
        VideoChooseHelper.getInstance().updateResultFile(null);
        List<ImageVideoEntity> list = VideoChooseHelper.getInstance().getSelectEntity();
        if (list == null) {
          return;
        }
        List<File> temFileList = new ArrayList<>();
        ImageVideoEntity entity;
        File tempFile;
        for (int i = 0; i < list.size(); i++) {
          entity = list.get(i);
          tempFile = getTempPath(entity);
          FileUtils.createNewFileSafely(tempFile);
          try {
            mVideoScaleHelper.scaleVideo(entity.path, tempFile.getPath());
          } catch (Exception e1) {
            continue;
          }
          temFileList.add(tempFile);
        }
        if (temFileList.size() == 0) {
          return;
        }
        File tempFile1 = new File(VideoRecordHelper.getRecordCacheBaseDir(), File.separator + VIDEO_PREFIX + "temp_" + System.currentTimeMillis() + VIDEO_SUFFIX);
        File targetFile;
        // 如果录制完成的视频只有一段，则无需进行合成
        if (temFileList.size() == 1) {
          targetFile = temFileList.get(0);
        } else {
          targetFile = tempFile1;
          if (targetFile.exists()) {
            FileUtils.delete(targetFile);
          }
          VideoComposer composer = new VideoComposer(temFileList, targetFile.getPath());
          composer.joinVideo();
        }

        File resultFile = VideoRecordHelper.copyToTarget(targetFile);
        e.onNext(resultFile);
      }
    }).subscribeOn(GVideoSchedulers.IO_PRIORITY_USER);
  }

  /**
   * 获取临时文件地址
   *
   * @return 临时文件地址
   */
  private File getTempPath(@NonNull ImageVideoEntity entity) {
    return new File(VideoRecordHelper.getRecordCacheBaseDir(), File.separator + VIDEO_PREFIX + "temp_" + entity.id + "_" + System.currentTimeMillis() + VIDEO_SUFFIX);
  }

  /**
   * 更新选择视频最大时长
   *
   * @param time  最大时长
   */
  public static void updateSelectVideoMaxTime(int time) {
    if (time <= 0) {
      return;
    }
    RecordSharedPrefs.getInstance().putInt(KEY_SELECT_VIDEO_MAX_TIME, time);
  }
}
