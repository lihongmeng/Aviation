package com.jxntv.network.repository;

import com.jxntv.network.engine.INetworkEngine;
import com.jxntv.network.request.BaseRequest;

/**
 * 本地数据, 只从本地获取数据
 *
 * @param <T> 模型泛型
 *
 * @since 2020-01-03 21:07
 */
public abstract class LocalData<T> extends BoundData<T> {

  //<editor-fold desc="构造函数">

  /**
   * @param engine 网络引擎 {@link INetworkEngine}
   */
  public LocalData(INetworkEngine engine) {
    super(engine);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected boolean shouldFetchUp(T t) {
    return false;
  }

  @Override
  protected BaseRequest<T> createRequest() {
    return null;
  }

  @Override
  protected void saveData(T t) {

  }
  //</editor-fold>
}
