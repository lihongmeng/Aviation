package com.hzlz.aviation.feature.feed;

import static com.hzlz.aviation.kernel.base.Constant.EXTRA_IS_FOLLOW_NEED_TOP_SPACE;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_TAB_ID;
import static com.hzlz.aviation.kernel.base.Constant.TAB_MEDIA_TYPE.MEDIA_TYPE_HOME;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hzlz.aviation.feature.feed.databinding.FragmentFeedBinding;
import com.hzlz.aviation.feature.feed.frame.tab.TabItemDataManager;
import com.hzlz.aviation.feature.feed.repository.FeedRepository;
import com.hzlz.aviation.feature.feed.view.FeedPageFragment;
import com.hzlz.aviation.feature.feed.view.recommend.HomeRecommendFragment;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.model.feed.TabItemInfo;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.LivePlugin;
import com.hzlz.aviation.kernel.base.plugin.WatchTvPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.base.view.tab.GVideoSmartTabLayout;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * feed类fragment基类
 */
public abstract class BaseFeedFragment extends BaseFragment<FragmentFeedBinding> {

    /**
     * 首页page 适配器
     */
    protected FragmentPagerItemAdapter mPagerAdapter;
    /**
     * tab info列表
     */
    protected List<TabItemInfo> mTabItemInfoList;
    /**
     * feed数据仓库
     */
    protected FeedRepository mFeedRepository = FeedRepository.getInstance();
    /**
     * 最长等待时间
     */
    private static final int LOAD_DATA_TIME_LIMIT = 10;
    /**
     * 当前选中界面
     */
    protected BaseFragment currentBaseFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_feed;
    }

    @Override
    protected void initView() {
        int color = isDarkMode() ? R.color.color_1b1c1f : R.color.color_ffffff;
        mBinding.fragmentLayout.setBackgroundColor(
                FeedRuntime.getAppContext().getResources().getColor(color));
        FeedFragmentManager.getInstance().addFragmentRef(this);
        setupPlaceholderLayout(R.id.empty_container);

        int statusBarHeight = WidgetUtils.getStatusBarHeight();
        int dp5 = getResources().getDimensionPixelOffset(R.dimen.DIMEN_5DP);
        ConstraintLayout.LayoutParams backButtonLayoutParams = (ConstraintLayout.LayoutParams) mBinding.homeTab.topBarSearchLayout.getLayoutParams();
        backButtonLayoutParams.setMargins(0, statusBarHeight + dp5, 0, 0);
        mBinding.homeTab.topBarSearchLayout.setLayoutParams(backButtonLayoutParams);
    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    public void onReload(@NonNull View view) {
        if (NetworkUtils.isNetworkConnected()) {
            startLoadData();
        } else {
            updatePlaceholderLayoutType(PlaceholderType.NETWORK_NOT_AVAILABLE);
        }
    }

    /**
     * 显示占位页面
     */
    private void showPlaceHolderView(@PlaceholderType int type) {
        if (mBinding == null) {
            return;
        }
        updatePlaceholderLayoutType(type);
        mBinding.emptyContainer.setVisibility(View.VISIBLE);
        mBinding.feedViewPager.setVisibility(View.GONE);
    }

    /**
     * 隐藏占位页面
     */
    private void hidePlaceHolderView() {
        if (mBinding == null) {
            return;
        }
        mBinding.emptyContainer.setVisibility(View.GONE);
        mBinding.feedViewPager.setVisibility(View.VISIBLE);
    }

    @Override
    protected void loadData() {
        if (NetworkUtils.isNetworkConnected() || getTabKey() == MEDIA_TYPE_HOME) {
            startLoadData();
        } else {
            showPlaceHolderView(PlaceholderType.NETWORK_NOT_AVAILABLE);
        }
    }

    private void startLoadDataReal(List<TabItemInfo> tabItemList) {
        hidePlaceHolderView();
        mTabItemInfoList = tabItemList;
        initViewPager();
        initTabLayout();
        initViewpagerDefaultIndex();
        tabViewPagerInitComplete();
    }

    private void initViewpagerDefaultIndex() {
        int defaultIndex = 0;
        for (int index = 0; index < mTabItemInfoList.size(); index++) {
            TabItemInfo tabItemInfo = mTabItemInfoList.get(index);
            if (tabItemInfo.selected) {
                defaultIndex = index;
            }
        }
        mBinding.feedViewPager.setCurrentItem(defaultIndex);
    }

    private void startLoadData() {
        int tabKey = getTabKey();
        List<TabItemInfo> tabItemList = TabItemDataManager.getInstance().getTabItemList(tabKey);
        if (tabItemList != null && !tabItemList.isEmpty()) {
            startLoadDataReal(tabItemList);
            return;
        }
        mFeedRepository.getTabsInfo(tabKey)
                .timeout(LOAD_DATA_TIME_LIMIT, TimeUnit.SECONDS)
                .subscribe(new BaseViewModel.BaseGVideoResponseObserver<List<TabItemInfo>>() {
                    @Override
                    protected void onRequestData(List<TabItemInfo> tabItemList) {
                        TabItemDataManager.getInstance().saveTabs(tabItemList, tabKey);
                        startLoadDataReal(tabItemList);
                    }

                    @Override
                    protected void onRequestStart() {
                        showPlaceHolderView(PlaceholderType.LOADING);
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        if (throwable instanceof TimeoutException || throwable instanceof SocketTimeoutException) {
                            AsyncUtils.runOnUIThread(() -> showPlaceHolderView(PlaceholderType.NETWORK_NOT_AVAILABLE));
                            return;
                        }
                        showPlaceHolderView(PlaceholderType.ERROR);
                    }
                });
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    /**
     * 初始化viewpager
     */
    protected void initViewPager() {

        // 网络通信结束后，UI元素可能已经被销导致getChildFragmentManager()抛出异常
        // ava.lang.IllegalStateException: Fragment FeedFragment ... has not been attached yet.
        FragmentManager fragmentManager;
        try {
            fragmentManager = getChildFragmentManager();
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        FragmentPagerItems.Creator creator = FragmentPagerItems.with(getContext());
        TabItemInfo tabItemInfo;
        for (int index = 0; index < mTabItemInfoList.size(); index++) {
            tabItemInfo = mTabItemInfoList.get(index);
            creator.add(tabItemInfo.tabName, getFragmentClass());
        }
        mPagerAdapter = new FeedPageAdapter(fragmentManager, creator.create(), mTabItemInfoList);

        mBinding.feedViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (currentBaseFragment != null) {
                    currentBaseFragment.onTabPauseFragment();
                }
                currentBaseFragment = (BaseFragment) mPagerAdapter.getPage(position);
                if (currentBaseFragment != null) {
                    currentBaseFragment.onTabResumeFragment();
                }
                viewPageChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBinding.feedViewPager.setAdapter(mPagerAdapter);
        mBinding.feedViewPager.setOffscreenPageLimit(mTabItemInfoList.size() - 1);
    }

    protected abstract Class<? extends Fragment> getFragmentClass();

    protected abstract GVideoSmartTabLayout getTabLayout();

    /**
     * 有些场景需要监听ViewPage切换
     *
     * @param position 当前index
     */
    protected abstract void viewPageChange(int position);

    /**
     * TAB、ViewPager刷新结束
     */
    protected abstract void tabViewPagerInitComplete();

    /**
     * 初始化tabLayout
     */
    protected void initTabLayout() {
        getTabLayout().setViewPager(mBinding.feedViewPager);
        if (mTabItemInfoList == null || mTabItemInfoList.size() <= 1) {
            int color = isDarkMode() ? R.color.color_1b1c1f : R.color.color_ffffff;
            getTabLayout().setSelectedIndicatorColors(GVideoRuntime.getAppContext().getResources().getColor(color));
        }
    }

    /**
     * 获取当前tab选择监听器
     *
     * @return tab选择监听器
     */
    protected abstract TabLayout.OnTabSelectedListener getTabSelectedListener();


    /**
     * 初始化tab视图
     *
     * @param tab      单个tab布局
     * @param item     tab数据模型
     * @param position tab位置
     */
    protected abstract void initTabView(TabLayout.Tab tab, TabItemInfo item, int position);


    /**
     * 获取数目
     *
     * @return tab信息list大小
     */
    private int getCount() {
        return mTabItemInfoList.size();
    }

    /**
     * 获取默认选择位置，默认为0
     *
     * @return tab列表默认选择位置
     */
    protected int getDefaultSelectedPosition() {
        return 0;
    }

    protected abstract int getTabKey();

    @Override
    public void showFragmentWithTabId(String tabId) {
        if(mBinding==null
                || mPagerAdapter==null
                || TextUtils.isEmpty(tabId)){
            return;
        }
        int resultIndex = -1;
        for (int index = 0; index < mPagerAdapter.getCount(); index++) {
            BaseFragment baseFragment = (BaseFragment) mPagerAdapter.getItem(index);
            if (!TextUtils.equals(baseFragment.getPid(), tabId)) {
                continue;
            }
            resultIndex = index;
        }
        if(resultIndex<0){
            return;
        }
        mBinding.feedViewPager.setCurrentItem(resultIndex);
    }

    public class FeedPageAdapter extends FragmentPagerItemAdapter {

        private List<TabItemInfo> mTabItemInfoList;

        public FeedPageAdapter(FragmentManager fm, FragmentPagerItems pages, List<TabItemInfo> tabItemInfoList) {
            super(fm, pages);
            mTabItemInfoList = tabItemInfoList;
        }

        @Override
        public Fragment getItem(int position) {
            TabItemInfo tabItemInfo = mTabItemInfoList.get(position);
            Fragment fragment = null;
            Bundle bundle = new Bundle();

            String pageName = tabItemInfo.tabName;
            String pageNameTemp = StatPid.getPageName(getPageName());
            if (TextUtils.isEmpty(pageNameTemp)) {
                pageName = pageNameTemp + "-" + tabItemInfo.tabName;
            }

            if (TextUtils.equals(TabItemDataManager.TAB_RES_TYPE_LINK, tabItemInfo.channelResType)) {
                fragment = PluginManager.get(WebViewPlugin.class).getWebViewFragment(tabItemInfo.url, bundle);
            }

            String tabId = tabItemInfo.tabId;
            // if (TextUtils.equals(tabId, TabItemDataManager.TAB_ID_MOMENT)) {
            //     fragment = PluginManager.get(AccountPlugin.class).getMomentFragment();
            // }

            //推荐
            if (TextUtils.equals(tabId, TabItemDataManager.TAB_ID_HOME_RECOMMEND)) {
                fragment = new HomeRecommendFragment();

            }

            //关注
            if (TextUtils.equals(tabId, TabItemDataManager.TAB_ID_FOLLOW)) {
                fragment = PluginManager.get(AccountPlugin.class).getHomeFollowFragment();
                bundle.putBoolean(EXTRA_IS_FOLLOW_NEED_TOP_SPACE, false);
            }

            //直播
            if (TextUtils.equals(tabId, TabItemDataManager.TAB_ID_HOME_LIVE)) {
                fragment = PluginManager.get(LivePlugin.class).getHomeLiveContentFragment();
                bundle.putString(EXTRA_TAB_ID, tabItemInfo.tabId);
                fragment.setArguments(bundle);
            }

            //看电视
            if (TextUtils.equals(tabId, TabItemDataManager.TAB_ID_WATCH_TV)) {
                fragment = PluginManager.get(WatchTvPlugin.class).getHomeWatchTvFragment();
            }

            if (fragment == null) {
                fragment = super.getItem(position);
            }
            if (fragment instanceof BaseFragment) {
                BaseFragment baseFragment= (BaseFragment) fragment;
                baseFragment.setPageName(pageName);
            }

            fragment.setArguments(bundle);
            if (currentBaseFragment == null) {
                currentBaseFragment = (BaseFragment) fragment;
            }
            return fragment;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            if (object instanceof FeedPageFragment) {
                TabItemInfo tabItemInfo = mTabItemInfoList.get(position);
                if (tabItemInfo == null) {
                    return object;
                }
                ((FeedPageFragment) object).init(
                        tabItemInfo.tabId,
                        tabItemInfo.tabName,
                        isDarkMode()
                );
            }
            return object;
        }
    }

//    @Override
//    protected void onInVisible() {
//        super.onInVisible();
//        if (mPagerAdapter != null) {
//            Fragment fragment;
//            for (int i = 0; i < mPagerAdapter.getCount(); i++) {
//                fragment = mPagerAdapter.getPage(i);
//                if (fragment instanceof MediaPageFragment) {
//                    LogUtils.e(mTabItemInfoList.get(i).tabName+" / "+mTabItemInfoList.get(i).tabId+": onParentInVisible");
//                    ((MediaPageFragment) fragment).onParentInVisible();
//                }
//            }
//        }
//    }

//    @Override protected void onVisible() {
//        super.onVisible();
//        if (mPagerAdapter != null) {
//            Fragment fragment;
//            for (int i = 0; i < mPagerAdapter.getCount(); i++) {
//                fragment = mPagerAdapter.getPage(i);
//                if (fragment instanceof MediaPageFragment) {
//                    LogUtils.e(mTabItemInfoList.get(i).tabName+" / "+mTabItemInfoList.get(i).tabId+": onParentVisible");
//                    ((MediaPageFragment) fragment).onParentVisible();
//                }
//            }
//        }
//    }

    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        if (currentBaseFragment != null) {
            currentBaseFragment.onTabPauseFragment();
        }
    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        if (currentBaseFragment != null) {
            currentBaseFragment.onTabResumeFragment();
        }
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if (currentBaseFragment != null) {
            currentBaseFragment.onFragmentPause();
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (currentBaseFragment != null) {
            currentBaseFragment.onFragmentResume();
        }
    }
}
