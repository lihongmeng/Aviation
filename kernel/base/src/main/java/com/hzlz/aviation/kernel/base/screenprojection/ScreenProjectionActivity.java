package com.hzlz.aviation.kernel.base.screenprojection;

import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.START_ACTIVITY_DESTINATION_ID;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;

import com.hzlz.aviation.kernel.base.BaseActivity;
import com.hzlz.aviation.kernel.base.R;
import com.hzlz.aviation.kernel.base.databinding.ActivityScreenProjectionBinding;
import com.liuwei.android.upnpcast.NLUpnpCastManager;

public class ScreenProjectionActivity extends BaseActivity<ActivityScreenProjectionBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_screen_projection;
    }

    @Override
    protected void initView() {
        NavController mNavController = Navigation.findNavController(this, R.id.fragment_entry);
        Intent intent = getIntent();
        int destId = intent != null ?
                intent.getIntExtra(START_ACTIVITY_DESTINATION_ID, R.id.screenProjectionFragment) : R.id.screenProjectionFragment;
        NavInflater inflater = mNavController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.screen_projection_nav_graph);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NLUpnpCastManager.getInstance().bindUpnpCastService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            NLUpnpCastManager.getInstance().bindUpnpCastService(this);
        }catch(Exception exception){
        }
    }
}
