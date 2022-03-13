package com.jxntv.pptv.model.annotation;

import androidx.annotation.StringDef;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PPTV 筛选分类注解
 *
 */
@StringDef({
    CategoryKey.YEAR,
    CategoryKey.TYPE,
    CategoryKey.AREA,
    CategoryKey.TAG,
    CategoryKey.PLATE
})
@Target({ ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface CategoryKey {
  String YEAR = "release_year";
  String TYPE = "program_type";
  String AREA = "country";
  String TAG = "tag";
  String PLATE = "plate";
}
