package com.hzlz.aviation.feature.account.repository;

import androidx.annotation.NonNull;

import com.hzlz.aviation.feature.account.model.HasUnreadMessageNotificationResponse;
import com.hzlz.aviation.feature.account.model.Media;
import com.hzlz.aviation.feature.account.request.GetMomentListRequest;
import com.hzlz.aviation.feature.account.request.HasNewMomentRequest;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.NetworkData;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

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
