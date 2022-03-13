package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

public class CircleVerify implements Parcelable {

    public long createDate;

    public long jid;

    public String nickname;

    public String reason;

    public CircleVerify() {

    }

    protected CircleVerify(Parcel in) {
        createDate = in.readLong();
        jid = in.readLong();
        nickname = in.readString();
        reason = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(createDate);
        dest.writeLong(jid);
        dest.writeString(nickname);
        dest.writeString(reason);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleVerify> CREATOR = new Creator<CircleVerify>() {
        @Override
        public CircleVerify createFromParcel(Parcel in) {
            return new CircleVerify(in);
        }

        @Override
        public CircleVerify[] newArray(int size) {
            return new CircleVerify[size];
        }
    };

}
