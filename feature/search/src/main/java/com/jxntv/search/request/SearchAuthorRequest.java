package com.jxntv.search.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.network.request.BaseGVideoPageMapRequest;
import com.jxntv.search.api.SearchApi;
import com.jxntv.search.api.SearchApiConstants;
import com.jxntv.search.model.SearchAuthorModel;
import com.jxntv.search.model.SearchType;

import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import retrofit2.Response;

/**
 * 搜索结果请求
 */
public class SearchAuthorRequest extends BaseGVideoPageMapRequest<SearchAuthorModel> {
  public SearchAuthorRequest(String query) {
    mParameters.put(SearchApiConstants.SEARCH_QUERY, query);
    mParameters.put(SearchApiConstants.SEARCH_CATEGORY, SearchType.CATEGORY_AUTHORS);
  }

  public void setPageNumber(int pageNumber) {
    mParameters.put("pageNum", pageNumber);
  }

  public void setPageSize(int pageSize) {
    mParameters.put("pageSize", pageSize);
  }

  @Override protected TypeToken<List<SearchAuthorModel>> getResponseTypeToken() {
    return new TypeToken<List<SearchAuthorModel>>() {
    };
  }

  @Override protected int getMaxParameterCount() {
    return 4;
  }

  @Override protected Observable<Response<JsonElement>> getResponseObservable() {
    return SearchApi.Instance.get().searchMedia(mParameters);
  }
  @Override
  protected JsonElement furtherProcessData(JsonElement data) {
    return getMemberJsonElement(data, "list");
  }
}