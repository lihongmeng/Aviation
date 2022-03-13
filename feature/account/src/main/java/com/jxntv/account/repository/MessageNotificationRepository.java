package com.jxntv.account.repository;

import androidx.annotation.NonNull;
import com.jxntv.account.model.HasUnreadMessageNotificationResponse;
import com.jxntv.account.model.MessageNotification;
import com.jxntv.account.model.MessageNotificationDetail;
import com.jxntv.account.request.GetMessageNotificationDetailListRequest;
import com.jxntv.account.request.GetMessageNotificationListRequest;
import com.jxntv.account.request.HasUnreadMessageNotificationRequest;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.repository.OneTimeNetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.network.response.ListWithPage;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;

/**
 * 消息通知仓库
 *
 *
 * @since 2020-03-11 20:08
 */
public final class MessageNotificationRepository extends BaseDataRepository {
  /**
   * 是否有未读消息
   */
  @NonNull
  public Observable<HasUnreadMessageNotificationResponse> hasUnreadMessageNotification() {
    return new OneTimeNetworkData<HasUnreadMessageNotificationResponse>(mEngine) {
      @Override
      protected BaseRequest<HasUnreadMessageNotificationResponse> createRequest() {
        return new HasUnreadMessageNotificationRequest();
      }
    }.asObservable();
  }

  /**
   * 获取消息通知列表
   *
   * @param pageNumber 分页编号
   * @param pageSize 分页大小
   */
  @NonNull
  public Observable<ListWithPage<MessageNotification>> getMessageNotificationList(
      int pageNumber,
      int pageSize) {
    return new NetworkData<ListWithPage<MessageNotification>>(mEngine) {
      @Override
      protected BaseRequest<ListWithPage<MessageNotification>> createRequest() {
        GetMessageNotificationListRequest request = new GetMessageNotificationListRequest();
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        return request;
      }

      @Override
      protected void saveData(ListWithPage<MessageNotification> messageNotificationListWithPage) {
        // 生成 MessageNotificationObservable
        List<MessageNotification> list = messageNotificationListWithPage.getList();
        for (MessageNotification notification : list) {
          notification.getMessageNotificationObservable();
        }
      }
    }.asObservable();
  }

  /**
   * 获取消息通知详情列表
   *
   * @param sourceId 消息类型
   * @param pageNumber 分页编号
   * @param pageSize 分页大小
   */
  @NonNull
  public Observable<ListWithPage<MessageNotificationDetail>> getMessageNotificationDetailList(
      int sourceId,
      int pageNumber,
      int pageSize) {
    return new NetworkData<ListWithPage<MessageNotificationDetail>>(mEngine) {
      @Override
      protected BaseRequest<ListWithPage<MessageNotificationDetail>> createRequest() {
        GetMessageNotificationDetailListRequest request =
            new GetMessageNotificationDetailListRequest();
        request.setSourceId(sourceId);
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        return request;
      }

      @Override
      protected void saveData(ListWithPage<MessageNotificationDetail> listWithPage) {
        // 生成 MessageNotificationObservable
        List<MessageNotificationDetail> list = listWithPage.getList();
        for (MessageNotificationDetail notification : list) {
          notification.getMessageNotificationDetailObservable();
        }
      }
    }.asObservable();
  }
}
