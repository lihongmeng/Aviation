package com.hzlz.aviation.feature.video.ui.detail.comment;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableInt;

import com.hzlz.aviation.feature.video.repository.CommentRepository;
import com.hzlz.aviation.feature.video.ui.detail.DetailViewModel;
import com.hzlz.aviation.kernel.base.model.comment.ReplyModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.base.view.recyclerview.BaseRecyclerAdapter;
import com.hzlz.aviation.kernel.base.view.recyclerview.interf.IBaseRecyclerView;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.feature.video.R;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;

public class ReplyViewModel extends DetailViewModel<ReplyModel> {
    private CommentRepository repository = new CommentRepository();
    /**
     * 主评论id，回复评论时使用
     */
    private long mPrimaryId;

    /**
     * @param adapter
     * @param view
     */
    public ReplyViewModel(BaseRecyclerAdapter<ReplyModel, ?> adapter, IBaseRecyclerView view) {
        super(GVideoRuntime.getApplication());
        init(adapter, view);
    }

    @Override
    protected Observable<ReplyModel> createInsertObservable(ReplyModel commentModel) {
        Observable<ReplyModel> response;
        long toId = commentModel.replyId;
        String primaryId = String.valueOf(mPrimaryId);
        String content = commentModel.replyContent;
        response = repository.reply(primaryId, toId, commentModel);
        return response;
    }

    @Override
    protected Observable<ReplyModel> createRemoveObservable(ReplyModel commentModel) {
        Observable<Object> observable;
        observable = repository.deleteReply(commentModel.replyId);
        return observable.map(new Function<Object, ReplyModel>() {
            @Override
            public ReplyModel apply(Object success) throws Exception {
                return success != null ? commentModel : null;
            }
        });
    }

    public void setPrimaryId(long primaryId) {
        mPrimaryId = primaryId;
    }

    public void setData(List<ReplyModel> list) {
        //复用CommentAdapter数据，ReplyAdapter增删数据时保持两边数据一致；
        this.list = list;
    }

    public void addReply(String content, List<String> imageList, String soundUrl, String soundFilePath, String soundTxt,
                         int soundDuration, String replyId) {
        localFilePath = soundFilePath;
        ReplyModel model = ReplyModel.Builder.aReplyModel()
                .withReplyId(Long.parseLong(replyId))
                .withReplyContent(content)
                .withSoundUrl(soundUrl)
                .withSoundContent(soundTxt)
                .withLength(soundDuration)
                .withImageList(imageList)
                .build();

        insert(this.list.size(), model);
    }

    public void addReply(String content, ReplyModel replyToReply) {
        ReplyModel model = ReplyModel.Builder.aReplyModel()
                .withReplyId(Long.parseLong(replyToReply.user.getId()))
                .withReplyContent(content)
                .build();

        insert(this.list.size(), model);
    }

    public void deleteReply(@NonNull ReplyModel comment) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ReplyModel model = list.get(i);
            if (comment.equals(model)) {
                remove(i);
                break;
            }
        }
    }

    @Override
    public void loadSuccess(List<ReplyModel> list) {
        super.loadSuccess(list);
        if (loadType == LOAD_DATA_TYPE_INSERT) {
            showToast(R.string.video_comment_send_success);
            ObservableInt commentObservable = InteractDataObservable.getInstance().getCommentObservable(mMediaId);
            int count = commentObservable.get();
            commentObservable.set(++count);
        } else if (loadType == LOAD_DATA_TYPE_REMOVE) {
            showToast(R.string.video_comment_delete_success);
            ObservableInt commentObservable = InteractDataObservable.getInstance().getCommentObservable(mMediaId);
            int count = commentObservable.get();
            commentObservable.set(--count);
        }
    }

    @Override
    public void loadFailure(Throwable throwable) {

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

        if (loadType == LOAD_DATA_TYPE_REMOVE) {
            showToast(R.string.video_comment_delete_failure);
        } else {
            Toast.makeText(
                    GVideoRuntime.getAppContext(),
                    throwable.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void showToast(int stringResourceId, @Nullable Object... arguments) {
        Toast.makeText(GVideoRuntime.getAppContext(), stringResourceId, Toast.LENGTH_SHORT).show();
    }
}
