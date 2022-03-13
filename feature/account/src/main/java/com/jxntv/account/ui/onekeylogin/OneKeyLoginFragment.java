package com.jxntv.account.ui.onekeylogin;

import static com.jxntv.base.Constant.EVENT_BUS_EVENT.ALREADY_START_LOGIN;
import static com.jxntv.base.Constant.LOGIN_TYPE.ONE_KEY_LOGIN;

import com.jxntv.account.R;
import com.jxntv.account.databinding.FragmentOneKeyLoginBinding;
import com.jxntv.account.ui.ugc.OneKeyLoginViewModel;
import com.jxntv.account.utils.oneKeyLogin.OneKeyLoginUtils;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.Constant;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.LogUtils;

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
