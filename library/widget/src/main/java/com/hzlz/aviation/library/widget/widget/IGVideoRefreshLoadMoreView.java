package com.hzlz.aviation.library.widget.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 刷新加载更多 View 接口
 *
 *
 * @since 2020-02-28 14:25
 */
public interface IGVideoRefreshLoadMoreView {
  //<editor-fold desc="API">

  /**
   * 启用刷新
   *
   * @param enable true : 启用；false : 不启用
   */
  void enableGVideoRefresh(boolean enable);

  /**
   * 设置刷新接口
   *
   * @param listener 刷新接口
   */
  void setGVideoOnRefreshListener(@Nullable OnRefreshListener listener);

  /**
   * 完成刷新
   */
  void finishGVideoRefresh();

  /**
   * 启用加载更多
   *
   * @param enable true : 启用；false : 不启用
   */
  void enableGVideoLoadMore(boolean enable);

  /**
   * 设置加载更多接口
   *
   * @param listener 加载更多接口
   */
  void setGVideoOnLoadMoreListener(@Nullable OnLoadMoreListener listener);

  /**
   * 完成加载更多
   */
  void finishGVideoLoadMore();
  //</editor-fold>

  //<editor-fold desc="接口">

  /**
   * 刷新接口
   */
  interface OnRefreshListener {
    void onRefresh(@NonNull IGVideoRefreshLoadMoreView view);
  }

  /**
   * 加载更多接口
   */
  interface OnLoadMoreListener {
    void onLoadMore(@NonNull IGVideoRefreshLoadMoreView view);
  }
  //</editor-fold>
}