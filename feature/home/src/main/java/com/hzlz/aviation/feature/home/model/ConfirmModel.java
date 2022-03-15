package com.hzlz.aviation.feature.home.model;

/**
 * 用户授权时记录当前状态；方便将来协议变化时针对哪些用户进行重新授权；
 * 首次使用时、用户注册时会记录；
 */
public class ConfirmModel {
  public String deviceId;
  public String userId;
  public String protocol;
  public String protocolVersion;
  public String date;
}
