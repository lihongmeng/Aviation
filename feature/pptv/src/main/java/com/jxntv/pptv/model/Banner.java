package com.jxntv.pptv.model;

import com.google.gson.annotations.SerializedName;

public final class Banner {
  @SerializedName("mediaId")
  private String id;
  @SerializedName("title")
  private String name;
  @SerializedName("imageUrl")
  private String cover;
  @SerializedName("actUrl")
  private String actUrl;
  @SerializedName("mediaType")
  private int type;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public String getActUrl() {
    return actUrl;
  }

  public void setActUrl(String actUrl) {
    this.actUrl = actUrl;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

}
