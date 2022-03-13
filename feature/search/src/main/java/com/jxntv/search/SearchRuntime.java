package com.jxntv.search;

import android.app.Application;
import android.content.Context;
import com.jxntv.runtime.GVideoRuntime;

/**
 * 搜索 运行时数据模型
 */
public class SearchRuntime {

    /** 持有的app context */
    private static Context sAppContext;
    /** 持有的application */
    private static Application sApplication;

    static {
        sAppContext = GVideoRuntime.getAppContext();
        sApplication = GVideoRuntime.getApplication();
    }

    /**
     * 获取app context
     */
    public static Context getAppContext() {
        return sAppContext;
    }

    /**
     * 获取application
     */
    public static Application getApplication() {
        return sApplication;
    }
}
