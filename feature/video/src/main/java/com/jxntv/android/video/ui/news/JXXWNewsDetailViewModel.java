package com.jxntv.android.video.ui.news;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;

import com.google.gson.JsonObject;
import com.jxntv.android.liteav.GVideoView;
import com.jxntv.android.liteav.LiteavConstants;
import com.jxntv.android.liteav.model.GVideoPlayerRenderMode;
import com.jxntv.android.liteav.player.GVideoPlayerListener;
import com.jxntv.android.liteav.view.CoverView;
import com.jxntv.android.video.R;
import com.jxntv.android.video.VideoHelper;
import com.jxntv.android.video.repository.MediaRepository;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.PendantModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.FavoritePlugin;
import com.jxntv.base.tag.TagHelper;
import com.jxntv.base.view.ShareButton;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.GVideoStatManager;
import com.jxntv.stat.StatConstants;
import com.jxntv.stat.StatPid;
import com.jxntv.stat.db.entity.StatEntity;
import com.jxntv.utils.ResourcesUtils;

public class JXXWNewsDetailViewModel extends BaseViewModel {

    // MediaRepository
    private final MediaRepository repository = new MediaRepository();

    // 通知Fragment将播放器全屏
    public final CheckThreadLiveData<Boolean> fullscreenLiveData = new CheckThreadLiveData<>();

    // 通知Fragment处理返回按钮点击
    // 如果发现当前没有播放视频，并且该Id查询到的资源不正常，需要通知Fragment回退
    public final CheckThreadLiveData<Object> backPressLiveData = new CheckThreadLiveData<>();

    // 通知Fragment重新拉去视频数据
    public VideoModel videoModel;
    public final CheckThreadLiveData<Boolean> videoModelLiveData = new CheckThreadLiveData<>();

    // 通知Fragment更新数据，不需要重新拉取视频数据
    public final CheckThreadLiveData<Object> updateLiveData = new CheckThreadLiveData<>();

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

    public JXXWNewsDetailViewModel(@NonNull Application application) {
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
        if (!TextUtils.isEmpty(videoModel.getId())
                && TextUtils.isEmpty(videoModel.getUrl())) {
            updatePlaceholderLayoutType(PlaceholderType.LOADING);
        }
        repository.loadMedia(id)
                .subscribe(new GVideoResponseObserver<VideoModel>() {
                    @Override
                    protected void onSuccess(@NonNull VideoModel netData) {
                        updatePlaceholderLayoutType(PlaceholderType.NONE);

                        // 如果VideoModel只有Id数据，那就要通知Fragment加载全部数据
                        if (!TextUtils.isEmpty(videoModel.getId())
                                && TextUtils.isEmpty(videoModel.getUrl())) {
                            videoModel = netData;
                            videoModelLiveData.setValue(true);
                            return;
                        }

                        // 更新现有数据，有些场景，传进来的数据不完整，干脆全部复制一遍
                        // 就不一一比对了
                        videoModel.setTitle(netData.getTitle());
                        videoModel.setPv(netData.getPv());
                        videoModel.setCreateDate(netData.getCreateDate());
                        videoModel.setAuthor(netData.getAuthor());
                        videoModel.setDescription(netData.getDescription());
                        videoModel.setIsFavor(netData.getIsFavor());
                        videoModel.setMediaType(netData.getMediaType());

                        updateLiveData.setValue(null);
                    }

                    @Override
                    protected void onFailed(@NonNull Throwable throwable) {
                        super.onFailed(throwable);
                        showToast(R.string.this_content_can_not_play);
                        updatePlaceholderLayoutType(PlaceholderType.ERROR);
                    }
                });
    }

    private void loadMedia(@NonNull GVideoView superPlayerView, VideoModel videoModel) {
        String url = videoModel.getUrl();
        if (TextUtils.isEmpty(url)) {
            return;
        }
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

    public ShareDataModel getShareModel() {
        if (videoModel == null) {
            return null;
        }
        ObservableBoolean favorObservable = videoModel.getObservable().getIsFavor();
        return new ShareDataModel.Builder()
                .setVideoModel(videoModel)
                .setShowShare(videoModel.mediaStatus != null && videoModel.mediaStatus == 3)
                .setFavorite(favorObservable.get())
                .setShowFollow(false)
                .setShowDelete(false)
                .setShowCreateBill(true)
                .build();
    }

    public void dealFavoriteClick(View view) {
        if (videoModel == null) {
            return;
        }
        String mediaId = videoModel.getId();
        if (TextUtils.isEmpty(mediaId)) {
            return;
        }
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        if (!accountPlugin.hasLoggedIn()) {
            accountPlugin.startLoginActivity(view.getContext());
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.like)
            );
            return;
        }
        boolean wantToFlavor = videoModel.getIsFavor() <= 0;
        PluginManager
                .get(FavoritePlugin.class)
                .getFavoriteRepository()
                .favoriteMedia(mediaId, wantToFlavor)
                .subscribe(new GVideoResponseObserver<Boolean>() {
                    @Override
                    protected void onSuccess(@NonNull Boolean result) {
                        GVideoSensorDataManager.getInstance().favoriteContent(videoModel, statFromModel.pid,
                                wantToFlavor, null);
                        videoModel.setIsFavor(wantToFlavor ? 1 : -1);
                        ((ShareButton) view).setIconImageResource(wantToFlavor ? R.drawable.ic_news_like_red : R.drawable.ic_news_like);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        GVideoSensorDataManager.getInstance().favoriteContent(videoModel, statFromModel.pid,
                                wantToFlavor, throwable.getMessage());
                    }
                });
    }

    public void clickAuthor(View view){
        if(videoModel==null){
            return;
        }
        AuthorModel authorModel=videoModel.getAuthor();
        if(authorModel==null){
            return;
        }
        PluginManager.get(AccountPlugin.class).startPgcActivity(view,authorModel);
    }

}
