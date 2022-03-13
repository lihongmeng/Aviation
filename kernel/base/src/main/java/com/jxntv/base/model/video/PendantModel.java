package com.jxntv.base.model.video;

import android.os.Parcel;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.jxntv.base.model.anotation.PendantShowType;

/**
 * 挂件模型
 */
public class PendantModel extends RecommendModel {

  /** 挂件前缀名 */
  public final String preTitle = "";
  /** 挂件是否一直显示  */
  public final @PendantShowType int isAlwaysExist = PendantShowType.NOT_ALWAYS_SHOW;
  /** 播放后开始显示时间 s为单位 */
  public final int showTime;
  /** 播放后持续时间 s为单位 */
  public final int showDuration = 5;

  protected PendantModel(String title, String subTitle, int tagType, String author,
      String imageUrl, int isAd, int isManual, String actUrl, String mediaId, int mediaType,
      String contentId, String extendId, int extendType, int showTime,String specialId) {
    super(title, subTitle, tagType, author, imageUrl, isAd, isManual, actUrl, mediaId, mediaType,
        contentId, extendId, extendType,specialId);
    this.showTime = showTime;
  }

  /**
   * 数据是否合法
   *
   * @return 判断数据合法性
   */
  public boolean isValid() {
    return !TextUtils.isEmpty(title) && !TextUtils.isEmpty(imageUrl)
        && (!TextUtils.isEmpty(actUrl) || !TextUtils.isEmpty(mediaId));
  }


  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeInt(this.showTime);
  }

  protected PendantModel(Parcel in) {
    super(in);
    this.showTime = in.readInt();
  }

  public static final Creator<PendantModel> CREATOR = new Creator<PendantModel>() {
    @Override public PendantModel createFromParcel(Parcel source) {
      return new PendantModel(source);
    }

    @Override public PendantModel[] newArray(int size) {
      return new PendantModel[size];
    }
  };

  public static class Builder {
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

    private int showTime;
    private String specialId;

    private Builder() {
    }

    public static Builder aPendantModel() {
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

    public Builder withShowTime(int showTime) {
      this.showTime = showTime;
      return this;
    }

    public Builder fromPendant(@NonNull PendantModel model) {
      this.title = model.title;
      this.subTitle = model.subTitle;
      this.tagType = model.tagType;
      this.author = model.author;
      this.imgUrl = model.imageUrl;
      this.isAd = model.isAd;
      this.isManual = model.isManual;
      this.actUrl = model.actUrl;
      this.mediaId = model.mediaId;
      this.mediaType = model.mediaType;
      this.contentId = model.contentId;
      this.extendId = model.extendId;
      this.extendType = model.extendType;
      this.showTime = model.showTime;
      this.specialId = model.specialId;
      return this;
    }

    public PendantModel build() {
      return new PendantModel(title, subTitle, tagType, author, imgUrl, isAd, isManual,
          actUrl, mediaId, mediaType, contentId, extendId, extendType, showTime, specialId);
    }
  }
}
