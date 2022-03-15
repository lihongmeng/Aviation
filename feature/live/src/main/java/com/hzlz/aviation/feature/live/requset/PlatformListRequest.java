package com.hzlz.aviation.feature.live.requset;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.live.api.LiveApi;
import com.hzlz.aviation.feature.live.model.PlatformMessageModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

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
