package com.jxntv.account.utils.oneKeyLogin;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jxntv.account.R;
import com.jxntv.base.Constant;
import com.jxntv.utils.SizeUtils;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;

public abstract class BaseUIConfig {
    public PhoneNumberAuthHelper mAuthHelper;
    protected InteractionListener interactionListener;

    public static BaseUIConfig init(int type, PhoneNumberAuthHelper authHelper, InteractionListener interactionListener) {
        switch (type) {
            case Constant.ONE_KEY_LOGIN_UI_STYLE.FULL_PORT:
                return new FullPortConfig(authHelper, interactionListener);
            default:
                return null;
        }
    }

    public BaseUIConfig(
            PhoneNumberAuthHelper authHelper,
            InteractionListener interactionListener
    ) {
        mAuthHelper = authHelper;
        this.interactionListener = interactionListener;
    }

    protected View initSwitchView(Context context, int marginTop) {
        TextView switchTV = new TextView(context);
        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, SizeUtils.dp2px(50));
        //一键登录按钮默认marginTop 270dp
        mLayoutParams.setMargins(0, SizeUtils.dp2px(marginTop), 0, 0);
        mLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        switchTV.setText(R.string.change_to_normal_method);
        switchTV.setTextColor(Color.BLACK);
        switchTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.0F);
        switchTV.setLayoutParams(mLayoutParams);
        return switchTV;
    }

    public abstract void configAuthPage(Context context);

    /**
     * 在横屏APP弹竖屏一键登录页面或者竖屏APP弹横屏授权页时处理特殊逻辑
     * Android8.0只能启动SCREEN_ORIENTATION_BEHIND模式的Activity
     */
    public void onResume() {

    }

    public interface InteractionListener {
        void clickOtherLoginMethod(Context context);

        void clickBack(Context context);
    }
}
