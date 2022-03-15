package com.hzlz.aviation.feature.community.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

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
