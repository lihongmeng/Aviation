package com.jxntv.account.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.account.model.Author;
import com.jxntv.network.request.BaseGVideoPageMapRequest;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import retrofit2.Response;

/**
 * 获取
 *
 *
 * @since 2020-02-10 21:34
 */
public final class GetFollowListRequest extends BaseGVideoPageMapRequest<Author> {
  //<editor-fold desc="设置参数">

  /**
   * 设置获取author关注列表
   * @param author
   */
  public void setAuthor(Author author) {
    if (author != null) {
      mParameters.put("authorId", author.getId());
      mParameters.put("authorType", author.getType());
    }
  }
  public void setInput(String input) {
    mParameters.put("input", input);
  }
  /**
   * 设置分页编号
   *
   * @param pageNumber 分页编号
   */
  public void setPageNumber(int pageNumber) {
    mParameters.put("pageNum", pageNumber);
  }

  /**
   * 设置分页大小
   *
   * @param pageSize 分页大小
   */
  public void setPageSize(int pageSize) {
    mParameters.put("pageSize", pageSize);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().getFlowList(mParameters);
  }

  @Override
  protected int getMaxParameterCount() {
    return 5;
  }

  @Override
  protected TypeToken<List<Author>> getResponseTypeToken() {
    return new TypeToken<List<Author>>() {
    };
  }

  @Override
  protected JsonElement furtherProcessData(JsonElement data) {
    return getMemberJsonElement(data, "list");
  }
  //</editor-fold>
}
