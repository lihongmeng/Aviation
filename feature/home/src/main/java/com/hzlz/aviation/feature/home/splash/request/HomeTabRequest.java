package com.hzlz.aviation.feature.home.splash.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.home.model.HomeTabInfo;
import com.hzlz.aviation.feature.home.splash.api.SplashApi;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

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
