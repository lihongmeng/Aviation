package com.jxntv.home.splash.api;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.home.splash.request.SplashRequest;
import com.jxntv.network.BaseUrlChangedCallback;
import com.jxntv.network.NetworkManager;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * 闪屏spi
 */
public interface SplashApi {


    final class Instance {
        @NonNull
        private static volatile SplashApi sInstance;

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

        private static SplashApi createAPI() {
            return NetworkManager.getInstance().getRetrofit().create(SplashApi.class);
        }

        @NonNull
        public static SplashApi get() {
            return sInstance;
        }
    }


    /**
     * 获取开屏广告信息
     */
    @GET("api/init/advertise/open")
    Observable<Response<JsonElement>> loadOpenAds();


    /**
     * 获取开屏广告信息
     */
    @POST("api/channel")
    Observable<Response<JsonElement>> uploadInstallChannel(@Body Map<String, Object> parameters);


    /**
     * 是否开启底部菜单(临时)
     *
     */
    @GET("api/init/tabs/switch")
    Observable<Response<JsonElement>> homeTabSwitch();


    /**
     * 上报游客id
     *
     */
    @GET("api/user/tourist")
    Observable<Response<JsonElement>> tourist(@Query("distinctId") String distinctId);

}

