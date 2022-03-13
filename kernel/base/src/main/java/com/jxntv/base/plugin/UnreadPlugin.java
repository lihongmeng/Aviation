package com.jxntv.base.plugin;

import com.jxntv.base.model.unread.MomentModel;
import com.jxntv.base.model.unread.NotificationModel;
import com.jxntv.ioc.Plugin;
import java.util.List;

/**
 * Unread模块接口
 */
public interface UnreadPlugin extends Plugin {
  String EVENT_UNREAD_MOMENT = "unread_moment";
  String EVENT_UNREAD_NOTIFICATION = "unread_notification";
  void checkUnread(boolean force);
  void setMoments(List<MomentModel> moments);
  void setNotifications(List<NotificationModel> notifications);
}
