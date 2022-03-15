package com.hzlz.aviation.kernel.base.utils;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

/**
 * 文件下载回调
 *
 *
 * @since 2020-03-16 17:04
 */
public interface FileDownloadCallback {
  /**
   * 进度回调
   *
   * @param fileUrl 文件 url
   * @param progress 进度 [0,1]
   */
  void onProgress(@NonNull String fileUrl, @FloatRange(from = 0f, to = 1f) float progress);

  /**
   * 成功回调
   *
   * @param fileUrl 文件 url
   */
  void onSuccess(@NonNull String fileUrl);

  /**
   * 错误回调
   *
   * @param fileUrl 文件 url
   * @param e 异常
   */
  void onError(@NonNull String fileUrl, @NonNull Throwable e);

  /**
   * 取消回调
   *
   * @param fileUrl 文件 url
   */
  void onCancel(@NonNull String fileUrl);
}
