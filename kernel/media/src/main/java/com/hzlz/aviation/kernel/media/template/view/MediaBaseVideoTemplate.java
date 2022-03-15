package com.hzlz.aviation.kernel.media.template.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.hzlz.aviation.kernel.base.Constant.CHANNEL_ID.VIDEO;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_TYPE.HERALD;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_TYPE.LIVING;
import static com.hzlz.aviation.kernel.base.Constant.LIVE_TYPE.REVIEW;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.hzlz.aviation.kernel.base.BaseFragment;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.PendantModel;
import com.hzlz.aviation.kernel.base.tag.TagTextHelper;
import com.hzlz.aviation.kernel.base.view.LiveStartTimeView;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.liteav.GVideoView;
import com.hzlz.aviation.kernel.liteav.LiteavConstants;
import com.hzlz.aviation.kernel.liteav.controller.GVideoFeedLiveController;
import com.hzlz.aviation.kernel.liteav.controller.GVideoFeedWBController;
import com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode;
import com.hzlz.aviation.kernel.liteav.player.GVideoPlayerListener;
import com.hzlz.aviation.kernel.media.MediaConstants;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.databind.MediaVideoDataBind;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.player.MediaPlayManager;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.media.template.MediaBaseTemplate;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.library.util.ScreenUtils;
import com.hzlz.aviation.library.util.SizeUtils;

/**
 * media 视频模板基类
 */
public abstract class MediaBaseVideoTemplate extends MediaBaseTemplate {

    /**
     * context
     */
    protected Context mContext;
    /**
     * 当前数据模型
     */
    protected GVideoView mPlayerView;
    /**
     * 播放控件父布局
     */
    protected View playerViewLayout;
    /**
     * 当前数据模型
     */
    protected MediaVideoDataBind mFeedVideoDataBind;
    /**
     * 播放器在子页面复用，忽略当前页面操作
     */
    protected boolean mIgnorePlayerAction = false;
    /**
     * 当前模板是否为全屏
     */
    protected boolean mIsFullScreen = false;

    private String mFragmentId;

    private long mPlayStart;
    private long mPlayDuration;
    private long mVideoTime;
    private StatFromModel mStatFromModel;

    /**
     * 用于动态计算toolbar宽度，部分确定宽度
     */
    protected float mAuthorNameMaxWidth = - 1;

    // 屏幕宽度
    private int screenWidth;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     */
    public MediaBaseVideoTemplate(Context context) {
        super(context);
        mContext = context;
        screenWidth = ScreenUtils.getScreenWidth(mContext);
    }

    /**
     * 初始化公用视图
     */
    void initCommonView() {
        GVideoPlayerListener listener = new GVideoPlayerListener() {

            @Override
            public void onPlayPrepared() {
                mPlayerView.showPendantByModel();
            }

            @Override
            public void onPlayBegin() {
                if (mPlayStart > 0) {
                    long now = System.currentTimeMillis();
                    long duration = now - mPlayStart;
                    mPlayDuration += duration;
                }
                mPlayStart = System.currentTimeMillis();
                onVideoPlayBegin();
                mMediaModel.totalPlayDuration=mPlayerView.getDuration();
                GVideoSensorDataManager.getInstance().videoPlay(mMediaModel, -1);
            }

            @Override
            public void onPlayEnd() {
                mMediaModel.playState = MediaConstants.STATE_STOP;
                if (!mIsFullScreen) {
                    MediaPlayManager.getInstance().onPlayEnd(mMediaModel.tabId, mMediaModel.viewPosition);
                }
                boolean mIsFinished;
                long now = System.currentTimeMillis();
                long duration = (now - mPlayStart);
                if (mPlayDuration <= 0) {
                    mIsFinished = true;
                } else {
                    mIsFinished = false;
                }
                mPlayDuration += duration;
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.VIDEO_PLAY_END).post(null);
                onVideoPlayEnd();
                statPlay(mIsFinished);
            }

            @Override
            public boolean interceptFullScreenEvent() {
                return handleInterceptFullScreenEvent();
            }

            @Override
            public void onFullscreenChanged(boolean fullscreen) {
                if (getPlayerViewParent() != null) {
                    mIsFullScreen = fullscreen;
                    if (fullscreen) {
                        ViewGroup group = (ViewGroup) mPlayerView.getParent();
                        if (group != null) {
                            group.removeView(mPlayerView);
                        }
                        mPlayerView.setCanResize(false);
                        mPlayerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_ADJUST_RESOLUTION);
                        mPlayerView.hideCover();
                        if (mViewGroup != null) {
                            mPlayerView.setTag(MediaConstants.playViewTag);
                            mViewGroup.addView(mPlayerView);
                        }
                    } else {
                        ViewGroup group = (ViewGroup) mPlayerView.getParent();
                        if (group != null) {
                            group.removeView(mPlayerView);
                        }
                        //横视频全屏返回检测播放器比例
                        if (mMediaModel != null && mMediaModel.isNormalMedia() && !mPlayerView.isPlayerRatio_16_9()) {
                            int margin = mContext.getResources().getDimensionPixelSize(R.dimen.feed_layout_margin);
                            int width = screenWidth - margin * 2;
                            int height = width * 9 / 16;
                            ViewGroup.LayoutParams layoutParams = mPlayerView.getLayoutParams();
                            layoutParams.width = width;
                            layoutParams.height = height;
                            mPlayerView.setLayoutParams(layoutParams);
                        }
                        mPlayerView.setCanResize(false);
                        mPlayerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_FILL_SCREEN);
                        mPlayerView.showCover();
                        getPlayerViewParent().addView(mPlayerView);

                        mPlayerView.setTag(null);
                    }
                }
            }

            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                if (mMediaModel != null) {
                    if (isPlaying) {
                        mMediaModel.playState = MediaConstants.STATE_PLAY;
                        MediaPlayManager.getInstance()
                                .onPlayStart(mMediaModel.tabId, MediaBaseVideoTemplate.this);
                    } else {
                        mMediaModel.playState = MediaConstants.STATE_PAUSE;
                        long now = System.currentTimeMillis();
                        long duration = now - mPlayStart;
                        mPlayDuration += duration;
                    }
                }
            }

            @Override
            public void onBackPressed() {
                mPlayerView.handleBackPressed();
            }

            @Override
            public void onProgressChange(int playDuration, int loadingDuration, int duration) {
                mVideoTime = duration;
            }

            @Override
            public void onPendantShow(PendantModel pendantModel) {
                GVideoEventBus.get(MediaConstants.EVENT_PENDANT_SHOW, PendantModel.class).post(pendantModel);
            }

            @Override
            public void onPendantClick(PendantModel pendantModel) {
                GVideoEventBus.get(MediaConstants.EVENT_PENDANT_CLICK, PendantModel.class).post(pendantModel);
                // 监听到点击挂件事件后，会跳转到Web页面；如果当前处于横屏状态需要退出；
                BaseFragment fragment = MediaFragmentManager.getInstance().getFragment(mFragmentId);
                if (fragment == null) {
                    return;
                }
                if (!fragment.isVisible()) return;

                if (mPlayerView != null && mPlayerView.isPlaying()) {
                    mPlayerView.handleBackPressed();
                }
            }
        };
        mPlayerView.setKSVideoPlayerListener(listener);
        mPlayerView.setTitleModeInWindowMode(LiteavConstants.TITLE_MODE_NONE);
        if (topBinding != null) {
//      calculateToolBarTitleMaxWidth();
        }
    }

    /**
     * 计算设置toolbar title最大宽度
     */
    private void calculateToolBarTitleMaxWidth() {
        mAuthorNameMaxWidth =
                SizeUtils.getDensityWidth()
                        - GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.media_tool_bar_person_img_size)
                        - GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.media_tool_bar_attention_right_min_margin)
                        - GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.media_tool_bar_icon_size) * 3
                        - GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.media_tool_bar_share_more_interval)
                        - GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.media_tool_bar_chat_share_interval)
                        - GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.media_tool_bar_person_img_margin)
                        - GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.media_tool_bar_person_img_margin)
                        - GVideoRuntime.getAppContext().getResources().getDimensionPixelSize(R.dimen.media_recycler_view_landscape_margin) * 2
                        - topBinding.toolbarAttentionText.getPaint().measureText(mContext.getResources().getText(R.string.all_followed).toString());
    }

    @Override
    public void update(@NonNull MediaModel feedModel, boolean isDarkMode, String fragmentId, int position) {
        super.update(feedModel, isDarkMode, fragmentId, position);

        if (feedModel.getMediaUrls() != null && feedModel.getMediaUrls().size() > 0) {
            mPlayerView.setPreparePlayUrl(feedModel.getMediaUrls().get(0));
        }

        BaseFragment fragment = MediaFragmentManager.getInstance().getFragment(fragmentId);
        if (fragment == null) {
            return;
        }
        mFragmentId = fragmentId;
        String pid = "";
        String channelId = "";
        if (fragment instanceof MediaPageFragment) {
            channelId = ((MediaPageFragment) fragment).getChannelId();
            pid = fragment.getPid();
        }

        //设置直播控制器
        if (mMediaModel.isLiveMedia()) {
            mPlayerView.setMediaController(
                    new GVideoFeedLiveController(
                            mContext,
                            mMediaModel.getLiveBroadcastStatusStr(),
                            mMediaModel.getIsLivingTag(),
                            () -> handleNavigateToDetail(false)
                    )
            );
        } else if (mMediaModel.isVerticalMedia() && !VIDEO.equals(channelId)) {
            mPlayerView.setMediaController(
                    new GVideoFeedWBController(
                            mContext,
                            () -> handleNavigateToDetail(false)
                    )
            );
        } else {
            mPlayerView.setMediaController(
                    new GVideoFeedWBController(
                            mContext,
                            () -> handleNavigateToDetail(false)
                    )
            );
            if (!mMediaModel.showInsideVideoTitle()) {
                mPlayerView.setTitleModeInWindowMode(LiteavConstants.TITLE_MODE_NONE);
            } else {
                mPlayerView.setTitleModeInWindowMode(LiteavConstants.TITLE_MODE_ONLY_TITLE);
            }
        }

        mPlayerView.showCoverWithDefaultConner(mMediaModel.getCoverUrl());

        TagTextHelper.createTagTitle(mContext, getTitleTextView(), feedModel.getContentThanTitle(), feedModel.getTagType(),
                mContext.getResources().getColor(R.color.media_big_img_title_color));
//    if (topBinding !=null && !mMediaModel.showNewsItem) {
//      topBinding.toolbarNameText.setText(getAuthorNameText(topBinding.toolbarAttentionText, feedModel));
//    }

        mStatFromModel = new StatFromModel(feedModel.getId(), pid, channelId, "", "");
    }

    public void initLiveType(MediaModel mediaModel, LinearLayout layoutLiveTag, LiveStartTimeView liveStartTime) {
        int liveBroadcastStatus = mediaModel.getLiveBroadcastStatus();
        int liveTypeBackGround = mediaModel.getLiveTypeBackGround();
        layoutLiveTag.setBackgroundResource(liveTypeBackGround);
        switch (liveBroadcastStatus) {
            case LIVING:
            case REVIEW:
                liveStartTime.setVisibility(GONE);
                break;
            case HERALD:
                liveStartTime.setVisibility(VISIBLE);
                liveStartTime.setLongTime(mediaModel.liveBroadcastStartTime);
                break;
            default:
                layoutLiveTag.setVisibility(GONE);
                liveStartTime.setVisibility(GONE);
        }
    }

    public CharSequence getAuthorNameText(@NonNull TextView view, @NonNull MediaModel mediaModel) {
        if (mAuthorNameMaxWidth > 0) {
            return TextUtils.ellipsize(mediaModel.getAuthor().getName(),
                    view.getPaint(),
                    mAuthorNameMaxWidth,
                    TextUtils.TruncateAt.END);
        }
        return mediaModel.getAuthor().getName();
    }

    /**
     * 获取playerView父布局
     *
     * @return playerView父布局
     */
    protected abstract ViewGroup getPlayerViewParent();

    /**
     * 获取title
     *
     * @return title对应的textView
     */
    protected abstract TextView getTitleTextView();

    /**
     * 是否支持全屏
     *
     * @return 是否支持全屏
     */
    protected abstract boolean handleInterceptFullScreenEvent();

    @Override
    public MediaModel getMediaModel() {
        return super.getMediaModel();
    }

    @Override
    protected void onDetachedFromWindow() {
        mFeedVideoDataBind = null;
        if (!mIsFullScreen) {
            mPlayerView.pause();
            mPlayerView.release();
            mPlayerView.setVisibility(View.GONE);
        }
        super.onDetachedFromWindow();
        statPlay(false);
    }

    @Override
    public void play() {

        if (mIgnorePlayerAction) {
            return;
        }
        super.play();
        if (!mPlayerView.isPlaying()) {
            mPlayerView.setVisibility(VISIBLE);
            mPlayerView.initModel(mMediaModel);
            mPlayerView.startPlay(mMediaModel.getUrl(), mMediaModel);
        }
//    switch (type){
//      case MediaSoundType.MUTE:
//        LogUtils.e(getCurrentPosition()+"  播放是否有声音： "+ type+ "  | " +false);
//          mPlayerView.setMute(true);
//        break;
//        case MediaSoundType.SOUND:
//          LogUtils.e(getCurrentPosition()+"  播放是否有声音："+ type+ "  | " +true);
//          mPlayerView.setMute(false);
//          break;
//      default:
//        LogUtils.e(getCurrentPosition()+"  播放是否有声音："+ type+ "  | " +!isMute());
//        mPlayerView.setMute(isMute());
//    }
//    return isMute();
    }

    @Override
    public void resume() {
        if (mIgnorePlayerAction) {
            return;
        }
        super.resume();
        mPlayerView.resume();
    }

    @Override
    public void stop() {
        if (mIgnorePlayerAction) {
            return;
        }
        super.stop();
        mPlayerView.pause();
        mPlayerView.release();
        mPlayerView.setVisibility(View.GONE);
    }

    @Override
    public void pause() {
        if (mIgnorePlayerAction) {
            return;
        }
        super.pause();
        mPlayerView.pause();
    }

    @Override
    public void onChangeToDetail() {
        mPlayerView.save();
        //播放器在子页面复用，跳转子页面后忽略当前页面播放器相关操作
        mIgnorePlayerAction = true;
        long now = System.currentTimeMillis();
        long duration = now - mPlayStart;
        mPlayDuration += duration;
        statPlay(false);
    }

    @Override
    public void onBackFeed() {
        boolean needContinuePlay = mPlayerView.restore();
        //从子页面返回，恢复当前页播放器操作
        if (mIgnorePlayerAction) {
            mPlayStart = 0;
            if (needContinuePlay) {
                mPlayerView.setLoop(false);
                resume();
            }
            mIgnorePlayerAction = false;
        }
    }

    @Override
    public void mute(boolean value) {
        mPlayerView.setMute(value);
    }

    protected void statPlay(boolean finish) {
        String channelId = mStatFromModel != null ? mStatFromModel.channelId : "";
        if (mPlayDuration > 1000 && !TextUtils.isEmpty(channelId)) {
            String isFinish = finish ? "1" : "0";
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
    }

    /**
     * 视频开始播放时回调
     */
    protected void onVideoPlayBegin() {
        // 子类实现
    }

    /**
     * 视频结束时回调
     */
    protected void onVideoPlayEnd() {
        // 子类实现
    }

    @Override
    protected View getPlayView() {
        return playerViewLayout;
    }

}
