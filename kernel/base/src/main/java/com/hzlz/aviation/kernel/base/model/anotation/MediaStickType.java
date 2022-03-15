package com.hzlz.aviation.kernel.base.model.anotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 媒体自定类型
 *
 *
 * @since 2020-03-10 17:12
 */
@IntDef({
    MediaStickType.NOT_STICK,
    MediaStickType.STICK,
})
@Retention(RetentionPolicy.SOURCE)
public @interface MediaStickType {
  int NOT_STICK = 0;
  int STICK = 1;
}
