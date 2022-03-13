package com.jxntv.base.model.share;

/**
 * 关注状态变化通知实体
 */
public class FollowChangeModel {
  public final String authorId;
  public final boolean follow;

  public FollowChangeModel(String authorId, boolean follow) {
    this.authorId = authorId;
    this.follow = follow;
  }
}
