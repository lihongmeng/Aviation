package com.jxntv.record.recorder.api;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.network.BaseUrlChangedCallback;
import com.jxntv.network.NetworkManager;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * 录制相关请求API
 */
public interface RecordApi {

    /**
     * 持有单例
     */
    final class Instance {
        @NonNull
        private static volatile RecordApi sInstance;

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

        /**
         * 创建API实例
         */
        private static RecordApi createAPI() {
            return NetworkManager.getInstance().getRetrofit().create(RecordApi.class);
        }

        @NonNull
        public static RecordApi get() {
            return sInstance;
        }
    }

    /**
     * 音视频点播云获取上传地址和凭证
     */
    @POST("api/file/vod/video/create")
    Observable<Response<JsonElement>> vodVideoCreate(@Body Map<String, Object> parameters);

    // 发布
    @POST("/api/media/ugc")
    Observable<Response<JsonElement>> publish(@Body Map<String, Object> parameters);

    // 获取发布链接的白名单列表
    @GET("api/media/out-share/whitelist")
    Observable<Response<JsonElement>> getPublishLinkWhiteList();

}
