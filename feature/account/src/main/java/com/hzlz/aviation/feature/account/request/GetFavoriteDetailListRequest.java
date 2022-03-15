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
 * 获取收藏详情列表请求
 *
 *
 * @since 2020-02-13 23:24
 */
public final class GetFavoriteDetailListRequest extends BaseGVideoPageMapRequest<Media> {
  //<editor-fold desc="属性">
  @Nullable
  private String mFavoriteId;
  //</editor-fold>

  //<editor-fold desc="设置参数">

  public void setAuthor(Author author) {
    if (author != null) {
      mParameters.put("authorId", author.getId());
      mParameters.put("authorType", author.getType());
    }
  }
  /**
   * 设置收藏 id
   *
   * @param favoriteId 收藏 id
   */
  public void setFavoriteId(@NonNull String favoriteId) {
    mFavoriteId = favoriteId;
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
    if (mFavoriteId == null) {
      throw new NullPointerException("favorite id is null");
    }
    if (mParameters.size() != 2 && mParameters.size() != 4) {
      throw new RuntimeException("invalid page parameters");
    }
    return AccountAPI.Instance.get().getFavoriteDetailList(mFavoriteId, mParameters);
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
