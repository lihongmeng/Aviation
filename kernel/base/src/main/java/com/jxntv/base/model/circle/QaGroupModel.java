package com.jxntv.base.model.circle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author huangwei
 * date : 2021/9/3
 * desc : 圈子详情问答广场对象
 **/
public class QaGroupModel implements Parcelable {

    /**
     * 回答用户头像
     */
    private String avatar;
    /**
     * 回答用户昵称
     */
    private String name;
    private String mediaId;
    private String commentId;
    /**
     * 提问用户昵称
     */
    private String questionAuthorName;

    private String jid;

    private boolean auth;

    public QaGroupModel() {
    }

    protected QaGroupModel(Parcel in) {
        avatar = in.readString();
        name = in.readString();
        mediaId = in.readString();
        commentId = in.readString();
        questionAuthorName = in.readString();
        jid = in.readString();
    }

    public static final Creator<QaGroupModel> CREATOR = new Creator<QaGroupModel>() {
        @Override
        public QaGroupModel createFromParcel(Parcel in) {
            return new QaGroupModel(in);
        }

        @Override
        public QaGroupModel[] newArray(int size) {
            return new QaGroupModel[size];
        }
    };

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getQuestionAuthorName() {
        return questionAuthorName;
    }

    public void setQuestionAuthorName(String questionAuthorName) {
        this.questionAuthorName = questionAuthorName;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(avatar);
        dest.writeString(name);
        dest.writeString(mediaId);
        dest.writeString(commentId);
        dest.writeString(questionAuthorName);
        dest.writeString(jid);
    }
}
