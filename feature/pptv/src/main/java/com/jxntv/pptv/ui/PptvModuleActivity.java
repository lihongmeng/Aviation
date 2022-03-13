package com.jxntv.pptv.ui;

import android.os.Bundle;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import com.jxntv.base.BaseActivity;
import com.jxntv.pptv.R;
import com.jxntv.pptv.databinding.ActivityPptvModuleBinding;

public final class PptvModuleActivity extends BaseActivity<ActivityPptvModuleBinding> {
  @Override
  protected int getLayoutId() {
    return R.layout.activity_pptv_module;
  }

  @Override
  protected boolean showToolbar() {
    return false;
  }

  @Override
  protected void initView() {
    NavController mNavController = Navigation.findNavController(this, R.id.fragment_pptv_module_activity);
    NavInflater inflater = mNavController.getNavInflater();
    NavGraph graph = inflater.inflate(R.navigation.channel_nav_graph);
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
