package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.InteractDataObservable;

import java.util.List;

public class BroadCastDetail implements Parcelable {

    private String name;

    private String url;

    private long startTime;

    private long endTime;

    private AuthorModel author;

    protected BroadCastDetail(Parcel in) {
        name = in.readString();
        url = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
        author = in.readParcelable(AuthorModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeParcelable(author, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BroadCastDetail> CREATOR = new Creator<BroadCastDetail>() {
        @Override
        public BroadCastDetail createFromParcel(Parcel in) {
            return new BroadCastDetail(in);
        }

        @Override
        public BroadCastDetail[] newArray(int size) {
            return new BroadCastDetail[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public AuthorModel getAuthor() {
        return author;
    }

    public void setAuthor(AuthorModel author) {
        this.author = author;
    }
}
