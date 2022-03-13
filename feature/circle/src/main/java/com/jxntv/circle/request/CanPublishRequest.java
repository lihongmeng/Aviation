package com.jxntv.circle.request;

import com.google.gson.JsonElement;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CanPublishRequest extends BaseGVideoMapRequest<Object> {

    public void setGroupId(long groupId) {
        mParameters.put("groupId", groupId);
    }

    public void setTopicId(long topicId) {
        mParameters.put("topicId", topicId);
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().canPublish(mParameters);
    }
}
