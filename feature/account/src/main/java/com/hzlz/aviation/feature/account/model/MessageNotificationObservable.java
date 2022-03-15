package com.hzlz.aviation.feature.account.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.hzlz.aviation.library.util.DateUtils;

import java.util.Date;

/**
 *
 * @since 2020-03-11 18:30
 */
public final class MessageNotificationObservable {
  //<editor-fold desc="属性">
  /** 头像 Url */
  @NonNull
  public ObservableField<Object> avatarUrl = new ObservableField<>();
  /** 通知标题 */
  @NonNull
  public ObservableField<String> title = new ObservableField<>();
  /** 通知内容 */
  @NonNull
  public ObservableField<String> content = new ObservableField<>();
  /** 消息通知最新时间 */
  @NonNull
  public ObservableField<String> newestTime = new ObservableField<>();
  /** 是否已读 */
  public ObservableBoolean hasRead = new ObservableBoolean();
  public ObservableField<String> unreadCount = new ObservableField();
  //</editor-fold>

  //<editor-fold desc="API">

  public void setAvatarUrl(@Nullable Object avatarUrl) {
    this.avatarUrl.set(avatarUrl);
  }

  public void setTitle(@Nullable String title) {
    this.title.set(title);
  }

  public void setContent(@Nullable String content) {
    this.content.set(content);
  }

  public void setNewestTime(@Nullable Date newestTime) {
    if (newestTime == null) {
      this.newestTime.set(null);
    } else {
      this.newestTime.set(DateUtils.friendlyTime2(newestTime));
    }
  }

  public void setHasRead(boolean hasRead) {
    this.hasRead.set(hasRead);
  }

  public void setUnreadCount(String unreadCount) {
    this.unreadCount.set(unreadCount);
  }

  //</editor-fold>
}
