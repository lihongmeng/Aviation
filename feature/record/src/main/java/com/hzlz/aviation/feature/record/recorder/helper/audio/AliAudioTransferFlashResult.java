package com.hzlz.aviation.feature.record.recorder.helper.audio;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AliAudioTransferFlashResult {

    @SerializedName("sentences")
    public List<AliAudioTransferFlashResultSentence> sentences;

    @SerializedName("duration")
    public long duration;

    @SerializedName("completed")
    public boolean completed;

    @SerializedName("latency")
    public long latency;

}
