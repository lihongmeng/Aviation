package com.hzlz.aviation.kernel.push.repository;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.kernel.network.engine.INetworkEngine;
import com.hzlz.aviation.kernel.network.engine.RealNetworkEngine;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.push.request.PushRequest;

import io.reactivex.rxjava3.core.Observable;

public class PushRepository{

    protected INetworkEngine mEngine = new RealNetworkEngine();

    /**
     * 上报注册token
     *
     */
    @NonNull
    public Observable<JsonElement> uploadPushToken(@NonNull String token, @NonNull int type) {
        return new NetworkData<JsonElement>(mEngine) {

            @Override
            protected BaseRequest<JsonElement> createRequest() {
                PushRequest request = new PushRequest();
                request.setToken(token);
                request.setPushType(type);
                return request;
            }

            @Override
            protected void saveData(JsonElement o) {

            }
        }.asObservable();
    }

}
