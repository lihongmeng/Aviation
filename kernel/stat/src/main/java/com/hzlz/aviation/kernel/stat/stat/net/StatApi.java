package com.hzlz.aviation.kernel.stat.stat.net;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.kernel.network.BaseUrlChangedCallback;
import com.hzlz.aviation.kernel.network.NetworkManager;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Tag;

/**
 * 埋点API
 */
public interface StatApi {
  final class Instance {
    @NonNull
    private static volatile StatApi sInstance;

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

    private static StatApi createAPI() {
      return NetworkManager.getInstance().getRetrofit().create(StatApi.class);
    }

    @NonNull
    public static StatApi get() {
      return sInstance;
    }
  }

  /**
   * 实时上传埋点
   */
  @POST("api/logging/realtime")
  Observable<Response<JsonElement>> uploadStatRealtime(
      @Tag String contentEncoding, @Body Map<String, Object> parameters);

  /**
   * 批量上传埋点
   */
  @POST("api/logging/non-realtime")
  Observable<Response<JsonElement>> uploadStat(
      @Tag String contentEncoding, @Body Map<String, Object> parameters);
}
