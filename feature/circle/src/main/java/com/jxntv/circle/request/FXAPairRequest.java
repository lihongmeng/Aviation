package com.jxntv.circle.request;


import com.google.gson.JsonElement;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class FXAPairRequest extends BaseGVideoMapRequest<Object> {

    public void setParameters(String memberId,String inputNumber){
        mParameters.put("memberId",memberId);
        mParameters.put("selectMemberCode",inputNumber);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().fxaPair(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 1;
    }
}
