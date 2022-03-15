package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.UGCMenuTabModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/12/29
 * desc :
 **/
public class UGCMenuTabRequest extends BaseGVideoRequest<UGCMenuTabModel> {

    private String userId;

    public void setUserId(String userId){
        this.userId = userId;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().getUgcMenuTabs(userId);
    }
}
