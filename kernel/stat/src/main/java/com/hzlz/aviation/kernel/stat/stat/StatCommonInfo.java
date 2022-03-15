package com.hzlz.aviation.kernel.stat.stat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatGroupEntity;
import com.hzlz.aviation.library.util.DeviceId;

import java.util.Date;

/**
 * 埋点通用信息，批量上传时保存一份
 */
public class StatCommonInfo {

  public static StatGroupEntity create(boolean realtime) {
    Context c = GVideoRuntime.getAppContext();
    StatGroupEntity.Builder builder = StatGroupEntity.Builder.aStatGroupEntity()
        .withRealtime(realtime)
        .withUid(GVideoStatManager.getInstance().getUid())
        .withCid(DeviceId.get())
        .withTimestamp(new Date())
        .withNetworkType(getNetworkType(c))
        .withCarrier(StatUtils.getNetworkOperatorName(c))
        .withAppName(c.getPackageName())
        .withAppVersion(StatUtils.getAppVersionName(c))
        .withChannel(GVideoStatManager.getInstance().getChannelId())
        .withManufacturer(Build.MANUFACTURER)
        .withModel(Build.MODEL)
        .withOs("Android")
        .withOsVersion(Build.VERSION.RELEASE);
    return builder.build();
  }
  private static String getNetworkType(Context c) {
    @SuppressLint("MissingPermission") StatUtils.NetworkType type = StatUtils.getNetworkType(c);
    String networkType = "";
    switch (type) {
      case NETWORK_2G:
        networkType = "2G";
        break;
      case NETWORK_3G:
        networkType = "3G";
        break;
      case NETWORK_4G:
        networkType = "4G";
        break;
      case NETWORK_WIFI:
        networkType = "WIFI";
        break;
      case NETWORK_ETHERNET:
        networkType = "ETH";
        break;
      case NETWORK_NO:
        networkType = "NO";
        break;
      case NETWORK_UNKNOWN:
      default:
        networkType = "UNKNOWN";
    }

    return networkType;
  }

}
