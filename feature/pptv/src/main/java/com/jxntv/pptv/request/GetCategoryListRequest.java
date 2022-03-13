package com.jxntv.pptv.request;

import com.google.gson.JsonElement;
import com.jxntv.network.request.BaseGVideoMapRequest;
import com.jxntv.pptv.api.PptvAPI;
import com.jxntv.pptv.model.CategoryResponse;
import io.reactivex.rxjava3.core.Observable;
import java.util.List;
import retrofit2.Response;

/**
 * 获取频道请求
 *
 */
public final class GetCategoryListRequest extends BaseGVideoMapRequest<List<CategoryResponse>> {

  public void setLabelType(int type) {
    mParameters.put("labelType", type);
  }
  @Override protected int getMaxParameterCount() {
    return 1;
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return PptvAPI.Instance.get().getCategoryList(mParameters);
  }

}
