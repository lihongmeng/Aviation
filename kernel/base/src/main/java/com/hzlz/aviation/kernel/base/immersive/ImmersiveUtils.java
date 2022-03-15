package com.hzlz.aviation.kernel.base.immersive;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.utils.WidgetUtils;

public class ImmersiveUtils {


  private ImmersiveUtils() {}


  public static void enterImmersive(@NonNull BaseFragment fragment) {
    enterImmersive(fragment, fragment.statusBarColor(), fragment.darkImmersive());
  }

  /**
   *  设置状态栏夜色
   * @param color 背景色
   * @param dark  是否黑色图标，文字
   */
  public static void enterImmersive(@NonNull BaseFragment fragment, int color, boolean dark) {
    enterImmersive(fragment, color, dark, true);
  }

  /**
   * 设置状态栏夜色
   * @param color 背景色
   * @param dark 是否黑色图标，文字
   * @param isFitSystemWindows 是否FitSystemWindows
   */
  public static void enterImmersive(@NonNull BaseFragment fragment, int color, boolean dark, boolean isFitSystemWindows) {
    Activity activity = fragment.getActivity();
    if (activity == null) {
      return;
    }
    if (fragment.customImmersive()) {
      return;
    }
    if (Build.VERSION.SDK_INT < fragment.minImmersiveVersionCode()) {
      return;
    }
    TypedArray array =
        activity.getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowFullscreen});
    if (!array.getBoolean(0, false)) {
      enterImmersiveWithoutPadding(activity, color, dark);
      //fitsSystemWindow 为 false, 不预留系统栏位置
      ViewGroup mContentView = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
      View mChildView = mContentView.getChildAt(0);
      if (mChildView != null) {
        mChildView.setFitsSystemWindows(isFitSystemWindows);
        ViewCompat.requestApplyInsets(mChildView);
      }
//        activity.findViewById(android.R.id.content).setPadding(0, WidgetUtils.getStatusBarHeight(), 0, 0);
    }
    array.recycle();
  }


  /**
   *
   * @param dark 是否黑色图标
   */
  public static void enterImmersiveFullTransparent(@NonNull Activity activity,boolean dark){
    enterImmersiveWithoutPadding(activity,Color.TRANSPARENT,dark);
  }

  public static void enterImmersiveFullTransparent(@NonNull Activity activity){
    enterImmersiveFullTransparent(activity,false);
  }

  /**
   * 设置状态栏图标颜色
   * @param dark
   */
  public static void setStatusBarIconColor(@NonNull Fragment fragment, boolean dark){
    Activity activity = fragment.getActivity();
    if (activity == null) {
      return;
    }
    Window window = activity.getWindow();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
      int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
      if (dark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        option |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (WidgetUtils.isMIUI()) {
          MIUIImmersiveUtils.setMIUILightStatusBar(activity, true);
        } else if (WidgetUtils.isFlyme()) {
          FlymeImmersiveUtils.setStatusBarDarkIcon(activity, true);
        }
      }
      window.getDecorView().setSystemUiVisibility(option);
    }else {
      window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }

  public static void enterImmersiveWithoutPadding(@NonNull Activity activity, int color, boolean dark) {
    Window window = activity.getWindow();
    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (dark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        option |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (WidgetUtils.isMIUI()) {
          MIUIImmersiveUtils.setMIUILightStatusBar(activity, true);
        } else if (WidgetUtils.isFlyme()) {
          FlymeImmersiveUtils.setStatusBarDarkIcon(activity, true);
        }
      }
      window.getDecorView().setSystemUiVisibility(option);
      window.setStatusBarColor(color);
      window.setNavigationBarColor(window.getNavigationBarColor());
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      window.getDecorView().setSystemUiVisibility(option);
    }
  }


  /**
   * 关闭沉浸式
   */
  public static void disableImmersive(@NonNull Activity activity, boolean needFullScreen,
                                      boolean needResetPadding) {
    if (!WidgetUtils.isFlyme()) { // 魅族手机不修改window属性，会影响展示
      Window window = activity.getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      if (needFullScreen) {
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
        );
      } else {
        window.getDecorView().setSystemUiVisibility(
            ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN & ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      }
    }
    if (needResetPadding) {
      activity.findViewById(android.R.id.content).setPadding(0, 0, 0,
        0);
    }
  }


  /**
   * 设置状态栏夜色
   * @param color 背景色
   * @param dark 是否黑色图标，文字
   * @param isFitSystemWindows 是否FitSystemWindows
   */
  public static void enterImmersive(Activity activity, int color, boolean dark, boolean isFitSystemWindows) {
    TypedArray array =
            activity.getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowFullscreen});
    if (!array.getBoolean(0, false)) {
      enterImmersiveWithoutPadding(activity, color, dark);
      //fitsSystemWindow 为 false, 不预留系统栏位置
      ViewGroup mContentView = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
      View mChildView = mContentView.getChildAt(0);
      if (mChildView != null) {
        mChildView.setFitsSystemWindows(isFitSystemWindows);
        ViewCompat.requestApplyInsets(mChildView);
      }
    }
    array.recycle();
  }

}
