package com.jxntv.api;


import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.network.BaseUrlChangedCallback;
import com.jxntv.network.NetworkManager;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PushApi {

    final class Instance {
        @NonNull
        private static volatile PushApi sInstance;

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

        private static PushApi createAPI() {
            return NetworkManager.getInstance().getRetrofit().create(PushApi.class);
        }

        @NonNull
        public static PushApi get() {
            return sInstance;
        }
    }


    /**
     * 上报token
     */
    @POST("api/push/token")
    Observable<Response<JsonElement>> uploadToken(@Body Map<String, Object> parameters);

}
