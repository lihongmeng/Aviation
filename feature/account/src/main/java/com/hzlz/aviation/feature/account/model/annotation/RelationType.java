package com.hzlz.aviation.feature.account.model.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({
    RelationType.START_FOLLOW,
    RelationType.START_FANS,
})
public @interface RelationType {
  int START_FOLLOW = 0;
  int START_FANS = 1;
}
