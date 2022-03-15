package com.hzlz.aviation.feature.video.ui.detail.comment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.video.Me;
import com.hzlz.aviation.feature.video.ui.detail.DetailAdapter;
import com.hzlz.aviation.feature.video.ui.viewholder.CommentViewHolder;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.comment.CommentModel;
import com.hzlz.aviation.kernel.base.model.comment.ReplyModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IBaseRecyclerView;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.DateUtils;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.video.R;
import com.hzlz.aviation.feature.video.databinding.VideoSuperCommentItemBinding;

import java.util.Date;
import java.util.List;

public class CommentAdapter extends DetailAdapter<CommentModel, CommentViewHolder> {

    /**
     * 视频id，评论数量变化时发送消息时使用
     */
    private final String mMediaId;

    private StatFromModel mStat;

    private VideoModel videoModel;

    private long commentId;
    //是否回复高亮
    private boolean isReplyPrompt;
    //是否显示了高亮
    private boolean isShowPrompt = false;

    public CommentAdapter(Context context, String mediaId) {
        super(context);
        mMediaId = mediaId;
    }

    public void setStatFromModel(StatFromModel stat, VideoModel videoModel) {
        mStat = stat;
        this.videoModel = videoModel;
    }

    /**
     * 设置高亮id
     *
     * @param commentId commentId
     */
    public void setPromptCommentId(long commentId, int commentType) {
        if (commentId > 0) {
            if (commentType == 1) {
                isReplyPrompt = true;
            }
            this.commentId = commentId;
        }
    }

    @Override
    public CommentViewHolder onCreateVH(ViewGroup parent, int viewType) {
        return new CommentViewHolder(
                VideoSuperCommentItemBinding.inflate(mInflater, parent, false));
    }

    @Override
    public void onBindVH(CommentViewHolder commentViewHolder, int position) {
        VideoSuperCommentItemBinding binding = commentViewHolder.getBinding();
        if (binding == null) {
            return;
        }
        CommentModel commentModel = mList.get(position);

        if (commentModel.replies != null) {
            initReply(binding.recyclerContainer, commentModel, position);
        }

        binding.setComment(commentModel);
        binding.setAdapter(this);
        binding.executePendingBindings();
        binding.content.setText(commentModel.content);
        if (commentModel.imageList == null || commentModel.imageList.size() == 0) {
            binding.commentImage.setVisibility(View.GONE);
        } else {
            binding.commentImage.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(commentModel.soundUrl)) {
            binding.soundView.setVisibility(View.GONE);
        } else {
            binding.soundView.setVisibility(View.VISIBLE);
            binding.soundView.setSoundText(commentModel.soundContent);
            binding.soundView.setTotalSecondTime(commentModel.length);
            binding.soundView.setSoundUrl(commentModel.soundUrl);

            binding.soundView.setPlayOnClickListener(view -> {
                AudioPlayManager.getInstance().start(binding.soundView);
            });
        }
        if (position == 0 && commentId > 0 && !isShowPrompt) {
            //布局高亮
            if (!isReplyPrompt) {
                isShowPrompt = true;
                AsyncUtils.runOnUIThread(() -> {
                    binding.tip.setVisibility(View.VISIBLE);
                    binding.tip.setPressed(true);
                }, 500);
                AsyncUtils.runOnUIThread(() -> {
                    binding.tip.setVisibility(View.GONE);
                    binding.tip.setPressed(false);
                }, 1500);
            }
        }
    }

    private void initReply(RecyclerView recyclerContainer, CommentModel commentModel, int position) {
        ReplyAdapter replyAdapter = new ReplyAdapter(mContext);

        if (position == 0 && isReplyPrompt && !isShowPrompt) {
            replyAdapter.setPromptCommentId(commentId);
            isShowPrompt = true;
        }
        recyclerContainer.setAdapter(replyAdapter);
        recyclerContainer.setHasFixedSize(false); //删除回复时，需要更新recyclerView高度
        recyclerContainer.setFocusable(false); //添加回复后，不能抢占焦点滑动
        recyclerContainer.setLayoutManager(new LinearLayoutManager(mContext));
        replyAdapter.refreshData(commentModel.replies);
        //当作presenter使用
        ReplyViewModel replyViewModel = new ReplyViewModel(
                replyAdapter,
                new IBaseRecyclerView() {
                }
        );
        replyViewModel.setMediaId(mMediaId);
        replyViewModel.setVideoModel(videoModel);
        replyViewModel.setPrimaryId(commentModel.primaryId);
        replyViewModel.setPid(mStat.pid);
        replyViewModel.setData(commentModel.replies);
        replyAdapter.mActionLiveData.observe(
                (LifecycleOwner) mContext,
                actionModel -> {
                    if (actionModel.type == ACTION_AVATAR) {
                        CommentModel m = CommentModel.Builder.aCommentModel()
                                .withFromUser(actionModel.model.user)
                                .build();
                        ActionModel<CommentModel> action = new ActionModel<>(m, actionModel.type);
                        mActionLiveData.setValue(action);
                    } else if (actionModel.type == ACTION_TO_USER) {
                        CommentModel m = CommentModel.Builder.aCommentModel()
                                .withFromUser(actionModel.model.toUser)
                                .build();
                        ActionModel<CommentModel> action = new ActionModel<>(m, actionModel.type);
                        mActionLiveData.setValue(action);
                    } else if (actionModel.type == ACTION_REMOVE) {
                        replyViewModel.deleteReply(actionModel.model);
                        statDelComment();
                    } else if (actionModel.type == ACTION_REPLY) {
                        showInputImageAudioPanel(actionModel.model, replyViewModel);
                    } else if (actionModel.type == ACTION_REPORT) {
                        PluginManager.get(AccountPlugin.class).showReportDialog(
                                mContext,
                                String.valueOf(actionModel.model.replyId),
                                2,
                                videoModel == null ? "" : videoModel.getPid()
                        );
                    }
                });
    }

    private void showInputImageAudioPanel(ReplyModel commentModel, ReplyViewModel replyViewModel) {
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
            if(videoModel!=null){
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(videoModel.getPid()),
                        ResourcesUtils.getString(R.string.comment)
                );
            }
            return;
        }
        String replyPrefix = "";
        String commentId;
        if (replyViewModel != null) {
            String name = commentModel.user.getName();
            replyPrefix = mContext.getResources().getString(R.string.video_reply_to_prefix, name);
            commentId = "reply_"+ commentModel.replyId;
        }else {
            commentId = "comment_" + mMediaId;
        }
        AudioPlayManager.getInstance().release();
        PluginManager.get(RecordPlugin.class).showCommentInputDialog(
                mContext,
                replyPrefix,
                commentId,
                RecordPlugin.DIALOG_TXT,
                new RecordPlugin.CommentListener() {

                    @Override
                    public void imageResult(String content, List<String> imageList) {
                        if (replyViewModel == null) {
                            return;
                        }
                        replyViewModel.addReply(content, imageList, "", "", "", 0, commentModel.user.getId());
                    }

                    @Override
                    public void soundResult(String content, String soundUrl, String soundFilePath, long soundLength, String soundTxt) {
                        if (replyViewModel == null) {
                            return;
                        }
                        replyViewModel.addReply(content, null, soundUrl, soundFilePath, soundTxt, (int) soundLength, commentModel.user.getId());
                    }
                }
        );

    }

    public String data2Text(Date date) {
        return DateUtils.getChineseYMD(date);
    }

    public boolean isDeleteVisible(CommentModel commentModel) {
        if (commentModel != null && commentModel.commentUser != null) {
            if (!commentModel.canComment()) {
                return false;
            }
            return TextUtils.equals(commentModel.commentUser.getId(), Me.getMeInfo().getId());
        }
        return false;
    }

    public boolean isReplyVisible(CommentModel commentModel) {
        if (commentModel != null) {
            return commentModel.canComment();
        }
        return true;
    }

    public void onAvatarClick(View v, CommentModel commentModel) {
        mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_AVATAR));
    }

    public void onDeleteClick(View v, CommentModel commentModel) {
        mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_REMOVE));
    }

    public void onReplyClick(View v, CommentModel commentModel) {
        mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_REPLY));
    }

    public void onReportClick(View v, CommentModel commentModel) {
        mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_REPORT));
    }

    public void onPraiseClick(View v, CommentModel commentModel) {
        mActionLiveData.postValue(new ActionModel<>(commentModel, ACTION_PRAISE));
    }


    private void statAddComment() {
        String commentType = "2";
        String comment = "1";
        JsonObject ds = createDsComment(commentType, comment);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withEv(StatConstants.EV_COMMENT)
                .withDs(ds.toString())
                .withPid(mStat != null ? mStat.pid : "")
                .build();

        GVideoStatManager.getInstance().stat(statEntity);
    }

    private void statDelComment() {
        String commentType = "2";
        String comment = "0";
        JsonObject ds = createDsComment(commentType, comment);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withEv(StatConstants.EV_COMMENT)
                .withDs(ds.toString())
                .withPid(mStat != null ? mStat.pid : "")
                .build();

        GVideoStatManager.getInstance().stat(statEntity);
    }

    private JsonObject createDsComment(String commentType, String comment) {
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(mStat);
        ds.addProperty(StatConstants.DS_KEY_COMMENT_TYPE, commentType);
        ds.addProperty(StatConstants.DS_KEY_COMMENT, comment);
        return ds;
    }
}
