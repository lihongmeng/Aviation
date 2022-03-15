package com.hzlz.aviation.kernel.base.model.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class WatchTvChannel implements Parcelable {

    // 频道名称
    @SerializedName(value = "channelName", alternate = {"programName", "columnName"})
    public String name;

    // 频道图标URL
    @SerializedName(value = "iconUrl", alternate = {"cover", "coverUrl"})
    public String picUrl;

    // 频道id
    public Long id;

    // 简介
    @SerializedName(value = "intro", alternate = "introduction")
    public String intro;

    // 频道类型
    public int type;

    // 栏目Id
    public long columnId;

    public WatchTvChannel() {

    }

    public WatchTvChannel(Long id) {
        this.id = id;
    }

    protected WatchTvChannel(Parcel in) {
        name = in.readString();
        picUrl = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        intro = in.readString();
        type = in.readInt();
        columnId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(picUrl);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(intro);
        dest.writeInt(type);
        dest.writeLong(columnId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WatchTvChannel> CREATOR = new Creator<WatchTvChannel>() {
        @Override
        public WatchTvChannel createFromParcel(Parcel in) {
            return new WatchTvChannel(in);
        }

        @Override
        public WatchTvChannel[] newArray(int size) {
            return new WatchTvChannel[size];
        }
    };

    public static List<String> getNameValueList(List<WatchTvChannel> watchTvChannelList) {
        List<String> result = new ArrayList<>();
        if (watchTvChannelList == null) {
            return result;
        }
        for (WatchTvChannel watchTvChannel : watchTvChannelList) {
            result.add(watchTvChannel == null ? "" : watchTvChannel.name);
        }
        return result;
    }

}
