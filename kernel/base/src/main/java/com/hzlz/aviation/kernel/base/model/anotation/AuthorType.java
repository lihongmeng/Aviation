package com.hzlz.aviation.kernel.base.model.anotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用户类型注解
 *
 */
@IntDef({
    AuthorType.PGC,
    AuthorType.UGC,
})
@Retention(RetentionPolicy.SOURCE)
public @interface AuthorType {
  int PGC = 0;
  int UGC = 1;
}
