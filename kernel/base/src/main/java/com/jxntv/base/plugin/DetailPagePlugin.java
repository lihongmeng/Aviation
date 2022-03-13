package com.jxntv.base.plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.jxntv.base.model.video.VideoModel;
import com.jxntv.ioc.Plugin;

/**
 * 分发逻辑
 */
public interface DetailPagePlugin extends Plugin {

    void dispatchToDetail(@NonNull Context context, VideoModel videoModel, Bundle extras);

}
