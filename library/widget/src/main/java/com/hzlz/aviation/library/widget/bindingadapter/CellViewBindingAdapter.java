package com.hzlz.aviation.library.widget.bindingadapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.hzlz.aviation.library.widget.widget.AviationCellView;

/**
 * {@link AviationCellView} 扩展
 *
 *
 * @since 2020-01-20 15:09
 */
public final class CellViewBindingAdapter {
  // left text
  @BindingAdapter("cvShowLeftText")
  public static void showLeftText(@NonNull AviationCellView cellView, boolean show) {
    cellView.showLeftText(show);
  }

  @BindingAdapter("cvLeftText")
  public static void setLeftText(@NonNull AviationCellView cellView, @Nullable CharSequence text) {
    cellView.setLeftText(text);
  }

  // right text
  @BindingAdapter("cvShowRightText")
  public static void showRightText(@NonNull AviationCellView cellView, boolean show) {
    cellView.showRightText(show);
  }

  @BindingAdapter("cvRightText")
  public static void setRightText(@NonNull AviationCellView cellView, @Nullable CharSequence text) {
    cellView.setRightText(text);
  }

  // right top text
  @BindingAdapter("cvShowRightTopText")
  public static void showRightTopText(@NonNull AviationCellView cellView, boolean show) {
    cellView.showRightTopText(show);
  }

  @BindingAdapter("cvRightTopText")
  public static void setRightTopText(
      @NonNull AviationCellView cellView,
      @Nullable CharSequence text) {
    cellView.setRightTopText(text);
  }

  // right bottom text
  @BindingAdapter("cvShowRightBottomText")
  public static void showRightBottomText(@NonNull AviationCellView cellView, boolean show) {
    cellView.showRightBottomText(show);
  }

  @BindingAdapter("cvRightBottomText")
  public static void setRightBottomText(
      @NonNull AviationCellView cellView,
      @Nullable CharSequence text) {
    cellView.setRightBottomText(text);
  }

  // right icon
  @BindingAdapter("cvShowRightIcon")
  public static void showRightIcon(@NonNull AviationCellView cellView, boolean show) {
    cellView.showRightIcon(show);
  }

  // left icon point
  @BindingAdapter("cvShowLeftIconPoint")
  public static void showLeftIconPoint(@NonNull AviationCellView cellView, boolean show) {
    cellView.showLeftIconPoint(show);
  }
}
