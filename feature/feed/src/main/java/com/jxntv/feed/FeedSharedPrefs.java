package com.jxntv.feed;

import com.jxntv.base.sp.SharedPrefsWrapper;

public class FeedSharedPrefs extends SharedPrefsWrapper {

    private static final String SP_FILE_NAME = "sp_feed";

    /**
     * 构造函数
     */
    private FeedSharedPrefs() {
        super(SP_FILE_NAME);
    }

    private volatile static FeedSharedPrefs singleInstance = null;

    public static FeedSharedPrefs getInstance() {
        if (singleInstance == null) {
            synchronized (FeedSharedPrefs.class) {
                if (singleInstance == null) {
                    singleInstance = new FeedSharedPrefs();
                }
            }
        }
        return singleInstance;
    }

}
