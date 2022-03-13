package com.jxntv.account.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

/**
 * 登录响应
 *
 *
 * @since 2020-01-13 16:05
 */
public final class LoginResponse {
  //<editor-fold desc="方法实现">
  @Nullable
  @SerializedName("user")
  private User mUser;
  @Nullable
  @SerializedName("token")
  private String mToken;
  //</editor-fold>

  //<editor-fold desc="Getter">

  @NonNull
  public User getUser() {
    if (mUser == null) {
      throw new NullPointerException("user is null, please check api response");
    }
    return mUser;
  }

  @NonNull
  public String getToken() {
    if (mToken == null) {
      throw new NullPointerException("token is null, please check api response");
    }
    return mToken;
  }

  //</editor-fold>
}
