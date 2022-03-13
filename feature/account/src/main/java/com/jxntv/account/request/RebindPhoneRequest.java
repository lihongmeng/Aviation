package com.jxntv.account.request;

import androidx.annotation.NonNull;
import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.User;
import com.jxntv.network.request.BaseGVideoMapRequest;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 换绑手机号请求
 *
 *
 * @since 2020-02-27 15:36
 */
public final class RebindPhoneRequest extends BaseGVideoMapRequest<User> {
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
  protected int getMaxParameterCount() {
    return 3;
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().rebindPhone(mParameters);
  }
  //</editor-fold>
}
