package com.jxntv.account.request;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.LoginResponse;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 登录请求
 *
 * @since 2020-01-13 16:05
 */
public final class QuickLoginRequest extends BaseGVideoMapRequest<LoginResponse> {

    /**
     * 设置国家代码
     *
     * @param countryCode 国家代码
     */
    public void setCountryCode(@NonNull String countryCode) {
        mParameters.put("countryCode", countryCode);
    }

    /**
     * 神策匿名id
     */
    public void setDistinctId(String distinctId) {
        mParameters.put("distinctId", distinctId);
    }

    public void setToken(String token) {
        mParameters.put("token", token);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().quickLogin(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

}
