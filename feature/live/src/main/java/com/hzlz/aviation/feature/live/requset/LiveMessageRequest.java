package com.hzlz.aviation.feature.live.requset;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.live.api.LiveApi;
import com.hzlz.aviation.feature.live.model.LiveDetailModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc :
 **/
public class LiveMessageRequest extends BaseGVideoRequest<LiveDetailModel> {

    /**
     * 获取重试次数
     *
     * @return 重试次数
     */
    public int getRetryTimes() {
        return 3;
    }

    private String mediaId;

    public void setId(String mediaId){
        this.mediaId = mediaId;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return LiveApi.Instance.get().getLiveMessage(mediaId);
    }

}
