package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.UGCMenuTabModel;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.network.request.BaseGVideoRequest;
import com.jxntv.network.request.BaseRequest;

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
