package com.hzlz.aviation.feature.video.ui.detail.comment;

import static com.hzlz.aviation.feature.video.ui.detail.DetailAdapter.ACTION_AVATAR;
import static com.hzlz.aviation.feature.video.ui.detail.DetailAdapter.ACTION_PRAISE;
import static com.hzlz.aviation.feature.video.ui.detail.DetailAdapter.ACTION_REMOVE;
import static com.hzlz.aviation.feature.video.ui.detail.DetailAdapter.ACTION_REPLY;
import static com.hzlz.aviation.feature.video.ui.detail.DetailAdapter.ACTION_REPORT;
import static com.hzlz.aviation.feature.video.ui.detail.DetailAdapter.ACTION_TO_USER;
import static com.hzlz.aviation.kernel.base.Constant.EVENT_BUS_EVENT.SEND_VIDEO_DATA_TO_PLAY;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.video.Constants;
import com.hzlz.aviation.feature.video.ui.detail.DetailFragment;
import com.hzlz.aviation.feature.video.ui.detail.DetailViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.comment.CommentModel;
import com.hzlz.aviation.kernel.base.model.video.AuthorModel;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.placeholder.PlaceholderType;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.video.R;

import java.util.List;

public class CommentFragment extends DetailFragment<CommentModel> {

    private CommentViewModel commentViewModel;

    private CommentInputTxtDialog commentInputTxtDialog;

    public CommentAdapter commentAdapter;

    @Override
    protected void bindViewModels() {
        boolean canComment =
                getArguments() == null || getArguments().getBoolean(Constants.EXTRA_CAN_COMMENT, true);
        commentViewModel = bingViewModel(CommentViewModel.class);
        commentViewModel.setComment(commentId, commentType, true);
        commentViewModel.setVideoModel(videoModel);
        commentViewModel.setMediaId(mediaId);
        commentViewModel.setCanComment(canComment);
        commentViewModel.getNoMoreLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean noMoreData) {
                mRefreshLayout.setNoMoreData(noMoreData);
            }
        });
    }

    /**
     * 显示文字输入dialog
     */
    public void onClickShowTxtInputDialog() {
        commentViewModel.showInputAllDialog(getContext(), RecordPlugin.DIALOG_TXT, null);
    }

    /**
     * 图文、语音输入dialog
     */
    public void onClickShowAudioDialog() {
        commentViewModel.showInputAllDialog(getContext(), RecordPlugin.DIALOG_AUDIO, null);
    }

    /**
     * 图文、语音输入dialog
     */
    public void onClickShowImageDialog() {
        commentViewModel.showInputAllDialog(getContext(), RecordPlugin.DIALOG_IMAGE, null);
    }

    @NonNull
    @Override
    protected DetailViewModel<CommentModel> getDetailViewModel() {
        return commentViewModel;
    }

    @Override
    protected void loadData() {
        super.loadData();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (positionStart == 0 && itemCount == 1) {
                    mRecyclerView.scrollToPosition(0);
                    GVideoEventBus.get(Constants.EXTRA_COMMENT).post(0);
                }
            }
        });
        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.SHOW_COMMENT_INPUT_PANEL, CommentModel.class).observe(
                this,
                commentModel -> {
                    if (!isVisible()) return;
                    showCommentInputPanel(commentModel);
                }
        );

        GVideoEventBus.get(SEND_VIDEO_DATA_TO_PLAY, VideoModel.class).observe(
                this,
                videoModel -> {
                    commentViewModel.setMediaId(videoModel.getId());
                    onRefresh(mBinding.refreshLayout);
                }
        );
    }

    @NonNull
    @Override
    protected BaseRecyclerAdapter<CommentModel, ?> createAdapter() {
        commentAdapter = new CommentAdapter(requireContext(), mediaId);
        commentAdapter.setStatFromModel(getStat(), getVideoModel());
        commentAdapter.setPromptCommentId(commentId, commentType);
        commentAdapter.mActionLiveData.observe(this,
                actionModel -> {
                    if (actionModel.type == ACTION_AVATAR
                            || actionModel.type == ACTION_TO_USER) {
                        navigateToUGCPage(actionModel.model.commentUser);
                    } else if (actionModel.type == ACTION_REMOVE) {
                        commentViewModel.deleteComment(actionModel.model);
                        statDelComment();
                    } else if (actionModel.type == ACTION_REPLY) {
                        showCommentInputPanel(actionModel.model);
                    }else if (actionModel.type == ACTION_PRAISE) {
                        commentViewModel.praiseComment(actionModel.model);
                    }else if (actionModel.type == ACTION_REPORT){
                        PluginManager.get(AccountPlugin.class).showReportDialog(
                                getContext(),
                                String.valueOf(actionModel.model.primaryId),
                                1,
                                videoModel == null ? "" : videoModel.getPid()
                        );
                    }
                });
        return commentAdapter;
    }

    private void showCommentInputPanel(CommentModel commentModel) {
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.comment)
            );
            return;
        }
        String replyPrefix = "";
        String commentId;
        AuthorModel user = commentModel != null ? commentModel.commentUser : null;
        if (commentModel != null) {
            String name = user.getName();
            replyPrefix = getString(R.string.video_reply_to_prefix, name);
            commentId = "reply_" + commentModel.primaryId;
        } else {
            commentId = "comment" + mediaId;
        }
        AudioPlayManager.getInstance().release();
        PluginManager.get(RecordPlugin.class).showCommentInputDialog(getActivity(),
                replyPrefix, commentId, RecordPlugin.DIALOG_TXT, new RecordPlugin.CommentListener() {

                    @Override
                    public void imageResult(String content, List<String> imageList) {
                        if (commentViewModel == null) {
                            return;
                        }
                        commentViewModel.addComment(content, imageList, "", "", 0, commentModel);
                    }

                    @Override
                    public void soundResult(String content, String soundUrl, String soundFilePath, long soundLength, String soundTxt) {
                        if (commentViewModel == null) {
                            return;
                        }
                        commentViewModel.addComment(content, null, soundUrl, soundTxt, (int) soundLength, commentModel);
                    }
                }
        );
    }

    private void navigateToUGCPage(AuthorModel authorModel) {
        PluginManager.get(AccountPlugin.class).startPgcActivity(mBinding.recycler, authorModel);
    }

    private void statAddComment(boolean reply) {
        String commentType = reply ? "2" : "1";
        String comment = "1";
        JsonObject ds = createDsComment(commentType, comment);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withEv(StatConstants.EV_COMMENT)
                .withDs(ds.toString())
                .withPid(getPid())
                .build();

        GVideoStatManager.getInstance().stat(statEntity);
    }

    private void statDelComment() {
        String commentType = "1";
        String comment = "0";
        JsonObject ds = createDsComment(commentType, comment);
        StatEntity statEntity = StatEntity.Builder.aStatEntity()
                .withEv(StatConstants.EV_COMMENT)
                .withDs(ds.toString())
                .withPid(getPid())
                .build();

        GVideoStatManager.getInstance().stat(statEntity);
    }

    private JsonObject createDsComment(String commentType, String comment) {
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(getStat());
        ds.addProperty(StatConstants.DS_KEY_COMMENT_TYPE, commentType);
        ds.addProperty(StatConstants.DS_KEY_COMMENT, comment);
        return ds;
    }

    @Override
    public void onReload(@NonNull View view) {
        updatePlaceholderLayoutType(PlaceholderType.LOADING);
        onRefresh(mBinding.refreshLayout);
    }

}
