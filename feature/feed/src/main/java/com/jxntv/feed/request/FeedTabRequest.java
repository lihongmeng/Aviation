package com.jxntv.feed.request;

import static com.jxntv.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_HOME;
import static com.jxntv.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_LIVE;
import static com.jxntv.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_RADIO;
import static com.jxntv.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_VIDEO;

import com.google.gson.JsonElement;
import com.jxntv.base.Constant;
import com.jxntv.base.model.feed.TabItemInfo;
import com.jxntv.feed.api.FeedApiConstants;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * feed tab请求
 */
public class FeedTabRequest extends BaseFeedRequest<List<TabItemInfo>> {

    /**  媒体类型 */
    private @Constant.TabMediaType
    int mMediaType;

    /**
     * 构造函数
     */
    public FeedTabRequest(@Constant.TabMediaType int type) {
        mMediaType = type;
    }

    @Override
    protected Observable<Response<JsonElement>> getResponseObservable() {
        String channelType;
        if (mMediaType == MEDIA_TYPE_VIDEO) {
            channelType = FeedApiConstants.CHANNEL_TYPE_VIDEO;
        } else if (mMediaType == MEDIA_TYPE_RADIO) {
            channelType = FeedApiConstants.CHANNEL_TYPE_RADIO;
        } else if(mMediaType == MEDIA_TYPE_HOME){
            channelType = FeedApiConstants.CHANNEL_TYPE_HOME;
        } else if(mMediaType == MEDIA_TYPE_LIVE){
            channelType = FeedApiConstants.CHANNEL_TYPE_LIVE;
        }else {
            channelType = "";
        }
        return getFeedApi().loadFeedTabs(channelType);
    }

    @Override
    protected int getMaxParameterCount() {
        return 0;
    }
}
