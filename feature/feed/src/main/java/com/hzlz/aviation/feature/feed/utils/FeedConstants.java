package com.hzlz.aviation.feature.feed.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * feed 常量类
 */
public class FeedConstants {

    public static final int THOUSAND = 1000;

    /** 推荐数据：广告 */
    public static final int RECOMMEND_AD = 1;
    /** 推荐数据：非广告  */
    public static final int RECOMMEND_NOT_AD = 0;

    @IntDef({RECOMMEND_AD, RECOMMEND_NOT_AD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RecommendAdType {
    }

    /** 推荐数据：非人工添加 */
    public static final int RECOMMEND_NOT_MANUAL = 0;
    /** 推荐数据：人工添加 */
    public static final int RECOMMEND_MANUAL = 1;

    @IntDef({RECOMMEND_NOT_MANUAL, RECOMMEND_MANUAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RecommendManualType {
    }

    /** 非收藏 */
    public static final int NOT_FAVOR = 0;
    /** 收藏 */
    public static final int IS_FAVOR = 1;

    @IntDef({NOT_FAVOR, IS_FAVOR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FavorType {
    }

    /** 评论数据变化 */
    public static final int UPDATE_TYPE_COMMENT = 1;
    /** 关注数据变化 */
    public static final int UPDATE_TYPE_FOLLOW = 2;
    /** 收藏数据变化 */
    public static final int UPDATE_TYPE_FAVORITE = 3;
    @IntDef({UPDATE_TYPE_COMMENT, UPDATE_TYPE_FOLLOW, UPDATE_TYPE_FAVORITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface UpdateType {}

}
