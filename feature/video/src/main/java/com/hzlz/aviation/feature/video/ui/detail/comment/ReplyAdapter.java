package com.hzlz.aviation.feature.video.ui.detail.comment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.hzlz.aviation.feature.video.Me;
import com.hzlz.aviation.feature.video.ui.detail.DetailAdapter;
import com.hzlz.aviation.feature.video.ui.viewholder.ReplyViewHolder;
import com.hzlz.aviation.kernel.base.model.comment.ReplyModel;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.feature.video.databinding.VideoSuperReplyItemBinding;

import java.util.Date;

public class ReplyAdapter extends DetailAdapter<ReplyModel, ReplyViewHolder> {

  private long relyId;
  //是否显示高亮
  private boolean isShowPrompt = false;

  public ReplyAdapter(Context context) {
    super(context);
  }

  @Override public ReplyViewHolder onCreateVH(ViewGroup parent, int viewType) {
    ReplyViewHolder vh = new ReplyViewHolder(
        VideoSuperReplyItemBinding.inflate(mInflater, parent, false));
    return vh;
  }

  public void setPromptCommentId(long replyId){
    this.relyId = replyId;
  }

  @Override public void onBindVH(ReplyViewHolder commentViewHolder, int position) {
    ReplyModel replyModel = mList.get(position);

    VideoSuperReplyItemBinding binding = commentViewHolder.getBinding();

    binding.setReply(replyModel);
    binding.setAdapter(this);
    binding.executePendingBindings();

    binding.content.setText(replyModel.replyContent);
    if (replyModel.imageList==null||replyModel.imageList.size()==0){
      binding.commentImage.setVisibility(View.GONE);
    }else {
      binding.commentImage.setVisibility(View.VISIBLE);
    }
    if (TextUtils.isEmpty(replyModel.soundUrl)){
      binding.soundView.setVisibility(View.GONE);
    }else {
      binding.soundView.setVisibility(View.VISIBLE);
      binding.soundView.setSoundText(replyModel.soundContent);
      binding.soundView.setTotalSecondTime(replyModel.length);
      binding.soundView.setSoundUrl(replyModel.soundUrl);
      binding.soundView.setPlayOnClickListener(view -> {
        AudioPlayManager.getInstance().start(binding.soundView);
      });
    }

    if (relyId > 0 && !isShowPrompt) {
      if (replyModel.replyId == relyId) {
        isShowPrompt = true;
        //布局高亮
        AsyncUtils.runOnUIThread(() -> {
          binding.tip.setPressed(true);
          binding.tip.setVisibility(View.VISIBLE);
        },500);
        AsyncUtils.runOnUIThread(() -> {
          binding.tip.setPressed(false);
          binding.tip.setVisibility(View.GONE);
        },1500);
      }
    }
  }

  public String data2Text(Date date) {
    return DateUtils.getChineseYMD(date);
  }

  public boolean isDeleteVisible(ReplyModel commentModel) {
    if (commentModel != null && commentModel.user != null) {
      if (!commentModel.canComment()) {
        return false;
      }
      if (TextUtils.equals(commentModel.user.getId(), Me.getMeInfo().getId())) {
        return true;
      }
    }
    return false;
  }

  public boolean isReplyVisible(ReplyModel commentModel) {
    if (commentModel != null) {
      if (!commentModel.canComment()) {
        return false;
      }
    }
    return true;
  }

  public void onNameClick(View v, ReplyModel commentModel) {
    mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_TO_USER));
  }

  public void onAvatarClick(View v, ReplyModel commentModel) {
    mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_AVATAR));
  }

  public void onDeleteClick(View v, ReplyModel commentModel) {
    mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_REMOVE));
  }

  public void onReplyClick(View v, ReplyModel commentModel) {
    mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_REPLY));
  }

  public void onReportClick(View v, ReplyModel commentModel) {
    mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_REPORT));
  }

}
