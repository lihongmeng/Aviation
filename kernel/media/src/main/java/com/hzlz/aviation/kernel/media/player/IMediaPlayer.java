package com.hzlz.aviation.kernel.media.player;

import com.hzlz.aviation.kernel.media.model.MediaModel;

/**
 * 媒体播放器接口类
 */
public interface IMediaPlayer {

    /**
     * 播放
     */
    void play();

    int getCurrentPosition();

    /**
     * 播放
     */
    void resume();

    /**
     * 停止
     */
    void stop();

    /**
     * 暂停
     */
    void pause();

    /**
     * 是否正在播放
     *
     * @return 是否正在播放
     */
    boolean isPlaying();

    /**
     * 是否支持自动播放
     *
     * @return 是否支持自动播放
     */
    boolean isSupportAutoPlay();

    /**
     * 是否处于播放区域中
     *
     * @return 是否处于播放区域
     */
    boolean isInPlayScale();

    /**
     * 获取position
     *
     * @return 当前的position
     */
    int getPosition();

    /**
     * 获取对应的feed model
     *
     * @return feed model
     */
    MediaModel getPlayerMediaModel();

    /**
     * 跳转至详情页
     */
    void onChangeToDetail();

    /**
     * 返回feed页
     */
    void onBackFeed();

    /**
     * 静音、恢复声音
     *
     * @param value true 静音
     *              false 恢复声音
     */
    void mute(boolean value);

}
