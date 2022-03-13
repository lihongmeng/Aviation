package com.jxntv.circle.fragment;

import static com.jxntv.base.Constant.EXTRA_GROUP_ID;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.FragmentCircleFamousBinding;
import com.jxntv.circle.viewmodel.CircleFamousViewModel;
import com.jxntv.ioc.PluginManager;
import com.jxntv.stat.StatPid;

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
