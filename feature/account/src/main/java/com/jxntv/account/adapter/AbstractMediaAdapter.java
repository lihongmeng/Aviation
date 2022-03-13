package com.jxntv.account.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.jxntv.account.BR;
import com.jxntv.account.model.Media;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;

/**
 * 媒体适配器
 *
 *
 * @since 2020-02-13 23:13
 */
public abstract class AbstractMediaAdapter extends BaseDataBindingAdapter<Media> {

  //<editor-fold desc="属性">
  @Nullable
  private OneColumnMediaAdapter.Listener mListener;
  //</editor-fold>

  //<editor-fold desc="属性">
  public AbstractMediaAdapter() {
    super();
  }
  //</editor-fold>

  //<editor-fold desc="API">

  public void setListener(@Nullable OneColumnMediaAdapter.Listener listener) {
    mListener = listener;
  }
  //</editor-fold>

  //<editor-fold desc="方法实现">

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    Media media = mDataList.get(position);
    holder.bindData(BR.adapter, this);
    holder.bindData(BR.position, media.getModelPosition());
    holder.bindData(BR.media, media.getObservable());
  }
  //</editor-fold>

  //<editor-fold desc="接口">
  public interface Listener {
    void onItemClick(@NonNull View view, @NonNull RecyclerView.Adapter adapter, int position);
    default void onUserClick(@NonNull View view, @NonNull RecyclerView.Adapter adapter, int position){}
    void onMore(@NonNull View view, @NonNull RecyclerView.Adapter adapter, int position);
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void onItemClick(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onItemClick(view, this, position);
    }
  }

  public void onMore(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onMore(view, this, position);
    }
  }

  public void onUserClick(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onUserClick(view, this, position);
    }
  }
  //</editor-fold>
}
