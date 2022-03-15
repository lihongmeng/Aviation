package com.hzlz.aviation.feature.account.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * 关注作者响应
 *
 *
 * @since 2020-03-03 18:01
 */
public final class FollowAuthorResponse {
  //<editor-fold desc="属性">
  @SerializedName("id")
  @Nullable
  private String mId;
  @SerializedName("isFollow")
  private boolean mIsFollowed;
  //</editor-fold>

  //<editor-fold desc="Getter">

  @Nullable
  public String getId() {
    return mId;
  }

  public boolean isFollowed() {
    return mIsFollowed;
  }

  //</editor-fold>
}
