package com.hzlz.aviation.feature.video.ui.vshort;

import static com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode.RENDER_MODE_ADJUST_RESOLUTION;
import static com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode.RENDER_MODE_FILL_SCREEN;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.video.VideoHelper;
import com.hzlz.aviation.feature.video.repository.MediaRepository;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.PendantModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.tag.TagHelper;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.liteav.LiteavConstants;
import com.hzlz.aviation.kernel.liteav.player.GVideoPlayerListener;
import com.hzlz.aviation.kernel.liteav.view.CoverView;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;

public class VideoShortViewModel extends BaseViewModel {

    // MediaRepository
    private final MediaRepository mediaRepository = new MediaRepository();

    // 需要使用上层传递进来的VideoModel获取最新的视频数据
    // View层通关观测这个值进行更新
    public final MutableLiveData<Boolean> videoLiveData = new MutableLiveData<>();

    /**
     * 代表当前播放的视频数据，需要在
     * {@link BaseFragment#getVideoModel()}方法中返回给Base层
     * 并且需要保持更新
     */
    public VideoModel videoModel;

    // ================================ 神策相关数据 ================================
    private long mPlayStart;
    private long mPlayDuration;
    private long mVideoTime;
    private boolean mIsFinished;
    private StatFromModel mStatFromModel;
    private boolean mIsVisible = false;
    // ============================================================================

    public VideoShortViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 初始化播放器
     *
     * @param gVideoView 播放器View
     * @param videoModel 视频相关数据
     */
    public void initKSView(GVideoView gVideoView, VideoModel videoModel) {
        this.videoModel = videoModel;
        gVideoView.initModel(videoModel);
        gVideoView.setKSVideoPlayerListener(new GVideoPlayerListener() {
            private boolean isBegin = false;

            @Override
            public void onPlayPrepared() {
                if (!mIsVisible) {
                    gVideoView.pause();
                } else {
                    gVideoView.showController();
                }
            }

            @Override
            public void onPlayBegin() {
                if (!isBegin) {
                    isBegin = true;
                }
                if (!mIsVisible) {
                    gVideoView.pause();
                } else {
                    gVideoView.showController();

                    //拖拽后调用
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
                if (!mIsVisible) {
                    return;
                }
                //setLoop(true) 时不会调用；
                isBegin = false;

                if (mPlayStart > 0) {
                    long now = System.currentTimeMillis();
                    long duration = now - mPlayStart;
                    mPlayDuration += duration;

                    statPlay();
                }
            }

            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                if (!mIsVisible) {
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
                    if (mPlayStart > 0) {
                        long now = System.currentTimeMillis();
                        long duration = now - mPlayStart;
                        mPlayDuration += duration;
                    }
                }
            }

            @Override
            public void onProgressChange(int playDuration, int loadingDuration, int duration) {
                if (!mIsVisible) {
                    return;
                }
                mVideoTime = duration;
                // setLoop（true）无法获取播放完成事件，通过进度条来近似
                if (playDuration + 100 >= duration) {
                    mIsFinished = mPlayDuration <= 0;
                    onPlayEnd();
                }
            }

            @Override
            public void onPendantShow(PendantModel pendantModel) {
                statPendant(pendantModel, false);
            }

            @Override
            public void onPendantClick(PendantModel pendantModel) {
                boolean fromDetail = TextUtils.equals(mStatFromModel.fromPid, StatPid.DETAIL);
                VideoHelper.handleAdvertClick(
                        gVideoView,
                        pendantModel,
                        fromDetail
                );
                statPendant(pendantModel, true);
            }
        });
    }

    public void setStat(StatFromModel stat) {
        mStatFromModel = stat;
    }

    public void loadInfo(GVideoView gVideoView) {
        if (videoModel == null) {
            return;
        }
        String coverUrl = videoModel.getCoverUrl();
        if (videoModel.isAudio()) {
            int coverType = CoverView.SOUND_MUSIC;
            if (videoModel.getTagType() == TagHelper.FEED_TAG_LIVE) {
                coverType = CoverView.SOUND_FM;
            }
            gVideoView.setSoundCover(coverUrl, coverType);
        } else {
            gVideoView.showCover(coverUrl, false);
        }
        gVideoView.setTitleModeInWindowMode(LiteavConstants.TITLE_MODE_ONLY_BACK);
        gVideoView.updateTitle(videoModel.getTitle(), TagHelper.FEED_TAG_NORMAL);
        gVideoView.setCanResize(false);
        gVideoView.setRenderMode(videoModel.isVerticalMedia() ? RENDER_MODE_FILL_SCREEN : RENDER_MODE_ADJUST_RESOLUTION);

        String url = videoModel.getUrl();
        gVideoView.startPlay(url, videoModel);
        gVideoView.setLoop(true);

        if (TextUtils.isEmpty(url)) {
            mediaRepository.loadMedia(videoModel.getId())
                    .subscribe(new GVideoResponseObserver<VideoModel>() {
                        @Override
                        protected void onSuccess(@NonNull VideoModel netData) {
                            videoModel = netData;
                            videoLiveData.postValue(true);
                        }
                    });
        } else {
            videoLiveData.postValue(true);
        }
    }


    public void setVisible(boolean visible) {
        mIsVisible = visible;
    }

    public void resume(GVideoView gVideoView) {
        if (mIsVisible && !gVideoView.isPlaying()) {
            gVideoView.resume();
        }
    }

    public void pause(GVideoView gVideoView) {
        gVideoView.pause();
    }

    public void destroy(GVideoView gVideoView) {
        gVideoView.release();
    }

    public void triggerPlay(GVideoView gVideoView) {
        if (gVideoView.isPlaying()) {
            pause(gVideoView);
        } else {
            resume(gVideoView);
        }
    }

    public void seek(GVideoView gVideoView, int position) {
        gVideoView.seek(position);
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

    public void statPlay() {
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
        mVideoTime = 0;
        mPlayStart = 0;
        mPlayDuration = 0;
        mIsFinished = false;
    }
}
