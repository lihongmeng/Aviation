package com.jxntv.base.permission;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 权限参数
 *
 *
 * @since 2020-03-20 19:09
 */
final class PermissionArgument {
  //<editor-fold desc="属性">
  @Nullable
  String[] mGrantedPermission;
  @NonNull
  String[] mDeniedPermissions;
  @Nullable
  PermissionDenialProcessor mPermissionDenialProcessor;
  @NonNull
  PermissionCallback mPermissionCallback;
  //</editor-fold>

  //<editor-fold desc="构造函数">

  PermissionArgument(
      @Nullable String[] grantedPermission,
      @NonNull String[] deniedPermissions,
      @Nullable PermissionDenialProcessor permissionDenialProcessor,
      @NonNull PermissionCallback permissionCallback) {
    mGrantedPermission = grantedPermission;
    mDeniedPermissions = deniedPermissions;
    mPermissionDenialProcessor = permissionDenialProcessor;
    mPermissionCallback = permissionCallback;
  }

  //</editor-fold>
}
