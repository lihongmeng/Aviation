package com.hzlz.aviation.feature.community.fragment;

import static com.hzlz.aviation.kernel.base.Constant.EXTRA_GROUP_ID;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.FragmentCircleFamousBinding;
import com.hzlz.aviation.feature.community.viewmodel.CircleFamousViewModel;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;

public class CircleFamousFragment extends BaseFragment<FragmentCircleFamousBinding> {

    private CircleFamousViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_circle_famous;
    }

    @Override
    protected void initView() {
        //ImmersiveUtils.enterImmersive(this, ResourcesUtils.getColor(R.color.color_f2f2f2), true);
        PluginManager.get(AccountPlugin.class).addDestinations(this);
        setToolbarTitle(getString(R.string.circle_famous));
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(CircleFamousViewModel.class);
        mBinding.setViewModel(viewModel);

        Bundle arguments = getArguments();
        if (arguments == null) {
            finishActivity();
            return;
        }
        viewModel.groupId = arguments.getLong(EXTRA_GROUP_ID, -1);

        viewModel.mAutoRefreshLiveData.observe(
                this,
                o -> viewModel.onRefresh(mBinding.refreshLayout)
        );
    }

    @Override
    protected void loadData() {

        // 检测登录状态
        viewModel.checkNetworkAndLoginStatus();
    }


    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    public void onReload(@NonNull View view) {
        if (viewModel != null) {
            viewModel.onRefresh(mBinding.refreshLayout);
        }
    }

    @Override
    public String getPid() {
        return StatPid.CIRCLE_FAMOUS;
    }

}
