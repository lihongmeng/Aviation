package com.jxntv.android.video.ui.detail;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jxntv.base.Constant;
import com.jxntv.base.model.comment.CommentModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.view.recyclerview.BaseRecyclerViewModel;
import com.jxntv.base.view.recyclerview.interf.IRecyclerModel;
import com.jxntv.base.view.recyclerview.interf.RecyclerViewLoadListener;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.sensordata.utils.InteractType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;

public class DetailViewModel<CommentModel> extends BaseRecyclerViewModel<CommentModel> implements IRecyclerModel<CommentModel> {
    public static final String ERR_MSG_NOMORE = "NoMore";
    protected String mMediaId;
    //置顶评论
    protected long commentId, mGroupId = 0;
    protected int commentType;
    protected VideoModel videoModel;
    protected List<CommentModel> list = new ArrayList<>();
    protected boolean isShowPlaceHolder = true;

    protected boolean closeErrorToast = false;

    //本地录音文件地址，评论后服务器返回可能没有soundUrl时使用
    protected String localFilePath;

    protected boolean isComment = false;

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void setVideoModel(VideoModel videoModel) {
        this.videoModel = videoModel;
    }

    public void setMediaId(String mediaId) {
        this.mMediaId = mediaId;
    }


    /**
     * 问答主评论设置社区信息
     *
     * @param groupId 社区id
     */
    public void setGroupId(long groupId) {
        this.mGroupId = groupId;
    }

    /**
     * 置顶评论
     *
     * @param commentId   评论id
     * @param commentType 0 主评论  1 回复
     */
    public void setComment(long commentId, int commentType, boolean isShowPlaceHolder) {
        this.commentId = commentId;
        this.commentType = commentType;
        this.isShowPlaceHolder = isShowPlaceHolder;
    }


    @Override
    protected IRecyclerModel<CommentModel> createModel() {
        return this;
    }

    protected Observable<CommentModel> createInsertObservable(CommentModel model) {
        return new Observable<CommentModel>() {
            @Override
            protected void subscribeActual(Observer<? super CommentModel> observer) {

            }
        };
    }

    protected Observable<CommentModel> createRemoveObservable(CommentModel model) {
        return new Observable<CommentModel>() {
            @Override
            protected void subscribeActual(Observer<? super CommentModel> observer) {

            }
        };
    }

    protected Observable<CommentModel> createUpdateObservable(CommentModel model) {
        return new Observable<CommentModel>() {
            @Override
            protected void subscribeActual(Observer<? super CommentModel> observer) {

            }
        };
    }

    protected Observable<DetailModelList<CommentModel>> createLoadObservable(String mediaId, int page) {
        return new Observable<DetailModelList<CommentModel>>() {
            @Override
            protected void subscribeActual(Observer<? super DetailModelList<CommentModel>> observer) {

            }
        };
    }

    @Override
    public void loadData(int page, RecyclerViewLoadListener<CommentModel> loadListener) {
        Observable<DetailModelList<CommentModel>> observable = createLoadObservable(mMediaId, page);
        observable.subscribe(new GVideoResponseObserver<DetailModelList<CommentModel>>() {
            @Override
            protected boolean isShowPlaceholderLayout() {
                return isShowPlaceHolder;
            }

            @Override
            protected void onSuccess(@NonNull DetailModelList<CommentModel> commentResponse) {
                list.clear();
                list.addAll(commentResponse.list);
                loadSuccess(list);

                loadComplete();

                if (!isShowPlaceHolder) {
                    if (list == null || list.size() == 0) {
                        mIsEmptyLiveData.setValue(true);
                    } else if (!commentResponse.hasMore) {
                        mNoMoreLiveData.setValue(true);
                    }
                } else {
                    if (!commentResponse.hasMore) {
                        mNoMoreLiveData.setValue(true);
                    }
                    if (list == null || list.size() == 0) {
                        if (isComment) {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY_COMMENT);
                        } else {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        }
                    } else {
                        updatePlaceholderLayoutType(PlaceholderType.NONE);
                    }
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                updatePlaceholderLayoutType(PlaceholderType.ERROR);
                loadComplete();

                if (list == null || list.size() == 0) {
                    if (!isShowPlaceHolder) {
                        mIsEmptyLiveData.setValue(true);
                    } else {
                        if (isComment) {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY_COMMENT);
                        } else {
                            updatePlaceholderLayoutType(PlaceholderType.EMPTY);
                        }
                    }
                } else {
                    updatePlaceholderLayoutType(PlaceholderType.NONE);
                }
            }
        });
    }

    @Override
    public void remove(int position, RecyclerViewLoadListener<CommentModel> loadListener) {
        CommentModel comment = list.get(position);
        Observable<CommentModel> observable = createRemoveObservable(comment);
        observable.subscribe(new GVideoResponseObserver<CommentModel>() {
            @Override
            protected boolean isShowPlaceholderLayout() {
                return true;
            }

            @Override
            protected void onSuccess(@NonNull CommentModel response) {
                GVideoEventBus.get(Constant.EVENT_MSG.COMMENT_DELETE).post(null);
                list.remove(position);
                loadSuccess(list);
                loadComplete();
                setStat(response, InteractType.DEL_COMMENT, null);
            }

            @Override
            public void onFailed(Throwable throwable) {
                loadFailure(throwable);
                setStat(comment, InteractType.DEL_COMMENT, throwable.getMessage());
            }
        });
    }

    @Override
    public void insert(int position, CommentModel commentModel,
                       RecyclerViewLoadListener<CommentModel> loadListener) {
        Observable<CommentModel> observable = createInsertObservable(commentModel);
        observable.subscribe(new GVideoResponseObserver<CommentModel>() {
            @Override
            protected boolean isShowPlaceholderLayout() {
                return false;
            }

            @Override
            protected void onSuccess(@NonNull CommentModel response) {

                int interactType = InteractType.COMMENT;
                if (response instanceof com.jxntv.base.model.comment.CommentModel) {
                    com.jxntv.base.model.comment.CommentModel model = (com.jxntv.base.model.comment.CommentModel) response;
                    if (!TextUtils.isEmpty(localFilePath) && TextUtils.isEmpty(model.soundUrl)) {
                        model.soundUrl = localFilePath;
                        response = (CommentModel) model;
                        localFilePath = "";
                    }
                    if (model.commentUser!=null) {
                        interactType = getCommentType(model.commentUser.getId());
                    }
                }

                if (response instanceof com.jxntv.base.model.comment.ReplyModel) {
                    com.jxntv.base.model.comment.ReplyModel model = (com.jxntv.base.model.comment.ReplyModel) response;
                    if (!TextUtils.isEmpty(localFilePath) && TextUtils.isEmpty(model.soundUrl)) {
                        model.soundUrl = localFilePath;
                        response = (CommentModel) model;
                        localFilePath = "";
                    }
                    if (model.user != null) {
                        interactType = getCommentType(model.user.getId());
                    }
                }
                setStat(response, interactType, null);

                list.add(position, response);
                loadSuccess(list);
                loadComplete();
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.DISMISS_COMMENT_INPUT_PANEL).post(null);
                mIsEmptyLiveData.setValue(false);
            }

            @Override
            public void onFailed(Throwable throwable) {
                loadFailure(throwable);
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.HIDE_LOADING_INPUT_PANEL).post(null);
                setStat(commentModel, InteractType.COMMENT, throwable.getMessage());
            }
        });
    }

    @Override
    public void update(int position, CommentModel commentModel,
                       RecyclerViewLoadListener<CommentModel> loadListener) {
        Observable<CommentModel> observable = createUpdateObservable(commentModel);
        observable.subscribe(new GVideoResponseObserver<CommentModel>() {
            @Override
            protected boolean isShowPlaceholderLayout() {
                return list == null || list.size() == 0;
            }

            @Override
            protected void onSuccess(@NonNull CommentModel response) {
                setStat(response, InteractType.COMMENT, null);
                list.remove(position);
                list.add(position, commentModel);
                loadSuccess(list);
                loadComplete();
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.DISMISS_COMMENT_INPUT_PANEL).post(null);
            }

            @Override
            public void onFailed(Throwable throwable) {
                setStat(commentModel, InteractType.COMMENT, throwable.getMessage());
                loadFailure(throwable);
                GVideoEventBus.get(Constant.EVENT_BUS_EVENT.HIDE_LOADING_INPUT_PANEL).post(null);
            }
        });
    }

    private void setStat(CommentModel commentModel, @InteractType int interactType, String error) {
        String commentContent = null;
        if (commentModel instanceof com.jxntv.base.model.comment.CommentModel) {
            com.jxntv.base.model.comment.CommentModel model = (com.jxntv.base.model.comment.CommentModel) commentModel;
            if (model.replies != null && model.replies.size() > 0) {
                int p = model.replies.size() - 1;
                commentContent = model.replies.get(p).replyContent;
            } else {
                commentContent = model.content;
            }
        }

        if (commentModel instanceof com.jxntv.base.model.comment.ReplyModel) {
            com.jxntv.base.model.comment.ReplyModel model = (com.jxntv.base.model.comment.ReplyModel) commentModel;
            commentContent = model.replyContent;
        }

        GVideoSensorDataManager.getInstance().clickContent(
                videoModel,
                getPid(),
                interactType,
                commentContent,
                error
        );

    }

    @Override
    public void initialData(RecyclerViewLoadListener<CommentModel> loadListener) {
    }

    public MutableLiveData<Boolean> mNoMoreLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> mIsEmptyLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getNoMoreLiveData() {
        return mNoMoreLiveData;
    }


    private @InteractType int getCommentType(String commentUserId){

        if (videoModel != null && videoModel.isNotNormalType()){
            if (TextUtils.equals(videoModel.getMentorJid(),commentUserId)){
                return InteractType.QA_ANSWER_COMMENT;
            }else {
                return InteractType.QA_NORMAL_COMMENT;
            }
        }
        return InteractType.COMMENT;
    }

}
