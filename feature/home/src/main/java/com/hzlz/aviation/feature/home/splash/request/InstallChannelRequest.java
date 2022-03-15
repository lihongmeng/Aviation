package com.hzlz.aviation.feature.home.splash.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.home.splash.api.SplashApi;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;
import com.hzlz.aviation.library.util.DeviceId;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 *  渠道信息
 */
public class InstallChannelRequest extends BaseGVideoMapRequest<String> {

    public void setChannelId(String channelId){
        mParameters.put("channelId",channelId);
        mParameters.put("deviceId", DeviceId.get());
    }

    public int getRetryTimes() {
        return 3;
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return SplashApi.Instance.get().uploadInstallChannel(mParameters);
    }
}
