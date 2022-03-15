package com.hzlz.aviation.feature.video.ui;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.VideoActivityBinding;

public class VideoActivity extends BaseActivity<VideoActivityBinding> {

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.video_activity;
    }

    @Override
    protected void initView() {

        NavController mNavController = Navigation.findNavController(this, R.id.host_fragment);
        int destId = getIntent() != null ? getIntent().getIntExtra(Constants.INTENT_VIDEO_TYPE, R.id.videoSuperFragment) : R.id.videoSuperFragment;

        NavInflater inflater = mNavController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.video_nav_graph);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
