package com.hzlz.aviation.kernel.base.plugin;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.hzlz.aviation.library.ioc.Plugin;

import java.util.ArrayList;

/**
 * 图片查看模块接口
 */
public interface PhotoPreviewPlugin extends Plugin {

  /**
   * 图片查看
   * @param  view 添加放大动画的View
   * @param imageUrl 图片链接
   */
  void startPhotoViewActivity(@NonNull Context context, View view, String imageUrl);

  /**
   * 图片查看
   * @param selectPosition recycler中点击的position
   */
  void startPhotoViewActivity(@NonNull Context context, View view, ArrayList<String> imageUrls, int selectPosition);



}
