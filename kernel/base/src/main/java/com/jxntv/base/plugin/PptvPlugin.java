package com.jxntv.base.plugin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.jxntv.base.BaseFragment;
import com.jxntv.ioc.Plugin;

/**
 * PPTV 模块接口
 */
public interface PptvPlugin extends Plugin {
  /**
   * 获取PPTV Fragment
   * @return
   */
  BaseFragment getPptvFragment();
  /**
   * 添加目的地，解决后续navigation跳转的问题
   *
   * @param fragment BaseFragment
   */
  void addDestinations(@NonNull BaseFragment fragment);
}
