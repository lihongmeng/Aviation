package com.hzlz.aviation.feature.live.ui.homelive;

import static com.hzlz.aviation.kernel.base.Constant.EXTRA_TAB_ID;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.MEET;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.NORMAL;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.SPRING_FESTIVAL;
import static com.hzlz.aviation.kernel.stat.stat.StatPid.HOME_LIVE;
import static com.hzlz.aviation.kernel.stat.stat.StatPid.LIVE;
import static com.hzlz.aviation.kernel.stat.stat.StatPid.WATCH_TV;
import static com.hzlz.aviation.kernel.stat.stat.StatPid.WEB_LINK;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.hzlz.aviation.feature.live.databinding.FragmentHomeLiveBinding;
import com.hzlz.aviation.feature.live.ui.homelive.homelive.HomeLiveContentFragment;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.adapter.BaseFragmentVpAdapter;
import com.hzlz.aviation.kernel.base.datamanager.ThemeDataManager;
import com.hzlz.aviation.kernel.base.model.feed.TabItemInfo;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.WatchTvPlugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.utils.MeetRedUtils;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.feature.live.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页的直播Tab页面
 */
public class HomeLiveFragment extends BaseFragment<FragmentHomeLiveBinding> {

    private List<BaseFragment> baseFragmentList;

    private BaseFragmentVpAdapter fragmentVpAdapter;

    private BaseFragment currentFragment;

    private HomeLiveViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_live;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initImmersive() {
        ViewGroup.LayoutParams layoutParams = mBinding.viewPagerTab.getLayoutParams();
        layoutParams.height += WidgetUtils.getStatusBarHeight();
        mBinding.viewPagerTab.setLayoutParams(layoutParams);
        mBinding.viewPagerTab.setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
    }

    @Override
    protected void initView() {

        setupPlaceholderLayout(R.id.empty_container);

        mBinding.viewPagerTab.setOnTabClickListener(position -> mBinding.viewPager.setCurrentItem(position));
        mBinding.viewPagerTab.setSelectSize(18);
        mBinding.viewPagerTab.setUnSelectSize(16);

        baseFragmentList = new ArrayList<>();
        fragmentVpAdapter = new BaseFragmentVpAdapter(getChildFragmentManager());
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBinding.viewPagerTab.updateIndex(position);

                if (currentFragment != null) {
                    currentFragment.onTabPauseFragment();
                }
                currentFragment = baseFragmentList.get(position);
                currentFragment.onTabResumeFragment();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBinding.viewPager.setAdapter(fragmentVpAdapter);

        updateTheme();
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CHANGE_THEME_COLOR).observe(
                this, o -> updateTheme()
        );
    }

    private void updateTheme() {
        int themeColorSwitchInteger = ThemeDataManager.getInstance().getThemeColorSwitchInteger();
        switch (themeColorSwitchInteger) {
            case MEET:
                MeetRedUtils.updateToRed(mBinding.viewPagerTab);
                break;
            case SPRING_FESTIVAL:
                MeetRedUtils.updateToSpringFestival(mBinding.viewPagerTab);
                break;
            case NORMAL:
            default:
                MeetRedUtils.updateToNormal(mBinding.viewPagerTab);
                break;
        }
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(HomeLiveViewModel.class);
        mBinding.setViewModel(viewModel);

        viewModel.refreshSuccess.observe(
                this,
                refreshSuccess -> {
                    if (!refreshSuccess) {
                        if (viewModel.tabItemInfoArrayList == null || viewModel.tabItemInfoArrayList.isEmpty()) {
                            mBinding.viewPagerTab.setVisibility(View.GONE);
                            mBinding.viewPager.setVisibility(View.GONE);
                            updatePlaceholderLayoutType(viewModel.failReason);
                        }
                        return;
                    }

                    mBinding.viewPagerTab.updateDataSourceWithTabList(viewModel.tabItemInfoArrayList);

                    baseFragmentList = new ArrayList<>();

                    for (TabItemInfo tabItemInfo : viewModel.tabItemInfoArrayList) {
                        if (tabItemInfo == null) {
                            continue;
                        }
                        String pageName = StatPid.getPageName(getPageName()) + "-" + tabItemInfo.tabName;
                        BaseFragment baseFragment = null;
                        if (WEB_LINK.equals(tabItemInfo.channelResType)) {
                            baseFragment = PluginManager.get(WebViewPlugin.class).getWebViewFragment(tabItemInfo.url, new Bundle());
                        } else {
                            switch (tabItemInfo.tabId) {
                                case HOME_LIVE:
                                    baseFragment = new HomeLiveContentFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(EXTRA_TAB_ID, tabItemInfo.tabId);
                                    baseFragment.setArguments(bundle);
                                    break;
                                case WATCH_TV:
                                    baseFragment = PluginManager.get(WatchTvPlugin.class).getHomeWatchTvFragment();
                            }
                        }
                        if (baseFragment != null) {
                            baseFragmentList.add(baseFragment);
                            baseFragment.setPageName(pageName);
                        }
                    }
                    fragmentVpAdapter.updateSource(baseFragmentList);
                    mBinding.viewPager.setCurrentItem(0);
                    currentFragment = baseFragmentList.get(0);

                    if (baseFragmentList == null || baseFragmentList.isEmpty()) {
                        mBinding.viewPagerTab.setVisibility(View.GONE);
                        mBinding.viewPager.setVisibility(View.GONE);
                        updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                    } else {
                        mBinding.viewPagerTab.setVisibility(View.VISIBLE);
                        mBinding.viewPager.setVisibility(View.VISIBLE);
                        updatePlaceholderLayoutType(PlaceholderType.NONE);
                    }

                }
        );

    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        if (currentFragment != null) {
            currentFragment.onTabResumeFragment();
        }
    }

    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        if (currentFragment != null) {
            currentFragment.onTabPauseFragment();
        }
    }

    @Override
    protected void loadData() {
        mBinding.viewPagerTab.setVisibility(View.GONE);
        mBinding.viewPager.setVisibility(View.GONE);
        updatePlaceholderLayoutType(PlaceholderType.LOADING);
        viewModel.loadData(false);
    }

    @Override
    public void onReload(@NonNull View view) {
        loadData();
    }

    @Override
    public String getPid() {
        return LIVE;
    }

    @Override
    protected void onVisible() {
        super.onVisible();

        if (currentFragment != null) {
            currentFragment.onFragmentResume();
        }
    }
}
