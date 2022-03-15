package com.hzlz.aviation.feature.record.recorder.helper.audio;

import com.google.gson.annotations.SerializedName;

public class AliAudioTransfer {

    @SerializedName("task_id")
    public String taskId;

    @SerializedName("result")
    public String result;

    @SerializedName("status")
    public long status;

    @SerializedName("message")
    public String message;

    @SerializedName("flash_result")
    public AliAudioTransferFlashResult flashResult;

}
