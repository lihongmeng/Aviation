package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.RegionModel;
import com.jxntv.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 地区信息
 *
 *
 * @since 2020-02-27 10:49
 */
public final class RegionRequest extends BaseGVideoRequest<List<RegionModel>> {
  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().getRegionMessage();
  }
  //</editor-fold>
}
