package com.jxntv.account.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.account.BR;
import com.jxntv.account.R;
import com.jxntv.account.model.Favorite;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;

/**
 * 收藏适配器
 *
 *
 * @since 2020-02-12 14:31
 */
public final class FavoriteAdapter extends BaseDataBindingAdapter<Favorite> {
  //<editor-fold desc="属性">
  @Nullable
  private Listener mListener;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public FavoriteAdapter() {
    super();
  }
  //</editor-fold>

  //<editor-fold desc="API">

  public void setListener(@Nullable Listener listener) {
    mListener = listener;
  }

  //</editor-fold>

  //<editor-fold desc="方法实现">
  @Override
  protected int getItemLayoutId() {
    return R.layout.adapter_favorite;
  }

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    Favorite favorite = mDataList.get(position);
    holder.bindData(BR.favorite, favorite.getFavoriteObservable());
    holder.bindData(BR.position, favorite.getModelPosition());
    holder.bindData(BR.adapter, this);
  }
  //</editor-fold>

  //<editor-fold desc="接口">
  public interface Listener {
    /**
     * 当 Item 点击回调
     *
     * @param view 被点击的视图
     * @param adapter 适配器
     * @param position 位置
     */
    void onItemClick(@NonNull View view, @NonNull FavoriteAdapter adapter, int position);
  }
  //</editor-fold>

  //<editor-fold desc="事件绑定">
  public void onItemClick(@NonNull View view, int position) {
    if (mListener != null) {
      mListener.onItemClick(view, this, position);
    }
  }
  //</editor-fold>
}
