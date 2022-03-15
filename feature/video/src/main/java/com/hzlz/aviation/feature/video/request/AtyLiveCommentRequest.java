package com.hzlz.aviation.feature.video.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.video.api.VideoAPI;
import com.hzlz.aviation.feature.video.model.LiveCommentModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

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
