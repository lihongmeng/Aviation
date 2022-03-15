package com.hzlz.aviation.feature.video.ui.detail.comment;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableInt;

import com.google.gson.JsonObject;
import com.hzlz.aviation.feature.video.repository.CommentRepository;
import com.hzlz.aviation.feature.video.ui.detail.DetailModelList;
import com.hzlz.aviation.feature.video.ui.detail.DetailViewModel;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.comment.CommentModel;
import com.hzlz.aviation.kernel.base.model.comment.ReplyModel;
import com.hzlz.aviation.kernel.base.model.stat.StatFromModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.AccountPlugin;
import com.hzlz.aviation.kernel.base.plugin.RecordPlugin;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.media.player.AudioPlayManager;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.kernel.stat.stat.GVideoStatManager;
import com.hzlz.aviation.kernel.stat.stat.StatConstants;
import com.hzlz.aviation.kernel.stat.stat.StatPid;
import com.hzlz.aviation.kernel.stat.stat.db.entity.StatEntity;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.util.ResourcesUtils;
import com.hzlz.aviation.feature.video.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

public class CommentViewModel extends DetailViewModel<CommentModel> {
    private CommentRepository repository = new CommentRepository();
    /**
     * 计数器，删除主评论时记录需要更新评论数（主评论+回复评论列表）
     */
    private int mDeleteCommentCount = 0;
    private boolean mCanComment;
    private boolean isQaType;

    //问答类型
    public void isQaType() {
        isQaType = true;
    }

    public CommentViewModel(@NonNull Application application) {
        super(application);
        isComment=true;
    }

    public void setCanComment(boolean canComment) {
        mCanComment = canComment;
    }

    /**
     * 显示文字输入dialog
     */
    public void onClickShowTxtInputDialog(View v) {
        showInputAllDialog(v.getContext(), RecordPlugin.DIALOG_TXT, null);
    }

    /**
     * 图文、语音输入dialog
     */
    public void onClickShowAudioDialog(View v) {
        showInputAllDialog(v.getContext(), RecordPlugin.DIALOG_AUDIO, null);
    }

    /**
     * 图文、语音输入dialog
     */
    public void onClickShowImageDialog(View v) {
        showInputAllDialog(v.getContext(), RecordPlugin.DIALOG_IMAGE, null);
    }

    public void showInputAllDialog(Context context, String type, CommentModel commentModel) {
        AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
        if (!accountPlugin.hasLoggedIn()) {
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.comment)
            );
            accountPlugin.startLoginActivity(context);
            return;
        }

        String replyPrefix = "";
        String commentId = "comment_"+ mMediaId;;
        if (commentModel != null) {
            String name = commentModel.commentUser.getName();
            replyPrefix = context.getResources().getString(R.string.video_reply_to_prefix, name);
            commentId = "reply_"+ commentModel.primaryId;
        } else if (isQaType){
            //问答时需要修改hint
            replyPrefix = "写下你的回答...";
        }
        AudioPlayManager.getInstance().release();
        PluginManager.get(RecordPlugin.class).showCommentInputDialog(
                context,
                replyPrefix,
                commentId,
                type,
                new RecordPlugin.CommentListener() {
                    @Override
                    public void imageResult(String content, List<String> imageList) {
                        addImageComment(content, imageList, commentModel);
                        statAddComment(commentModel == null);
                    }

                    @Override
                    public void soundResult(String content, String soundUrl, String soundFilePath, long soundLength, String soundTxt) {
                        localFilePath = soundFilePath;
                        addSoundComment(content, soundUrl, soundTxt, (int) soundLength, commentModel);
                        statAddComment(commentModel == null);
                    }
                });
    }

    @Override
    protected Observable<CommentModel> createInsertObservable(CommentModel commentModel) {
        Observable<CommentModel> response;
        response = repository.comment(mMediaId, mGroupId,commentModel,isQaType);
        return response;
    }

    @Override
    protected Observable<CommentModel> createRemoveObservable(CommentModel commentModel) {
        Observable<Object> observable;
        observable = repository.deleteComment(String.valueOf(commentModel.primaryId));
        return observable.map(new Function<Object, CommentModel>() {
            @Override
            public CommentModel apply(Object success) throws Exception {
                return success != null ? commentModel : null;
            }
        });
    }

    @Override
    protected Observable<CommentModel> createUpdateObservable(CommentModel commentModel) {
        Observable<CommentModel> response;
        String primaryId = String.valueOf(commentModel.primaryId);
        long toId = -1;
        if (commentModel.commentUser != null) {
            toId = Long.parseLong(commentModel.commentUser.getId());
        }
        int size = commentModel.replies.size();
//        String content = commentModel.replies.get(size - 1).replyContent;

        ReplyModel model = commentModel.replies.get(size - 1);

        response = repository.reply(primaryId, toId, model).map(
                new Function<ReplyModel, CommentModel>() {
                    @Override
                    public CommentModel apply(ReplyModel replyModel) throws Exception {
                        if (replyModel != null) {
                            if (!TextUtils.isEmpty(localFilePath) && TextUtils.isEmpty(replyModel.soundUrl)) {
                                replyModel.soundUrl = localFilePath;
                                localFilePath = "";
                            }
                            int size = commentModel.replies.size();
                            commentModel.replies.remove(size - 1);
                            commentModel.replies.add(replyModel);
                        } else {
                            commentModel.replies.remove(0);
                        }
                        return commentModel;
                    }
                });
        return response;
    }

    @Override
    protected Observable<DetailModelList<CommentModel>> createLoadObservable(String mediaId, int page) {
        return repository.loadComment(mediaId, page, mCanComment, commentId, commentType).map(commentResponse -> {
            if (commentResponse == null) return null;
            return new DetailModelList<>(commentResponse.getList(),
                    commentResponse.getPage().hasNextPage());
        });
    }


    public void addComment(String content, CommentModel replyToComment) {
        addComment(content, null, "", "", 0, replyToComment);
    }

    public void addImageComment(String content, List<String> imageList, CommentModel replyToComment) {
        addComment(content, imageList, "", "", 0, replyToComment);
    }

    public void addSoundComment(String content, String soundUrl, String soundTxt,
                                int soundDuration, CommentModel replyToComment) {
        addComment(content, null, soundUrl, soundTxt, soundDuration, replyToComment);
    }

    public void addComment(
            String content,
            List<String> imageList,
            String soundUrl,
            String soundTxt,
            int soundDuration,
            CommentModel replyToComment
    ) {
        if (replyToComment != null) {
            List<ReplyModel> replyList = new ArrayList<>();
            if (replyToComment.replies != null && !replyToComment.replies.isEmpty()) {
                replyList.addAll(replyToComment.replies);
            }
            ReplyModel reply = ReplyModel.Builder.aReplyModel()
                    .withReplyContent(content)
                    .withImageList(imageList)
                    .withSoundUrl(soundUrl)
                    .withSoundContent(soundTxt)
                    .withLength(soundDuration)
                    .build();
            replyList.add(reply);

            CommentModel newComment = CommentModel.Builder.aCommentModel()
                    .fromComment(replyToComment)
                    .withReplyComment(replyList)
                    .build();

            int size = list.size();
            int i;
            for (i = 0; i < size; i++) {
                CommentModel model = list.get(i);
                if (replyToComment.equals(model)) {
                    break;
                }
            }
            if (i < size) {
                update(i, newComment);
            }
        } else {
            CommentModel model = CommentModel.Builder.aCommentModel()
                    .withContent(content)
                    .withImageList(imageList)
                    .withSoundUrl(soundUrl)
                    .withLength(soundDuration)
                    .withSoundContent(soundTxt)
                    .build();
            insert(0, model);
        }

    }

    public void praiseComment(CommentModel commentModel) {
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
            if(videoModel!=null){
                GVideoSensorDataManager.getInstance().enterRegister(
                        StatPid.getPageName(videoModel.getPid()),
                        ResourcesUtils.getString(R.string.like)
                );
            }
            return;
        }
        repository.commentPraise(commentModel.primaryId, false, !commentModel.getIsPraiseFiled().get())
                .subscribe(new GVideoResponseObserver<Boolean>() {
                    @Override
                    protected void onSuccess(@NonNull Boolean aBoolean) {
                        commentModel.setPraise(aBoolean);
                    }
                });

    }


    public void deleteComment(@NonNull CommentModel comment) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            CommentModel model = list.get(i);
            if (comment.equals(model)) {
                int rCount = model.replies != null ? model.replies.size() : 0;
                mDeleteCommentCount = rCount + 1;
                remove(i);
                break;
            }
        }
        statDelComment();
    }

    @Override
    public void loadSuccess(List<CommentModel> list) {
        super.loadSuccess(list);
        if (loadType == LOAD_DATA_TYPE_INSERT) {
            showToast(R.string.video_comment_send_success);
            ObservableInt commentObservable = InteractDataObservable.getInstance().getCommentObservable(mMediaId);
            int count = commentObservable.get();
            commentObservable.set(++count);
        } else if (loadType == LOAD_DATA_TYPE_UPDATE) {
            //点击主评论回复按钮，会产生一条回复评论记录
            showToast(R.string.video_comment_send_success);
            ObservableInt commentObservable = InteractDataObservable.getInstance().getCommentObservable(mMediaId);
            int count = commentObservable.get();
            commentObservable.set(++count);
        } else if (loadType == LOAD_DATA_TYPE_REMOVE) {
            //点击主评论删除，可能删除主评论+一批回复评论数据
            showToast(R.string.video_comment_delete_success);
            ObservableInt commentObservable = InteractDataObservable.getInstance().getCommentObservable(mMediaId);
            int count = commentObservable.get();
            commentObservable.set(count - mDeleteCommentCount);
            mDeleteCommentCount = 0;
        }
    }

    @Override
    public void loadFailure(Throwable throwable) {
        mDeleteCommentCount = 0;

        // 未加入圈子，需要特殊处理
        // if (throwable instanceof GVideoAPIException) {
        //     GVideoAPIException exception = (GVideoAPIException) throwable;
        //     if (exception.getCode() == CODE_HAS_NOT_JOIN_CIRCLE) {
        //         PluginManager.get(CirclePlugin.class).dealGVideoAPIExceptionError(
        //                 AppManager.getAppManager().currentActivity(),
        //                 exception
        //         );
        //         return;
        //     }
        // }

        if (loadType == LOAD_DATA_TYPE_INSERT
                || loadType == LOAD_DATA_TYPE_UPDATE) {
            showToast(throwable.getMessage());
        } else if (loadType == LOAD_DATA_TYPE_REMOVE) {
            showToast(R.string.video_comment_delete_failure);
        } else {
            showToast(throwable.getMessage());
        }
    }

    @Override
    protected void showToast(int stringResourceId, @Nullable Object... arguments) {
        Toast.makeText(GVideoRuntime.getAppContext(), stringResourceId, Toast.LENGTH_SHORT).show();
    }


    //----------- 埋点相关  ----------
    private StatFromModel mStat;

    public void setStatFromModel(StatFromModel stat, VideoModel videoModel) {
        mStat = stat;
        this.videoModel = videoModel;
    }

    public StatFromModel getmStat() {
        return mStat;
    }

    private void statAddComment(boolean reply) {
        if (mStat == null) return;
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
        if (mStat == null) return;
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
        JsonObject ds = GVideoStatManager.getInstance().createDsContent(mStat);
        ds.addProperty(StatConstants.DS_KEY_COMMENT_TYPE, commentType);
        ds.addProperty(StatConstants.DS_KEY_COMMENT, comment);
        return ds;
    }

}
