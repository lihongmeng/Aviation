package com.jxntv.record.recorder.data;

import android.text.TextUtils;

public class SoundEntity {

    // 语音文件在阿里云的位置
    public String soundFilePath;

    // 语音对应的文字
    public String soundText;

    // 语音的时长
    // 可用于粗略计算，与语音文件真实时长可能有几十毫秒误差
    public long operationTime;

    public boolean isValid() {
        return !TextUtils.isEmpty(soundFilePath)
                && operationTime > 0;
    }

}
