package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.AvatarInfo;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取头像列表请求
 *
 *
 * @since 2020-02-27 21:38
 */
public final class GetAvatarListRequest extends BaseGVideoRequest<ArrayList<AvatarInfo>> {

  //<editor-fold desc="方法实现">

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().getAvatarList();
  }
  //</editor-fold>
}
