package com.hzlz.aviation.feature.test;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.hzlz.aviation.kernel.base.BaseViewModel;

/**
 * Test 界面 ViewModel
 *
 */
public final class TestViewModel extends BaseViewModel {

  public TestViewModel(@NonNull Application application) {
    super(application);
  }

  public void onEnvironmentClick(View v) {
    Navigation.findNavController(v).navigate(R.id.action_to_environment);
  }

  public void onSplashDataClick(View v) {
    Navigation.findNavController(v).navigate(R.id.action_to_splash);
  }
}
