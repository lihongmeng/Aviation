package com.jxntv.media.model;

import com.google.gson.annotations.SerializedName;
import com.jxntv.base.adapter.IAdapterModel;
import com.jxntv.base.model.anotation.MediaStickType;
import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.media.MediaConstants;
import com.jxntv.media.MediaPageSource;

import java.util.ArrayList;
import java.util.List;

import static com.jxntv.media.MediaConstants.STATE_STOP;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;

/**
 * 自动播放中使用的Media实体
 */
public class MediaModel extends VideoModel implements IAdapterModel {

  private transient ObservableInt mModelPosition;

  public MediaModel(VideoModel media) {
    updateFrom(media);
  }

  /* 以下为Feed流中使用数据 */
  /** 是否为置顶数据 */
  private int isStick = MediaStickType.NOT_STICK;

  //专题列表id
  private String listId;

  //特殊专题块数据
  private List<MediaModel> headTop;
  private List<MediaModel> headBottom;

  //滚动新闻
  private List<MediaModel> items;
  //滑动时间
  private int sliderTime;

  public List<MediaModel> getHeadTop() {
    return headTop;
  }

  public int getSliderTime() {
    return sliderTime;
  }

  public void setSliderTime(int sliderTime) {
    this.sliderTime = sliderTime;
  }

  public void setHeadTop(List<MediaModel> headTop) {
    this.headTop = headTop;
  }

  public List<MediaModel> getHeadBottom() {
    return headBottom;
  }

  public void setHeadBottom(List<MediaModel> headBottom) {
    this.headBottom = headBottom;
  }

  public List<MediaModel> getItems() {
    return items;
  }

  public void setItems(List<MediaModel> items) {
    this.items = items;
  }

  public String getListId() {
    return listId;
  }

  public void setListId(String listId) {
    this.listId = listId;
  }

  public boolean isStick() {
    return isStick == MediaStickType.STICK;
  }

  public void setIsStick(boolean isStick) {
    this.isStick = isStick ? MediaStickType.STICK : MediaStickType.NOT_STICK;
  }

  /** 持有的feed列表推荐模型列表 */
  @SerializedName("adverts")
  public List<RecommendModel> feedRecommendModelList = new ArrayList<>();
  /** 显示推荐列表时间 s为单位 */
  public int showRecTime;

  /**  -------- 以下本地使用  -------- */
  /** 视图真实位置 */
  public int viewPosition;
  /** 视图对应tab */
  public String tabId;
  /** 对应模型播放状态 */
  public @MediaConstants.PlayState int playState = STATE_STOP;
  /** 对应模型数据地址 */
  public int correspondVideoModelAddress;

  //搜索界面展示的标签
  public boolean isShowTag;
  /**
   * 搜索词
   */
  public String searchWord;
  /**
   * 预制文案搜索词
   */
  public String hintSearchWord;
  /**
   * 搜索关键词匹配
   */
  public List<String> words;

  public List<String> getWords() {
    if (words == null && !TextUtils.isEmpty(searchWord)) {
      words = new ArrayList<>();
      words.add(searchWord);
    }
    return words;
  }

  public @MediaPageSource.PageSource int showMediaPageSource = MediaPageSource.PageSource.DEFAULT;

  public boolean showInPgc(){
    return showMediaPageSource == MediaPageSource.PageSource.PGC;
  }

  public boolean showInAudiovisual(){
    return showMediaPageSource == MediaPageSource.PageSource.AUDIOVISUAL;
  }

  public boolean showInNews(){
    return showMediaPageSource == MediaPageSource.PageSource.NEWS;
  }

  public boolean showInSearch(){
    return showMediaPageSource == MediaPageSource.PageSource.SEARCH;
  }

  public boolean showInTVCollection(){
    return showMediaPageSource == MediaPageSource.PageSource.TV_COLLECTION;
  }

  /**
   *  视频标题显示在视频空间内容
   */
  public boolean showInsideVideoTitle(){
    return showInPgc() || showInTVCollection() || showInAudiovisual();
  }

  @Override
  public void setModelPosition(int position) {
    if(mModelPosition==null){
      mModelPosition=new ObservableInt();
    }
    mModelPosition.set(position);
  }

  @Override
  @NonNull
  public ObservableInt getModelPosition() {
    return mModelPosition;
  }
}
