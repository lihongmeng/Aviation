package com.hzlz.aviation.kernel.base.placeholder;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 占位布局类型
 *
 *
 * @since 2020-01-06 23:42
 */
@IntDef({
    PlaceholderType.NONE,
    PlaceholderType.LOADING,
    PlaceholderType.NETWORK_NOT_AVAILABLE,
    PlaceholderType.ERROR,
    PlaceholderType.EMPTY,
    PlaceholderType.UN_LOGIN,
    PlaceholderType.EMPTY_SEARCH,
    PlaceholderType.EMPTY_COMMENT
})
@Retention(RetentionPolicy.SOURCE)
public @interface PlaceholderType {
  int NONE = 0;
  int LOADING = 1;
  int NETWORK_NOT_AVAILABLE = 2;
  int ERROR = 3;
  int EMPTY = 4;
  int UN_LOGIN = 5;
  int EMPTY_SEARCH = 6;
  int EMPTY_COMMENT = 7;
}
