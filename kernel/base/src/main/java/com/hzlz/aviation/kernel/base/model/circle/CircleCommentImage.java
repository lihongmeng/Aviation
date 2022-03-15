package com.hzlz.aviation.kernel.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

public class CircleCommentImage implements Parcelable {

    /**
     * 阿里云uuid
     */
    public String ossId;

    /**
     * 图片地址
     */
    public String url;

    public CircleCommentImage() {

    }

    public CircleCommentImage(String ossId, String url) {
        this.ossId = ossId;
        this.url = url;
    }

    protected CircleCommentImage(Parcel in) {
        ossId = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ossId);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleCommentImage> CREATOR = new Creator<CircleCommentImage>() {
        @Override
        public CircleCommentImage createFromParcel(Parcel in) {
            return new CircleCommentImage(in);
        }

        @Override
        public CircleCommentImage[] newArray(int size) {
            return new CircleCommentImage[size];
        }
    };
}
