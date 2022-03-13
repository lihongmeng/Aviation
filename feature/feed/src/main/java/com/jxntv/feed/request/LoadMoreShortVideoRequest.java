package com.jxntv.feed.request;

import com.google.gson.JsonElement;
import com.jxntv.feed.model.FeedResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class LoadMoreShortVideoRequest extends BaseFeedRequest<FeedResponse> {

    public LoadMoreShortVideoRequest(String mediaId, int pageNumber, int pageSize) {
        mParameters.put("mediaId", mediaId);
        mParameters.put("pageNum", pageNumber);
        mParameters.put("pageSize", pageSize);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        return getFeedApi().loadMoreShortVideo(mParameters);
    }

    @Override
    protected int getMaxParameterCount() {
        return 3;
    }
}
