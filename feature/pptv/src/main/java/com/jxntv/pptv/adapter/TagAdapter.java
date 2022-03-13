package com.jxntv.pptv.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.jxntv.base.adapter.BaseDataBindingAdapter;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.pptv.BR;
import com.jxntv.pptv.R;
import com.jxntv.pptv.model.Tag;

public final class TagAdapter extends BaseDataBindingAdapter<Tag> {
  @Nullable private Listener mListener;
  @Override protected int getItemLayoutId() {
    return R.layout.adapter_tag;
  }

  public void setListener(Listener listener) {
    mListener = listener;
  }

  @Override protected void bindData(@NonNull DataBindingViewHolder holder, int position) {
    Tag tag = mDataList.get(position);
    holder.bindData(BR.tag, tag);
    holder.bindData(BR.adapter, this);
    holder.bindData(BR.position, tag.getModelPosition());
  }

  public void onItemRootViewClicked(@NonNull View v, int position) {
    if (mListener != null) {
      mListener.onItemRootViewClicked(v, this, position);
    }
  }

  public interface Listener {
    void onItemRootViewClicked(@NonNull View v, @NonNull TagAdapter adapter, final int itemAdapterPosition);
  }
}
