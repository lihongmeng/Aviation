package com.jxntv.feed.api;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * feed api使用常量
 */
public class FeedApiConstants {

    /** 媒体类型 */
    public static final String CHANNEL_TYPE = "type";
    /** cursor */
    public static final String CURSOR = "cursor";
    /** 垂类id */
    public static final String CHANNEL_ID = "channelId";

    /** media类型 home */
    public static final String CHANNEL_TYPE_HOME = "home";
    /** media类型 video */
    public static final String CHANNEL_TYPE_VIDEO = "video";
    /** media类型 radio */
    public static final String CHANNEL_TYPE_RADIO = "fm";
    /** media类型 live */
    public static final String CHANNEL_TYPE_LIVE = "live";

    /** type */
    public static final String TYPE = "type";
    /** 长资源 */
    public static final int TYPE_LONG = 1;
    /** 短资源 */
    public static final int TYPE_SHORT = 2;

    @IntDef({TYPE_LONG, TYPE_SHORT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ResourceType {
    }

}
