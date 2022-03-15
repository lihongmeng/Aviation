package com.hzlz.aviation.kernel.media;

import android.app.Application;
import android.content.Context;

import com.hzlz.aviation.kernel.media.player.MediaPlayManager;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;

/**
 * media 运行时数据模型
 */
public class MediaRuntime {

  /** 持有的app context */
  private static Context sAppContext;
  /** 持有的application */
  private static Application sApplication;

  static {
    sAppContext = GVideoRuntime.getAppContext();
    sApplication = GVideoRuntime.getApplication();
  }

  /**
   * 获取app context
   */
  public static Context getAppContext() {
    return sAppContext;
  }

  /**
   * 获取application
   */
  public static Application getApplication() {
    return sApplication;
  }

  /** 页面退出时释放 */
  public static void release() {
    MediaFragmentManager.release();
    MediaPlayManager.release();
  }
}
