package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.MessageNotification;
import com.hzlz.aviation.kernel.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取消息通知列表请求
 *
 *
 * @since 2020-03-11 20:10
 */
public final class GetMessageNotificationListRequest
    extends BaseGVideoPageMapRequest<MessageNotification> {

  //<editor-fold desc="设置参数">

  /**
   * 设置分页编号
   *
   * @param pageNumber 分页编号
   */
  public void setPageNumber(int pageNumber) {
    mParameters.put("pageNum", pageNumber);
  }

  /**
   * 设置每页大小
   *
   * @param pageSize 每页大小
   */
  public void setPageSize(int pageSize) {
    mParameters.put("pageSize", pageSize);
  }

  //</editor-fold>
  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    if (mParameters.size() != 2) {
      throw new RuntimeException(
          "invalid page parameters for get message notification list request"
      );
    }
    return AccountAPI.Instance.get().getMessageNotificationList(mParameters);
  }

  @Override
  protected TypeToken<List<MessageNotification>> getResponseTypeToken() {
    return new TypeToken<List<MessageNotification>>() {
    };
  }

  @Override
  protected int getMaxParameterCount() {
    return 2;
  }

  @Override
  protected JsonElement furtherProcessData(JsonElement data) {
    return getMemberJsonElement(data, "list");
  }
  //</editor-fold>
}
