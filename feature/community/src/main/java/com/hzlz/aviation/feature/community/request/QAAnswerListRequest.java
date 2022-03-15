package com.hzlz.aviation.feature.community.request;


import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.community.api.CircleAPI;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 问答者列表
 */
public class QAAnswerListRequest extends BaseGVideoPageMapRequest<AuthorModel> {

    private final String groupId;

    public QAAnswerListRequest(String groupId,int pageNum) {
        this.groupId = groupId;
        mParameters.put("groupId", groupId);
        mParameters.put("pageNum", pageNum);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return CircleAPI.Instance.get().getQATeacherList(groupId, mParameters);
    }

    @Override
    protected TypeToken<List<AuthorModel>> getResponseTypeToken() {
        return new TypeToken<List<AuthorModel>>() {};
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }
}
