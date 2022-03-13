package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.OneKeyFollowModel;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.network.request.BaseGVideoRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class OneKeyFollowRequest extends BaseGVideoRequest<Object> {

    private List<OneKeyFollowModel> oneKeyFollowModelList;

    public void setOneKeyFollowModelList(List<OneKeyFollowModel> oneKeyFollowModelList){
        this.oneKeyFollowModelList=oneKeyFollowModelList;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return AccountAPI.Instance.get().oneKeyFollow(oneKeyFollowModelList);
    }
}
