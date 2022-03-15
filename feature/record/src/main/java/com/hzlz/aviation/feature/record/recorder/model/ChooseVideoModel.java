package com.hzlz.aviation.feature.record.recorder.model;

/**
 * 选择视频的结果消息实体
 */
public final class ChooseVideoModel {

    // 视频文件在手机上的地址
    public String videoPath;

    public ChooseVideoModel() {

    }

    public ChooseVideoModel(String videoPath) {
        this.videoPath = videoPath;
    }

}
