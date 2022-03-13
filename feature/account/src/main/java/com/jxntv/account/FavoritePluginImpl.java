package com.jxntv.account;

import androidx.annotation.NonNull;
import com.jxntv.account.repository.FavoriteRepository;
import com.jxntv.base.plugin.FavoritePlugin;
import com.jxntv.base.plugin.IFavoriteRepository;

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
