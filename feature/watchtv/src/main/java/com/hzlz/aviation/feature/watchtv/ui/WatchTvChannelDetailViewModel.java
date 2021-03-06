package com.hzlz.aviation.feature.watchtv.ui;

import static com.hzlz.aviation.kernel.base.Constant.LIVE_TO_WATCH_TV_PLAY_TYPE.BUILD_SELF;
import static com.hzlz.aviation.kernel.base.Constant.WATCH_TV_CHANNEL_TYPE.BUILD_SELF_LIVE;
import static com.hzlz.aviation.kernel.base.Constant.WATCH_TV_CHANNEL_TYPE.TV;
import static com.hzlz.aviation.kernel.base.model.anotation.MediaType.WATCH_TV;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.video.VideoHelper;
import com.hzlz.aviation.feature.watchtv.WatchTvRepository;
import com.hzlz.aviation.feature.watchtv.adapter.WatchTvChannelDetailChannelAdapter;
import com.hzlz.aviation.feature.watchtv.adapter.WatchTvChannelDetailTimeAdapter;
import com.hzlz.aviation.feature.watchtv.entity.WatchTvChannelDetail;
import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.CheckThreadLiveData;
import com.hzlz.aviation.kernel.base.adapter.BaseFragmentVpAdapter;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.PendantModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.model.video.WatchTvChannel;
import com.hzlz.aviation.kernel.base.screenprojection.DLNAHelper;
import com.hzlz.aviation.kernel.base.view.GVideoSmartRefreshLayout;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.liteav.player.GVideoPlayerListener;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.network.response.ListWithPage;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WatchTvChannelDetailViewModel extends BaseViewModel {

    // ??????????????????????????????????????????
    // ????????????????????????????????????????????????
    public long channelId;

    /**
     * ????????????
     * {@link com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin#dispatchToDetail}
     * ????????????????????????????????????????????????
     * <p>
     * ??????????????????????????????????????????Id??????????????????
     * <p>
     * ????????????
     * 1????????????Id????????????????????????
     * 2?????????title???????????????????????????
     * <p>
     * ????????????????????????????????????
     */
    public VideoModel videoModel;

    // WatchTvRepository
    private final WatchTvRepository watchTvRepository = new WatchTvRepository();

    // ???????????????????????????
    public WatchTvChannelDetailChannelAdapter watchTvChannelDetailChannelAdapter;

    // ?????????ViewPager???adapter
    public BaseFragmentVpAdapter tvListViewPagerAdapter;

    // ??????tab????????????
    public WatchTvChannelDetailTimeAdapter watchTvChannelDetailTimeAdapter;

    // ??????Fragment??????????????????
    public final CheckThreadLiveData<Boolean> updateChannelList = new CheckThreadLiveData<>();

    // ??????????????????????????????????????????
    public List<WatchTvChannel> watchTvChannelList = new ArrayList<>();

    // ?????????????????????
    public String watchTvPlayUrl;

    // ????????????????????????????????????VideoModel,??????????????????????????????????????????????????????
    public int playType;

    // ???????????????????????????????????????????????????????????????????????????????????????
    // ???????????????????????????????????????UI?????????????????????????????????adapter???????????????????????????
    // ????????????,?????????UI????????????????????????????????????????????????????????????????????????return?????????
    // ?????????????????????????????????????????????
    public final LinkedList<WatchTvChannel> watchTvQueue = new LinkedList<>();

    public boolean isVisible;
    private long mPlayStart;
    public long mPlayDuration;
    private long mVideoTime;
    private boolean mIsFinished;
    public StatFromModel statFromModel;
    public final CheckThreadLiveData<Boolean> startPlayTv = new CheckThreadLiveData<>();
    public final CheckThreadLiveData<Boolean> fullscreenLiveData = new CheckThreadLiveData<>();
    public final CheckThreadLiveData<Object> backPressLiveData = new CheckThreadLiveData<>();
    public final CheckThreadLiveData<Object> lockPortraitLiveData = new CheckThreadLiveData<>();
    public final CheckThreadLiveData<Object> onPlayBeginLiveData = new CheckThreadLiveData<>();

    public WatchTvChannelDetailViewModel(@NonNull Application application) {
        super(application);
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

    public void onRefresh(IGVideoRefreshLoadMoreView view) {
        initChannelData(view);
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
        // ??????????????????????????????
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


    public void initChannelData(IGVideoRefreshLoadMoreView view) {

        watchTvQueue.offer(new WatchTvChannel(channelId));

        // ??????ChannelId???????????????????????????
        watchTvRepository.getChannelInfo(channelId).
                subscribe(new BaseResponseObserver<WatchTvChannelDetail>() {
                    @Override
                    protected void onRequestData(WatchTvChannelDetail watchTvChannelDetail) {
                        LogUtils.d("WatchTvChannelDetail -->>" + watchTvChannelDetail.channelName);
                        GVideoSensorDataManager.getInstance().clickWatchTvLive(
                                Long.toString(watchTvChannelDetail.id),
                                watchTvChannelDetail.channelName,
                                watchTvChannelDetail.channelName
                        );

                        // ???????????????????????????????????????????????????Id
                        // ?????????????????????????????????????????????????????????????????????????????????
                        for (WatchTvChannel watchTvChannel : watchTvQueue) {
                            if (watchTvChannel == null
                                    || watchTvChannel.id == null
                                    || !watchTvChannel.id.equals(watchTvChannelDetail.id)) {
                                continue;
                            }
                            watchTvChannel.name = watchTvChannelDetail.channelName;
                            watchTvChannel.picUrl = watchTvChannelDetail.coverUrl;
                        }

                        if (playType != BUILD_SELF) {
                            // ??????????????????????????????????????????????????????????????????????????????
                            watchTvRepository.getWatchTvAuthentication(
                                    ((GVideoSmartRefreshLayout) view).getContext(),
                                    watchTvChannelDetail.authUrl,
                                    new Callback() {

                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                            // ???????????????????????????
                                            watchTvQueue.poll();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) {
                                            ResponseBody responseBody = response.body();
                                            if (responseBody == null) {
                                                // ???????????????????????????
                                                watchTvQueue.poll();
                                                return;
                                            }
                                            JSONObject jsonObject;
                                            try {
                                                // ???????????????????????????????????????????????????????????????????????????????????????????????????
                                                jsonObject = new JSONObject(responseBody.string());
                                                StringBuilder params = new StringBuilder("?");
                                                Iterator<String> iterator = jsonObject.keys();
                                                while (iterator.hasNext()) {
                                                    String key = iterator.next();
                                                    params.append(key)
                                                            .append("=")
                                                            .append(jsonObject.optString(key))
                                                            .append("&");
                                                }
                                                String result = params.toString();
                                                if (result.endsWith("&")) {
                                                    result = result.substring(0, result.length() - 1);
                                                }
                                                watchTvPlayUrl = watchTvChannelDetail.playUrl + result;
                                                startPlayTv.setValue(true);
                                                LogUtils.d("WatchTvPlayUrl -->> " + watchTvPlayUrl);
                                            } catch (Exception e) {
                                                watchTvPlayUrl = "";
                                                // ???????????????????????????
                                                watchTvQueue.poll();
                                            }
                                        }
                                    }
                            );
                        } else {
                            watchTvPlayUrl = watchTvChannelDetail.playUrl;
                            startPlayTv.setValue(true);
                        }
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {

                        // ???????????????????????????
                        watchTvQueue.poll();

                        LogUtils.d("");
                    }
                });

        if (playType != BUILD_SELF) {
            watchTvRepository.getChannelList(
                    TV + "",
                    1,
                    100
            ).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseResponseObserver<ListWithPage<WatchTvChannel>>() {
                        @Override
                        protected void onRequestData(ListWithPage<WatchTvChannel> netData) {
                            watchTvChannelList = netData.getList();
                            if (watchTvChannelList == null) {
                                watchTvChannelList = new ArrayList<>();
                            }
                            updateChannelList.setValue(true);
                            view.finishGVideoRefresh();
                        }

                        @Override
                        protected void onRequestError(Throwable throwable) {
                            view.finishGVideoRefresh();
                            watchTvChannelList = new ArrayList<>();
                            updateChannelList.setValue(true);
                        }
                    });
        } else {
            watchTvRepository.getChannelList(
                    BUILD_SELF_LIVE + "",
                    1,
                    100
            ).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseResponseObserver<ListWithPage<WatchTvChannel>>() {
                        @Override
                        protected void onRequestData(ListWithPage<WatchTvChannel> netData) {
                            watchTvChannelList = netData.getList();
                            if (watchTvChannelList == null) {
                                watchTvChannelList = new ArrayList<>();
                            }
                            updateChannelList.setValue(true);
                            view.finishGVideoRefresh();
                        }

                        @Override
                        protected void onRequestError(Throwable throwable) {
                            view.finishGVideoRefresh();
                            watchTvChannelList = new ArrayList<>();
                            updateChannelList.setValue(true);
                        }
                    });
        }

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
                    if (mPlayStart > 0) {
                        long now = System.currentTimeMillis();
                        long duration = now - mPlayStart;
                        mPlayDuration += duration;
                    }
                    mPlayStart = System.currentTimeMillis();

                    // ??????????????????????????????????????????
                    WatchTvChannel watchTvChannel = watchTvQueue.poll();
                    if (watchTvChannel == null) {
                        return;
                    }
                    GVideoSensorDataManager.getInstance().videoPlay(
                            new VideoModel(watchTvChannel,superPlayerView.getDuration()),
                            0
                    );
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

                // ????????????
                startPlayTv.setValue(true);
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

            @Override
            public void onScreenProjection(View view) {
                DLNAHelper.getInstance().startScreenProjection(view.getContext(), watchTvPlayUrl);
                GVideoSensorDataManager.getInstance().enterDeviceSelectPage(
                        videoModel,
                        WATCH_TV,
                        ""
                );
                lockPortraitLiveData.setValue(true);
            }

            @Override
            public void onEndScreenProjection(View view) {
                DLNAHelper dlnaHelper = DLNAHelper.getInstance();
                dlnaHelper.setCurrentPlayUrl("");
                dlnaHelper.setNeedPlayUrl("");
                dlnaHelper.setCurrentDevice(null);
                dlnaHelper.stop();
                superPlayerView.hideScreenProjectionLayout();
                superPlayerView.resume();
            }
        });
    }
}
