package com.jxntv.feed.request;

import com.jxntv.feed.api.FeedApi;
import com.jxntv.network.BaseUrlChangedCallback;
import com.jxntv.network.NetworkManager;
import com.jxntv.network.request.BaseGVideoMapRequest;

/**
 * feed数据请求基类
 */
public abstract class BaseFeedRequest<T> extends BaseGVideoMapRequest<T> {

    /** 持有的feed api */
    private volatile static FeedApi sAPI = null;

    /**
     * 获取持有的feed api
     *
     * @return feed api
     */
    protected FeedApi getFeedApi() {
        FeedApi temp = sAPI;
        if (temp == null) {
            synchronized (BaseFeedRequest.class) {
                temp = sAPI;
                if (temp == null) {
                    temp = getRetrofit().create(FeedApi.class);
                    sAPI = temp;
                    NetworkManager.getInstance().addBaseUrlChangedCallback(new BaseUrlChangedCallback() {
                        @Override
                        public void onBaseUrlChanged() {
                            sAPI = getRetrofit().create(FeedApi.class);
                        }
                    });
                }
            }
        }
        return temp;
    }
}
