package com.hzlz.aviation.kernel.base.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.hzlz.aviation.library.util.AppManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * 权限 Fragment
 *
 *
 * @since 2020-03-20 16:16
 */
public final class PermissionFragment extends Fragment {
  //<editor-fold desc="常量">
  private static final String PERMISSION_FRAGMENT_TAG = PermissionFragment.class.getName();
  private static final int REQUEST_PERMISSION = 1;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  @NonNull
  private Queue<PermissionArgument> mPermissionArgumentQueue = new LinkedList<>();
  @Nullable
  private PermissionArgument mCurrentPermissionArgument;
  private boolean mCanProcessPermissionImmediately = false;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  private PermissionFragment() {

  }
  //</editor-fold>

  //<editor-fold desc="API">
  static void requestPermission(@NonNull Context context, @NonNull PermissionArgument argument) {

    FragmentManager manager = null;
    if (context instanceof FragmentActivity){
      manager = ((FragmentActivity) context).getSupportFragmentManager();
    }else {
      Activity activity = AppManager.getAppManager().currentActivity();
      if (activity instanceof FragmentActivity) {
        manager = ((FragmentActivity) activity).getSupportFragmentManager();
      }
    }
    if (manager == null){
      return;
    }
    Fragment fragment = manager.findFragmentByTag(PERMISSION_FRAGMENT_TAG);
    if (fragment == null) {
      fragment = new PermissionFragment();
      manager.beginTransaction().add(fragment, PERMISSION_FRAGMENT_TAG).commit();
    }
    ((PermissionFragment) fragment).addPermissionArgument(argument);
  }

  //</editor-fold>

  //<editor-fold desc="生命周期">
  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mCanProcessPermissionImmediately = true;
    processPermissionArguments();
  }

  @Override
  public void onDestroy() {
    mCanProcessPermissionImmediately = false;
    mPermissionArgumentQueue.clear();
    super.onDestroy();
  }
  //</editor-fold>

  //<editor-fold desc="方法重写">
  @Override
  public void onRequestPermissionsResult(
      int requestCode,
      @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode != REQUEST_PERMISSION || mCurrentPermissionArgument == null) {
      return;
    }
    // 收集已获取/被拒绝的请求
    Set<String> grantedSet = arrayToSet(mCurrentPermissionArgument.mGrantedPermission);
    Set<String> deniedSet = arrayToSet(mCurrentPermissionArgument.mDeniedPermissions);
    int size = permissions.length;
    for (int i = 0; i < size; i++) {
      if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
        grantedSet.add(permissions[i]);
        deniedSet.remove(permissions[i]);
      } else {
        deniedSet.add(permissions[i]);
      }
    }
    // 判断是否所有请求是否都已被获取
    if (deniedSet.isEmpty()) {
      mCurrentPermissionArgument.mPermissionCallback.onPermissionGranted(requireContext());
      return;
    }
    //
    String[] grantedPermissions = setToString(grantedSet);
    String[] deniedPermissions = setToString(deniedSet);
    if (mCurrentPermissionArgument.mPermissionDenialProcessor != null) {
      //noinspection ConstantConditions
      mCurrentPermissionArgument.mPermissionDenialProcessor.processDeniedPermissions(
          requireContext(), deniedPermissions
      );
      return;
    }
    //noinspection ConstantConditions
    mCurrentPermissionArgument.mPermissionCallback.onPermissionDenied(
        requireContext(), grantedPermissions, deniedPermissions
    );
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">
  private void addPermissionArgument(@NonNull PermissionArgument argument) {
    mPermissionArgumentQueue.add(argument);
    if (mCanProcessPermissionImmediately) {
      processPermissionArguments();
    }
  }

  private void processPermissionArguments() {
    Context context = getContext();
    if (context == null) {
      return;
    }
    mCurrentPermissionArgument = mPermissionArgumentQueue.poll();
    if (mCurrentPermissionArgument == null) {
      return;
    }
    requestPermissions(mCurrentPermissionArgument.mDeniedPermissions, REQUEST_PERMISSION);
  }

  @NonNull
  private Set<String> arrayToSet(@Nullable String[] strings) {
    if (strings != null && strings.length > 0) {
      return new HashSet<>(Arrays.asList(strings));
    }
    return new HashSet<>();
  }

  @Nullable
  private String[] setToString(@NonNull Set<String> set) {
    if (!set.isEmpty()) {
      int size = set.size();
      String[] strings = new String[size];
      int i = 0;
      for (String s : set) {
        strings[i++] = s;
      }
      return strings;
    }
    return new String[set.size()];
  }
  //</editor-fold>
}
