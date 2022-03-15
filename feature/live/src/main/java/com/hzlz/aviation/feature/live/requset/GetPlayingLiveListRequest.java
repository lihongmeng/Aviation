package com.hzlz.aviation.feature.live.requset;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.live.api.LiveApi;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
public class GetPlayingLiveListRequest extends BaseGVideoRequest<List<MediaModel>> {
    
    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return LiveApi.Instance.get().getPlayingLiveList();
    }

}
