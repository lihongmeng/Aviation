package com.hzlz.aviation.kernel.base.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 数据绑定的 ViewHolder
 *
 *
 * @since 2020-01-14 20:49
 */
public class DataBindingViewHolder<VDB extends ViewDataBinding>
    extends RecyclerView.ViewHolder {
  //<editor-fold desc="属性">
  // 使用 public, 因为 RecyclerView.ViewHolder 的属性也是 public 的
  public VDB binding;
  //</editor-fold>

  //<editor-fold desc="构造函数">
  public DataBindingViewHolder(@NonNull ViewGroup parent, @LayoutRes int itemLayoutId) {
    this(DataBindingUtil.inflate(
        LayoutInflater.from(parent.getContext()),
        itemLayoutId,
        parent,
        false
    ));
  }

  private DataBindingViewHolder(@NonNull VDB binding) {
    super(binding.getRoot());
    this.binding = binding;
  }

  public DataBindingViewHolder(@NonNull View itemView) {
    super(itemView);
  }
  //</editor-fold>

  //<editor-fold desc="API">

  /**
   * 绑定数据
   *
   * @param variableId 参数 id (BR.*)
   * @param value 参数值
   */
  public void bindData(int variableId, @Nullable Object value) {
    if (binding != null) {
      binding.setVariable(variableId, value);
    }
  }
  //</editor-fold>
}
