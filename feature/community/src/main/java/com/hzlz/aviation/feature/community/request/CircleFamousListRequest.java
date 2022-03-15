package com.hzlz.aviation.feature.community.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.base.model.circle.CircleFamous;
import com.hzlz.aviation.kernel.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CircleFamousListRequest extends BaseGVideoPageMapRequest<CircleFamous> {

    public void setGroupId(long groupId) {
        mParameters.put("groupId", groupId);
    }

    public void setPageNum(int pageNum) {
        mParameters.put("pageNum", pageNum);
    }

    public void setPageSize(int pageSize) {
        mParameters.put("pageSize", pageSize);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().circleFamousList(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }

    @Override
    protected TypeToken<List<CircleFamous>> getResponseTypeToken() {
        return new TypeToken<List<CircleFamous>>() {
        };
    }

}
