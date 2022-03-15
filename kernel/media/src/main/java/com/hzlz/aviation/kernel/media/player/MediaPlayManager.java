package com.hzlz.aviation.kernel.media.player;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.media.MediaConfig;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.library.util.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * media媒体统一控制类
 */
public class MediaPlayManager {

  /** 持有的单例 */
  private volatile static MediaPlayManager sInstance = null;
  /** 根据tabId来存储对应的静音自动播放模板 */
  private HashMap<String, List<IMediaPlayer>> mFeedPlayers;
  /** 存储对应的pageFragment */
  private HashMap<String, WeakReference<MediaPageFragment>> mReferences;
  /** 存储对应的player */
  private HashMap<String, WeakReference<IMediaPlayer>> mLastFeedPlayers;
  /** 标记首页splash事件 */
  private boolean splashFlag = false;
  /** 等待splash加载的tab */
  private String mLastWaitSplashFlagTabId = "";

  /**
   * 构造函数
   */
  private MediaPlayManager() {
    mFeedPlayers = new HashMap<>();
    mReferences = new HashMap<>();
    mLastFeedPlayers = new HashMap<>();
  }

  /**
   * 构造函数
   */
  public static MediaPlayManager getInstance() {
    if (sInstance == null) {
      synchronized (MediaPlayManager.class) {
        if (sInstance == null) {
          sInstance = new MediaPlayManager();
        }
      }
    }
    return sInstance;
  }

  /**
   * 释放本地资源
   */
  public static void release() {
    if (sInstance != null) {
      sInstance.realRelease();
    }
  }

  /**
   * 注册player
   *
   * @param tabId 频道id
   * @param feedPlayer feed播放器
   */
  public void registerMediaPlayer(String tabId, IMediaPlayer feedPlayer) {
    if (!checkParams(tabId)) {
      return;
    }

    List<IMediaPlayer> feedPlayerList = mFeedPlayers.get(tabId);
    if (feedPlayerList == null) {
      feedPlayerList = new ArrayList<>(2);
      mFeedPlayers.put(tabId, feedPlayerList);
    }
    if (!feedPlayerList.contains(feedPlayer)) {
      feedPlayerList.add(feedPlayer);
    }
  }

  /**
   * 反注册player
   *
   * @param tabId 频道id
   * @param feedPlayer feed播放器
   */
  public void unRegisterMediaPlayer(String tabId, IMediaPlayer feedPlayer) {
    if (!checkParams(tabId, feedPlayer)) {
      return;
    }
    List<IMediaPlayer> muteVideos = mFeedPlayers.get(tabId);
    if (feedPlayer.isPlaying()){
      feedPlayer.stop();
    }
    if (muteVideos != null) {
      muteVideos.remove(feedPlayer);
    }
  }

  public void registerFragment(String tabId, MediaPageFragment feedPageFragment) {
    if (!checkParams(tabId, feedPageFragment)) {
      return;
    }
    WeakReference<MediaPageFragment> reference = new WeakReference<>(feedPageFragment);
    mReferences.put(tabId, reference);
  }

  /**
   * 播放（指定tab)
   *
   * @param tabId 频道id
   */
  public void tryStartPlay(String tabId){
    if (!checkParams(tabId)) {
      return;
    }
    // 此时还在播放广告，则记录tab id 等待广告播放
    if (!splashFlag) {
      mLastWaitSplashFlagTabId = tabId;
      return;
    }

    List<IMediaPlayer> feedPlayerList = mFeedPlayers.get(tabId);
    // 原视频检测逻辑
//    boolean isPlaying = checkPlayList(feedPlayerList, true);
//    if (!isPlaying) {
//      tryStartPlay(feedPlayerList);
//    }
    tryStartPlay(feedPlayerList);

  }


  /**
   * 获取首页splash结束事件
   */
  public void onSplashEnd() {
    splashFlag = true;
    if (!TextUtils.isEmpty(mLastWaitSplashFlagTabId)) {
      if (!TextUtils.equals(mLastWaitSplashFlagTabId, StatPid.HOME_RECOMMEND)){
        tryStartPlay(mLastWaitSplashFlagTabId);
      }
      mLastWaitSplashFlagTabId = "";
    }
  }

  /**
   * 播放指定位置的player
   */
  public boolean play(String tabId, int position) {
    if (!checkParams(tabId)) {
      return false;
    }
    List<IMediaPlayer> feedPlayerList = mFeedPlayers.get(tabId);
    if (feedPlayerList == null) {
      return false;
    }
    for (IMediaPlayer feedPlayer : feedPlayerList) {
      if (feedPlayer != null && feedPlayer.getPosition() == position) {
        if (!feedPlayer.isPlaying()) {
          play(tabId, feedPlayer);
        }
        return true;
      }
    }
    return false;
  }

  /**
   * 停止播放（指定tab)
   *
   * @param tabId 频道id
   */
  public void stop(String tabId) {
    if (!checkParams(tabId)) {
      return;
    }
    List<IMediaPlayer> feedPlayerList = mFeedPlayers.get(tabId);
    boolean isPlaying = checkPlayList(feedPlayerList, false);
    if (isPlaying) {
      tryStop(feedPlayerList);
    }
  }

  /**
   * 播放（指定tab和player)
   *
   * @param tabId 频道id
   * @param player feed播放器
   */
  public void play(String tabId, IMediaPlayer player) {
    if (!checkParams(tabId, player)) {
      return;
    }

    List<IMediaPlayer> feedPlayerList = mFeedPlayers.get(tabId);

    if (feedPlayerList == null || feedPlayerList.size() <= 0) {
      if (!player.isPlaying()) {
        player.play();
      }
      return;
    }
    for (IMediaPlayer feedPlayer : feedPlayerList) {
      if (feedPlayer.isPlaying()) {
        feedPlayer.stop();
      }
    }
    if (!player.isPlaying()) {
      player.play();
    }

//    if (feedPlayerList == null || feedPlayerList.size() <= 0) {
//      if (!player.isPlaying()) {
//        player.play(isAutoPlayNext?MediaSoundType.SOUND:MediaSoundType.DEFAULT);
//      }
//      return;
//    }
//    for (IMediaPlayer feedPlayer : feedPlayerList) {
//      if (feedPlayer.isPlaying()) {
//        feedPlayer.stop();
//      }
//    }
//    if (!player.isPlaying()) {
//      player.play(isAutoPlayNext?MediaSoundType.SOUND:MediaSoundType.DEFAULT);
//    }
  }

  public void muteSound(String tabId, boolean mute) {
    if (!checkParams(tabId)) {
      LogUtils.e("Tab id is illegal");
      return;
    }
    List<IMediaPlayer> feedPlayerList = mFeedPlayers.get(tabId);
    if (feedPlayerList == null || feedPlayerList.size() <= 0) {
      return;
    }
    for (IMediaPlayer feedPlayer : feedPlayerList) {
      feedPlayer.mute(mute);
    }
  }

  /**
   * 播放（指定tab和player)
   *
   * @param tabId 频道id
   */
  public void stop(String tabId, IMediaPlayer player) {
    if (!checkParams(tabId, player)) {
      return;
    }
    player.stop();
  }

  /**
   * 检查播放列表
   *
   * @param feedPlayerList         待查询的player list
   * @param needStopOutScalePlayer 是否需要将不在播放区域的视频停止掉，该视频不计入查找范围
   * @return 当前是否有正在播放的视频
   */
  private boolean checkPlayList(List<IMediaPlayer> feedPlayerList, boolean needStopOutScalePlayer) {
    if (feedPlayerList == null || feedPlayerList.size() <= 0) {
      return false;
    }
    boolean isFound = false;
    for (IMediaPlayer feedPlayer : feedPlayerList) {
      //查询逻辑 与 自动播放逻辑分开
      if (feedPlayer.isPlaying()) {
        if (!needStopOutScalePlayer) {
          isFound = true;
          break;
        } else {
          if (!feedPlayer.isInPlayScale() || isFound) {
            // 将不在播放区的视频停止掉
            feedPlayer.stop();
          } else {
            isFound = true;
          }
        }
      }
    }
    return isFound;
  }

  /**
   * 尝试播放
   *
   * @param feedPlayerList 带处理的player list
   */
  private void tryStartPlay(List<IMediaPlayer> feedPlayerList) {
    if (feedPlayerList == null || feedPlayerList.size() <= 0) {
      return ;
    }
//    原视频自动播放逻辑
//    for (IMediaPlayer feedPlayer : feedPlayerList) {
//      if (feedPlayer.isSupportAutoPlay() && feedPlayer.isInPlayScale()) {
//        feedPlayer.play();
//        break;
//      }
//    }

    int isFirstPlayer = 0;
//    boolean isNeedMute = false;
    //列表重新排序
    List<IMediaPlayer> list = new ArrayList<>(feedPlayerList);
    for (int i=1;i<list.size();i++){
      for (int j =0; j<list.size();j++){
        if (list.get(i).getCurrentPosition() < list.get(j).getCurrentPosition()){
          IMediaPlayer mediaPlayer = list.get(i);
          list.set(i,list.get(j));
          list.set(j,mediaPlayer);
        }
      }
    }

    boolean hasPlayMedia = false;
    for (int i = 0;i < list.size();i++){
      IMediaPlayer iMediaPlayer =list.get(i);
      if (iMediaPlayer.isSupportAutoPlay() && iMediaPlayer.isInPlayScale()){
        iMediaPlayer.play();
        iMediaPlayer.mute(true);
        hasPlayMedia = true;
        break;
      }
    }

    if (!hasPlayMedia) {
      for (IMediaPlayer feedPlayer : feedPlayerList) {
        if (feedPlayer != null) {
          feedPlayer.stop();
        }
      }
    }
//    //遍历播放列表中的至多2个视频
//    for (int i = 0;i < list.size();i++){
//      if (isFirstPlayer==0) {
//        //当前为第一个可播放的视频时
//        if (list.get(i).isSupportAutoPlay() && list.get(i).isInPlayScale()) {
//          boolean nextMute = true;
//          //剩余列表中的视频是否静音播放
//          for (int j = i+1;j<list.size();j++){
//            if (list.get(j).isSupportAutoPlay() && list.get(j).isInPlayScale()) {
//              nextMute = list.get(j).isMute();
//              if (!nextMute){
//                break;
//              }
//            }
//          }
//          if (!nextMute) {
//             list.get(i).play(MediaSoundType.MUTE);
//             isNeedMute = false;
//          } else {
//            isNeedMute = list.get(i).play(MediaSoundType.DEFAULT);
//          }
//          isFirstPlayer++;
//        }
//      }else {
//        if (isFirstPlayer==1) {
//          //第二个视频播放
//          if (list.get(i).isSupportAutoPlay() && list.get(i).isInPlayScale()) {
//            list.get(i).play(isNeedMute ? MediaSoundType.MUTE : MediaSoundType.DEFAULT);
//          }
//          isFirstPlayer++;
//        }else {
//          //其他视频暂停
//          list.get(i).stop();
//        }
//      }
//    }
  }

  /**
   * 尝试停止指定列表
   *
   * @param feedPlayerList 指定列表
   */
  private void tryStop(List<IMediaPlayer> feedPlayerList) {
    if (feedPlayerList == null || feedPlayerList.size() <= 0) {
      return;
    }
    for (IMediaPlayer feedPlayer : feedPlayerList) {
      if (feedPlayer.isPlaying()) {
        feedPlayer.stop();
        break;
      }
    }
  }

  /**
   * 检查参数
   */
  private boolean checkParams(String param, Object... objects) {
    if (TextUtils.isEmpty(param) || objects == null) {
      return false;
    }
    for (Object o : objects) {
      if (o == null) {
        return false;
      }
    }
    return true;
  }

  /**
   * 检查参数
   */
  public void onPlayEnd(String tabId, int position) {
    if (!MediaConfig.isSupportAutoPlayNext()) {
      return;
    }
    WeakReference<MediaPageFragment> reference = mReferences.get(tabId);
    if (reference == null) {
      return;
    }
    MediaPageFragment fragment = reference.get();
    if (fragment == null) {
      return;
    }
    fragment.tryAutoPlayNext(position);
  }

  /**
   * 开始播放事件，用于暂停其他player
   */
  public void onPlayStart(String tabId, IMediaPlayer player) {
    if (!checkParams(tabId, player)) {
      return;
    }
    List<IMediaPlayer> feedPlayerList = mFeedPlayers.get(tabId);
    if (feedPlayerList == null) {
      return;
    }
    for (IMediaPlayer feedPlayer : feedPlayerList) {
      if (feedPlayer != null && feedPlayer != player) {
        feedPlayer.stop();
      }
    }
    AudioPlayManager.getInstance().release();
  }

  /**
   * 跳转至detail
   */
  public void onChangeToDetail(@NonNull MediaModel model) {
    List<IMediaPlayer> feedPlayerList = mFeedPlayers.get(model.tabId);
    if (feedPlayerList == null || model.getMediaType() == MediaType.IM_VERTICAL_LIVE ||
            model.getMediaType() == MediaType.IM_HORIZONTAL_LIVE || model.tabId.contains("special_list")) {
      return;
    }
    for (IMediaPlayer feedPlayer : feedPlayerList) {
      if (feedPlayer.getPlayerMediaModel() == model) {
        feedPlayer.onChangeToDetail();
        mLastFeedPlayers.put(model.tabId, new WeakReference<>(feedPlayer));
        return;
      }
    }
  }

  /**
   * 返回feed
   */
  public void onBackFeed(String tabId) {
    if (!checkParams(tabId)) {
      return;
    }
    WeakReference<IMediaPlayer> reference = mLastFeedPlayers.get(tabId);
    if (reference == null) {
      return;
    }
    IMediaPlayer player = reference.get();
    if (player == null) {
      return;
    }
    player.onBackFeed();
  }

  /**
   * 释放本地资源
   */
  private void realRelease() { mFeedPlayers = null;
    mReferences = null;
    mLastFeedPlayers = null;
    splashFlag = false;
    mLastWaitSplashFlagTabId = "";
    sInstance = null;
  }
}
