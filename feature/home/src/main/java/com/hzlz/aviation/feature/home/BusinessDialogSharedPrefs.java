package com.hzlz.aviation.feature.home;

import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;

public class BusinessDialogSharedPrefs extends SharedPrefsWrapper {

    private static final String SP_FILE_NAME = "sp_home";

    /**
     * 构造函数
     */
    private BusinessDialogSharedPrefs() {
        super(SP_FILE_NAME);
    }

    private volatile static BusinessDialogSharedPrefs singleInstance = null;

    public static BusinessDialogSharedPrefs getInstance() {
        if (singleInstance == null) {
            synchronized (BusinessDialogSharedPrefs.class) {
                if (singleInstance == null) {
                    singleInstance = new BusinessDialogSharedPrefs();
                }
            }
        }
        return singleInstance;
    }

}
