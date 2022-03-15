package com.hzlz.aviation.kernel.network.repository;

import com.hzlz.aviation.kernel.network.engine.INetworkEngine;

/**
 * 网路数据，从网络获取数据并进行保存
 *
 * @param <T> 模型泛型
 *
 * @since 2020-01-03 21:09
 */
public abstract class NetworkData<T> extends BoundData<T> {

  //<editor-fold desc="构造函数">

  /**
   * @param engine 网络引擎 {@link INetworkEngine}
   */
  public NetworkData(INetworkEngine engine) {
    super(engine);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected T loadFromLocal() {
    return null;
  }

  @Override
  protected boolean shouldFetchUp(T t) {
    return true;
  }
  //</editor-fold>
}
