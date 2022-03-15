package com.hzlz.aviation.feature.feed;

import static com.hzlz.aviation.kernel.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_RADIO;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.material.tabs.TabLayout;
import com.hzlz.aviation.feature.feed.view.FeedPageFragment;
import com.hzlz.aviation.feature.feed.view.GVideoFeedTabLayout;
import com.hzlz.aviation.kernel.base.model.feed.TabItemInfo;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.base.view.tab.GVideoSmartTabLayout;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;

/**
 * radio tab fragment
 */
public class RadioFragment extends BaseFeedFragment {
    @Override public String getPid() {
        return StatPid.FM;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //注册监听提前了，对于懒加载fragment仍可以监听，需要留意mBinding等可能还没初始化
        listenEvent();
    }

    @Override
    protected void initImmersive() {
        super.initImmersive();
        mBinding.getRoot().setPadding(0, WidgetUtils.getStatusBarHeight(),0,0);
    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
    }

    @Override
    protected void initView() {
        mBinding.feedTab.changeType(GVideoFeedTabLayout.TYPE_FEED_TAB_SEARCH_ICON_DARK);
        mBinding.feedTab.setVisibility(View.VISIBLE);
        mBinding.feedTab.setPid(getPageName());
        mBinding.homeTab.homeTabContainer.setVisibility(View.GONE);
        super.initView();
    }

    // @Override protected void initViewPager() {
    //     super.initViewPager();
    //     String tabId = getArguments() != null ? getArguments().getString(FeedPlugin.KEY_START_TAB_ID) : null;
    //     checkStartTabId(tabId);
    // }

    @Override
    protected Class<? extends Fragment> getFragmentClass() {
        return FeedPageFragment.class;
    }

    @Override
    protected GVideoSmartTabLayout getTabLayout() {
        return mBinding.feedTab.getTabLayout();
    }

    @Override
    protected void viewPageChange(int position) {

    }

    @Override
    protected void tabViewPagerInitComplete() {

    }

    @Override
    protected TabLayout.OnTabSelectedListener getTabSelectedListener() {
        return null;
    }

    @Override
    protected void initTabView(TabLayout.Tab tab, TabItemInfo item, int position) {

    }


    @Override
    protected int getTabKey() {
        return MEDIA_TYPE_RADIO;
    }

    protected boolean isDarkMode() {
        return true;
    }

    private void listenEvent() {
        GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION, Integer.class)
            .observe(this, new Observer<Integer>() {
                @Override public void onChanged(Integer integer) {
                    if (mBinding != null) {
                        mBinding.feedTab.setUnreadCount(integer);
                    }
                }
            });
        // GVideoEventBus.get(HomePlugin.EVENT_FM_TAB, String.class).observe(this,
        //     new Observer<String>() {
        //         @Override public void onChanged(String tabId) {
        //             if (getArguments() != null) {
        //                 getArguments().putString(FeedPlugin.KEY_START_TAB_ID, tabId);
        //             }
        //             checkStartTabId(tabId);
        //         }
        //     });
    }

    // private void checkStartTabId(String tabId) {
    //     if (mTabItemInfoList != null && !mTabItemInfoList.isEmpty()) {
    //         int size = mTabItemInfoList.size();
    //         for (int i = 0; i < size; i++) {
    //             TabItemInfo tab = mTabItemInfoList.get(i);
    //             if (tab != null && TextUtils.equals(tab.tabId, tabId)) {
    //                 if (mBinding != null) {
    //                     mBinding.feedViewPager.setCurrentItem(i);
    //                 }
    //                 break;
    //             }
    //         }
    //     }
    // }
}
