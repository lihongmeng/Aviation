package com.jxntv.android.liteav.player;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.jxntv.android.liteav.BuildConfig;
import com.jxntv.android.liteav.IPlayer;
import com.jxntv.android.liteav.controller.IMediaPlayerControl;
import com.jxntv.android.liteav.model.GVideoPlayerRenderMode;
import com.jxntv.android.liteav.model.GVideoPlayerRenderRotation;
import com.jxntv.base.StaticParams;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.utils.LogUtils;
import com.jxntv.utils.ScreenUtils;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * 腾讯云播放器内核对接类
 */
public class GVideoPlayer implements IMediaPlayerControl, IPlayer {

    private static final boolean DEBUG = BuildConfig.DEBUG && false;

    private static final String TAG = GVideoPlayer.class.getSimpleName();

    // Context
    private final Context context;

    // UI层创建TXCloudVideoView并添加到视图树
    // 传递进来和播放器进行绑定
    private TXCloudVideoView mTXCloudVideoView;

    // 是否能全屏
    private boolean mCanFullscreen = true;

    // 当前腾讯云播放器
    private VodPlayer mCurrentPlayer;

    // 当前播放视频的URL
    private String mCurrentUrl;

    // 埋点需要使用的视频相关数据
    private VideoModel videoModel;

    // 埋点需要使用的进度数据
    private long playProgress;

    // 是否续播
    private boolean isContinuePlay = false;

    // 向外层暴露的接口
    private GVideoPlayerListener mKSVideoPlayerListener;

    // 播放器铺满或者适应相关的参数
    private GVideoPlayerRenderMode mGVideoPlayerRenderMode = GVideoPlayerRenderMode.RENDER_MODE_ADJUST_RESOLUTION;

    // 播放器画面旋转相关的参数
    private GVideoPlayerRenderRotation mGVideoPlayerRenderRotation = GVideoPlayerRenderRotation.RENDER_ROTATION_PORTRAIT;

    @Override
    public void onShow() {
        if (mKSVideoPlayerListener != null) {
            mKSVideoPlayerListener.onControllerShow();
        }
    }

    @Override
    public void onHide() {
        if (mKSVideoPlayerListener != null) {
            mKSVideoPlayerListener.onControllerHide();
        }
    }

    @Override
    public void onBackPressed() {
        if (mKSVideoPlayerListener != null) {

            // 1、手机是竖着拿，视频是全屏播放，点击返回按钮，应该退出全屏，忽略传感器回调
            // 2、视频是非全屏播放，不管手机怎么拿，点击返回按钮就需要退出页面

            // 点击返回按钮而记录的值，考虑是退出全屏还是退出页面
            // 退出全屏就标记true，忽略传感器回调，退出页面就直接重置
            StaticParams.needTemplateLockNotFullScreen = isFullScreen();
            // 点击全屏按钮而记录的值直接重置为false，
            // 情况1，手机横着拿的时候不需要理会该值
            // 情况2，页面已经销毁，也可以直接重置该值
            StaticParams.needTemplateLockFullScreen = false;

            mKSVideoPlayerListener.onBackPressed();
        }
    }

    @Override
    public void onShareClick(View view) {
        if (mKSVideoPlayerListener != null) {
            mKSVideoPlayerListener.onShareClick(view);
        }
    }

    @Override
    public void onScreenProjection(View view) {
        if (mKSVideoPlayerListener != null) {
            mKSVideoPlayerListener.onScreenProjection(view);
        }
    }

    @Override
    public void onEndScreenProjection(View view){
        if (mKSVideoPlayerListener != null) {
            mKSVideoPlayerListener.onEndScreenProjection(view);
        }
    }

    @Override
    public void onRetryClicked() {
        if (mCurrentPlayer != null && !TextUtils.isEmpty(mCurrentUrl)) {
            mCurrentPlayer.startPlay(mCurrentUrl);
        }
    }

    @Override
    public boolean canFullscreen() {
        //音频等场景不支持全屏按钮
        return mCanFullscreen;
    }

    @Override
    public boolean isFullScreen() {
        return ScreenUtils.isLandscape(context);
    }

    @Override
    public void toggleFullScreen() {
        boolean fullscreen = isFullScreen();
        if (mKSVideoPlayerListener != null) {
            if (mKSVideoPlayerListener.interceptFullScreenEvent()) {
                return;
            }
            mKSVideoPlayerListener.onFullscreenChanged(!fullscreen);
        }
        if (fullscreen) {
            ScreenUtils.setPortrait((Activity) context);
            ScreenUtils.setNonFullScreen((Activity) context);
        } else {
            ScreenUtils.setSensorLandscape((Activity) context);
            ScreenUtils.setFullScreen((Activity) context);
        }
    }

    @Override
    public void toggleFullScreen(boolean needReverse) {
        boolean fullscreen = isFullScreen();
        if (mKSVideoPlayerListener != null) {
            if (mKSVideoPlayerListener.interceptFullScreenEvent()) {
                return;
            }
            mKSVideoPlayerListener.onFullscreenChanged(!fullscreen);
        }
        if (fullscreen) {
            ScreenUtils.setPortrait((Activity) context);
            ScreenUtils.setNonFullScreen((Activity) context);
        } else {
            if (needReverse) {
                ScreenUtils.setReverseLandscape((Activity) context);
            } else {
                ScreenUtils.setSensorLandscape((Activity) context);
            }
            ScreenUtils.setFullScreen((Activity) context);
        }
    }

    @Override
    public void start() {
        resume();
    }


    @Override
    public int getDuration() {
        if (mCurrentPlayer != null) {
            return (int) mCurrentPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mCurrentPlayer != null) {
            return (int) mCurrentPlayer.getCurrentPlaybackTime();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (mCurrentPlayer != null) {
            mCurrentPlayer.seek(pos);
        }
    }

    @Override
    public boolean isPlaying() {
        if (mCurrentPlayer != null) {
            return mCurrentPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        if (mCurrentPlayer != null) {
            return (int) (100f * mCurrentPlayer.getBufferDuration() / mCurrentPlayer.getDuration());
        }
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public GVideoPlayer(Context context) {
        this.context = context;
    }

    @Override
    public void setVideoPlayerView(ViewGroup containerView) {
        if (containerView instanceof TXCloudVideoView) {
            mTXCloudVideoView = (TXCloudVideoView) containerView;
        }
    }

    @Override
    public View getVideoPlayerView() {
        return mTXCloudVideoView;
    }

    public void setKSVideoPlayerListener(GVideoPlayerListener ksVideoPlayerListener) {
        mKSVideoPlayerListener = ksVideoPlayerListener;
    }

    public void setCanFullscreen(boolean canFullscreen) {
        mCanFullscreen = canFullscreen;
    }

    public void setLoop(boolean loop) {
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setLoop(loop);
        }
    }

    public void setMute(boolean mute) {
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setMute(mute);
        }
    }

    @Override
    public void setPreparePlayUrl(String url) {
        if (mCurrentPlayer == null) {
            mCurrentPlayer = createVodPlayer();
        }
        //本地未设置播放地址，使用url进行预加载
        if (TextUtils.isEmpty(mCurrentUrl) && !TextUtils.isEmpty(url)) {
            //设置不自动播放会进行预加载
            mCurrentPlayer.setAutoPlay(false);
            mCurrentPlayer.startPlay(url);
            //设置回收状态，确保视频未播放时进入相应详情页，返回不会进行续播
            mCurrentPlayer.release();
            mCurrentUrl = url;
        }
    }

    /**
     * 返回列表进行续播
     */
    @Override
    public boolean restore() {
        if (mCurrentPlayer != null && !mCurrentPlayer.isReleased()) {
            isContinuePlay = true;
            mCurrentPlayer.pause();
            mCurrentPlayer.setPlayerView(mTXCloudVideoView);
            mCurrentPlayer.setVodListener(mITXVodPlayListener);
            mCurrentPlayer.setRenderMode(mGVideoPlayerRenderMode.mRenderMode);
            mCurrentPlayer.resume();
            return true;
        }
        return false;
    }

    @Override
    public void save() {
        PlayerViewHolder.getInstance().save(mCurrentPlayer);
    }

    @Override
    public void clear() {
        PlayerViewHolder.getInstance().clear();
    }

    @Override
    public void startPlay(String currentUrl) {
        VodPlayer player = PlayerViewHolder.getInstance().get();
        PlayerViewHolder.getInstance().save(null);
        if (player != null && !player.isReleased()) {
            if (TextUtils.equals(currentUrl, player.getPlayUrl())) {
                isContinuePlay = true;
                mCurrentPlayer = player;
                mCurrentPlayer.pause();
                mCurrentPlayer.setPlayerView(mTXCloudVideoView); //保留md5 作为页面识别，
                mCurrentPlayer.setVodListener(mITXVodPlayListener);
                mCurrentPlayer.setRenderMode(mGVideoPlayerRenderMode.mRenderMode);
                mCurrentPlayer.resume();
                mCurrentUrl = currentUrl;
                return;
            } else {
                player.pause();
            }
        }
        isContinuePlay = false;

        String currentPlayUrl = mCurrentPlayer == null ? "" : mCurrentPlayer.getPlayUrl();
        currentPlayUrl = TextUtils.isEmpty(currentPlayUrl) ? "" : currentPlayUrl;

        if (mCurrentPlayer != null
                && !TextUtils.isEmpty(currentPlayUrl)
                && currentPlayUrl.equals(currentUrl)
                && !TextUtils.isEmpty(mCurrentUrl) && mCurrentUrl.equals(currentUrl)) {
            mCurrentPlayer.setPlayerView(mTXCloudVideoView);
            mCurrentPlayer.setVodListener(mITXVodPlayListener);
            // 如果有设置预加载直接进行播放
            mCurrentPlayer.resume();
        } else {
            mCurrentPlayer = createVodPlayer();
            mCurrentPlayer.setPlayerView(mTXCloudVideoView);
            mCurrentPlayer.setVodListener(mITXVodPlayListener);
            mCurrentPlayer.setAutoPlay(true);
            mCurrentPlayer.startPlay(currentUrl);
            mCurrentUrl = currentUrl;
        }
    }

    public void release() {
        if (mCurrentPlayer != null && selfPlayer()) {
            mCurrentPlayer.stopPlay(true); // true 代表清除最后一帧画面
            if (mKSVideoPlayerListener != null) {
                videoPlayStat(false);
            }
            mCurrentPlayer.setVodListener(null);
            mCurrentPlayer.release();
            mCurrentUrl = "";
            videoModel = null;
        }
        // 结束播放时 记得销毁 view 控件 ，尤其是在下次 startPlay 之前，否则会产生大量的内存泄露以及闪屏问题
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
        }
    }

    public void resume() {
        if (mCurrentPlayer != null && selfPlayer()) {
            if (!isPlaying()) {
                mCurrentPlayer.resume();
                if (mKSVideoPlayerListener != null) {
                    mKSVideoPlayerListener.onPlayStateChanged(true);
                }
            }
        }
    }

    @Override
    public void pause() {
        if (mCurrentPlayer != null && selfPlayer()) {
            if (isPlaying()) {
                mCurrentPlayer.pause();
                if (mKSVideoPlayerListener != null) {
                    mKSVideoPlayerListener.onPlayStateChanged(false);
                }
            }
        }
    }

    private boolean selfPlayer() {
        return mCurrentPlayer == null
                || mCurrentPlayer.getCloudVideoView() == mTXCloudVideoView;
    }

    /**
     * 设置屏幕适配方式
     */
    public void setRenderMode(GVideoPlayerRenderMode renderMode) {
        mGVideoPlayerRenderMode = renderMode;
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setRenderMode(mGVideoPlayerRenderMode.mRenderMode);
        }
    }

    /**
     * 设置横竖屏
     */
    public void setRenderRotation(GVideoPlayerRenderRotation renderRotation) {
        mGVideoPlayerRenderRotation = renderRotation;
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setRenderRotation(mGVideoPlayerRenderRotation.mRenderRotation);
        }
    }

    /**
     * 变速播放
     */
    public void setPlayerRate(float rate) {
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setRate(rate);
        }
    }

    /**
     * 设置采用硬解
     */
    public void setHardwareDecode(boolean isHard) {
        if (mCurrentPlayer != null) {
            mCurrentPlayer.stopPlay(isHard);
            mCurrentPlayer.enableHardwareDecode(isHard);
            mCurrentPlayer.startPlay(mCurrentUrl);
        }
    }

    /**
     * 屏幕截图
     */
    public Bitmap snapshotPlayer() {
        final Bitmap[] snapshotBitmap = {null};
        if (mCurrentPlayer != null) {
            mCurrentPlayer.snapshot(
                    bitmap -> {
                        if (null != bitmap) {
                            // 获取到截图bitmap
                            snapshotBitmap[0] = bitmap;
                        }
                    });
        }
        return snapshotBitmap[0];
    }

    /**
     * 获取宽度
     */
    public int getPlayerWidth() {
        if (mCurrentPlayer != null) {
            return mCurrentPlayer.getWidth();
        }
        return 0;
    }

    /**
     * 获取高度
     */
    public int getPlayerHeight() {
        if (mCurrentPlayer != null) {
            return mCurrentPlayer.getHeight();
        }
        return 0;
    }

    @Override
    public void setStatData(VideoModel videoModel) {
        this.videoModel = videoModel;
    }

    private VodPlayer createVodPlayer() {
        TXVodPlayConfig config = new TXVodPlayConfig();
        //最大缓存50个文件
//        config.setMaxCacheItems(50);
//        config.setCacheFolderPath(StorageUtils.getCacheDirectory().getAbsolutePath());

        config.setSmoothSwitchBitrate(true);
        VodPlayer vodPlayer = new VodPlayer(context);
        vodPlayer.setConfig(config);
        vodPlayer.setRenderMode(mGVideoPlayerRenderMode.mRenderMode);
        vodPlayer.setRenderRotation(mGVideoPlayerRenderRotation.mRenderRotation);
        return vodPlayer;
    }

    private final ITXVodPlayListener mITXVodPlayListener = new ITXVodPlayListener() {
        @Override
        public void onPlayEvent(TXVodPlayer txVodPlayer, int event, Bundle param) {

            if (event != TXLiveConstants.PLAY_EVT_PLAY_PROGRESS && DEBUG) {
                Log.d(TAG, "播放器onPlayEvent事件 -->>"
                        + event
                        + ", "
                        + param.getString(TXLiveConstants.EVT_DESCRIPTION)
                );
            }

            switch (event) {

                // 播放器已准备完成，可以播放
                // 设置了 autoPlay 为 false 之后
                // 需要在收到此事件后，调用 resume 才会开始播放
                case TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED:
                    LogUtils.d("================= 播放器已准备完成，可以播放 =================");
                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_EVT_VOD_PLAY_PREPARED");
                        }
                        mKSVideoPlayerListener.onPlayPrepared();
                    }
                    break;

                // 视频播放开始
                case TXLiveConstants.PLAY_EVT_PLAY_BEGIN:
                    LogUtils.d("================= 视频播放开始 =================");
                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_EVT_PLAY_BEGIN");
                        }
                        videoPlayStat(true);
                        mKSVideoPlayerListener.onPlayBegin();
                    }
                    break;

                // 视频播放进度（点播专用）
                case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_EVT_PLAY_PROGRESS");
                        }

                        int durationMs = param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS);
                        if (durationMs > 0) {
                            playProgress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS) * 100L / durationMs;
                        }
                        mKSVideoPlayerListener.onProgressChange(
                                param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS),
                                param.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS),
                                param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS));
                    }
                    break;

                // 网络断连，且连续三次无法重新连接，需要自行重启推流
                case TXLiveConstants.PLAY_ERR_NET_DISCONNECT:
                    LogUtils.d("================= 网络断连，且连续三次无法重新连接，需要自行重启推流 =================");
                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_ERR_NET_DISCONNECT");
                        }
                        videoPlayErrorStat("网络断连");
                        GVideoSensorDataManager.getInstance().videoPlayFail(videoModel, "网络断连");
                        mKSVideoPlayerListener.onErrorNetDisconnect();
                    }
                    break;
                // 视频播放结束
                case TXLiveConstants.PLAY_EVT_PLAY_END:
                    LogUtils.d("================= 视频播放结束 =================");
                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_EVT_PLAY_END");
                        }
                        videoPlayStat(false);
                        mKSVideoPlayerListener.onPlayEnd();
                    }
                    break;

                // 播放文件不存在
                case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND:
                    LogUtils.d("================= 播放文件不存在 =================");
                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_ERR_FILE_NOT_FOUND");
                        }
                        videoPlayErrorStat("未找到播放文件");
                        GVideoSensorDataManager.getInstance().videoPlayFail(videoModel, "未找到播放文件");
                        mKSVideoPlayerListener.onFileNotFound();
                    }
                    break;

                // 视频播放加载中
                case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                    LogUtils.d("================= 视频播放加载中 =================");
                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_EVT_PLAY_LOADING");
                        }
                        mKSVideoPlayerListener.onPlayLoading();
                    }
                    break;

                // 渲染首个视频数据包（IDR）
                case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                    LogUtils.d("================= 渲染首个视频数据包（IDR） =================");
                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_EVT_RCV_FIRST_I_FRAME");
                        }
                        mKSVideoPlayerListener.onReceiveFirstFrame();
                    }
                    break;

                // 视频分辨率改变
                case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION:
                    LogUtils.d("================= 视频分辨率改变 =================");
                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_EVT_CHANGE_RESOLUTION");
                        }
                        mKSVideoPlayerListener.onChangeResolution();
                    }
                    break;

                // HLS 解码 key 获取失败
                case TXLiveConstants.PLAY_ERR_HLS_KEY:
                    LogUtils.d("================= HLS 解码 key 获取失败 =================");
                    if (DEBUG) {
                        Log.d(TAG, "receive event: " + event + ", PLAY_ERR_HLS_KEY");
                    }
                    break;

                // 网络断连，已启动自动重连恢复
                // 重连超过三次就直接抛送 PLAY_ERR_NET_DISCONNECT
                case TXLiveConstants.PLAY_WARNING_RECONNECT:
                    LogUtils.d("================= 网络断连，已启动自动重连恢复,重连超过三次就直接抛送 PLAY_ERR_NET_DISCONNECT =================");

                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_WARNING_RECONNECT");
                        }
                        mKSVideoPlayerListener.onReconnect();
                    }
                    break;

                // MP4视频旋转角度
                case TXLiveConstants.PLAY_EVT_CHANGE_ROTATION:
                    LogUtils.d("================= MP4视频旋转角度 =================");

                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_EVT_CHANGE_ROTATION");
                        }
                        mKSVideoPlayerListener.onMp4ChangeRoation();
                    }
                    break;

                // 加载结束
                case TXLiveConstants.PLAY_EVT_VOD_LOADING_END:
                    LogUtils.d("================= 加载结束 =================");

                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_EVT_VOD_LOADING_END");
                        }
                        mKSVideoPlayerListener.onPlayLoadingEnd();
                    }
                    break;

                // 当前视频帧解码失败
                case TXLiveConstants.PLAY_WARNING_VIDEO_DECODE_FAIL:
                    LogUtils.d("================= 当前视频帧解码失败 =================");

                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_WARNING_VIDEO_DECODE_FAIL");
                        }
                        videoPlayErrorStat("当前视频帧解码失败");
                        mKSVideoPlayerListener.onVoiceDecodeFail();
                        GVideoSensorDataManager.getInstance().videoPlayFail(videoModel, "当前视频帧解码失败");
                    }
                    break;

                // 当前音频帧解码失败
                case TXLiveConstants.PLAY_WARNING_AUDIO_DECODE_FAIL:
                    LogUtils.d("================= 当前音频帧解码失败 =================");

                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_WARNING_VIDEO_DECODE_FAIL");
                        }
                        videoPlayErrorStat("当前音频帧解码失败");
                        GVideoSensorDataManager.getInstance().videoPlayFail(videoModel, "当前音频帧解码失败");
                        mKSVideoPlayerListener.onAudioDecodeFail();
                    }
                    break;

                // 当前视频播放出现卡顿
                case TXLiveConstants.PLAY_WARNING_VIDEO_PLAY_LAG:
                    LogUtils.d("================= 当前视频播放出现卡顿 =================");

                    if (mKSVideoPlayerListener != null) {
                        if (DEBUG) {
                            Log.d(TAG, "receive event: " + event + ", PLAY_WARNING_VIDEO_PLAY_LAG");
                        }
                        GVideoSensorDataManager.getInstance().videoPlayFail(videoModel, "视频播放卡顿");
                        mKSVideoPlayerListener.onPlayStuck();
                    }
                    break;
            }

        }

        @Override
        public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {
            if (DEBUG) {
                Log.d(TAG, "receive event: " + ", NET_STATUS_CPU_USAGE: "
                        + bundle.get(TXLiveConstants.NET_STATUS_CPU_USAGE));
            }
        }
    };

    private void videoPlayStat(boolean isStart) {
        try {
            //当时在播放整期节目
            if (videoModel != null && !TextUtils.isEmpty(videoModel.getColumnId()) && Long.parseLong(videoModel.getColumnId()) > 0
                                && videoModel.getStatFromModel()!=null && !TextUtils.isEmpty(videoModel.getStatFromModel().pid)){
                if (isStart) {
                    GVideoSensorDataManager.getInstance().startProgramPlay(videoModel, System.currentTimeMillis() +"",
                                    videoModel.getStatFromModel().pid, videoModel.getStatFromModel().fromPid, isContinuePlay,
                                    getDuration());
                }
            }else {
                if (videoModel != null) {
                    videoModel.totalPlayDuration = getDuration();
                }
                if (isStart){
                    playProgress = -1;
                }
                GVideoSensorDataManager.getInstance().videoPlay(videoModel, playProgress);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void videoPlayErrorStat(String error) {
        if (videoModel != null) {
            videoModel.totalPlayDuration = getDuration();
        }
        GVideoSensorDataManager.getInstance().videoPlayError(videoModel, playProgress, error);
    }

    public long getPlayProgress(){
        return playProgress;
    }

}
