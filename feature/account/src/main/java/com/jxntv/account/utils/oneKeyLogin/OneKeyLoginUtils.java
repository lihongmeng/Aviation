package com.jxntv.account.utils.oneKeyLogin;

import static com.jxntv.base.Constant.ONE_KEY_LOGIN_UI_STYLE.FULL_PORT;
import static com.mobile.auth.gatewayauth.ResultCode.CODE_ERROR_USER_CANCEL;
import static com.mobile.auth.gatewayauth.ResultCode.CODE_ERROR_USER_SWITCH;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.jxntv.account.R;
import com.jxntv.account.model.annotation.SmsCodeType;
import com.jxntv.account.ui.AccountModuleActivity;
import com.jxntv.account.ui.onekeylogin.OneKeyLoginActivity;
import com.jxntv.base.datamanager.LoginDataManager;
import com.jxntv.base.plugin.H5Plugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.LogUtils;
import com.jxntv.utils.ResourcesUtils;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.PreLoginResultListener;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;

public class OneKeyLoginUtils {

    private volatile static OneKeyLoginUtils singleInstance = null;

    // SDK接口调用超时时间
    private final int OUT_TIME = 5000;

    // SDK帮助类
    public PhoneNumberAuthHelper phoneNumberAuthHelper;

    // 启动一键登录后，回调异常、用户取消、获取登录成功等等回调
    // 服务于一键登录页面
    private ResultListener resultListener;

    // 一键登录页面UI配置
    private BaseUIConfig mUIConfig;

    // 来自于上层页面，在启动登录整套逻辑的时候，希望在流程中保存的参数
    // 例如，在登录成功之后，将首页tab移动到第四页
    private Bundle bundle;

    private OneKeyLoginUtils() {
    }

    public static OneKeyLoginUtils getInstance() {
        if (singleInstance == null) {
            synchronized (OneKeyLoginUtils.class) {
                if (singleInstance == null) {
                    singleInstance = new OneKeyLoginUtils();
                }
            }
        }
        return singleInstance;
    }

    /**
     * AppId通过init配置接口下发
     * 所以在每次更新init配置接口时，初始化SDK
     * 需要登录时直接使用
     * <p>
     * 需要注意，phoneNumberAuthHelper功能的执行结果
     * 通过TokenResultListener
     */
    public void init() {
        phoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(
                GVideoRuntime.getAppContext(),
                null
        );
        phoneNumberAuthHelper.setAuthSDKInfo(LoginDataManager.getInstance().getQuickLoginKey());
        phoneNumberAuthHelper.getReporter().setLoggerEnable(true);
        accelerateVerify();
    }

    /**
     * 启动登录流程，这一步会检测当前手机环境
     * 如果环境符合一键登录要求，就启动一键登录页面
     * 否则就使用默认登录方式
     *
     * @param context Context
     * @param bundle  来自上层的参数，进入登录界面时，传递进去即可
     */
    public void dispatchLogin(Context context, Bundle bundle) {
        this.bundle = bundle;
        phoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(
                GVideoRuntime.getAppContext(),
                new TokenResultListener() {
                    @Override
                    public void onTokenSuccess(String s) {
                        TokenRet pTokenRet = TokenRet.fromJson(s);
                        // 终端⽀持认证
                        if (ResultCode.CODE_ERROR_ENV_CHECK_SUCCESS.equals(pTokenRet.getCode())) {

                            // 构建一键登录页面UI
                            mUIConfig = BaseUIConfig.init(FULL_PORT, phoneNumberAuthHelper, interactionListener);

                            startOneKeyLoginActivity(context);
                        } else {
                            startDefaultLogin(context);
                        }
                    }

                    @Override
                    public void onTokenFailed(String s) {
                        LogUtils.d("checkEnvAvailable onTokenFailed -->> " + s);
                        startDefaultLogin(context);
                    }
                }
        );

        // SDK环境检查函数，检查终端是否⽀持号码认证，通过TokenResultListener返回code
        phoneNumberAuthHelper.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_LOGIN);
    }

    // 一键登录界面相关元素的交互回调
    private final BaseUIConfig.InteractionListener interactionListener = new BaseUIConfig.InteractionListener() {

        /**
         * 点击了切换其他登录方式
         *
         * @param context Context
         */
        @Override
        public void clickOtherLoginMethod(Context context) {
            startDefaultLogin(context);
        }

        /**
         * 点击了返回按钮
         *
         * @param context Context
         */
        @Override
        public void clickBack(Context context) {
            phoneNumberAuthHelper.setAuthListener(null);
            phoneNumberAuthHelper.quitLoginPage();
            if (resultListener != null) {
                resultListener.userCancel();
            }
        }
    };

    /**
     * 可以无视返回结果的加速方法
     * 用于加速一键登录整体流程
     */
    private void accelerateVerify() {
        phoneNumberAuthHelper.accelerateVerify(OUT_TIME, new PreLoginResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                LogUtils.d("accelerateVerify onTokenSuccess -->> " + s);
            }

            @Override
            public void onTokenFailed(String s, String s1) {
                LogUtils.d("accelerateVerify onTokenFailed -->> " + s + "," + s1);
            }
        });
    }

    /**
     * 启动授权页，并且根据授权页交互结果，回调返回Token值
     * 所以此方法中的回调会执行两次
     */
    public void startOneKeyLoginReal(Context context, ResultListener resultListener) {
        this.resultListener = resultListener;
        phoneNumberAuthHelper.setAuthListener(new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                TokenRet tokenRet;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    String code = tokenRet.getCode();

                    // 如果是成功唤起授权页，不往下执行，等待授权页的结果回调
                    if (ResultCode.CODE_START_AUTHPAGE_SUCCESS.equals(code)) {
                        return;
                    }
                    if (ResultCode.CODE_SUCCESS.equals(code)) {
                        if (resultListener != null) {
                            resultListener.loginSuccess(tokenRet.getToken());
                        }
                    } else {
                        startDefaultLogin(context);
                    }
                    phoneNumberAuthHelper.setAuthListener(null);
                    phoneNumberAuthHelper.quitLoginPage();

                } catch (Exception e) {
                    e.printStackTrace();
                    startDefaultLogin(context);
                    phoneNumberAuthHelper.setAuthListener(null);
                    phoneNumberAuthHelper.quitLoginPage();
                }
            }

            @Override
            public void onTokenFailed(String s) {
                TokenRet tokenRet;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    String code = tokenRet.getCode();

                    phoneNumberAuthHelper.quitLoginPage();
                    phoneNumberAuthHelper.setAuthListener(null);

                    // 如果是用户主动返回或使用其他登录方式，要交由上层决定跳转
                    if (CODE_ERROR_USER_CANCEL.equals(code)
                            || CODE_ERROR_USER_SWITCH.equals(code)) {
                        if (resultListener != null) {
                            resultListener.hideLoading();
                            resultListener.userCancel();
                        }
                        return;
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                // 如果是非用户原因的异常，使用默认登录方式
                if (resultListener != null) {
                    resultListener.hideLoading();
                }
                startDefaultLogin(context);
            }
        });
        initUIConfig(context);

        // 启动一键登录授权页并获取登录相关Token
        phoneNumberAuthHelper.getLoginToken(GVideoRuntime.getAppContext(), OUT_TIME);
        if (resultListener != null) {
            resultListener.showLoading("请稍候...");
        }
    }

    private void initUIConfig(Context context) {
        H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);
        if (TextUtils.isEmpty(h5Plugin.getUserAgreementUrl()) || TextUtils.isEmpty(h5Plugin.getPrivacyPolicyUrl())) {
            if (resultListener != null) {
                resultListener.programException(ResourcesUtils.getString(R.string.net_error));
                return;
            }
            return;
        }
        mUIConfig.configAuthPage(context);
    }

    private void startDefaultLogin(Context context) {
        bundle.putInt("type", SmsCodeType.LOGIN);
        AccountModuleActivity.start(context, bundle);
    }

    private void startOneKeyLoginActivity(Context context) {
        OneKeyLoginActivity.start(context, bundle);
    }

    public interface ResultListener {

        void showLoading(String content);

        void hideLoading();

        void loginSuccess(String token);

        void userCancel();

        void programException(String exception);

    }

}
