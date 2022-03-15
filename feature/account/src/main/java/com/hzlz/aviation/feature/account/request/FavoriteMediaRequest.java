package com.hzlz.aviation.feature.account.request;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 收藏资源请求
 *
 *
 * @since 2020-03-11 16:14
 */
public final class FavoriteMediaRequest extends BaseGVideoMapRequest<Integer> {

  //<editor-fold desc="设置参数">

  /**
   * 设置资源 id
   *
   * @param mediaId 资源 id
   */
  public void setMediaId(@NonNull String mediaId) {
    mParameters.put("mediaId", mediaId);
  }

  /**
   * 设置是否收藏
   *
   * @param favorite true : 收藏；false : 不收藏
   */
  public void setFavorite(boolean favorite) {
    mParameters.put("type", favorite);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getMaxParameterCount() {
    return 2;
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().favoriteMedia(mParameters);
  }
  //</editor-fold>
}
