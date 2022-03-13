package com.jxntv.android.video.request;

import com.google.gson.JsonElement;
import com.jxntv.android.video.api.VideoAPI;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.network.request.BaseGVideoRequest;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import retrofit2.Response;

public class LoadRecommendRequest extends BaseGVideoRequest<List<RecommendModel>> {
  private String mediaId;
  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

  @Override protected Observable<Response<JsonElement>> getResponseObservable() {
    return VideoAPI.Instance.get().loadRecommend(mediaId);
  }
}
