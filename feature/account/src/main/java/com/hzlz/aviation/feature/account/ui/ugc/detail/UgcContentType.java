package com.hzlz.aviation.feature.account.ui.ugc.detail;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 媒体类型
 *
 * @since 2020-02-13 20:25
 */
@IntDef({
        UgcContentType.COMPOSITION,
        UgcContentType.COMMENT,
        UgcContentType.FAVORITE,
        UgcContentType.QUESTION,
        UgcContentType.ANSWER,
})
@Retention(RetentionPolicy.SOURCE)
public @interface UgcContentType {
    /**
     * 动态
     */
    int COMPOSITION = 1;
    /**
     * 评论
     */
    int COMMENT = 2;
    /**
     * 喜欢
     */
    int FAVORITE = 3;
    /**
     * 提问
     */
    int QUESTION = 4;
    /**
     * 回答
     */
    int ANSWER = 5;
}


