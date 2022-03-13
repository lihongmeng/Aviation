package com.jxntv.pptv.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.pptv.databinding.AdapterChannelMediaBinding;

@Deprecated
final class ItemViewHolder extends DataBindingViewHolder<AdapterChannelMediaBinding> {
  ItemViewHolder(@NonNull View itemView) {
    super(itemView);
    binding = DataBindingUtil.bind(itemView);
  }
}
