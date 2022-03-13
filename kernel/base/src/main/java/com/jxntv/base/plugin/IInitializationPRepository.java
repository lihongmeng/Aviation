package com.jxntv.base.plugin;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Observable;

/**
 * 初始化接口
 *
 *
 * @since 2020-03-07 21:18
 */
public interface IInitializationPRepository {
  /**
   * 获取初始化配置
   */
  @NonNull
  Observable<Object> getInitializationConfigure();
}
