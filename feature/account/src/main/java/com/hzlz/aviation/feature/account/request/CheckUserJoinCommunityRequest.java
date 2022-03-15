package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CheckUserJoinCommunityRequest extends BaseGVideoMapRequest<Boolean> {

    public void setJid(String jid){
        mParameters.put("jid",jid);
    }

    public void setGroupId(long groupId) {
        mParameters.put("groupId", groupId);
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().checkUserJoinCommunity(mParameters);
    }

}
