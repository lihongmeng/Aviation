package com.hzlz.aviation.kernel.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ResourceVO implements Parcelable {

    public String content;

    public int fileType;

    public String imageId;

    public List<String> imageList;

    public String introduction;

    public boolean isPublic;

    public String length;

    public String soundContent;

    public String videoId;

    public ResourceVO() {

    }

    protected ResourceVO(Parcel in) {
        content = in.readString();
        fileType = in.readInt();
        imageId = in.readString();
        imageList = in.createStringArrayList();
        introduction = in.readString();
        isPublic = in.readByte() != 0;
        length = in.readString();
        soundContent = in.readString();
        videoId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeInt(fileType);
        dest.writeString(imageId);
        dest.writeStringList(imageList);
        dest.writeString(introduction);
        dest.writeByte((byte) (isPublic ? 1 : 0));
        dest.writeString(length);
        dest.writeString(soundContent);
        dest.writeString(videoId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResourceVO> CREATOR = new Creator<ResourceVO>() {
        @Override
        public ResourceVO createFromParcel(Parcel in) {
            return new ResourceVO(in);
        }

        @Override
        public ResourceVO[] newArray(int size) {
            return new ResourceVO[size];
        }
    };

}
