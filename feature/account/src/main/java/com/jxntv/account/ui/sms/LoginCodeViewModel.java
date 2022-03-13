package com.jxntv.account.ui.sms;

import static com.jxntv.base.plugin.AccountPlugin.VERIFICATION_STATUS_NO_VERIFY;
import static com.jxntv.base.plugin.AccountPlugin.VERIFICATION_STATUS_VERIFYING;
import static com.jxntv.network.exception.GVideoCode.VALIDATE_CODE_EXPIRED;
import static com.jxntv.network.exception.GVideoCode.VALIDATE_CODE_UNMATCHED;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.jxntv.account.R;
import com.jxntv.account.model.LoginResponse;
import com.jxntv.account.model.User;
import com.jxntv.account.model.annotation.SmsCodeType;
import com.jxntv.account.repository.SmsRepository;
import com.jxntv.account.repository.UserRepository;
import com.jxntv.account.utils.HeaderUtils;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.Constant;
import com.jxntv.base.StaticParams;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.HomePlugin;
import com.jxntv.base.utils.JudgeUtils;
import com.jxntv.base.utils.NavigationUtil;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.network.exception.GVideoAPIException;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.widget.GVideoButton;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

/**
 * 短信验证码界面
 *
 * @since 2020-01-14 11:40
 */
public final class LoginCodeViewModel extends BaseViewModel {

    //<editor-fold desc="属性">

    @SmsCodeType
    private int mType;

    // SmsCodeDataBinding
    private final SmsCodeDataBinding dataBinding = new SmsCodeDataBinding();

    // SmsRepository
    private final SmsRepository mSmsRepository = new SmsRepository();

    // UserRepository
    private final UserRepository mUserRepository = new UserRepository();

    // public CheckThreadLiveData<Boolean> showInviteCode = new CheckThreadLiveData<>();

    // 之前有没有成功核销过邀请码，
    // 若true，以后任何账号都不需要邀请码，
    // 若false，需要接口请求是否需要
    // 核销成功后，将此值保存为true
    // public boolean hasVerificate;

    //</editor-fold>

    //<editor-fold desc="构造函数">

    public LoginCodeViewModel(@NonNull Application application) {
        super(application);
        mType = SmsCodeType.LOGIN;
    }

    //</editor-fold>

    //<editor-fold desc="API">

    @NonNull
    SmsCodeDataBinding getDataBinding() {
        return dataBinding;
    }

    void init(@NonNull LoginCodeFragmentArgs args) {
        mType = args.getType();
        dataBinding.init(args);
        // checkNeedInviteCode();
    }

    //</editor-fold>

    //<editor-fold desc="生命周期">

    @Override
    protected void onCleared() {
        super.onCleared();
        dataBinding.onCleared();
    }

    //</editor-fold>

    //<editor-fold desc="事件绑定">

    /**
     * 返回
     *
     * @param view 被点击的控件
     */
    public void back(@NonNull View view) {
        Navigation.findNavController(view).popBackStack();
    }

    /**
     * 发送验证码
     *
     * @param view 被点击的控件
     */
    public void sendSmsCode(View view) {
        mSmsRepository.sendSmsCode(dataBinding.mPhoneNumber, dataBinding.mCountryCode, mType)
                .subscribe(new GVideoResponseObserver<Object>() {
                    @Override
                    protected boolean isShowNetworkDialog() {
                        return false;
                    }

                    @Override
                    protected void onSuccess(@NonNull Object t) {
                        showToast(R.string.fragment_sms_send_sms_code_successfully);
                        GVideoSensorDataManager.getInstance().clickGetSMS(true);
                        if (view == null) {
                            return;
                        }
                        dataBinding.smsCodeSentSuccessfully();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        if (throwable instanceof TimeoutException ||
                                throwable instanceof SocketTimeoutException ||
                                throwable instanceof UnknownHostException) {
                            showToast(R.string.all_network_not_available_action_tip);
                        }
                        GVideoSensorDataManager.getInstance().clickGetSMS(false);
                        if (throwable instanceof TimeoutException ||
                                throwable instanceof SocketTimeoutException ||
                                throwable instanceof UnknownHostException) {
                            showToast(R.string.all_network_not_available_action_tip);
                            return;
                        }
                        showToast(throwable.getMessage());
                    }
                });
    }

    /**
     * 确认
     *
     * @param view 被点击的控件
     */
    public void confirm(@NonNull View view) {
        switch (mType) {
            case SmsCodeType.REBIND_PHONE:
                rebindPhone(view);
                break;
            case SmsCodeType.LOGIN:
                login(view);
                break;
            case SmsCodeType.SWITCH_ACCOUNT:
                switchAccount(view);
                break;
            default:
                break;
        }
    }

    public void smsCodeChange(String content, GVideoButton confirmButton) {
        dataBinding.smsInviteCode = content;
        dataBinding.updateConfirmButtonChange(confirmButton);
    }

    private void login(View view) {
        mUserRepository.loginBySmsCode(dataBinding.mPhoneNumber,
                dataBinding.mCountryCode,
                dataBinding.smsInviteCode
        ).subscribe(new GVideoResponseObserver<LoginResponse>() {
            @Override
            protected void onSuccess(@NonNull LoginResponse response) {

                // 通知登录
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).post(null);

                GVideoSensorDataManager.getInstance().login();

                if (view == null) {
                    return;
                }
                Context context = view.getContext();
                if (context == null) {
                    return;
                }

                AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
                int auditStatusValue;

                User user = response.getUser();
                User.NewValue auditStatus = user.getNewNickname();
                if (auditStatus == null) {
                    auditStatusValue = VERIFICATION_STATUS_NO_VERIFY;
                } else {
                    auditStatusValue = auditStatus.getAuditStatus();
                }

                // 如果id和nickname相同，并且不是审核中的状态，就需要设置昵称
                String mId = user.getId();
                String mNickName = user.getNickname();
                if (mId != null
                        && !TextUtils.isEmpty(mId)
                        && mId.equals(mNickName)
                        && auditStatusValue != VERIFICATION_STATUS_VERIFYING) {
                    accountPlugin.startNickNameSetActivity(context);
                    NavigationUtil.getInstance().popBackStack(view,R.id.phone_nav_graph,true);
                } else {
                    if (StaticParams.isForeLogin) {
                        StaticParams.isForeLogin = false;
                        PluginManager.get(HomePlugin.class).restartApp(context);
                    } else {
                        // 准备一张头像，用于分享
                        HeaderUtils.getInstance().preHeaderImage(context);
                        NavigationUtil.getInstance().popBackStack(view,R.id.phone_nav_graph,true);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof TimeoutException ||
                        throwable instanceof SocketTimeoutException ||
                        throwable instanceof UnknownHostException) {
                    showToast(R.string.all_network_not_available_action_tip);
                    return;
                }
                if (throwable instanceof GVideoAPIException) {
                    // boolean showInviteCodeResult;
                    // Boolean showInviteCodeValue = showInviteCode.getValue();
                    // showInviteCodeResult = showInviteCodeValue != null && showInviteCodeValue;

                    switch (((GVideoAPIException) throwable).getCode()) {
                        case VALIDATE_CODE_EXPIRED:
                            // if (showInviteCodeResult) {
                            //     showToast(R.string.invite_code_already_expire);
                            // } else {
                                showToast(R.string.verificate_code_already_expire);
                            // }
                            break;
                        case VALIDATE_CODE_UNMATCHED:
                            // if (showInviteCodeResult) {
                            //     showToast(R.string.invite_code_wrong);
                            // } else {
                                showToast(R.string.verificate_code_wrong);
                            // }
                            break;
                        default:
                            String message = throwable.getMessage();
                            if (!TextUtils.isEmpty(message)) {
                                showToast(message);
                            }
                    }
                }
            }

        });
    }

    // private void checkNeedInviteCode() {
    //     if (hasVerificate) {
    //         showInviteCode.setValue(false);
    //         return;
    //     }
    //     mSmsRepository.checkNeedInviteCode(dataBinding.mPhoneNumber)
    //             .subscribe(new GVideoResponseObserver<Boolean>() {
    //
    //                 @Override
    //                 protected void onSuccess(@NonNull Boolean value) {
    //                     showInviteCode.setValue(value);
    //                 }
    //
    //                 @Override
    //                 protected void onFailed(@NonNull Throwable throwable) {
    //                     showInviteCode.setValue(false);
    //                 }
    //             });
    // }

    /**
     * 登录
     *
     * @param view 被点击的控件
     */
    private void switchAccount(View view) {
        mUserRepository.switchAccount(
                dataBinding.mPhoneNumber,
                dataBinding.mCountryCode,
                dataBinding.smsInviteCode
        ).subscribe(new GVideoResponseObserver<LoginResponse>() {
            @Override
            protected void onSuccess(@NonNull LoginResponse response) {
                if (view == null) {
                    return;
                }
                Context context = view.getContext();
                if (context == null) {
                    return;
                }

                int auditStatusValue;
                User user = response.getUser();
                User.NewValue auditStatus = user.getNewNickname();
                if (auditStatus == null) {
                    auditStatusValue = VERIFICATION_STATUS_NO_VERIFY;
                } else {
                    auditStatusValue = auditStatus.getAuditStatus();
                }

                // 如果id和nickname相同，并且不是审核中的状态，就需要设置昵称
                String mId = user.getId();
                String mNickName = user.getNickname();
                AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
                if (mId != null
                        && !TextUtils.isEmpty(mId)
                        && mId.equals(mNickName)
                        && auditStatusValue != VERIFICATION_STATUS_VERIFYING) {
                    accountPlugin.startNickNameSetActivity(context);
                } else {
                    // 通知登录
                    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).post(null);
                    GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGOUT).post(null);
                    HeaderUtils.getInstance().preHeaderImage(context);
                }
                NavigationUtil.getInstance().popBackStack(view,R.id.phone_nav_graph,true);
            }

            @Override
            protected void onFailed(@NonNull Throwable throwable) {
                if (throwable instanceof TimeoutException ||
                        throwable instanceof SocketTimeoutException ||
                        throwable instanceof UnknownHostException) {
                    showToast(R.string.all_network_not_available_action_tip);
                    return;
                }
                showToast(throwable.getMessage());
            }

        });
    }

    /**
     * 重新绑定手机号
     *
     * @param view 被点击的控件
     */
    private void rebindPhone(@NonNull View view) {
        mUserRepository.rebindPhone(
                dataBinding.mPhoneNumber,
                dataBinding.mCountryCode,
                dataBinding.smsInviteCode
        ).subscribe(new GVideoResponseObserver<User>() {
            @Override
            protected void onSuccess(@NonNull User response) {
                if (!JudgeUtils.isViewAvailable(view)) {
                    return;
                }
                showToast(R.string.all_account_rebind_phone_successfully);
                NavigationUtil.getInstance().popBackStack(view,R.id.phone_nav_graph,true);
            }

            @Override
            protected void onAPIError(@NonNull Throwable throwable) {
                showToast(throwable.getMessage());
            }

            @Override
            protected void onFailed(@NonNull Throwable throwable) {
                if (throwable instanceof TimeoutException ||
                        throwable instanceof SocketTimeoutException ||
                        throwable instanceof UnknownHostException) {
                    showToast(R.string.all_network_not_available_action_tip);
                    return;
                }
                showToast(throwable.getMessage());
            }

        });
    }

    //</editor-fold>

}
