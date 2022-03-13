package com.jxntv.android.video.ui.news;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;

import com.jxntv.android.video.Constants;
import com.jxntv.android.video.R;
import com.jxntv.android.video.repository.CommentRepository;
import com.jxntv.android.video.repository.MediaRepository;
import com.jxntv.android.video.repository.RecommendRepository;
import com.jxntv.android.video.ui.VideoActivity;
import com.jxntv.android.video.ui.news.adapter.NewsRecommendAdapter;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.CheckThreadLiveData;
import com.jxntv.base.Constant;
import com.jxntv.base.model.comment.CommentModel;
import com.jxntv.base.model.share.ShareDataModel;
import com.jxntv.base.model.stat.StatFromModel;
import com.jxntv.base.model.video.AuthorModel;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.placeholder.PlaceholderType;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.FavoritePlugin;
import com.jxntv.base.plugin.RecordPlugin;
import com.jxntv.base.plugin.SharePlugin;
import com.jxntv.base.utils.NetworkTipUtils;
import com.jxntv.event.GVideoEventBus;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.player.AudioPlayManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.sensordata.utils.InteractType;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.AppManager;
import com.jxntv.utils.ResourcesUtils;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/5/20
 * desc : 新闻详情
 **/
public class NewsDetailViewModel extends BaseViewModel {

    private MediaRepository repository = new MediaRepository();
    private RecommendRepository recommendRepository = new RecommendRepository();
    private CommentRepository commentRepository = new CommentRepository();

    public NewsDetailViewModel(@NonNull Application application) {
        super(application);
    }

    private CheckThreadLiveData<VideoModel> videoModel = new CheckThreadLiveData<>();

    public ObservableBoolean isRelatedNews = new ObservableBoolean();

    public CheckThreadLiveData<VideoModel> getVideoModel() {
        return videoModel;
    }

    @NonNull
    public NewsRecommendAdapter recommendAdapter = new NewsRecommendAdapter(AppManager.getAppManager().currentActivity());

    public void loadData(String mediaId) {
        updatePlaceholderLayoutType(PlaceholderType.LOADING);
        repository.loadMedia(mediaId).subscribe(new GVideoResponseObserver<VideoModel>() {
            @Override
            protected void onSuccess(@NonNull VideoModel videoModel) {
                getVideoModel().setValue(videoModel);
                updatePlaceholderLayoutType(PlaceholderType.NONE);
                loadRecommend(mediaId);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                updatePlaceholderLayoutType(PlaceholderType.ERROR);
            }
        });
    }

    @Override
    public void placeholderReload(@NonNull View view) {
        super.placeholderReload(view);
//        loadData();
    }

    /**
     * 推荐
     */
    private void loadRecommend(String mediaId) {
        recommendRepository.loadRecommend(mediaId).subscribe(new GVideoResponseObserver<List<RecommendModel>>() {
            @Override
            protected void onSuccess(@NonNull List<RecommendModel> recommendModels) {
                isRelatedNews.set(recommendModels != null && recommendModels.size() > 0);
                recommendAdapter.addMoreData(recommendModels);
            }
        });
    }
    /**
     * 评论
     */
    public void onclickShowCommentDialog(View view) {
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.comment)
            );
            return;
        }
        if (getVideoModel().getValue() == null) {
            return;
        }

        String replyPrefix = "";
        String commentId = "";
        if (getVideoModel().getValue().getId()!=null) {
            commentId = "news_"+getVideoModel().getValue().getCommentId();
        }
        AudioPlayManager.getInstance().release();
        PluginManager.get(RecordPlugin.class).showCommentInputDialog(view.getContext(), replyPrefix,
                commentId,
                RecordPlugin.DIALOG_TXT,
                new RecordPlugin.CommentListener() {
                    @Override
                    public void imageResult(String content, List<String> imageList) {
                        CommentModel model = CommentModel.Builder.aCommentModel()
                                .withContent(content)
                                .withImageList(imageList)
                                .build();
                        addComment(model, view);
                    }


                    @Override
                    public void soundResult(String content, String soundUrl, String soundFilePath,
                                            long soundLength, String soundTxt) {
                        CommentModel model = CommentModel.Builder.aCommentModel()
                                .withContent(content)
                                .withSoundUrl(soundUrl)
                                .withSoundContent(soundTxt)
                                .withLength((int) soundLength)
                                .build();
                        addComment(model, view);
                    }
                });

    }

    /**
     * 评论
     */
    public void onCommentClicked(View view) {
        if(getVideoModel().getValue() == null){
            return;
        }
        Intent intent = new Intent(view.getContext(), VideoActivity.class);
        Bundle arguments = new Bundle();
        arguments.putString(Constants.EXTRA_MEDIA_ID, getVideoModel().getValue().getId());
        arguments.putParcelable(Constants.EXTRA_VIDEO_MODEL, getVideoModel().getValue());
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.newsCommentFragment);
        intent.putExtras(arguments);
        view.getContext().startActivity(intent);
    }


    /**
     * 喜欢
     */
    public void onLikeClicked(View view) {
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.like)
            );
            return;
        }
        if (getVideoModel() == null) {
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(view.getContext())) {
            return;
        }
        ObservableBoolean favorObservable = getVideoModel().getValue().getObservable().getIsFavor();
        String mediaId = getVideoModel().getValue().getId();
        PluginManager.get(FavoritePlugin.class).getFavoriteRepository()
                .favoriteMedia(mediaId, !favorObservable.get()).subscribe(new GVideoResponseObserver<Boolean>() {
            @Override
            protected void onSuccess(@NonNull Boolean result) {
                GVideoSensorDataManager.getInstance().favoriteContent(videoModel.getValue(),getPid(),
                        !favorObservable.get(),null);
                getVideoModel().getValue().getObservable().setIsFavor(result);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                GVideoSensorDataManager.getInstance().favoriteContent(videoModel.getValue(),getPid(),
                        !favorObservable.get(),throwable.getMessage());
            }
        });

    }

    /**
     * 点击用户头像
     */
    public void onAvatarClicked(View v) {
        PluginManager.get(AccountPlugin.class).startPgcActivity(v, getVideoModel().getValue().getAuthor());
    }

    /**
     * 分享按钮点击
     */
    public void onShareClick(View view) {
        VideoModel videoModel = getVideoModel().getValue();
        if (videoModel == null){
            return;
        }
        PluginManager.get(SharePlugin.class).showShareDialog(view.getContext(), false, getShareModel(), getStatFromModel());
    }

    /**
     * 关注
     */
    public void onFollowClicked(View v) {
        if (!PluginManager.get(AccountPlugin.class).hasLoggedIn()) {
            GVideoEventBus.get(Constant.EVENT_BUS_EVENT.START_LOGIN, String.class).post("");
            GVideoSensorDataManager.getInstance().enterRegister(
                    StatPid.getPageName(getPid()),
                    ResourcesUtils.getString(R.string.follow)
            );
            return;
        }
        if (!NetworkTipUtils.checkNetworkOrTip(v.getContext())) {
            return;
        }
        AuthorModel authorModel = getVideoModel().getValue().getAuthor();
        PluginManager.get(AccountPlugin.class).getFollowRepository()
                .followAuthor(authorModel.getId(),
                        authorModel.getType(),
                        authorModel.getName(),
                        !authorModel.isFollow()).subscribe(new GVideoResponseObserver<Boolean>() {
            @Override
            protected void onSuccess(@NonNull Boolean result) {
                getVideoModel().getValue().getAuthor().setFollow(result);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                GVideoSensorDataManager.getInstance().followAccount(!authorModel.isFollow(), authorModel.getId(),
                        authorModel.getName(),authorModel.getType(),throwable.getMessage());
            }
        });
    }


    private void addComment(CommentModel commentModel, View view) {

        commentRepository.comment(getVideoModel().getValue().getId(),0, commentModel,false)
                .subscribe(new GVideoResponseObserver<CommentModel>() {
                    @Override
                    protected void onSuccess(@NonNull CommentModel commentModel) {
                        onCommentClicked(view);
                        GVideoSensorDataManager.getInstance().clickContent(videoModel.getValue(), getPid(),
                                InteractType.COMMENT,commentModel.content,null);
                        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.DISMISS_COMMENT_INPUT_PANEL).post(null);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        showToast(throwable.getMessage());
                        GVideoSensorDataManager.getInstance().clickContent(videoModel.getValue(), getPid(),
                                        InteractType.COMMENT, commentModel.content, throwable.getMessage());
                        GVideoEventBus.get(Constant.EVENT_BUS_EVENT.HIDE_LOADING_INPUT_PANEL).post(null);
                    }
                });
    }

    public ShareDataModel getShareModel() {
        VideoModel videoModel = getVideoModel().getValue();
        if (videoModel == null) {
            return null;
        }
        ObservableBoolean favorObservable = videoModel.getObservable().getIsFavor();
        ObservableBoolean followObservable;
        if (videoModel.getAuthor() != null) {
            followObservable = videoModel.getAuthor().getObservable().getIsFollow();
        } else {
            followObservable = new ObservableBoolean();
        }
        ShareDataModel shareDataModel = new ShareDataModel.Builder()
                .setVideoModel(videoModel)
                .setShowShare(videoModel.mediaStatus != null && videoModel.mediaStatus == 3)
                .setFavorite(favorObservable.get())
                .setFollow(followObservable.get())
                .setShowDelete(false)
                .setShowCreateBill(true)
                .setShowFollow(getVideoModel().getValue().getAuthor() != null).build();
        return shareDataModel;
    }

    public StatFromModel getStatFromModel() {
        if (getVideoModel().getValue() != null) {
            return new StatFromModel(getVideoModel().getValue().getId(), getPid(), null, null, null);
        }
        return null;
    }

}
