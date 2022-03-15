package com.hzlz.aviation.feature.account.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.hzlz.aviation.kernel.base.adapter.AbstractAdapterModel;

/**
 * 收藏模型
 *
 *
 * @since 2020-02-12 15:20
 */
public final class Favorite extends AbstractAdapterModel {
  //<editor-fold desc="属性">
  @Nullable
  @SerializedName("id")
  private String mId;
  @Nullable
  @SerializedName("coverImageUrl")
  private String mCoverUrl;
  @Nullable
  @SerializedName("name")
  private String mGroupName;
  @SerializedName("count")
  private int mCount;
  @NonNull
  @SerializedName("detailText")
  private String mDetailText;
  @SerializedName("vertical")
  private boolean mIsVertical;

  // 定义属性
  @Nullable
  private transient FavoriteObservable mFavoriteObservable;
  //</editor-fold>

  //<editor-fold desc="Setter">

  public void setIsVertical(boolean isVertical) {
    mIsVertical = isVertical;
    if (mFavoriteObservable != null) {
      mFavoriteObservable.setIsVertical(isVertical);
    }
  }

  public void setDetailText(String detailText) {
    mDetailText = detailText;
    if (mFavoriteObservable != null) {
      mFavoriteObservable.setDetailText(detailText);
    }
  }

  public void setCoverUrl(@Nullable String coverUrl) {
    mCoverUrl = coverUrl;
    if (mFavoriteObservable != null) {
      mFavoriteObservable.setCoverUrl(coverUrl);
    }
  }

  public void setGroupName(@Nullable String groupName) {
    mGroupName = groupName;
    if (mFavoriteObservable != null) {
      mFavoriteObservable.setGroupName(groupName);
      mFavoriteObservable.setCount(groupName, mCount);
    }
  }

  public void setCount(int count) {
    mCount = count;
    if (mFavoriteObservable != null) {
      mFavoriteObservable.setCount(mGroupName, count);
    }
  }
  //</editor-fold>

  //<editor-fold desc="Getter">

  @Nullable
  public String getId() {
    return mId;
  }

  @Nullable
  public String getCoverUrl() {
    return mCoverUrl;
  }

  @Nullable
  public String getGroupName() {
    return mGroupName;
  }

  public int getCount() {
    return mCount;
  }

  public String getDetailText() {
    return mDetailText;
  }

  public boolean isVertical() {
    return mIsVertical;
  }

  //</editor-fold>

  //<editor-fold desc="API">
  public void update(@NonNull Favorite favorite) {
    setCoverUrl(favorite.mCoverUrl);
    setGroupName(favorite.mGroupName);
    setCount(favorite.mCount);
    setIsVertical(favorite.mIsVertical);
    setDetailText(favorite.mDetailText);
  }

  @NonNull
  public FavoriteObservable getFavoriteObservable() {
    if (mFavoriteObservable == null) {
      mFavoriteObservable = new FavoriteObservable();
      update(this);
    }
    return mFavoriteObservable;
  }

  //</editor-fold>
}
