package com.hzlz.aviation.feature.record.recorder.model;

import com.google.gson.annotations.SerializedName;

/**
 * vod视频数据模型
 */
public final class VodVideoCreateModel {
  /** 视频id */
  @SerializedName("videoId")
  private String mVideoId;
  /** 上传地址 */
  @SerializedName("uploadAddress")
  private String mUploadAddress;
  /** 上传auth */
  @SerializedName("uploadAuth")
  private String mUploadAuth;

  /**
   * 获取视频id
   *
   * @return 视频Id
   */
  public String getVideoId() {
    return mVideoId;
  }

  /**
   * 设置视频id
   *
   * @param videoId 视频Id
   */
  public void setVideoId(String videoId) {
    this.mVideoId = videoId;
  }

  /**
   * 获取上传地址
   *
   * @return 上传地址
   */
  public String getUploadAddress() {
    return mUploadAddress;
  }

  /**
   * 设置上传地址
   *
   * @param uploadAddress 上传地址
   */
  public void setUploadAddress(String uploadAddress) {
    this.mUploadAddress = uploadAddress;
  }

  /**
   * 获取上传auth
   *
   * @return 上传auth
   */
  public String getUploadAuth() {
    return mUploadAuth;
  }

  /**
   * 设置上传auth
   *
   * @param uploadAuth 上传auth
   */
  public void setUploadAuth(String uploadAuth) {
    this.mUploadAuth = uploadAuth;
  }
}
