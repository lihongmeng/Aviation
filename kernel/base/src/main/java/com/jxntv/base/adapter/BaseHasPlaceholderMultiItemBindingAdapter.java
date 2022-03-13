package com.jxntv.base.adapter;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jxntv.base.placeholder.PlaceholderListener;
import com.jxntv.base.placeholder.PlaceholderType;
import java.util.List;

/**
 * 有占位的多 Item Adapter 基类
 *
 *
 * @since 2020-02-28 20:06
 */
@SuppressWarnings("WeakerAccess")
public abstract class BaseHasPlaceholderMultiItemBindingAdapter<T extends IAdapterModel>
    extends BaseMultiItemDataBindingAdapter<T> {
  //<editor-fold desc="常量">
  public static final int ITEM_TYPE_PLACEHOLDER = -1;
  public static final int ITEM_TYPE_NO_MORE_DATA = -2;
  //</editor-fold>

  //<editor-fold desc="属性">
  protected boolean mEnablePlaceholder;
  private boolean mHasCheckGridLayoutManager;
  private boolean mAutoCheckGridLayoutManager;
  @PlaceholderType
  private int mPlaceholderType = PlaceholderType.LOADING;
  @Nullable
  private PlaceholderListener mPlaceholderListener;
  private boolean mNoMoreData = false;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public BaseHasPlaceholderMultiItemBindingAdapter() {
    this(true, true);
  }

  /**
   * @param enablePlaceholder 是否启用占位
   * @param autoCheckGridLayoutManager 是否自动检测 GridLayoutManager
   */
  public BaseHasPlaceholderMultiItemBindingAdapter(
      boolean enablePlaceholder,
      boolean autoCheckGridLayoutManager) {
    mEnablePlaceholder = enablePlaceholder;
    mAutoCheckGridLayoutManager = autoCheckGridLayoutManager;
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 显示加载中占位视图
   */
  public void showLoadingPlaceholder() {
    if (mEnablePlaceholder && mDataList.isEmpty()) {
      mPlaceholderType = PlaceholderType.LOADING;
      notifyDataSetChanged();
    }
  }

  /**
   * 隐藏占位视图
   */
  public void hidePlaceholder() {
    if (mEnablePlaceholder) {
      mPlaceholderType = PlaceholderType.NONE;
      notifyDataSetChanged();
    }
  }

  /**
   * 显示空的占位视图
   */
  public void showEmptyPlaceholder() {
    if (mEnablePlaceholder && mDataList.isEmpty()) {
      mPlaceholderType = PlaceholderType.EMPTY;
      notifyDataSetChanged();
    }
  }

  /**
   * 显示错误占位视图
   */
  public void showErrorPlaceholder() {
    if (mEnablePlaceholder && mDataList.isEmpty()) {
      mPlaceholderType = PlaceholderType.ERROR;
      notifyDataSetChanged();
    }
  }

  /**
   * 显示网络不可用占位视图
   */
  public void showNetworkNotAvailablePlaceholder() {
    if (mEnablePlaceholder && mDataList.isEmpty()) {
      mPlaceholderType = PlaceholderType.NETWORK_NOT_AVAILABLE;
      notifyDataSetChanged();
    }
  }

  /**
   * 显示未登陆占位视图
   */
  public void showUnLoginPlaceholder() {
    if (mEnablePlaceholder && mDataList.isEmpty()) {
      mPlaceholderType = PlaceholderType.UN_LOGIN;
      notifyDataSetChanged();
    }
  }

  /**
   * 设置占位视图接口
   *
   * @param listener 占位视图接口
   */
  public void setPlaceHolderListener(@Nullable PlaceholderListener listener) {
    mPlaceholderListener = listener;
  }

  /**
   * 显示没有更多数据
   */
  public void showNoMoreData() {
    if (mNoMoreData) {
      return;
    }
    mNoMoreData = true;
    if (!mDataList.isEmpty()) {
      notifyItemInserted(getItemCount());
    }
  }

  /**
   * 隐藏没有更多数据
   */
  public void hideNoMoreData() {
    if (!mNoMoreData) {
      return;
    }
    mNoMoreData = false;
    notifyItemRemoved(getItemCount());
  }
  //</editor-fold>

  //<editor-fold desc="内部方法">

  /**
   * 获取数据部分 ItemView 类型
   *
   * @param position 数据在布局中的位置
   * @return 数据部分 ItemView 类型
   */
  protected int getDataItemViewType(int position) {
    return super.getItemViewType(position);
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  public int getItemCount() {
    if (mEnablePlaceholder && mPlaceholderType != PlaceholderType.NONE) {
      return 1;
    }
    return super.getItemCount() + (mNoMoreData ? 1 : 0);
  }

  @Override
  public final int getItemViewType(int position) {
    if (mEnablePlaceholder && position == 0 && mPlaceholderType != PlaceholderType.NONE) {
      return ITEM_TYPE_PLACEHOLDER;
    }
    if (mNoMoreData && position == mDataList.size()) {
      return ITEM_TYPE_NO_MORE_DATA;
    }
    return getDataItemViewType(position);
  }

  @NonNull
  @Override
  public DataBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    // 检测 LayoutManager 是否为 GridLayoutManager
    checkGridLayoutManager(parent);
    // 判断是否为占位图
    if (viewType == ITEM_TYPE_PLACEHOLDER) {
      return new DefaultPlaceHolderViewHolder(parent);
    }
    if (viewType == ITEM_TYPE_NO_MORE_DATA) {
      return new DefaultNoMoreDataViewHolder(parent);
    }
    return super.onCreateViewHolder(parent, viewType);
  }

  @Override
  public void onBindViewHolder(@NonNull DataBindingViewHolder holder, int position) {
    if (holder instanceof DefaultPlaceHolderViewHolder) {
      DefaultPlaceHolderViewHolder viewHolder = (DefaultPlaceHolderViewHolder) holder;
      viewHolder.setPlaceholderListener(mPlaceholderListener);
      viewHolder.updatePlaceholderType(mPlaceholderType);
      return;
    }
    if (holder instanceof DefaultNoMoreDataViewHolder) {
      return;
    }
    super.onBindViewHolder(holder, position);
  }

  @Override
  public void addData(int index, @Nullable List<T> dataList) {
    super.addData(index, dataList);
    hidePlaceholder();
    showEmptyPlaceholder();
  }

  @Override
  public void removeData(int index, int count) {
    super.removeData(index, count);
    showEmptyPlaceholder();
  }

  @Override
  public void replaceData(@Nullable List<T> dataList) {
    // 判断是否取消移除占位视图
    if (dataList != null
        && !dataList.isEmpty()
        && mEnablePlaceholder
        && mPlaceholderType != PlaceholderType.NONE) {
      mPlaceholderType = PlaceholderType.NONE;
      notifyItemRemoved(0);
    }
    super.replaceData(dataList);
    showEmptyPlaceholder();
  }
  //</editor-fold>

  //<editor-fold desc="私有方法">

  /**
   * 检测是否为 GridLayoutManager
   *
   * @param parent 父布局
   */
  private void checkGridLayoutManager(@NonNull ViewGroup parent) {
    // 是否启用占位图
    // 是否自动检测 GridLayoutManager
    if (!mEnablePlaceholder || mHasCheckGridLayoutManager) {
      return;
    }
    mHasCheckGridLayoutManager = true;
    // 是否检测过 GridLayoutManager
    if (!mAutoCheckGridLayoutManager) {
      return;
    }
    // 检测 Parent 是否为 RecyclerView
    if (!(parent instanceof RecyclerView)) {
      return;
    }
    RecyclerView recyclerView = (RecyclerView) parent;
    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
    // 判断 LayoutManager 是否为 GridLayoutManager
    if (layoutManager == null) {
      return;
    }
    if (!(layoutManager instanceof GridLayoutManager)) {
      return;
    }
    GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
      @Override
      public int getSpanSize(int position) {
        int viewType = getItemViewType(position);
        if (viewType == ITEM_TYPE_PLACEHOLDER || viewType == ITEM_TYPE_NO_MORE_DATA) {
          return gridLayoutManager.getSpanCount();
        }
        return 1;
      }

      @Override
      public int getSpanIndex(int position, int spanCount) {
        return position % spanCount;
      }
    });
  }
  //</editor-fold>
}
