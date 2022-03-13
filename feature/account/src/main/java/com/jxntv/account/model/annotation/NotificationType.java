package com.jxntv.account.model.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 消息通知类型
 *
 */
@IntDef({
    NotificationType.SYSTEM,
    NotificationType.PLATFORM,
    NotificationType.INTERACTIVE,
    NotificationType.FOLLOW,
    NotificationType.QA,
})
@Retention(RetentionPolicy.SOURCE)
public @interface NotificationType {
  // 系统消息
  int SYSTEM = 0;
  // 认证消息
  int PLATFORM = 1;
  // 互动消息
  int INTERACTIVE = 2;
  // 粉丝关注消息
  int FOLLOW = 3;
  // 问答
  int QA = 4;
}
