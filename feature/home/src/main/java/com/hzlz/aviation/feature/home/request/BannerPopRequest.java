package com.hzlz.aviation.feature.home.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.home.api.HomeApi;
import com.hzlz.aviation.kernel.base.model.banner.BannerModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class BannerPopRequest extends BaseGVideoMapRequest<BannerModel> {

    /**
     * 场景
     * @param type  0信息流  1 详情页
     */
    public void setScene(int type) {
        mParameters.put("scene", type);
    }

    public void setLocationId(long locationId) {
        mParameters.put("locationId", locationId);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return getRetrofit().create(HomeApi.class).getBannerTopList(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

}