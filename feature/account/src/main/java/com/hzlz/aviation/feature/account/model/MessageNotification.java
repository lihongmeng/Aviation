package com.hzlz.aviation.feature.account.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.hzlz.aviation.feature.account.model.annotation.NotificationType;
import com.hzlz.aviation.kernel.base.adapter.AbstractAdapterModel;

import java.util.Date;

/**
 * 消息通知模型
 *
 *
 * @since 2020-03-11 16:56
 */
public final class MessageNotification extends AbstractAdapterModel {
  //<editor-fold desc="属性">
  /** 来源id */
  @Nullable
  @SerializedName("fromId")
  private String mId;
  /** 头像 Url */
  @Nullable
  @SerializedName("avatar")
  private String mAvatarUrl;
  /** 通知标题 */
  @Nullable
  @SerializedName("title")
  private String mTitle;
  /** 通知内容 */
  @Nullable
  @SerializedName("content")
  private String mContent;
  /** 消息通知最新时间 */
  @Nullable
  @SerializedName("newestTime")
  private Date mNewestTime;
  /** 是否已读 */
  @SerializedName("unReadCount")
  private int mUnreadCount;
  @NotificationType
  @SerializedName("msgType")
  private int mMsgType;
  // 自定义属性
  @Nullable
  private transient MessageNotificationObservable mMessageNotificationObservable;
  //</editor-fold>

  //<editor-fold desc="API">
  public void update(@NonNull MessageNotification messageNotification) {
    setId(messageNotification.mId);
    setAvatarUrl(messageNotification.mAvatarUrl);
    setTitle(messageNotification.mTitle);
    setContent(messageNotification.mContent);
    setNewestTime(messageNotification.mNewestTime);
    setUnreadCount(messageNotification.mUnreadCount);
    setMsgType(messageNotification.mMsgType);
  }

  @NonNull
  public MessageNotificationObservable getMessageNotificationObservable() {
    if (mMessageNotificationObservable == null) {
      mMessageNotificationObservable = new MessageNotificationObservable();
      update(this);
    }
    return mMessageNotificationObservable;
  }

  //</editor-fold>

  //<editor-fold desc="Setter">

  public void setId(@Nullable String id) {
    mId = id;
  }

  public void setAvatarUrl(@Nullable String avatarUrl) {
    mAvatarUrl = avatarUrl;
    if (mMessageNotificationObservable != null) {
      mMessageNotificationObservable.setAvatarUrl(avatarUrl);
    }
  }

  public void setAvatarIcon(@Nullable int resId) {
    mAvatarUrl = "";
    if (mMessageNotificationObservable != null) {
      mMessageNotificationObservable.setAvatarUrl(resId);
    }
  }

  public void setTitle(@Nullable String title) {
    mTitle = title;
    if (mMessageNotificationObservable != null) {
      mMessageNotificationObservable.setTitle(title);
    }
  }

  public void setContent(@Nullable String content) {
    mContent = content;
    if (mMessageNotificationObservable != null) {
      mMessageNotificationObservable.setContent(content);
    }
  }

  public void setNewestTime(@Nullable Date newestTime) {
    mNewestTime = newestTime;
    if (mMessageNotificationObservable != null) {
      mMessageNotificationObservable.setNewestTime(newestTime);
    }
  }

  public void setUnreadCount(int unreadCount) {
    mUnreadCount = unreadCount;
    if (mMessageNotificationObservable != null) {
      mMessageNotificationObservable.setHasRead(unreadCount <= 0);
      mMessageNotificationObservable.setUnreadCount(unreadCount>99?"···":unreadCount+"");
    }
  }

  public int getMsgType() {
    return mMsgType;
  }

  public void setMsgType(int mMsgType) {
    this.mMsgType = mMsgType;
  }


//</editor-fold>

  //<editor-fold desc="Getter">

  @Nullable
  public String getId() {
    return mId;
  }

  @Nullable
  public String getAvatarUrl() {
    return mAvatarUrl;
  }

  @Nullable
  public String getTitle() {
    return mTitle;
  }

  @Nullable
  public String getContent() {
    return mContent;
  }

  @Nullable
  public Date getNewestTime() {
    return mNewestTime;
  }

  public boolean hasRead() {
    return mUnreadCount <= 0;
  }
  //</editor-fold>
}
