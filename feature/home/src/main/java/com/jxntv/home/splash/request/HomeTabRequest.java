package com.jxntv.home.splash.request;

import com.google.gson.JsonElement;
import com.jxntv.home.model.HomeTabInfo;
import com.jxntv.home.splash.api.SplashApi;
import com.jxntv.network.request.BaseGVideoRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/12/29
 * desc :
 **/
public class HomeTabRequest extends BaseGVideoRequest<HomeTabInfo> {
    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return SplashApi.Instance.get().homeTabSwitch();
    }
}
