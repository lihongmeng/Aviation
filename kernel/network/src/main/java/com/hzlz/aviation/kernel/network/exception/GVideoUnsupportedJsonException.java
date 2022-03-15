package com.hzlz.aviation.kernel.network.exception;

/**
 * 不支持 Json 格式异常，无法解析后台 json 格式
 *
 *
 * @since 2020-01-09 20:41
 */
public final class GVideoUnsupportedJsonException extends Exception {
  //<editor-fold desc="构造函数">
  public GVideoUnsupportedJsonException() {
  }

  public GVideoUnsupportedJsonException(String message) {
    super(message);
  }
  //</editor-fold>
}
