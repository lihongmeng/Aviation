package com.hzlz.aviation.kernel.base.model.anotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 关键显示类型
 *
 *
 * @since 2020-03-10 16:43
 */
@IntDef({
    PendantShowType.NOT_ALWAYS_SHOW,
    PendantShowType.ALWAYS_SHOW,
})
@Retention(RetentionPolicy.SOURCE)
public @interface PendantShowType {
  int NOT_ALWAYS_SHOW = 0;
  int ALWAYS_SHOW = 1;
}
