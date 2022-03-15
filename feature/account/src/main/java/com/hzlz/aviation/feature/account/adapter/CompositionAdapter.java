package com.hzlz.aviation.feature.account.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hzlz.aviation.feature.account.R;
import com.hzlz.aviation.kernel.base.adapter.DataBindingViewHolder;

/**
 * 作品适配器
 */
public final class CompositionAdapter extends AbstractMediaAdapter {
  //<editor-fold desc="属性">
  @Nullable
  private Listener mListener;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public CompositionAdapter() {
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
    return R.layout.adapter_composition;
  }

  @Override
  protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    super.bindData(holder, position);
  }
  //</editor-fold>

  //<editor-fold desc="接口">
  public interface Listener {
    void onItemClick(@NonNull View view, @NonNull CompositionAdapter adapter, int position);
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
