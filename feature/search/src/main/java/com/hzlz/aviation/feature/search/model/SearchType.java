package com.hzlz.aviation.feature.search.model;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 搜索类型
 */

@IntDef({
        SearchType.CATEGORY_ALL,
        SearchType.CATEGORY_AUTHORS,
        SearchType.CATEGORY_COMMUNITY,
        SearchType.CATEGORY_PROGRAM,
        SearchType.CATEGORY_NEWS,
        SearchType.CATEGORY_MOMENT,
})
@Retention(RetentionPolicy.SOURCE)
public @interface SearchType {
    /**
     * 全部
     */
    int CATEGORY_ALL = 0;
    /**
     * 用户
     */
    int CATEGORY_AUTHORS = 1;
    /**
     * 社区
     */
    int CATEGORY_COMMUNITY = 2;
    /**
     * 新闻
     */
    int CATEGORY_NEWS = 3;
    /**
     * 节目
     */
    int CATEGORY_PROGRAM = 5;
    /**
     * 动态
     */
    int CATEGORY_MOMENT = 4;
}
