package com.jxntv.pptv.model.annotation;

import androidx.annotation.IntDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef({
    BannerType.MEDIA,
    BannerType.URL,
})
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface BannerType {
  int MEDIA = 0;
  int URL = 1;
}
