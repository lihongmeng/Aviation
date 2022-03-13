package com.jxntv.search.api;

import androidx.annotation.NonNull;
import com.google.gson.JsonElement;
import com.jxntv.network.BaseUrlChangedCallback;
import com.jxntv.network.NetworkManager;
import io.reactivex.rxjava3.core.Observable;
import java.util.Map;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 搜索api接口
 */
public interface SearchApi {
  final class Instance {
    @NonNull
    private static volatile SearchApi sInstance;

    static {
      // 监听 BaseUrl 改变
      NetworkManager.getInstance().addBaseUrlChangedCallback(new BaseUrlChangedCallback() {
        @Override
        public void onBaseUrlChanged() {
          sInstance = createAPI();
        }
      });
      sInstance = createAPI();
    }

    private static SearchApi createAPI() {
      return NetworkManager.getInstance().getRetrofit().create(SearchApi.class);
    }

    @NonNull
    public static SearchApi get() {
      return sInstance;
    }
  }

  /**
   * 根据用户搜索条件分类搜索
   *
   //* @param query 搜索query
   //* @param category 搜索类型
   //* @param cursor 搜索游标，上次搜索到位置
   * @return 搜索结果
   */
  @GET("api/search/multiple")
  Observable<Response<JsonElement>> searchMedia(
      @QueryMap Map<String, Object> parameters);
}
