package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.InitializationConfig;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

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

