package com.hzlz.aviation.kernel.base.plugin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.hzlz.aviation.kernel.base.BaseViewModel;
import com.hzlz.aviation.kernel.base.model.video.ShortVideoListModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.library.ioc.Plugin;

import io.reactivex.rxjava3.core.Observable;

/**
 * 视频详情页模块接口
 */
public interface VideoPlugin extends Plugin {
  /** 0 长视频展示推荐位tab，短视频直接播放； */
  int START_DEFAULT = 0;
  /** 1 长视频展示评论tab，短视频弹出评论弹窗； */
  int START_COMMENT = 1;
  /** 2 长视频展示栏目tab，短视频直接播放； */
  int START_COLUMN = 2;

  /**
   * extras中附带，启动详情页后直接展示评论页面
   * 0 长视频展示推荐位tab，短视频直接播放；1 长视频展示评论tab，短视频弹出评论弹窗；2 长视频展示栏目tab，短视频直接播放；
   */
  String EXTRA_START = "extra_start";
  /**
   * 详情页comment置顶id
   */
  String EXTRA_START_COMMENT_ID = "extra_start_comment_id";
  /**
   * 评论类型 0 评论  1 回复
   */
  String EXTRA_START_COMMENT_TYPE = "extra_start_comment_type";

  /** 统计埋点，来源 */
  String EXTRA_FROM_PID = "extra_from_pid";
  /** 统计埋点，频道页来源中附带channelId参数 */
  String EXTRA_FROM_CHANNEL_ID = "extra_from_channel_id";

  /**
   * 启动长视频详情页
   * @param context
   * @param videoModel 视频元数据，其中mediaId为必传（其他数据传空则不展示相应UI）；
   * @param extras
   */
  void startLongVideoActivity(@NonNull Context context, VideoModel videoModel, Bundle extras);

  /**
   * 启动短视频列表页
   * @param context
   * @param shortVideoListModel 短视频列表数据，对于其中每个VideoModel短视频元数据mediaId为必传（其他数据传空则不展示相应UI）；
   * @param extras
   */
  void startShortVideoActivity(@NonNull Context context, ShortVideoListModel shortVideoListModel, Bundle extras);


  /**
   * 启动直播详情页
   * @param context
   * @param videoModel 视频元数据，其中mediaId为必传（其他数据传空则不展示相应UI）；
   * @param extras
   */
  void startLiveActivity(@NonNull Context context, VideoModel videoModel, Bundle extras);



  /**
   * 启动图、文、语音 动态详情页
   * @param context
   * @param videoModel 视频元数据，其中mediaId为必传（其他数据传空则不展示相应UI）；
   * @param extras
   */
  void startImageTxtAudioActivity(@NonNull Context context, VideoModel videoModel, Bundle extras);


  /**
   * 新闻详情页
   * @param context
   * @param videoModel 视频元数据，其中mediaId为必传（其他数据传空则不展示相应UI）；
   * @param extras
   */
  void startNewsDetail(@NonNull Context context, VideoModel videoModel, Bundle extras);


  /**
   * 新闻列表详情页
   * @param context
   * @param videoModel 视频元数据，其中mediaId为必传（其他数据传空则不展示相应UI）；
   * @param extras
   */
  void startNewsList(@NonNull Context context, VideoModel videoModel, Bundle extras);


  /**
   * 专题列表详情页
   * @param context
   * @param videoModel 视频元数据，其中mediaId为必传（其他数据传空则不展示相应UI）；
   * @param extras
   */
  void startSpecialList(@NonNull Context context, VideoModel videoModel, Bundle extras);

  /**
   * 问答详情页
   */
  void startQADetailActivity(@NonNull Context context, VideoModel videoModel, Bundle extras);

  /**
   * 启动江西新闻联播特殊见面 -_-!
   * @param context
   * @param videoModel
   * @param extras
   */
  void startJXNewsActivity(@NonNull Context context, VideoModel videoModel, Bundle extras);

  /**
   * 评论点赞
   * @param commentId    评论id
   * @param isPrise      点赞、取消点赞
   */
  void commentPraise(String commentId, boolean isPrise, BaseViewModel.BaseGVideoResponseObserver<Boolean> observer);

  Observable<Object> deleteComment(String commentId);

}
