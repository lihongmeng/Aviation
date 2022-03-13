package com.jxntv.account.ui.follow;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.NEED_UPDATE_HOME_FOLLOW_WHOLE_PAGE;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.REFRESH_PAGE_BECAUSE_PUBLISH;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.START_SCAN_HOME;
import static com.jxntv.base.Constant.EXTRA_IS_FOLLOW_NEED_TOP_SPACE;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentHomeFollowBinding;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.FeedPlugin;
import com.jxntv.base.plugin.SearchPlugin;
import com.jxntv.base.utils.WidgetUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;

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
                        // 如果followCount小于0，说明没有关注别人，但是自己发布了内容
                        // 还是需要显示信息流列表
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

        // 发布成功后会收到通知，更新关注页
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
            //因为当前生命周期交给子fragment处理，在第一次创建时需要手动调用，保证埋点准确性
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
