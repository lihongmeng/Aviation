package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.feature.account.model.Author;
import com.hzlz.aviation.feature.account.model.UserAuthor;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 通过 id 获取UGC作者请求
 *
 */
public final class GetUserAuthorByIdRequest extends BaseGVideoRequest<UserAuthor> {
  //<editor-fold desc="属性">
  private HashMap<String, Object> mParameters = new HashMap<>(2);
  //</editor-fold>

  //<editor-fold desc="设置参数">
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
    return AccountAPI.Instance.get().getUserAuthorById(mParameters);
  }
  //</editor-fold>
}
