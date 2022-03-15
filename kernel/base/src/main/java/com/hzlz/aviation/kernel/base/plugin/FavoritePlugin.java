package com.hzlz.aviation.kernel.base.plugin;

import androidx.annotation.NonNull;

import com.hzlz.aviation.library.ioc.Plugin;

/**
 * 收藏接口
 *
 *
 * @since 2020-03-23 16:58
 */
public interface FavoritePlugin extends Plugin {
  /**
   * 获取收藏仓库
   */
  @NonNull
  IFavoriteRepository getFavoriteRepository();
}
