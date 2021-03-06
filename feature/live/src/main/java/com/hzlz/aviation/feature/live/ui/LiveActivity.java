package com.hzlz.aviation.feature.live.ui;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.live.Constants;
import com.hzlz.aviation.feature.live.databinding.LiveActivityBinding;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.feature.live.R;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc :
 **/
public class LiveActivity extends BaseActivity<LiveActivityBinding> {

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.live_activity;
    }

    @Override
    protected void initView() {
        NavController mNavController = Navigation.findNavController(this, R.id.live_activity);
        int destId = getIntent() != null ? getIntent().getIntExtra(Constants.INTENT_LIVE_TYPE, R.id.author_prepare_fragment) : R.id.author_prepare_fragment;
        NavInflater inflater = mNavController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.live_nav_graph);
        graph.setStartDestination(destId);
        Bundle bundle = getIntent() != null ? getIntent().getExtras() : null;
        mNavController.setGraph(graph, bundle);

    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected void loadData() {

    }

}
