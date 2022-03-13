package com.jxntv.watchtv.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ChannelTvManifest implements Parcelable {

    // 栏目id
    public Long columnId;

    // 栏目名称
    public String columnName;

    // 播放时间
    public String playTime;

    // 节目id
    public Long programId;

    // 节目名称
    public String programName;

    public ChannelTvManifest() {

    }

    protected ChannelTvManifest(Parcel in) {
        if (in.readByte() == 0) {
            columnId = null;
        } else {
            columnId = in.readLong();
        }
        columnName = in.readString();
        playTime = in.readString();
        if (in.readByte() == 0) {
            programId = null;
        } else {
            programId = in.readLong();
        }
        programName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (columnId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(columnId);
        }
        dest.writeString(columnName);
        dest.writeString(playTime);
        if (programId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(programId);
        }
        dest.writeString(programName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChannelTvManifest> CREATOR = new Creator<ChannelTvManifest>() {
        @Override
        public ChannelTvManifest createFromParcel(Parcel in) {
            return new ChannelTvManifest(in);
        }

        @Override
        public ChannelTvManifest[] newArray(int size) {
            return new ChannelTvManifest[size];
        }
    };

}
