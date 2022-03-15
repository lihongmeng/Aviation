package com.hzlz.aviation.kernel.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CircleDetailFavoriteVO implements Parcelable {

    /**
     * 红人总数
     */
    public int count;

    /**
     * 红人列表
     */
    public List<CircleFamous> userList;

    public CircleDetailFavoriteVO(){

    }

    protected CircleDetailFavoriteVO(Parcel in) {
        count = in.readInt();
        userList = in.createTypedArrayList(CircleFamous.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
        dest.writeTypedList(userList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleDetailFavoriteVO> CREATOR = new Creator<CircleDetailFavoriteVO>() {
        @Override
        public CircleDetailFavoriteVO createFromParcel(Parcel in) {
            return new CircleDetailFavoriteVO(in);
        }

        @Override
        public CircleDetailFavoriteVO[] newArray(int size) {
            return new CircleDetailFavoriteVO[size];
        }
    };
}
