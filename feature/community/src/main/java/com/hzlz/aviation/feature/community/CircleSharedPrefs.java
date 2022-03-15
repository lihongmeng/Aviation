package com.hzlz.aviation.feature.community;

import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;

public class CircleSharedPrefs extends SharedPrefsWrapper {

    private volatile static CircleSharedPrefs singleInstance = null;

    private static final String SP_FILE_NAME = "sp_circle";

    private CircleSharedPrefs() {
        super(SP_FILE_NAME);
    }

    public static CircleSharedPrefs getInstance() {
        if (singleInstance == null) {
            synchronized (CircleSharedPrefs.class) {
                if (singleInstance == null) {
                    singleInstance = new CircleSharedPrefs();
                }
            }
        }
        return singleInstance;
    }

}
