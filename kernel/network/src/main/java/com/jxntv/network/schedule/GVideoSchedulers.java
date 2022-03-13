package com.jxntv.network.schedule;

import com.jxntv.async.GlobalExecutor;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 调度
 *
 *
 * @since 2020-01-07 14:27
 */
public final class GVideoSchedulers {
  //<editor-fold desc="静态公开属性">
  /**
   * 主线程
   */
  public static final Scheduler MAIN = AndroidSchedulers.mainThread();

  /** 接入统一线程池 background priority {@link GlobalExecutor} */
  public static final Scheduler IO_PRIORITY_BACKGROUND =
      Schedulers.from(new GlobalExecutor(GlobalExecutor.PRIORITY_BACKGROUND));

  /** 接入统一线程池 normal priority {@link GlobalExecutor} */
  public static final Scheduler IO_PRIORITY_NORMAL =
      Schedulers.from(new GlobalExecutor(GlobalExecutor.PRIORITY_NORMAL));

  /** 接入统一线程池 user priority {@link GlobalExecutor} */
  public static final Scheduler IO_PRIORITY_USER =
      Schedulers.from(new GlobalExecutor(GlobalExecutor.PRIORITY_USER));
  //</editor-fold>

  //<editor-fold desc="私有方法">
  private GVideoSchedulers() {

  }
  //</editor-fold>
}
