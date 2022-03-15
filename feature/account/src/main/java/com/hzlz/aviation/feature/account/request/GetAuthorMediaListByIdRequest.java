package com.hzlz.aviation.feature.account.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.Media;
import com.hzlz.aviation.kernel.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 获取动态列表请求
 *
 *
 * @since 2020-03-10 19:25
 */
public final class GetAuthorMediaListByIdRequest extends BaseGVideoPageMapRequest<Media> {
  //<editor-fold desc="属性">
  @Nullable
  private String mAuthorId;
  //</editor-fold>

  //<editor-fold desc="设置参数">
  public void setAuthorId(@NonNull String authorId) {
    mAuthorId = authorId;
  }

  public void setAuthor(Author author) {
    if (author != null) {
      mParameters.put("authorId", author.getId());
      mParameters.put("authorType", author.getType());
    }
  }

  public void setMediaTab(int mediaTab) {
    mParameters.put("mediaTab", mediaTab);
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

    if (mAuthorId == null) {
      throw new NullPointerException("author id is null");
    }
    return AccountAPI.Instance.get().getAuthorMediaList(mAuthorId, mParameters);
  }

  @Override
  protected TypeToken<List<Media>> getResponseTypeToken() {
    return new TypeToken<List<Media>>() {
    };
  }

  @Override
  protected int getMaxParameterCount() {
    return 5;
  }

  @Override
  protected JsonElement furtherProcessData(JsonElement data) {
    return getMemberJsonElement(data, "list");
  }
  //</editor-fold>
}
