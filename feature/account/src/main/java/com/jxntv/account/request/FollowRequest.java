package com.jxntv.account.request;

import androidx.annotation.NonNull;
import com.google.gson.JsonElement;
import com.jxntv.account.api.AccountAPI;
import com.jxntv.base.model.anotation.AuthorType;
import com.jxntv.network.request.BaseGVideoMapRequest;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * 关注请求
 *
 *
 * @since 2020-02-10 21:52
 */
public final class FollowRequest extends BaseGVideoMapRequest<Object> {
  //<editor-fold desc="设置参数">

  /**
   * 设置作者 id
   *
   * @param authorId 作者 id
   * @param authorType 用户类型
   */
  public void setAuthorId(@NonNull String authorId, @AuthorType int authorType) {
    mParameters.put("authorId", authorId);
    mParameters.put("authorType", authorType);
  }


  /**
   * 设置是否关注
   *
   * @param follow true : 关注 ; false 不关注
   */
  public void setFollow(boolean follow) {
    mParameters.put("type", follow);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().follow(mParameters);
  }

  @Override
  protected int getMaxParameterCount() {
    return 3;
  }
  //</editor-fold>
}
