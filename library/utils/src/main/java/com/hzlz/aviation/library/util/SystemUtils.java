package com.hzlz.aviation.library.util;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;

public final class SystemUtils {
  private static Boolean sIsMainProcess;

  private SystemUtils() {
  }

  public static boolean isMainProcess(@NonNull Context context) {
    if (sIsMainProcess != null) {
      return sIsMainProcess;
    }
    String packageName = getProcessName();
    return sIsMainProcess =
        packageName == null || TextUtils.equals(packageName, context.getPackageName());
  }

  @Nullable
  public static String getProcessName() {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader("/proc/" + Process.myPid() + "/cmdline"));
      String processName = reader.readLine();
      if (!TextUtils.isEmpty(processName)) {
        processName = processName.trim();
      }
      return processName;
    } catch (Throwable ignore) {
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (Exception ignore) {
      }
    }
    return null;
  }

  /**
   * 获取核心数
   *
   * @return 核心数
   */
  public static int getCoreCount() {
    return Runtime.getRuntime().availableProcessors();
  }
}
