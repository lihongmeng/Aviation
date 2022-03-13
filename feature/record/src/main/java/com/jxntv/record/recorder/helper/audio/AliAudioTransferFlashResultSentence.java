package com.jxntv.record.recorder.helper.audio;

import com.google.gson.annotations.SerializedName;

public class AliAudioTransferFlashResultSentence {

    @SerializedName("text")
    public String text;

    @SerializedName("begin_time")
    public long beginTime;

    @SerializedName("end_time")
    public long endTime;

    @SerializedName("channel_id")
    public long channelId;

}
