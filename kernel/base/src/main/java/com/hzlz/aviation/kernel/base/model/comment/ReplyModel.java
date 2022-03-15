package com.hzlz.aviation.kernel.base.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.hzlz.aviation.kernel.base.model.video.AuthorModel;

import java.util.Date;
import java.util.List;

public class ReplyModel implements Parcelable {
  public final long replyId;
  public final Date replyDate;
  public final String replyContent;
  public final AuthorModel user;
  public final AuthorModel toUser;
  /** 评论图片资源 */
  public final List<String> imageList;
  public final List<String> oriImageList;
  /** 评论语音资源 */
  public String soundUrl;
  /** 语音时长 */
  public final int length;
  /** 语音文字 */
  public final String soundContent;
  /** 该评论关联的资源是否支持评论，如果不支持需要隐藏掉对应的回复、删除按钮 */
  private boolean canComment = true;
  /** 是否点赞*/
  private boolean isPraise;
  /** 点赞数量*/
  private int praiseTotal;

  @NonNull
  private ObservableField<Integer> praiseTotalFiled;
  @NonNull
  private ObservableBoolean isPraiseFiled;

  public ReplyModel(long replyId, Date replyDate, String replyContent, AuthorModel user,
                    AuthorModel toUser,List<String> imageList, List<String> oriImageList,String soundUrl,
                    int length,String soundContent, boolean isPraise, int praiseTotal) {
    this.replyId = replyId;
    this.replyDate = replyDate;
    this.replyContent = replyContent;
    this.user = user;
    this.toUser = toUser;
    this.imageList = imageList;
    this.oriImageList = oriImageList;
    this.soundUrl = soundUrl;
    this.length = length;
    this.soundContent = soundContent;
    this.isPraise = isPraise;
    this.praiseTotal = praiseTotal;
  }

  public boolean canComment() {
    return canComment;
  }

  public void setCanComment(boolean canComment) {
    this.canComment = canComment;
  }

  public void setPraise(boolean praise) {
    if (isPraiseFiled==null){
      isPraiseFiled = new ObservableBoolean();
    }
    isPraiseFiled.set(praise);
    this.isPraise = praise;
  }

  public void setPraiseTotal(int praiseTotal) {
    if (praiseTotalFiled==null){
      praiseTotalFiled = new ObservableField<>();
    }
    praiseTotalFiled.set(praiseTotal);
    this.praiseTotal = praiseTotal;
  }

  @NonNull
  public ObservableField<Integer> getPraiseTotalFiled() {
    setPraiseTotal(praiseTotal);
    return praiseTotalFiled;
  }

  @NonNull
  public ObservableBoolean getIsPraiseFiled() {
    setPraise(isPraise);
    return isPraiseFiled;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.replyId);
    dest.writeLong(this.replyDate != null ? this.replyDate.getTime() : -1);
    dest.writeString(this.replyContent);
    dest.writeParcelable(this.user, flags);
    dest.writeParcelable(this.toUser, flags);
    dest.writeInt(this.canComment ? 1 : 0);
    dest.writeList(this.imageList);
    dest.writeList(this.oriImageList);
    dest.writeString(this.soundUrl);
    dest.writeInt(this.length);
    dest.writeString(this.soundContent);
    dest.writeInt(this.isPraise?1:0);
    dest.writeInt(this.praiseTotal);
  }

  protected ReplyModel(Parcel in) {
    this.replyId = in.readLong();
    long tmpReplyDate = in.readLong();
    this.replyDate = tmpReplyDate == -1 ? null : new Date(tmpReplyDate);
    this.replyContent = in.readString();
    this.user = in.readParcelable(AuthorModel.class.getClassLoader());
    this.toUser = in.readParcelable(AuthorModel.class.getClassLoader());
    this.canComment = in.readInt() > 0;
    this.imageList = in.createStringArrayList();
    this.oriImageList = in.createStringArrayList();
    this.soundUrl = in.readString();
    this.length = in.readInt();
    this.soundContent = in.readString();
    this.isPraise = in.readInt()>0;
    this.praiseTotal = in.readInt();

  }

  public static final Creator<ReplyModel> CREATOR =
      new Creator<ReplyModel>() {
        @Override public ReplyModel createFromParcel(Parcel source) {
          return new ReplyModel(source);
        }

        @Override public ReplyModel[] newArray(int size) {
          return new ReplyModel[size];
        }
      };

  public static final class Builder {
    private long replyId;
    private Date replyDate;
    private String replyContent;
    private AuthorModel user;
    private AuthorModel toUser;
    private List<String> imageList;
    private List<String> oriImageList;
    private String soundUrl;
    private int length;
    private String soundContent;
    private boolean isPraise;
    private int praiseTotal;


    private Builder() {
    }

    public static Builder aReplyModel() {
      return new Builder();
    }

    public Builder withReplyId(long replyId) {
      this.replyId = replyId;
      return this;
    }

    public Builder withReplyDate(Date replyDate) {
      this.replyDate = replyDate;
      return this;
    }

    public Builder withReplyContent(String replyContent) {
      this.replyContent = replyContent;
      return this;
    }

    public Builder withUser(AuthorModel user) {
      this.user = user;
      return this;
    }

    public Builder withToUser(AuthorModel toUser) {
      this.toUser = toUser;
      return this;
    }


    public Builder withImageList(List<String> imageList) {
      this.imageList = imageList;
      return this;
    }

    public Builder withSoundUrl(String soundUrl) {
      this.soundUrl = soundUrl;
      return this;
    }

    public Builder withLength(int length) {
      this.length = length;
      return this;
    }

    public Builder withSoundContent(String soundContent) {
      this.soundContent = soundContent;
      return this;
    }

    public ReplyModel build() {
      return new ReplyModel(replyId, replyDate, replyContent, user, toUser,imageList,oriImageList,
              soundUrl,length,soundContent,isPraise,praiseTotal);
    }
  }
}
