package com.jxntv.base.environment;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.utils.DeviceId;

/**
 * 环境管理
 *
 *
 * @since 2020-03-04 11:48
 */
public final class EnvironmentManager {
  //<editor-fold desc="属性">
  private static EnvironmentManager sInstance;
  @Nullable
  private IEnvironment mEnvironment;
  //测试包
  private boolean isTestProductFlavor = false;
  //</editor-fold>

  //<editor-fold desc="私有构造函数">
  private EnvironmentManager() {

  }
  //</editor-fold>

  //<editor-fold desc="API">
  public static EnvironmentManager getInstance() {
    if (sInstance == null) {
      sInstance = new EnvironmentManager();
    }
    return sInstance;
  }

  public void setEnvironment(IEnvironment environment) {
    if (environment == null) {
      throw new NullPointerException();
    }
    mEnvironment = environment;
  }

  @IEnvironment.Type
  public int getCurrentType() {
    if (mEnvironment == null) {
      throw new NullPointerException();
    }
    return mEnvironment.getCurrentType();
  }

  public void updateType(@IEnvironment.Type int type, String otherUrl) {
    if (mEnvironment == null) {
      throw new NullPointerException();
    }
    mEnvironment.updateType(type, otherUrl);
  }

  @NonNull
  public String getCurrentAPIUrl() {
    if (mEnvironment == null) {
      throw new NullPointerException();
    }
    return mEnvironment.getCurrentAPIUrl();
  }

  public void setTestProductFlavors(boolean isTestProductFlavor){
    this.isTestProductFlavor = isTestProductFlavor;
  }

  public boolean getIsTestProductFlavors(){
    switch (DeviceId.get()){
      case "500996265993BE56E6686E457BBFC65C577E4159":
      case "8D6E26BB1F08CCFA1E64E7148B06B70BDCC7A073":
      case "CE6EC91F4B5F60FB0C56487379BCE4CF3C28881C":
      case "6B18E1192A247DED033B3AF80B17AA94A15EDA04":
      case "FA41E04794F02BBF0A57182A278509D7606DAE30":
      case "65C4C3C4BBCA109CBADD925479FF23B31F7B93BA":
        return true;
      default:
        return isTestProductFlavor;
    }
  }

  //</editor-fold>
}
