package com.hzlz.aviation.kernel.liteav.player;

import android.view.View;

import com.hzlz.aviation.kernel.base.model.video.PendantModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放器回调代理，支持多个回调
 */
public class GVideoPlayerListenerProxy implements GVideoPlayerListener {
  private List<GVideoPlayerListener> mListeners = new ArrayList<>();

  public GVideoPlayerListenerProxy() {
  }

  public void addListener(GVideoPlayerListener listener) {
    mListeners.add(listener);
  }

  public void removeListener(GVideoPlayerListener listener) {
    mListeners.remove(listener);
  }

  @Override public void onPlayPrepared() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onPlayPrepared();
      }
    }
  }

  @Override public void onReceiveFirstFrame() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onReceiveFirstFrame();
      }
    }
  }

  @Override public void onPlayBegin() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onPlayBegin();
      }
    }
  }

  @Override public void onPlayLoading() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onPlayLoading();
      }
    }
  }

  @Override public void onProgressChange(int playDuration, int loadingDuration, int duration) {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onProgressChange(playDuration, loadingDuration, duration);
      }
    }
  }

  @Override public void onPlayLoadingEnd() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onPlayLoadingEnd();
      }
    }
  }

  @Override public void onPlayEnd() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onPlayEnd();
      }
    }
  }

  @Override public void onErrorNetDisconnect() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onErrorNetDisconnect();
      }
    }
  }

  @Override public void onVoiceDecodeFail() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onVoiceDecodeFail();
      }
    }
  }

  @Override public void onAudioDecodeFail() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onAudioDecodeFail();
      }
    }
  }

  @Override public void onReconnect() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onReconnect();
      }
    }
  }

  @Override
  public void onPlayStuck() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onPlayStuck();
      }
    }
  }

  @Override public void onChangeResolution() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onChangeResolution();
      }
    }
  }

  @Override public void onMp4ChangeRoation() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onMp4ChangeRoation();
      }
    }
  }

  @Override public void onFileNotFound() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onFileNotFound();
      }
    }
  }

  @Override public void onNetStatus() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onNetStatus();
      }
    }
  }

  @Override public boolean interceptFullScreenEvent() {
    boolean handled = false;
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        boolean result = listener.interceptFullScreenEvent();
        if (result) {
          handled = true;
        }
      }
    }
    return handled;
  }

  @Override public void onFullscreenChanged(boolean fullscreen) {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onFullscreenChanged(fullscreen);
      }
    }
  }

  @Override public void onBackPressed() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onBackPressed();
      }
    }
  }

  @Override public void onShareClick(View view) {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onShareClick(view);
      }
    }
  }

  @Override public void onScreenProjection(View view) {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onScreenProjection(view);
      }
    }
  }

  @Override public void onControllerShow() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onControllerShow();
      }
    }
  }

  @Override public void onPendantShow(PendantModel pendantModel) {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onPendantShow(pendantModel);
      }
    }
  }

  @Override public void onPendantClick(PendantModel pendantModel) {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onPendantClick(pendantModel);
      }
    }
  }

  @Override public void onControllerHide() {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onControllerHide();
      }
    }
  }

  @Override public void onPlayStateChanged(boolean isPlaying) {
    if (mListeners != null && !mListeners.isEmpty()) {
      for (GVideoPlayerListener listener : mListeners) {
        listener.onPlayStateChanged(isPlaying);
      }
    }
  }
}
