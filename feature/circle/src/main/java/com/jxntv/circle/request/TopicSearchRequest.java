package com.jxntv.circle.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.base.model.circle.TopicDetail;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class TopicSearchRequest extends BaseGVideoPageMapRequest<TopicDetail> {

    public void setGroupId(long groupId) {
        mParameters.put("groupId", groupId);
    }

    public void setKey(String key) {
        mParameters.put("key", key);
    }

    public void setPageNumber(int pageNumber) {
        mParameters.put("pageNum", pageNumber);
    }

    public void setPageSize(int pageSize) {
        mParameters.put("pageSize", pageSize);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().topicSearch(mParameters);
    }

    @Override
    protected TypeToken<List<TopicDetail>> getResponseTypeToken() {
        return new TypeToken<List<TopicDetail>>() {
        };
    }

    @Override
    protected int getMaxParameterCount() {
        return 4;
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }

}
