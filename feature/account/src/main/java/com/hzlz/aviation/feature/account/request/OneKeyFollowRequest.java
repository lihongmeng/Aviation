package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.OneKeyFollowModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

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
