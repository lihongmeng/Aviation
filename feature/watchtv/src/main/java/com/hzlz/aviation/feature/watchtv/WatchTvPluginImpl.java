package com.hzlz.aviation.feature.watchtv;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.COLUMN_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.PROGRAM_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.START_ACTIVITY_DESTINATION_ID;
import static com.hzlz.aviation.kernel.base.Constant.BUNDLE_KEY.VIDEO_MODEL;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.watchtv.ui.HomeWatchTvFragment;
import com.hzlz.aviation.feature.watchtv.ui.WatchTvActivity;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.WatchTvPlugin;

public class WatchTvPluginImpl implements WatchTvPlugin {

    @Override
    public void addDestinations(@NonNull BaseFragment fragment) {
        fragment.addDestination(R.navigation.watch_tv_nav_graph);
    }

    /**
     * 创建一个HomeWatchTvFragment
     *
     * @return HomeWatchTvFragment
     */
    @Override
    public BaseFragment getHomeWatchTvFragment() {
        return new HomeWatchTvFragment();
    }

    /**
     * 以startActivity的形式启动渠道详情页
     *
     * @param context    Context
     * @param videoModel 媒体相关数据
     * @param bundle     其他的数据
     */
    @Override
    public void startWatchTvChannelDetailWithActivity(Context context, VideoModel videoModel, Bundle bundle) {
        Intent intent = new Intent(context, WatchTvActivity.class);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putParcelable(VIDEO_MODEL, videoModel);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 以startActivity的形式启动整期详情页
     *
     * @param context  Context
     * @param columnId 有的地方使用的是节目Id,例如看电视首页的热播节目列表
     *                 有的地方使用的频道Id,例如看电视首页下方的Viewpager内容列表
     *                 具体需要和后端的同事沟通
     * @param source_scenePage
     */
    @Override
    public void startWatchTvWholePeriodDetailWithActivity(Context context, long columnId, String source_scenePage) {
        startWatchTvWholePeriodDetailWithActivity(context, columnId, 0, source_scenePage);
    }

    @Override
    public void startWatchTvWholePeriodDetailWithActivity(Context context, long columnId, long programId, String source_scenePage) {
        Intent intent = new Intent(context, WatchTvActivity.class);
        intent.putExtra(COLUMN_ID, columnId);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        if (columnId > 0) {
            intent.putExtra(PROGRAM_ID, programId);
        }
        intent.putExtra(Constant.EXTRA_FROM_PID, source_scenePage);
        intent.putExtra(START_ACTIVITY_DESTINATION_ID, R.id.watchTvWholePeriodDetailFragment);
        context.startActivity(intent);
    }
}
