package com.jxntv.pptv.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.pptv.BR;
import com.jxntv.pptv.R;
import com.jxntv.pptv.model.Category;

public final class CategoryDetailAdapter extends BaseDataBindingAdapter<Category> {
  @Nullable private Listener mListener;
  public void setListener(Listener listener) {
    mListener = listener;
  }
  @Override protected int getItemLayoutId() {
    return R.layout.adapter_category_detail;
  }

  @Override protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    Category category = mDataList.get(position);
    holder.bindData(BR.category, category.getCategoryObservable());
    holder.bindData(BR.adapter, this);
    holder.bindData(BR.position, category.getModelPosition());
  }

  public void onItemRootViewClicked(@NonNull View v, int position) {
    if (mListener != null) {
      mListener.onItemRootViewClicked(v, this, position);
    }
  }

  public interface Listener {
    void onItemRootViewClicked(@NonNull View v, @NonNull CategoryDetailAdapter adapter, final int itemAdapterPosition);
  }
}
