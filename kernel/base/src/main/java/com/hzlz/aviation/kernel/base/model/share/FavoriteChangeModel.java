package com.hzlz.aviation.kernel.base.model.share;

/**
 * 喜欢状态变化通知实体
 */
public class FavoriteChangeModel {
  public final String mediaId;
  public final boolean favorite;

  public FavoriteChangeModel(String mediaId, boolean favorite) {
    this.mediaId = mediaId;
    this.favorite = favorite;
  }
}
