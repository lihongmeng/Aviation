package com.hzlz.aviation.feature.account.request;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.User;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 身份认证请求
 *
 *
 * @since 2020-03-06 16:09
 */
public final class AuthenticateIdCardRequest extends BaseGVideoMapRequest<User> {

  //<editor-fold desc="设置参数">

  /**
   * 设置身份证号
   *
   * @param idCardNumber 身份证号
   */
  public void setIdCardNumber(@NonNull String idCardNumber) {
    mParameters.put("id", idCardNumber);
  }

  /**
   * 设置身份证正面文件 id
   *
   * @param idCardFrontImageFileId 身份证正面文件 id
   */
  public void setIdCardFrontImageFileId(@NonNull String idCardFrontImageFileId) {
    mParameters.put("frontImage", idCardFrontImageFileId);
  }

  /**
   * 设置身份证背面文件 id
   *
   * @param idCardBackImageFileId 身份证正面文件 id
   */
  public void setIdCardBackImageFileId(@NonNull String idCardBackImageFileId) {
    mParameters.put("backImage", idCardBackImageFileId);
  }

  /**
   * 设置手持身份证文件 id
   *
   * @param idCardSelfImageFileId 身份证正面文件 id
   */
  public void setIdCardSelfImageFileId(@NonNull String idCardSelfImageFileId) {
    mParameters.put("handImage", idCardSelfImageFileId);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getMaxParameterCount() {
    return 4;
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    if (mParameters.size() != 4) {
      throw new IllegalStateException("parameters' invalid");
    }
    return AccountAPI.Instance.get().authenticateIdCard(mParameters);
  }
  //</editor-fold>
}
