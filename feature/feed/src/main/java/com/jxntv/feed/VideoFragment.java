package com.jxntv.feed;

import static com.jxntv.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_VIDEO;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.model.feed.TabItemInfo;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.utils.WidgetUtils;
import com.jxntv.base.view.tab.GVideoSmartTabLayout;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.feed.view.FeedPageFragment;
import com.jxntv.feed.view.GVideoFeedTabLayout;
import com.jxntv.stat.StatPid;

/**
 * video tab fragment
 */
public class VideoFragment extends BaseFeedFragment {

    @Override
    public String getPid() {
        return StatPid.VIDEO;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //注册监听提前了，对于懒加载fragment仍可以监听，需要留意mBinding等可能还没初始化
        listenEvent();
    }

    @Override
    protected void initImmersive() {
        mBinding.getRoot().setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        if (getContext() != null) {
            ImmersiveUtils.setStatusBarIconColor(this, true);
        }
    }


    @Override
    protected void initView() {
        mBinding.feedTab.changeType(GVideoFeedTabLayout.TYPE_FEED_TAB_NONE_SEARCH_ICON);
        mBinding.feedTab.setPid(getPageName());
        mBinding.feedTab.setVisibility(View.VISIBLE);
        mBinding.homeTab.homeTabContainer.setVisibility(View.GONE);
        super.initView();
    }

    // @Override
    // protected void initViewPager() {
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
        return MEDIA_TYPE_VIDEO;
    }

    private void listenEvent() {
        GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION, Integer.class)
                .observe(this, count -> {
                    if (mBinding != null) {
                        mBinding.feedTab.setUnreadCount(count);
                    }
                });
        // GVideoEventBus.get(HomePlugin.EVENT_VIDEO_TAB, String.class).observe(this,
        //         new Observer<String>() {
        //             @Override
        //             public void onChanged(String tabId) {
        //                 if (getArguments() != null) {
        //                     getArguments().putString(FeedPlugin.KEY_START_TAB_ID, tabId);
        //                 }
        //                 checkStartTabId(tabId);
        //             }
        //         });
    }

    // private void checkStartTabId(String tabId) {
    //     if (mTabItemInfoList != null && !mTabItemInfoList.isEmpty() && !TextUtils.isEmpty(tabId)) {
    //         int size = mTabItemInfoList.size();
    //         for (int i = 0; i < size; i++) {
    //             TabItemInfo tab = mTabItemInfoList.get(i);
    //             if (tab != null && TextUtils.equals(tab.tabId, tabId)) {
    //                 if (mBinding != null) {
    //                     mBinding.feedViewPager.setCurrentItem(i);
    //                 }
    //             }
    //         }
    //     }
    // }
}