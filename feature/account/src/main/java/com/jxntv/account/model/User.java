package com.jxntv.account.model;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import com.jxntv.account.R;
import com.jxntv.account.model.annotation.PrivacyRange;
import com.jxntv.account.model.annotation.Verification;
import com.jxntv.account.presistence.UserManager;
import com.jxntv.base.model.anotation.Gender;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;
import com.jxntv.utils.DateUtils;
import com.jxntv.utils.ResourcesUtils;
import java.util.Date;
import java.util.List;

/**
 * 用户模型
 *
 *
 * @since 2020-02-18 10:32
 */
public final class User implements Cloneable {
  //<editor-fold desc="属性">
  @Nullable
  @SerializedName("userId")
  private String mId;
  @Nullable
  @SerializedName("nickname")
  private String mNickname;
  @Gender
  @SerializedName("gender")
  private int mGender = Gender.NONE;
  @PrivacyRange
  @SerializedName("genderVisible")
  private int mGenderPrivacyRange = PrivacyRange.NONE;
  @Nullable
  @SerializedName("birthday")
  private Date mBirthday;
  @PrivacyRange
  @SerializedName("birthdayVisible")
  private int mBirthdayPrivacyRange = PrivacyRange.NONE;
  @Nullable
  @SerializedName("avatarImageUrl")
  private String mAvatarUrl;
  @SerializedName("hasRealnameAuth")
  private boolean mHasIdentityVerified;
  @Nullable
  @SerializedName("countryCode")
  private String mCountryCode;
  @Nullable
  @SerializedName("phone")
  private String mPhoneNumber;
  @Nullable
  @SerializedName("info")
  private String mDescription;
  @Nullable
  @SerializedName("newNickName")
  private NewValue mNewNickname;
  @Nullable
  @SerializedName("newIdentity")
  private NewValue mNewIdentity;
  @Nullable
  @SerializedName("newAvatar")
  private NewValue mNewAvatar;
  @Nullable
  @SerializedName("newInfo")
  private NewValue mNewDescription;
  @Nullable
  @SerializedName("platformUser")
  private int mPlatformUser;
  @Nullable
  @SerializedName("isAuthentication")
  private boolean mIsAuthentication;

  @Nullable
  @SerializedName("provinceId")
  private int mProvinceId;
  @Nullable
  @SerializedName("province")
  private String mProvince;
  @Nullable
  @SerializedName("cityId")
  private int mCityId;
  @Nullable
  @SerializedName("city")
  private String mCity;
  @Nullable
  @SerializedName("joinGroup")
  private List<String> mJoinGroup;

  // 自定义属性
  @Nullable
  private transient String mGenderValue;
  @Nullable
  private transient String mGenderPrivacyRangeValue;
  @Nullable
  private transient String mBirthdayChineseYMDValue;
  @Nullable
  private transient String mBirthdayPrivacyRangeValue;
  @Nullable
  private transient String mEncryptedPhoneNumberWithCountryCode;
  @Nullable
  private transient CharSequence mNewNicknameCharSequence;
  @Nullable
  private transient SpannableString mIdentityVerificationSpannable;
  @Nullable
  private transient UserObservable mUserObservable;
  @Nullable
  private transient UserAuthorObservable mUserAuthorObservable;
  //</editor-fold>

  //<editor-fold desc="子类">
  public static final class NewValue {
    //<editor-fold desc="属性">
    @Nullable
    @SerializedName("value")
    private String mValue;
    @Verification
    @SerializedName("auditStatus")
    private int mAuditStatus;
    @Nullable
    @SerializedName("auditHint")
    private String mAuditHint;
    //</editor-fold>

    //<editor-fold desc="Getter">

    @Nullable
    public String getValue() {
      return mValue;
    }

    @Verification
    public int getAuditStatus() {
      return mAuditStatus;
    }

    @Nullable
    public String getAuditHit() {
      return mAuditHint;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if (obj == this) {
        return true;
      }
      NewValue newValue = null;
      if (obj != null && NewValue.class.equals(obj.getClass())) {
        newValue = (NewValue) obj;
      }
      return newValue != null
          // value
          && ((newValue.mValue == null && mValue == null)
          || (newValue.mValue != null && newValue.mValue.equals(mValue)))
          // auditStatus
          && newValue.mAuditStatus == mAuditStatus
          // auditHint
          && ((newValue.mAuditHint == null && mAuditHint == null)
          || (newValue.mAuditHint != null && newValue.mAuditHint.equals(mAuditHint)));
    }
    //</editor-fold>
  }
  //</editor-fold>

  //<editor-fold desc="Setter">
  public void setId(@Nullable String id) {
    mId = id;
  }

  public void setNickname(@Nullable String nickname) {
    mNickname = nickname;
    if (mUserObservable != null) {
      mUserObservable.setNickname(nickname);
    }

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setName(nickname);
    }
  }

  public void setGender(@Gender int gender) {
    setGender(gender, mGenderPrivacyRange);

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setGender(gender);
    }
  }

  public void setGenderPrivacyRange(@PrivacyRange int genderPrivacyRange) {
    setGender(mGender, genderPrivacyRange);
  }

  private void setGender(@Gender int gender, @PrivacyRange int genderPrivacyRange) {
    // gender
    if (mGenderValue == null || mGender != gender) {
      switch (gender) {
        case Gender.MALE:
          mGenderValue = ResourcesUtils.getString(R.string.gender_item_view_male);
          break;
        case Gender.FEMALE:
          mGenderValue = ResourcesUtils.getString(R.string.gender_item_view_female);
          break;
        case Gender.NONE:
        default:
          mGenderValue = null;
          break;
      }
    }
    mGender = gender;
    // gender privacy
    if (mGenderValue == null) {
      mGenderPrivacyRangeValue = null;
    } else if (mGenderPrivacyRangeValue == null || mGenderPrivacyRange != genderPrivacyRange) {
      switch (genderPrivacyRange) {
        case PrivacyRange.PUBLIC:
          mGenderPrivacyRangeValue = ResourcesUtils.getString(R.string.privacy_range_public);
          break;
        case PrivacyRange.ONLY_SELF:
          mGenderPrivacyRangeValue = ResourcesUtils.getString(R.string.privacy_range_only_self);
          break;
        case PrivacyRange.NONE:
        default:
          mGenderPrivacyRangeValue = null;
          break;
      }
    }
    mGenderPrivacyRange = genderPrivacyRange;
    // observable
    if (mUserObservable != null) {
      mUserObservable.setGender(mGenderValue, mGenderPrivacyRangeValue);
    }
  }

  public void setBirthday(@Nullable Date birthday) {
    setBirthday(birthday, mBirthdayPrivacyRange);

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setBirthday(birthday);
    }
  }

  public void setBirthdayPrivacyRange(@PrivacyRange int birthdayPrivacyRange) {
    setBirthday(mBirthday, birthdayPrivacyRange);
  }

  private void setBirthday(@Nullable Date birthday, @PrivacyRange int birthdayPrivacyRange) {
    // birthday
    if (mBirthdayChineseYMDValue == null || birthday == null || !birthday.equals(mBirthday)) {
      if (birthday == null) {
        mBirthdayChineseYMDValue = null;
      } else {
        mBirthdayChineseYMDValue = DateUtils.getChineseYMD(birthday);
      }
    }
    mBirthday = birthday;
    // birthday privacy
    if (mBirthday == null) {
      mBirthdayPrivacyRangeValue = null;
    } else if (mBirthdayPrivacyRangeValue == null
        || mBirthdayPrivacyRange != birthdayPrivacyRange) {
      switch (birthdayPrivacyRange) {
        case PrivacyRange.PUBLIC:
          mBirthdayPrivacyRangeValue = ResourcesUtils.getString(R.string.privacy_range_public);
          break;
        case PrivacyRange.ONLY_SELF:
          mBirthdayPrivacyRangeValue = ResourcesUtils.getString(R.string.privacy_range_only_self);
          break;
        case PrivacyRange.NONE:
        default:
          mBirthdayPrivacyRangeValue = null;
          break;
      }
    }
    mBirthdayPrivacyRange = birthdayPrivacyRange;
    // observable
    if (mUserObservable != null) {
      mUserObservable.setBirthday(mBirthdayChineseYMDValue, mBirthdayPrivacyRangeValue);
    }
  }

  public void setAvatarUrl(@Nullable String avatarUrl) {
    mAvatarUrl = avatarUrl;
    if (mUserObservable != null) {
      mUserObservable.setAvatarUrl(mAvatarUrl);
    }

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setAvatarUrl(avatarUrl);
    }
  }

  public void setDescription(@Nullable String description) {
    mDescription = description;
    if (mUserObservable != null) {
      mUserObservable.setDescription(mDescription);
    }

    if (mUserAuthorObservable != null) {
      mUserAuthorObservable.setIntroduction(description);
    }
  }

  public void setHasIdentityVerified(boolean hasIdentityVerified) {
    setIdentityVerification(hasIdentityVerified, mNewIdentity);
  }

  public void setCountryCode(@Nullable String countryCode) {
    setPhoneNumber(countryCode, mPhoneNumber);
  }

  public void setPhoneNumber(@Nullable String phoneNumber) {
    setPhoneNumber(mCountryCode, phoneNumber);
  }

  public void setIsAuthentication(@Nullable boolean isAuthentication) {
    this.mIsAuthentication = isAuthentication;
    if (mUserObservable != null) {
      mUserObservable.setIsAuthentication(isAuthentication);
    }
  }

  private void setPhoneNumber(@Nullable String countryCode, @Nullable String phoneNumber) {
    if (mEncryptedPhoneNumberWithCountryCode == null
        || countryCode == null || !countryCode.equals(mCountryCode)
        || phoneNumber == null || !phoneNumber.equals(mPhoneNumber)) {
      if (TextUtils.isEmpty(phoneNumber)) {
        mEncryptedPhoneNumberWithCountryCode = null;
      } else {
        // 处理国家代码
        StringBuilder result = new StringBuilder();
        if (!TextUtils.isEmpty(countryCode)) {
          result.append(countryCode).append(" ");
        }
        // 处理手机号
        if (phoneNumber.length() == 11) {
          result.append(phoneNumber.substring(0, 3))
              .append("****")
              .append(phoneNumber.substring(7));
        } else {
          result.append(phoneNumber);
        }
        mEncryptedPhoneNumberWithCountryCode = result.toString();
      }
    }
    mCountryCode = countryCode;
    mPhoneNumber = phoneNumber;
    if (mUserObservable != null) {
      mUserObservable.setEncryptedPhoneNumberWithCountryCode(mEncryptedPhoneNumberWithCountryCode);
    }
  }

  public void setNewNickname(@Nullable NewValue newNickname) {
    if (mNewNicknameCharSequence == null
        || newNickname == null
        || !newNickname.equals(mNewNickname)) {
      if (newNickname == null || TextUtils.isEmpty(newNickname.mValue)) {
        mNewNicknameCharSequence = null;
      } else {
        SpannableString ss = null;
        String hint = newNickname.mAuditHint == null ? "" : newNickname.mAuditHint;
        switch (newNickname.getAuditStatus()) {
          case Verification.REJECT:
          case Verification.VERIFYING:
            ss = new SpannableString(newNickname.mValue + " " + hint);
            break;
          case Verification.VERIFIED:
          default:
            mNewNicknameCharSequence = null;
            break;
        }
        if (ss != null) {
          int end = ss.length();
          int start = end - hint.length();
          ss.setSpan(
              new ForegroundColorSpan(ResourcesUtils.getColor(R.color.t_color06)),
              start,
              end,
              Spannable.SPAN_INCLUSIVE_EXCLUSIVE
          );
          mNewNicknameCharSequence = ss;
        }
      }
    }
    mNewNickname = newNickname;
    if (mUserObservable != null) {
      mUserObservable.setNewNickname(mNewNicknameCharSequence);
    }
  }

  public void setNewIdentity(@Nullable NewValue identity) {
    setIdentityVerification(mHasIdentityVerified, identity);
  }

  private void setIdentityVerification(
      boolean hasIdentityVerified,
      @Nullable NewValue newIdentity) {
    if (mIdentityVerificationSpannable == null
        || hasIdentityVerified != mHasIdentityVerified
        || newIdentity == null || !newIdentity.equals(mNewIdentity)) {
      if (hasIdentityVerified) {
        mIdentityVerificationSpannable = new SpannableString(
            ResourcesUtils.getString(R.string.all_account_has_identity_verified)
        );
      } else {
        if (newIdentity == null || newIdentity.mAuditHint == null) {
          mIdentityVerificationSpannable = new SpannableString(
              ResourcesUtils.getString(R.string.all_account_goto_identity_verification)
          );
        } else {
          mIdentityVerificationSpannable = new SpannableString(newIdentity.mAuditHint);
        }
        mIdentityVerificationSpannable.setSpan(
            new ForegroundColorSpan(ResourcesUtils.getColor(R.color.t_color06)),
            0,
            mIdentityVerificationSpannable.length(),
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        );
      }
    }
    mHasIdentityVerified = hasIdentityVerified;
    mNewIdentity = newIdentity;
    if (mUserObservable != null) {
      boolean canVerifyIdentity = true;
      if (hasIdentityVerified) { //已经审核过
        canVerifyIdentity = false;
      } else if (newIdentity != null && newIdentity.mAuditStatus == Verification.VERIFYING) { //审核中
        canVerifyIdentity = false;
      } else if (newIdentity != null && newIdentity.mAuditStatus == Verification.REJECT) { //被拒绝
      } else {
        mIdentityVerificationSpannable = null;
      }
      mUserObservable.setCanVerifyIdentity(canVerifyIdentity);
      mUserObservable.setIdentityVerification(mIdentityVerificationSpannable);
    }
  }

  private void setNewAvatar(@Nullable NewValue newAvatar) {
    mNewAvatar = newAvatar;
  }
  private void setNewDescription(@Nullable NewValue newDescription) {
    mNewDescription = newDescription;
  }

  private void setRegion(String province,int provinceId,String city,int cityId){
    this.mProvince = province;
    this.mProvinceId = provinceId;
    this.mCity = city;
    this.mCityId = cityId;
    if (mUserObservable!=null) {
      mUserObservable.setRegion(province,city);
    }
  }
  //</editor-fold>

  //<editor-fold desc="Getter">
  @Nullable
  public String getId() {
    return mId;
  }

  @Nullable
  public String getNickname() {
    return mNickname;
  }

  @Nullable
  public int getPlatformUser() {
    return mPlatformUser;
  }

  @Nullable
  public void setPlatformUser(@Nullable int platformUser) {
    mPlatformUser = platformUser;
  }

  @Gender
  public int getGender() {
    return mGender;
  }

  @PrivacyRange
  public int getGenderPrivacyRange() {
    return mGenderPrivacyRange;
  }

  @Nullable
  public Date getBirthday() {
    return mBirthday;
  }

  @PrivacyRange
  public int getBirthdayPrivacyRange() {
    return mBirthdayPrivacyRange;
  }

  @Nullable
  public String getAvatarUrl() {
    return mAvatarUrl;
  }

  @Nullable
  public String getDescription() {
    return mDescription;
  }

  public boolean hasIdentityVerified() {
    return mHasIdentityVerified;
  }

  @Nullable
  public String getCountryCode() {
    return mCountryCode;
  }

  @Nullable
  public String getPhoneNumber() {
    return mPhoneNumber;
  }

  @Nullable
  public NewValue getNewNickname() {
    return mNewNickname;
  }

  @Nullable
  public NewValue getNewIdentity() {
    return mNewIdentity;
  }

  @Nullable
  public int getProvinceId() {
    return mProvinceId;
  }

  @Nullable
  public String getProvince() {
    return mProvince;
  }

  @Nullable
  public int getCityId() {
    return mCityId;
  }

  @Nullable
  public String getCity() {
    return mCity;
  }

  public List<String> getJoinGroup(){
    return mJoinGroup;
  }

  public void setJoinGroup(List<String> mJoinGroup){
    this.mJoinGroup = mJoinGroup;
  }

  //</editor-fold>

  //<editor-fold desc="API">
  public void update(@NonNull User user) {
    setId(user.mId);
    setNickname(user.mNickname);
    setGender(user.mGender);
    setGenderPrivacyRange(user.mGenderPrivacyRange);
    setBirthday(user.mBirthday);
    setBirthdayPrivacyRange(user.mBirthdayPrivacyRange);
    setPlatformUser(user.mPlatformUser);
    setRealUserAvatar(user.mAvatarUrl);
    if (user.mNewAvatar != null && user.mNewAvatar.mValue != null && user.mNewAvatar.mAuditStatus != Verification.REJECT) {
      setAvatarUrl(user.mNewAvatar.mValue);
    } else {
      setAvatarUrl(user.mAvatarUrl);
    }
    if (user.mNewDescription != null && user.mNewDescription.mValue != null && user.mNewDescription.mAuditStatus != Verification.REJECT) {
      setDescription(user.mNewDescription.mValue);
    } else {
      setDescription(user.mDescription);
    }
    setHasIdentityVerified(user.mHasIdentityVerified);
    setCountryCode(user.mCountryCode);
    setPhoneNumber(user.mPhoneNumber);
    setNewNickname(user.mNewNickname);
    setNewIdentity(user.mNewIdentity);
    setNewAvatar(user.mNewAvatar);
    setNewDescription(user.mNewDescription);
    setIsAuthentication(user.mIsAuthentication);
    setRegion(user.mProvince,user.mProvinceId,user.mCity,user.mCityId);
    if (mUserObservable != null) {
      mUserObservable.setHasLoggedIn(UserManager.hasLoggedIn());
    }
    setJoinGroup(user.mJoinGroup);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @NonNull
  public UserObservable getUserObservable() {
    if (mUserObservable == null) {
      mUserObservable = new UserObservable();
      // 设置属性
      update(this);
    }
    return mUserObservable;
  }

  @NonNull
  public UserAuthorObservable getUserAuthorObservable() {
    if (mUserAuthorObservable == null) {
      mUserAuthorObservable = new UserAuthorObservable();
      update(this);
    }
    return mUserAuthorObservable;
  }


  private String mRealUserAvatar;
  private void setRealUserAvatar(String url){
    this.mRealUserAvatar = url;
  }
  public String getRealUserAvatar(){
    return mRealUserAvatar;
  }
  //</editor-fold>
}
