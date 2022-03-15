package com.hzlz.aviation.feature.account.ui.onekeylogin;

import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.ALREADY_START_LOGIN;
import static com.hzlz.aviation.kernel.base.Constant.LOGIN_TYPE.ONE_KEY_LOGIN;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.databinding.FragmentOneKeyLoginBinding;
import com.hzlz.aviation.feature.account.ui.ugc.OneKeyLoginViewModel;
import com.hzlz.aviation.feature.account.utils.oneKeyLogin.OneKeyLoginUtils;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.util.LogUtils;

public class OneKeyLoginFragment extends BaseFragment<FragmentOneKeyLoginBinding> {

    private OneKeyLoginViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_one_key_login;
    }

    @Override
    protected void initView() {
        GVideoEventBus.get(ALREADY_START_LOGIN).post(ONE_KEY_LOGIN);
    }

    @Override
    public String getPid() {
        return StatPid.ONE_KEY_LOGIN;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void bindViewModels() {
        viewModel = bingViewModel(OneKeyLoginViewModel.class);

        viewModel.finish.observe(this, o -> finishActivity());

        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN, Integer.class).observe(
                this,
                value -> {
                    if (value == null || value == ONE_KEY_LOGIN) {
                        return;
                    }
                    finishActivity();
                }
        );
    }

    @Override
    protected void loadData() {
        OneKeyLoginUtils.getInstance().startOneKeyLoginReal(getActivity(), new OneKeyLoginUtils.ResultListener() {
            @Override
            public void showLoading(String content) {

            }

            @Override
            public void hideLoading() {

            }

            @Override
            public void loginSuccess(String token) {
                LogUtils.d("token -->>" + token);
                if (viewModel != null && mBinding != null) {
                    viewModel.startLogin(mBinding.root, token, getArguments());
                }
            }

            @Override
            public void userCancel() {
                finishActivity();
            }

            @Override
            public void programException(String exception) {
                showToast(exception);
                finishActivity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.resumeTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.stayTime = System.currentTimeMillis() - viewModel.resumeTime;
    }

    @Override
    protected String getSourcePage() {
        return getStringDataFromBundle(Constant.EXTRA_FROM_PID);
    }

}
