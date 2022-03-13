package com.jxntv.account.model;

import androidx.annotation.NonNull;

/**
 * 国家模型接口
 *
 *
 * @since 2020-01-14 16:21
 */
public interface ICountry {
  /**
   * 获取国家名称
   *
   * @return 国家名称
   */
  @NonNull
  String getCountryName();

  /**
   * 获取国家代码
   *
   * @return 国家代码
   */
  @NonNull
  String getDisplayCountryCode();

  /**
   * 获取展示的名称
   *
   * @return 展示的名称
   */
  @NonNull
  String getDisplayName();
}
