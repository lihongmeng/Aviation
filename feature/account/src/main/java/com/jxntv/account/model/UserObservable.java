package com.jxntv.account.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import com.jxntv.base.utils.StringUtils;

/**
 * 可观察的用户模型
 *
 *
 * @since 2020-02-17 17:27
 */
public final class UserObservable {
  //<editor-fold desc="属性">
  @NonNull
  public ObservableField<String> nickname = new ObservableField<>();
  @NonNull
  public ObservableField<CharSequence> newNickname = new ObservableField<>();
  @NonNull
  public ObservableField<String> encryptedPhoneNumberWithCountryCode = new ObservableField<>();
  @NonNull
  public ObservableField<String> gender = new ObservableField<>();
  @NonNull
  public ObservableField<String> genderVisibility = new ObservableField<>();
  @NonNull
  public ObservableField<String> birthday = new ObservableField<>();
  @NonNull
  public ObservableField<String> birthdayVisibility = new ObservableField<>();
  @NonNull
  public ObservableBoolean canVerifyIdentity = new ObservableBoolean();
  @NonNull
  public ObservableField<CharSequence> identityVerification = new ObservableField<>();
  @NonNull
  public ObservableField<String> description = new ObservableField<>();
  @NonNull
  public ObservableField<String> newDescription = new ObservableField<>();
  @NonNull
  public ObservableField<String> avatarUrl = new ObservableField<>();
  @NonNull
  public ObservableBoolean hasLoggedIn = new ObservableBoolean();
  @NonNull
  public ObservableBoolean isAuthentication = new ObservableBoolean();
  @NonNull
  public ObservableField<String> region = new ObservableField<>();
  //</editor-fold>

  //<editor-fold desc="API">
  void setNickname(@Nullable String nickName) {
    nickName = StringUtils.filterWhiteSpace(nickName);
    this.nickname.set(nickName);
  }

  void setEncryptedPhoneNumberWithCountryCode(
      @Nullable String encryptedPhoneNumberWithCountryCode) {
    this.encryptedPhoneNumberWithCountryCode.set(encryptedPhoneNumberWithCountryCode);
  }

  void setGender(@Nullable String gender, @Nullable String genderVisibility) {
    this.gender.set(gender);
    this.genderVisibility.set(genderVisibility);
  }

  void setBirthday(@Nullable String birthday, @Nullable String birthdayVisibility) {
    this.birthday.set(birthday);
    this.birthdayVisibility.set(birthdayVisibility);
  }

  void setAvatarUrl(@Nullable String avatarUrl) {
    this.avatarUrl.set(avatarUrl);
  }

  void setDescription(@Nullable String description) {
    description = StringUtils.filterWhiteSpace(description);
    this.description.set(description);
  }

  void setNewNickname(@Nullable CharSequence newNickname) {
    this.newNickname.set(newNickname);
  }

  void setCanVerifyIdentity(boolean canVerifyIdentity) {
    this.canVerifyIdentity.set(canVerifyIdentity);
  }

  void setIdentityVerification(@Nullable CharSequence identityVerification) {
    this.identityVerification.set(identityVerification);
  }

  void setIsAuthentication(@Nullable boolean isAuthentication) {
    this.isAuthentication.set(isAuthentication);
  }

  void setHasLoggedIn(boolean hasLoggedIn) {
    this.hasLoggedIn.set(hasLoggedIn);
  }

  void setRegion(String province,String city){
    if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city)){
      this.region.set(null);
    }else {
      this.region.set(province + " " + city);
    }
  }
  //</editor-fold>
}
