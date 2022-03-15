package com.hzlz.aviation.feature.video.ui.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerViewHolder;
import com.hzlz.aviation.library.widget.widget.FoldTextView;
import com.hzlz.aviation.feature.video.databinding.VideoSuperCommentItemBinding;

public class CommentViewHolder extends BaseRecyclerViewHolder<VideoSuperCommentItemBinding> {

  public final ImageView ivAvatar;
  public final TextView tvName;
  public final View vDelete;
  public final TextView tvDate;
  public final FoldTextView tvContent;
  public final View vReply;
  public final ViewGroup tvReplyContainer;

  /**
   * 构造方法
   */
  public CommentViewHolder(
      VideoSuperCommentItemBinding binding) {
    super(binding);

    ivAvatar = binding.avatar;
    tvName = binding.name;
    vDelete = binding.delete;
    tvDate = binding.date;
    tvContent = binding.content;
    vReply = binding.replyAction;
    tvReplyContainer = binding.recyclerContainer;
  }
}
