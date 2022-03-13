package com.jxntv.account.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.account.BR;
import com.jxntv.account.R;
import com.jxntv.account.model.MessageNotification;
import com.jxntv.account.model.annotation.NotificationType;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;

/**
 * 消息通知适配器
 *
 *
 * @since 2020-03-11 18:38
 */
public final class MessageNotificationAdapter extends BaseDataBindingAdapter<MessageNotification> {
  //<editor-fold desc="属性">
  @Nullable
  private Listener mListener;
  //</editor-fold>

  //<editor-fold desc="API">

  public void setListener(@Nullable Listener listener) {
    mListener = listener;
  }

  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getItemLayoutId() {
    return R.layout.adapter_message_notification;
  }

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    MessageNotification notification = mDataList.get(position);
    switch (notification.getMsgType()){
      case NotificationType.SYSTEM:
        notification.setAvatarIcon(R.drawable.ic_notification_system);
        break;
      case NotificationType.INTERACTIVE:
        notification.setAvatarIcon(R.drawable.ic_notification_interactive);
        break;
      case NotificationType.FOLLOW:
        notification.setAvatarIcon(R.drawable.ic_notification_follow);
        break;
      case NotificationType.QA:
        notification.setAvatarIcon(R.drawable.ic_notification_qa);
        break;
    }
//    if (TextUtils.equals(notification.getId(), "0")) {
//      notification.setAvatarIcon(R.drawable.ic_notification);
//    }
    holder.bindData(BR.adapter, this);
    holder.bindData(BR.position, notification.getModelPosition());
    holder.bindData(BR.notification, notification.getMessageNotificationObservable());
  }
  //</editor-fold>

  //<editor-fold desc="接口">
  public interface Listener {
    void onItemClick(@NonNull MessageNotificationAdapter adapter, @NonNull View view, int position);
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void onItemClick(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onItemClick(this, view, position);
    }
  }
  //</editor-fold>
}
