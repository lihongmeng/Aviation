package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

public class CircleDetailLiveVo implements Parcelable {

    /**
     * 直播id
     */
    public long id;

    /**
     * 直播标题
     */
    public String title;

    public CircleDetailLiveVo(){

    }

    protected CircleDetailLiveVo(Parcel in) {
        id = in.readLong();
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleDetailLiveVo> CREATOR = new Creator<CircleDetailLiveVo>() {
        @Override
        public CircleDetailLiveVo createFromParcel(Parcel in) {
            return new CircleDetailLiveVo(in);
        }

        @Override
        public CircleDetailLiveVo[] newArray(int size) {
            return new CircleDetailLiveVo[size];
        }
    };
}
