package com.jxntv.test.environment;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.test.R;
import com.jxntv.test.databinding.FragmentEnvironmentBinding;
import com.jxntv.utils.AppManager;

/**
 * 切换环境界面
 *
 * @since 2020-03-04 10:36
 */
public final class EnvironmentFragment extends BaseFragment<FragmentEnvironmentBinding> {
    //<editor-fold desc="属性">
    private EnvironmentViewModel mEnvironmentViewModel;
    //</editor-fold>

    //<editor-fold desc="方法实现">
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_environment;
    }

    @Override
    protected void initView() {
        setToolbarTitle(R.string.switch_environment);
        showRightOperationTextView(true);
        setRightOperationTextViewText("切换");
    }

    @Override
    protected void bindViewModels() {
        mEnvironmentViewModel = bingViewModel(EnvironmentViewModel.class);
        mBinding.setViewModel(mEnvironmentViewModel);
        mEnvironmentViewModel.getEnableSwitchLiveData().observe(this, new NotNullObserver<Boolean>() {
            @Override
            protected void onModelChanged(@NonNull Boolean enable) {
                enableRightOperationTextView(enable);
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onRightOperationPressed(@NonNull View view) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        super.onRightOperationPressed(view);
        mEnvironmentViewModel.switchEnvironment(view);
    }
    //</editor-fold>
}
