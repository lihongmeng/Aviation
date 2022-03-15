package com.hzlz.aviation.feature.video.model;

import com.hzlz.aviation.kernel.base.model.comment.CommentModel;

import java.util.List;

public class CommentResponse {
  public final List<CommentModel> list;
  public final int pageNum;
  public final int pageSize;
  public final int total;

  public CommentResponse(List<CommentModel> list, int pageNum, int pageSize, int total) {
    this.list = list;
    this.pageNum = pageNum;
    this.pageSize = pageSize;
    this.total = total;
  }

  public static final class Builder {
    private List<CommentModel> list;
    private int pageNum;
    private int pageSize;
    private int total;

    private Builder() {
    }

    public static Builder aCommentResponse() {
      return new Builder();
    }

    public Builder withList(List<CommentModel> list) {
      this.list = list;
      return this;
    }

    public Builder withPageNum(int pageNum) {
      this.pageNum = pageNum;
      return this;
    }

    public Builder withPageSize(int pageSize) {
      this.pageSize = pageSize;
      return this;
    }

    public Builder withTotal(int total) {
      this.total = total;
      return this;
    }

    public CommentResponse build() {
      return new CommentResponse(list, pageNum, pageSize, total);
    }
  }
}
