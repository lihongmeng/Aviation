package com.jxntv.runtime;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 全局run time提供类
 */
public class GVideoRuntime {
    public static final String APP_STATUS_CHANGED = "app_status_changed";

    /** runtime application */
    private static Application sApplication;

    private static String channelType = "";

    /**
     * 初始化
     */
    public static void onApplicationattachBaseContext(Application application ,String type) {
        sApplication = application;
        channelType = type;
    }

    /**
     * 获取全局app context
     */
    public static Context getAppContext() {
        return sApplication;
    }

    /**
     * 获取application
     */
    public static Application getApplication() {
        return sApplication;
    }

    public static String getVersionName() {
        try {
            return sApplication.getPackageManager().getPackageInfo(sApplication.getPackageName(),
                PackageManager.GET_META_DATA).versionName;
        } catch (Exception e) {
        }
        return "";
    }

    public static int getVersionCode() {
        try {
            return sApplication.getPackageManager().getPackageInfo(sApplication.getPackageName(),
                PackageManager.GET_META_DATA).versionCode;
        } catch (Exception e) {
        }
        return 0;
    }


    public static String getPackageName() {
        try {
            return sApplication.getPackageManager().getPackageInfo(sApplication.getPackageName(),
                    PackageManager.GET_META_DATA).packageName;
        } catch (Exception e) {
        }
        return "";
    }

    public static String getChannelType(){
        return channelType;
    }
}
