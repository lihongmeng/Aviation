package com.jxntv.android.video.request;

import com.google.gson.JsonElement;
import com.jxntv.android.video.api.VideoAPI;
import com.jxntv.android.video.model.LiveDetailModel;
import com.jxntv.network.request.BaseGVideoRequest;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc :
 */
public class AtyLiveDetailRequest extends BaseGVideoRequest<LiveDetailModel> {

    private String mediaId;

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return VideoAPI.Instance.get().loadAtyLiveDetail(mediaId);
    }

}
