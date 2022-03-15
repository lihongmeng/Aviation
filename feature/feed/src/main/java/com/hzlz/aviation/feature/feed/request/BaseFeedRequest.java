package com.hzlz.aviation.feature.feed.request;

import com.hzlz.aviation.feature.feed.api.FeedApi;
import com.hzlz.aviation.kernel.network.BaseUrlChangedCallback;
import com.hzlz.aviation.kernel.network.NetworkManager;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

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
