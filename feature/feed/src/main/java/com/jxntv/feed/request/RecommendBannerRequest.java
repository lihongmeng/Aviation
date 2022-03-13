package com.jxntv.feed.request;

import com.google.gson.JsonElement;
import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.feed.api.FeedApi;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取banner数据
 */
public final class RecommendBannerRequest extends BaseGVideoMapRequest<BannerModel> {

    /**
     * 场景
     * @param type 0信息流  1 详情页
     */
    public void setScene(int type) {
        mParameters.put("scene", type);
    }

    public void setLocationId(int targetId) {
        mParameters.put("locationId", targetId);
    }


    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return getRetrofit().create(FeedApi.class).getBannerList(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }
}
