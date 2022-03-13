package com.jxntv.circle.request;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.jxntv.circle.api.CircleAPI;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CircleEditRequest extends BaseGVideoMapRequest<Object> {

    public void setCover(String cover) {
        if(TextUtils.isEmpty(cover)){
            return;
        }
        mParameters.put("cover", cover);
    }

    public void setGroupId(long groupId) {
        mParameters.put("groupId", groupId);
    }

    public void setIntroduction(String introduction) {
        if(TextUtils.isEmpty(introduction)){
            return;
        }
        mParameters.put("introduction", introduction);
    }

    public void setName(String name) {
        if(TextUtils.isEmpty(name)){
            return;
        }
        mParameters.put("name", name);
    }

    @Override
    protected int getMaxParameterCount() {
        return 4;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().circleEdit(mParameters);
    }
}
