package com.hzlz.aviation.kernel.media;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 视频页面来源
 */
public class MediaPageSource {

    @IntDef({
            PageSource.DEFAULT,
            PageSource.PGC,
            PageSource.NEWS,
            PageSource.AUDIOVISUAL,
            PageSource.MINE,
            PageSource.MINE_ANSWER,
            PageSource.SEARCH,
            PageSource.TV_COLLECTION,
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface PageSource {
        int DEFAULT = 0;
        /**
         * pgc主页
         */
        int PGC = 1;
        /**
         * 新闻板块
         */
        int NEWS = 2;
        /**
         * 视听板块
         */
        int AUDIOVISUAL = 3;
        /**
         * 我的
         */
        int MINE = 4;
        /**
         * 我的回答
         */
        int MINE_ANSWER = 5;
        /**
         * 搜索板块
         */
        int SEARCH = 6;
        /**
         * 看电视整期
         */
        int TV_COLLECTION = 7;
    }
}
