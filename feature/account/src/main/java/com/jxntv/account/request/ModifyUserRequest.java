package com.jxntv.account.request;

import androidx.annotation.NonNull;
import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.User;
import com.jxntv.account.model.annotation.PrivacyRange;
import com.jxntv.base.model.anotation.Gender;
import com.jxntv.network.request.BaseGVideoMapRequest;
import io.reactivex.rxjava3.core.Observable;
import java.util.Date;
import retrofit2.Response;

/**
 * 修改用户请求
 *
 *
 * @since 2020-02-27 16:58
 */
public final class ModifyUserRequest extends BaseGVideoMapRequest<User> {
  //<editor-fold desc="API">

  /**
   * 设置昵称
   *
   * @param nickname 昵称
   */
  public void setNickname(@NonNull String nickname) {
    mParameters.put("nickname", nickname);
  }

  /**
   * 设置性别
   *
   * @param gender 性别
   * @param genderRange 性别范围
   */
  public void setGender(@Gender int gender, @PrivacyRange int genderRange) {
    mParameters.put("gender", gender);
    mParameters.put("genderVisible", genderRange);
  }

  /**
   * 设置生日
   *
   * @param birthday 生日
   * @param birthdayRange 生日范围
   */
  public void setBirthday(@NonNull Date birthday, @PrivacyRange int birthdayRange) {
    mParameters.put("birthday", birthday);
    mParameters.put("birthdayVisible", birthdayRange);
  }

  /**
   * 设置头像
   *
   * @param avatarFileId 头像文件 id
   */
  public void setAvatar(@NonNull String avatarFileId) {
    mParameters.put("avatarFileId", avatarFileId);
  }

  /**
   * 设置地区
   *
   * @param provinceId 省份id
   * @param cityId 城市id
   */
  public void setRegion(int provinceId,String province,int cityId, String city) {
    mParameters.put("provinceId", provinceId);
    mParameters.put("cityId", cityId);
    mParameters.put("province", province);
    mParameters.put("city", city);
  }

  /**
   * 设置简介
   *
   * @param description 简介
   */
  public void setDescription(@NonNull String description) {
    mParameters.put("info", description);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getMaxParameterCount() {
    return 6;
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().modifyUser(mParameters);
  }
  //</editor-fold>
}
