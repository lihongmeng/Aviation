package com.hzlz.aviation.kernel.base.model.unread;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.model.video.AuthorModel;

import java.util.Date;

/**
 * 通知中心小红点
 */
public class NotificationModel implements Parcelable {
  public final long id;
  @NonNull
  public final AuthorModel author;
  @NonNull
  public final Date date;
  @NonNull
  public final String content;

  private NotificationModel(long id, @NonNull AuthorModel author, @NonNull Date date,
      @NonNull String content) {
    this.id = id;
    this.author = author;
    this.date = date;
    this.content = content;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeParcelable(this.author, flags);
    dest.writeLong(this.date != null ? this.date.getTime() : -1);
    dest.writeString(this.content);
  }

  protected NotificationModel(Parcel in) {
    this.id = in.readLong();
    this.author = in.readParcelable(AuthorModel.class.getClassLoader());
    long tmpDate = in.readLong();
    this.date = tmpDate == -1 ? null : new Date(tmpDate);
    this.content = in.readString();
  }

  public static final Parcelable.Creator<NotificationModel> CREATOR =
      new Parcelable.Creator<NotificationModel>() {
        @Override public NotificationModel createFromParcel(Parcel source) {
          return new NotificationModel(source);
        }

        @Override public NotificationModel[] newArray(int size) {
          return new NotificationModel[size];
        }
      };

  public static final class Builder {
    private long id;
    private AuthorModel author;
    private Date date;
    private String content;

    private Builder() {
    }

    public static Builder aNotificationModel() {
      return new Builder();
    }

    public Builder withId(long id) {
      this.id = id;
      return this;
    }

    public Builder withAuthor(AuthorModel author) {
      this.author = author;
      return this;
    }

    public Builder withDate(Date date) {
      this.date = date;
      return this;
    }

    public Builder withContent(String content) {
      this.content = content;
      return this;
    }

    public NotificationModel build() {
      return new NotificationModel(id, author, date, content);
    }
  }
}
