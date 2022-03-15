package com.hzlz.aviation.kernel.liteav.player;

import android.content.Context;

import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * 腾讯云播放器
 */
class VodPlayer extends TXVodPlayer {
  private TXCloudVideoView mCloudVideoView;
  private String mPlayUrl;
  private boolean mReleased;
  public VodPlayer(Context context) {
    super(context);
  }

  @Override public void setPlayerView(TXCloudVideoView txCloudVideoView) {
    mCloudVideoView = txCloudVideoView;
    super.setPlayerView(txCloudVideoView);
  }

  @Override public int startPlay(String s) {
    mPlayUrl = s;
    mReleased = false;
    return super.startPlay(s);
  }

  public void release() {
    mReleased = true;
    mPlayUrl = "";
  }

  public TXCloudVideoView getCloudVideoView() {
    return mCloudVideoView;
  }

  public String getPlayUrl() {
    return mPlayUrl;
  }

  public boolean isReleased() {
    return mReleased;
  }

  @Override
  public void resume() {
    mReleased = false;
    super.resume();
  }
}
