package com.jxntv.request;

import com.google.gson.JsonElement;
import com.jxntv.api.PushApi;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.utils.DeviceId;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class PushRequest extends BaseGVideoMapRequest<JsonElement> {


    /**
     * 设置推送token
     * @param token  推送token
     */
    public void setToken(String token){
        mParameters.put("token",token);
        mParameters.put("deviceId", DeviceId.get());
    }

    /**
     * 设置推送类型
     * @param type   0 华为，1 oppo ，2 小米 ，3 vivo， 4 ios
     */
    public void setPushType(int type){
        mParameters.put("type",type);
    }


    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return PushApi.Instance.get().uploadToken(mParameters);
    }
}
