package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class CircleTag implements Parcelable {

    public static final String TAG = "CircleTag";

    public String key;

    public String value;

    public CircleTag() {

    }

    protected CircleTag(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleTag> CREATOR = new Creator<CircleTag>() {
        @Override
        public CircleTag createFromParcel(Parcel in) {
            return new CircleTag(in);
        }

        @Override
        public CircleTag[] newArray(int size) {
            return new CircleTag[size];
        }
    };

    public static List<CircleTag> createTestData() {
        List<CircleTag> result = new ArrayList<>();

        CircleTag A1 = new CircleTag();
        A1.key = "1";
        A1.value = "新";
        result.add(A1);

        CircleTag A2 = new CircleTag();
        A2.key = "2";
        A2.value = "新新";
        result.add(A2);

        CircleTag A3 = new CircleTag();
        A3.key = "3";
        A3.value = "新新新";
        result.add(A3);

        CircleTag A4 = new CircleTag();
        A4.key = "4";
        A4.value = "新新新新";
        result.add(A4);

        CircleTag A5 = new CircleTag();
        A5.key = "5";
        A5.value = "新新新新新";
        result.add(A5);

        CircleTag A6 = new CircleTag();
        A6.key = "6";
        A6.value = "新新新新新新";
        result.add(A6);

        CircleTag A7 = new CircleTag();
        A7.key = "7";
        A7.value = "新新新新新新新";
        result.add(A7);

        return result;
    }

    public static boolean isDataChange(
            List<CircleTag> oldDataList,
            List<CircleTag> newDataList
    ) {
        if (oldDataList == null && newDataList == null) {
            return false;
        }
        if (oldDataList == null || newDataList == null) {
            return true;
        }
        int size = oldDataList.size();
        if (size != newDataList.size()) {
            return false;
        }
        for (int index = 0; index < size; index++) {
            CircleTag left = oldDataList.get(index);
            if (left == null || TextUtils.isEmpty(left.key)) {
                return false;
            }
            CircleTag right = newDataList.get(index);
            if (right == null
                    || TextUtils.isEmpty(right.key)
                    || !left.key.equals(right.key)
                    || !left.value.equals(right.value)
            ) {
                return false;
            }
        }
        return true;
    }

}
