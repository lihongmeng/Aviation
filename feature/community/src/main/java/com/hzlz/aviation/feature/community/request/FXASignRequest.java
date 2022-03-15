package com.hzlz.aviation.feature.community.request;


import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class FXASignRequest extends BaseGVideoMapRequest<Object> {

    public void setId(String memberId){
        mParameters.put("memberId",memberId);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().fxaSign(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 1;
    }
}
