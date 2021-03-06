package com.hzlz.aviation.feature.account.request;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.LoginResponse;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 切换账号请求
 *
 *
 * @since 2020-01-13 16:05
 */
public final class SwitchAccountRequest extends BaseGVideoMapRequest<LoginResponse> {
  //<editor-fold desc="API">

  /**
   * 设置手机号
   *
   * @param phoneNumber 手机号
   */
  public void setPhoneNumber(@NonNull String phoneNumber) {
    mParameters.put("phone", phoneNumber);
  }

  /**
   * 设置国家代码
   *
   * @param countryCode 国家代码
   */
  public void setCountryCode(@NonNull String countryCode) {
    mParameters.put("countryCode", countryCode);
  }

  /**
   * 设置短信验证码
   *
   * @param smsCode 短信验证码
   */
  public void setSmsCode(@NonNull String smsCode) {
    mParameters.put("smsCode", smsCode);
  }

  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().switchAccount(mParameters);
  }

  @Override
  protected int getMaxParameterCount() {
    return 3;
  }
  //</editor-fold>
}
