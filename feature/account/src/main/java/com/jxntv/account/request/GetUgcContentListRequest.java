package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.Media;
import com.jxntv.account.ui.ugc.detail.UgcContentType;
import com.jxntv.media.model.MediaModel;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import io.reactivex.rxjava3.core.Observable;

import java.util.List;

import retrofit2.Response;

/**
 * 获取收藏列表请求
 *
 * @since 2020-02-12 15:36
 */
public final class GetUgcContentListRequest extends BaseGVideoPageMapRequest<MediaModel> {

    private String userId;
    private @UgcContentType
    int type;

    public void setParameters(String userId, int pageNum, @UgcContentType int type) {
        mParameters.put("userId", userId);
        mParameters.put("pageNum", pageNum);
        mParameters.put("pageSize", 20);
        this.userId = userId;
        this.type = type;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        if (type == UgcContentType.COMPOSITION) {
            return AccountAPI.Instance.get().getUgcCompositionList(userId, mParameters);
        } else if (type == UgcContentType.QUESTION) {
            return AccountAPI.Instance.get().getUgcQuestionList(userId, mParameters);
        }if (type == UgcContentType.ANSWER) {
            return AccountAPI.Instance.get().getUgcAnswerList(userId, mParameters);
        } else {
            return AccountAPI.Instance.get().getUgcFavoriteList(userId, mParameters);
        }
    }

    @Override
    protected TypeToken<List<MediaModel>> getResponseTypeToken() {
        return new TypeToken<List<MediaModel>>() {};
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
