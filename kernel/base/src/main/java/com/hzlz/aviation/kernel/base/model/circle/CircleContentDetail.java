package com.hzlz.aviation.kernel.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

public class CircleContentDetail implements Parcelable {

    public String authorName;

    public long mediaId;

    public long publishTime;

    public String showName;

    public int status;

    public long topicId;

    public CircleContentDetail(){

    }

    protected CircleContentDetail(Parcel in) {
        authorName = in.readString();
        mediaId = in.readLong();
        publishTime = in.readLong();
        showName = in.readString();
        status = in.readInt();
        topicId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authorName);
        dest.writeLong(mediaId);
        dest.writeLong(publishTime);
        dest.writeString(showName);
        dest.writeInt(status);
        dest.writeLong(topicId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleContentDetail> CREATOR = new Creator<CircleContentDetail>() {
        @Override
        public CircleContentDetail createFromParcel(Parcel in) {
            return new CircleContentDetail(in);
        }

        @Override
        public CircleContentDetail[] newArray(int size) {
            return new CircleContentDetail[size];
        }
    };
}
