package com.hzlz.aviation.feature.video.ui.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.feature.video.databinding.VideoSuperSeriesItemBinding;

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
