package com.hzlz.aviation.kernel.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 *
 *
 * @since 2020-01-07 14:44
 */
public final class NetworkUtils {
  //<editor-fold desc="私有构造函数">
  private NetworkUtils() {

  }
  //</editor-fold>

  //<editor-fold desc="静态 API">

  /**
   * 检测网络是否连接
   *
   * @return true : 已经连接 ; false : 没有连接
   */
  public static boolean isNetworkConnected() {
    NetworkInfo activeNetworkInfo = getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  /**
   * wifi网络是否可用
   *
   * @return true : 可用 ; false : 不可用
   */
  public static boolean isWifiNetworkConnected() {
    NetworkInfo activeNetworkInfo = getActiveNetworkInfo();
    return activeNetworkInfo  != null && activeNetworkInfo .isAvailable()
            && (activeNetworkInfo .getType() == ConnectivityManager.TYPE_WIFI);
  }

  /**
   * 获取活动的连接。
   *
   * @return 当前连接。ContextUtils初始化失败时可能会返回Null
   */
  public static NetworkInfo getActiveNetworkInfo() {
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) NetworkManager
              .getInstance()
              .getApplicationContext()
              .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (connectivityManager == null) {
        return null;
      }
      return connectivityManager.getActiveNetworkInfo();
    } catch (Exception ignore) {
    }
    return null;
  }
  //</editor-fold>
}
