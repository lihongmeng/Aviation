package com.jxntv.pptv.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.network.request.BaseGVideoPageMapRequest;
import com.jxntv.pptv.api.PptvAPI;
import com.jxntv.pptv.model.Media;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import retrofit2.Response;

/**
 * 筛选pptv请求
 *
 */
public final class GetFilterListRequest extends BaseGVideoPageMapRequest<Media> {

  /**
   * 设置查询条件
   * @param queryKey
   * @param queryValue
   */
  public void setQuery(String queryKey, String queryValue) {
    mParameters.put(queryKey, queryValue);
  }

  /**
   * 设置分页编号
   *
   * @param pageNumber 分页编号
   */
  public void setPageNumber(int pageNumber) {
    mParameters.put("pageNum", pageNumber);
  }

  /**
   * 设置分页大小
   *
   * @param pageSize 分页大小
   */
  public void setPageSize(int pageSize) {
    mParameters.put("pageSize", pageSize);
  }


  @Override
  protected int getMaxParameterCount() {
    return 6;
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return PptvAPI.Instance.get().getFilterList(mParameters);
  }

  @Override protected TypeToken<List<Media>> getResponseTypeToken() {
    return new TypeToken<List<Media>>() {
    };
  }

  @Override
  protected JsonElement furtherProcessData(JsonElement data) {
    return getMemberJsonElement(data, "list");
  }
}
