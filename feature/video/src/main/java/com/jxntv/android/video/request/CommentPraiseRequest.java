package com.jxntv.android.video.request;

import com.google.gson.JsonElement;
import com.jxntv.android.video.api.VideoAPI;
import com.jxntv.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 评论点赞
 */
public class CommentPraiseRequest extends BaseGVideoMapRequest<Object> {


    public void setCommentId(long commentId) {
        mParameters.put("commentId", commentId);
    }

    public void setType(boolean isReply) {
        mParameters.put("type", isReply ? 1 : 0);
    }

    public void setIsPraise(boolean isPraise) {
        mParameters.put("status", isPraise);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return VideoAPI.Instance.get().commentPraise(mParameters);
    }
}
