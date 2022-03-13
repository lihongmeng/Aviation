package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MyCircle implements Parcelable {

    public List<Circle> groupList;

    public MyCircle(){

    }


    protected MyCircle(Parcel in) {
        groupList = in.createTypedArrayList(Circle.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(groupList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyCircle> CREATOR = new Creator<MyCircle>() {
        @Override
        public MyCircle createFromParcel(Parcel in) {
            return new MyCircle(in);
        }

        @Override
        public MyCircle[] newArray(int size) {
            return new MyCircle[size];
        }
    };

}
