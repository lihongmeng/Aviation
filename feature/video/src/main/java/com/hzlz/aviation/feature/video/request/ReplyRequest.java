package com.hzlz.aviation.feature.video.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.video.api.VideoAPI;
import com.hzlz.aviation.kernel.base.model.comment.ReplyModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class ReplyRequest extends BaseGVideoMapRequest<ReplyModel> {
  public void setPrimaryId(String primaryId) {
    mParameters.put("primaryId", primaryId);
  }

  /**
   * 回复对象uid
   * @param toUId
   */
  public void setToId(long toUId) {
    if (toUId > 0) {
      mParameters.put("toId", toUId);
    }
  }

  public void setImageList(List<String> imageList) {
    mParameters.put("imageList", imageList);
  }

  public void setSoundId(String soundId) {
    mParameters.put("soundId", soundId);
  }

  public void setSoundDuration(int time) {
    mParameters.put("length", time);
  }

  public void setSoundContent (String soundContent) {
    mParameters.put("soundContent", soundContent);
  }

  public void setContent(String content) {
    mParameters.put("content", content);
  }

  @Override protected int getMaxParameterCount() {
    return 3;
  }

  @Override protected Observable<Response<JsonElement>> getResponseObservable() {
    return VideoAPI.Instance.get().reply(mParameters);
  }
}
