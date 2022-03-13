package com.jxntv.pptv.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.jxntv.base.adapter.DataBindingViewHolder;
import com.jxntv.pptv.databinding.AdapterChannelSectionBinding;

@Deprecated
final class HeaderViewHolder extends DataBindingViewHolder<AdapterChannelSectionBinding> {
  HeaderViewHolder(@NonNull final View view) {
    super(view);
    binding = DataBindingUtil.bind(view);
  }
}
