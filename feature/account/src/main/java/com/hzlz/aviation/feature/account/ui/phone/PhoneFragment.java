package com.hzlz.aviation.feature.account.ui.phone;

import static android.view.View.GONE;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.ALREADY_START_LOGIN;
import static com.hzlz.aviation.kernel.base.Constant.LOGIN_TYPE.SMS_CODE_LOGIN;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentPhoneBinding;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.StaticParams;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.ioc.PluginManager;

/**
 * 手机号界面
 *
 * @since 2020-01-14 09:53
 */
@SuppressWarnings("FieldCanBeLocal")
public final class PhoneFragment extends BaseFragment<FragmentPhoneBinding> {

    private PhoneViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_phone;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initView() {
        GVideoEventBus.get(ALREADY_START_LOGIN).post(SMS_CODE_LOGIN);
        // ImmersiveUtils.enterImmersive(this, ResourcesUtils.getColor(R.color.color_ffffff), true);
        openSoftKeyBoardDelay(mBinding.editTextPhoneNumber);
        PluginManager.get(WebViewPlugin.class).addWebViewDestination(this);

        if (StaticParams.isForeLogin) {
            mBinding.imageViewBack.setVisibility(GONE);
        } else {
            mBinding.imageViewBack.setVisibility(View.VISIBLE);
        }

        if (isFullTransparent()) {
            ViewGroup.LayoutParams layoutParams = mBinding.topBlank.getLayoutParams();
            layoutParams.height += WidgetUtils.getStatusBarHeight();
            mBinding.topBlank.setLayoutParams(layoutParams);
        }

    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(PhoneViewModel.class);
        mBinding.setViewModel(viewModel);
        mBinding.setBinding(viewModel.getDataBinding());

        viewModel.getFinishActivityLiveData().observe(this, o -> requireActivity().finish());

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN, Integer.class).observe(
                this,
                value -> {
                    if(value==null||value==SMS_CODE_LOGIN){
                        return;
                    }
                    finishActivity();
                }
        );
    }

    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            int type = PhoneFragmentArgs.fromBundle(arguments).getType();
            viewModel.setType(type);
        }
    }

    @Override
    public void onDestroyView() {
        closeSoftKeyboard();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentPause();
    }

    @Override
    public String getPid() {
        return StatPid.LOGIN;
    }

}
