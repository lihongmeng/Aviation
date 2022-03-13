package com.jxntv.record;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.jxntv.base.BaseActivity;
import com.jxntv.record.databinding.ActivityRecordBinding;

/**
 * 录制activity
 */
public class RecordActivity extends BaseActivity<ActivityRecordBinding> {

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
        return R.layout.activity_record;
    }

    @Override
    protected void initView() {
        NavController mNavController = Navigation.findNavController(this, R.id.fragment_entry);
        int destId = getIntent() != null ? getIntent().getIntExtra(RecordPluginImpl.INTENT_RECORD_TYPE, R.id.recordFragment) : R.id.recordFragment;
        NavInflater inflater = mNavController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.record_nav_graph);
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
