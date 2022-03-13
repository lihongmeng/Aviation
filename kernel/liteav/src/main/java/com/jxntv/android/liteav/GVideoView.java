package com.jxntv.android.liteav;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.danikula.videocache.common.PreloadManager;
import com.jxntv.android.liteav.controller.GVideoControllerSmall;
import com.jxntv.android.liteav.controller.VideoControllerView;
import com.jxntv.android.liteav.model.GVideoPlayerRenderMode;
import com.jxntv.android.liteav.model.GVideoPlayerRenderRotation;
import com.jxntv.android.liteav.player.GVideoPlayer;
import com.jxntv.android.liteav.player.GVideoPlayerListener;
import com.jxntv.android.liteav.player.GVideoPlayerListenerProxy;
import com.jxntv.android.liteav.view.CoverView;
import com.jxntv.android.liteav.view.PendantView;
import com.jxntv.base.Constant;
import com.jxntv.base.StaticParams;
import com.jxntv.base.model.anotation.PlayerType;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.tag.TagHelper;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.LogUtils;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.reflect.Constructor;

/**
 * 播放器入口
 */
public class GVideoView extends FrameLayout {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TAG = GVideoView.class.getSimpleName();

    // 视频控制器
    private VideoControllerView mMediaController;

    // 播放器封面
    private CoverView mCoverView;

    // 播放器挂件
    private PendantView mPendant;

    // 播放器回调代理
    private final GVideoPlayerListenerProxy mListenerProxy = new GVideoPlayerListenerProxy();

    // 播放器接口，各播放器内核实现
    private IPlayer mPlayer;

    // Context
    private Context mContext;

    // 播放器是否处于列表中
    private boolean isInRecycler = false;

    // 窗口模式的LayoutParams
    private ViewGroup.LayoutParams mLayoutParamWindowMode;

    // 全屏模式的LayoutParams
    private ViewGroup.LayoutParams mLayoutParamFullScreenMode;

    private boolean mCanResize = true;

    // 播放器是否跟随系统进行旋转
    private boolean isAutoFollowSystemRotation = false;

    // 传感器管理类
    private SensorManager sensorManager;

    // 传感器监听事件回调频繁，需要做时间控制
    private long lastChangeTime;

    /**
     * 当前屏幕默认亮度
     */
    private float currentBrightness = 160;
    /**
     * 当前页面的window窗口
     */
    private Window currentWindow;

    /**
     * 当前是否AttachedToWindow
     * 渲染完成之后，再做重力感应相关的操作更合理一些
     */
    private boolean isOnAttachStateChange = false;

    public GVideoView(Context context, @PlayerType int playerType) {
        super(context);
        init(context, playerType);
    }

    public GVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.GVideoView);
        int type = array.getInt(R.styleable.GVideoView_player_type, PlayerType.GVIDEO);
        isInRecycler = array.getBoolean(R.styleable.GVideoView_play_in_recycler, false);
        init(context, type);
        array.recycle();
    }

    private void init(Context context, @PlayerType int playerType) {
        if (context instanceof Activity) {
            currentWindow = ((Activity) context).getWindow();
            currentBrightness = currentWindow.getAttributes().screenBrightness;
        }
        mContext = context;
        setBackgroundColor(Color.BLACK);

        initCoverLayer();
        if (playerType == PlayerType.SIMPLE) {
            initPlayerLayer();
            return;
        } else {
            initPlayerLayer();
        }

        initDefaultControllerLayer();
        this.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                if (DEBUG) {
                    Log.d(TAG, "onViewAttachedToWindow - " + v);
                }
                mLayoutParamWindowMode = getLayoutParams();
                try {
                    // 依据上层Parent的LayoutParam类型来实例化一个新的fullscreen模式下的LayoutParam
                    Class parentLayoutParamClazz = getLayoutParams().getClass();
                    Constructor constructor = parentLayoutParamClazz.getDeclaredConstructor(int.class, int.class);
                    mLayoutParamFullScreenMode = (ViewGroup.LayoutParams) constructor.newInstance(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isOnAttachStateChange = true;
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (DEBUG) {
                    Log.d(TAG, "onViewDetachedFromWindow - " + v);
                }
            }
        });
        if (context instanceof AppCompatActivity) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.RESUME_VIDEO_VOLUME)
                    .observe((LifecycleOwner) context, o -> setMute(false));
        }
    }

    private void initPlayerLayer() {
        TXCloudVideoView videoView = new TXCloudVideoView(mContext);
        addView(videoView);
        mPlayer = new GVideoPlayer(mContext);
        mPlayer.setVideoPlayerView(videoView);
        addPlayerListener();
    }

    private void registerGravitySensor() {
        if (sensorManager!=null){
            return;
        }
        sensorManager = (SensorManager) GVideoRuntime.getAppContext().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            return;
        }
        lastChangeTime = 0;
        sensorManager.registerListener(
                sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    private void unRegisterGravitySensor() {
        if (sensorManager == null) {
            return;
        }
        lastChangeTime = 0;
        sensorManager.unregisterListener(sensorEventListener);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {

        @SuppressWarnings("DanglingJavadoc")
        @Override
        public void onSensorChanged(SensorEvent event) {

            // 渲染未完成，不做理会
            if (!isOnAttachStateChange) {
                return;
            }

            // 回调会非常频繁，做间隔处理
            long currentTime = System.currentTimeMillis();
            if (lastChangeTime != 0 && currentTime - lastChangeTime < 1000) {
                return;
            }
            lastChangeTime = currentTime;

            // X轴变化
            float x = event.values[0 % event.values.length];
            // Y轴变化
            float y = event.values[1 % event.values.length];
            // Z轴变化
            float z = event.values[2 % event.values.length];

            boolean fullscreen = isFullScreen();

            if (z > 8.0 || z < -8.0) {
                // Log.d("传感器监听", "=========== 平放状态 ===========");
                return;
            }

            if (x > 7.0 && -4.0 < y && y < 4.0) {
                // Log.d("传感器监听", "=========== 进入顶部在左的横屏状态 ===========");

                /**
                 * 如果当前是竖屏状态，就需要切换成横屏
                 * 但是通过{@link com.jxntv.utils.ScreenUtils#isFullScreen(Activity)}方法
                 * 判断而得的fullscreen有时不准
                 * 所以也判断当前视图高度是否MATCH_PARENT
                 * 不是的话就说明还是竖屏状态，需要切换到全屏
                 *
                 * 还要注意，手机竖着拿，点击返回按钮，应该退出全屏状态，需要根据
                 * {@link StaticParams#needTemplateLockNotFullScreen}的值
                 * 忽略本次回调
                 */
                if ((!fullscreen || getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT)
                        && !StaticParams.needTemplateLockNotFullScreen
                        && mPlayer != null
                        && mMediaController != null) {

                    // 转换播放器当前状态
                    mPlayer.toggleFullScreen();

                    // 更新播放器控制栏状态
                    mMediaController.show();
                }

                // 横屏了之后旋转手机到竖屏，就可以直接转换为竖屏状态
                // 无需理会全屏按钮的效果
                StaticParams.needTemplateLockFullScreen = false;

                return;
            }

            if (x < -7.0 && -4.0 < y && y < 4.0) {
                // Log.d("传感器监听", "=========== 进入顶部在右的横屏状态 ===========");

                /**
                 * 如果当前是竖屏状态，就需要切换成横屏
                 * 但是通过{@link com.jxntv.utils.ScreenUtils#isFullScreen(Activity)}方法
                 * 判断而得的fullscreen有时不准
                 * 所以也判断当前视图高度是否MATCH_PARENT
                 * 不是的话就说明还是竖屏状态，需要切换到全屏
                 *
                 * 还要注意，手机竖着拿，点击返回按钮，应该退出全屏状态，需要根据
                 * {@link StaticParams#needTemplateLockNotFullScreen}的值
                 * 忽略本次回调
                 */
                if ((!fullscreen || getLayoutParams().height != ViewGroup.LayoutParams.MATCH_PARENT)
                        && !StaticParams.needTemplateLockNotFullScreen
                        && mPlayer != null
                        && mMediaController != null) {

                    // 转换播放器当前状态
                    mPlayer.toggleFullScreen(true);

                    // 更新播放器控制栏状态
                    mMediaController.show();
                }

                // 横屏了之后旋转手机到竖屏，就可以直接转换为竖屏状态
                // 无需理会全屏按钮的效果
                StaticParams.needTemplateLockFullScreen = false;

                return;
            }

            if (y < -4.0) {
                // Log.d("传感器监听", "=========== 倒立状态 ===========");
                return;
            }

            // Log.d("传感器监听", "=========== 进入竖屏状态 ===========");

            /**
             * 如果当前是竖屏状态，就需要切换成横屏
             * 但是通过{@link com.jxntv.utils.ScreenUtils#isFullScreen(Activity)}方法
             * 判断而得的fullscreen有时不准
             * 所以也判断当前视图高度是否MATCH_PARENT
             * 是的话就说明还是全屏状态，需要切换到竖屏
             *
             * 并且如果是由全屏按钮引起的全屏
             * 就不需要处理
             */
            if (!StaticParams.needTemplateLockFullScreen
                    && (fullscreen || getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT)
                    && mPlayer != null
                    && mMediaController != null) {
                // 转换播放器当前状态
                mPlayer.toggleFullScreen();

                // 更新播放器控制栏状态
                mMediaController.show();
            }

            // 进入竖屏状态后
            StaticParams.needTemplateLockNotFullScreen = false;

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void addPlayerListener() {
        mListenerProxy.addListener(new GVideoPlayerListener() {
            @Override
            public void onPlayPrepared() {
                //信息流中需要隐藏封面，减少封面消失的闪屏
                //详情中视频可以不会完全遮盖封面，需要隐藏封面
                if (mCoverView != null && !isInRecycler) {
                    mCoverView.startWave(true);
                    mCoverView.hideCover();
                }
                setScreenOn();
                clearControllerState();
                if (mCanResize) {
                    int w = mPlayer.getVideoPlayerView().getWidth();
                    int h = mPlayer.getVideoPlayerView().getHeight();
                    int playerW = mPlayer.getPlayerWidth();
                    int playerH = mPlayer.getPlayerHeight();

                    if (playerH > 0 && playerW > 0 && w > 0 && h > 0) {
                        boolean playerMoreWidth = 1.0f * playerW / playerH > 1.0f * w / h;
                        boolean moreWidth = w > h;
                        //横视频窗口，保证高一致；竖视频窗口，保证宽一致；
                        if ((moreWidth && playerMoreWidth) || (!moreWidth && !playerMoreWidth)) {
                            mPlayer.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_FILL_SCREEN);
                        } else {
                            mPlayer.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_ADJUST_RESOLUTION);
                        }
                    }
                }
            }

            @Override
            public void onPlayBegin() {
                setScreenOn();
                clearControllerState();
                if (mCoverView != null && !isInRecycler) {
                    mCoverView.startWave(true);
                    mCoverView.hideCover();
                }
                postAudioEvent();
            }

            @Override
            public void onPlayEnd() {
                clearScreenOn();
                if (mMediaController != null) {
                    mMediaController.onReplayShow(true);
                    mMediaController.onPlayEnd(true);
                }
                if (mCoverView != null) {
                    mCoverView.startWave(false);
                }
            }

            @Override
            public void onFullscreenChanged(boolean fullscreen) {
                if (fullscreen) {
                    setLayoutParams(mLayoutParamFullScreenMode);
                    if (mMediaController != null) {
                        mMediaController.setTitleVisible(LiteavConstants.TITLE_MODE_FULL);
                        mMediaController.setShareVisible(LiteavConstants.TITLE_MODE_FULL);
                        mMediaController.setScreenProjectionVisible(LiteavConstants.TITLE_MODE_FULL);
                    }
                } else {
                    if (currentWindow != null) {
                        WindowManager.LayoutParams attributes = currentWindow.getAttributes();
                        attributes.screenBrightness = currentBrightness;
                        currentWindow.setAttributes(attributes);
                    }
                    setLayoutParams(mLayoutParamWindowMode);
                    if (mMediaController != null) {
                        mMediaController.setTitleVisible(mTitleModeInWindowMode);
                        mMediaController.setShareVisible(mTitleModeInWindowMode);
                        mMediaController.setScreenProjectionVisible(mTitleModeInWindowMode);
                    }
                }
            }

            @Override
            public void onControllerShow() {
                if (mCoverView != null) {
                    mCoverView.showWave(false);
                }
            }

            @Override
            public void onControllerHide() {
                if (mCoverView != null) {
                    mCoverView.showWave(true);
                }
            }

            @Override
            public void onPlayStateChanged(boolean isPlaying) {
                if (mCoverView != null) {
                    mCoverView.startWave(isPlaying);
                }
            }

            @Override
            public void onPlayLoading() {
                if (mMediaController != null && !isInRecycler) {
                    mMediaController.showTip(mContext.getString(R.string.liteav_loading), false);
                }
            }

            @Override
            public void onPlayLoadingEnd() {
                if (mMediaController != null) {
                    mMediaController.showTip("", false);
                }
            }

            @Override
            public void onErrorNetDisconnect() {
                if (mMediaController != null) {
                    mMediaController.showTip(mContext.getString(R.string.liteav_err_network), true);
                    mMediaController.onPlayEnd(true);
                }
            }

            @Override
            public void onVoiceDecodeFail() {
                if (mMediaController != null) {
                    mMediaController.showTip(mContext.getString(R.string.liteav_err_video), true);
                    mMediaController.onPlayEnd(true);
                }
            }

            @Override
            public void onAudioDecodeFail() {
                if (mMediaController != null) {
                    mMediaController.showTip(mContext.getString(R.string.liteav_err_audio), true);
                    mMediaController.onPlayEnd(true);
                }
            }

        });
        mPlayer.setKSVideoPlayerListener(mListenerProxy);
    }

    private void initCoverLayer() {
        mCoverView = new CoverView(mContext);
        addView(mCoverView);
    }

    public void initDefaultControllerLayer() {
        GVideoControllerSmall controller = new GVideoControllerSmall(mContext);
        setMediaController(controller);
        controller.hide();
    }

    private void initPendantLayer() {
        mPendant = new PendantView(mContext);
        mPendant.setPlayerListener(mListenerProxy);
        addView(mPendant);
    }

    public void setKSVideoPlayerListener(GVideoPlayerListener listener) {
        mListenerProxy.addListener(listener);
    }

    public void removeKSVideoPlayerListener(GVideoPlayerListener listener) {
        mListenerProxy.removeListener(listener);
    }

    public boolean handleBackPressed() {
        if (mPlayer.isFullScreen()) {
            StaticParams.needTemplateLockFullScreen = false;
            mPlayer.toggleFullScreen();
            return true;
        }
        return false;
    }

    /**
     * 是否是16 ：9
     */
    public boolean isPlayerRatio_16_9() {
        if (mPlayer != null && mPlayer.getPlayerHeight() > 0 && mPlayer.getPlayerWidth() > 0) {
            return (16 * 10000 / 9) == (mPlayer.getPlayerWidth() * 10000 / mPlayer.getPlayerHeight());
        }
        return false;
    }

    public boolean isFullScreen() {
        return mPlayer.isFullScreen();
    }

    /**
     * 当该View重新获得焦点时调用，用于处理多页面复用播放器时更新view
     */
    public boolean restore() {
        return mPlayer.restore();
    }

    /**
     * 保存当前播放器状态，用于多页面跳转后复用相同播放器
     */
    public void save() {
        mPlayer.save();
    }

    /**
     * 清楚当前播放器状态
     */
    public void clear() {
        mPlayer.clear();
    }


    public void setCanFullscreen(boolean canFullscreen) {
        mPlayer.setCanFullscreen(canFullscreen);
    }

    /**
     * 是否支持调整renderMode
     *
     * @param canResize
     */
    public void setCanResize(boolean canResize) {
        mCanResize = canResize;
        //if (!mCanResize) {
        //  if (mPlayer != null) {
        //    mPlayer.setRenderMode(KSPlayerRenderMode.RENDER_MODE_ADJUST_RESOLUTION);
        //  }
        //}
    }

    public void setRenderMode(GVideoPlayerRenderMode renderMode) {
        if (mPlayer != null) {
            mPlayer.setRenderMode(renderMode);
        }
    }

    public void setRenderRotation(GVideoPlayerRenderRotation renderRotation) {
        if (mPlayer != null) {
            mPlayer.setRenderRotation(renderRotation);
        }
    }


    /***
     * 设置预加载
     */
    public void setPreparePlayUrl(String url) {
        PreloadManager.getInstance(mContext).addPreloadTask(url);
        //使用播放器自带缓存功能，可能会造成播放错乱的问题，在低性能手机上容易内存溢出
//    mPlayer.setPreparePlayUrl(url);
    }

    public void startPlay(String url) {
        startPlay(url, null);
    }

    /**
     * 播放
     *
     * @param url        链接
     * @param videoModel 埋点数据
     */
    public void startPlay(String url, VideoModel videoModel) {
        try {
            if (isAutoFollowSystemRotation){
                registerGravitySensor();
            }
            url = PreloadManager.getInstance(getContext()).getPlayUrl(url);
            mPlayer.setStatData(videoModel);
            mPlayer.startPlay(url);
            StaticParams.needTemplateLockFullScreen = false;
        } catch (Throwable e) {
            LogUtils.e("startPlayPlatformError", e.getMessage());
        }
        clearControllerState();
        mListenerProxy.onPlayLoading();
        setAudioManagerLister();
    }

    public void release() {
        unRegisterGravitySensor();
        mPlayer.release();
        cancelAudioManagerLister();
        if (mCoverView != null) {
            mCoverView.release();
        }
        if (mPendant != null) {
            mPendant.stop();
        }
    }

    public void resume() {
        mPlayer.resume();
    }

    public void pause() {
        if (getMediaController() != null) {
            getMediaController().changePausePlay();
        }
        mPlayer.pause();
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public void setLoop(boolean loop) {
        mPlayer.setLoop(loop);
    }

    public void seek(int pos) {
        mPlayer.seekTo(pos);
    }

    public void setMute(boolean mute) {
        mPlayer.setMute(mute);
    }

    public void showController() {
        if (mMediaController != null) {
            mMediaController.setMediaPlayer(mPlayer);
            mMediaController.setEnabled(true);
            mMediaController.show();
        }
    }

    private @LiteavConstants.TitleMode
    int mTitleModeInWindowMode;

    public void setTitleModeInWindowMode(@LiteavConstants.TitleMode int mode) {
        mTitleModeInWindowMode = mode;
        if (mMediaController != null) {
            mMediaController.setTitleVisible(mode);
        }
    }

    public void updateTitle(String title, @TagHelper.GvideoTagType int tag) {
        if (mMediaController != null) {
            mMediaController.updateTitle(title != null ? title : "", tag);
        }
    }

    public void setMediaController(VideoControllerView controller) {
        if (mMediaController != null) {
            mMediaController.requestRemove();
        }
        mMediaController = controller;
        attachMediaController();
    }

    public VideoControllerView getMediaController() {
        return mMediaController;
    }

    private void attachMediaController() {
        if (mPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(mPlayer);
            //View anchorView = this.getParent() instanceof View ?
            //    (View)this.getParent() : this;
            mMediaController.setAnchorView(this);
            mMediaController.setEnabled(true);
        }
    }

    /**
     * 清除Controller状态时使用，如重播、loading等恢复到缺省状态；
     */
    private void clearControllerState() {
        if (mMediaController != null) {
            mMediaController.onReplayShow(false);
            mMediaController.onPlayEnd(false);
            mMediaController.showTip("", false);
        }
    }

    /**
     * 设置音频样式封面
     *
     * @param coverUrl 左侧封面背景url
     * @param cover    与TagType联动，区分Music 与 FM样式
     */
    public void setSoundCover(String coverUrl, @CoverView.SOUND_TYPE int cover) {
        if (cover == CoverView.SOUND_FM || cover == CoverView.SOUND_MUSIC) {
            mCanResize = false;
        }
        if (mCoverView != null) {
            mCoverView.setSoundCover(coverUrl, cover);
        }
    }

    public void showCover(Drawable cover) {
        if (mCoverView != null) {
            mCoverView.showCover(cover);
        }
    }

    public void showCover(String coverUrl) {
        if (mCoverView != null) {
            mCoverView.showCover(coverUrl);
        }
    }

    public void showCover(String coverUrl, boolean centerCrop) {
        if (mCoverView != null) {
            mCoverView.showCover(coverUrl, centerCrop);
        }
    }

    public void showCover() {
        if (mCoverView != null) {
            mCoverView.setVisibility(VISIBLE);
        }
    }

    public void hideCover() {
        if (mCoverView != null) {
            mCoverView.setVisibility(GONE);
        }
    }

    public void showCoverWithDefaultConner(String coverUrl) {
        if (mCoverView != null) {
            mCoverView.showCoverWithDefaultConner(coverUrl);
        }
    }

    public void initModel(VideoModel videoModel) {
        if (videoModel == null) {
            return;
        }
        if (mPendant == null) {
            initPendantLayer();
        }
        if (mPendant != null) {
            mPendant.initPendant(videoModel.getPendant());
        }
        updateTitle(videoModel.getContentThanTitle(), videoModel.getTagType());
    }

    public void showPendantByModel() {
        if (mPendant != null) {
            mPendant.start();
        }
    }

    public void setIsRecycler() {
        this.isInRecycler = true;
    }

    /**
     * 设置自动跟随系统旋转视频
     */
    public void setAutoFollowSystemRotation(boolean isAutoRotation){
        this.isAutoFollowSystemRotation = isAutoRotation;
    }

    /**
     * 设置常亮
     */
    private void setScreenOn() {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void clearScreenOn() {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    //------------ 设置音频直播资源监听  -------------
    private boolean isAudioService = false;

    public void setIsAudioService() {
        isAudioService = true;
    }

    Observer<Object> observer = o -> setMute(true);

    private void postAudioEvent() {
        if (!isAudioService) {
            GVideoEventBus.get(Constant.EVENT_MSG.AUDIO_BG_NEED_PAUSE).post(null);
        }
    }

    /**
     * 设置音频监听
     */
    private void setAudioManagerLister() {
        if (!isAudioService) {
            GVideoEventBus.get(Constant.EVENT_MSG.VIDEO_NEED_PAUSE).observeForever(observer);
        }
    }

    private void cancelAudioManagerLister() {
        if (!isAudioService) {
            GVideoEventBus.get(Constant.EVENT_MSG.VIDEO_NEED_PAUSE).removeObserver(observer);
        }
    }

    public void hideScreenProjectionLayout(){
        if(mMediaController==null){
            return;
        }
        mMediaController.hideScreenProjectionLayout();
    }

    public int getDuration(){
        return mPlayer == null ? -1 : mPlayer.getDuration();
    }

    public String getPlayerProcess(){
        return mPlayer == null ? "0" : mPlayer.getPlayProgress() + "";
    }

}
