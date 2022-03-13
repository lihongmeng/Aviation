package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

import com.jxntv.base.adapter.AbstractAdapterModel;

public class CircleFamous extends AbstractAdapterModel implements Parcelable {

    public String authenticationIntro;

    public CircleCommentImage avatar;

    public boolean isAuthentication;

    public long jid;

    public String nickname;

    public int roleType;

    public CircleFamous() {

    }

    protected CircleFamous(Parcel in) {
        authenticationIntro = in.readString();
        avatar = in.readParcelable(CircleCommentImage.class.getClassLoader());
        isAuthentication = in.readByte() != 0;
        jid = in.readLong();
        nickname = in.readString();
        roleType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authenticationIntro);
        dest.writeParcelable(avatar, flags);
        dest.writeByte((byte) (isAuthentication ? 1 : 0));
        dest.writeLong(jid);
        dest.writeString(nickname);
        dest.writeInt(roleType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleFamous> CREATOR = new Creator<CircleFamous>() {
        @Override
        public CircleFamous createFromParcel(Parcel in) {
            return new CircleFamous(in);
        }

        @Override
        public CircleFamous[] newArray(int size) {
            return new CircleFamous[size];
        }
    };

}
