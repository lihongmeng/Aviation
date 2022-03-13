package com.jxntv.circle.request;

import com.google.gson.JsonElement;
import com.jxntv.base.model.circle.MyCircle;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class MyCircleRequest extends BaseGVideoRequest<MyCircle> {

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().myCircle();
    }

}
