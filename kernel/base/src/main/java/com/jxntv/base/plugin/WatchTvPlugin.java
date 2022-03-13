package com.jxntv.base.plugin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.ioc.Plugin;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc : 看电视
 **/
public interface WatchTvPlugin extends Plugin {

    void addDestinations(@NonNull BaseFragment fragment);

    /**
     * 创建一个HomeWatchTvFragment
     *
     * @return HomeWatchTvFragment
     */
    BaseFragment getHomeWatchTvFragment();

    /**
     * 以startActivity的形式启动渠道详情页
     *
     * @param context    Context
     * @param videoModel 视频相关数据
     * @param bundle     其他的数据
     */
    void startWatchTvChannelDetailWithActivity(Context context, VideoModel videoModel, Bundle bundle);

    /**
     * 以startActivity的形式启动整期详情页
     *
     * @param context          Context
     * @param columnId         栏目Id
     * @param source_scenePage 来源页面名称
     */
    void startWatchTvWholePeriodDetailWithActivity(Context context, long columnId, String source_scenePage);


    /**
     * 以startActivity的形式启动整期详情页,并跳转到指定节目
     *
     * @param context          Context
     * @param columnId         栏目Id
     * @param programId        节目id
     * @param source_scenePage 来源页面名称
     */
    void startWatchTvWholePeriodDetailWithActivity(Context context, long columnId, long programId, String source_scenePage);

}
