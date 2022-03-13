package com.jxntv.base.permission;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.ref.WeakReference;

/**
 * 权限解释抽象类
 *
 *
 * @since 2020-03-20 16:49
 */
public abstract class AbstractPermissionExplanation implements PermissionExplanation {
  //<editor-fold desc="属性">
  @Nullable
  private Callback mCallback;
  @Nullable
  private String[] mPermissions;
  @Nullable
  private WeakReference<Context> mWeakContext;
  //</editor-fold>

  //<editor-fold desc="抽象方法">

  /**
   * 解释权限
   *
   * @param context 上下文
   * @param permissions 权限列表
   */
  protected abstract void explainPermissions(
      @NonNull Context context,
      @NonNull String[] permissions
  );
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public final void setCallback(@NonNull Callback callback) {
    mCallback = callback;
  }

  @Override
  public final void explain(@NonNull Context context, @NonNull String[] permissions) {
    mPermissions = permissions;
    mWeakContext = new WeakReference<>(context);
    explainPermissions(context, permissions);
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 请求权限
   */
  protected void requestPermissions() {
    if (mWeakContext == null) {
      return;
    }
    @Nullable Context context = mWeakContext.get();
    if (context == null) {
      return;
    }
    if (mCallback != null && mPermissions != null && mPermissions.length > 0) {
      mCallback.onRequestPermissions(context, mPermissions);
    }
  }

  /**
   * 取消请求权限
   */
  protected void cancelRequestPermission() {
    if (mWeakContext == null) {
      return;
    }
    @Nullable Context context = mWeakContext.get();
    if (context == null) {
      return;
    }
    if (mCallback != null && mPermissions != null && mPermissions.length > 0) {
      mCallback.onCancelRequestPermissions(context, mPermissions);
    }
  }
  //</editor-fold>
}
