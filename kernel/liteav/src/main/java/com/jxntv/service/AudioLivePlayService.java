package com.jxntv.service;


import static com.jxntv.service.AudioLivePlayHelper.ACTION_END;
import static com.jxntv.service.AudioLivePlayHelper.ACTION_INTENT;
import static com.jxntv.service.AudioLivePlayHelper.ACTION_PAUSE;
import static com.jxntv.service.AudioLivePlayHelper.ACTION_PLAY;
import static com.jxntv.service.AudioLivePlayHelper.ACTION_SET_DATA;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;

import com.jxntv.android.liteav.GVideoView;
import com.jxntv.android.liteav.player.GVideoPlayerListener;
import com.jxntv.base.Constant;
import com.jxntv.base.R;
import com.jxntv.base.model.anotation.PlayerType;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.CircleDetail;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.utils.AppManager;
import com.jxntv.utils.OSUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author huangwei
 * date : 2021/9/22
 * desc : 音频播放服务
 **/
public class AudioLivePlayService extends Service {

    private final int mNotificationId = 1;
    private NotificationManager mNotificationManager;
    private GVideoView playView;
    private CircleDetail circleDetail;
    private Disposable timeDisposable;

    public static void startService(CircleDetail circleDetail){
        Context context = AppManager.getAppManager().currentActivity();
        if (context != null) {
            Intent intent = new Intent(context, AudioLivePlayService.class);
            intent.putExtra(ACTION_SET_DATA, circleDetail);
            context.startService(intent);
        }
    }

    private void close() {
        timeDisposable();
        Activity c = AppManager.getAppManager().currentActivity();
        if (c != null) {
            c.stopService(new Intent(c, AudioLivePlayService.class));
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null){
            circleDetail = intent.getParcelableExtra(ACTION_SET_DATA);
            if (circleDetail!=null) {
                mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                final IntentFilter filter = new IntentFilter();
                filter.addAction(ACTION_PLAY);
                filter.addAction(ACTION_PAUSE);
                filter.addAction(ACTION_INTENT);
                filter.addAction(ACTION_END);
                filter.addAction(ACTION_SET_DATA);
                registerReceiver(mIntentReceiver,filter);
                initVideoView(circleDetail.broadcastDetail.getUrl());
                GVideoEventBus.get(Constant.EVENT_MSG.VIDEO_NEED_PAUSE).post(null);
                startForeground(mNotificationId, getNotification());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GVideoEventBus.get(Constant.EVENT_MSG.AUDIO_BG_NEED_PAUSE).observeForever(observer);
    }

    /**
     * 通知栏点击事件处理
     */
    private final AudioLivePlayReceiver mIntentReceiver = new AudioLivePlayReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case ACTION_PLAY:
                    playAudio();
                    break;
                case ACTION_PAUSE:
                    pauseAudio();
                    break;
                case ACTION_INTENT:
                    Circle circle = new Circle();
                    circle.groupId = circleDetail.groupId;
                    PluginManager.get(CirclePlugin.class).startCircleDetailWithActivity(
                            context, circle,null
                    );
                    break;
                case ACTION_END:
                    close();
                    break;
            }
        }
    };

    private void playAudio(){
        //断网后会释放播放器，需要判断是否有数据，重新设置播放
        if (playView==null) {
            if (circleDetail!=null){
                initVideoView(circleDetail.broadcastDetail.getUrl());
            }else {
                return;
            }
        }else {
            playView.resume();
        }
        notifyChange();
    }

    private void pauseAudio(){
        if (playView==null) {
            return;
        }
        playView.pause();
        notifyChange();
    }

    private boolean isPlaying(){
        if (playView!=null){
            return playView.isPlaying();
        }
        return false;
    }

    private void notifyChange(){
        if (isPlaying()) {
            startForeground(mNotificationId, getNotification());
        } else {
            mNotificationManager.notify(mNotificationId, getNotification());
        }
    }

    /**
     * 设置通知栏组件
     */
    @SuppressLint("RemoteViewLayout")
    private RemoteViews getContentView(Context context) {

        RemoteViews contentView;
        if (OSUtil.isEmui() || OSUtil.isOppo()){
            contentView = new RemoteViews(context.getPackageName(), R.layout.layout_service_audio_emui);
        }else {
            contentView = new RemoteViews(context.getPackageName(), R.layout.layout_service_audio);
        }

        Intent intent = new Intent(isPlaying()?ACTION_PAUSE:ACTION_PLAY);
        PendingIntent playIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        contentView.setOnClickPendingIntent(R.id.audio_play, playIntent);

        Intent close = new Intent(ACTION_END);
        PendingIntent closeIntent = PendingIntent.getBroadcast(context, 0, close, 0);
        contentView.setOnClickPendingIntent(R.id.close, closeIntent);

        if (circleDetail!=null && circleDetail.broadcastDetail!=null) {
            contentView.setTextViewText(R.id.title, circleDetail.broadcastDetail.getName());
        }

        if (isPlaying()){
            contentView.setImageViewResource(R.id.audio_play, R.drawable.ic_bt_widget_pause);
        }else {
            contentView.setImageViewResource(R.id.audio_play, R.drawable.ic_bt_widget_play);
        }

        return contentView;
    }

    /**
     *  初始化播放器
     */
    private void initVideoView(String url){
        if (playView!=null){
            playView.release();
        }
        playView = new GVideoView(getBaseContext(), PlayerType.GVIDEO);
        playView.setKSVideoPlayerListener(new GVideoPlayerListener() {
                @Override
                public void onPlayPrepared() {

                }

                @Override
                public void onPlayBegin() {
                    GVideoEventBus.get(Constant.EVENT_MSG.VIDEO_NEED_PAUSE).post(null);
                    notifyChange();
                }

                @Override
                public void onPlayEnd() {
                    if (System.currentTimeMillis() >= circleDetail.broadcastDetail.getEndTime()) {
                        getBaseContext().sendBroadcast(new Intent(ACTION_END));
                        timeDisposable();
                    }else {
                        getBaseContext().sendBroadcast(new Intent(ACTION_PAUSE));
                    }
                }

                @Override
                public void onErrorNetDisconnect() {
                    if (playView != null) {
                        playView.release();
                        playView = null;
                    }
                    getBaseContext().sendBroadcast(new Intent(ACTION_PAUSE));
                    notifyChange();
                }

            @Override
            public void onReconnect() {

            }

            @Override
            public void onPlayStuck() {

            }
        });

        if (timeDisposable == null){
            timeDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        if (System.currentTimeMillis() >= circleDetail.broadcastDetail.getEndTime()){
                            getBaseContext().sendBroadcast(new Intent(ACTION_END));
                            timeDisposable();
                        }
                    });

        }

        playView.setIsAudioService();
        playView.startPlay(url);
    }


    private Notification getNotification(){
        String channelId = "notification_audio";
        Notification notification;
        Intent intent = new Intent(ACTION_INTENT);
        PendingIntent sender = PendingIntent.getBroadcast(getBaseContext(), 0, intent,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "默认通知",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(this, channelId)
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_SYSTEM)
                    .setOngoing(true)
                    .setPriority(NotificationManager.IMPORTANCE_MAX)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentText(getString(R.string.app_name))
                    .setTicker(getString(R.string.app_name))
                    .setContentIntent(sender)
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setCustomContentView(getContentView(getBaseContext()))
                    .setChannelId(channelId)
                    .build();
        } else {
            notification = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setTicker(getString(R.string.app_name))
                    .setContentIntent(sender)
                    .build();
        }
        return notification;
    }

    private void timeDisposable(){
        if (timeDisposable!=null){
            timeDisposable.dispose();
            timeDisposable = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (playView != null) {
            playView.release();
        }
        unregisterReceiver(mIntentReceiver);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(mNotificationId);
        }
        timeDisposable();
    }

    Observer<Object> observer = o -> AudioLivePlayHelper.getInstance().pause();

}
