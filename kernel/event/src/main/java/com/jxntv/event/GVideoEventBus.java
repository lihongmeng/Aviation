package com.jxntv.event;

import com.jeremyliao.liveeventbus.core.Config;
import com.jeremyliao.liveeventbus.core.LiveEventBusCore;
import com.jeremyliao.liveeventbus.core.Observable;

/**
 * 事件总线入口。封装了LiveEventBus。
 */
public class GVideoEventBus {
  public static <T> Observable<T> get(String key, Class<T> type) {
    return LiveEventBusCore.get().with(key, type);
  }

  public static Observable<Object> get(String key) {
    return get(key, Object.class);
  }

  public static Config config() {
    return LiveEventBusCore.get().config();
  }
}
