package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.GVideoFile;
import com.jxntv.network.request.BaseGVideoMapRequest;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 *
 * @since 2020-03-04 17:40
 */
public final class CreateFileRequest extends BaseGVideoMapRequest<GVideoFile> {

  //<editor-fold desc="设置参数">

  /**
   * 设置文件业务类型
   *
   * @param fileBusinessType 文件业务类型
   */
  public void setFileBusinessType(int fileBusinessType) {
    mParameters.put("bizType", fileBusinessType);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().createFile(mParameters);
  }

  @Override
  protected int getMaxParameterCount() {
    return 1;
  }

  //</editor-fold>
}
