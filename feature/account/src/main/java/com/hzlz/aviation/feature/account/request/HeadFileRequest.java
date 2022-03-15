package com.hzlz.aviation.feature.account.request;

import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * Head 文件请求
 *
 *
 * @since 2020-03-07 16:56
 */
public final class HeadFileRequest extends BaseRequest<Response<Void>> {
  //<editor-fold desc="属性">
  @Nullable
  private String mUrl;
  //</editor-fold>

  //<editor-fold desc="设置参数">
  public void setUrl(@NonNull String url) {
    if (!URLUtil.isNetworkUrl(url)) {
      throw new IllegalArgumentException("url is not a network url !!!");
    }
    mUrl = url;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public Observable<Response<Void>> getObservable() {
    if (mUrl == null) {
      throw new NullPointerException("head file request with a null url");
    }
    return AccountAPI.Instance.get().headFile(mUrl, "omitAppHeader");
  }
  //</editor-fold>
}
