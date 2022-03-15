package com.hzlz.aviation.feature.video.model.anotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 媒体类型
 *
 * @since 2020-02-13 20:25
 */
@IntDef({
        AtyLiveStatus.PREVIEW,
        AtyLiveStatus.LIVING,
        AtyLiveStatus.PLAYBACK,
        AtyLiveStatus.OFF_SHELF,
})
@Retention(RetentionPolicy.SOURCE)
public @interface AtyLiveStatus {
    /*预告*/
    int PREVIEW = 1;
    /*直播中*/
    int LIVING = 2;
    /*回放*/
    int PLAYBACK = 3;
    /*下架*/
    int OFF_SHELF = 4;
}


