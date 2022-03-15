package com.hzlz.aviation.feature.community.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.base.model.circle.Circle;
import com.hzlz.aviation.kernel.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetRecommendCommunityRequest extends BaseGVideoPageMapRequest<Circle> {

    public void setPageNumber(int pageNumber) {
        mParameters.put("pageNum", pageNumber);
    }

    public void setPageSize(int pageSize) {
        mParameters.put("pageSize", pageSize);
    }

    public void setFirstGroupId(long firstGroupId) {
        mParameters.put("firstGroupId", firstGroupId);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().getRecommendCommunity(mParameters);
    }


    @Override
    protected TypeToken<List<Circle>> getResponseTypeToken() {
        return new TypeToken<List<Circle>>() {
        };
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }

}
