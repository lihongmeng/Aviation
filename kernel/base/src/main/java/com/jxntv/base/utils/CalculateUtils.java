package com.jxntv.base.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Pair;

import java.io.File;

public class CalculateUtils {

    // 根据获取视频文件第一帧，计算视频文件的长宽
    public static Pair<Integer, Integer> getVideoFileWidthHeight(File video) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(video.getPath());
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        return new Pair<>(bitmap.getWidth(), bitmap.getHeight());
    }

}
