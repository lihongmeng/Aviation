package com.jxntv.network.observer;

import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 响应监听基类
 *
 * @param <T> 响应模型泛型
 *
 * @since 2020-01-06 16:07
 */
public abstract class BaseResponseObserver<T> implements Observer<T> {

  //<editor-fold desc="内部方法">

  /**
   * 当请求开始回调<br/>
   * 注 : 这里不提供 Disposable 参数，
   * 所有请求取消统一通过 {@link BaseDataRepository#cancelRequest(BaseRequest)}处理
   */
  @SuppressWarnings("WeakerAccess")
  protected void onRequestStart() {

  }

  /**
   * 当请求结束回调，即 onError 回调，onComplete 也回调<br/>
   */
  protected void onRequestFinished() {

  }
  //</editor-fold>

  //<editor-fold desc="抽象方法">

  /**
   * 当请求有数据回调
   *
   * @param t 数据模型
   */
  protected abstract void onRequestData(T t);

  /**
   * 当请求发生异常回调
   *
   * @param throwable 异常
   */
  protected abstract void onRequestError(Throwable throwable);

  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public void onSubscribe(Disposable d) {
    onRequestStart();
  }

  @Override
  public void onNext(T value) {
    onRequestData(value);
  }

  @Override
  public void onError(Throwable e) {
    onRequestError(e);
    onRequestFinished();
  }

  @Override
  public void onComplete() {
    onRequestFinished();
  }
  //</editor-fold>
}
