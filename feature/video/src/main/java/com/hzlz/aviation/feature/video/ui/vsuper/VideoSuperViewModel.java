package com.hzlz.aviation.feature.video.ui.vsuper;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.video.VideoHelper;
import com.hzlz.aviation.feature.video.repository.MediaRepository;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.PendantModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.tag.TagHelper;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.liteav.LiteavConstants;
import com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode;
import com.hzlz.aviation.kernel.liteav.player.GVideoPlayerListener;
import com.hzlz.aviation.kernel.liteav.view.CoverView;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.feature.video.R;

public class VideoSuperViewModel extends BaseViewModel {

    // MediaRepository
    private final MediaRepository repository = new MediaRepository();

    // 通知Fragment将播放器全屏
    public final CheckThreadLiveData<Boolean> fullscreenLiveData = new CheckThreadLiveData<>();

    // 通知Fragment处理返回按钮点击
    // 如果发现当前没有播放视频，并且该Id查询到的资源不正常，需要通知Fragment回退
    public final CheckThreadLiveData<Object> backPressLiveData = new CheckThreadLiveData<>();

    // 播放的视频相关数据
    public VideoModel videoModel;
    public final CheckThreadLiveData<Boolean> videoModelLiveData = new CheckThreadLiveData<>();

    // 通知Fragment更新标题
    public final CheckThreadLiveData<String> titleLiveData = new CheckThreadLiveData<>();

    // =================== 播放过程中记录的参数 ===================
    private boolean isVisible;
    private long mPlayStart;
    private long mPlayDuration;
    private long mVideoTime;
    private boolean mIsFinished;
    // =========================================================

    // =================== 神策上报的相关数据 ===================
    public StatFromModel statFromModel;
    public String fromPid;
    public String fromChannelId;
    // =======================================================

    public VideoSuperViewModel(@NonNull Application application) {
        super(application);
    }

    public StatFromModel getStatFromModel() {
        return statFromModel;
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
                if (!isVisible) {
                    pause(superPlayerView);
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
                fullscreenLiveData.setValue(fullscreen);
            }

            @Override
            public void onBackPressed() {
                if (!isVisible) {
                    return;
                }
                if (!superPlayerView.handleBackPressed()) {
                    backPressLiveData.setValue(null);
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
                superPlayerView.handleBackPressed();
                boolean fromDetail = TextUtils.equals(statFromModel.fromPid, StatPid.DETAIL);
                VideoHelper.handleAdvertClick(
                        superPlayerView,
                        pendantModel,
                        fromDetail
                );
                statPendant(pendantModel, true);
            }

        });
    }

    public void start(@NonNull GVideoView superPlayerView) {
        if (videoModel == null) {
            return;
        }
        String url = videoModel.getUrl();
        if (!TextUtils.isEmpty(url)) {
            loadMedia(superPlayerView, videoModel);
        }
    }

    public void loadInfo(String id) {
        repository.loadMedia(id)
                .subscribe(new GVideoResponseObserver<VideoModel>() {
                    @Override
                    protected void onSuccess(@NonNull VideoModel netData) {

                        // 当前保留的可能是上层传递进来的不完整的数据，无论如何先确保关键数据不为空
                        videoModel.setMediaType(netData.getMediaType());

                        // 如果当前有数据正在播放，并且和网络数据的关键字段一致
                        // 就可以忽略本次网络加载
                        String netTitle = netData.getTitle();
                        String currentTitle = videoModel.getTitle();
                        if (TextUtils.equals(netTitle, currentTitle)) {
                            return;
                        }

                        // 如果VideoModel只有Id数据，那就要通知Fragment加载全部数据
                        // 否则说明当前播放数据绝大部分不需要更改，修改特殊的几个地方即可
                        if (!TextUtils.isEmpty(videoModel.getId())
                                && TextUtils.isEmpty(videoModel.getUrl())) {
                            videoModel = netData;
                            videoModelLiveData.setValue(true);
                        } else {
                            videoModel.setTitle(netTitle);
                            titleLiveData.setValue(netTitle);
                        }

                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        showToast(R.string.this_content_can_not_play);
                        backPressLiveData.setValue(null);
                    }
                });
    }

    private void loadMedia(@NonNull GVideoView superPlayerView, VideoModel videoModel) {
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
        superPlayerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_FILL_SCREEN);
        String url = videoModel.getUrl();
        superPlayerView.startPlay(url, videoModel);
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void resume(@NonNull GVideoView superPlayerView) {
        superPlayerView.resume();
    }

    public void pause(@NonNull GVideoView superPlayerView) {
        superPlayerView.pause();
    }

    public void destroy(@NonNull GVideoView superPlayerView) {
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
        if (mPlayDuration > 1000) {
            String isFinish = mIsFinished ? "1" : "0";
            JsonObject ds = GVideoStatManager.getInstance().createDsContent(statFromModel);
            ds.addProperty(StatConstants.DS_KEY_PLAY_DURATION, String.valueOf(mPlayDuration));
            ds.addProperty(StatConstants.DS_KEY_VIDEO_TIME, String.valueOf(mVideoTime));
            ds.addProperty(StatConstants.DS_KEY_IS_FINISH, isFinish);
            StatEntity statEntity = StatEntity.Builder.aStatEntity()
                    .withEv(StatConstants.EV_PLAY)
                    .withDs(ds.toString())
                    .withPid(statFromModel != null ? statFromModel.pid : "")
                    .build();
            GVideoStatManager.getInstance().stat(statEntity);
        }

        mPlayStart = 0;
        mPlayDuration = 0;
        mVideoTime = 0;
        mIsFinished = false;
    }
}
