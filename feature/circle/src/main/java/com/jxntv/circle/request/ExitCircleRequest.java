package com.jxntv.circle.request;

import com.google.gson.JsonElement;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class ExitCircleRequest extends BaseGVideoMapRequest<Object> {

    public void setGroupId(long groupId) {
        mParameters.put("groupId", groupId);
    }

    @Override
    protected int getMaxParameterCount() {
        return 1;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().exitCircle(mParameters);
    }

}
