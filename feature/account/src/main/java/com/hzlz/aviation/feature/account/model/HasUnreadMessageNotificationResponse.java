package com.hzlz.aviation.feature.account.model;

import com.google.gson.annotations.SerializedName;

/**
 * 是否有未读消息响应
 *
 *
 * @since 2020-03-12 16:33
 */
public final class HasUnreadMessageNotificationResponse {
  //<editor-fold desc="属性">
  @SerializedName("hasUnread")
  private boolean mHasUnread;

  @SerializedName("unreadCount")
  private int mUnreadCount;
  //</editor-fold>

  //<editor-fold desc="Getter">
  public boolean hasUnread() {
    return mHasUnread;
  }

  public int getUnreadCount(){ return mUnreadCount; }
  //</editor-fold>
}
