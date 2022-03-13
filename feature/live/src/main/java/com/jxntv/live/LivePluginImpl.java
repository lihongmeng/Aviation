package com.jxntv.live;

import static com.jxntv.base.Constant.EXTRA_LIVE_TYPE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jxntv.base.BaseFragment;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.LivePlugin;
import com.jxntv.live.ui.LiveActivity;
import com.jxntv.live.ui.homelive.HomeLiveFragment;
import com.jxntv.live.ui.homelive.homelive.HomeLiveContentFragment;

import io.reactivex.rxjava3.core.Observable;

/**
 * @author huangwei
 * date : 2021/3/4
 * desc :
 **/
public class LivePluginImpl implements LivePlugin {

    @Override
    public void initTencentIM() {
        LiveManager.getInstance().init();
    }

    @Override
    public void startAuthorPrepareActivity(@NonNull Context context) {
        Intent intent = new Intent(context, LiveActivity.class);
        intent.putExtra(Constants.INTENT_LIVE_TYPE, R.id.author_prepare_fragment);
        context.startActivity(intent);
    }

    @Override
    public void startAuthorLiveActivity(@NonNull Context context, String mediaId, String title,
                                        String shareUrl, Integer liveType) {
        Intent intent = new Intent(context, LiveActivity.class);
        intent.putExtra(Constants.INTENT_LIVE_ID, mediaId);
        intent.putExtra(Constants.INTENT_LIVE_TITLE, title);
        intent.putExtra(Constants.INTENT_LIVE_SHARE, shareUrl);
        if (liveType != null) {
            intent.putExtra(EXTRA_LIVE_TYPE, liveType);
        }
        intent.putExtra(Constants.INTENT_LIVE_TYPE, R.id.videoAuthorLiveFragment);
        context.startActivity(intent);
    }

    @Override
    public void startAudienceActivity(@NonNull Context context, VideoModel videoModel, Bundle bundle) {
        Intent intent = new Intent(context, LiveActivity.class);
        intent.putExtra(Constants.INTENT_LIVE_TYPE, R.id.videoAudienceFragment);
        intent.putExtra(Constants.INTENT_ID, videoModel.getId());
        intent.putExtra(Constants.EXTRA_VIDEO_MODEL, videoModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void checkHasLive(@NonNull Context context) {
        LiveManager.getInstance().checkHasLive(context);
    }

    @Nullable
    @Override
    public Observable<String> getHasLivePermission() {
        return LiveManager.getInstance().hadLivePermission();
    }

    @Override
    public BaseFragment getHomeLiveFragment() {
        return new HomeLiveFragment();
    }

    @Override
    public BaseFragment getHomeLiveContentFragment() {
        return new HomeLiveContentFragment();
    }

}