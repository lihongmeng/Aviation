package com.jxntv.pptv.adapter;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.jxntv.pptv.BR;
import com.jxntv.pptv.R;
import com.jxntv.pptv.model.Channel;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

class MediaSection extends Section {
  private final Channel mChannel;
  private final ChannelAdapter mAdapter;

  public MediaSection(Channel channel, ChannelAdapter adapter) {
    super(SectionParameters.builder()
        .itemResourceId(R.layout.adapter_channel_media)
        .headerResourceId(R.layout.adapter_channel_section)
        .build());
    this.mChannel = channel;
    this.mAdapter = adapter;
  }

  @Override public int getContentItemsTotal() {
    return mChannel.getMedia().size();
  }

  @Override public RecyclerView.ViewHolder getItemViewHolder(View view) {
    return new ItemViewHolder(view);
  }

  @Override public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
    final ItemViewHolder itemHolder = (ItemViewHolder) holder;
    itemHolder.bindData(BR.adapter, mAdapter);
    itemHolder.bindData(BR.channel, mChannel);
    itemHolder.bindData(BR.media, mChannel.getMedia().get(position));
    itemHolder.bindData(BR.position, position);
    itemHolder.binding.executePendingBindings();
  }

  @Override public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
    return new HeaderViewHolder(view);
  }

  @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
    final HeaderViewHolder itemHolder = (HeaderViewHolder) holder;
    itemHolder.bindData(BR.adapter, mAdapter);
    itemHolder.bindData(BR.channel, mChannel);
    itemHolder.binding.executePendingBindings();
  }

}
