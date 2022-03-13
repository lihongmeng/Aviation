package com.jxntv.account.repository;

import androidx.annotation.NonNull;
import com.jxntv.account.model.HasUnreadMessageNotificationResponse;
import com.jxntv.account.model.Media;
import com.jxntv.account.request.GetMomentListRequest;
import com.jxntv.account.request.HasNewMomentRequest;
import com.jxntv.network.repository.BaseDataRepository;
import com.jxntv.network.repository.NetworkData;
import com.jxntv.network.repository.OneTimeNetworkData;
import com.jxntv.network.request.BaseRequest;
import com.jxntv.network.response.ListWithPage;
import io.reactivex.rxjava3.core.Observable;

/**
 * 动态仓库
 *
 *
 * @since 2020-02-17 22:07
 */
public final class MomentRepository extends BaseDataRepository {
  /**
   * 是否有新动态
   */
  @NonNull
  public Observable<HasUnreadMessageNotificationResponse> hasNewMoment() {
    return new OneTimeNetworkData<HasUnreadMessageNotificationResponse>(mEngine) {
      @Override
      protected BaseRequest<HasUnreadMessageNotificationResponse> createRequest() {
        return new HasNewMomentRequest();
      }
    }.asObservable();
  }
  /**
   * 获取动态列表
   *
   * @param pageNumber 分页编号
   * @param pageSize 分页大小
   */
  @NonNull
  public Observable<ListWithPage<Media>> getMomentList(int pageNumber, int pageSize) {
    return new NetworkData<ListWithPage<Media>>(mEngine) {
      @Override
      protected BaseRequest<ListWithPage<Media>> createRequest() {
        GetMomentListRequest request = new GetMomentListRequest();
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        return request;
      }

      @Override
      protected void saveData(ListWithPage<Media> mediaListWithPage) {
        if (mediaListWithPage != null && mediaListWithPage.getList() != null) {
          for (Media media : mediaListWithPage.getList()) {
            if(media==null){
              continue;
            }
            media.updateInteract();
          }
        }
      }
    }.asObservable();
  }
}
