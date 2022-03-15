package com.hzlz.aviation.kernel.base.plugin;

import android.os.Bundle;

import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.library.ioc.Plugin;

import io.reactivex.rxjava3.core.Observable;

/**
 * Feed模块接口
 */
public interface LoadMorePlugin extends Plugin {

    /** 模块-key */
    String KEY_MODULE = "key_module";
    /** 模块-feed */
    String MODULE_FEED = "module_feed";
    /** 模块-search */
    String MODULE_SEARCH = "module_search";

    /**
     * 拉取更多数据
     *
     * @param refresh 进入详情页时重新请求数据，清除掉历史cursor；
     * @param entryBundle   中转数据存储
     * @return 对应的observable
     */
    Observable<ShortVideoListModel> loadMore(boolean refresh, Bundle entryBundle);
}
