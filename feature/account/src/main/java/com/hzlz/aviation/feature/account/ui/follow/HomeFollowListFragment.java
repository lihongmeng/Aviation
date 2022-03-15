package com.hzlz.aviation.feature.account.ui.follow;

import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.adapter.HomeFollowAdapter;
import com.hzlz.aviation.feature.account.databinding.FragmentHomeFollowListBinding;
import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;

public class HomeFollowListFragment extends BaseFragment<FragmentHomeFollowListBinding> {

    private HomeFollowListViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_follow_list;
    }

    @Override
    public String getPid() {
        return StatPid.HOME_FOLLOW_LIST;
    }

    @Override
    protected void initView() {
        mBinding.batchReplace.setOnClickListener(view -> viewModel.onBatchReplaceClick(mBinding.refreshLayout));
        mBinding.oneKeyFollowComplete.setOnClickListener(oneKeyFollowCompleteListener);
        mBinding.refreshLayout.setEnableLoadMore(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        bindViewModels();
        loadData();
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
        viewModel = bingViewModel(HomeFollowListViewModel.class);
        mBinding.setViewModel(viewModel);

        viewModel.adapter.setListener(listener);

        viewModel.mAutoRefreshLiveData.observe(
                this,
                mAutoRefreshLiveData -> {
                    viewModel.updateRecommendFollowList();
                    viewModel.onRefresh(mBinding.refreshLayout);
                }
        );

        viewModel.hasFollowAtLeastOne.observe(
                this,
                hasFollowAtLeastOne -> {
                    if (mBinding == null) {
                        return;
                    }
                    if (hasFollowAtLeastOne) {
                        mBinding.oneKeyFollowComplete.setText(R.string.complete);
                        mBinding.oneKeyFollowComplete.setOnClickListener(
                                v -> GVideoEventBus.get(AccountPlugin.EVENT_SHOW_FOLLOW_CONTENT_LIST).post(null)
                        );
                    } else {
                        mBinding.oneKeyFollowComplete.setText(R.string.one_key_follow_high_quality_author);
                        mBinding.oneKeyFollowComplete.setOnClickListener(oneKeyFollowCompleteListener);
                    }
                }
        );

    }

    private final View.OnClickListener oneKeyFollowCompleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!UserManager.hasLoggedIn()) {
                AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
                if (plugin != null) {
                    plugin.startLoginActivity(view.getContext());
                }
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(getPageName()),
                        getString(R.string.one_key_follow)
                );
                return;
            }
            viewModel.onKeyFollow();
        }
    };

    @Override
    protected void loadData() {

        // 检测登录状态
        viewModel.checkNetworkAndLoginStatus();
    }

    @Override
    public void onReload(@NonNull View view) {
        if (viewModel != null) {
            viewModel.onRefresh(mBinding.refreshLayout);
        }
    }

    private final HomeFollowAdapter.Listener listener = (view, author) -> {
        if (author == null) {
            showToast(getString(R.string.join_failed));
            return;
        }
        if (!UserManager.hasLoggedIn()) {
            AccountPlugin plugin = PluginManager.get(AccountPlugin.class);
            if (plugin != null) {
                plugin.startLoginActivity(view.getContext());
            }
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPageName()),
                    getString(R.string.follow)
            );
            return;
        }
        viewModel.follow(author);
    };
}
