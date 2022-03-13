package com.jxntv.base.model.video;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

/**
 * 短视频列表模型
 */
public class ShortVideoListModel implements Parcelable {
  /**
   * 是否为加载更多数据
   */
  public final boolean loadMore;
  /**
   * 当前要播放短视频，在短视频元数据列表中位置
   */
  public final String cursor;
  /**
   * 短视频元数据列表
   */
  public final List<VideoModel> list;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(this.loadMore ? 1 : 0);
    dest.writeString(this.cursor);
    dest.writeTypedList(this.list);
  }

  private ShortVideoListModel(boolean loadMore, String cursor, List<VideoModel> list) {
    this.loadMore = loadMore;
    this.cursor = cursor;
    this.list = list;
  }

  private ShortVideoListModel(Parcel in) {
    this.loadMore = in.readInt() > 0;
    this.cursor = in.readString();
    this.list = in.createTypedArrayList(VideoModel.CREATOR);
  }

  public static final Parcelable.Creator<ShortVideoListModel> CREATOR = new Parcelable.Creator<ShortVideoListModel>() {
    @Override public ShortVideoListModel createFromParcel(Parcel source) {
      return new ShortVideoListModel(source);
    }

    @Override public ShortVideoListModel[] newArray(int size) {
      return new ShortVideoListModel[size];
    }
  };

  public String getFirstMediaId(){
    if(list==null||list.isEmpty()){
      return null;
    }
    return list.get(0).getId();
  }

  public static final class Builder {
    private boolean loadMore;
    private String cursor;
    private List<VideoModel> list;

    private Builder() {
    }

    public static Builder aFeedModel() {
      return new Builder();
    }

    public Builder withLoadMore(boolean loadMore) {
      this.loadMore = loadMore;
      return this;
    }
    public Builder withCursor(String cursor) {
      this.cursor = cursor;
      return this;
    }

    public Builder withList(List<VideoModel> list) {
      this.list = list;
      return this;
    }

    public ShortVideoListModel build() {
      return new ShortVideoListModel(loadMore, cursor, list);
    }
  }
}
