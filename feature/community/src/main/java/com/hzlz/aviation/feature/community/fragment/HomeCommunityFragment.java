package com.hzlz.aviation.feature.community.fragment;

import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.CHANGE_THEME_COLOR;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.MEET;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.NORMAL;
import static com.hzlz.aviation.kernel.base.Constant.THEME_COLOR_SWITCH_TOP.SPRING_FESTIVAL;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.hzlz.aviation.feature.community.CirclePluginImpl;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.FragmentCircleBinding;
import com.hzlz.aviation.feature.community.viewmodel.HomeCommunityViewModel;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.StaticParams;
import com.hzlz.aviation.kernel.base.adapter.BaseFragmentVpAdapter;
import com.hzlz.aviation.kernel.base.datamanager.ThemeDataManager;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.model.circle.CircleJoinStatus;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.utils.MeetRedUtils;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页社区板块
 */
public final class HomeCommunityFragment extends BaseFragment<FragmentCircleBinding> {

    // ViewModel
    private HomeCommunityViewModel viewModel;

    // 当前ViewPager中的Fragment
    private List<BaseFragment> baseFragmentList;

    // 当前正在展示的Fragment
    private BaseFragment currentFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle;
    }

    @Override
    protected void initImmersive() {
        // 首页是全屏状态，顶部组件需要留出状态栏的高度
        ViewGroup.LayoutParams layoutParams = mBinding.tabLayout.getLayoutParams();
        int statusBarHeight = WidgetUtils.getStatusBarHeight();
        layoutParams.height += statusBarHeight;
        mBinding.tabLayout.setLayoutParams(layoutParams);
        mBinding.tabLayout.setPadding(0, statusBarHeight, 0, 0);
        mBinding.messageLayout.setPadding(0, statusBarHeight, 0, 0);
    }

    @Override
    protected void initView() {
        setupPlaceholderLayout(R.id.empty_container);
        mBinding.tabLayout.setSelectSize(20);
        mBinding.tabLayout.setUnSelectSize(16);
        mBinding.messageLayout.setPageName(getPageName());

        int hasUnread = PluginManager.get(FeedPlugin.class).getUnreadCount();
        mBinding.messageLayout.setUnreadCount(hasUnread);

        // 更新主题相关
        updateTheme();
        GVideoEventBus.get(CHANGE_THEME_COLOR).observe(this, o -> updateTheme());
    }

    private void updateTheme() {
        int themeColorSwitchInteger = ThemeDataManager.getInstance().getThemeColorSwitchInteger();
        switch (themeColorSwitchInteger) {
            case MEET:
                MeetRedUtils.updateToRed(mBinding.tabLayout, mBinding.messageLayout);
                break;
            case SPRING_FESTIVAL:
                MeetRedUtils.updateToSpringFestival(mBinding.tabLayout, mBinding.messageLayout);
                break;
            case NORMAL:
            default:
                MeetRedUtils.updateToNormal(mBinding.tabLayout, mBinding.messageLayout);
                break;
        }
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(HomeCommunityViewModel.class);
        mBinding.setViewModel(viewModel);

        viewModel.myCircle.observe(
                this,
                myCircle -> {

                    // 没有加入任何社区
                    if (myCircle == null
                            || myCircle.groupList == null
                            || myCircle.groupList.isEmpty()) {

                        // 如果当前ViewPager只有一个子元素，直接通知其更新即可
                        if (viewModel.circleVpAdapter != null && viewModel.circleVpAdapter.getCount() == 1) {
                            GVideoEventBus.get(CirclePluginImpl.UPDATE_FIND_CIRCLE_LIST).post(null);
                            return;
                        }
                        // 否则直接重构页面
                        else {
                            showOneTab();
                        }
                        return;
                    }

                    // 如果当前ViewPager有两个子元素，显示该页，并通知其更新
                    if (viewModel.circleVpAdapter != null && viewModel.circleVpAdapter.getCount() == 2) {
                        int currentItem = mBinding.viewpager.getCurrentItem();

                        // circleFragmentInitIndex是刷新前用户选择的页数，先还原到该页
                        if (currentItem != StaticParams.circleFragmentInitIndex) {
                            mBinding.viewpager.setCurrentItem(StaticParams.circleFragmentInitIndex);
                            StaticParams.circleFragmentInitIndex = 0;
                        }
                        if (currentItem != 0) {
                            GVideoEventBus.get(CirclePluginImpl.UPDATE_FIND_CIRCLE_LIST).post(null);
                        }
                    }

                    // 否则直接重构页面
                    else {
                        showTwoTab();
                    }

                }
        );

    }

    public void showOneTab() {
        final List<String> titleStringList = new ArrayList<>();
        titleStringList.add(getString(R.string.find_circle));
        mBinding.tabLayout.updateDataSourceWithStringList(titleStringList);

        baseFragmentList = new ArrayList<>();
        baseFragmentList.add(new HomeFindCommunityFragment());
        viewModel.circleVpAdapter = new BaseFragmentVpAdapter(getChildFragmentManager());
        viewModel.circleVpAdapter.updateSource(baseFragmentList);
        mBinding.viewpager.setAdapter(viewModel.circleVpAdapter);
        mBinding.viewpager.setVisibility(View.VISIBLE);
        currentFragment = baseFragmentList.get(0);
        currentFragment.onTabResumeFragment();
    }

    public void showTwoTab() {
        mBinding.viewpager.removeAllViews();

        // 初始化Tab
        final List<String> titleStringList = new ArrayList<>();
        titleStringList.add(getString(R.string.my_circle));
        titleStringList.add(getString(R.string.find_circle));
        mBinding.tabLayout.updateDataSourceWithStringList(titleStringList);
        mBinding.tabLayout.setOnTabClickListener(position -> {
            mBinding.viewpager.setCurrentItem(position);
            StaticParams.circleFragmentInitIndex = position;
        });
        mBinding.viewpager.removeAllViews();

        // 初始化ViewPager
        baseFragmentList = new ArrayList<>();
        baseFragmentList.add(new HomeMyCommunityFragment());
        baseFragmentList.add(new HomeFindCommunityFragment());
        viewModel.circleVpAdapter = new BaseFragmentVpAdapter(getChildFragmentManager());
        viewModel.circleVpAdapter.updateSource(baseFragmentList);
        mBinding.viewpager.setAdapter(viewModel.circleVpAdapter);
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBinding.tabLayout.updateIndex(position);
                StaticParams.circleFragmentInitIndex = position;

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
        mBinding.viewpager.setCurrentItem(StaticParams.circleFragmentInitIndex);
        currentFragment = baseFragmentList.get(StaticParams.circleFragmentInitIndex);
        currentFragment.onTabResumeFragment();
        mBinding.viewpager.setVisibility(View.VISIBLE);
        StaticParams.circleFragmentInitIndex = 0;
    }

    @Override
    protected void loadData() {
        initListener();
        viewModel.initData();
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    public void onTabResumeFragment() {
        super.onTabResumeFragment();
        if (getContext() != null) {
            ImmersiveUtils.setStatusBarIconColor(this, true);
        }
        if (currentFragment != null && currentFragment.isAdded()) {
            currentFragment.onTabResumeFragment();
        }
        if (viewModel == null) {
            return;
        }
        viewModel.initData();
    }

    @Override
    public void onTabPauseFragment() {
        super.onTabPauseFragment();
        if (currentFragment != null) {
            mBinding.viewpager.post(() -> currentFragment.onTabPauseFragment());
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (currentFragment instanceof HomeMyCommunityFragment) {
            getChildFragmentManager().executePendingTransactions();
            if (currentFragment.isAdded() && !currentFragment.isDetached() && !currentFragment.isRemoving()) {
                currentFragment.onFragmentResume();
            }
        } else if (currentFragment != null) {
            mBinding.viewpager.post(() -> currentFragment.onFragmentResume());
        }
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if (currentFragment != null) {
            mBinding.viewpager.post(() -> currentFragment.onFragmentPause());
        }
    }

    private void initListener() {

        // 登录
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(
                this,
                o -> {
                    if (viewModel == null) {
                        return;
                    }
                    viewModel.initData();
                }

        );

        // 登出
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observe(
                this,
                o -> {
                    if (viewModel == null) {
                        return;
                    }
                    viewModel.initData();
                }

        );

        // 社区加入、退出状态改变
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.CIRCLE_JOIN_EXIT_CHANGE, CircleJoinStatus.class)
                .observe(this, circleJoinStatus -> {
                    if (viewModel == null) {
                        return;
                    }
                    viewModel.initData();
                });

        // 未读消息通知
        GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION, Integer.class)
                .observe(this, count -> mBinding.messageLayout.setUnreadCount(count));
    }

    @Override
    public String getPid() {
        return StatPid.PID_CIRCLE;
    }

}
