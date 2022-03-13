package com.jxntv.account.model;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

/**
 * 收藏资源响应
 *
 *
 * @since 2020-03-11 16:15
 */
public final class FavoriteMediaResponse {
  //<editor-fold desc="属性">
  @Nullable
  @SerializedName("id")
  private String mId;
  @SerializedName("isFavorite")
  private boolean mIsFavorited;
  //</editor-fold>

  //<editor-fold desc="Getter">

  @Nullable
  public String getId() {
    return mId;
  }

  public boolean isFavorited() {
    return mIsFavorited;
  }
  //</editor-fold>
}
