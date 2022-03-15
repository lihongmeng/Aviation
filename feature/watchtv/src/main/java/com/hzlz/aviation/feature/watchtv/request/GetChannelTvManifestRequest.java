package com.hzlz.aviation.feature.watchtv.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.watchtv.api.WatchTvAPI;
import com.hzlz.aviation.feature.watchtv.entity.ChannelTvManifest;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetChannelTvManifestRequest extends BaseGVideoMapRequest<List<ChannelTvManifest>> {

    private long channelId;

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    // yyyy-MM-dd
    public void setPlayDate(String playDate) {
        mParameters.put("playDate", playDate);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return WatchTvAPI.Instance.get().getChannelTvManifest(channelId, mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }
}
