package com.hzlz.aviation.kernel.base.model.stat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 通用埋点数据模型，用来记录详情页播放视频来源
 */
public class StatFromModel implements Parcelable {

    public String contentId;
    public String pid;
    public String channelId;
    public String fromPid;
    public String fromChannelId;

    public StatFromModel() {
    }

    public StatFromModel(
            String contentId,
            String pid,
            String channelId,
            String fromPid,
            String fromChannelId
    ) {
        this.contentId = contentId;
        this.pid = pid;
        this.channelId = channelId;
        this.fromPid = fromPid;
        this.fromChannelId = fromChannelId;
    }

    protected StatFromModel(Parcel in) {
        contentId = in.readString();
        pid = in.readString();
        channelId = in.readString();
        fromPid = in.readString();
        fromChannelId = in.readString();
    }

    public static final Creator<StatFromModel> CREATOR = new Creator<StatFromModel>() {
        @Override
        public StatFromModel createFromParcel(Parcel in) {
            return new StatFromModel(in);
        }

        @Override
        public StatFromModel[] newArray(int size) {
            return new StatFromModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contentId);
        parcel.writeString(pid);
        parcel.writeString(channelId);
        parcel.writeString(fromPid);
        parcel.writeString(fromChannelId);
    }
}
