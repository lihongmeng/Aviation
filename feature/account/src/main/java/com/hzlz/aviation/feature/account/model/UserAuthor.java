package com.hzlz.aviation.feature.account.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableInt;

import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.base.model.anotation.Gender;
import com.hzlz.aviation.kernel.base.model.circle.CircleModule;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;

import java.util.Calendar;
import java.util.Date;

/**
 * 作者模型
 *
 *
 * @since 2020-03-03 20:18
 */
public final class UserAuthor extends AuthorModel implements IAdapterModel {
  //<editor-fold desc="属性">
  @Gender
  /** 性别 */
  private int gender;
  /** 年龄 */
  private Date birthday;
  /** 关注数 */
  private int followCount;
  /**加入圈子数*/
  private int followGroupCount;
  /** 粉丝数 */
  private int fansCount;
  /** 作品数 */
  private int compositionCount;
  /** 喜欢数 */
  private int favoriteCount;
  /** 省份id*/
  private int provinceId;
  /** 省份id */
  private String province;
  /** 城市id */
  private int cityId;
  /** 城市名称 */
  private String city;

  //导师信息
  private UgcAuthorModel mentor;
  //导师关联的组件信息
  private CircleModule gather;

  @Nullable
  private transient UserAuthorObservable mUserAuthorObservable;
  @NonNull
  private transient ObservableInt mModelPosition = new ObservableInt();
  //</editor-fold>
  //<editor-fold desc="API">
  public void update(@NonNull UserAuthor author) {
    setId(author.getId());
    setName(author.getName());
    setAvatar(author.getAvatar());
    setCoverUrl(author.getCoverUrl());
    setIntro(author.getIntro());
    setFollow(author.isFollow());
    setFollowMe(author.isFollowMe());
    setGender(author.getGender());
    setBirthday(author.getBirthday());
    setFollowCount(author.getFollowCount());
    setFollowGroupCount(author.getFollowGroupCount());
    setFansCount(author.getFansCount());
    setCompositionCount(author.getCompositionCount());
    setFavoriteCount(author.getFavoriteCount());
    setAuthentication(author.isAuthentication());
    setAuthenticationIntro(author.getAuthenticationIntro());
    setRegion(author.getProvince(),author.getCity());
    setMentor(author.getMentor());
  }

  @NonNull
  public UserAuthorObservable getAuthorObservable() {
    if (mUserAuthorObservable == null) {
      mUserAuthorObservable = new UserAuthorObservable();
      //
      update(this);
    }
    return mUserAuthorObservable;
  }
  //</editor-fold

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setGender(gender);
    }
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setAge(getAge());
    }
  }

  public int getFollowGroupCount() {
    return followGroupCount;
  }

  public void setFollowGroupCount(int followGroupCount) {
    this.followGroupCount = followGroupCount;
    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setFollowGroupCount(getFollowGroupCount());
    }
  }

  public int getFollowCount() {
    return followCount;
  }

  public void setFollowCount(int followCount) {
    this.followCount = followCount;

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setFollowCount(followCount);
    }
  }

  public int getFansCount() {
    return fansCount;
  }

  public void setFansCount(int fansCount) {
    this.fansCount = fansCount;

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setFansCount(fansCount);
    }
  }

  public int getCompositionCount() {
    return compositionCount;
  }

  public void setCompositionCount(int compositionCount) {
    this.compositionCount = compositionCount;

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setCompositionCount(compositionCount);
    }
  }

  public int getFavoriteCount() {
    return favoriteCount;
  }

  public void setFavoriteCount(int favoriteCount) {
    this.favoriteCount = favoriteCount;

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setFavoriteCount(favoriteCount);
    }
  }

  public int getAge() {
    if (birthday == null) {
      return 0;
    }
    int nowYear = Calendar.getInstance().get(Calendar.YEAR);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(birthday);
    int birthdayYear = calendar.get(Calendar.YEAR);
    int age = nowYear - birthdayYear;
    if (age < 0) {
      age = 0;
    }
    return age;
  }

  public String getProvince() {
    return province;
  }

  public String getCity() {
    return city;
  }

  public void setRegion(String province, String city) {
    if (mUserAuthorObservable != null && !TextUtils.isEmpty(province)) {
      mUserAuthorObservable.setRegion(province+" · "+city);
    }
  }

  //<editor-fold desc="方法实现">
  @Override public void setId(@NonNull String id) {
    super.setId(id);

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setSelf(super.isSelf());
    }
  }

  @Override public void setName(String name) {
    super.setName(name);

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setName(name);
    }
  }

  @Override public void setAvatar(String avatar) {
    super.setAvatar(avatar);

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setAvatarUrl(avatar);
    }
  }

  @Override public void setIntro(String intro) {
    super.setIntro(intro);

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setIntroduction(intro);
    }
  }

  @Override public void setFollow(boolean follow) {
    super.setFollow(follow);

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setIsFollowed(follow);
    }
  }

  @Override public void setFollowMe(boolean followMe) {
    super.setFollowMe(followMe);

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setIsFollowMe(followMe);
    }
  }

  @Override public void setCoverUrl(String mCoverUrl) {
    super.setCoverUrl(mCoverUrl);
  }

  @Override public void setType(int type) {
    super.setType(type);
  }

  public UgcAuthorModel getMentor() {
    return mentor;
  }

  public void setMentor(UgcAuthorModel mentor) {
    this.mentor = mentor;
  }

  public CircleModule getGather() {
    return gather;
  }

  public void setGather(CircleModule gather) {
    this.gather = gather;
  }

  @Override
  public void setModelPosition(int position) {
    mModelPosition.set(position);
  }

  @Override
  @NonNull
  public ObservableInt getModelPosition() {
    return mModelPosition;
  }
  //</editor-fold>

}
