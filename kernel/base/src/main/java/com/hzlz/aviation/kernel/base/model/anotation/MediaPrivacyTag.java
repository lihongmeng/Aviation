package com.hzlz.aviation.kernel.base.model.anotation;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视频隐私状态注解
 *
 */
@IntDef({
    MediaPrivacyTag.PRIVATE,
    MediaPrivacyTag.PUBLIC,
    MediaPrivacyTag.AUTHENTICATING,
})
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface MediaPrivacyTag {
  int PRIVATE = 0; //私密
  int PUBLIC = 1; //审核完成可以公开
  int AUTHENTICATING = 2; //审核中只有发布者可见
}
