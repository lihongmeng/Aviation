package com.hzlz.aviation.feature.account.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.account.api.AccountAPI;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public final class PublishRequest extends BaseGVideoMapRequest<Integer> {

  /** 待设置的参数列表 */
  private static final String PARAM_FILE_TYPE = "fileType";
  private static final int PARAM_FILE_TYPE_DEFAULT = 1;
  private static final String PARAM_IS_PUBLIC = "isPublic";
  private static final String PARAM_INTRODUCTION = "introduction";
  private static final String PARAM_IMAGE_ID = "imageId";
  private static final String PARAM_VIDEO_ID = "videoId";
  private static final String PARAM_GATHER_ID = "gatherId";


  public PublishRequest() {
    super();
    mParameters.put( PARAM_FILE_TYPE, PARAM_FILE_TYPE_DEFAULT);
  }

  //<editor-fold desc="方法实现">
  public void setPublic(boolean isPublic) {
    mParameters.put(PARAM_IS_PUBLIC, isPublic);
  }
  public void setIntroduction(String introduction) {
    mParameters.put(PARAM_INTRODUCTION, introduction);
  }

  /**
   * 设置imageId
   *
   * @param imageId  待设置的参数
   */
  public void setImageId(String imageId) {
    mParameters.put(PARAM_IMAGE_ID, imageId);
  }

  /**
   * 设置videoId
   *
   * @param videoId  待设置的参数
   */
  public void setVideoId(String videoId) {
    mParameters.put(PARAM_VIDEO_ID, videoId);
  }

  public void setGatherId(String gatherId) {
    mParameters.put(PARAM_GATHER_ID, gatherId);
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return AccountAPI.Instance.get().publish(mParameters);
  }

  @Override protected int getMaxParameterCount() {
    return 5;
  }
  //</editor-fold>
}
