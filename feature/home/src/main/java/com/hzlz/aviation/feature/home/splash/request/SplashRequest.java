package com.hzlz.aviation.feature.home.splash.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.home.splash.api.SplashApi;
import com.hzlz.aviation.feature.home.splash.db.entitiy.SplashAdEntity;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 闪屏request
 */
public class SplashRequest extends BaseGVideoRequest<List<SplashAdEntity>> {

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return SplashApi.Instance.get().loadOpenAds();
    }
}
