package com.jxntv.home.splash.request;

import com.google.gson.JsonElement;
import com.jxntv.home.splash.api.SplashApi;
import com.jxntv.home.splash.db.entitiy.SplashAdEntity;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.network.request.BaseGVideoRequest;
import com.jxntv.utils.DeviceId;

import java.util.List;

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
