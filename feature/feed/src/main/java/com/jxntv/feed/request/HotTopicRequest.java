package com.jxntv.feed.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.base.model.circle.TopicDetail;
import com.jxntv.feed.api.FeedApi;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class HotTopicRequest extends BaseGVideoPageMapRequest<TopicDetail> {

    /**
     * 设置分页编号
     *
     * @param pageNumber 分页编号
     */
    public void setPageNumber(int pageNumber) {
        mParameters.put("pageNum", pageNumber);
    }

    /**
     * 设置每页大小
     *
     * @param pageSize 每页大小
     */
    public void setPageSize(int pageSize) {
        mParameters.put("pageSize", pageSize);
    }


    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        if (mParameters.size() != 2) {
            throw new RuntimeException("invalid parameters for get moment list request !!!");
        }
        return getRetrofit().create(FeedApi.class).hotTopic(mParameters);
    }


    @Override
    protected TypeToken<List<TopicDetail>> getResponseTypeToken() {
        return new TypeToken<List<TopicDetail>>() {
        };
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }

}
