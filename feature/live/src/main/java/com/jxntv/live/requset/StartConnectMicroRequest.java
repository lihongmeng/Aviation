package com.jxntv.live.requset;

import static com.jxntv.base.Constant.CONNECT_VIDEO;
import static com.jxntv.base.Constant.MEDIA_ID;

import com.google.gson.JsonElement;
import com.jxntv.live.api.LiveApi;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class StartConnectMicroRequest extends BaseGVideoMapRequest<Object> {

    public void setConnectVideo(long connectVideo) {
        mParameters.put(CONNECT_VIDEO, connectVideo);
    }

    public void setMediaId(String mediaId) {
        mParameters.put(MEDIA_ID, mediaId);
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return LiveApi.Instance.get().startConnectMicro(mParameters);
    }
}
