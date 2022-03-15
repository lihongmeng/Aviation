package com.hzlz.aviation.feature.account.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 通过 id 获取作者请求
 *
 *
 * @since 2020-03-10 22:32
 */
public final class GetAuthorByIdRequest extends BaseGVideoRequest<Author> {
  //<editor-fold desc="属性">
  @Nullable
  private String mAuthorId;
  private HashMap<String, Object> mParameters = new HashMap<>(2);
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
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    if (mAuthorId == null) {
      throw new NullPointerException("author id is null");
    }
    return AccountAPI.Instance.get().getAuthorById(mAuthorId, mParameters);
  }
  //</editor-fold>
}
