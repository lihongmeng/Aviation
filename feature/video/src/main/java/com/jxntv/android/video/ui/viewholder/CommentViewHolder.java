package com.jxntv.android.video.ui.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.jxntv.android.video.databinding.VideoSuperCommentItemBinding;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewHolder;
import com.jxntv.widget.FoldTextView;

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
