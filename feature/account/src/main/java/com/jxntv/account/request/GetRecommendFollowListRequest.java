package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.Author;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.network.request.BaseGVideoRequest;

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
