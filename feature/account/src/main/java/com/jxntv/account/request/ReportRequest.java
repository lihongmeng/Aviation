package com.jxntv.account.request;


import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 举报
 */
public final class ReportRequest extends BaseGVideoMapRequest<Object> {

    public void setParameters(int type, int contentType, String mediaId) {
        mParameters.put("contentId", mediaId);
        mParameters.put("contentType", contentType);
        mParameters.put("type", type);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().report(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }
}
