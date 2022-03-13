package com.jxntv.base.permission;

import android.content.Context;
import androidx.annotation.NonNull;

/**
 * 权限拒绝处理接口
 *
 *
 * @since 2020-03-20 17:04
 */
public interface PermissionDenialProcessor {
  /**
   * 处理被拒绝的权限
   *
   * @param context 上下文
   * @param deniedPermissions 被拒绝的权限列表
   */
  void processDeniedPermissions(@NonNull Context context, @NonNull String[] deniedPermissions);
}