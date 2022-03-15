package com.hzlz.aviation.feature.community.request;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

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
