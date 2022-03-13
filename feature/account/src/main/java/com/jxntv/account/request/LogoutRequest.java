package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.network.request.BaseGVideoRequest;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 登出请求
 *
 *
 * @since 2020-02-27 10:49
 */
public final class LogoutRequest extends BaseGVideoRequest<Object> {
  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().logout();
  }
  //</editor-fold>
}
