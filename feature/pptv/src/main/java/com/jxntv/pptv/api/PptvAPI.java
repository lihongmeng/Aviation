package com.jxntv.pptv.api;

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
 * PPTV相关 API
 *
 */
public interface PptvAPI {
  final class Instance {
    @NonNull
    private static volatile PptvAPI sInstance;

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

    private static PptvAPI createAPI() {
      return NetworkManager.getInstance().getRetrofit().create(PptvAPI.class);
    }

    @NonNull
    public static PptvAPI get() {
      return sInstance;
    }
  }

  /**
   * 获取pptv频道页信息
   */
  @GET("api/media/pptv")
  Observable<Response<JsonElement>> getChannelList();

  /**
   * 获取pptv分类页信息
   */
  @GET("api/media/pptv/category")
  Observable<Response<JsonElement>> getCategoryList(@QueryMap Map<String, Object> parameters);

  /**
   * 获取pptv频道页信息
   */
  @GET("api/media/pptv/filter")
  Observable<Response<JsonElement>> getFilterList(@QueryMap Map<String, Object> parameters);
}
