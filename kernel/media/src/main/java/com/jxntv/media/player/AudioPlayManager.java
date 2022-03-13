package com.jxntv.media.player;

import androidx.lifecycle.Observer;

import com.jxntv.android.liteav.GVideoSoundView;
import com.jxntv.base.Constant;
import com.jxntv.event.GVideoEventBus;

/**
 * @author huangwei
 * date : 2021/4/20
 * desc : 详情页语音播放管理
 **/
public class AudioPlayManager {

    /**
     * 持有的单例
     */
    private volatile static AudioPlayManager sInstance = null;

    private GVideoSoundView currentSoundView = null;

    /**
     * 构造函数
     */
    private AudioPlayManager() {

    }

    /**
     * 构造函数
     */
    public static AudioPlayManager getInstance() {
        if (sInstance == null) {
            synchronized (AudioPlayManager.class) {
                if (sInstance == null) {
                    sInstance = new AudioPlayManager();
                    sInstance.setEventbus();
                }
            }
        }
        return sInstance;
    }

    /**
     * 播放
     *
     * @param soundView 播放器
     */
    public void start(GVideoSoundView soundView) {
        if (currentSoundView != null) {
            currentSoundView.stop();
            currentSoundView = null;
        }
        currentSoundView = soundView;
        currentSoundView.start();
    }

    public void resume() {
        if (currentSoundView != null) {
            currentSoundView.resume();
        }
    }

    public void pause() {
        if (currentSoundView != null) {
            currentSoundView.pause();
        }
    }

    /**
     * 释放本地资源
     */
    public void release() {
        if (currentSoundView != null) {
            currentSoundView.stop();
            currentSoundView = null;
        }

        if (sInstance != null) {
            sInstance.realRelease();
        }
    }


    /**
     * 释放本地资源
     */
    private void realRelease() {
        sInstance = null;
        if (currentSoundView != null) {
            currentSoundView.stop();
            currentSoundView = null;
        }
    }

    private void setEventbus(){
        GVideoEventBus.get(Constant.EVENT_MSG.VIDEO_NEED_PAUSE).observeForever(o -> {
            release();
        });
    }
}
