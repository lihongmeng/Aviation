package com.jxntv.base.plugin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.model.banner.BannerModel;
import com.jxntv.base.model.feed.TabItemInfo;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.ioc.Plugin;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * Feed模块接口
 */
public interface FeedPlugin extends Plugin {

    /**
     * KEY: fragment uuid
     */
    String KEY_FRAGMENT_UUID = "key_fragment_uuid";
    /**
     * 启动tabId
     */
    String KEY_START_TAB_ID = "key_start_tab_id";

    /**
     * 获取Feed frgament
     *
     * @param packageName 页面可能是通用的页面，无法决定自身的pid
     *                    所以需要上层指定它的packageName
     *
     * @return 返回Feed fragment
     */
    @NonNull
    BaseFragment getFeedFragment(String packageName);

    /**
     * 获取radio frgament
     *
     * @return 返回radio fragment
     */
    @NonNull
    BaseFragment getRadioFragment();

    /**
     * 获取video frgament
     *
     * @return 返回video fragment
     */
    @NonNull
    BaseFragment getVideoFragment();

    /**
     * 闪屏结束事件
     */
    void onSplashEnd();

    /**
     * 首页release事件
     */
    void onHomeRelease();

    /**
     * 加载更多feed短视频/音频数据
     *
     * @param refresh 进入详情页首次加载数据时会清空掉历史cursor；
     * @param data    对应的bundle信息，用于内部判断
     * @return 对应的observable
     */
    @Nullable
    Observable<ShortVideoListModel> loadMoreFeedData(boolean refresh, Bundle data);


    @Nullable
    Observable<ShortVideoListModel> loadMoreFeedData(boolean refresh, String tabId);

    /**
     * 获取banner数据
     *
     * @param locationId 内容id  -2 首页推荐   -1 新闻首页   圈子id
     * @return Observable<BannerModel>
     */
    Observable<BannerModel> getBannerList(int locationId);

    /**
     * 获取banner数据
     *
     * @param locationId 内容id  -2 首页推荐   -1 新闻首页   圈子id
     * @param scene      Banner位场景
     * @return Observable<BannerModel>
     */
    Observable<BannerModel> getBannerList(int locationId, int scene);

    /**
     * 是否有未读消息
     */
    int getUnreadCount();

    void setUnreadCount(int hasUnread);

    Observable<List<TabItemInfo>> getTabItemInfoList(int mediaType);

    Observable<ShortVideoListModel> loadMoreShortVideo(
            String mediaId,
            int pageNumber,
            int pageSize
    );

}
