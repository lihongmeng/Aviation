package com.jxntv.test;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import com.jxntv.base.BaseFragment;
import com.jxntv.base.plugin.TestPlugin;

/**
 * Test 接口实现
 *
 */
public final class TestPluginImpl implements TestPlugin {
  @Override
  public void addDestination(@NonNull BaseFragment fragment) {
    fragment.addDestination(R.navigation.test_nav_graph);
  }

  @Override
  public void startFragment(@NonNull View view) {
    Navigation.findNavController(view).navigate(R.id.test_nav_graph);
  }

}
