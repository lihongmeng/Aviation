package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 授权访问，同意协议
 *
 */
public final class AuthVisitRequest extends BaseGVideoMapRequest<Object> {
  private List<Integer> mProtocols = new ArrayList<>();
  public void authProtocol(int protocol) {
    mProtocols.add(protocol);
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    mParameters.put("protocolType", mProtocols.toArray(new Integer[0]));
    mParameters.put("appVersion", GVideoRuntime.getVersionName());
    return AccountAPI.Instance.get().authVisit(mParameters);
  }

  @Override protected int getMaxParameterCount() {
    return 2;
  }
}

