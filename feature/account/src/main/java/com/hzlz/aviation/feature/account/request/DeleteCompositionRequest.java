package com.hzlz.aviation.feature.account.request;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 删除作品
 *
 */
public final class DeleteCompositionRequest extends BaseGVideoRequest<Object> {
  //<editor-fold desc="方法实现">

  private String mMediaId;
  public void setMediaId(String mediaId) {
    mMediaId = mediaId;
  }
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    if (TextUtils.isEmpty(mMediaId)) {
      throw new NullPointerException("media id is null");
    }
    return AccountAPI.Instance.get().deleteComposition(mMediaId);
  }
  //</editor-fold>
}
