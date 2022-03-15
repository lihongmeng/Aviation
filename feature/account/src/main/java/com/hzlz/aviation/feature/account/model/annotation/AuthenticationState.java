package com.hzlz.aviation.feature.account.model.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 认证状态
 *
 *
 * @since 2020-02-18 10:38
 */
@IntDef({
    AuthenticationState.NONE,
    AuthenticationState.UN_AUTHENTICATED,
    AuthenticationState.AUTHENTICATING,
    AuthenticationState.AUTHENTICATED,
})
@Retention(RetentionPolicy.SOURCE)
public @interface AuthenticationState {
  int NONE = -1;
  int UN_AUTHENTICATED = 0;
  int AUTHENTICATING = 1;
  int AUTHENTICATED = 2;
}
