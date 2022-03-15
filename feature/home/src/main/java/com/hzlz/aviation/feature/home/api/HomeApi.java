package com.hzlz.aviation.feature.home.api;

import com.google.gson.JsonElement;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface HomeApi {

    // 获取首页的Banner
    @GET("api/banners/pop")
    Observable<Response<JsonElement>> getBannerTopList(@QueryMap Map<String, Object> parameters);

}
