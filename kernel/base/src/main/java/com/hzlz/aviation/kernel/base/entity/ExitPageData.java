package com.hzlz.aviation.kernel.base.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.hzlz.aviation.kernel.base.model.video.VideoModel;

public class ExitPageData implements Parcelable {

    public VideoModel videoModel;

    /**
     * 页面名称
     */
    public String pageName;

    /**
     * 社区名称
     */
    public String communityName;

    /**
     * 所属MCN
     */
    public Long tenantId;

    /**
     * 所属MCN名称
     */
    public String tenantName;

    /**
     * 来源页面, 即上一级界面
     */
    public String sourcePage;


    public ExitPageData(){

    }

    protected ExitPageData(Parcel in) {
        videoModel = in.readParcelable(VideoModel.class.getClassLoader());
        pageName = in.readString();
        communityName = in.readString();
        if (in.readByte() == 0) {
            tenantId = null;
        } else {
            tenantId = in.readLong();
        }
        tenantName = in.readString();
        sourcePage = in.readString();
    }

    public static final Creator<ExitPageData> CREATOR = new Creator<ExitPageData>() {
        @Override
        public ExitPageData createFromParcel(Parcel in) {
            return new ExitPageData(in);
        }

        @Override
        public ExitPageData[] newArray(int size) {
            return new ExitPageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(videoModel, i);
        parcel.writeString(pageName);
        parcel.writeString(communityName);
        if (tenantId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(tenantId);
        }
        parcel.writeString(tenantName);
        parcel.writeString(sourcePage);
    }
}
