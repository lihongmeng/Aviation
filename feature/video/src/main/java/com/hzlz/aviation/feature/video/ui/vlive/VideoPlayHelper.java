package com.hzlz.aviation.feature.video.ui.vlive;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.video.VideoHelper;
import com.hzlz.aviation.feature.video.model.LiveDetailModel;
import com.hzlz.aviation.feature.video.model.anotation.AtyLiveStatus;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.PendantModel;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.liteav.LiteavConstants;
import com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode;
import com.hzlz.aviation.kernel.liteav.player.GVideoPlayerListener;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;

/**
 * @author huangwei
 * date : 2021/2/10
 * desc : 播放器播放工具类
 **/
public class VideoPlayHelper {

    private boolean isVisible;
    private long mPlayStart;
    private long mPlayDuration;
    private long mVideoTime;
    private boolean mIsFinished;
    private StatFromModel mStatFromModel;
    /**
     * 普通横屏模式控制器
     */
    private GVideoAtyLiveHorizontalController horizontalController;
    /**
     * 竖屏模式控制器
     */
    private GVideoAtyLiveVerticalController verticalController;

    private @NonNull GVideoView playerView;

    private CheckThreadLiveData<LiveDetailModel> mLiveDetailData = new CheckThreadLiveData<>();
    private CheckThreadLiveData<Boolean> mFullscreenLiveData = new CheckThreadLiveData<>();
    private CheckThreadLiveData<Object> mBackPressLiveData = new CheckThreadLiveData<>();

    public VideoPlayHelper(@NonNull GVideoView playerView){
        this.playerView = playerView;
        setVideoListener();
    }

    public void startPlay(@NonNull LiveDetailModel liveDetailModel, StatFromModel statFromModel, AtyLiveViewModel viewModel) {

        int status = liveDetailModel.getBroadcastDTO().getStatus();
        //下架布局与正在直播相同
        boolean isLiving = status == AtyLiveStatus.LIVING || status == AtyLiveStatus.OFF_SHELF;
        if (liveDetailModel.getBroadcastDTO().isVerticalPlayStyle()){
            verticalController = new GVideoAtyLiveVerticalController(playerView.getContext(),isLiving);
            verticalController.getVideoLiveVerticalControllerBinding().setViewModel(viewModel);
            playerView.setMediaController(verticalController);
            if (status == AtyLiveStatus.OFF_SHELF){
                playerView.clear();
                destroy();
                verticalController.onReplayShow(true);
                return;
            }
        }else {
            horizontalController = new GVideoAtyLiveHorizontalController(playerView.getContext(), isLiving);
            playerView.setMediaController(horizontalController);
            if (status == AtyLiveStatus.OFF_SHELF){
                playerView.clear();
                destroy();
                horizontalController.onReplayShow(true);
                return;
            }
        }
        mStatFromModel = statFromModel;
        isVisible = true;
        mLiveDetailData.setValue(liveDetailModel);
        start();
    }

    public void changePlayUrl(String url){
        if (playerView==null){
            return;
        }
        if (playerView.isPlaying()){
            playerView.release();
        }
        playerView.startPlay(url);
    }

    private void start() {
        LiveDetailModel liveDetailModel = mLiveDetailData.getValue();
        String url = liveDetailModel.getBroadcastDTO().getLiveUrl();
        if (!TextUtils.isEmpty(url)) {
            loadMedia(liveDetailModel,liveDetailModel.getBroadcastDTO().getStatus() == AtyLiveStatus.PREVIEW);
        }
    }

    public void resume() {
        playerView.resume();

    }

    public void pause() {
        playerView.pause();
    }

    public void destroy() {
        playerView.release();
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean onBackPress() {
        return playerView.handleBackPressed();
    }

    private void setVideoListener() {
        playerView.setKSVideoPlayerListener(new GVideoPlayerListener() {
            @Override
            public void onPlayPrepared() {
                if (!isVisible) {
                    pause();
                } else {
                    playerView.showPendantByModel();
                }
            }

            @Override
            public void onPlayBegin() {
                if (!isVisible) {
                    pause();
                } else {
                    if (mPlayStart > 0) {
                        long now = System.currentTimeMillis();
                        long duration = now - mPlayStart;
                        mPlayDuration += duration;
                    }
                    mPlayStart = System.currentTimeMillis();
                }
            }

            @Override
            public void onPlayEnd() {
                if (!isVisible) {
                    return;
                }
                long now = System.currentTimeMillis();
                long duration = (now - mPlayStart);
                if (mPlayDuration <= 0) {
                    mIsFinished = true;
                }
                mPlayDuration += duration;
                statPlay();
            }

            @Override
            public void onFullscreenChanged(boolean fullscreen) {
                if (!isVisible) {
                    return;
                }
                mFullscreenLiveData.setValue(fullscreen);
                if (horizontalController!=null) {
                    if (fullscreen) {
                        horizontalController.showBackBtn();
                    } else {
                        horizontalController.hideBackBtn();
                    }
                }
            }

            @Override
            public void onBackPressed() {
                if (!isVisible) {
                    return;
                }
                if (!playerView.handleBackPressed()) {
                    mBackPressLiveData.setValue(null);
                }
            }

            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                if (!isVisible) {
                    return;
                }
                if (isPlaying) {
                    //if (mPlayStart > 0) {
                    //  long now = System.currentTimeMillis();
                    //  long duration = now - mPlayStart;
                    //  mPlayDuration += duration;
                    //}
                    //mPlayStart = System.currentTimeMillis();
                } else {
                    long now = System.currentTimeMillis();
                    long duration = now - mPlayStart;
                    mPlayDuration += duration;

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
                playerView.handleBackPressed();
                boolean fromDetail = TextUtils.equals(mStatFromModel.fromPid, StatPid.DETAIL);
                VideoHelper.handleAdvertClick(
                        playerView,
                        pendantModel,
                        fromDetail
                );
                statPendant(pendantModel, true);
            }
        });
    }

    private void loadMedia(LiveDetailModel liveDetailModel, boolean isLoop) {
        if (playerView.isPlaying()){
            playerView.release();
        }
        playerView.showCover(liveDetailModel.getBroadcastDTO().getThumbUrl());
        playerView.setTitleModeInWindowMode(LiteavConstants.TITLE_MODE_ONLY_BACK);
        playerView.setCanResize(false);
        playerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_ADJUST_RESOLUTION);
        playerView.startPlay(liveDetailModel.getBroadcastDTO().getLiveUrl());
        //设置循环播放
        playerView.setLoop(isLoop);
    }


    private void statPendant(PendantModel pendantModel, boolean click) {
        String extendId = pendantModel.extendId;
        String extendName = pendantModel.title;
        String extendShowType = String.valueOf(pendantModel.extendType);
        String place = StatConstants.DS_KEY_PLACE_PENDANT;
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(mStatFromModel);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_ID, extendId);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_NAME, extendName);
        ds.addProperty(StatConstants.DS_KEY_EXTEND_SHOW_TYPE, extendShowType);
        ds.addProperty(StatConstants.DS_KEY_PLACE, place);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withPid(mStatFromModel != null ? mStatFromModel.pid : "")
                .withEv(StatConstants.EV_ADVERT)
                .withDs(ds.toString())
                .withType(click ? StatConstants.TYPE_CLICK_C : StatConstants.TYPE_SHOW_E)
                .build();
        GVideoStatManager.getInstance().stat(statEntity);
    }

   private void statPlay() {
        // 没有播放数据，不打点
        if (mPlayDuration > 1000) {
            String isFinish = mIsFinished ? "1" : "0";
            JsonObject ds = GVideoStatManager.getInstance().createDsContent(mStatFromModel);
            ds.addProperty(StatConstants.DS_KEY_PLAY_DURATION, String.valueOf(mPlayDuration));
            ds.addProperty(StatConstants.DS_KEY_VIDEO_TIME, String.valueOf(mVideoTime));
            ds.addProperty(StatConstants.DS_KEY_IS_FINISH, isFinish);
            StatEntity statEntity = StatEntity.Builder.aStatEntity()
                    .withEv(StatConstants.EV_PLAY)
                    .withDs(ds.toString())
                    .withPid(mStatFromModel != null ? mStatFromModel.pid : "")
                    .build();
            GVideoStatManager.getInstance().stat(statEntity);
        }

        mPlayStart = 0;
        mPlayDuration = 0;
        mVideoTime = 0;
        mIsFinished = false;
    }
}
