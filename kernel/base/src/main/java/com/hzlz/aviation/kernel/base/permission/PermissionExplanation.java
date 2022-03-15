package com.hzlz.aviation.kernel.base.permission;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * 权限解释接口
 *
 *
 * @since 2020-03-20 16:41
 */
public interface PermissionExplanation {
  /**
   * 设置回调接口
   *
   * @param callback 回调接口
   */
  void setCallback(@NonNull Callback callback);

  /**
   * 解释权限
   *
   * @param context 上下文
   * @param permissions 权限列表
   */
  void explain(@NonNull Context context, @NonNull String[] permissions);

  /**
   * 回调接口，用于处理用户看到权限解释后是否需要请求权限
   */
  interface Callback {
    /**
     * 请求权限回调
     *
     * @param context 上下文
     * @param permissions 权限列表
     */
    void onRequestPermissions(@NonNull Context context, @NonNull String[] permissions);

    /**
     * 取消请求权限回调
     *
     * @param context 上下文
     * @param permissions 权限列表
     */
    void onCancelRequestPermissions(@NonNull Context context, @NonNull String[] permissions);
  }
}
