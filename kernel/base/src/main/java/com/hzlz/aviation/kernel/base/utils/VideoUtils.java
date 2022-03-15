package com.hzlz.aviation.kernel.base.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.File;

public class VideoUtils {

    public static Bitmap getFirstFrameBitmap(File video){
        return getFirstFrameBitmap(video.getPath());
    }

    public static Bitmap getFirstFrameBitmap(String videoUrl){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoUrl);
        return mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }

}
