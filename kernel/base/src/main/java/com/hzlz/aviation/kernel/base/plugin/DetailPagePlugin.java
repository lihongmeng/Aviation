package com.hzlz.aviation.kernel.base.plugin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.library.ioc.Plugin;

/**
 * 分发逻辑
 */
public interface DetailPagePlugin extends Plugin {

    void dispatchToDetail(@NonNull Context context, VideoModel videoModel, Bundle extras);

}
