package com.hzlz.aviation.kernel.network;

import com.google.gson.JsonElement;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

import java.lang.reflect.Type;

/**
 * 网络日志接口
 *
 *
 * @since 2020-01-06 16:31
 */
public interface NetworkLogger {
  /**
   * 当请求重试的时候回调
   *
   * @param request 请求
   * @param throwable 异常
   */
  void retry(/*NotNull*/BaseRequest request,/*NotNull*/Throwable throwable);

  /**
   * 当请求进行数据转换的时候回调
   *
   * @param request 请求
   * @param jsonElement 请求成功(200)的 json 对象
   * @param responseType 请求响应模型类型
   */
  void convert(
      /*NotNull*/ BaseRequest request,
      /*Nullable*/ JsonElement jsonElement,
      /*Nullable*/ Type responseType
  );
}
