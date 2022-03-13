package com.jxntv.watchtv.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class WatchTvChannelDetail implements Parcelable {

    // 鉴权URL
    public String authUrl;

    // 频道名称
    public String channelName;

    // 频道图标URL
    public String iconUrl;

    // 频道id
    public Long id;

    // 频道视频播放URL
    public String playUrl;

    /**
     * 频道类型
     *
     * @see com.jxntv.base.Constant.WATCH_TV_CHANNEL_TYPE
     */
    public int type;

    // 频道封面URL
    public String coverUrl;

    protected WatchTvChannelDetail(Parcel in) {
        authUrl = in.readString();
        channelName = in.readString();
        iconUrl = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        playUrl = in.readString();
        type = in.readInt();
        coverUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authUrl);
        dest.writeString(channelName);
        dest.writeString(iconUrl);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(playUrl);
        dest.writeInt(type);
        dest.writeString(coverUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WatchTvChannelDetail> CREATOR = new Creator<WatchTvChannelDetail>() {
        @Override
        public WatchTvChannelDetail createFromParcel(Parcel in) {
            return new WatchTvChannelDetail(in);
        }

        @Override
        public WatchTvChannelDetail[] newArray(int size) {
            return new WatchTvChannelDetail[size];
        }
    };

}
