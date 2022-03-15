package com.hzlz.aviation.feature.record.recorder.request;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.record.recorder.api.RecordApi;
import com.hzlz.aviation.feature.record.recorder.model.VodVideoCreateModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

/**
 * vod video创建事件请求
 */
public class VodVideoCreateRequest extends BaseGVideoMapRequest<VodVideoCreateModel> {
  /** 处理文件名 */
  private static final String PARAM_FILE_NAME = "fileName";

  /**
   * 设置文件名
   *
   * @param fileName 待设置的文件名
   */
  public void setFileName(String fileName) {
    if (TextUtils.isEmpty(fileName)) {
      return;
    }
    mParameters.put(PARAM_FILE_NAME, fileName);
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return RecordApi.Instance.get().vodVideoCreate(mParameters);
  }

  @Override
  protected int getMaxParameterCount() {
    return 1;
  }
}
