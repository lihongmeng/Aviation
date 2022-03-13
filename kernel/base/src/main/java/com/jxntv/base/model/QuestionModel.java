package com.jxntv.base.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.jxntv.base.model.video.AuthorModel;

import java.util.List;

/**
 * 提问
 */
public class QuestionModel implements Parcelable {

    public String title;
    /**
     * 提问内容
     */
    public String content;

    public AuthorModel author;

    protected QuestionModel(Parcel in) {
        title = in.readString();
        content = in.readString();
        author = in.readParcelable(AuthorModel.class.getClassLoader());
    }

    public static final Creator<QuestionModel> CREATOR = new Creator<QuestionModel>() {
        @Override
        public QuestionModel createFromParcel(Parcel in) {
            return new QuestionModel(in);
        }

        @Override
        public QuestionModel[] newArray(int size) {
            return new QuestionModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AuthorModel getAuthor() {
        return author;
    }

    public void setAuthor(AuthorModel author) {
        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(content);
        parcel.writeParcelable(author, i);
    }
}
