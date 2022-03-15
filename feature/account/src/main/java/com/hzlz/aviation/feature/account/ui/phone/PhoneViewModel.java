package com.hzlz.aviation.feature.account.ui.phone;

import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.model.annotation.SmsCodeType;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.StaticParams;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.plugin.WebViewPlugin;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;

/**
 * 手机号界面 ViewModel
 *
 * @since 2020-01-14 10:31
 */
public final class PhoneViewModel extends BaseViewModel {
    //<editor-fold desc="属性">

    @NonNull
    private PhoneDataBinding mDataBinding = new PhoneDataBinding();

    private MutableLiveData<Object> mFinishActivityLiveData = new MutableLiveData<>();

    @SmsCodeType
    private int mSmsType;

    //</editor-fold>

    //<editor-fold desc="构造函数">

    public PhoneViewModel(@NonNull Application application) {
        super(application);
        mSmsType = SmsCodeType.LOGIN;
    }

    //</editor-fold>

    @NonNull
    PhoneDataBinding getDataBinding() {
        return mDataBinding;
    }

    @NonNull
    LiveData<Object> getFinishActivityLiveData() {
        return mFinishActivityLiveData;
    }

    //<editor-fold desc="API">

    public void setType(int type) {
        mSmsType = type;
        mDataBinding.setType(type);
    }

    //</editor-fold>

    //<editor-fold desc="数据绑定">

    public void back(@NonNull View view) {
        if (StaticParams.isForeLogin) {
            StaticParams.isForeLogin = false;
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        if (!Navigation.findNavController(view).popBackStack()) {
            mFinishActivityLiveData.postValue(null);
        }
    }

    /**
     * 选择国家代码
     *
     * @param view 被点击的控件
     */
    public void selectCountryCode(@NonNull View view) {
        // 暂不处理多国家代码逻辑
        //Navigation.findNavController(view).navigate(R.id.action_phone_to_country_code);
    }

    /**
     * 下一步
     *
     * @param view 被点击的控件
     */
    public void startLoginCodeFragment(@NonNull View view) {
        if (!mDataBinding.isNumberValid()) {
            showToast(R.string.please_input_11_digits_phone_number);
            return;
        }
        if (!mDataBinding.isCheck.get() && mDataBinding.tipVisibility.get() == View.VISIBLE) {
            showToast(R.string.please_read_policy);
            return;
        }
        // 有概率报异常“.....cannot be found from the current destination NavGraph...”而崩溃
        try {
            Navigation.findNavController(view)
                    .navigate(PhoneFragmentDirections.actionPhoneToLoginCode()
                            .setPhoneNumber(mDataBinding.phoneNumber.get())
                            .setCountryCode(mDataBinding.countryCode.get())
                            .setType(mSmsType)
                    );
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 显示用户协议
     *
     * @param view 被点击的控件
     */
    public void showUserAgreement(@NonNull View view) {
        H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
        if (h5Plugin != null && h5Plugin.getUserAgreementUrl() != null) {
            final WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
            if (webViewPlugin != null) {
                Bundle arguments = new Bundle();
                arguments.putString("url", h5Plugin.getUserAgreementUrl());
                arguments.putString("title", ResourcesUtils.getString(R.string.fragment_phone_user_contract));
                webViewPlugin.startWebViewFragment(view, arguments);
            }
        } else {
            showToast(R.string.all_account_check_network);
        }
    }

    /**
     * 显示隐私政策
     *
     * @param view 被点击的控件
     */
    public void showPrivacyPolicy(@NonNull View view) {
        H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
        if (h5Plugin != null && h5Plugin.getPrivacyPolicyUrl() != null) {
            final WebViewPlugin webViewPlugin = PluginManager.get(WebViewPlugin.class);
            if (webViewPlugin != null) {
                Bundle arguments = new Bundle();
                arguments.putString("url", h5Plugin.getPrivacyPolicyUrl());
                arguments.putString("title", ResourcesUtils.getString(R.string.fragment_phone_privacy_policy));
                webViewPlugin.startWebViewFragment(view, arguments);
            }
        } else {
            showToast(R.string.all_account_check_network);
        }
    }
    //</editor-fold>
}
