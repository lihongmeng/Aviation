package com.hzlz.aviation.feature.feed.frame.tab;

import com.hzlz.aviation.kernel.base.model.feed.TabItemInfo;

import java.util.List;

/**
 * tab数据接口
 */
public interface ITabItemDataManager {

    /**
     * 获取tab列表
     */
    List<TabItemInfo> getTabItemList(int key);

    /**
     * 用于检测是否需要刷新tab导航栏
     */
    boolean isNeedRefresh();
}
