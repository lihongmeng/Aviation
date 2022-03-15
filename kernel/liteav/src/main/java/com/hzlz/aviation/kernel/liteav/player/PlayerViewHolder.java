package com.hzlz.aviation.kernel.liteav.player;

/**
 * 播放器持有类，在列表与详情页相互跳转时复用播放器，做出无缝切换的体验
 */
class PlayerViewHolder {
  private PlayerViewHolder() {}
  private static class HOLDER {
    private static PlayerViewHolder holder = new PlayerViewHolder();
  }
  public static final PlayerViewHolder getInstance() {
    return new HOLDER().holder;
  }


  private VodPlayer mPlayer;
  public void save(VodPlayer player) {
    mPlayer = player;
  }

  public VodPlayer get() {
    return mPlayer;
  }

  public void clear() {
    if (mPlayer!=null){
      mPlayer.stopPlay(true);
      mPlayer.setVodListener(null);
      mPlayer.release();
      mPlayer = null;
    }
  }
}
