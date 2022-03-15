package com.hzlz.aviation.feature.account;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.repository.FavoriteRepository;
import com.hzlz.aviation.kernel.base.plugin.FavoritePlugin;
import com.hzlz.aviation.kernel.base.plugin.IFavoriteRepository;

/**
 *
 * @since 2020-03-23 17:01
 */
public final class FavoritePluginImpl implements FavoritePlugin {
  @NonNull
  @Override
  public IFavoriteRepository getFavoriteRepository() {
    return new FavoriteRepository();
  }
}
