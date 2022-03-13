package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.InitializationConfig;
import com.jxntv.network.request.BaseGVideoRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取初始化配置请求
 *
 * @since 2020-03-07 21:13
 */
public final class GetInitializationConfigRequest extends BaseGVideoRequest<InitializationConfig> {

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().getInitializationConfig();
    }

    @Override
    public int getRetryTimes() {
        return 3;
    }

}

