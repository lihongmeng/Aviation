package com.jxntv.base.model.ugcauth;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class AuthUgcReply implements Parcelable {

    public String avatar;

    public String content;

    protected AuthUgcReply(Parcel in) {
        avatar = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatar);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuthUgcReply> CREATOR = new Creator<AuthUgcReply>() {
        @Override
        public AuthUgcReply createFromParcel(Parcel in) {
            return new AuthUgcReply(in);
        }

        @Override
        public AuthUgcReply[] newArray(int size) {
            return new AuthUgcReply[size];
        }
    };

    public static String getName(String content, int maxLength) {
        if (TextUtils.isEmpty(content) || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength);
    }

}
