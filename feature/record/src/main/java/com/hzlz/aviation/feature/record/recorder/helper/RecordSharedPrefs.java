package com.hzlz.aviation.feature.record.recorder.helper;

import com.hzlz.aviation.kernel.base.sp.SharedPrefsWrapper;

/**
 * 录制相关sp
 */
public class RecordSharedPrefs extends SharedPrefsWrapper {

    /** key ：record文件 */
    public static final String SP_FILE_RECORD = "sp_record";
    /** key ：录制最大时长 */
    public static final String KEY_SELECT_VIDEO_MAX_TIME = "key_select_video_max_time";
    /** 选择视频默认最大时长（s） */
    public static final int SELECT_VIDEO_MAX_TIME_DEFAULT = 60;

    /**
     * 本地单利持有类
     */
    private static final class Holder {
        /** 持有的单例 */
        private static final RecordSharedPrefs INSTANCE = new RecordSharedPrefs();
    }

    /**
     * 构造函数
     */
    private RecordSharedPrefs() {
        super(SP_FILE_RECORD);
    }

    /**
     * 获取单例
     */
    public static RecordSharedPrefs getInstance() {
        return Holder.INSTANCE;
    }
}
