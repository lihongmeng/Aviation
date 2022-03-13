package com.jxntv.account.ui.account;

import static com.jxntv.base.Constant.SP_LOGIN_HAS_VERIFICATE;

import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.jxntv.account.BuildConfig;
import com.jxntv.account.R;
import com.jxntv.account.model.User;
import com.jxntv.account.model.annotation.SmsCodeType;
import com.jxntv.account.repository.UserRepository;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.Constant;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.sharedprefs.KernelSharedPrefs;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.sensordata.GVideoSensorDataManager;

/**
 * @since 2020-01-20 15:57
 */
public final class AccountSecurityViewModel extends BaseViewModel {
    //<editor-fold desc="属性">
    @NonNull
    private CheckThreadLiveData<User> mUserLiveData = new CheckThreadLiveData<>();
    @NonNull
    private CheckThreadLiveData<Boolean> mLogoutDialogLiveData = new CheckThreadLiveData<>();
    @NonNull
    private UserRepository mUserRepository = new UserRepository();
    @NonNull
    public ObservableInt switchEnvironmentVisibility = new ObservableInt();
    //</editor-fold>

    //<editor-fold desc="构造函数">
    public AccountSecurityViewModel(@NonNull Application application) {
        super(application);
        switchEnvironmentVisibility.set(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
    }
    //</editor-fold>

    //<editor-fold desc="API">
    @NonNull
    LiveData<User> getUserLiveData() {
        return mUserLiveData;
    }

    @NonNull
    LiveData<Boolean> getLogoutDialogLiveData() {
        return mLogoutDialogLiveData;
    }

    void loadData() {
        mUserRepository.getCurrentUser().subscribe(new GVideoResponseObserver<User>() {
            @Override
            protected void onSuccess(@NonNull User user) {
                mUserLiveData.setValue(user);
            }
        });
    }

    void logout(@NonNull Fragment fragment) {
        mUserRepository.logout().subscribe(new GVideoResponseObserver<Object>() {
            @Override
            protected void onSuccess(@NonNull Object result) {
                // 通知登出
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).post(null);
                // 回退
                NavHostFragment.findNavController(fragment).popBackStack();

                GVideoSensorDataManager.getInstance().logout();
            }
        });
    }

    void switchAccount(@NonNull Fragment fragment) {
        Bundle arguments = new Bundle();
        arguments.putInt("type", SmsCodeType.SWITCH_ACCOUNT);
        NavHostFragment.findNavController(fragment).navigate(R.id.phone_nav_graph, arguments);
    }
    //</editor-fold>

    //<editor-fold desc="事件绑定">

    /**
     * 重新绑定手机号
     *
     * @param view 被点击的控件
     */
    public void rebindPhoneNumber(@NonNull View view) {
        KernelSharedPrefs.getInstance().putBoolean(SP_LOGIN_HAS_VERIFICATE, true);
        Bundle arguments = new Bundle();
        arguments.putInt("type", SmsCodeType.REBIND_PHONE);
        Navigation.findNavController(view).navigate(R.id.phone_nav_graph, arguments);
    }

    /**
     * 导航到身份认证界面
     *
     * @param view 被点击的控件
     */
    public void navigationToAuthentication(@NonNull View view) {
        Navigation.findNavController(view).navigate(R.id.action_account_security_to_authentication);
    }

    /**
     * 注销账户
     *
     * @param view 被点击的控件
     */
    public void cancelAccount(@NonNull View view) {
        KernelSharedPrefs.getInstance().putBoolean(SP_LOGIN_HAS_VERIFICATE, true);
        Navigation.findNavController(view).navigate(R.id.action_account_security_to_cancel_account);
    }

    /**
     * 退出登录
     */
    public void logout() {
        KernelSharedPrefs.getInstance().putBoolean(SP_LOGIN_HAS_VERIFICATE, true);
        mLogoutDialogLiveData.setValue(true);
    }
    //</editor-fold>
}
