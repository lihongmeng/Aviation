package com.jxntv.base.permission;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 权限回调
 *
 *
 * @since 2020-03-20 16:15
 */
public interface PermissionCallback {
  /**
   * 当所有权限全部获取后回调
   *
   * @param context 上下文
   */
  void onPermissionGranted(@NonNull Context context);

  /**
   * 当权限被拒绝后回调
   *
   * @param grantedPermissions 已经获取的权限列表
   * @param deniedPermission 被拒绝的权限列表
   */
  void onPermissionDenied(
      @NonNull Context context,
      @Nullable String[] grantedPermissions,
      @NonNull String[] deniedPermission
  );
}
