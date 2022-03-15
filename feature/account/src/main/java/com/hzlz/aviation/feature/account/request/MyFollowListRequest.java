package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.UgcAuthorModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class MyFollowListRequest extends BaseGVideoPageMapRequest<UgcAuthorModel> {

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

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().myFollowList(mParameters);
    }

    @Override
    protected TypeToken<List<UgcAuthorModel>> getResponseTypeToken() {
        return new TypeToken<List<UgcAuthorModel>>() {
        };
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }

}
