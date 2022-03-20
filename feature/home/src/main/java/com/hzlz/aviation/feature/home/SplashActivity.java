package com.hzlz.aviation.feature.home;

import android.content.Intent;

import com.hzlz.aviation.feature.home.databinding.HomeSplashBinding;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;

/**
 * 空页面，用于解决切到后台，点击桌面快捷方式返回时，会杀死HomeActivity栈之上Activity的问题
 */
public class SplashActivity extends BaseActivity<HomeSplashBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.home_splash;
    }

    @Override
    protected void initView() {

        ImmersiveUtils.enterImmersiveFullTransparent(SplashActivity.this);

        enterHome();
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void bindViewModels() {
    }

    @Override
    protected void loadData() {

    }

    private void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

}

