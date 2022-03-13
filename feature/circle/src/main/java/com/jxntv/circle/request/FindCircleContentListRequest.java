package com.jxntv.circle.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class FindCircleContentListRequest extends BaseGVideoPageMapRequest<Circle> {

    public void setLabel(Long label) {
        if(label==null){
            return;
        }
        mParameters.put("label", label);
    }

    public void setPageNum(int pageNum) {
        mParameters.put("pageNum", pageNum);
    }

    public void setPageSize(int pageSize) {
        mParameters.put("pageSize", pageSize);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().findCircleContentList(mParameters);
    }

    @Override
    protected TypeToken<List<Circle>> getResponseTypeToken() {
        return new TypeToken<List<Circle>>() {
        };
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }

}
