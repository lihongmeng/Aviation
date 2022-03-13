package com.jxntv.record.recorder.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * 选择图片的结果消息实体
 */
public final class ChooseImageListModel {

    // 选择的图片在手机上的地址
    @SerializedName("imagePathList")
    public ArrayList<String> imagePathList;

    /**
     * 是否是以替换的方式启动
     */
    @SerializedName("operationType")
    public int operationType;

    /**
     * 如果是单张替换，需要对应的位置
     */
    @SerializedName("singleOperationIndex")
    public int singleOperationIndex;

    public ChooseImageListModel() {
    }

    public ChooseImageListModel(ArrayList<String> imagePathList,int operationType,int singleOperationIndex) {
        this.imagePathList = imagePathList;
        this.operationType = operationType;
        this.singleOperationIndex = singleOperationIndex;
    }

}
