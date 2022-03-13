package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

public class CircleSpecialTag implements Parcelable {

    /**
     * 栏目ID
     */
    public long id;

    /**
     * 栏目名称
     */
    public String name;

    public CircleSpecialTag() {

    }

    protected CircleSpecialTag(Parcel in) {
        id = in.readLong();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleSpecialTag> CREATOR = new Creator<CircleSpecialTag>() {
        @Override
        public CircleSpecialTag createFromParcel(Parcel in) {
            return new CircleSpecialTag(in);
        }

        @Override
        public CircleSpecialTag[] newArray(int size) {
            return new CircleSpecialTag[size];
        }
    };
}
