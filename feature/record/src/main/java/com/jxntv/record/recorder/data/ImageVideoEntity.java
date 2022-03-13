package com.jxntv.record.recorder.data;

/**
 * 视频实体类
 */
public class ImageVideoEntity {
  /** 视频或图片存储地址 */
  public String path;
  /** 视频id */
  public long id;
  /** 视频名称 */
  public String name;
  /** 视频分辨率 */
  public String resolution;
  /** 视频大小 */
  public long size;
  /** 视频时长 */
  public long duration;
  /** 视频选择次序 */
  public int selectPosition = -1;
  /** 视频修改时间戳 */
  public long modifiedTime = -1;
  /** 是否是视频*/
  public boolean isVideo = true;
  /** 是否是拍照按钮 */
  public boolean isTakePhoto = false;

}
