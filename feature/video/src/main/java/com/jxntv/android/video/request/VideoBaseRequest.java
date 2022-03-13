package com.jxntv.android.video.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.android.video.api.VideoAPI;
import com.jxntv.network.request.BaseGVideoRequest;

public abstract class VideoBaseRequest<T> extends BaseGVideoRequest<T> {
  @Nullable
  private volatile static VideoAPI sAPI = null;

  @NonNull
  protected VideoAPI getVideoAPI() {
    VideoAPI temp = sAPI;
    if (temp == null) {
      synchronized (VideoBaseRequest.class) {
        temp = sAPI;
        if (temp == null) {
          temp = getRetrofit().create(VideoAPI.class);
          sAPI = temp;
        }
      }
    }
    return temp;
  }
}
