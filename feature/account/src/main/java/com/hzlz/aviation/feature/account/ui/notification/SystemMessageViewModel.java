package com.hzlz.aviation.feature.account.ui.notification;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

import com.hzlz.aviation.feature.account.adapter.MessageNotificationAdapter;
import com.hzlz.aviation.feature.account.model.MessageNotification;
import com.hzlz.aviation.feature.account.repository.MessageNotificationRepository;
import com.hzlz.aviation.kernel.base.BaseRefreshLoadMoreViewModel;
import com.hzlz.aviation.kernel.base.adapter.BaseDataBindingAdapter;
import com.hzlz.aviation.kernel.base.adapter.IAdapterModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.library.widget.widget.IGVideoRefreshLoadMoreView;

import java.util.List;

/**
 * 消息通知 ViewModel
 *
 *
 * @since 2020-03-11 16:53
 */
public final class SystemMessageViewModel extends BaseRefreshLoadMoreViewModel
    implements MessageNotificationAdapter.Listener {
  //<editor-fold desc="属性">
  @NonNull
  private MessageNotificationAdapter mAdapter = new MessageNotificationAdapter();
  //
  @NonNull
  private MessageNotificationRepository mMessageNotificationRepository = new MessageNotificationRepository();
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public SystemMessageViewModel(@NonNull Application application) {
    super(application);
    mAdapter.setListener(this);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @NonNull
  @Override
  protected BaseDataBindingAdapter<? extends IAdapterModel> createAdapter() {
    return mAdapter;
  }

  @Override
  public void onRefresh(@NonNull IGVideoRefreshLoadMoreView view) {
    super.onRefresh(view);
    mMessageNotificationRepository
        .getMessageNotificationList(1, mLocalPage.getPageSize())
        .subscribe(new GVideoRefreshObserver<>(view));
  }

  @Override
  public void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view) {
    super.onLoadMore(view);
    mMessageNotificationRepository
        .getMessageNotificationList(mLocalPage.getPageNumber(), mLocalPage.getPageSize())
        .subscribe(new GVideoLoadMoreObserver<>(view));
  }

  //</editor-fold>

  //<editor-fold desc="控件事件监听">
  @Override
  public void onItemClick(@NonNull MessageNotificationAdapter adapter, @NonNull View view, int position) {
    MessageNotification notification = mAdapter.getData().get(position);
    if (notification.getId() == null) {
      return;
    }
    // 更新已读
    notification.setUnreadCount(0);
    // 跳转
    String title = notification.getTitle();
    if (title == null) {
      title = "";
    }
    Navigation.findNavController(view).navigate(
        MessageNotificationFragmentDirections.actionMessageNotificationToMessageNotificationDetail(
            notification.getMsgType(),title)
    );
  }
  //</editor-fold>

  //<editor-fold desc="生命周期">
  @Override
  protected void onCleared() {
    super.onCleared();
    // 判断是否还有未读消息，并通知其他界面
    boolean hasUnreadMessageNotification = false;
    List<MessageNotification> messageNotificationList = mAdapter.getData();
    for (MessageNotification notification : messageNotificationList) {
      hasUnreadMessageNotification = !notification.hasRead();
      if (hasUnreadMessageNotification) {
        break;
      }
    }
    GVideoEventBus.get(AccountPlugin.EVENT_UNREAD_NOTIFICATION)
        .post(hasUnreadMessageNotification);
  }
  //</editor-fold>
}
