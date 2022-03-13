package com.jxntv.live.requset;

import com.google.gson.JsonElement;
import com.jxntv.live.api.LiveApi;
import com.jxntv.live.model.OpenLiveResultModel;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc : Im信息
 **/
public class OpenLiveRequest extends BaseGVideoMapRequest<OpenLiveResultModel> {

    /**
     * 开启直播
     *
     * @param platformId  入驻号id
     * @param roomName    房间名称
     * @param thumbId     图片id
     * @param description 描述
     */
    public void setParameters(int platformId, String roomName, String thumbId, String description, int liveType) {
        mParameters.put("certificationId", platformId);
        mParameters.put("description", description);
        mParameters.put("thumb", thumbId);
        mParameters.put("title", roomName);
        mParameters.put("type", liveType);
    }

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
        return LiveApi.Instance.get().startLive(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 4;
    }

}
