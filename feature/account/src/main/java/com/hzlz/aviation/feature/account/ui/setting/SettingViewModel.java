package com.hzlz.aviation.feature.account.ui.setting;

import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static android.provider.Settings.EXTRA_CHANNEL_ID;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.feature.account.presistence.VideoPlaySettingManager;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.library.util.ResourcesUtils;

/**
 * 设置界面 ViewModel
 *
 *
 * @since 2020-02-19 14:29
 */
public final class SettingViewModel extends BaseViewModel implements LifecycleObserver {
  //<editor-fold desc="属性">
  @NonNull
  public ObservableField<String> acceptPushNotification = new ObservableField<>();
  @NonNull
  public ObservableField<String> autoPlay = new ObservableField<>();
  @NonNull
  public ObservableField<String> autoPlayNextContent = new ObservableField<>();

  @NonNull
  private CheckThreadLiveData<Boolean> mAutoPlayLiveData = new CheckThreadLiveData<>();
  @NonNull
  private CheckThreadLiveData<Boolean> mAutoPlayNextContentLiveData =
      new CheckThreadLiveData<>();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public SettingViewModel(@NonNull Application application) {
    super(application);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  @NonNull
  LiveData<Boolean> getAutoPlayLiveData() {
    return mAutoPlayLiveData;
  }

  @NonNull
  LiveData<Boolean> getAutoPlayNextContentLiveData() {
    return mAutoPlayNextContentLiveData;
  }

  void init() {
    // 自动播放
    if (VideoPlaySettingManager.isAutoPlay()) {
      autoPlay.set(ResourcesUtils.getString(R.string.auto_play_in_any_network));
    } else if (VideoPlaySettingManager.isAutoPlayOnlyInWifi()) {
      autoPlay.set(ResourcesUtils.getString(R.string.auto_play_only_in_wifi));
    } else {
      autoPlay.set(ResourcesUtils.getString(R.string.not_auto_play));
    }
    // 自动播放下一个内容
    if (VideoPlaySettingManager.isAutoPlayNextContent()) {
      autoPlayNextContent.set(ResourcesUtils.getString(
          R.string.auto_play_next_content_in_any_network
      ));
    } else if (VideoPlaySettingManager.isAutoPlayNextContentOnlyInWifi()) {
      autoPlayNextContent.set(ResourcesUtils.getString(
          R.string.auto_play_next_content_only_in_wifi
      ));
    } else {
      autoPlayNextContent.set(ResourcesUtils.getString(
          R.string.not_auto_play_next_content
      ));
    }
  }

  // 自动播放
  void autoPlay() {
    VideoPlaySettingManager.setAutoPlay();
    autoPlay.set(ResourcesUtils.getString(R.string.auto_play_in_any_network));
  }

  void autoPlayOnlyInWifi() {
    VideoPlaySettingManager.setAutoPlayOnlyInWifi();
    autoPlay.set(ResourcesUtils.getString(R.string.auto_play_only_in_wifi));
  }

  void notAutoPlay() {
    VideoPlaySettingManager.setNotAutoPlay();
    autoPlay.set(ResourcesUtils.getString(R.string.not_auto_play));
  }

  // 自动播放下一个内容
  void autoPlayNextContent() {
    VideoPlaySettingManager.setAutoPlayNextContent();
    autoPlayNextContent.set(ResourcesUtils.getString(
        R.string.auto_play_next_content_in_any_network
    ));
  }

  void autoPlayNextContentOnlyInWifi() {
    VideoPlaySettingManager.setAutoPlayNextContentOnlyInWifi();
    autoPlayNextContent.set(ResourcesUtils.getString(
        R.string.auto_play_next_content_only_in_wifi
    ));
  }

  void notAutoPlayNextContent() {
    VideoPlaySettingManager.setNotAutoPlayNextContent();
    autoPlayNextContent.set(ResourcesUtils.getString(R.string.not_auto_play_next_content));
  }
  //</editor-fold>

  //<editor-fold desc="生命周期">
  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  public void onResume(@NonNull LifecycleOwner owner) {
    Context context = null;
    if (owner instanceof Fragment) {
      context = ((Fragment) owner).requireContext();
    } else if (owner instanceof Activity) {
      context = (Context) owner;
    }
    if (context != null) {
      acceptPushNotification.set(getAcceptPushNotificationState(context));
    }
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void startNotificationSettingActivity(@NonNull View view) {
    startNotificationSettingActivity(view.getContext());
  }

  public void showAutoPlayDialog() {
    mAutoPlayLiveData.setValue(true);
  }

  public void showAutoPlayNextContentDialog() {
    mAutoPlayNextContentLiveData.setValue(true);
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 是否开启通知权限
   *
   * @param context 上下文
   * @return true : 已开启 ; false : 未开启
   */
  private boolean whetherEnableNotificationPermission(@NonNull Context context) {
    NotificationManagerCompat manager = NotificationManagerCompat.from(context);
    return manager.areNotificationsEnabled();
  }

  /**
   * 获取接受推送通知状态
   *
   * @param context 上下文
   */
  @NonNull
  private String getAcceptPushNotificationState(@NonNull Context context) {
    boolean enable = whetherEnableNotificationPermission(context);
    if (enable) {
      return ResourcesUtils.getString(R.string.opened);
    }
    return ResourcesUtils.getString(R.string.unopened);
  }

  /**
   * 跳转系统通知界面
   *
   * @param context 上下文
   */
  private void startNotificationSettingActivity(@NonNull Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
      intent.putExtra(EXTRA_APP_PACKAGE, context.getPackageName());
      intent.putExtra(EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
      intent.putExtra("app_package", context.getPackageName());
      intent.putExtra("app_uid", context.getApplicationInfo().uid);
      if (intent.resolveActivity(context.getPackageManager()) != null) {
        context.startActivity(intent);
      } else {
        startApplicationDetailSettingActivity(context);
      }
    } else {
      startApplicationDetailSettingActivity(context);
    }
  }

  /**
   * 跳转应用详情设置界面
   *
   * @param context 上下文
   */
  private void startApplicationDetailSettingActivity(@NonNull Context context) {
    Intent intent = new Intent();
    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
    intent.setData(uri);
    if (intent.resolveActivity(context.getPackageManager()) != null) {
      context.startActivity(intent);
    }
  }
  //</editor-fold>
}
