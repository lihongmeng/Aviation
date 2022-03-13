package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.HasUnreadMessageNotificationResponse;
import com.jxntv.network.request.BaseGVideoRequest;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 是否有新动态请求
 *
 *
 * @since 2020-03-12 16:31
 */
public final class HasNewMomentRequest
    extends BaseGVideoRequest<HasUnreadMessageNotificationResponse> {
  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().hasNewMoment();
  }
  //</editor-fold>
}
