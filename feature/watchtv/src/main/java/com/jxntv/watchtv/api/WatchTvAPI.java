package com.jxntv.watchtv.api;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.network.BaseUrlChangedCallback;
import com.jxntv.network.NetworkManager;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface WatchTvAPI {

    final class Instance {

        @NonNull
        private static volatile WatchTvAPI sInstance;

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

        private static WatchTvAPI createAPI() {
            return NetworkManager.getInstance().getRetrofit().create(WatchTvAPI.class);
        }

        @NonNull
        public static WatchTvAPI get() {
            return sInstance;
        }

    }

    // 获取频道分页列表
    @GET("api/tv/channel/page")
    Observable<Response<JsonElement>> getChannelList(@QueryMap Map<String, Object> parameters);

    // 获取频道信息
    @GET("api/tv/channel/{channelId}")
    Observable<Response<JsonElement>> getChannelInfo(
            @Path("channelId") long channelId
    );

    // 获取频道栏目列表
    @GET("api/tv/channel/{channelId}/column/page")
    Observable<Response<JsonElement>> getChannelColumnList(
            @Path("channelId") long channelId,
            @QueryMap Map<String, Object> parameters
    );

    // 获取频道播放节目清单
    @GET("api/tv/channel/{channelId}/menus")
    Observable<Response<JsonElement>> getChannelTvManifest(
            @Path("channelId") long channelId,
            @QueryMap Map<String, Object> parameters
    );

    // 获取热播节目分页列表
    @GET("api/tv/program/hot/page")
    Observable<Response<JsonElement>> getHotTvList(@QueryMap Map<String, Object> parameters);

    // 获取栏目整期分页列表
    @GET("api/tv/program/page")
    Observable<Response<JsonElement>> getColumnAllList(@QueryMap Map<String, Object> parameters);


    // 获取栏目详情
    @GET("api/tv/column/{columnId}")
    Observable<Response<JsonElement>> getColumnDetail(@Path("columnId") String columnId);

}
