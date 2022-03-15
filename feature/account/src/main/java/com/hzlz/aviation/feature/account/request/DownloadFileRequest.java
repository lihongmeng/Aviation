package com.hzlz.aviation.feature.account.request;

import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

/**
 *
 * @since 2020-03-07 17:00
 */
public final class DownloadFileRequest extends BaseRequest<ResponseBody> {
  //<editor-fold desc="属性">
  @Nullable
  private String mUrl;
  private long mRangeStart;
  //</editor-fold>

  //<editor-fold desc="设置参数">

  /**
   * 设置文件 Url
   *
   * @param url 文件 Url
   */
  public void setUrl(@NonNull String url) {
    if (!URLUtil.isNetworkUrl(url)) {
      throw new IllegalArgumentException("url is not a network url");
    }
    mUrl = url;
  }

  /**
   * 设置 Range Start
   *
   * @param rangeStart Range Start
   */
  public void setRangeStart(long rangeStart) {
    if (rangeStart < 0) {
      throw new IllegalArgumentException("range start < 0");
    }
    mRangeStart = rangeStart;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public Observable<ResponseBody> getObservable() {
    if (mUrl == null) {
      throw new NullPointerException("download file request with a null url");
    }
    return AccountAPI.Instance.get().downloadFile(
        mUrl, "bytes=" + mRangeStart + "-", "omitAppHeader"
    );
  }
  //</editor-fold>
}
