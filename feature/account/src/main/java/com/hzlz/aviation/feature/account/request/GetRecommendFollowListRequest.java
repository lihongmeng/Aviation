package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetRecommendFollowListRequest extends BaseGVideoRequest<List<Author>> {

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().getRecommendFollowList();
    }

    @Override
    protected TypeToken<List<Author>> getResponseTypeToken() {
        return new TypeToken<List<Author>>() {
        };
    }

}
