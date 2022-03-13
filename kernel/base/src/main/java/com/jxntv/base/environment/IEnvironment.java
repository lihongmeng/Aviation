package com.jxntv.base.environment;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @since 2020-03-04 11:45
 */
public interface IEnvironment {
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({
      Type.TEST_190,
      Type.TEST_47,
      Type.RELEASE,
      Type.OTHER,
  }) @interface Type {
    int TEST_190 = 0;
    int RELEASE = 1;
    int OTHER = 2;
    int TEST_47 = 3;
  }

  /**
   * 获取当前环境类型
   *
   * @return 当前环境类型 {@link Type}
   */
  @Type
  int getCurrentType();

  /**
   * 更新类型
   *
   * @param type 类型 {@link Type}
   * @param otherUrl 自定义url
   */
  void updateType(@Type int type, String otherUrl);

  /**
   * 获取当前 API 地址
   *
   * @return 当前 API 地址
   */
  @NonNull
  String getCurrentAPIUrl();
}
