package com.jxntv.circle.request;


import com.google.gson.JsonElement;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.circle.model.FXAModel;
import com.jxntv.network.request.BaseGVideoRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class FXADataRequest extends BaseGVideoRequest<FXAModel> {

    private String id;

    public void setId(String id){
        this.id = id;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().getActivityData(id);
    }
}
