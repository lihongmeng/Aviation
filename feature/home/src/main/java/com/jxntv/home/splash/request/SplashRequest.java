package com.jxntv.home.splash.request;

import com.google.gson.JsonElement;
import com.jxntv.home.splash.api.SplashApi;
import com.jxntv.home.splash.db.entitiy.SplashAdEntity;
import com.jxntv.network.request.BaseGVideoRequest;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
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
