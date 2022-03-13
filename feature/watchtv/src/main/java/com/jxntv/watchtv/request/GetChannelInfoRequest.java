package com.jxntv.watchtv.request;

import com.google.gson.JsonElement;
import com.jxntv.network.request.BaseGVideoRequest;
import com.jxntv.watchtv.api.WatchTvAPI;
import com.jxntv.watchtv.entity.WatchTvChannelDetail;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetChannelInfoRequest extends BaseGVideoRequest<WatchTvChannelDetail> {

    private long channelId;

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return WatchTvAPI.Instance.get().getChannelInfo(channelId);
    }
}
