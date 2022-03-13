package com.jxntv.circle.request;

import com.google.gson.JsonElement;
import com.jxntv.base.model.circle.CircleTag;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class FindCircleTagListRequest extends BaseGVideoRequest<List<CircleTag>> {

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().findCircleTagList();
    }

}
