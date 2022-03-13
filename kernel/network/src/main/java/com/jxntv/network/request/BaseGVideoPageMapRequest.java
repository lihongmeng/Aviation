package com.jxntv.network.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.network.response.ListWithPage;
import java.util.List;

/**
 * 有分页的请求基类，内部有一个 Map 对象，用于存储请求参数
 *
 *
 * @since 2020-03-02 08:18
 */
public abstract class BaseGVideoPageMapRequest<T> extends BaseGVideoMapRequest<ListWithPage<T>> {

  //<editor-fold desc="抽象方法">
//  @Override
//  protected abstract TypeToken<List<T>> getResponseTypeToken();
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected boolean isPageRequest() {
    return true;
  }
  //</editor-fold>

  @Override
  protected JsonElement furtherProcessData(JsonElement data) {
    return getMemberJsonElement(data, "list");
  }

}
