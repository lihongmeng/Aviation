package com.jxntv.account.request;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CheckNeedInviteCodeRequest extends BaseGVideoMapRequest<Boolean> {

    public void setMobile(@NonNull String mobile) {
        mParameters.put("mobile", mobile);
    }

    @Override
    protected int getMaxParameterCount() {
        return 1;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().checkNeedInviteCode(mParameters);
    }

}
