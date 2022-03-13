package com.jxntv.circle.request;

import com.google.gson.JsonElement;
import com.jxntv.base.model.circle.CircleModule;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoRequest;

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
