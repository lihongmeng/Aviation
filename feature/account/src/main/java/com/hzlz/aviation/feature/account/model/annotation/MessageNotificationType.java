package com.hzlz.aviation.feature.account.model.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 消息通知类型
 *
 *
 * @since 2020-03-12 09:57
 */
@IntDef({
    MessageNotificationType.TEXT,
    MessageNotificationType.BUTTON_LINK,
    MessageNotificationType.IMAGE_LINK,
})
@Retention(RetentionPolicy.SOURCE)
public @interface MessageNotificationType {
  int TEXT = 0;
  int BUTTON_LINK = 1;
  int IMAGE_LINK = 2;
  int INTERACTIVE = 3;
  int FOLLOW = 4;
}
