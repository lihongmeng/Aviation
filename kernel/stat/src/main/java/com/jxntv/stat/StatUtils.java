package com.jxntv.stat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import androidx.annotation.RequiresPermission;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

/**
 * 埋点工具类
 */
public class StatUtils {
  public enum NetworkType {
    NETWORK_ETHERNET,
    NETWORK_WIFI,
    NETWORK_4G,
    NETWORK_3G,
    NETWORK_2G,
    NETWORK_UNKNOWN,
    NETWORK_NO
  }
  /**
   * Return the name of network operate.
   *
   * @return the name of network operate
   */
  public static String getNetworkOperatorName(Context context) {
    TelephonyManager tm =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (tm == null) return "";
    return tm.getNetworkOperatorName();
  }

  /**
   * Return type of network.
   * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
   *
   * @return type of network
   * <ul>
   * <li>{@link NetworkType#NETWORK_ETHERNET} </li>
   * <li>{@link NetworkType#NETWORK_WIFI    } </li>
   * <li>{@link NetworkType#NETWORK_4G      } </li>
   * <li>{@link NetworkType#NETWORK_3G      } </li>
   * <li>{@link NetworkType#NETWORK_2G      } </li>
   * <li>{@link NetworkType#NETWORK_UNKNOWN } </li>
   * <li>{@link NetworkType#NETWORK_NO      } </li>
   * </ul>
   */
  @RequiresPermission(ACCESS_NETWORK_STATE)
  public static NetworkType getNetworkType(Context context) {
    if (isEthernet(context)) {
      return NetworkType.NETWORK_ETHERNET;
    }
    NetworkInfo info = getActiveNetworkInfo(context);
    if (info != null && info.isAvailable()) {
      if (info.getType() == ConnectivityManager.TYPE_WIFI) {
        return NetworkType.NETWORK_WIFI;
      } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
        switch (info.getSubtype()) {
          case TelephonyManager.NETWORK_TYPE_GSM:
          case TelephonyManager.NETWORK_TYPE_GPRS:
          case TelephonyManager.NETWORK_TYPE_CDMA:
          case TelephonyManager.NETWORK_TYPE_EDGE:
          case TelephonyManager.NETWORK_TYPE_1xRTT:
          case TelephonyManager.NETWORK_TYPE_IDEN:
            return NetworkType.NETWORK_2G;

          case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
          case TelephonyManager.NETWORK_TYPE_EVDO_A:
          case TelephonyManager.NETWORK_TYPE_UMTS:
          case TelephonyManager.NETWORK_TYPE_EVDO_0:
          case TelephonyManager.NETWORK_TYPE_HSDPA:
          case TelephonyManager.NETWORK_TYPE_HSUPA:
          case TelephonyManager.NETWORK_TYPE_HSPA:
          case TelephonyManager.NETWORK_TYPE_EVDO_B:
          case TelephonyManager.NETWORK_TYPE_EHRPD:
          case TelephonyManager.NETWORK_TYPE_HSPAP:
            return NetworkType.NETWORK_3G;

          case TelephonyManager.NETWORK_TYPE_IWLAN:
          case TelephonyManager.NETWORK_TYPE_LTE:
            return NetworkType.NETWORK_4G;

          default:
            String subtypeName = info.getSubtypeName();
            if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                || subtypeName.equalsIgnoreCase("WCDMA")
                || subtypeName.equalsIgnoreCase("CDMA2000")) {
              return NetworkType.NETWORK_3G;
            } else {
              return NetworkType.NETWORK_UNKNOWN;
            }
        }
      } else {
        return NetworkType.NETWORK_UNKNOWN;
      }
    }
    return NetworkType.NETWORK_NO;
  }

  @RequiresPermission(ACCESS_NETWORK_STATE)
  private static NetworkInfo getActiveNetworkInfo(Context context) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm == null) return null;
    return cm.getActiveNetworkInfo();
  }

  /**
   * Return whether using ethernet.
   * <p>Must hold
   * {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
   *
   * @return {@code true}: yes<br>{@code false}: no
   */
  @RequiresPermission(ACCESS_NETWORK_STATE)
  private static boolean isEthernet(Context context) {
    final ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm == null) return false;
    final NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
    if (info == null) return false;
    NetworkInfo.State state = info.getState();
    if (null == state) return false;
    return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING;
  }


  /**
   * Return the application's version name.
   *
   * @return the application's version name
   */
  public static String getAppVersionName(Context context) {
    return getAppVersionName(context, context.getPackageName());
  }

  /**
   * Return the application's version name.
   *
   * @param packageName The name of the package.
   * @return the application's version name
   */
  public static String getAppVersionName(Context context, final String packageName) {
    try {
      PackageManager pm = context.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(packageName, 0);
      return pi == null ? null : pi.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return "";
    }
  }

  /**
   * Return the application's version code.
   *
   * @return the application's version code
   */
  public static int getAppVersionCode(Context context) {
    return getAppVersionCode(context, context.getPackageName());
  }

  /**
   * Return the application's version code.
   *
   * @param packageName The name of the package.
   * @return the application's version code
   */
  public static int getAppVersionCode(Context context, final String packageName) {
    try {
      PackageManager pm = context.getPackageManager();
      PackageInfo pi = pm.getPackageInfo(packageName, 0);
      return pi == null ? -1 : pi.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
      return -1;
    }
  }
}
