package com.hzlz.aviation.kernel.base;

/**
 * Activity中注册系统Back键处理接口
 */
public interface BackPressHandler {
  boolean onBackPressed();
}