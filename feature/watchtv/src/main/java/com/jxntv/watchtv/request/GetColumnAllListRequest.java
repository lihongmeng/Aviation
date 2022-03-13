package com.jxntv.watchtv.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.media.model.MediaModel;
import com.jxntv.network.request.BaseGVideoPageMapRequest;
import com.jxntv.watchtv.api.WatchTvAPI;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetColumnAllListRequest extends BaseGVideoPageMapRequest<VideoModel> {

    public void setColumnId(String columnId) {
        mParameters.put("columnId", columnId);
    }

    public void setProgramId(String programId) {
        mParameters.put("programId", programId);
    }

    public void setPageNumber(int pageNumber) {
        mParameters.put("pageNum", pageNumber);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return WatchTvAPI.Instance.get().getColumnAllList(mParameters);
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
