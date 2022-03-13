package com.jxntv.network.engine;

import com.jxntv.network.request.BaseRequest;
import io.reactivex.rxjava3.core.Observable;

/**
 * 网络引擎接口
 *
 *
 * @since 2020-01-03 16:26
 */
public interface INetworkEngine {
  /**
   * 执行请求
   *
   * @param request 请求 {@link BaseRequest}
   * @param <T> 请求响应泛型
   * @return Observable {@link Observable} 对象
   */
  <T> Observable<T> executeRequest(BaseRequest<T> request);

  /**
   * 取消请求
   *
   * @param request 请求 {@link BaseRequest}
   */
  void cancelRequest(BaseRequest request);

  /**
   * 取消所有请求
   */
  void cancelAllRequest();
}
