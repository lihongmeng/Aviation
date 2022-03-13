package com.jxntv.live.requset;

import androidx.lifecycle.ViewModel;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.live.api.LiveApi;
import com.jxntv.media.model.MediaModel;
import com.jxntv.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
public class GetPlayingLiveListRequest extends BaseGVideoRequest<List<MediaModel>> {
    
    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return LiveApi.Instance.get().getPlayingLiveList();
    }

}
