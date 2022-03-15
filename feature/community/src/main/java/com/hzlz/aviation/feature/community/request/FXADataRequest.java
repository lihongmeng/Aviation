package com.hzlz.aviation.feature.community.request;


import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.feature.community.model.FXAModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

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
