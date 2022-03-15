package com.hzlz.aviation.app;

import static com.hzlz.aviation.kernel.base.environment.IEnvironment.Type.OTHER;
import static com.hzlz.aviation.kernel.base.environment.IEnvironment.Type.RELEASE;
import static com.hzlz.aviation.kernel.base.environment.IEnvironment.Type.TEST_190;
import static com.hzlz.aviation.kernel.base.environment.IEnvironment.Type.TEST_47;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.environment.EnvironmentManager;
import com.hzlz.aviation.kernel.base.environment.IEnvironment;
import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;
import com.hzlz.aviation.kernel.network.NetworkManager;

/**
 * 今视频环境
 *
 *
 * @since 2020-03-04 12:22
 */
public final class GVideoEnvironment implements IEnvironment {
  //<editor-fold desc="常量">

  // 开发环境
  private static final String TEST_API_URL_190 = "http://api.todayvideo.net/";
  // 测试环境
  private static final String TEST_API_URL_47 = "http://api.jspdemo.jxtvcn.com.cn/";
  //正式环境地址
  private static final String RELEASE_API_URL = "https://app.jxgdw.com/";

  private static final String ENVIRONMENT_FILE_NAME = "gvideo_environment";
  private static final String ENVIRONMENT_KEY = "environment";
  private static final String OTHER_URL_KEY = "environment_other_url";
  //</editor-fold>

  //<editor-fold desc="属性">
  private static Integer sCurrentType = null;
  private SharedPrefsWrapper mSp = new SharedPrefsWrapper(ENVIRONMENT_FILE_NAME);
  //</editor-fold>

  //<editor-fold desc="构造函数">

  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public int  getCurrentType() {
    if (true || EnvironmentManager.getInstance().getIsTestProductFlavors()) {
      if (sCurrentType == null) {
        sCurrentType = mSp.getInt(ENVIRONMENT_KEY, TEST_47);
      }
      return sCurrentType;
    }
    return RELEASE;
  }

  @Override
  public void updateType(@Type int type, String otherUrl) {
    if (type == getCurrentType()) {
      return;
    }
    final String url;
    switch (type) {
      case TEST_190:
        url = TEST_API_URL_190;
        break;
      case TEST_47:
        url = TEST_API_URL_47;
        break;
      case RELEASE:
        url = RELEASE_API_URL;
        break;
      case OTHER:
        url = otherUrl;
        break;
      default:
        throw new UnsupportedOperationException();
    }
    sCurrentType = type;
    // 更新 sp
    mSp.putInt(ENVIRONMENT_KEY, type);
    mSp.putString(OTHER_URL_KEY, otherUrl);
    // 更新 Retrofit BaseUrl
    NetworkManager.getInstance().updateBaseUrl(url);
  }

  @NonNull
  @Override
  public String getCurrentAPIUrl() {
    switch (getCurrentType()) {
      case TEST_47:
        return TEST_API_URL_47;
      case TEST_190:
        return TEST_API_URL_190;
      case RELEASE:
        return RELEASE_API_URL;
      case OTHER:
        return mSp.getString(OTHER_URL_KEY, "");
      default:
        throw new UnsupportedOperationException();
    }
  }
  //</editor-fold>
}
