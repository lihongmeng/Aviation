package com.hzlz.aviation.feature.community.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

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
