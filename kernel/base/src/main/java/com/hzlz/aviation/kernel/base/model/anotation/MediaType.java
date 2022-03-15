package com.hzlz.aviation.kernel.base.model.anotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 媒体类型
 *
 * @since 2020-02-13 20:25
 */
@IntDef({
        MediaType.SHORT_VIDEO,
        MediaType.LONG_VIDEO,
        MediaType.SHORT_AUDIO,
        MediaType.LONG_AUDIO,
        MediaType.VERTICAL_LIVE,
        MediaType.HORIZONTAL_LIVE,
        MediaType.IM_HORIZONTAL_LIVE,
        MediaType.IM_VERTICAL_LIVE,
        MediaType.IMAGE_TXT,
        MediaType.AUDIO_TXT,
        MediaType.NEWS_IMAGE,
        MediaType.NEWS_RIGHT_IMAGE,
        MediaType.NEWS_LINK,
        MediaType.NEWS_SPECIAL_ITEM,
        MediaType.NEWS_SCROLL,
        MediaType.FXA_DETAIL,
        MediaType.HOME_CIRCLE,
        MediaType.CIRCLE_DETAIL,
        MediaType.TOPIC_DETAIL,
        MediaType.WATCH_TV_CHANNEL_BUILD_SELF,
        MediaType.WATCH_TV,
        MediaType.COLLECTION_DETAIL,
        MediaType.QA_DETAIL,
        MediaType.NEWS_SPECIAL_HORIZONTAL_SCROLL,
        MediaType.QA_LIST_GROUP,
})
@Retention(RetentionPolicy.SOURCE)
public @interface MediaType {
    /*短视频、竖视频*/
    int SHORT_VIDEO = 2;
    /*长视频、横视频*/
    int LONG_VIDEO = 1;
    /*短音频、竖音频*/
    int SHORT_AUDIO = 4;
    /*长音频、横音频*/
    int LONG_AUDIO = 3;
    /*横活动直播*/
    int HORIZONTAL_LIVE = 5;
    /*竖活动直播*/
    int VERTICAL_LIVE = 6;
    /*互动横屏直播*/
    int IM_HORIZONTAL_LIVE = 7;
    /*互动竖屏直播*/
    int IM_VERTICAL_LIVE = 8;
    /* 图文展示*/
    int IMAGE_TXT = 9;
    /* 语音文字*/
    int AUDIO_TXT = 10;
    /* 新闻大图或三图*/
    int NEWS_IMAGE = 11;
    /* 新闻左文右图*/
    int NEWS_RIGHT_IMAGE = 12;
    /* 外链*/
    int NEWS_LINK = 13;
    /* 特殊展示块, 新闻顶部专题展示样式*/
    int NEWS_SPECIAL_ITEM = 14;
    /* 滚动新闻*/
    int NEWS_SCROLL = 15;
    /* 首页圈子 */
    int HOME_CIRCLE = 16;
    /* 横向滑动专题新闻 */
    int NEWS_SPECIAL_HORIZONTAL_SCROLL = 17;

    /* 圈子详情 */
    int CIRCLE_DETAIL = 20;
    /* 话题详情 */
    int TOPIC_DETAIL = 21;
    /* 放心爱详情 */
    int FXA_DETAIL = 22;
    /* 看电视自建频道详情页 */
    int WATCH_TV_CHANNEL_BUILD_SELF=23;
    /* 看电视频道详情页 */
    int WATCH_TV = 24;
    /* 整期详情 */
    int COLLECTION_DETAIL = 25;
    /* 问答详情 */
    int QA_DETAIL = 26;
    /* 问答广场 */
    int QA_LIST_GROUP = 27;

    class MediaTypeCheck {
        public static boolean isMediaTypeValid(@MediaType int mediaType) {
            return mediaType >= LONG_VIDEO && mediaType <= NEWS_SPECIAL_HORIZONTAL_SCROLL;
        }


        /**
         * 是否需要检查数据
         */
        public static boolean isNeedCheckData(@MediaType int mediaType) {
            return !(mediaType == NEWS_SPECIAL_ITEM || mediaType == NEWS_SCROLL
            || mediaType == NEWS_SPECIAL_HORIZONTAL_SCROLL );
        }
    }
}


