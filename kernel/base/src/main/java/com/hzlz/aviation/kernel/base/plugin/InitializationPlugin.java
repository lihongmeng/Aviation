package com.hzlz.aviation.kernel.base.plugin;

import androidx.annotation.NonNull;

import com.hzlz.aviation.library.ioc.Plugin;

/**
 * 初始化接口
 *
 *
 * @since 2020-03-07 21:23
 */
public interface InitializationPlugin extends Plugin {
  /**
   * 获取初始化仓库
   *
   * @return 初始化仓库
   */
  @NonNull
  IInitializationPRepository getInitializationPRepository();

  /**
   * 获取授权仓库
   */
  @NonNull
  IAuthRepository getAuthRepository();
}
