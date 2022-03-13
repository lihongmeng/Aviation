package com.jxntv.home.launch;

import com.jxntv.base.sp.SharedPrefsWrapper;

/**
 * 启动用sp
 */
public class LaunchSharedPrefs extends SharedPrefsWrapper {

    public static final String KEY_LAUNCH_STATE = "key_launch_state";
    /** 启动状态：正常启动。 */
    public static final int STATE_NORMAL = 1;
    /** 启动状态：升级后启动 */
    public static final int STATE_UPDATE = 20;
    /** 启动状态：新安装启动 */
    public static final int STATE_INSTALL = 30;

    /** key ：版本号 */
    public static final String KEY_VERSION = "key_version";
    /** key ：是否确认协议 */
    public static final String KEY_HAS_CONFIRM = "has_confirm";
    /** key ：launch文件 */
    public static final String SP_FILE_LAUNCH = "sp_launch";
    /** 已经授权登陆过 */
    public static final String KEY_AUTH_VISITED = "auth_visited";

    /**
     * 本地单利持有类
     */
    private static final class Holder {
        /** 持有的单例 */
        private static final LaunchSharedPrefs INSTANCE = new LaunchSharedPrefs();
    }

    /**
     * 构造函数
     */
    private LaunchSharedPrefs() {
        super(SP_FILE_LAUNCH);
    }

    /**
     * 获取单例
     */
    public static LaunchSharedPrefs getInstance() {
        return Holder.INSTANCE;
    }
}
