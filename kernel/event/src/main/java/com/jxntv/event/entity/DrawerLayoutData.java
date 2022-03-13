package com.jxntv.event.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class DrawerLayoutData implements Parcelable {

    /**
     * 对抽屉的操作
     * <p>
     * true   打开
     * false  关闭
     */
    public boolean isOpen;

    /**
     * 打开抽屉的页面的Pid
     */
    public String pageName;

    public DrawerLayoutData(boolean isOpen, String pageName) {
        this.isOpen = isOpen;
        this.pageName = pageName;
    }

    protected DrawerLayoutData(Parcel in) {
        isOpen = in.readByte() != 0;
        pageName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isOpen ? 1 : 0));
        dest.writeString(pageName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DrawerLayoutData> CREATOR = new Creator<DrawerLayoutData>() {
        @Override
        public DrawerLayoutData createFromParcel(Parcel in) {
            return new DrawerLayoutData(in);
        }

        @Override
        public DrawerLayoutData[] newArray(int size) {
            return new DrawerLayoutData[size];
        }
    };
}
