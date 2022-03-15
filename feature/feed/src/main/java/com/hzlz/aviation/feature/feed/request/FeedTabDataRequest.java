package com.hzlz.aviation.feature.feed.request;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.feed.api.FeedApiConstants;
import com.hzlz.aviation.feature.feed.frame.tab.TabItemDataManager;
import com.hzlz.aviation.feature.feed.model.FeedResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * feed tab数据请求
 */
public class FeedTabDataRequest extends BaseFeedRequest<FeedResponse> {

    public static final String HOME_RECOMMEND_ID = "home_recommend";
    /** Home垂类id */
    private static final String HOME_CHANNEL_ID = "1";

    /** 待请求的tab id */
    private String mTabId;
    /** 资源类型 */
    private int mType;
    /** 位置标记 */
    private String mCursor;


    /**
     * 构造函数
     */
    public FeedTabDataRequest(String tabId, String cursor) {
        mTabId = tabId;
        mCursor = cursor;
        if (!TextUtils.isEmpty(tabId)) {
            mParameters.put(FeedApiConstants.CHANNEL_ID, tabId);
        }
        if (!TextUtils.isEmpty(cursor)) {
            mParameters.put(FeedApiConstants.CURSOR, cursor);
        }
    }

    /**
     *  设置加载type
     *
     * @param type  待设置的type类型
     */
    public void setType(@FeedApiConstants.ResourceType int type) {
        mType = type;
        mParameters.put(FeedApiConstants.TYPE, type);
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        if (TextUtils.equals(HOME_RECOMMEND_ID, mTabId)) {
            mParameters.put(FeedApiConstants.CHANNEL_ID, HOME_CHANNEL_ID);
            return getFeedApi().loadHomeData(mParameters);
        }else if (TextUtils.equals(TabItemDataManager.TAB_ID_VIDEO_AUDIO, mTabId)){
            return getFeedApi().loadVideoAudio(mParameters);
        }else if (TextUtils.equals(TabItemDataManager.TAB_ID_NEWS,mTabId)){
            return getFeedApi().loadFeedData(mType, mTabId, mCursor);
        }
        return getFeedApi().loadTabData(mType, mTabId, mCursor);
    }

    @Override
    protected int getMaxParameterCount() {
        return 2;
    }
}
