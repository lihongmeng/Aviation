package com.jxntv.account.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import com.jxntv.base.adapter.AbstractAdapterModel;

/**
 * 头像信息
 *
 *
 * @since 2020-02-24 20:53
 */
public final class AvatarInfo extends AbstractAdapterModel implements Parcelable {

  //<editor-fold desc="属性">

  @SerializedName("id")
  private String mId;

  @SerializedName("url")
  private String mUrl;

  // 自定义属性
  @Nullable
  private transient AvatarInfoObservable mAvatarInfoObservable;

  public AvatarInfo(){

  }

  public AvatarInfo(String mId, String mUrl) {
    this.mId = mId;
    this.mUrl = mUrl;
  }

  //</editor-fold>

  //<editor-fold desc="Setter">

  protected AvatarInfo(Parcel in) {
    mId = in.readString();
    mUrl = in.readString();
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(mId);
    dest.writeString(mUrl);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<AvatarInfo> CREATOR = new Creator<AvatarInfo>() {
    @Override
    public AvatarInfo createFromParcel(Parcel in) {
      return new AvatarInfo(in);
    }

    @Override
    public AvatarInfo[] newArray(int size) {
      return new AvatarInfo[size];
    }
  };

  public void setId(@Nullable String id) {
    mId = id;
  }

  public void setUrl(@Nullable String url) {
    mUrl = url;
    if (mAvatarInfoObservable != null) {
      mAvatarInfoObservable.url.set(url);
    }
  }

  //</editor-fold>

  //<editor-fold desc="Getter">

  @Nullable
  public String getId() {
    return mId;
  }

  @Nullable
  public String getUrl() {
    return mUrl;
  }

  //</editor-fold>

  //<editor-fold desc="API">

  public void update(@NonNull AvatarInfo avatarInfo) {
    setId(avatarInfo.mId);
    setUrl(avatarInfo.mUrl);
  }

  @NonNull
  public AvatarInfoObservable getAvatarInfoObservable() {
    if (mAvatarInfoObservable == null) {
      mAvatarInfoObservable = new AvatarInfoObservable();
      update(this);
    }
    return mAvatarInfoObservable;
  }

  //</editor-fold>



}
