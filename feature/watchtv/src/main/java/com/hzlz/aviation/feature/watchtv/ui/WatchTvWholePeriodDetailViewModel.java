package com.hzlz.aviation.feature.watchtv.ui;

import static com.hzlz.aviation.kernel.base.model.anotation.MediaType.COLLECTION_DETAIL;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.video.VideoHelper;
import com.hzlz.aviation.feature.watchtv.WatchTvRepository;
import com.hzlz.aviation.feature.watchtv.entity.ColumnDetail;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.share.ShareDataModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.model.video.PendantModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.SharePlugin;
import com.hzlz.aviation.kernel.base.screenprojection.DLNAHelper;
import com.hzlz.aviation.kernel.base.tag.TagHelper;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.liteav.LiteavConstants;
import com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode;
import com.hzlz.aviation.kernel.liteav.player.GVideoPlayerListener;
import com.hzlz.aviation.kernel.liteav.view.CoverView;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.library.ioc.PluginManager;

public class WatchTvWholePeriodDetailViewModel extends BaseViewModel {

    // 视频相关数据
    public VideoModel videoModel;

    // 播放器全屏事件监听
    public final CheckThreadLiveData<Boolean> fullscreenLiveData = new CheckThreadLiveData<>();

    // 播放器返回按钮点击事件监听
    public final CheckThreadLiveData<Object> backPressLiveData = new CheckThreadLiveData<>();

    public final CheckThreadLiveData<Object> lockPortraitLiveData = new CheckThreadLiveData<>();
    public final CheckThreadLiveData<Object> onPlayBeginLiveData = new CheckThreadLiveData<>();

    public final CheckThreadLiveData<ColumnDetail> columnDetailLiveData = new CheckThreadLiveData<>();

    public final CheckThreadLiveData<Boolean> followLiveData = new CheckThreadLiveData<>();

    // 神策统计相关数据
    public StatFromModel statFromModel;

    // 记录最开始播放视频的时间
    public long playStart;

    // 看的过程中可能暂停，所以在状态改变的时候，每次累加时间
    // 就得到视频真正的播放时间
    public long playDuration;

    // 由播放器回调的播放状态
    public boolean isPlayingStatus;

    public boolean isVisible;
    public long mVideoTime;
    public boolean isFinished;

    public WatchTvWholePeriodDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void getColumnDetail(String columnId){
        new WatchTvRepository().getColumnDetail(columnId).subscribe(new GVideoResponseObserver<ColumnDetail>(){

            @Override
            protected void onSuccess(@NonNull ColumnDetail detail) {
                columnDetailLiveData.setValue(detail);
                followLiveData.setValue(detail.getGroup().isJoin());
                ObservableBoolean joinCircleObservable = InteractDataObservable.getInstance().getJoinCircleObservable(detail.getGroup().groupId);
                joinCircleObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        if (sender.equals(joinCircleObservable)){
                            followLiveData.setValue(joinCircleObservable.get());
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);

            }
        });
    }

    public void setVideoListener(@NonNull GVideoView superPlayerView) {
        superPlayerView.setKSVideoPlayerListener(new GVideoPlayerListener() {
            @Override
            public void onPlayPrepared() {
                if (!isVisible) {
                    pause(superPlayerView);
                } else {
                    superPlayerView.showPendantByModel();
                }
            }

            @Override
            public void onPlayBegin() {
                onPlayBeginLiveData.setValue(null);
                if (!isVisible) {
                    pause(superPlayerView);
                } else {
                    playStart = System.currentTimeMillis();
                    isFinished = false;
                }
            }

            @Override
            public void onPlayEnd() {
                if (!isVisible) {
                    return;
                }
                long now = System.currentTimeMillis();
                long duration = (now - playStart);
                if (playDuration <= 0) {
                    isFinished = true;
                }
                playDuration += duration;
            }

            @Override
            public void onFullscreenChanged(boolean fullscreen) {
                if (!isVisible) {
                    return;
                }
                fullscreenLiveData.setValue(fullscreen);


            }

            @Override
            public void onBackPressed() {
                if (!isVisible) {
                    return;
                }

                dealBack(superPlayerView);

                if (!superPlayerView.handleBackPressed()) {
                    backPressLiveData.setValue(null);
                }
            }


            // 此方法只能监听视频暂停、播放、重新播放
            // 切换到其他视频、退出页面、播放完成都不会回调此方法
            // 切换到其他视频、退出页面的埋点需要单独做
            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                isPlayingStatus = isPlaying;
                if (!isVisible) {
                    return;
                }
                if (!isPlaying) {

                    // 每次不再继续播放，就用当前时间减去开始播放的时间，
                    // 累加到playDuration中
                    playDuration += System.currentTimeMillis() - playStart;
                    Log.d("onPlayStateChanged", "playDuration -->> " + playDuration);

                }
            }

            @Override
            public void onProgressChange(int playDuration, int loadingDuration, int duration) {
                if (!isVisible) {
                    return;
                }
                mVideoTime = duration;
            }

            @Override
            public void onPendantShow(PendantModel pendantModel) {
                if (!isVisible) return;
                statPendant(pendantModel, false);
            }

            @Override
            public void onPendantClick(PendantModel pendantModel) {
                if (!isVisible) return;
                superPlayerView.handleBackPressed();
                boolean fromDetail = TextUtils.equals(statFromModel.fromPid, StatPid.DETAIL);
                VideoHelper.handleAdvertClick(
                        superPlayerView,
                        pendantModel,
                        fromDetail
                );
                statPendant(pendantModel, true);
            }

            @Override
            public void onShareClick(View view) {
                if (videoModel == null) {
                    return;
                }
                ShareDataModel shareDataModel = new ShareDataModel.Builder()
                        .setVideoModel(videoModel)
                        .setShowShare(videoModel.mediaStatus != null && videoModel.mediaStatus == 3)
                        .setReportUrl(videoModel.getShareUrl())
                        .setShowFollow(false)
                        .setShowFavorite(false)
                        .setShowDelete(false)
                        .build();
                PluginManager.get(SharePlugin.class).showShareDialog(view.getContext(), false, shareDataModel, statFromModel);
            }

            @Override
            public void onScreenProjection(View view) {
                DLNAHelper.getInstance().startScreenProjection(view.getContext(),videoModel.getUrl());
                lockPortraitLiveData.setValue(true);
                GVideoSensorDataManager.getInstance().enterDeviceSelectPage(
                        videoModel,
                        COLLECTION_DETAIL,
                        ""
                );
            }
        });

    }

    public void dealBack(GVideoView superPlayerView) {

        // 迅速操作的情况下，会空指针
        if (superPlayerView == null) {
            return;
        }
        // 点击返回按钮，需要考虑视频正在全屏播放的情况
        if (!superPlayerView.isFullScreen() && videoModel != null) {
            dealLastVideoPlayEnd(superPlayerView.getDuration());
        }
    }

    /**
     * 目前有连个入口会导致
     */
    public void dealLastVideoPlayEnd(long videoTotalTime) {

        // 播放器初始化没完成，就不上报时间
        if (playStart == 0) {
            return;
        }

        // 可能正在播放，所以需要再计算最后播放的时长
        if (!isPlayingStatus) {

            // 每次不再继续播放，就用当前时间减去开始播放的时间，
            // 累加到playDuration中
            playDuration += (System.currentTimeMillis() - playStart);
            Log.d("onPlayStateChanged", "playDuration -->> " + playDuration);
        }

        GVideoSensorDataManager.getInstance().programPlay(
                videoModel,
                isFinished,
                playDuration,
                playStart + "",
                "programDetailPage",
                videoTotalTime
        );
    }

    public void loadMedia(@NonNull GVideoView superPlayerView, VideoModel videoModel) {
        String url = videoModel.getUrl();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        videoModel.setStatFromModel(statFromModel);
        superPlayerView.initModel(videoModel);
        if (videoModel.isAudio()) {
            int coverType = CoverView.SOUND_MUSIC;
            if (videoModel.getTagType() == TagHelper.FEED_TAG_LIVE) {
                coverType = CoverView.SOUND_FM;
            }
            superPlayerView.setSoundCover(videoModel.getCoverUrl(), coverType);
        } else {
            superPlayerView.showCover(videoModel.getCoverUrl());
        }
        superPlayerView.setTitleModeInWindowMode(LiteavConstants.TITLE_MODE_ONLY_BACK);
        superPlayerView.updateTitle(videoModel.getTitle(), TagHelper.FEED_TAG_NORMAL);
        superPlayerView.setCanResize(true);
        superPlayerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_ADJUST_RESOLUTION);
        superPlayerView.startPlay(url, videoModel);
    }

    void setVisible(boolean visible) {
        isVisible = visible;
    }

    void resume(@NonNull GVideoView superPlayerView) {
        superPlayerView.resume();
    }

    void pause(@NonNull GVideoView superPlayerView) {
        superPlayerView.pause();
    }

    void destroy(@NonNull GVideoView superPlayerView) {
        superPlayerView.release();
    }

    private void statPendant(PendantModel pendantModel, boolean click) {
        String extendId = pendantModel.extendId;
        String extendName = pendantModel.title;
        String extendShowType = String.valueOf(pendantModel.extendType);
        String place = StatConstants.DS_KEY_PLACE_PENDANT;
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(statFromModel);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_ID, extendId);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_NAME, extendName);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_SHOW_TYPE, extendShowType);
        ds.addProperty(StatConstants.DS_KEY_PLACE, place);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withPid(statFromModel != null ? statFromModel.pid : "")
                .withEv(StatConstants.EV_ADVERT)
                .withDs(ds.toString())
                .withType(click ? StatConstants.TYPE_CLICK_C : StatConstants.TYPE_SHOW_E)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

    public void statPlay() {
        // 没有播放数据，不打点
        if (playDuration > 1000) {
            String isFinish = isFinished ? "1" : "0";
            JsonObject ds = GVideoStatManager.getInstance().createDsContent(statFromModel);
            ds.addProperty(StatConstants.DS_KEY_PLAY_DURATION, String.valueOf(playDuration));
            ds.addProperty(StatConstants.DS_KEY_VIDEO_TIME, String.valueOf(mVideoTime));
            ds.addProperty(StatConstants.DS_KEY_IS_FINISH, isFinish);
            StatEntity statEntity = StatEntity.Builder.aStatEntity()
                    .withEv(StatConstants.EV_PLAY)
                    .withDs(ds.toString())
                    .withPid(statFromModel != null ? statFromModel.pid : "")
                    .build();
            GVideoStatManager.getInstance().stat(statEntity);
        }

        playStart = 0;
        playDuration = 0;
        mVideoTime = 0;
        isFinished = false;
    }

    public String getCircleName() {
        if (videoModel == null) {
            return "";
        }
        String circleName=videoModel.getGroupName();
        return TextUtils.isEmpty(circleName) ? "" : circleName;
    }

}
