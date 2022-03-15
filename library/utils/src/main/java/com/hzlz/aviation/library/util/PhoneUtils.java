package com.hzlz.aviation.library.util;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

/**
 * 手机号工具类
 *
 *
 * @since 2020-01-14 17:38
 */
public final class PhoneUtils {
  //<editor-fold desc="属性">
  @Nullable
  private static volatile PhoneNumberUtil sPhoneNumberUtils;
  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private PhoneUtils() {

  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  @NonNull
  public static PhoneNumberUtil getPhoneNumberUtils(@NonNull Application application) {
    PhoneNumberUtil temp = sPhoneNumberUtils;
    if (temp == null) {
      synchronized (PhoneUtils.class) {
        temp = sPhoneNumberUtils;
        if (temp == null) {
          temp = PhoneNumberUtil.createInstance(application);
          sPhoneNumberUtils = temp;
        }
      }
    }
    return temp;
  }
  //</editor-fold>
}
