package com.jxntv.bindingadapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import com.jxntv.widget.GVideoCellView;

/**
 * {@link GVideoCellView} 扩展
 *
 *
 * @since 2020-01-20 15:09
 */
public final class CellViewBindingAdapter {
  // left text
  @BindingAdapter("cvShowLeftText")
  public static void showLeftText(@NonNull GVideoCellView cellView, boolean show) {
    cellView.showLeftText(show);
  }

  @BindingAdapter("cvLeftText")
  public static void setLeftText(@NonNull GVideoCellView cellView, @Nullable CharSequence text) {
    cellView.setLeftText(text);
  }

  // right text
  @BindingAdapter("cvShowRightText")
  public static void showRightText(@NonNull GVideoCellView cellView, boolean show) {
    cellView.showRightText(show);
  }

  @BindingAdapter("cvRightText")
  public static void setRightText(@NonNull GVideoCellView cellView, @Nullable CharSequence text) {
    cellView.setRightText(text);
  }

  // right top text
  @BindingAdapter("cvShowRightTopText")
  public static void showRightTopText(@NonNull GVideoCellView cellView, boolean show) {
    cellView.showRightTopText(show);
  }

  @BindingAdapter("cvRightTopText")
  public static void setRightTopText(
      @NonNull GVideoCellView cellView,
      @Nullable CharSequence text) {
    cellView.setRightTopText(text);
  }

  // right bottom text
  @BindingAdapter("cvShowRightBottomText")
  public static void showRightBottomText(@NonNull GVideoCellView cellView, boolean show) {
    cellView.showRightBottomText(show);
  }

  @BindingAdapter("cvRightBottomText")
  public static void setRightBottomText(
      @NonNull GVideoCellView cellView,
      @Nullable CharSequence text) {
    cellView.setRightBottomText(text);
  }

  // right icon
  @BindingAdapter("cvShowRightIcon")
  public static void showRightIcon(@NonNull GVideoCellView cellView, boolean show) {
    cellView.showRightIcon(show);
  }

  // left icon point
  @BindingAdapter("cvShowLeftIconPoint")
  public static void showLeftIconPoint(@NonNull GVideoCellView cellView, boolean show) {
    cellView.showLeftIconPoint(show);
  }
}
