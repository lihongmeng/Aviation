package com.hzlz.aviation.feature.feed.frame.tab;


import static com.hzlz.aviation.kernel.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_HOME;
import static com.hzlz.aviation.kernel.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_RADIO;
import static com.hzlz.aviation.kernel.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_VIDEO;

import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.feed.TabItemInfo;
import com.hzlz.aviation.kernel.stat.stat.StatPid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tab数据管理类
 */
public class TabItemDataManager implements ITabItemDataManager {

    //首页新闻
    public static final String TAB_ID_NEWS = StatPid.HOME_NEWS;
    //视听
    public static final String TAB_ID_VIDEO_AUDIO = StatPid.VIDEO_AUDIO;
    //首页推荐
    public static final String TAB_ID_HOME_RECOMMEND = StatPid.HOME_RECOMMEND;
    //首页关注
    public static final String TAB_ID_FOLLOW = StatPid.HOME_FOLLOW;
    //首页关注
    public static final String TAB_ID_HOME_LIVE = StatPid.HOME_LIVE;
    //首页关注
    public static final String TAB_ID_WATCH_TV = StatPid.WATCH_TV;
    //web外链界面
    public static final String TAB_RES_TYPE_LINK = StatPid.WEB_LINK;

    private final Map<Integer, List<TabItemInfo>> mTabItemMap = new HashMap<>();

    /**
     * 单例模式
     */
    private static volatile TabItemDataManager sInstance;

    public static TabItemDataManager getInstance() {
        if (sInstance == null) {
            synchronized (TabItemDataManager.class) {
                if (sInstance == null) {
                    sInstance = new TabItemDataManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 测试接口
     */
    private TabItemDataManager() {
        List<TabItemInfo> feedList = new ArrayList<>();

//        TabItemInfo tabItemInfo = new TabItemInfo(FeedRuntime.getAppContext().getString(R.string.feed_page_title_news));
//        tabItemInfo.tabId = TAB_ID_NEWS;
//        feedList.add(tabItemInfo);
//
//        tabItemInfo = new TabItemInfo(FeedRuntime.getAppContext().getString(R.string.feed_page_title_home));
//        tabItemInfo.tabId = TAB_ID_HOME_RECOMMEND;
//        feedList.add(tabItemInfo);
//
//        tabItemInfo = new TabItemInfo(FeedRuntime.getAppContext().getString(R.string.feed_page_title_follow));
//        tabItemInfo.tabId = TAB_ID_FOLLOW;
//        feedList.add(tabItemInfo);

//        tabItemInfo = new TabItemInfo(FeedRuntime.getAppContext().getString(R.string.feed_page_title_moment));
//        tabItemInfo.tabId = TAB_ID_MOMENT;
//        feedList.add(tabItemInfo);

        mTabItemMap.put(MEDIA_TYPE_HOME, feedList);

        List<TabItemInfo> videoList = new ArrayList<>();
        mTabItemMap.put(MEDIA_TYPE_VIDEO, videoList);

        List<TabItemInfo> radioList = new ArrayList<>();
        mTabItemMap.put(MEDIA_TYPE_RADIO, radioList);
    }

    /**
     * 外部统一存储接口
     */
    public void saveTabs(List<TabItemInfo> tabs, @Constant.TabMediaType int key) {
        mTabItemMap.put(key, tabs);
    }

    @Nullable
    @Override
    public List<TabItemInfo> getTabItemList(@Constant.TabMediaType int key) {
        return mTabItemMap.get(key);
    }

    @Override
    public boolean isNeedRefresh() {
        return false;
    }
}
