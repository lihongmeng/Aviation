package com.jxntv.bindingadapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import com.jxntv.widget.IGVideoRefreshLoadMoreView;

/**
 * 刷新加载更多 View 数据绑定
 *
 *
 * @since 2020-02-28 15:23
 */
public final class RefreshLoadMoreViewBindingAdapter {

  /**
   * 启用刷新
   *
   * @param view 刷新加载更多 View
   * @param enable 是否启用
   */
  @BindingAdapter("ksrlv_enableRefresh")
  public static void enableRefresh(@NonNull View view, boolean enable) {
    if (view instanceof IGVideoRefreshLoadMoreView) {
      ((IGVideoRefreshLoadMoreView) view).enableGVideoRefresh(enable);
    }
  }

  /**
   * 设置刷新接口
   *
   * @param view 刷新加载更多 View
   * @param listener 刷新接口
   */
  @BindingAdapter("ksrlv_onRefresh")
  public static void setOnRefreshListener(@NonNull View view,
      @NonNull IGVideoRefreshLoadMoreView.OnRefreshListener listener) {
    if (view instanceof IGVideoRefreshLoadMoreView) {
      ((IGVideoRefreshLoadMoreView) view).setGVideoOnRefreshListener(listener);
    }
  }

  /**
   * 启用加载更多
   *
   * @param view 刷新加载更多 View
   * @param enable 是否启用
   */
  @BindingAdapter("ksrlv_enableLoadMore")
  public static void enableLoadMore(@NonNull View view, boolean enable) {
    if (view instanceof IGVideoRefreshLoadMoreView) {
      ((IGVideoRefreshLoadMoreView) view).enableGVideoLoadMore(enable);
    }
  }

  /**
   * 设置刷新接口
   *
   * @param view 刷新加载更多 View
   * @param listener 加载更多接口
   */
  @BindingAdapter("ksrlv_onLoadMore")
  public static void setOnLoadMoreListener(@NonNull View view,
      @NonNull IGVideoRefreshLoadMoreView.OnLoadMoreListener listener) {
    if (view instanceof IGVideoRefreshLoadMoreView) {
      ((IGVideoRefreshLoadMoreView) view).setGVideoOnLoadMoreListener(listener);
    }
  }
}
