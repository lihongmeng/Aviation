package com.jxntv.base.model.comment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.jxntv.base.model.video.AuthorModel;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CommentModel implements Parcelable {
  public final long primaryId;
  public final AuthorModel commentUser;
  public final Date commentDate;
  public final String content;
  public final List<ReplyModel> replies;
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
  private ObservableField<String> praiseTotalFiled;
  @NonNull
  private ObservableBoolean isPraiseFiled;

  public CommentModel(long primaryId, AuthorModel commentUser, Date commentDate, String content,
      List<ReplyModel> replies,List<String> imageList,List<String> oriImageList, String soundUrl,int length,String soundContent,boolean isPraise, int praiseTotal) {
    this.primaryId = primaryId;
    this.commentUser = commentUser;
    this.commentDate = commentDate;
    this.content = content;
    this.replies = replies;
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
    praiseTotalFiled.set(praiseTotal+"");
    this.praiseTotal = praiseTotal;
  }

  @NonNull
  public ObservableField<String> getPraiseTotalFiled() {
    setPraiseTotal(praiseTotal);
    return praiseTotalFiled;
  }

  @NonNull
  public ObservableBoolean getIsPraiseFiled() {
    setPraise(isPraise);
    return isPraiseFiled;
  }


  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommentModel that = (CommentModel) o;
    return primaryId == that.primaryId &&
        canComment == that.canComment &&
        (Objects.equals(commentUser, that.commentUser)) &&
        (Objects.equals(commentDate, that.commentDate)) &&
        ((content.equals(that.content)) || (content != null && content.equals(that.content))) &&
        (Objects.equals(replies, that.replies))&&
        (Objects.equals(imageList, that.imageList))&&
        (Objects.equals(oriImageList, that.oriImageList))&&
        (Objects.equals(soundUrl, that.soundUrl))&&
        length == that.length &&
        (Objects.equals(soundContent, that.soundContent));

  }

  @Override public int hashCode() {
    Object[] objects = { primaryId, commentUser, commentDate, content, replies, canComment ,imageList, oriImageList,soundUrl, length, soundContent};
    return Arrays.hashCode(objects);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.primaryId);
    dest.writeParcelable(this.commentUser, flags);
    dest.writeLong(this.commentDate != null ? this.commentDate.getTime() : -1);
    dest.writeString(this.content);
    dest.writeTypedList(this.replies);
    dest.writeInt(this.canComment ? 1 : 0);
    dest.writeList(this.imageList);
    dest.writeList(this.oriImageList);
    dest.writeString(this.soundUrl);
    dest.writeInt(this.length);
    dest.writeString(this.soundContent);
    dest.writeInt(this.isPraise ?1:0);
    dest.writeInt(this.praiseTotal);
  }

  protected CommentModel(Parcel in) {
    this.primaryId = in.readLong();
    this.commentUser = in.readParcelable(AuthorModel.class.getClassLoader());
    long tmpDate = in.readLong();
    this.commentDate = tmpDate == -1 ? null : new Date(tmpDate);
    this.content = in.readString();
    this.replies = in.createTypedArrayList(ReplyModel.CREATOR);
    this.canComment = in.readInt() > 0;
    this.imageList = in.createStringArrayList();
    this.oriImageList = in.createStringArrayList();
    this.soundUrl = in.readString();
    this.length = in.readInt();
    this.soundContent = in.readString();
    this.isPraise = in.readInt()>0;
    this.praiseTotal = in.readInt();
  }

  public static final Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
    @Override public CommentModel createFromParcel(Parcel source) {
      return new CommentModel(source);
    }

    @Override public CommentModel[] newArray(int size) {
      return new CommentModel[size];
    }
  };

  public static final class Builder {
    private long id;
    private AuthorModel fromUser;
    private Date date;
    private String content;
    private List<ReplyModel> replyComment;
    private List<String> imageList;
    private List<String> oriImageList;
    private String soundUrl;
    private int length;
    private String soundContent;
    private boolean isPraise;
    private int praiseTotal;


    private Builder() {
    }

    public static Builder aCommentModel() {
      return new Builder();
    }

    public Builder fromComment(@NonNull CommentModel model) {
      this.id = model.primaryId;
      this.fromUser = model.commentUser;
      this.date = model.commentDate;
      this.content = model.content;
      this.replyComment = model.replies;
      this.imageList = model.imageList;
      this.oriImageList = model.oriImageList;
      this.soundUrl = model.soundUrl;
      this.length = model.length;
      this.soundContent = model.soundContent;        ;
      return this;
    }

    public Builder withId(long id) {
      this.id = id;
      return this;
    }

    public Builder withFromUser(AuthorModel author) {
      this.fromUser = author;
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

    public Builder withReplyComment(List<ReplyModel> replyComment) {
      this.replyComment = replyComment;
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

    public CommentModel build() {
      return new CommentModel(id, fromUser, date, content, replyComment,imageList,oriImageList,
              soundUrl,length, soundContent,isPraise,praiseTotal);
    }
  }


}
