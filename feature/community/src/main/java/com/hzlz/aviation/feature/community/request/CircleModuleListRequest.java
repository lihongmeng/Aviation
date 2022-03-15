package com.hzlz.aviation.feature.community.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.base.model.circle.CircleModule;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CircleModuleListRequest extends BaseGVideoRequest<List<CircleModule>> {

    private long groupId;

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().circleModuleList(groupId);
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
