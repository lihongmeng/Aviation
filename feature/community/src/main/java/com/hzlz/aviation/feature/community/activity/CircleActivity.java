package com.hzlz.aviation.feature.community.activity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.community.CirclePluginImpl;
import com.hzlz.aviation.feature.community.R;
import com.hzlz.aviation.feature.community.databinding.ActivityCircleBinding;
import com.hzlz.aviation.kernel.base.BaseActivity;

public class CircleActivity extends BaseActivity<ActivityCircleBinding> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_circle;
    }

    @Override
    protected void initView() {
        NavController mNavController = Navigation.findNavController(this, R.id.fragment_entry);
        int destId = getIntent() != null ? getIntent().getIntExtra(CirclePluginImpl.INTENT_CIRCLE_TYPE, R.id.circleFragment) : R.id.circleFragment;
        NavInflater inflater = mNavController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.circle_nav_graph);
        graph.setStartDestination(destId);
        Bundle bundle = getIntent() != null ? getIntent().getExtras() : null;
        mNavController.setGraph(graph, bundle);
    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void loadData() {
    }
}
