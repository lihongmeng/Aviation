package com.jxntv.base.tag;

import androidx.annotation.IntDef;
import com.jxntv.base.R;
import com.jxntv.base.model.anotation.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * feed 标签辅助类
 */
public class TagHelper {

    /** TAG类型：无tag */
    public static final int FEED_TAG_NORMAL = 0;
    /** TAG类型：预告  */
    public static final int FEED_TAG_FORE_SHOW = 1;
    /** TAG类型：广播  */
    public static final int FEED_TAG_BROADCAST = 2;
    /** TAG类型：直播  */
    public static final int FEED_TAG_LIVE = 3;
    /** TAG类型：音频  */
    public static final int FEED_TAG_MUSIC = 4;

    @IntDef({FEED_TAG_NORMAL, FEED_TAG_FORE_SHOW, FEED_TAG_BROADCAST, FEED_TAG_LIVE, FEED_TAG_MUSIC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GvideoTagType {
    }

    /**
     * 获取指定类型对应的图片res
     *
     * @param tagType   tag类型
     * @return 对应的图片res
     */
    public static int getDrawableIdByTagType(@GvideoTagType int tagType) {
        switch (tagType) {
            case FEED_TAG_FORE_SHOW:
                return R.drawable.tag_fore_show;
            case FEED_TAG_BROADCAST:
                return R.drawable.tag_broadcast;
            case FEED_TAG_LIVE:
                return R.drawable.tag_live;
            case FEED_TAG_MUSIC:
                return R.drawable.tag_music;
            default:
                 return 0;
        }
    }

    /**
     * 获取指定类型对应的文字res
     *
     * @param tagType   tag类型
     * @return 对应的文字res
     */
    public static int getStringIdByTagType(@GvideoTagType int tagType) {
        switch (tagType) {
            case FEED_TAG_FORE_SHOW:
                return R.string.tag_fore_show;
            case FEED_TAG_BROADCAST:
                return R.string.tag_broadcast;
            case FEED_TAG_LIVE:
                return R.string.tag_live;
            case FEED_TAG_MUSIC:
                return R.string.tag_music;
            case FEED_TAG_NORMAL:
            default:
                return 0;
        }
    }

    /**
     * 判断type合法性
     *
     * @return type合法性
     */
    static boolean isModelValid(int titleTagType) {
       return MediaType.MediaTypeCheck.isMediaTypeValid(titleTagType);
    }

}
