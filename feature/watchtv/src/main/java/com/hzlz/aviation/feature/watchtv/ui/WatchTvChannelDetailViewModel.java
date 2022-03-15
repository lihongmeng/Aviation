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

    // 界面初始化的时候，由上层指定
    // 点击频道列表进行切换时，更新此值
    public long channelId;

    /**
     * 通过方法
     * {@link com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin#dispatchToDetail}
     * 跳转到频道详情页，将数据携带进来
     * <p>
     * 如果不是通过该方法，那本值除Id之外，全为空
     * <p>
     * 主要用来
     * 1、保存的Id用以查询频道详情
     * 2、保存title用以上报神策大数据
     * <p>
     * 其他情况可以不用理会此值
     */
    public VideoModel videoModel;

    // WatchTvRepository
    private final WatchTvRepository watchTvRepository = new WatchTvRepository();

    // 横向频道列表适配器
    public WatchTvChannelDetailChannelAdapter watchTvChannelDetailChannelAdapter;

    // 标签下ViewPager的adapter
    public BaseFragmentVpAdapter tvListViewPagerAdapter;

    // 时间tab的适配器
    public WatchTvChannelDetailTimeAdapter watchTvChannelDetailTimeAdapter;

    // 通知Fragment更新频道列表
    public final CheckThreadLiveData<Boolean> updateChannelList = new CheckThreadLiveData<>();

    // 频道列表数据（频道横向列表）
    public List<WatchTvChannel> watchTvChannelList = new ArrayList<>();

    // 电视的播放地址
    public String watchTvPlayUrl;

    // 上级页面传递进来的可能是VideoModel,如果是自建频道，就不需要获取频道信息
    public int playType;

    // 因为获取播放数据并开始播放时，需要获取到视频对应的频道信息
    // 但是在异步加载播放数据时，UI线程又点了别的频道，用adapter获取当前频道就不准
    // 频道队列,在每次UI点击进入队列，在成功播放、获取播放数据报错、逻辑return中弹出
    // 这样队列末端就是对应的频道数据
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


    public void initChannelData(IGVideoRefreshLoadMoreView view) {

        watchTvQueue.offer(new WatchTvChannel(channelId));

        // 根据ChannelId获取频道的详情数据
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

                        // 因为页面初始化的时候，可能只有频道Id
                        // 那最好在获取到详情数据的时候，遍历队列，将数据更新进去
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
                            // 频道的详情数据中包含一个获取鉴权的地址，构建网络请求
                            watchTvRepository.getWatchTvAuthentication(
                                    ((GVideoSmartRefreshLayout) view).getContext(),
                                    watchTvChannelDetail.authUrl,
                                    new Callback() {

                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                            // 删除对应的频道信息
                                            watchTvQueue.poll();
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) {
                                            ResponseBody responseBody = response.body();
                                            if (responseBody == null) {
                                                // 删除对应的频道信息
                                                watchTvQueue.poll();
                                                return;
                                            }
                                            JSONObject jsonObject;
                                            try {
                                                // 获取鉴权数据后，和频道详情中的播放地址进行拼接，就是最终的视频地址
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
                                                // 删除对应的频道信息
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

                        // 删除对应的频道信息
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

                    // 正常播放就可以将频道数据出列
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

                // 播完重播
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
