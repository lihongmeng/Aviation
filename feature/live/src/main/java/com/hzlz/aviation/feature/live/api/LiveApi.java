package com.hzlz.aviation.feature.live.api;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.kernel.network.BaseUrlChangedCallback;
import com.hzlz.aviation.kernel.network.NetworkManager;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc :
 **/
public interface LiveApi {

    final class Instance {
        @NonNull
        private static volatile LiveApi sInstance;

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

        private static LiveApi createAPI() {
            return NetworkManager.getInstance().getRetrofit().create(LiveApi.class);
        }

        @NonNull
        public static LiveApi get() {
            return sInstance;
        }
    }

    /**
     * 获取账号下入驻号
     */
    @GET("api/interact/broadcast/certification")
    Observable<Response<JsonElement>> getPlatformList();

    /**
     * 获取直播权限
     */
    @GET("api/interact/broadcast/right")
    Observable<Response<JsonElement>> hasLivePermission();

    /**
     * 开播
     */
    @POST("api/interact/broadcast")
    Observable<Response<JsonElement>> startLive(@Body Map<String, Object> parameters);

    /**
     * 直播配置信息
     */
    @GET("api/interact/broadcast/config")
    Observable<Response<JsonElement>> getIMConfig(@Query("jid") String userId);

    /**
     * 直播数据
     */
    @GET("api/interact/broadcast/info")
    Observable<Response<JsonElement>> getLiveMessage(@Query("mediaId") String mediaId);


    /**
     * 直播结束
     */
    @PUT("api/interact/broadcast/end")
    Observable<Response<JsonElement>> uploadLiveEndMessage(@Body HashMap<String, Object> parameters);


    /**
     * 检查是否有断开的直播
     */
    @GET("api/interact/broadcast/status/check")
    Observable<Response<JsonElement>> checkLive();

    // 是否连麦
    @POST("api/interact/broadcast/connect/video")
    Observable<Response<JsonElement>> startConnectMicro(@Body Map<String, Object> parameters);

    // 获取正在播放的直播
    @GET("api/media/list/live/playing")
    Observable<Response<JsonElement>> getPlayingLiveList();

    // 获取直播
    @GET("api/media/list/live/playback")
    Observable<Response<JsonElement>> getLiveReviewList(@QueryMap Map<String, Object> parameters);

    // 获取首页推荐内容列表
    @GET("api/ai/recommend/content")
    Observable<Response<JsonElement>> getHomeRecommendContentList(@QueryMap Map<String, Object> parameters);
}
