package com.jxntv.repository;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.network.engine.INetworkEngine;
import com.jxntv.network.engine.RealNetworkEngine;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.request.PushRequest;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;

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
