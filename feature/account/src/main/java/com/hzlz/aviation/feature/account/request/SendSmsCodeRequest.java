package com.hzlz.aviation.feature.account.request;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 发送验证码请求
 *
 *
 * @since 2020-01-13 15:42
 */
public final class SendSmsCodeRequest extends BaseGVideoMapRequest<Object> {
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
   * 业务类型
   * <ul>
   * <li>1 : 登录</li>
   * <li>2 : 重新绑定手机号</li>
   * <li>3 : 切换账号</li>
   * </ul>
   *
   * @param type 类型
   */
  public void setType(int type) {
    mParameters.put("type", type);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    if (mParameters.size() != 3) {
      throw new IllegalStateException("parameters are invalid for sending sms code request");
    }
    return AccountAPI.Instance.get().sendSmsCode(mParameters);
  }

  @Override
  protected int getMaxParameterCount() {
    return 2;
  }
  //</editor-fold>
}
