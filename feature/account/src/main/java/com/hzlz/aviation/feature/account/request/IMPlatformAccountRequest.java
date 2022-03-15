package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * im 聊天账号
 *
 */
public final class IMPlatformAccountRequest extends BaseGVideoRequest<List<String>> {
  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().getIMPlatformAccount();
  }

  @Override
  public int getRetryTimes() {
    return 3;
  }

  //</editor-fold>
}
