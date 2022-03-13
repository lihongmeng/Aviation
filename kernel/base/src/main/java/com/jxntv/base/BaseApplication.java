package com.jxntv.base;

import android.app.Application;
import com.jxntv.utils.SystemUtils;

/**
 * Application 基类
 *
 *
 * @since 2020-01-13 16:52
 */
public abstract class BaseApplication extends Application {
  //<editor-fold desc="抽象方法">

  /**
   * 当当前进程为主进程时回调
   */
  protected abstract void onMainProcessCreate();
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 所有进程都回调
   */
  protected void onAllProgressCreate() {
  }
  //</editor-fold>

  //<editor-fold desc="生命周期">
  @Override
  public void onCreate() {
    super.onCreate();
    if (SystemUtils.isMainProcess(this)) {
      onMainProcessCreate();
    }
    onAllProgressCreate();
  }
    //</editor-fold>
}
