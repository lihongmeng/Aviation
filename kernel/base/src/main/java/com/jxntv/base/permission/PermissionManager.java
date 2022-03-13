package com.jxntv.base.permission;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.jxntv.utils.AppManager;
import com.tbruyelle.rxpermissions3.RxPermissions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * 权限管理类
 *
 *
 * @since 2020-03-20 16:36
 */
public final class PermissionManager {

  private PermissionManager() {
    throw new IllegalStateException("no instance !!!");
  }

  public static void requestPermissions(
      @NonNull Context context,
      @NonNull PermissionCallback callback,
      @NonNull String... permissions) {

    FragmentActivity activity = getFragmentActivity(context);
    if (activity!=null  && permissions!=null && permissions.length>0){
      RxPermissions rxPermissions = new RxPermissions(activity);
      rxPermissions.request(permissions).subscribe(aBoolean -> {
        if (aBoolean){
          callback.onPermissionGranted(context);
        }else {
          List<String> denied = new ArrayList<>();
          List<String> granted = new ArrayList<>();
          for (String permission : permissions) {
            if (rxPermissions.isGranted(permission)) {
              granted.add(permission);
            } else {
              denied.add(permission);
            }
          }
          callback.onPermissionDenied(context,listToArray(granted),listToArray(denied));
        }
      });
    }
  }


  private static String[] listToArray(@NonNull List<String> list) {
    if (!list.isEmpty()) {
      int size = list.size();
      String[] strings = new String[size];
      for (int i = 0; i < size; i++) {
        strings[i] = list.get(i);
      }
      return strings;
    }
    return new String[0];
  }

  private static FragmentActivity getFragmentActivity(Context context){
    FragmentActivity activity = null;
    if (context instanceof FragmentActivity){
      activity = (FragmentActivity) context;
    }else if (context instanceof ContextWrapper && ((ContextWrapper)context).getBaseContext() instanceof FragmentActivity){
      activity = (FragmentActivity) ((ContextWrapper)context).getBaseContext();
    }else if (AppManager.getAppManager().currentActivity() instanceof FragmentActivity){
      activity = (FragmentActivity) AppManager.getAppManager().currentActivity();
    }
    return activity;
  }

}
