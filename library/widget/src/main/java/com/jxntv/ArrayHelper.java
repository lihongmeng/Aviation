package com.jxntv;

import androidx.annotation.Nullable;

/**
 * 数组帮助类
 *
 *
 * @since 2020-01-19 12:05
 */
public final class ArrayHelper {
  //<editor-fold desc="Int">
  @Nullable
  public static Integer[] integers(Integer... is) {
    return is;
  }

  @Nullable
  public static int[] ints(int... is) {
    return is;
  }
  //</editor-fold>

  //<editor-fold desc="String">
  @Nullable
  public String[] strings(String... s) {
    return s;
  }
  //</editor-fold>

  //<editor-fold desc="Long">
  @Nullable
  public Long[] Longs(Long... ls) {
    return ls;
  }

  @Nullable
  public long[] longs(long... ls) {
    return ls;
  }
  //</editor-fold>

  //<editor-fold desc="Boolean">
  @Nullable
  public Boolean[] Booleans(Boolean... booleans) {
    return booleans;
  }

  @Nullable
  public boolean[] Booleans(boolean... booleans) {
    return booleans;
  }
  //</editor-fold>
}
