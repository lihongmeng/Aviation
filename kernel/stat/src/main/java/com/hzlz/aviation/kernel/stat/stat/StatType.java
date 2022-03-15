package com.hzlz.aviation.kernel.stat.stat;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 埋点类型
 */
@StringDef({StatConstants.TYPE_APP_A,
    StatConstants.TYPE_CLICK_C,
    StatConstants.TYPE_SHOW_E,
    StatConstants.TYPE_PAGE_W,
    StatConstants.TYPE_PAGE_Q})
@Retention(RetentionPolicy.SOURCE)
public @interface StatType {
}
