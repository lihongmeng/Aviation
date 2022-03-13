package com.jxntv.account.model;

import androidx.annotation.NonNull;

/**
 * 国家模型
 *
 *
 * @since 2020-01-14 16:29
 */
public final class Country implements ICountry {
  //<editor-fold desc="属性">
  @NonNull
  private String mCountryName;
  @NonNull
  private String mDisplayCountryCode;
  @NonNull
  private String mDisplayName;
  //</editor-fold>

  //<editor-fold desc="构造函数">

  public Country(
      @NonNull String countryName,
      @NonNull String displayCountryCode,
      @NonNull String displayName) {
    mCountryName = countryName;
    mDisplayCountryCode = displayCountryCode;
    mDisplayName = displayName;
  }

  //</editor-fold>

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  public String getCountryName() {
    return mCountryName;
  }

  @NonNull
  @Override
  public String getDisplayCountryCode() {
    return mDisplayCountryCode;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return mDisplayName;
  }

  @Override
  public String toString() {
    return "Country{" +
        "mCountryName='" + mCountryName + '\'' +
        ", mDisplayCountryCode='" + mDisplayCountryCode + '\'' +
        ", mDisplayName='" + mDisplayName + '\'' +
        '}';
  }
  //</editor-fold>
}
