package com.hzlz.aviation.kernel.base.model.video;

import android.os.Parcel;
import android.os.Parcelable;

import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.base.tag.TagHelper;

import java.util.Date;

/**
 * 推荐模型
 */
public class RecommendModel implements Parcelable {
  /** 标题数据 */
  public final String title;
  /** 副标题 */
  public final String subTitle;
  /** 标题tag类型 */
  public final @TagHelper.GvideoTagType int tagType;
  /** 来源数据 */
  public final String author;
  /** 图片url */
  public final String imageUrl;
  /** 是否为广告，0否1是 */
  public final int isAd;
  /** 是否为人工添加，0否1是 */
  public final int isManual;
  /** 跳转url */
  public final String actUrl;

  /** 当推广位为音视频资源时此处展示mediaId */
  public final String mediaId;
  /** 资源类型 */
  public final @MediaType int mediaType;

  /** 关联mediaId */
  public String contentId;
  /** 素材id */
  public final String extendId;
  /** 素材类型 1为推广，0为资源*/
  public int extendType;

  public String specialId;
  public int pv;
  public Date createDate;

  public boolean isLongVideo() {
    return mediaType == MediaType.LONG_VIDEO || mediaType == MediaType.LONG_AUDIO;
  }

  public boolean isShortVideo() {
    return mediaType == MediaType.SHORT_VIDEO || mediaType == MediaType.SHORT_AUDIO;
  }

  public boolean isIMLive() {
    return mediaType == MediaType.IM_HORIZONTAL_LIVE || mediaType == MediaType.IM_VERTICAL_LIVE;
  }

  protected RecommendModel(String title, String subTitle, int tagType, String author, String imageUrl, int isAd,
      int isManual, String actUrl, String mediaId, int mediaType,
      String contentId, String extendId, int extendType,String specialId) {
    this.title = title;
    this.subTitle = subTitle;
    this.tagType = tagType;
    this.author = author;
    this.imageUrl = imageUrl;
    this.isAd = isAd;
    this.isManual = isManual;
    this.actUrl = actUrl;
    this.mediaId = mediaId;
    this.mediaType = mediaType;
    this.contentId = contentId;
    this.extendId = extendId;
    this.extendType = extendType;
    this.specialId = specialId;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.title);
    dest.writeString(this.subTitle);
    dest.writeInt(this.tagType);
    dest.writeString(this.author);
    dest.writeString(this.imageUrl);
    dest.writeInt(this.isAd);
    dest.writeInt(this.isManual);
    dest.writeString(this.actUrl);
    dest.writeString(this.mediaId);
    dest.writeInt(this.mediaType);
    dest.writeString(this.contentId);
    dest.writeString(this.extendId);
    dest.writeInt(this.extendType);
    dest.writeInt(this.pv);
    dest.writeLong(this.createDate!=null?this.createDate.getTime():-1);
    dest.writeString(this.specialId);
  }

  protected RecommendModel(Parcel in) {
    this.title = in.readString();
    this.subTitle = in.readString();
    this.tagType = in.readInt();
    this.author = in.readString();
    this.imageUrl = in.readString();
    this.isAd = in.readInt();
    this.isManual = in.readInt();
    this.actUrl = in.readString();
    this.mediaId = in.readString();
    this.mediaType = in.readInt();
    this.contentId = in.readString();
    this.extendId = in.readString();
    this.extendType = in.readInt();
    this.pv = in.readInt();
    this.createDate = in.readLong() == -1 ? null : new Date(in.readLong());
    this.specialId = in.readString();
  }

  public static final Creator<RecommendModel> CREATOR = new Creator<RecommendModel>() {
    @Override public RecommendModel createFromParcel(Parcel source) {
      return new RecommendModel(source);
    }

    @Override public RecommendModel[] newArray(int size) {
      return new RecommendModel[size];
    }
  };

  public static final class Builder {
    private String title;
    private String subTitle;
    private int tagType;
    private String author;
    private String imgUrl;
    private int isAd;
    private int isManual;
    private String actUrl;
    private String mediaId;
    private int mediaType;
    private String contentId;
    private String extendId;
    private int extendType;
    private String specialId;

    private Builder() {
    }

    public static Builder aRecommendModel() {
      return new Builder();
    }

    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder withSubTitle(String subTitle) {
      this.subTitle = subTitle;
      return this;
    }

    public Builder withTagType(int tagType) {
      this.tagType = tagType;
      return this;
    }

    public Builder withAuthor(String author) {
      this.author = author;
      return this;
    }

    public Builder withImageUrl(String imgUrl) {
      this.imgUrl = imgUrl;
      return this;
    }

    public Builder withIsAd(int isAd) {
      this.isAd = isAd;
      return this;
    }

    public Builder withIsManual(int isManual) {
      this.isManual = isManual;
      return this;
    }

    public Builder withActUrl(String actUrl) {
      this.actUrl = actUrl;
      return this;
    }

    public Builder withMediaId(String mediaId) {
      this.mediaId = mediaId;
      return this;
    }

    public Builder withMediaType(int mediaType) {
      this.mediaType = mediaType;
      return this;
    }

    public Builder withContentId(String contentId) {
      this.contentId = contentId;
      return this;
    }

    public Builder withExtendId(String extendId) {
      this.extendId = extendId;
      return this;
    }

    public Builder withExtendType(int extendType) {
      this.extendType = extendType;
      return this;
    }

    public RecommendModel build() {
      return new RecommendModel(title, subTitle, tagType, author, imgUrl, isAd, isManual,
          actUrl, mediaId, mediaType, contentId, extendId, extendType, specialId);
    }
  }
}
