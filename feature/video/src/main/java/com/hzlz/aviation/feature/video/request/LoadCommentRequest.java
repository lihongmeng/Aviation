package com.hzlz.aviation.feature.video.request;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.hzlz.aviation.feature.video.api.VideoAPI;
import com.hzlz.aviation.kernel.base.model.comment.CommentModel;
import com.hzlz.aviation.kernel.network.request.BaseGVideoPageMapRequest;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class LoadCommentRequest extends BaseGVideoPageMapRequest<CommentModel> {
  private String mediaId;
  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

  /**
   * 设置置顶评论
   * @param commentId 评论id
   * @param commentType  类型  0 评论  1 回复
   */
  public void setComment(long commentId, int commentType){
    if (commentId > 0){
      mParameters.put("commentId",commentId);
      mParameters.put("type",commentType);
    }
  }

  public void setPageNumber(int pageNumber) {
    mParameters.put("pageNum", pageNumber);
  }

  public void setPageSize(int pageSize) {
    mParameters.put("pageSize", pageSize);
  }

  @Override protected TypeToken<List<CommentModel>> getResponseTypeToken() {
    return new TypeToken<List<CommentModel>>(){};
  }

  @Override protected int getMaxParameterCount() {
    return 4;
  }

  @Override protected Observable<Response<JsonElement>> getResponseObservable() {
    return VideoAPI.Instance.get().loadComment(mediaId, mParameters);
  }

  @Override
  protected JsonElement furtherProcessData(JsonElement data) {
    return getMemberJsonElement(data, "list");
  }
}
