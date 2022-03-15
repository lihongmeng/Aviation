package com.hzlz.aviation.feature.video.request;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.video.api.VideoAPI;
import com.hzlz.aviation.kernel.base.model.comment.CommentModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CommentRequest extends BaseGVideoMapRequest<CommentModel> {
  public void setMediaId(String mediaId) {
    mParameters.put("mediaId", mediaId);
  }

  public void setContent(String content) {
    mParameters.put("content", content);
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

  public void setGroupId(long groupId) {
    mParameters.put("groupId", groupId);
  }

  @Override
  protected int getMaxParameterCount() {
    return 7;
  }

  @Override
  protected Observable<Response<JsonElement>> getResponseObservable() {
    return VideoAPI.Instance.get().comment(mParameters);
  }
}
