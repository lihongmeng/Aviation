package com.jxntv.live.requset;

import com.google.gson.JsonElement;
import com.jxntv.live.api.LiveApi;
import com.jxntv.live.model.OpenLiveResultModel;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.network.request.BaseGVideoRequest;
import com.jxntv.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/3/11
 * desc : 检查是否有直播
 **/
public class CheckHasLiveRequest extends BaseGVideoRequest<OpenLiveResultModel> {



    /**
     * 获取重试次数
     *
     * @return 重试次数
     */
    public int getRetryTimes() {
        return 3;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return LiveApi.Instance.get().checkLive();
    }

}
