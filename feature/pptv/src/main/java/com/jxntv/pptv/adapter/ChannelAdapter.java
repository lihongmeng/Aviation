package com.jxntv.pptv.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import com.jxntv.pptv.model.Channel;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import java.util.List;

public final class ChannelAdapter extends SectionedRecyclerViewAdapter {
  private Listener mSectionListener;
  public ChannelAdapter() {
    super();
  }

  public void setSectionListener(Listener sectionListener) {
    mSectionListener = sectionListener;
  }

  public void replace(@NonNull List<Channel> list) {
    removeAllSections();
    for (Channel channel : list) {
      addSection(new MediaSection(channel, this));
    }
    notifyDataSetChanged();
  }

  public void onHeaderMoreButtonClicked(@NonNull View v, @NonNull Channel channel) {
    if (mSectionListener != null) {
      mSectionListener.onHeaderMoreButtonClicked(v, channel);
    }
  }

  public void onItemRootViewClicked(@NonNull View v, @NonNull Channel channel, int itemAdapterPosition) {
    if (mSectionListener != null) {
      mSectionListener.onItemRootViewClicked(v, channel, itemAdapterPosition);
    }
  }

  public interface Listener {
    void onHeaderMoreButtonClicked(@NonNull View v, @NonNull final Channel channel);
    void onItemRootViewClicked(@NonNull View v, @NonNull final Channel channel, final int itemAdapterPosition);
  }
}
