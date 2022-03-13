package com.jxntv.circle.request;

import com.google.gson.JsonElement;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class ApplyJoinCircleRequest extends BaseGVideoMapRequest<Object> {

    public void setGroupId(long groupId) {
        mParameters.put("groupId", groupId);
    }

    public void setReason(String reason) {
        mParameters.put("reason", reason);
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().applyJoinCircle(mParameters);
    }

}
