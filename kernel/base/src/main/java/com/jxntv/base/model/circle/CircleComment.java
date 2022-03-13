package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CircleComment implements Parcelable {

    /**
     * 评论内容
     */
    public String comment;

    /**
     * 评论id
     */
    public long commentId;

    /**
     * 评论内容类型
     */
    public String contentType;

    /**
     * 评论时间
     */
    public long createDate;

    /**
     * 评论人
     */
    public String createUserName;

    /**
     * 图片列表
     */
    public List<CircleCommentImage> imageList;

    /**
     * 评论资源id
     */
    public long mediaId;

    /**
     * 内容
     */
    public String soundContent;

    /**
     * 语音url
     */
    public String soundUrl;

    /**
     * 状态
     */
    public String status;

    /**
     * 评论类型
     */
    public String type;

    public CircleComment(){

    }

    protected CircleComment(Parcel in) {
        comment = in.readString();
        commentId = in.readLong();
        contentType = in.readString();
        createDate = in.readLong();
        createUserName = in.readString();
        imageList = in.createTypedArrayList(CircleCommentImage.CREATOR);
        mediaId = in.readLong();
        soundContent = in.readString();
        soundUrl = in.readString();
        status = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeLong(commentId);
        dest.writeString(contentType);
        dest.writeLong(createDate);
        dest.writeString(createUserName);
        dest.writeTypedList(imageList);
        dest.writeLong(mediaId);
        dest.writeString(soundContent);
        dest.writeString(soundUrl);
        dest.writeString(status);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleComment> CREATOR = new Creator<CircleComment>() {
        @Override
        public CircleComment createFromParcel(Parcel in) {
            return new CircleComment(in);
        }

        @Override
        public CircleComment[] newArray(int size) {
            return new CircleComment[size];
        }
    };
}
