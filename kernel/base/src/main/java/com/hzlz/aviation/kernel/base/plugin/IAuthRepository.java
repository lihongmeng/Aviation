package com.hzlz.aviation.kernel.base.plugin;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Observable;

/**
 * 授权接口
 *
 */
public interface IAuthRepository {
  int PROTOCOL_USER = 1;
  int PROTOCOL_PRIVACY = 2;
  int PROTOCOL_UNSUBSCRIBE = 3;
  int PROTOCOL_FEEDBACK = 4;
  int PROTOCOL_REGISTER = 5;

  /**
   * 同意协议，授权
   */
  @NonNull
  Observable<Object> authVisit(Integer... authProtocols);
}
