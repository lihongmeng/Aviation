package com.hzlz.aviation.kernel.network.request;

import com.google.gson.Gson;
import com.hzlz.aviation.kernel.network.NetworkManager;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Retrofit;

/**
 * 请求基类
 *
 * @param <T> 请求响应泛型
 *
 * @since 2020-01-03 16:16
 */

public abstract class BaseRequest<T> {
  //<editor-fold desc="常量">
  // 默认重试次数(0次)
  private static final int DEFAULT_RETRY_TIMES = 0;
  // 默认检测网络连接等待时长（1s）
  private static final long DEFAULT_RETRY_WAIT_TIMEOUT = 1000L;
  // 默认等待重试超时时长(3s)
  private static final long DEFAULT_RETRY_TIMEOUT = 3 * 1000L;
  //</editor-fold>

  //<editor-fold desc="属性">
  // 标记请求是否被取消
  private volatile boolean mIsCanceled = false;
  //</editor-fold>

  //<editor-fold desc="抽象方法">

  /**
   * 获取一个 Observable {@link Observable} 对象用于请求的执行
   *
   * @return 一个 Observable 对象
   */
  public abstract Observable<T> getObservable();

  //</editor-fold>

  //<editor-fold desc="内部 API">

  /**
   * 获取 Retrofit {@link Retrofit} 实例
   *
   * @return Retrofit 实例
   */
  protected Retrofit getRetrofit() {
    return NetworkManager.getInstance().getRetrofit();
  }

  /**
   * 获取 Gson {@link Gson} 实例
   *
   * @return Gson 实例
   */
  protected Gson getGson() {
    return NetworkManager.getInstance().getGson();
  }
  //</editor-fold>

  //<editor-fold desc="请求取消相关方法">

  /**
   * 取消请求
   */
  public void cancel() {
    mIsCanceled = true;
  }

  /**
   * 请求是否被取消
   *
   * @return true : 请求被取消 ; false : 请求尚未取消
   */
  public boolean isCanceled() {
    return mIsCanceled;
  }
  //</editor-fold>

  //<editor-fold desc="重试相关方法">

  /**
   * 重试步骤 :
   * 1. 判断是否可以重试
   * 2. 判断请求是否被取消，如果被取消，则快速结束重试，否则进入第 3 步
   * 3. 判断网络是否连接成功，如果连接成功则快速结束重试，否则进入第 4 步
   * 4. 判断请求是否取消，如果请求被取消则快速结束重试，否则进入第 5 步
   * 5. 等待 {@link BaseRequest#getRetryWaitTimeout()} }
   * 6. 判断请求是否被取消，如果被取消则快速结束重试，否则进入第 7 步
   * 7. 判断重试是否超时，如果超时则快速结束重试，否则进入第 4 步
   */

  /**
   * 是否可以重试
   *
   * @param throwable 异常
   * @return true : 可以重试 ; false : 不可以重试
   */
  public boolean canRetry(Throwable throwable) {
    return throwable instanceof IOException;
  }

  /**
   * 获取重试次数
   *
   * @return 重试次数
   */
  public int getRetryTimes() {
    return DEFAULT_RETRY_TIMES;
  }

  /**
   * 获取请求重试超时时长
   *
   * @return 请求重试超时时长
   */
  public long getRetryTimeout() {
    return DEFAULT_RETRY_TIMEOUT;
  }

  /**
   * 获取重试等待超时时长
   *
   * @return 重试等待超时时长
   */
  public long getRetryWaitTimeout() {
    return DEFAULT_RETRY_WAIT_TIMEOUT;
  }
  //</editor-fold>
}
