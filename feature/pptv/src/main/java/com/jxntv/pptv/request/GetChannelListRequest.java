package com.jxntv.pptv.request;

import com.google.gson.JsonElement;
import com.jxntv.network.request.BaseGVideoRequest;
import com.jxntv.pptv.api.PptvAPI;
import com.jxntv.pptv.model.ChannelResponse;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取频道请求
 *
 */
public final class GetChannelListRequest extends BaseGVideoRequest<ChannelResponse> {
  @Override protected Observable<Response<JsonElement>> getResponseObservable() {
    return PptvAPI.Instance.get().getChannelList();
  }
}
