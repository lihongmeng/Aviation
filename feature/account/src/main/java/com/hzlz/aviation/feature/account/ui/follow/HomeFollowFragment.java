package com.hzlz.aviation.feature.account.ui.follow;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.NEED_UPDATE_HOME_FOLLOW_WHOLE_PAGE;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.REFRESH_PAGE_BECAUSE_PUBLISH;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.START_SCAN_HOME;
import static com.hzlz.aviation.kernel.base.Constant.EXTRA_IS_FOLLOW_NEED_TOP_SPACE;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentHomeFollowBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.FeedPlugin;
import com.hzlz.aviation.kernel.base.plugin.SearchPlugin;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;

public class HomeFollowFragment extends BaseFragment<FragmentHomeFollowBinding> {

    private HomeFollowViewModel viewModel;
    private BaseFragment currentFragment;
    private boolean isFollowNeedTopSpace;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        isFollowNeedTopSpace = arguments == null || arguments.getBoolean(EXTRA_IS_FOLLOW_NEED_TOP_SPACE, true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_follow;
    }

    @Override
    protected void initView() {
        mBinding.homeTab.gvideoTabs.setVisibility(GONE);
        mBinding.homeTab.searchSpace.setVisibility(VISIBLE);
        mBinding.homeTab.topBarSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GVideoSensorDataManager.getInstance().clickSearchEditText();
                PluginManager.get(SearchPlugin.class).navigateToSearchActivity(getContext());
            }
        });
        setupPlaceholderLayout(R.id.empty_container);

        mBinding.homeTab.homeTabContainer.setVisibility(isFollowNeedTopSpace ? VISIBLE : GONE);
        if (isFollowNeedTopSpace) {
            mBinding.homeTab.scan.setOnClickListener((View.OnClickListener) v -> GVideoEventBus.get(START_SCAN_HOME).post(null));
        }

        mBinding.homeTab.messageLayout.setPageName(getPageName());
    }

    @Override
    public String getPid() {
        return StatPid.HOME_FOLLOW;
    }

    @Override
    protected boolean enableLazyLoad() {
        return true;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(HomeFollowViewModel.class);
        viewModel.followCount.observe(
                this,
                followCount -> {
                    if (viewModel.forceShowNoData) {
                        clearAllFragment();
                    } else {
                        // ??????followCount??????0?????????????????????????????????????????????????????????
                        // ?????????????????????????????????
                        if (followCount == null || followCount == 0) {
                            showFollowList();
                        } else {
                            showFollowContentList(followCount < 0);
                        }
                    }
                }
        );

        viewModel.initData();
    }


    @Override
    protected void loadData() {
        GVideoEventBus.get(AccountPlugin.EVENT_SHOW_FOLLOW_CONTENT_LIST).observe(
                this,
                o -> showFollowContentList(false)
        );
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(
                this,
                o -> {
                    if (viewModel == null) {
                        return;
                    }
                    viewModel.initData();
                }
        );
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).observe(
                this,
                o -> {
                    if (viewModel == null) {
                        return;
                    }
                    viewModel.initData();
                }

        );
        GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION, Integer.class)
                .observe(this, hasUnread -> {
                    PluginManager.get(FeedPlugin.class).setUnreadCount(hasUnread);
                    mBinding.homeTab.messageLayout.setUnreadCount(hasUnread);
                });

        GVideoEventBus.get(NEED_UPDATE_HOME_FOLLOW_WHOLE_PAGE).observe(
                this,
                o -> {
                    if (viewModel == null) {
                        return;
                    }
                    viewModel.initData();
                }
        );

        // ????????????????????????????????????????????????
        GVideoEventBus.get(REFRESH_PAGE_BECAUSE_PUBLISH, String.class).observe(
                this,
                startPublishFrom -> {
                    if (mBinding == null
                            || viewModel == null) {
                        return;
                    }
                    if (currentFragment != null && currentFragment instanceof HomeFollowContentListFragment) {
                        ((HomeFollowContentListFragment) currentFragment).loadData();
                    } else {
                        viewModel.initData();
                    }
                }
        );

    }

    private void showFollowContentList(boolean isOnlyMe) {
        if (currentFragment == null || !(currentFragment instanceof HomeFollowContentListFragment)) {
            currentFragment = new HomeFollowContentListFragment(isOnlyMe);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, currentFragment)
                    .commitAllowingStateLoss();
            mBinding.content.setVisibility(VISIBLE);
            //?????????????????????????????????fragment????????????????????????????????????????????????????????????????????????
            currentFragment.onTabResumeFragment();
        }
    }

    private void showFollowList() {
        if (currentFragment == null || !(currentFragment instanceof HomeFollowListFragment)) {
            currentFragment = new HomeFollowListFragment();
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, currentFragment)
                    .commitAllowingStateLoss();
            mBinding.content.setVisibility(VISIBLE);
            currentFragment.onTabResumeFragment();
        }
    }

    private void clearAllFragment() {
        currentFragment = null;
        mBinding.content.removeAllViews();
        mBinding.content.setVisibility(GONE);
    }

    @Override
    public void onTabResumeFragment() {
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
    protected void initImmersive() {
        if (isFollowNeedTopSpace) {
            mBinding.getRoot().setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
        }
    }

    @Override
    public void onTabPauseFragment() {
        if (currentFragment != null) {
            currentFragment.onTabPauseFragment();
        }
    }

    @Override
    public void onReload(@NonNull View view) {
        super.onReload(view);
        if (viewModel == null) {
            return;
        }
        viewModel.initData();
    }

    @Override
    public void onFragmentResume() {
        if (currentFragment != null) {
            currentFragment.onFragmentResume();
        }
    }

    @Override
    public void onFragmentPause() {
        if (currentFragment != null) {
            currentFragment.onFragmentPause();
        }
    }
}
