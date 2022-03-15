package com.hzlz.aviation.feature.webview;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.webview.databinding.ActivityWebViewBinding;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.immersive.ImmersiveUtils;

public class WebViewActivity extends BaseActivity<ActivityWebViewBinding> {

    private BaseFragment baseFragment;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initView() {
        ImmersiveUtils.enterImmersive(this, Color.WHITE,true,false);
        NavController mNavController = Navigation.findNavController(this, R.id.host_fragment);
        NavInflater inflater = mNavController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.web_view_nav_graph);
        Bundle bundle = getIntent() != null ? getIntent().getExtras() : null;
        mNavController.setGraph(graph, bundle);
        if (getFragment()!=null) {
            getFragment().onFragmentResume();
        }
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
        if (getFragment()!=null) {
            getFragment().onFragmentResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getFragment()!=null) {
            getFragment().onFragmentPause();
        }
    }

    public BaseFragment getFragment() {
        if (baseFragment == null) {
            Fragment mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.webViewFragment);
            if (mMainNavFragment == null) {
                mMainNavFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                if (mMainNavFragment instanceof BaseFragment) {
                    baseFragment = (BaseFragment) mMainNavFragment;
                }
            }
            if (mMainNavFragment != null) {
                //获取指定的fragment
                Fragment fragment = mMainNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                if (fragment instanceof BaseFragment) {
                    baseFragment = (BaseFragment) fragment;
                }
            }
        }
        return baseFragment;
    }

}
