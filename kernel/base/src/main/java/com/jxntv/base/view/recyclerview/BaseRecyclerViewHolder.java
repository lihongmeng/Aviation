package com.jxntv.base.view.recyclerview;

import android.view.View;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * recyclerViewHolder基类
 *
 *
 * @since 2020.1.17
 */
public class BaseRecyclerViewHolder<VBH extends ViewDataBinding> extends RecyclerView.ViewHolder {

  /** 持有的数据绑定模型 */
  protected VBH mBinding;

  /**
   * 构造方法
   */
  public BaseRecyclerViewHolder(VBH binding) {
    super(binding.getRoot());
    mBinding = binding;
  }

  /**
   * 构造方法
   */
  public BaseRecyclerViewHolder(View view) {
    super(view);
  }

  /**
   * 获取当前持有的绑定数据模型
   *
   * @return 数据绑定模型
   */
  @Nullable
  public VBH getBinding() {
    return mBinding;
  }

}
