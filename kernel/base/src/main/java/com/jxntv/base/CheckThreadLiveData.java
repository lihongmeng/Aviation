package com.jxntv.base;

import android.os.Looper;
import androidx.lifecycle.LiveData;

/**
 * 检查线程的 LiveData, 只在主线程进行数据更新
 *
 * @param <T> 数据泛型
 *
 * @since 2020-01-20 09:58
 */
public class CheckThreadLiveData<T> extends LiveData<T> {
  //<editor-fold desc="构造函数">
  public CheckThreadLiveData(T value) {
    super(value);
  }

  public CheckThreadLiveData() {
  }
  //</editor-fold>

  //<editor-fold desc="方法重写">
  @Override
  public void setValue(T value) {
    if (isMainThread()) {
      super.setValue(value);
    } else {
      postValue(value);
    }
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 是否为主线程
   *
   * @return true : 是主线程 ; false : 不是主线程
   */
  private boolean isMainThread() {
    return Looper.getMainLooper().equals(Looper.myLooper());
  }
  //</editor-fold>
}
