package com.hzlz.aviation.kernel.base.plugin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.library.ioc.Plugin;

import io.reactivex.rxjava3.core.Observable;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc : 直播
 **/
public interface LivePlugin extends Plugin {

    //<editor-fold desc="常量">

    void initTencentIM();

    void startAuthorPrepareActivity(@NonNull Context context);

    void startAuthorLiveActivity(@NonNull Context context, String mediaId, String title, String shareUrl, Integer liveType);

    void startAudienceActivity(@NonNull Context context, VideoModel videoModel, Bundle bundle);

    /**
     * 是否有断开的直播
     */
    void checkHasLive(@NonNull Context context);


    /**
     * 是否有开直播直播权限
     *
     * @return  对应的observable
     */
    @Nullable
    Observable<String> getHasLivePermission();

    BaseFragment getHomeLiveFragment();

    BaseFragment getHomeLiveContentFragment();

}
