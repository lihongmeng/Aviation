package com.hzlz.aviation.library.ioc;

import java.util.HashMap;
import java.util.Map;

public final class PluginManager {

  private static final Map<Class<? extends Plugin>, Plugin> PLUGIN_MAP = new HashMap<>();

  private PluginManager() {
  }

  public static <T extends Plugin> void register(Class<T> c, T plugin) {
    PLUGIN_MAP.put(c, plugin);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Plugin> T get(Class<T> c) {
    return (T) PLUGIN_MAP.get(c);
  }
}
