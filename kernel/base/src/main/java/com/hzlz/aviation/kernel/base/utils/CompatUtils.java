package com.hzlz.aviation.kernel.base.utils;

import android.os.Build;

import java.util.Arrays;
import java.util.Objects;

public class CompatUtils {
  public static boolean equals(Object a, Object b) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      return Objects.equals(a, b);
    } else {
      return (a == b) || (a != null && a.equals(b));
    }
  }

  public static int hash(Object... values) {
    return Arrays.hashCode(values);
  }
}
