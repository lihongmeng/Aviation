package com.hzlz.aviation.kernel.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.plugin.ChatIMPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.observer.BaseResponseObserver;
import com.hzlz.aviation.kernel.push.push.hms.HmsPushHelper;
import com.hzlz.aviation.kernel.push.push.mi.MiPushHelper;
import com.hzlz.aviation.kernel.push.push.oppo.OppoPushHelper;
import com.hzlz.aviation.kernel.push.push.vivo.VivoPushHelper;
import com.hzlz.aviation.kernel.push.repository.PushRepository;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.LogUtils;
import com.hzlz.aviation.library.util.OSUtil;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;

import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * @author huangwei
 * date : 2021/1/22 9:34
 * desc : 推送工具类 ，使用intent uri跳转到指定位置
 **/
public class PushManager {

    private static PushManager helper;
    private String registerId = "";
    private PushListener pushListener;
    private PushRepository pushRepository = new PushRepository();

    public static synchronized PushManager getInstance() {
        if (helper == null) {
            helper = new PushManager();
        }
        return helper;
    }

    /**
     * 初始化
     */
    public void init() {

        pushListener = new PushListener() {
            @Override
            public void success(String token) {
                LogUtils.d("push token: " + token);
                PushManager.this.registerId = token;
                uploadToken();
            }

            @Override
            public void error(String errorMessage) {
                LogUtils.e("push error: " + errorMessage);
            }
        };

        //监听登录，token与用户进行绑定，登出后解绑由后台处理
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.LOGIN).observeForever(o -> uploadToken());

        createNotificationChannel();
        if (OSUtil.isEmui()) {
            HmsPushHelper.getInstance().init(pushListener);
        } else if (OSUtil.isOppo()) {
            OppoPushHelper.getInstance().init(pushListener);
        } else if (OSUtil.isVivo()) {
            VivoPushHelper.getInstance().init(pushListener);
        } else {
            MiPushHelper.getInstance().init(pushListener);
        }

    }

    /**
     * 清除应用角标数字
     */
    public void removeShortcutBadger(){
        ShortcutBadger.removeCount(GVideoRuntime.getAppContext());
    }

    /**
     * 上报token
     */
    private void uploadToken() {

        if (TextUtils.isEmpty(registerId)) {
            LogUtils.e("push token is null");
            return;
        }

        //设置离线推送消息
        PluginManager.get(ChatIMPlugin.class).setThirdPushToken(registerId);

        pushRepository.uploadPushToken(registerId,getSystemType())
                .subscribe(new BaseResponseObserver<JsonElement>() {
                    @Override
                    protected void onRequestData(JsonElement o) {
                        LogUtils.d("token 上传成功："+ o.toString());
                    }

                    @Override
                    protected void onRequestError(Throwable throwable) {
                        LogUtils.e("push 上传失败："+throwable.getMessage());
                    }
                });
    }

    /**
     * 创建通知通道
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) GVideoRuntime.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = "1";
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "消息通知";
            // 用户可以看到的通知渠道的描述
            String description = "消息通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private int getSystemType() {
        if (OSUtil.isEmui()) {
            return 2;
        } else if (OSUtil.isOppo()) {
            return 3;
        } else if (OSUtil.isVivo()) {
            return 5;
        } else {
            return 4;
        }
    }

    public interface PushListener {
        void success(String token);

        void error(String errorMessage);
    }

    /**
     * 获取推送消息
     */
    public String getXiaomiMessageByBundle(Bundle bundle) {
        MiPushMessage miPushMessage = (MiPushMessage) bundle.getSerializable(PushMessageHelper.KEY_MESSAGE);
        if (miPushMessage == null) {
            return null;
        }
        Map extra = miPushMessage.getExtra();
        return extra.get("ext").toString();
    }

    public String getVivoMessage(){
        return "";
    }
}
