package com.hzlz.aviation.feature.live.requset;

import static com.hzlz.aviation.kernel.base.Constant.CONNECT_VIDEO;
import static com.hzlz.aviation.kernel.base.Constant.MEDIA_ID;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.live.api.LiveApi;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

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
