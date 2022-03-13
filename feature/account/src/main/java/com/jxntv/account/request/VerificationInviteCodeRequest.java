package com.jxntv.account.request;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class VerificationInviteCodeRequest extends BaseGVideoMapRequest<Object> {

    public void setInviteCode(@NonNull String inviteCode) {
        mParameters.put("inviteCode", inviteCode);
    }

    public void setMobile(@NonNull String mobile) {
        mParameters.put("mobile", mobile);
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().verificationInviteCode(mParameters);
    }

}
