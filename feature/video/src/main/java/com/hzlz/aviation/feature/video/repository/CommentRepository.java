package com.hzlz.aviation.feature.video.repository;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.hzlz.aviation.feature.video.api.VideoAPI;
import com.hzlz.aviation.feature.video.request.CommentPraiseRequest;
import com.hzlz.aviation.feature.video.request.CommentRequest;
import com.hzlz.aviation.feature.video.request.LoadCommentRequest;
import com.hzlz.aviation.feature.video.request.ReplyRequest;
import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.base.model.anotation.AuthorType;
import com.hzlz.aviation.kernel.base.model.comment.CommentModel;
import com.hzlz.aviation.kernel.base.model.comment.ReplyModel;
import com.hzlz.aviation.kernel.base.model.video.InteractDataObservable;
import com.hzlz.aviation.kernel.event.GVideoEventBus;
import com.hzlz.aviation.kernel.network.repository.BaseDataRepository;
import com.hzlz.aviation.kernel.network.repository.OneTimeNetworkData;
import com.hzlz.aviation.kernel.network.request.BaseGVideoRequest;
import com.hzlz.aviation.kernel.network.request.BaseRequest;
import com.hzlz.aviation.kernel.network.response.ListWithPage;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class CommentRepository extends BaseDataRepository {
    private static final int PAGE_SIZE = 10;

    public Observable<ListWithPage<CommentModel>> loadComment(String mediaId, int pageNum, boolean canComment,
                                                              long commentId, int commentType) {
        return new OneTimeNetworkData<ListWithPage<CommentModel>>(mEngine) {
            @Override
            protected BaseRequest<ListWithPage<CommentModel>> createRequest() {
                LoadCommentRequest request = new LoadCommentRequest();
                request.setMediaId(mediaId);
                request.setPageNumber(pageNum);
                request.setPageSize(PAGE_SIZE);
                request.setComment(commentId, commentType);
                return request;
            }

            @Override
            protected void saveData(ListWithPage<CommentModel> commentModelListWithPage) {
                if (commentModelListWithPage != null && !commentModelListWithPage.getList().isEmpty()) {
                    for (CommentModel commentModel : commentModelListWithPage.getList()) {
                        setCommentAuthorType(commentModel, canComment);
                    }
                }
            }
        }.asObservable();
    }

    public Observable<CommentModel> comment(String mediaId, long groupId, CommentModel commentModel, boolean isQaType) {
        return new OneTimeNetworkData<CommentModel>(mEngine) {
            @Override
            protected BaseRequest<CommentModel> createRequest() {
                CommentRequest request = new CommentRequest();
                request.setMediaId(mediaId);
                request.setContent(commentModel.content);
                request.setImageList(commentModel.imageList);
                request.setSoundId(commentModel.soundUrl);
                request.setSoundContent(commentModel.soundContent);
                request.setSoundDuration(commentModel.length);
                if (groupId > 0) {
                    request.setGroupId(groupId);
                }
                return request;
            }

            @Override
            protected void saveData(CommentModel commentModel) {
                setCommentAuthorType(commentModel, true);
                if (isQaType) {
                    //通知评论增加
                    GVideoEventBus.get(Constant.EVENT_MSG.COMMENT_ADD).post(null);
                }else {
                    GVideoEventBus.get(Constant.EVENT_MSG.COMMENT_ADD_QA).post(null);
                }
            }
        }.asObservable();
    }

    public Observable<Object> deleteComment(String commentId) {
        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                return new BaseGVideoRequest<Object>() {
                    @Override
                    protected Observable<Response<JsonElement>> getResponseObservable() {
                        return VideoAPI.Instance.get().deleteComment(commentId);
                    }
                };
            }
        }.asObservable();
    }

    public Observable<ReplyModel> reply(String primaryId, long toUId, ReplyModel commentModel) {
        return new OneTimeNetworkData<ReplyModel>(mEngine) {
            @Override
            protected BaseRequest<ReplyModel> createRequest() {
                ReplyRequest request = new ReplyRequest();
                request.setContent(commentModel.replyContent);
                request.setToId(toUId);
                request.setPrimaryId(primaryId);
                request.setImageList(commentModel.imageList);
                request.setSoundContent(commentModel.soundContent);
                request.setSoundDuration(commentModel.length);
                request.setSoundId(commentModel.soundUrl);
                return request;
            }

            @Override
            protected void saveData(ReplyModel replyModel) {
                setReplyAuthorType(replyModel, true);
                GVideoEventBus.get(Constant.EVENT_MSG.COMMENT_ADD).post(null);
            }

        }.asObservable();
    }

    public Observable<Object> deleteReply(long replyId) {
        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                return new BaseGVideoRequest<Object>() {
                    @Override
                    protected Observable<Response<JsonElement>> getResponseObservable() {
                        return VideoAPI.Instance.get().deleteReply(replyId);
                    }
                };
            }

        }.asObservable();
    }


    /**
     * 点赞
     *
     * @param commentId 评论id
     * @param isReply   是否回复
     */
    @NonNull
    public Observable<Boolean> commentPraise(long commentId, boolean isReply, boolean isPraise) {
        return new OneTimeNetworkData<Object>(mEngine) {
            @Override
            protected BaseRequest<Object> createRequest() {
                CommentPraiseRequest request = new CommentPraiseRequest();
                request.setCommentId(commentId);
                request.setIsPraise(isPraise);
                request.setType(isReply);
                return request;
            }
        }.asObservable().map(o -> {
            InteractDataObservable.getInstance().setPraiseCountObservable(commentId + "", isPraise ? -1 : -2);
            InteractDataObservable.getInstance().setPraiseCommentObservable(commentId + "", isPraise);
            return isPraise;
        });
    }

    private void setCommentAuthorType(CommentModel commentModel, boolean canComment) {
        commentModel.setCanComment(canComment);
        if (commentModel.commentUser != null) {
            commentModel.commentUser.setType(AuthorType.UGC);
        }
        if (commentModel.replies != null && !commentModel.replies.isEmpty()) {
            for (ReplyModel replyModel : commentModel.replies) {
                setReplyAuthorType(replyModel, canComment);
            }
        }
    }

    private void setReplyAuthorType(ReplyModel replyModel, boolean canComment) {
        replyModel.setCanComment(canComment);
        if (replyModel.user != null) {
            replyModel.user.setType(AuthorType.UGC);
        }
        if (replyModel.toUser != null) {
            replyModel.toUser.setType(AuthorType.UGC);
        }
    }
}
