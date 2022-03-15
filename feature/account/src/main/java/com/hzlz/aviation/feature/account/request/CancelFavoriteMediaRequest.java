package com.hzlz.aviation.feature.account.request;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 取消收藏媒体请求
 *
 *
 * @since 2020-03-17 16:40
 */
public final class CancelFavoriteMediaRequest extends BaseGVideoMapRequest<Object> {
  //<editor-fold desc="方法实现">

  /**
   * 设置收藏 id
   *
   * @param favoriteId 收藏 id
   */
  public void setFavoriteId(@NonNull String favoriteId) {
    mParameters.put("favoriteId", favoriteId);
  }

  /**
   * 设置媒体 id
   *
   * @param mediaId 媒体 id
   */
  public void setMediaId(@NonNull String mediaId) {
    mParameters.put("mediaId", mediaId);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getMaxParameterCount() {
    return 2;
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    if (mParameters.size() != 2) {
      throw new RuntimeException("invalid parameters for cancel favorite media request");
    }
    return AccountAPI.Instance.get().cancelFavorite(mParameters);
  }
  //</editor-fold>
}
