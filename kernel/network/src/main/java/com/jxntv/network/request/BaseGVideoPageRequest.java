package com.jxntv.network.request;

import com.google.gson.reflect.TypeToken;
import com.jxntv.network.response.ListWithPage;
import java.util.List;

/**
 * 有分页的请求基类
 *
 *
 * @since 2020-03-02 08:17
 */
public abstract class BaseGVideoPageRequest<T> extends BaseGVideoRequest<ListWithPage<T>> {
  //<editor-fold desc="抽象方法">
  @Override
  protected abstract TypeToken<List<T>> getResponseTypeToken();
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected boolean isPageRequest() {
    return true;
  }
  //</editor-fold>
}
