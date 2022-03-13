package com.jxntv.android.video.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoStateModel implements Parcelable {
  public final boolean isPlaying;
  public final int currentProgress;
  public final int loadProgress;
  public final int totalProgress;

  public VideoStateModel(boolean isPlaying, int currentProgress, int loadProgress, int totalProgress) {
    this.isPlaying = isPlaying;
    this.currentProgress = currentProgress;
    this.loadProgress = loadProgress;
    this.totalProgress = totalProgress;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(this.isPlaying ? (byte) 1 : (byte) 0);
    dest.writeInt(this.currentProgress);
    dest.writeInt(this.loadProgress);
    dest.writeInt(this.totalProgress);
  }

  protected VideoStateModel(Parcel in) {
    this.isPlaying = in.readByte() != 0;
    this.currentProgress = in.readInt();
    this.loadProgress = in.readInt();
    this.totalProgress = in.readInt();
  }

  public static final Parcelable.Creator<VideoStateModel> CREATOR =
      new Parcelable.Creator<VideoStateModel>() {
        @Override public VideoStateModel createFromParcel(Parcel source) {
          return new VideoStateModel(source);
        }

        @Override public VideoStateModel[] newArray(int size) {
          return new VideoStateModel[size];
        }
      };
}
