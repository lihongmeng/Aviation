package com.hzlz.aviation.feature.watchtv.ui;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.START_ACTIVITY_DESTINATION_ID;

import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.feature.watchtv.R;
import com.hzlz.aviation.feature.watchtv.databinding.ActivityWatchTvBinding;

/**
 * 承载看电视功能的Activity
 */
public class WatchTvActivity extends BaseActivity<ActivityWatchTvBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_watch_tv;
    }

    @Override
    protected void initView() {
        NavController mNavController = Navigation.findNavController(this, R.id.fragment_entry);
        Intent intent = getIntent();
        int destId = intent != null ?
                intent.getIntExtra(START_ACTIVITY_DESTINATION_ID, R.id.watchTvChannelDetailFragment) : R.id.watchTvChannelDetailFragment;
        NavInflater inflater = mNavController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.watch_tv_nav_graph);
        graph.setStartDestination(destId);

        Bundle bundle = intent != null ? intent.getExtras() : null;
        mNavController.setGraph(graph, bundle);
    }

    @Override
    protected void bindViewModels() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

}
