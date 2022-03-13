package com.jxntv.live.requset;

import androidx.lifecycle.ViewModel;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.live.api.LiveApi;
import com.jxntv.media.model.MediaModel;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetLiveReviewListRequest extends BaseGVideoPageMapRequest<MediaModel> {

    public void setPageNum(int pageNum) {
        mParameters.put("pageNum", pageNum);
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
        return LiveApi.Instance.get().getLiveReviewList(mParameters);
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
