package com.jxntv.android.video.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.android.video.api.VideoAPI;
import com.jxntv.media.model.MediaModel;
import com.jxntv.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * @author huangwei
 * date : 2021/2/9
 * desc :
 */
public class NewsListRequest extends BaseGVideoPageMapRequest<MediaModel> {


    private boolean isSpecial = false;
    private String id;

    public void setParameters(String id,int pageNum) {
        this.id = id;
        mParameters.put("id",id);
        mParameters.put("pageNum",pageNum);
        mParameters.put("pageSize",20);
    }

    public void setSpecial(boolean isSpecial){
        this.isSpecial = isSpecial;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {

        if (!isSpecial){
            return VideoAPI.Instance.get().loadNewsContentList(id, mParameters);
        }else {
            return VideoAPI.Instance.get().loadSpecialList(id, mParameters);
        }
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected TypeToken<List<MediaModel>> getResponseTypeToken() {
        return new TypeToken<List<MediaModel>>(){};
    }

    @Override
    protected JsonElement furtherProcessData(JsonElement data) {
        return getMemberJsonElement(data, "list");
    }
}
