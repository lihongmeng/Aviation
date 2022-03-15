package com.hzlz.aviation.kernel.base.model.anotation;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 性别注解
 *
 *
 * @since 2020-02-06 09:59
 */
@IntDef({
    Gender.NONE,
    Gender.MALE,
    Gender.FEMALE,
})
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface Gender {
  int NONE = 0;
  int MALE = 1;
  int FEMALE = 2;
}
