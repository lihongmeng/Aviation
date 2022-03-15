package com.hzlz.aviation.kernel.base.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.hzlz.aviation.kernel.base.model.video.AuthorModel;

import java.util.List;

public class QualityComment implements Parcelable {

    /**
     * 评论时间
     */
    public String commentDate;

    /**
     * 评论用户
     */
    public AuthorModel commentUser;

    /**
     * 评论内容
     */
    public String content;

    /**
     * 评论图片
     */
    public List<String> imageList;

    /**
     * 源评论图片
     */
    public List<String> oriImageList;

    /**
     * 是否点赞
     */
    public boolean isPraise;

    /**
     * 点赞数量
     */
    public int praiseTotal;

    /**
     * 评论唯一标识
     */
    public String primaryId;

    /**
     * 关联语音文字
     */
    public String soundContent;

    /**
     * 关联语音
     */
    public String soundUrl;

    /**
     * 语音长度
     */
    public long length;

    protected QualityComment(Parcel in) {
        commentDate = in.readString();
        commentUser = in.readParcelable(AuthorModel.class.getClassLoader());
        content = in.readString();
        imageList = in.createStringArrayList();
        oriImageList = in.createStringArrayList();
        isPraise = in.readByte() != 0;
        praiseTotal = in.readInt();
        primaryId = in.readString();
        soundContent = in.readString();
        soundUrl = in.readString();
        length = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(commentDate);
        dest.writeParcelable(commentUser, flags);
        dest.writeString(content);
        dest.writeStringList(imageList);
        dest.writeStringList(oriImageList);
        dest.writeByte((byte) (isPraise ? 1 : 0));
        dest.writeInt(praiseTotal);
        dest.writeString(primaryId);
        dest.writeString(soundContent);
        dest.writeString(soundUrl);
        dest.writeLong(length);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QualityComment> CREATOR = new Creator<QualityComment>() {
        @Override
        public QualityComment createFromParcel(Parcel in) {
            return new QualityComment(in);
        }

        @Override
        public QualityComment[] newArray(int size) {
            return new QualityComment[size];
        }
    };
}
