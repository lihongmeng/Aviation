package com.jxntv.watchtv.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.network.request.BaseGVideoPageMapRequest;
import com.jxntv.watchtv.api.WatchTvAPI;
import com.jxntv.base.model.video.WatchTvChannel;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetChannelColumnListRequest extends BaseGVideoPageMapRequest<WatchTvChannel> {

    private long channelId;

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public void setPageNumber(int pageNumber) {
        mParameters.put("pageNum", pageNumber);
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
        return WatchTvAPI.Instance.get().getChannelColumnList(channelId,mParameters);
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
