package com.hzlz.aviation.kernel.network.engine;

import com.hzlz.aviation.kernel.network.NetworkManager;
import com.hzlz.aviation.kernel.network.NetworkUtils;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.schedule.GVideoSchedulers;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * INetworkEngine {@link INetworkEngine} 实现类
 *
 *
 * @since 2020-01-03 16:30
 */
public final class RealNetworkEngine implements INetworkEngine {
  //<editor-fold desc="属性">
  // 请求 : Disposable HasMap, 用于取消请求
  private Map<BaseRequest, Disposable> mRequestDisposableMap = new ConcurrentHashMap<>();
  //</editor-fold>

  //<editor-fold desc="接口实现">
  @Override
  public <T> Observable<T> executeRequest(final BaseRequest<T> request) {
    // 判断请求是否被取消
    if (request.isCanceled()) {
      throw new RuntimeException("cannot execute a canceled request!!!");
    }
    return request.getObservable()
        // 切换至子线程
        .subscribeOn(GVideoSchedulers.IO_PRIORITY_BACKGROUND)
        // 重试
        .retry(request.getRetryTimes(), throwable -> retryRequest(request, throwable))
        // 记录请求的 Disposable
        .doOnSubscribe(disposable -> mRequestDisposableMap.put(request, disposable))
        // 移除请求的 Disposable
        .doOnError(throwable -> mRequestDisposableMap.remove(request))
        .doOnComplete(() -> mRequestDisposableMap.remove(request))
        // 切换至主线程
        .observeOn(GVideoSchedulers.MAIN);
  }

  @Override
  public void cancelRequest(BaseRequest request) {
    if (!request.isCanceled()) {
      // 从 HasMap 中移除请求，并取消请求
      Disposable disposable = mRequestDisposableMap.remove(request);
      if (disposable != null && !disposable.isDisposed()) {
        disposable.dispose();
        // 标记请求被取消
        request.cancel();
        // 通知其他线程请求被取消
        synchronized (RealNetworkEngine.class) {
          RealNetworkEngine.class.notifyAll();
        }
      }
    }
  }

  @Override
  public void cancelAllRequest() {
    // 循环 Map 取消所有请求
    Set<Map.Entry<BaseRequest, Disposable>> entrySet = mRequestDisposableMap.entrySet();
    for (Map.Entry<BaseRequest, Disposable> entry : entrySet) {
      // 取消请求
      Disposable disposable = entry.getValue();
      disposable.dispose();
      // 标记请求被取消
      BaseRequest request = entry.getKey();
      request.cancel();
    }
    // 清空 HasMap
    mRequestDisposableMap.clear();
    // 通知其他线程请求被取消
    synchronized (RealNetworkEngine.class) {
      RealNetworkEngine.class.notifyAll();
    }
  }
  //</editor-fold>

  //<editor-fold desc="请求重试">

  /**
   * 重试请求
   *
   * @param request 请求
   * @param throwable 异常
   * @return true : 表示可以被重试 ; false : 表示无法进行重试
   */
  private boolean retryRequest(BaseRequest request, Throwable throwable) {
    // 判断改请求时候可以重试
    if (!request.canRetry(throwable)) {
      return false;
    }
    // 判断请求是否被取消
    if (request.isCanceled()) {
      return false;
    }
    // 日志回调
    if (NetworkManager.getInstance().getLogger() != null) {
      NetworkManager.getInstance().getLogger().retry(request, throwable);
    }
    // 死循环进行等待
    long current = 0;
    while (true) {
      // 检测网络是否连接成功
      if (NetworkUtils.isNetworkConnected()) {
        break;
      }
      // 检测请求是否被取消
      if (request.isCanceled()) {
        return false;
      }
      // 等待
      long start = System.currentTimeMillis();
      synchronized (RealNetworkEngine.class) {
        try {
          RealNetworkEngine.class.wait(request.getRetryWaitTimeout());
        } catch (InterruptedException ignore) {
        }
      }
      // 检测请求是否被取消
      if (request.isCanceled()) {
        return false;
      }
      // 累加超时时间
      current += (System.currentTimeMillis() - start);
      // 判断是否超时
      if (current >= request.getRetryTimeout()) {
        return false;
      }
    }
    return true;
  }
  //</editor-fold>
}
