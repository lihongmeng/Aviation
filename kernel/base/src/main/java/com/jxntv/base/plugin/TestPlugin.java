package com.jxntv.base.plugin;

import android.view.View;
import androidx.annotation.NonNull;
import com.jxntv.base.BaseFragment;
import com.jxntv.ioc.Plugin;

/**
 * Test 接口
 *
 */
public interface TestPlugin extends Plugin {

  /**
   * 添加 Test 目的地
   *
   * @param fragment Fragment
   */
  void addDestination(@NonNull BaseFragment fragment);

  /**
   * 跳转 WebView 界面
   *
   * @param view 控件
   */
  void startFragment(@NonNull View view);
}
