package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.Media;
import com.jxntv.account.model.UgcMyCommentModel;
import com.jxntv.account.ui.ugc.detail.UgcContentType;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取收藏列表请求
 *
 * @since 2020-02-12 15:36
 */
public final class GetUgcMyCommentListRequest extends BaseGVideoPageMapRequest<UgcMyCommentModel> {

    private String userId;

    public void setParameters(String userId, int pageNum) {
        mParameters.put("userId", userId);
        mParameters.put("pageNum", pageNum);
        mParameters.put("pageSize", 20);
        this.userId = userId;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().getUgcCommentList(userId, mParameters);
    }

    @Override
    protected TypeToken<List<UgcMyCommentModel>> getResponseTypeToken() {
        return new TypeToken<List<UgcMyCommentModel>>() {};
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }
}
