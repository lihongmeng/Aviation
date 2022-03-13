package com.jxntv.feed.request;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.feed.model.FeedResponse;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * feed tab数据请求
 */
public class FeedTabDetailRequest extends BaseFeedRequest<FeedResponse> {

    public static final String HOME_RECOMMEND_ID = "home_recommend";
    /** Home垂类id */
    private static final String HOME_CHANNEL_ID = "1";

    /** 待请求的tab id */
    private String mTabId;
    /** 位置标记 */
    private String mCursor;


    /**
     * 构造函数
     */
    public FeedTabDetailRequest(String tabId, String cursor) {
        mTabId = tabId;
        mCursor = cursor;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        if (TextUtils.equals(mTabId, HOME_RECOMMEND_ID)) {
            mTabId = HOME_CHANNEL_ID;
        }
        return getFeedApi().loadTabDetail(mTabId, mCursor);
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }
}
