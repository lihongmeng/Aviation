package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.Media;
import com.jxntv.network.request.BaseGVideoPageMapRequest;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import retrofit2.Response;

/**
 * 获取动态列表请求
 *
 *
 * @since 2020-03-10 19:25
 */
public final class GetMomentListRequest extends BaseGVideoPageMapRequest<Media> {
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
      throw new RuntimeException("invalid parameters for get moment list request !!!");
    }
    return AccountAPI.Instance.get().getMomentList(mParameters);
  }

  @Override
  protected TypeToken<List<Media>> getResponseTypeToken() {
    return new TypeToken<List<Media>>() {
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
