package com.jxntv.account.ui.sms;

import static android.view.View.VISIBLE;
import static com.jxntv.base.Constant.SP_LOGIN_HAS_VERIFICATE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentSmsCodeBinding;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.StaticParams;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.sharedprefs.KernelSharedPrefs;
import com.jxntv.ioc.PluginManager;
import com.jxntv.stat.StatPid;

/**
 * 短信验证码界面
 *
 * @since 2020-01-14 09:55
 */
@SuppressWarnings("FieldCanBeLocal")
public final class LoginCodeFragment extends BaseFragment<FragmentSmsCodeBinding> {

    // <editor-folder desc="实例变量">

    private LoginCodeViewModel viewModel;

    // </editor-folder>

    // <editor-folder desc="自定义Override方法">

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sms_code;
    }

    @Override
    protected void initView() {
        // ImmersiveUtils.enterImmersive(this, ResourcesUtils.getColor(R.color.color_ffffff), true);

        mBinding.textViewTitle.setText(getString(R.string.input_verification_code));
        mBinding.textViewSmsCodeSent.setVisibility(VISIBLE);
        mBinding.editTextSmsCode.setHint(getString(R.string.fragment_sms_code_hint));
        mBinding.textObtainSmsCode.setVisibility(VISIBLE);

        mBinding.editTextSmsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.smsCodeChange((s == null ? "" : s.toString()), mBinding.buttonConfirm);
            }
        });

        openSoftKeyBoard(mBinding.editTextSmsCode);

    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(LoginCodeViewModel.class);
        mBinding.setViewModel(viewModel);
        mBinding.setBinding(viewModel.getDataBinding());

        // viewModel.showInviteCode.observe(
        //         this,
        //         showInviteCode -> {
        //             if (showInviteCode == null) {
        //                 showInviteCode = false;
        //             }
        //
        //             mBinding.textViewTitle.setText(showInviteCode ? getString(R.string.input_invite_code) : getString(R.string.input_verification_code));
        //
        //             mBinding.textViewSmsCodeSent.setVisibility(showInviteCode ? GONE : VISIBLE);
        //
        //             mBinding.editTextSmsCode.setHint(showInviteCode ? getString(R.string.fragment_invite_code_hint) : getString(R.string.fragment_sms_code_hint));
        //
        //             mBinding.textObtainSmsCode.setVisibility(showInviteCode ? GONE : VISIBLE);
        //
        //         }
        // );
        // Bundle argument = getArguments();
        // if (argument == null) {
        //     return;
        // }
        // viewModel.hasVerificate = KernelSharedPrefs.getInstance().getBoolean(SP_LOGIN_HAS_VERIFICATE, false);
    }

    @Override
    protected void loadData() {
        // 获取传递过来的参数
        Bundle argument = getArguments();
        if (argument != null) {
            viewModel.init(LoginCodeFragmentArgs.fromBundle(argument));
        }

        if (StaticParams.isForeLogin) {
            PluginManager.get(AccountPlugin.class).addDestinations(this);
        }
    }

    // </editor-folder>

    // <editor-folder desc="Android Override方法">

    @Override
    public void onDestroyView() {
        closeSoftKeyboard();
        super.onDestroyView();
    }

    @Override
    public String getPid() {
        return StatPid.VERIFY_CODE_LOGIN;
    }

    // </editor-folder>

}
