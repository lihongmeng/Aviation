package com.jxntv.circle.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.media.model.MediaModel;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CircleModuleListContentRequest extends BaseGVideoPageMapRequest<MediaModel> {

    public void setGatherId(long gatherId) {
        mParameters.put("gatherId", gatherId);
    }

    public void setPageNumber(int pageNumber) {
        mParameters.put("pageNum", pageNumber);
    }

    public void setPageSize(int pageSize) {
        mParameters.put("pageSize", pageSize);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().circleModuleListContent(mParameters);
    }

    @Override
    protected TypeToken<List<MediaModel>> getResponseTypeToken() {
        return new TypeToken<List<MediaModel>>() {
        };
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }
}
