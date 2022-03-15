package com.hzlz.aviation.feature.community.request;


import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 问答广场
 */
public class QAGroupListRequest extends BaseGVideoPageMapRequest<MediaModel> {

    /**
     * 参数
     *
     * @param groupId 圈子id
     * @param page    页数
     */
    public void setParameters(String groupId, int page) {
        mParameters.put("groupId", groupId);
        mParameters.put("pageNum", page);
        mParameters.put("pageSize", 20);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().getQAGroupList(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected TypeToken<List<MediaModel>> getResponseTypeToken() {
        return new TypeToken<List<MediaModel>>() {};
    }

}
