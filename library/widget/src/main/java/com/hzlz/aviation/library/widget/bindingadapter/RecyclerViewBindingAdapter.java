package com.hzlz.aviation.library.widget.bindingadapter;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * @since 2020-02-17 21:16
 */
public final class RecyclerViewBindingAdapter {
  /**
   * 设置 Adapter
   */
  @BindingAdapter("adapter")
  public static void setAdapter(
      @NonNull RecyclerView recyclerView,
      @NonNull RecyclerView.Adapter adapter) {
    recyclerView.setAdapter(adapter);
  }
}
