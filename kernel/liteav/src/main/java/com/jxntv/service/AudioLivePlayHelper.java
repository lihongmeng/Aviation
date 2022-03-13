package com.jxntv.service;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jxntv.base.model.circle.CircleDetail;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.AppManager;
import com.youth.banner.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangwei
 * date : 2021/9/26
 * desc : 音频直播工具
 **/
public class AudioLivePlayHelper {

    public final static String ACTION_PLAY = "com.jxntv.aduiolive.ACTION_OPT_PLAY";
    public final static String ACTION_PAUSE = "com.jxntv.aduiolive.ACTION_OPT_PAUSE";
    public final static String ACTION_END = "com.jxntv.aduiolive.ACTION_OPT_END";
    public final static String ACTION_INTENT = "com.jxntv.aduiolive.ACTION_OPT_INTENT";
    public final static String ACTION_SET_DATA = "com.jxntv.aduiolive.AUDIO_SET_DATA";

    private static AudioLivePlayHelper playHelper;
    private List<PlayListener> playListeners = new ArrayList<>();
    private CircleDetail circleDetail;
    private AudioLivePlayReceiver mIntentReceiver;
    private boolean isPlaying;

    public static AudioLivePlayHelper getInstance(){
        if (playHelper==null){
            playHelper = new AudioLivePlayHelper();
            playHelper.registerReceiver();
        }
        return playHelper;
    }


    /**
     * 设置音频播放数据
     * @param circleDetail 圈子详情数据
     */
    public void setPlayData(CircleDetail circleDetail){
        if (isCurrentCircleData(circleDetail)){
            play();
        }else {
            this.circleDetail = circleDetail;
            AudioLivePlayService.startService(circleDetail);
            sendBroadcast(ACTION_SET_DATA);
        }
    }

    public void play(){
        sendBroadcast(ACTION_PLAY);
    }

    public void pause(){
        sendBroadcast(ACTION_PAUSE);
    }

    public void close(){
        circleDetail = null;
        isPlaying = false;
        sendBroadcast(ACTION_END);
    }
    private void sendBroadcast(String action){
        Activity activity = AppManager.getAppManager().currentActivity();
        if (activity!=null){
            activity.sendBroadcast(new Intent(action));
        }
    }

    public boolean isRelease(){
        return circleDetail == null;
    }

    public void closeByData(CircleDetail circleDetail){
        if (isCurrentCircleData(circleDetail)){
            close();
        }
    }

    /**
     * 当前圈子是否正在播放音频
     * @return
     */
    public boolean isCurrentCirclePlaying(CircleDetail circleDetail){
        return isPlaying() && isCurrentCircleData(circleDetail);
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public CircleDetail getCurrentPlayCircleData(){
        return circleDetail;
    }

    /**
     * 正在播放的音频是否是当前数据
     */
    private boolean isCurrentCircleData(CircleDetail circleDetail){
        return circleDetail!=null && this.circleDetail!=null
                && circleDetail.groupId == this.circleDetail.groupId;
    }


    /**
     * 添加播放监听
     */
    public void setPlayListener(PlayListener listener){
        if (playListeners==null){
            playListeners = new ArrayList<>();
        }
        if (listener!=null && !playListeners.contains(listener)){
            playListeners.add(listener);
        }
    }

    private void registerReceiver(){
        //设置广播获取播放状态，用于更新控件
        if (mIntentReceiver==null) {
            mIntentReceiver = new AudioLivePlayReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (intent.getAction()) {
                        case ACTION_SET_DATA:
                            isPlaying = true;
                            if (circleDetail != null) {
                                for (int i = 0; i < playListeners.size(); i++) {
                                    playListeners.get(i).setPlayData(circleDetail);
                                }
                            }
                            break;
                        case ACTION_PLAY:
                            isPlaying = true;
                            for (int i = 0; i<playListeners.size();i++){
                                playListeners.get(i).play();
                            }
                            break;
                        case ACTION_PAUSE:
                            for (int i = 0; i<playListeners.size();i++){
                                playListeners.get(i).pause();
                            }
                            isPlaying = false;
                            break;
                        case ACTION_END:
                            for (int i = 0; i<playListeners.size();i++){
                                playListeners.get(i).end();
                            }
                            isPlaying = false;
                            circleDetail = null;
                            break;
                    }
                }
            };
            final IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_PLAY);
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_INTENT);
            filter.addAction(ACTION_END);
            filter.addAction(ACTION_SET_DATA);
            GVideoRuntime.getAppContext().registerReceiver(mIntentReceiver, filter);
        }
    }


    /**
     * 移除播放监听
     */
    public void removePlayListener(PlayListener listener){
        if (playListeners!=null && listener!=null){
            playListeners.remove(listener);
        }
    }


    public interface PlayListener{
        void setPlayData(CircleDetail circleDetail);
        void play();
        void pause();
        void end();
    }
}
