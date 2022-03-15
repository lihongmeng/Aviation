package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

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
