package com.hzlz.aviation.feature.watchtv.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.watchtv.api.WatchTvAPI;
import com.hzlz.aviation.feature.watchtv.entity.WatchTvChannelDetail;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

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
