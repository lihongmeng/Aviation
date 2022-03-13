package com.jxntv.account;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.jxntv.account.model.Media;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.ioc.PluginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 *
 *
 * @since 2020-03-10 21:59
 */
public final class Utils {
  //<editor-fold desc="私有构造函数">
  private Utils() {

  }
  //</editor-fold>

  //<editor-fold desc="API">
  public static void startVideo(Context context, List<Media> mediaList, Media media, Bundle bundle) {
    VideoPlugin plugin = PluginManager.get(VideoPlugin.class);
    if (plugin == null) {
      return;
    }
    if (!media.isShortMedia()) {
      PluginManager.get(DetailPagePlugin.class).dispatchToDetail(context,media,bundle);
    } else {
      ArrayList mShotMediaList = new ArrayList<>();
      int size = mediaList.size();
      mShotMediaList.ensureCapacity(size);
      mShotMediaList.clear();
      int cursor = 0;
      int cursorIndex = 0;
      for (int i = 0; i < size; i++) {
        Media item = mediaList.get(i);
        if (item.isShortMedia()) {
          if (item == media) {
            cursor = cursorIndex;
          } else {
            cursorIndex++;
          }
          mShotMediaList.add(item);
        }
      }

      plugin.startShortVideoActivity(
              context,
              Utils.convertMediaListToShortVideoListModel(mShotMediaList, cursor),
              bundle
      );
    }
  }

  @NonNull
  public static ShortVideoListModel convertMediaListToShortVideoListModel(
      @NonNull List<Media> mediaList, int position) {
    int size = mediaList.size();
    List<VideoModel> videoModelList = new ArrayList<>(size);
    videoModelList.addAll(mediaList);
    return ShortVideoListModel.Builder.aFeedModel()
        .withList(videoModelList)
        .withCursor(String.valueOf(position))
        .build();
  }

  //</editor-fold>
}
