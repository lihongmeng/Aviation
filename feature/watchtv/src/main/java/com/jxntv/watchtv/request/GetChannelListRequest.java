package com.jxntv.watchtv.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.network.request.BaseGVideoPageMapRequest;
import com.jxntv.watchtv.api.WatchTvAPI;
import com.jxntv.base.model.video.WatchTvChannel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetChannelListRequest extends BaseGVideoPageMapRequest<WatchTvChannel> {

    public void setPageNumber(int pageNumber) {
        mParameters.put("pageNum", pageNumber);
    }

    public void setType(String type) {
        mParameters.put("type", type);
    }

    public void setPageSize(int pageSize) {
        mParameters.put("pageSize", pageSize);
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return WatchTvAPI.Instance.get().getChannelList(mParameters);
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }

    @Override
    protected TypeToken<List<WatchTvChannel>> getResponseTypeToken() {
        return new TypeToken<List<WatchTvChannel>>() {
        };
    }

}
