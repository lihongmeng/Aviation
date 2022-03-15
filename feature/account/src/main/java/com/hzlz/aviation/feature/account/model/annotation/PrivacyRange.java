package com.hzlz.aviation.feature.account.model.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 隐私范围注解
 *
 *
 * @since 2020-02-06 10:02
 */
@IntDef({
    PrivacyRange.NONE,
    PrivacyRange.PUBLIC,
    PrivacyRange.ONLY_SELF,
})
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD })
public @interface PrivacyRange {
  int NONE = 0;
  int PUBLIC = 1;
  int ONLY_SELF = 2;
}
