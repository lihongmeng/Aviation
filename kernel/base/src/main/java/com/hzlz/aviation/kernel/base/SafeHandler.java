package com.hzlz.aviation.kernel.base;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * 安全的 Handler, 监听生命周期
 *
 *
 * @since 2020-02-05 17:54
 */
public final class SafeHandler extends Handler implements LifecycleObserver {
  //<editor-fold desc="构造函数">
  public SafeHandler(@NonNull LifecycleOwner owner) {
    super(Looper.getMainLooper());
    // 监听生命周期
    owner.getLifecycle().addObserver(this);
  }
  //</editor-fold>

  //<editor-fold desc="生命周期">
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void onDestroy() {
    removeCallbacksAndMessages(null);
  }
  //</editor-fold>
}
