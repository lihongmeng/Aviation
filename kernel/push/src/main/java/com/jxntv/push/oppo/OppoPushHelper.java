package com.jxntv.push.oppo;

import android.text.TextUtils;

import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.jxntv.BuildConfig;
import com.jxntv.PushManager;
import com.jxntv.runtime.GVideoRuntime;
import com.jxntv.utils.LogUtils;

/**
 * @author huangwei
 * time : 2021/1/22 9:34
 * desc : oppo 推送
 **/
public class OppoPushHelper {

    private static OppoPushHelper helper;
    private PushManager.PushListener pushListener;
    private String registerId;

    public static OppoPushHelper getInstance(){
        if (helper == null){
            helper = new OppoPushHelper();
        }
        return helper;
    }


    public void init(PushManager.PushListener listener){
        this.pushListener = listener;
        if (TextUtils.isEmpty(BuildConfig.oppo_appId)){
            if (pushListener!=null) pushListener.error("OPPO推送配置信息缺失");
        }else {
            HeytapPushManager.init(GVideoRuntime.getAppContext(), BuildConfig.DEBUG);
            if (HeytapPushManager.isSupportPush()) {
                try {
                    HeytapPushManager.clearNotifications();
                    HeytapPushManager.register(GVideoRuntime.getAppContext(), BuildConfig.oppo_appKey, BuildConfig.oppo_appSecret, mPushCallback);
                    HeytapPushManager.requestNotificationPermission();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (pushListener!=null) pushListener.error("oppo 推送注册失败："+e.getMessage());
                }
            }
        }
    }


    private int count = 0;
    private ICallBackResultService mPushCallback = new ICallBackResultService() {
        @Override
        public void onRegister(int code, String s) {
            if (code == 0) {
                if (TextUtils.isEmpty(registerId) || !s.equals(registerId)){
                    registerId = s;
                    HeytapPushManager.getPushStatus();
                    HeytapPushManager.getPushVersionName();
                    if (pushListener!=null) pushListener.success(registerId);
                }
            } else {
                if (count<3) {
                    count++;
                    HeytapPushManager.getRegister();
                }
                LogUtils.e("注册失败: code=" + code + ",msg=" + s);
            }
        }

        @Override
        public void onUnRegister(int code) {
            if (code == 0) {
                LogUtils.d("注销成功 code=" + code);
            } else {
                LogUtils.e("注销失败 code=" + code);
            }
        }


        @Override
        public void onGetPushStatus(final int code, int status) {
            if (code == 0 && status == 0) {
                LogUtils.d("Push状态正常 code=" + code + ",status=" + status);
            } else {
                LogUtils.e("Push状态错误 code=" + code + ",status=" + status);
            }
        }

        @Override
        public void onGetNotificationStatus(final int code, final int status) {
            if (code == 0 && status == 0) {
                LogUtils.d("通知状态正常 code=" + code + ",status=" + status);
            } else {
                LogUtils.e("通知状态错误 code=" + code + ",status=" + status);
            }
        }

        @Override
        public void onSetPushTime(final int code, final String s) {
            LogUtils.d("SetPushTime code=" + code + ",result:" + s);
        }

    };


    public void unregisterPush(){
        HeytapPushManager.unRegister();
    }

}
