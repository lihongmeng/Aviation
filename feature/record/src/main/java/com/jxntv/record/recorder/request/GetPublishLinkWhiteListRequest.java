package com.jxntv.record.recorder.request;

import com.google.gson.JsonElement;
import com.jxntv.network.request.BaseGVideoRequest;
import com.jxntv.record.recorder.api.RecordApi;
import com.jxntv.base.model.PublishLinkWhiteListItem;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetPublishLinkWhiteListRequest extends BaseGVideoRequest<ArrayList<PublishLinkWhiteListItem>> {
    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return RecordApi.Instance.get().getPublishLinkWhiteList();
    }
}
