package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.User;
import com.jxntv.network.request.BaseGVideoRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取当前用户
 *
 *
 * @since 2020-03-06 20:10
 */
public final class GetCurrentUserRequest extends BaseGVideoRequest<User> {
  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().getCurrentUser();
  }
  //</editor-fold>
}
