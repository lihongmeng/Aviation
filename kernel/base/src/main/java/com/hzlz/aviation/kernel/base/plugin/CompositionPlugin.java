package com.hzlz.aviation.kernel.base.plugin;

import androidx.annotation.NonNull;

import com.hzlz.aviation.library.ioc.Plugin;

/**
 * 作品模块接口
 *
 */
public interface CompositionPlugin extends Plugin {
  /**
   * 获取个人作品仓库
   */
  @NonNull
  ICompositionRepository getCompositionRepository();
}
