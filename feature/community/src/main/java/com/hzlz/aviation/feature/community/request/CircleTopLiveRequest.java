package com.hzlz.aviation.feature.community.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CircleTopLiveRequest extends BaseGVideoMapRequest<List<MediaModel>> {

    public void setGroupId(long groupId) {
        mParameters.put("groupId", groupId);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().circleTopLive(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 1;
    }

}
