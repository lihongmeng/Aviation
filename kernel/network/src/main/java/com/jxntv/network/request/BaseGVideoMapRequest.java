package com.jxntv.network.request;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求基类，内部有一个 Map 对象，用于存储请求参数
 *
 *
 * @since 2020-02-26 15:14
 */
public abstract class BaseGVideoMapRequest<T> extends BaseGVideoRequest<T> {
  //<editor-fold desc="属性">
  /*@NotNull*/
  protected Map<String, Object> mParameters;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public BaseGVideoMapRequest() {
    mParameters = new HashMap<>(getMaxParameterCount());
  }
  //</editor-fold>

  //<editor-fold desc="抽象方法">
  protected abstract int getMaxParameterCount();
  //</editor-fold>
}
