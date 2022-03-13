package com.jxntv.account.ui;

import static com.jxntv.base.Constant.BUNDLE_KEY.IS_NEED_BROAD_HOME_TO_ME;
import static com.jxntv.base.Constant.EVENT_BUS_EVENT.HOME_TO_ME;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.jxntv.account.R;
import com.jxntv.account.databinding.ActivityAccountModuleBinding;
import com.jxntv.base.BaseActivity;
import com.jxntv.base.Constant;
import com.jxntv.base.immersive.ImmersiveUtils;
import com.jxntv.event.GVideoEventBus;

/**
 * 账户模块界面
 *
 * @since 2020-01-13 14:34
 */
@SuppressWarnings("FieldCanBeLocal")
public final class AccountModuleActivity extends BaseActivity<ActivityAccountModuleBinding> {

    /**
     * 账户模块跳转，默认界面为登录界面
     *
     * @param context Context
     * @param bundle  相关参数，原则上，登录界面应该与所有模块相互独立
     *                通过广播解耦，但是一些特殊逻辑，需要在登录完成后，执行特定操作
     *                就在此参数中记录，所以非特殊情况，此参数为空即可
     */
    public static void start(@NonNull Context context, Bundle bundle) {
        Intent starter = new Intent(context, AccountModuleActivity.class);
        if (bundle != null) {
            starter.putExtras(bundle);
        }
        if (context instanceof Application) {
            starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_module;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initView() {
        ImmersiveUtils.enterImmersive(this, Color.WHITE, true, false);
        NavController navController = Navigation.findNavController(this, R.id.fragment_account_module_activity);
        NavInflater inflater = navController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.phone_nav_graph);

        // 正常情况一定会有参数，这两步判断用于保险,启动默认Fragment
        Intent intent = getIntent();
        if (intent == null) {
            graph.setStartDestination(R.id.phoneFragment);
            navController.setGraph(graph, null);
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            graph.setStartDestination(R.id.phoneFragment);
            navController.setGraph(graph, null);
            return;
        }

        int fragmentId = bundle.getInt(Constant.EXTRA_FRAGMENT_ID, -1);
        int navigation = bundle.getInt(Constant.EXTRA_NAVIGATION, -1);

        // 如果调用者指明了导航和目标Fragment，设置进去
        if (fragmentId > 0 && navigation > 0) {
            graph = inflater.inflate(navigation);
            graph.setStartDestination(fragmentId);
        } else {
            graph.setStartDestination(R.id.phoneFragment);
        }
        navController.setGraph(graph, bundle);

    }

    @Override
    protected void bindViewModels() {
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observeForever(mLoginObserver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).removeObserver(mLoginObserver);
    }

    private final Observer<Object> mLoginObserver = o -> {
        boolean value = getBooleanDataFromBundle(IS_NEED_BROAD_HOME_TO_ME);
        if (value) {
            GVideoEventBus.get(HOME_TO_ME).post(null);
        }
        finish();
    };


}
