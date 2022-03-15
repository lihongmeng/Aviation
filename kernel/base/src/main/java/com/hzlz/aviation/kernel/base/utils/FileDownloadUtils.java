package com.hzlz.aviation.kernel.base.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import com.hzlz.aviation.kernel.base.plugin.FilePlugin;
import com.hzlz.aviation.kernel.base.plugin.IFileCreator;
import com.hzlz.aviation.kernel.base.plugin.IFileRepository;
import com.hzlz.aviation.library.ioc.PluginManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 文件下载工具类
 *
 *
 * @since 2020-03-16 18:44
 */
public final class FileDownloadUtils {
  //<editor-fold desc="属性">
  @Nullable
  private static IFileRepository sFileRepository;
  @NonNull
  private final static Map<String, Set<FileDownloadCallback>> sDownloadCallbackMap =
      new HashMap<>();
  @NonNull
  private final static Set<String> sDownloadingFile = new HashSet<>();
  @NonNull
  private final static Map<String, Disposable> sDisposableMap = new HashMap<>();
  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private FileDownloadUtils() {
    throw new IllegalStateException("no instance !!!");
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 下载文件
   *
   * @param fileUrl 文件
   * @param creator 文件创建器
   * @param callback 文件下载回调
   */
  public synchronized static void downloadFile(
      @NonNull String fileUrl,
      @NonNull IFileCreator creator,
      @Nullable FileDownloadCallback callback) {
    downloadFile(fileUrl, 0, creator, callback);
  }

  /**
   * 下载文件
   *
   * @param fileUrl 文件
   * @param timeout 超时时间
   * @param creator 文件创建器
   * @param callback 文件下载回调
   */
  public synchronized static void downloadFile(
      @NonNull String fileUrl,
      @NonNull long timeout,
      @NonNull IFileCreator creator,
      @Nullable FileDownloadCallback callback) {
    // 创建 File Repository
    if (sFileRepository == null) {
      FilePlugin plugin = PluginManager.get(FilePlugin.class);
      if (plugin != null) {
        sFileRepository = plugin.getFileRepository();
      }
    }
    if (sFileRepository == null) {
      throw new NullPointerException("File Repository is null");
    }
    // 记录文件回调
    final String urlMd5 = md5(fileUrl);
    Set<FileDownloadCallback> downloadCallbackSet = sDownloadCallbackMap.get(urlMd5);
    if (downloadCallbackSet == null) {
      downloadCallbackSet = new HashSet<>();
      sDownloadCallbackMap.put(urlMd5, downloadCallbackSet);
    }
    downloadCallbackSet.add(callback);
    // 判断文件是否在下载
    if (sDownloadingFile.contains(urlMd5)) {
      return;
    }
    // 记录正在下载的文件
    sDownloadingFile.add(urlMd5);
    if (timeout > 0) {
      // 下载
      sFileRepository.downloadFile(fileUrl, creator)
          .timeout(timeout, TimeUnit.MILLISECONDS)
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new DownloadObserver(urlMd5, fileUrl));
    } else {
      // 下载
      sFileRepository.downloadFile(fileUrl, creator)
          .subscribe(new DownloadObserver(urlMd5, fileUrl));
    }
  }

  private static final class DownloadObserver implements Observer<Float> {
    //<editor-fold desc="属性">
    @NonNull
    private final String mUrlMd5;
    @NonNull
    private final String mFileUrl;
    //</editor-fold>

    //<editor-fold desc="构造函数">

    private DownloadObserver(@NonNull String urlMd5, @NonNull String fileUrl) {
      mUrlMd5 = urlMd5;
      mFileUrl = fileUrl;
    }

    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    public void onSubscribe(@NonNull Disposable d) {
      // 记录 Disposable, 便于取消
      sDisposableMap.put(mUrlMd5, d);
    }

    @Override
    public void onNext(@NonNull Float progress) {
      notifyProgress(mUrlMd5, mFileUrl, progress);
    }

    @Override
    public void onError(@NonNull Throwable e) {
      notifyError(mUrlMd5, mFileUrl, e);
    }

    @Override
    public void onComplete() {
      notifySuccess(mUrlMd5, mFileUrl);
    }
    //</editor-fold>
  }

  /**
   * 移除下载回调
   *
   * @param fileUrl 文件 url
   * @param callback 下载回调
   */
  public synchronized static void removeDownloadCallback(
      @NonNull String fileUrl,
      @NonNull FileDownloadCallback callback) {
    final String urlMd5 = md5(fileUrl);
    Set<FileDownloadCallback> callbackSet = sDownloadCallbackMap.get(urlMd5);
    if (callbackSet != null) {
      callbackSet.remove(callback);
    }
  }

  /**
   * 取消下载
   *
   * @param fileUrl 文件 url
   */
  @UiThread
  public synchronized static void cancelDownload(@NonNull String fileUrl) {
    final String urlMd5 = md5(fileUrl);
    notifyCancel(urlMd5, fileUrl);
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">
  @SuppressWarnings("CharsetObjectCanBeUsed")
  private static String md5(@NonNull String content) {
    try {
      byte[] contentBytes = content.getBytes("UTF-8");
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] digestBytes = md.digest(contentBytes);
      return new String(digestBytes, "UTF-8");
    } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
      e.printStackTrace();
      return content;
    }
  }

  /**
   * 通知成功
   *
   * @param urlMd5 url md5
   * @param fileUrl 文件 url
   */
  private static void notifySuccess(@NonNull String urlMd5, @NonNull String fileUrl) {
    // 移除回调
    Set<FileDownloadCallback> callbackSet = sDownloadCallbackMap.remove(urlMd5);
    if (callbackSet != null) {
      for (FileDownloadCallback callback : callbackSet) {
        callback.onSuccess(fileUrl);
      }
    }
    // 移除正在下载
    sDownloadingFile.remove(urlMd5);
    // 移除取消
    sDisposableMap.remove(urlMd5);
  }

  /**
   * 通知进度
   *
   * @param urlMd5 url md5
   * @param fileUrl 文件 url
   * @param progress 进度
   */
  private static void notifyProgress(
      @NonNull String urlMd5,
      @NonNull String fileUrl,
      float progress) {
    // 回调
    Set<FileDownloadCallback> callbackSet = sDownloadCallbackMap.get(urlMd5);
    if (callbackSet != null) {
      for (FileDownloadCallback callback : callbackSet) {
        callback.onProgress(fileUrl, progress);
      }
    }
  }

  /**
   * 通知失败
   *
   * @param urlMd5 url md5
   * @param fileUrl 文件 url
   * @param e 异常
   */
  private static void notifyError(
      @NonNull String urlMd5,
      @NonNull String fileUrl,
      @NonNull Throwable e) {
    // 移除回调
    Set<FileDownloadCallback> callbackSet = sDownloadCallbackMap.remove(urlMd5);
    if (callbackSet != null) {
      for (FileDownloadCallback callback : callbackSet) {
        callback.onError(fileUrl, e);
      }
    }
    // 移除正在下载
    sDownloadingFile.remove(urlMd5);
    // 移除取消
    sDisposableMap.remove(urlMd5);
  }

  /**
   * 通知取消
   *
   * @param urlMd5 url md5
   * @param fileUrl 文件 url
   */
  private static void notifyCancel(@NonNull String urlMd5, @NonNull String fileUrl) {
    // 取消下载
    Disposable disposable = sDisposableMap.remove(urlMd5);
    if (disposable != null) {
      disposable.dispose();
    }
    // 移除回调
    Set<FileDownloadCallback> callbackSet = sDownloadCallbackMap.remove(urlMd5);
    if (callbackSet != null) {
      for (FileDownloadCallback callback : callbackSet) {
        callback.onCancel(fileUrl);
      }
    }
    // 移除正在下载
    sDownloadingFile.remove(urlMd5);
  }
  //</editor-fold>
}
