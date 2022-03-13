package com.jxntv.feed.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.feed.api.FeedApi;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class HotCircleRequest extends BaseGVideoPageMapRequest<Circle> {

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
        return getRetrofit().create(FeedApi.class).hotCircle(mParameters);
    }

    @Override
    protected TypeToken<List<Circle>> getResponseTypeToken() {
        return new TypeToken<List<Circle>>() {
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
