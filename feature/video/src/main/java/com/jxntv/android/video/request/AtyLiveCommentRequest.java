package com.jxntv.android.video.request;

import com.google.gson.JsonElement;
import com.jxntv.android.video.api.VideoAPI;
import com.jxntv.android.video.model.LiveCommentModel;
import com.jxntv.android.video.model.LiveDetailModel;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.network.request.BaseGVideoRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc :
 */
public class AtyLiveCommentRequest extends BaseGVideoRequest<LiveCommentModel> {

    private String mediaId;
    private String fetchTime;

    public void setParameters(String mediaId,String requestCommentTime) {
        this.mediaId = mediaId;
        this.fetchTime = requestCommentTime;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {

        return VideoAPI.Instance.get().loadAtyLiveComment(mediaId,fetchTime);
    }

}
