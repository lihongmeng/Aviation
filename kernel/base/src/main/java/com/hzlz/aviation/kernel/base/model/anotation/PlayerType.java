package com.hzlz.aviation.kernel.base.model.anotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 播放器类型注解
 */
@IntDef({
    PlayerType.GVIDEO,
    PlayerType.SIMPLE,
})
@Retention(RetentionPolicy.SOURCE)
public @interface PlayerType {
  int GVIDEO = 0;
  int SIMPLE = 10;
}
