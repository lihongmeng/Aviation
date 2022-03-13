package com.jxntv.base.sharedprefs;

import com.jxntv.base.sp.SharedPrefsWrapper;

public class KernelSharedPrefs extends SharedPrefsWrapper {

    private static final String SP_FILE_NAME = "sp_kernel";

    /**
     * 构造函数
     */
    private KernelSharedPrefs() {
        super(SP_FILE_NAME);
    }

    private volatile static KernelSharedPrefs singleInstance = null;

    public static KernelSharedPrefs getInstance() {
         if (singleInstance == null) {
            synchronized (KernelSharedPrefs.class) {
                if (singleInstance == null) {
                    singleInstance = new KernelSharedPrefs();
                }
            }
        }
        return singleInstance;
    }

}
