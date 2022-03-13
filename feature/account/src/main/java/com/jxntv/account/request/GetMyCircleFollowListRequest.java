package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.UgcAuthorModel;
import com.jxntv.network.request.BaseGVideoPageMapRequest;
import java.util.List;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取我的关注、我的圈子
 */
public final class GetMyCircleFollowListRequest extends BaseGVideoPageMapRequest<UgcAuthorModel> {

    private String userId;
    private boolean isFollow;

    public void setParameters(String userId, int pageNum,boolean isFollow) {
        mParameters.put("userId", userId);
        mParameters.put("pageNum", pageNum);
        mParameters.put("pageSize", 500);
        this.userId = userId;
        this.isFollow = isFollow;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        if (isFollow) {
            return AccountAPI.Instance.get().getUgcMyFollow(userId, mParameters);
        }else {
            return AccountAPI.Instance.get().getUgcMyCircle(userId, mParameters);
        }
    }

    @Override
    protected TypeToken<List<UgcAuthorModel>> getResponseTypeToken() {
        return new TypeToken<List<UgcAuthorModel>>() {
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

}
