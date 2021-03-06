package com.hzlz.aviation.feature.video.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.video.api.VideoAPI;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class LoadSeriesRequest extends BaseGVideoPageMapRequest<VideoModel> {
  private String columnId;
  public void setColumnId(String columnId) {
    this.columnId = columnId;
  }

  public void setPageNumber(int pageNumber) {
    mParameters.put("pageNum", pageNumber);
  }

  public void setPageSize(int pageSize) {
    mParameters.put("pageSize", pageSize);
  }

  @Override protected TypeToken<List<VideoModel>> getResponseTypeToken() {
    return new TypeToken<List<VideoModel>>(){};
  }

  @Override protected int getMaxParameterCount() {
    return 2;
  }

  @Override protected Observable<Response<JsonElement>> getResponseObservable() {
    return VideoAPI.Instance.get().loadSeries(columnId, mParameters);
  }

  @Override
  protected JsonElement furtherProcessData(JsonElement data) {
    return getMemberJsonElement(data, "list");
  }
}
