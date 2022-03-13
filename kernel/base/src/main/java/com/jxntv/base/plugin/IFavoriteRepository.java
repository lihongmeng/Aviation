package com.jxntv.base.plugin;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Observable;

/**
 *
 * @since 2020-03-23 16:59
 */
public interface IFavoriteRepository {
  /**
   * 收藏资源
   *
   * @param mediaId 资源 id
   * @param favorite 是否收藏
   */
  @NonNull
  Observable<Boolean> favoriteMedia(@NonNull String mediaId, boolean favorite);
}
