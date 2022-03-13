package com.jxntv.android.video.ui.viewholder;

import android.widget.ImageView;
import android.widget.TextView;
import com.jxntv.android.video.databinding.VideoSuperSeriesItemBinding;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;

public class SeriesViewHolder extends BaseRecyclerViewHolder<VideoSuperSeriesItemBinding> {
  public final TextView tvAuthor;
  public final ImageView ivImage;
  public final TextView tvTitle;
  /**
   * 构造方法
   */
  public SeriesViewHolder(
      VideoSuperSeriesItemBinding binding) {
    super(binding);
    tvAuthor = binding.author;
    ivImage = binding.image;
    tvTitle = binding.title;
  }
}
