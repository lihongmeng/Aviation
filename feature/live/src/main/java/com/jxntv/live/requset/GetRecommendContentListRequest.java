package com.jxntv.live.requset;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.live.api.LiveApi;
import com.jxntv.media.model.MediaModel;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取动态列表请求
 *
 * @since 2020-03-10 19:25
 */
public final class GetRecommendContentListRequest extends BaseGVideoPageMapRequest<MediaModel> {
    //<editor-fold desc="设置参数">

    /**
     * 设置分页编号
     *
     * @param pageNumber 分页编号
     */
    public void setPageNumber(int pageNumber) {
        mParameters.put("pageNum", pageNumber);
    }

    /**
     * 设置每页大小
     *
     * @param pageSize 每页大小
     */
    public void setPageSize(int pageSize) {
        mParameters.put("pageSize", pageSize);
    }

    public void setNeedComment(boolean needComment) {
        mParameters.put("needComment", needComment);
    }

    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        if (mParameters.size() != 3) {
            throw new RuntimeException("invalid parameters for get moment list request !!!");
        }
        return getRetrofit().create(LiveApi.class).getHomeRecommendContentList(mParameters);
    }

    @Override
    protected TypeToken<List<MediaModel>> getResponseTypeToken() {
        return new TypeToken<List<MediaModel>>() {
        };
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }
    //</editor-fold>
}
