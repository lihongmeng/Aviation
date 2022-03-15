package com.hzlz.aviation.feature.account.utils.oneKeyLogin;

import static android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.kernel.base.plugin.H5Plugin;
import com.hzlz.aviation.kernel.base.utils.ToastUtils;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.mobile.auth.gatewayauth.AuthRegisterViewConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;

import org.json.JSONException;
import org.json.JSONObject;

public class FullPortConfig extends BaseUIConfig {
    private final String TAG = "全屏竖屏样式";

    public FullPortConfig(
            PhoneNumberAuthHelper authHelper,
            InteractionListener interactionListener
    ) {
        super(authHelper, interactionListener);
    }

    @Override
    public void configAuthPage(Context context) {
        mAuthHelper.setUIClickListener((code, context1Value, jsonString) -> {
            JSONObject jsonObj = null;
            try {
                if (!TextUtils.isEmpty(jsonString)) {
                    jsonObj = new JSONObject(jsonString);
                }
            } catch (JSONException e) {
                jsonObj = new JSONObject();
            }
            switch (code) {
                //点击授权页默认样式的返回按钮
                case ResultCode.CODE_ERROR_USER_CANCEL:
                    if (interactionListener != null) {
                        interactionListener.clickBack(context);
                    }
                    break;
                //点击一键登录按钮会发出此回调
                //当协议栏没有勾选时 点击按钮会有默认toast 如果不需要或者希望自定义内容 setLogBtnToastHidden(true)隐藏默认Toast
                //通过此回调自己设置toast
                case ResultCode.CODE_ERROR_USER_LOGIN_BTN:
                    if (!jsonObj.optBoolean("isChecked")) {
                        ToastUtils.showShort(R.string.please_read_policy);
                    }
                    break;
                // 用户切换其他登录方式
                case ResultCode.CODE_ERROR_USER_SWITCH:
                    if (interactionListener != null) {
                        interactionListener.clickOtherLoginMethod(context);
                    }
                    break;
                default:
                    break;

            }

        });
        mAuthHelper.removeAuthRegisterXmlConfig();
        mAuthHelper.removeAuthRegisterViewConfig();

        //添加自定义切换其他登录方式
        mAuthHelper.addAuthRegistViewConfig("switch_msg", new AuthRegisterViewConfig.Builder()
                .setView(initSwitchView(context, 350))
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
                .build());
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        H5Plugin h5Plugin = PluginManager.get(H5Plugin.class);

        // 详细配置请见文档 https://help.aliyun.com/document_detail/144231.html
        AuthUIConfig.Builder authUIConfigBuilder = new AuthUIConfig.Builder();

        // 页面整体属性
        authUIConfigBuilder
                .setScreenOrientation(authPageOrientation)
                .setPageBackgroundDrawable(ContextCompat.getDrawable(context, R.color.color_ffffff));

        // 状态栏
        authUIConfigBuilder
                // 设置状态栏UI属性，View.SYSTEM_UI_FLAG_LOW_PROFILE、View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                .setStatusBarUIFlag(SYSTEM_UI_FLAG_LOW_PROFILE);

        // 导航栏
        authUIConfigBuilder
                // 设置导航栏颜色
                .setNavColor(Color.WHITE)
                // 设置导航栏标题文字
                .setNavText("")
                // 设置导航栏返回键图片
                .setNavReturnImgDrawable(ContextCompat.getDrawable(context, R.drawable.icon_back))
                // 设置导航栏返回按钮宽度，单位dp
                .setNavReturnImgWidth(45)
                // 设置导航栏返回按钮高度，单位dp
                .setNavReturnImgHeight(45)
                // 设置导航栏返回按钮ScaleType
                .setNavReturnScaleType(ImageView.ScaleType.CENTER);

        // logo
        authUIConfigBuilder
                // 设置logo图片
                .setLogoImgDrawable(ContextCompat.getDrawable(context, R.drawable.icon_one_key_login_logo))
                // 设置logo控件宽度，单位dp
                .setLogoWidth(67)
                // 设置logo控件高度，单位dp
                .setLogoHeight(56)
                // 设置logo控件相对导航栏顶部的位移，单位dp
                .setLogoOffsetY(175)
                // 设置logo图片ScaleType
                .setLogoScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // 号码
        authUIConfigBuilder
                // 设置号码栏控件相对导航栏顶部的位移，单位dp
                .setNumFieldOffsetY(279)
                // 设置手机号码字体大小，单位dp，字体大小不随系统变化
                .setNumberSize(25)
                // 设置手机号码字体颜色
                .setNumberColor(Color.BLACK);

        // Slogan
        authUIConfigBuilder
                // 设置Slogan文字颜色
                .setSloganTextColor(ContextCompat.getColor(context, R.color.color_000000_50))
                // 设置Slogan相对导航栏顶部的位移，单位dp
                .setSloganOffsetY(313)
                // 设置Slogan文字大小，单位dp，字体大小不随系统变化
                .setSloganTextSize(13);

        // 一键登录按钮
        authUIConfigBuilder
                // 设置登录按钮相对导航栏顶部的位移，单位dp
                .setLogBtnOffsetY(344)
                // 设置登录按钮背景图片路径
                .setLogBtnBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.shape_soild_ff1b36_coners_200dp))
                // 设置登录按钮文字颜色
                .setLogBtnTextColor(Color.WHITE)
                // 设置登录按钮文字大小，单位dp，字体大小不随系统变化
                .setLogBtnTextSize(20)
                .setLogBtnToastHidden(true);

        // 切换到其他方式
        authUIConfigBuilder
                .setSwitchAccTextColor(ContextCompat.getColor(context, R.color.color_000000_50))
                .setSwitchAccTextSizeDp(15)
                .setSwitchOffsetY(406)
                .setSwitchAccText(ResourcesUtils.getString(R.string.change_to_normal_method));

        // 协议栏
        authUIConfigBuilder
                .setPrivacyOffsetY_B(67)
                .setPrivacyTextSize(11)
                .setCheckBoxWidth(14)
                .setCheckBoxHeight(14)
                .setAppPrivacyOne(ResourcesUtils.getString(R.string.fragment_phone_user_contract), h5Plugin.getUserAgreementUrl())
                .setAppPrivacyTwo(ResourcesUtils.getString(R.string.fragment_phone_privacy_policy), h5Plugin.getPrivacyPolicyUrl())
                .setCheckedImgDrawable(ContextCompat.getDrawable(context, R.drawable.icon_check_circle_w_h_10))
                .setUncheckedImgDrawable(ContextCompat.getDrawable(context, R.drawable.icon_uncheck_circle_w_h_10))
                .setProtocolGravity(Gravity.CENTER_HORIZONTAL)
                .setAppPrivacyColor(
                        ContextCompat.getColor(context, R.color.color_000000_50),
                        ContextCompat.getColor(context, R.color.color_ff1b36)
                );

        mAuthHelper.setAuthUIConfig(authUIConfigBuilder.create());
    }


}
