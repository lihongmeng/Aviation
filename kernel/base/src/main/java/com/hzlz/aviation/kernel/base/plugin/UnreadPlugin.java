package com.hzlz.aviation.kernel.base.plugin;

import com.hzlz.aviation.kernel.base.model.unread.MomentModel;
import com.hzlz.aviation.kernel.base.model.unread.NotificationModel;
import com.hzlz.aviation.library.ioc.Plugin;

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
