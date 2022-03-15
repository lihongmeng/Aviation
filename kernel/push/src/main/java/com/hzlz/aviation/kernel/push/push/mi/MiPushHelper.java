package com.hzlz.aviation.kernel.push.push.mi;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.hzlz.aviation.kernel.push.PushManager;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;

import java.util.List;

/**
 * 小米推送工具类
 */
public class MiPushHelper {

    private String registerId;

    private static MiPushHelper miPushHelper;
    private PushManager.PushListener listener;

    public static MiPushHelper getInstance(){
        if (miPushHelper==null){
            miPushHelper = new MiPushHelper();
        }
        return miPushHelper;
    }

    public void init(PushManager.PushListener listener){
        this.listener = listener;
        // if (TextUtils.isEmpty(BuildConfig.xiaomi_appId)){
        //     if (listener!=null) listener.error("MI推送配置信息缺失");
        // }else {
        //     if (shouldInit()) {
        //         MiPushClient.registerPush(GVideoRuntime.getAppContext(), BuildConfig.xiaomi_appId, BuildConfig.xiaomi_appKey);
        //     }
        // }
    }

    public void setRegisterId(String registerId){
        this.registerId = registerId;
        if (listener!=null) listener.success(registerId);
    }


    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) GVideoRuntime.getAppContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = GVideoRuntime.getAppContext().getApplicationContext().getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}
