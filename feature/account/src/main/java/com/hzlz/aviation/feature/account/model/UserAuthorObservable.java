package com.hzlz.aviation.feature.account.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.hzlz.aviation.feature.account.presistence.UserManager;
import com.hzlz.aviation.kernel.base.utils.StringUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 可观察的作者模型
 *
 *
 * @since 2020-03-03 20:41
 */
public final class UserAuthorObservable {
  //<editor-fold desc="属性">
  @NonNull
  private ObservableField<String> name = new ObservableField<>();
  @NonNull
  private ObservableField<String> avatar = new ObservableField<>();
  @NonNull
  private ObservableField<String> description = new ObservableField<>();
  @NonNull
  private ObservableField<String> region = new ObservableField<>();
  @NonNull
  private ObservableInt gender = new ObservableInt();
  @NonNull
  private ObservableInt age = new ObservableInt();
  @NonNull
  private ObservableField<String> followCount = new ObservableField<>();
  @NonNull
  private ObservableField<String> followGroupCount = new ObservableField<>();
  @NonNull
  private ObservableField<String> fansCount = new ObservableField<>();
  @NonNull
  private ObservableField<String> compositionCount = new ObservableField<>();
  @NonNull
  private ObservableField<String> favoriteCount = new ObservableField<>();
  @NonNull
  public ObservableField<String> coverUrl = new ObservableField<>();
  @NonNull
  public ObservableBoolean self = new ObservableBoolean();
  @NonNull
  public ObservableBoolean isFollowed = new ObservableBoolean();
  @NonNull
  public ObservableBoolean isFollowMe = new ObservableBoolean();
  @NonNull
  private final ObservableBoolean isAuthentication = new ObservableBoolean();
  @NonNull
  private final ObservableField<String> authenticationIntro = new ObservableField<>();
  //</editor-fold>

  //<editor-fold desc="API">

  @NonNull public ObservableField<String> getName() {
    if (self.get()) {
      return UserManager.getCurrentUser().getUserAuthorObservable().name;
    }
    return name;
  }

  @NonNull public ObservableField<String> getAvatarUrl() {
    if (self.get()) {
      return UserManager.getCurrentUser().getUserAuthorObservable().avatar;
    }
    return avatar;
  }

  @NonNull public ObservableField<String> getIntroduction() {
    if (self.get()) {
      return UserManager.getCurrentUser().getUserAuthorObservable().description;
    }
    return description;
  }

  @NonNull public ObservableInt getGender() {
    if (self.get()) {
      return UserManager.getCurrentUser().getUserAuthorObservable().gender;
    }
    return gender;
  }

  @NonNull public ObservableInt getAge() {
    if (self.get()) {
      return UserManager.getCurrentUser().getUserAuthorObservable().age;
    }
    return age;
  }

  @NonNull public ObservableField<String> getRegion() {
    return region;
  }

  @NonNull public ObservableBoolean getIsAuthentication() {
    return isAuthentication;
  }

  @NonNull public ObservableField<String> getAuthenticationIntro() {
    return authenticationIntro;
  }

  @NonNull public ObservableField<String> getFollowCount() {
    return followCount;
  }

  @NonNull public ObservableField<String> getFollowGroupCount() {
    return followGroupCount;
  }

  @NonNull public ObservableField<String> getFansCount() {
    return fansCount;
  }

  @NonNull public ObservableField<String> getCompositionCount() {
    return compositionCount;
  }

  @NonNull public ObservableField<String> getFavoriteCount() {
    return favoriteCount;
  }

  void setName(@Nullable String name) {
    name = StringUtils.filterWhiteSpace(name);
    this.name.set(name);
  }

  void setAvatarUrl(@Nullable String avatarUrl) {
    this.avatar.set(avatarUrl);
  }

  void setCoverUrl(@Nullable String coverUrl) {
    this.coverUrl.set(coverUrl);
  }

  void setIntroduction(@Nullable String introduction) {
    introduction = StringUtils.filterWhiteSpace(introduction);
    this.description.set(introduction);
  }

  void setIsFollowed(boolean isFollowed) {
    this.isFollowed.set(isFollowed);
  }

  void setIsAuthentication(boolean isAuthentication) {
    this.isAuthentication.set(isAuthentication);
  }

  void setAuthenticationIntro(@Nullable String authenticationIntro) {
    this.authenticationIntro.set(authenticationIntro);
  }

  void setIsFollowMe(boolean isFollowMe) {
    this.isFollowMe.set(isFollowMe);
  }

  void setGender(int gender) {
    this.gender.set(gender);
  }

  void setAge(int age) {
    this.age.set(age);
  }

  void setRegion(String region){
    this.region.set(region);
  }

  void setFollowCount(int followCount) {
    String count = formatCount(followCount);
    this.followCount.set(count);
  }

  void setFollowGroupCount(int followGroupCount) {
    String count = formatCount(followGroupCount);
    this.followGroupCount.set(count);
  }

  void setFansCount(int fansCount) {
    String count = formatCount(fansCount);
    this.fansCount.set(count);
  }

  void setCompositionCount(int compositionCount) {
    String count = formatCount(compositionCount);
    this.compositionCount.set(count);
  }

  void setFavoriteCount(int favoriteCount) {
    String count = formatCount(favoriteCount);
    this.favoriteCount.set(count);
  }

  void setSelf(boolean self) {
    this.self.set(self);
  }

  void setBirthday(Date birthday) {
    int yearsOld = 0;
    if (birthday != null) {
      int nowYear = Calendar.getInstance().get(Calendar.YEAR);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(birthday);
      int birthdayYear = calendar.get(Calendar.YEAR);
      yearsOld = nowYear - birthdayYear;
      if (yearsOld < 0) {
        yearsOld = 0;
      }
    }

    this.age.set(yearsOld);
  }
  //</editor-fold>


  /** 评论数显示增加后缀限制 */
  private static final int NUM_LIMIT = 10000;
  /** 评论数显示限制上限 */
  private static final int NUM_MAX_LIMIT = 100000;
  private String formatCount(int count) {
    if (count <= NUM_LIMIT) {
      return String.valueOf(count);
    }

    int nums = Math.min(count, NUM_MAX_LIMIT);
    return nums / NUM_LIMIT + "w+";
  }
}
