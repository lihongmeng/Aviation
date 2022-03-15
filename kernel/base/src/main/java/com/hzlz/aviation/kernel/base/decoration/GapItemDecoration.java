package com.hzlz.aviation.kernel.base.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 间距 ItemDecoration
 *
 *
 * @since 2020-02-12 16:57
 */
public final class GapItemDecoration extends RecyclerView.ItemDecoration {
  //<editor-fold desc="属性">
  // 水平方向的间距
  private int mHorizontalGap;
  // 垂直方向的间距
  private int mVerticalGap;
  // 第一行的上间距
  private int mFistRowTopGap;
  // 最后一行的下间距
  private int mLastRowBottomGap;
  // 用于 GridLayout 的item 的宽度
  private int mItemWidthForGridLayoutManager;
  //</editor-fold>

  //<editor-fold desc="构造函数">

  public GapItemDecoration() {
  }

  //</editor-fold>

  //<editor-fold desc="API">

  public void setHorizontalGap(int horizontalGap) {
    mHorizontalGap = horizontalGap;
  }

  public void setVerticalGap(int verticalGap) {
    mVerticalGap = verticalGap;
  }

  public void setFistRowTopGap(int fistRowTopGap) {
    mFistRowTopGap = fistRowTopGap;
  }

  public void setLastRowBottomGap(int lastRowBottomGap) {
    mLastRowBottomGap = lastRowBottomGap;
  }

  public void setItemWidthForGridLayoutManager(int itemWidthForGridLayoutManager) {
    mItemWidthForGridLayoutManager = itemWidthForGridLayoutManager;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  public void getItemOffsets(
      @NonNull Rect outRect,
      @NonNull View view,
      @NonNull RecyclerView parent,
      @NonNull RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);
    RecyclerView.Adapter adapter = parent.getAdapter();
    if (adapter == null) {
      return;
    }
    int itemCount = adapter.getItemCount();
    if (itemCount < 1) {
      return;
    }
    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
    int position = parent.getChildAdapterPosition(view);
    if (layoutManager instanceof GridLayoutManager) {
      processGridLayout(
          position,
          itemCount,
          ((GridLayoutManager) layoutManager).getSpanCount(),
          parent.getWidth() - parent.getPaddingStart() - parent.getPaddingEnd(),
          outRect
      );
    } else if (layoutManager instanceof LinearLayoutManager) {
      processLinearLayout(position, itemCount, outRect);
    }
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">
  private void processGridLayout(
      int position,
      int itemCount,
      int spanCount,
      int itemTotalWidth,
      @NonNull Rect outRect) {
    int row = position / spanCount;
    boolean isLastRow = (itemCount - 1) / spanCount == row;
    int column = position % spanCount;
    // 计算 item 之间的 gap
    int horizontalGap = mHorizontalGap;
    if (mItemWidthForGridLayoutManager > 0) {
      int expectItemGap =
          (itemTotalWidth - mItemWidthForGridLayoutManager * spanCount) / (spanCount - 1);
      int itemBlankSpace = itemTotalWidth / spanCount - mItemWidthForGridLayoutManager;
      horizontalGap = column * (expectItemGap - itemBlankSpace);
    }
    // 第一列设置最左边间距，其他水平间距
    outRect.left = horizontalGap;
    // 第一行设置最上方间距其他设置垂直间距
    outRect.top = row == 0 ? mFistRowTopGap : mVerticalGap;
    // 最后一行设置最下方间距，其他 0
    outRect.bottom = isLastRow ? mLastRowBottomGap : 0;
  }

  private void processLinearLayout(
      int position,
      int itemCount,
      @NonNull Rect outRect) {
    // 第一个设置最上方间距其他设置垂直间距
    outRect.top = position == 0 ? mFistRowTopGap : mVerticalGap;
    // 最后一个设置最下方间距，其他 0
    outRect.bottom = position + 1 == itemCount ? mLastRowBottomGap : 0;
  }
  //</editor-fold>
}
