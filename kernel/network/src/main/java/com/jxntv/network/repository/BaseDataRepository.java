package com.jxntv.network.repository;

import com.jxntv.network.engine.INetworkEngine;
import com.jxntv.network.engine.RealNetworkEngine;
import com.jxntv.network.request.BaseRequest;

/**
 * 仓库基类，用于统一管理一类 API, 比如用户的所有相关 API
 *
 *
 * @since 2020-01-05 13:19
 */
public abstract class BaseDataRepository {
  //<editor-fold desc="属性">
  protected INetworkEngine mEngine;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public BaseDataRepository() {
    mEngine = new RealNetworkEngine();
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 取消请求
   *
   * @param request 请求 {@link BaseRequest} 实例
   */
  public void cancelRequest(BaseRequest request) {
    mEngine.cancelRequest(request);
  }

  /**
   * 取消所有请求
   */
  public void cancelAllRequest() {
    mEngine.cancelAllRequest();
  }
  //</editor-fold>
}
