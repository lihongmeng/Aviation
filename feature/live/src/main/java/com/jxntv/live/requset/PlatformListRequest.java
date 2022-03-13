package com.jxntv.live.requset;

import com.google.gson.JsonElement;
import com.jxntv.live.api.LiveApi;
import com.jxntv.live.model.PlatformMessageModel;
import com.jxntv.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc :
 **/
public class PlatformListRequest extends BaseGVideoRequest<List<PlatformMessageModel>> {

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return LiveApi.Instance.get().getPlatformList();
    }

}
