package com.hzlz.aviation.feature.record.recorder.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.record.recorder.api.RecordApi;
import com.hzlz.aviation.kernel.base.model.PublishLinkWhiteListItem;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class GetPublishLinkWhiteListRequest extends BaseGVideoRequest<ArrayList<PublishLinkWhiteListItem>> {
    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return RecordApi.Instance.get().getPublishLinkWhiteList();
    }
}
