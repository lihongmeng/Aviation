package com.hzlz.aviation.feature.feed;

import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.START_SCAN_HOME;
import static com.hzlz.aviation.kernel.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_HOME;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.MEET;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.NORMAL;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.SPRING_FESTIVAL;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.material.tabs.TabLayout;
import com.hzlz.aviation.feature.feed.adapter.TopSearchAdapter;
import com.hzlz.aviation.feature.feed.view.FeedPageFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.datamanager.ThemeDataManager;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.feed.TabItemInfo;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.plugin.HomePlugin;
import com.hzlz.aviation.kernel.base.plugin.SearchPlugin;
import com.hzlz.aviation.kernel.base.utils.MeetRedUtils;
import com.hzlz.aviation.kernel.base.view.tab.GVideoSmartTabLayout;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;

import java.util.Collections;
import java.util.List;

/**
 * 首页推荐流fragment
 */
public class FeedFragment extends BaseFeedFragment {

    // private String needSelectTabId = "";

    public FeedFragment() {
        this("");
    }

    public FeedFragment(String packageName) {
        setPageName(packageName);
    }

    @Override
    protected void onArgumentsHandle(Bundle bundle) {
        super.onArgumentsHandle(bundle);
        // needSelectTabId = bundle.getString(FeedPlugin.KEY_START_TAB_ID);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listenEvent();
    }

    @Override
    public String getPageName() {
        // 如果当前子Fragment还没初始化，就使用上级Fragment指定的pageName
        // 如果子类已经初始化完成，就使用子类的，例如“首页-推荐”
        if (currentBaseFragment == null) {
            return super.getPageName();
        }
        return currentBaseFragment.getPageName();
    }

    @Override
    protected void initImmersive() {
        // mBinding.getRoot().setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        if (getContext() != null) {
            ImmersiveUtils.setStatusBarIconColor(this, true);
        }
    }

    private void navigateToSearchActivity(String hintSearchWord) {
        boolean defaultSearchWord = getResources().getString(R.string.search).equalsIgnoreCase(hintSearchWord);
        PluginManager.get(SearchPlugin.class).navigateToSearchActivity(getContext(), defaultSearchWord ? "" : hintSearchWord);
    }

    @Override
    protected void initView() {
        mBinding.feedTab.setVisibility(View.GONE);
        mBinding.homeTab.homeTabContainer.setVisibility(View.VISIBLE);
        super.initView();
        initTopSearchBanner();
        mBinding.homeTab.topBarSearchLayout.setOnClickListener((View.OnClickListener) v -> {
            GVideoSensorDataManager.getInstance().clickSearchEditText();
            int currentItem = mBinding.homeTab.topSearchBanner.getCurrentItem();
            BannerAdapter adapter = mBinding.homeTab.topSearchBanner.getAdapter();
            if (adapter instanceof TopSearchAdapter) {
                String data = (String) adapter.getRealData(currentItem);
                navigateToSearchActivity(data);
            } else {
                navigateToSearchActivity(getResources().getString(R.string.search));
            }

        });
        mBinding.homeTab.messageLayout.setPageName(getPageName());
        mBinding.homeTab.scan.setOnClickListener(
                (View.OnClickListener) v -> GVideoEventBus.get(START_SCAN_HOME).post(null));

        listenEvent();

        updateTheme();
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CHANGE_THEME_COLOR).observe(
                this, o -> updateTheme()
        );
    }

    private void initTopSearchBanner() {
        //先默认初始化兜底数据
        final List<String> defaultSearchList = Collections.singletonList(getResources().getString(R.string.search));
        mBinding.homeTab.topSearchBanner.setAdapter(new TopSearchAdapter(defaultSearchList))
                .setOrientation(Banner.VERTICAL)
                .setLoopTime(6000)
                .setUserInputEnabled(false)
                .addBannerLifecycleObserver(getViewLifecycleOwner())
                .setOnBannerListener((data, p) -> {
                    navigateToSearchActivity((String) data);
                });
        GVideoEventBus.get(HomePlugin.EVENT_HOME_SEARCH_WORD, List.class).observe(getViewLifecycleOwner(), new Observer<List>() {
            @Override
            public void onChanged(List list) {
                if (list != null && list.size() > 0) {
                    mBinding.homeTab.topSearchBanner.setDatas(list);
                } else {
                    mBinding.homeTab.topSearchBanner.setDatas(defaultSearchList);
                }
            }
        });
    }

    private void updateTheme() {
        mBinding.homeTab.gvideoTabs.setNeedDealMeetRed(true);
        int themeColorSwitchInteger = ThemeDataManager.getInstance().getThemeColorSwitchInteger();
        switch (themeColorSwitchInteger) {
            case MEET:
                MeetRedUtils.updateToRed(
                        getActivity(),
                        mBinding.homeTab.homeTabContainer,
                        mBinding.homeTab.topBarSearchLayout,
                        mBinding.homeTab.search,
                        mBinding.homeTab.scan,
                        mBinding.homeTab.gvideoTabs,
                        mBinding.homeTab.messageLayout
                );
                break;
            case SPRING_FESTIVAL:
                MeetRedUtils.updateToSpringFestival(
                        getActivity(),
                        mBinding.homeTab.homeTabContainer,
                        mBinding.homeTab.topBarSearchLayout,
                        mBinding.homeTab.search,
                        mBinding.homeTab.scan,
                        mBinding.homeTab.gvideoTabs,
                        mBinding.homeTab.messageLayout
                );
                break;
            case NORMAL:
            default:
                MeetRedUtils.updateToNormal(
                        getActivity(),
                        mBinding.homeTab.homeTabContainer,
                        mBinding.homeTab.topBarSearchLayout,
                        mBinding.homeTab.search,
                        mBinding.homeTab.scan,
                        mBinding.homeTab.gvideoTabs,
                        mBinding.homeTab.messageLayout
                );
                break;
        }
    }

    @Override
    protected Class<? extends Fragment> getFragmentClass() {
        return FeedPageFragment.class;
    }

    @Override
    protected GVideoSmartTabLayout getTabLayout() {
        return mBinding.homeTab.gvideoTabs;
    }

    @Override
    protected void viewPageChange(int position) {
        // if(position>1){
        //     return;
        // }
        // 用于记录离开FeedFragment最后浏览的子Fragment，再次启动App的时候直接显示
        // String userId = PluginManager.get(AccountPlugin.class).getUserId();
        // FeedSharedPrefs feedSharedPrefs=FeedSharedPrefs.getInstance();
        // if (TextUtils.isEmpty(userId)) {
        //     feedSharedPrefs.putString(SP_HOME_FEED_TAB_KEY_VISITOR, mTabItemInfoList.get(position).tabId);
        // } else {
        //     feedSharedPrefs.putString(SP_HOME_FEED_TAB_KEY + userId, mTabItemInfoList.get(position).tabId);
        // }
    }

    @Override
    protected void tabViewPagerInitComplete() {
        // 用于记录离开FeedFragment最后浏览的子Fragment，再次启动App的时候直接显示
        // String userId = PluginManager.get(AccountPlugin.class).getUserId();
        // FeedSharedPrefs feedSharedPrefs=FeedSharedPrefs.getInstance();
        // String key = "";
        // if (TextUtils.isEmpty(userId)){
        //     key = SP_HOME_FEED_TAB_KEY_VISITOR;
        // }else {
        //     key = SP_HOME_FEED_TAB_KEY + userId;
        // }
        // String tabId = feedSharedPrefs.getString(key, "");

        // 默认打开首页推荐
        // String tabId = TabItemDataManager.TAB_ID_HOME_RECOMMEND;
        // if (!TextUtils.isEmpty(needSelectTabId)) {
        //     tabId = needSelectTabId;
        // }
        // int index = 0;
        // boolean hasFindTab = false;
        // for (int i = 0; i < mTabItemInfoList.size(); i++) {
        //     if (TextUtils.equals(mTabItemInfoList.get(i).tabId, tabId)) {
        //         index = i;
        //         hasFindTab = true;
        //         break;
        //     }
        // }
        // if (!hasFindTab) {
        //     for (int i = 0; i < mTabItemInfoList.size(); i++) {
        //         if (TextUtils.equals(mTabItemInfoList.get(i).tabId, TabItemDataManager.TAB_ID_HOME_RECOMMEND)) {
        //             index = i;
        //             break;
        //         }
        //     }
        // }
        // feedSharedPrefs.putString(key, mTabItemInfoList.get(index).tabId);
        // mBinding.feedViewPager.setCurrentItem(index);
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
        return MEDIA_TYPE_HOME;
    }

    private void listenEvent() {
        GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION, Integer.class)
                .observe(this, hasUnread -> {
                    if (mBinding != null) {
                        PluginManager.get(FeedPlugin.class).setUnreadCount(hasUnread);
                        mBinding.homeTab.messageLayout.setUnreadCount(hasUnread);
                    }
                });
        // GVideoEventBus.get(HomePlugin.EVENT_HOME_TAB, String.class)
        //         .observe(this, tabId -> {
        //             Bundle bundle = getArguments();
        //             if (bundle != null) {
        //                 bundle.putString(FeedPlugin.KEY_START_TAB_ID, tabId);
        //             }
        //             checkStartTabId(tabId);
        //         });
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
