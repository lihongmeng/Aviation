package com.hzlz.aviation.feature.account.model.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 头像入口注解
 *
 */
@IntDef({
    AvatarEntry.LOCAL,
    AvatarEntry.GALLERY,
    AvatarEntry.CAMERA,
})
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface AvatarEntry {
  int LOCAL = 0;
  int GALLERY = 1;
  int CAMERA = 2;
}
