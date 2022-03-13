package com.jxntv.account.model;

import com.google.gson.annotations.SerializedName;

/**
 * 今视频文件
 *
 *
 * @since 2020-03-04 17:36
 */
public final class GVideoFile {
  //<editor-fold desc="属性">

  @SerializedName("id")
  private String mId;

  @SerializedName("url")
  private String mUploadUrl;
  //</editor-fold>

  //<editor-fold desc="Getter">

  public String getId() {
    return mId;
  }

  public String getUploadUrl() {
    return mUploadUrl;
  }

  //</editor-fold>
}
