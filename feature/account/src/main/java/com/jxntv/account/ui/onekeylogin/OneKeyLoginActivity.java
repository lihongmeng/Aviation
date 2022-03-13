package com.jxntv.account.ui.onekeylogin;

import static com.jxntv.base.Constant.BUNDLE_KEY.IS_NEED_BROAD_HOME_TO_ME;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.HOME_TO_ME;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.jxntv.account.R;
import com.jxntv.account.databinding.ActivityOneKeyLoginBinding;
import com.jxntv.base.BaseActivity;
import com.jxntv.base.Constant;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.event.GVideoEventBus;

public class OneKeyLoginActivity extends BaseActivity<ActivityOneKeyLoginBinding> {

    /**
     * 一键登录容器页，导航中只有一个oneKeyLoginFragment
     * <p>
     * 导航文件中的startDestination决定了默认启动的Fragment
     * 而业务场景上，
     * 有的地方需要默认启动验证码登录页
     * <p>
     * 有的地方需要优先默认启动一键登录页，并且代码调用位置
     * 无权修改phone_nav_graph的startDestination属性
     * <p>
     * 如果在验证码登录和一键登录之前再加一个分发页Fragment，会有页面回退问题
     * <p>
     * 所以没有将验证码登录和一键登录保存在一个导航文件中
     *
     * @param context Context
     * @param bundle  相关参数，原则上，登录界面应该与所有模块相互独立
     *                通过广播解耦，但是一些特殊逻辑，需要在登录完成后，执行特定操作
     *                就在此参数中记录，所以非特殊情况，此参数为空即可
     */
    public static void start(@NonNull Context context, Bundle bundle) {
        Intent intent = new Intent(context, OneKeyLoginActivity.class);
        intent.putExtras(bundle);
        if (context instanceof Application) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_one_key_login;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void bindViewModels() {
        ImmersiveUtils.enterImmersive(this, Color.WHITE, true, false);
        NavController navController = Navigation.findNavController(this, R.id.fragment_one_key_login);
        NavInflater inflater = navController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.one_key_login_nav_graph);
        navController.setGraph(graph);
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observe(this, o -> {
            boolean value = getBooleanDataFromBundle(IS_NEED_BROAD_HOME_TO_ME);
            if (value) {
                GVideoEventBus.get(HOME_TO_ME).post(null);
            }
            finish();
        });
    }

    @Override
    protected void loadData() {
    }

}
