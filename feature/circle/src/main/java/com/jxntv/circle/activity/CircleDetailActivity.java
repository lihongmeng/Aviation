package com.jxntv.circle.activity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.jxntv.base.BaseActivity;
import com.jxntv.circle.CirclePluginImpl;
import com.jxntv.circle.R;
import com.jxntv.circle.databinding.ActivityCircleBinding;

public class CircleDetailActivity extends BaseActivity<ActivityCircleBinding> {

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
