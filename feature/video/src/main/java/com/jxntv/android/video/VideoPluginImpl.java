package com.jxntv.android.video;

import static com.jxntv.base.Constant.BUNDLE_KEY.COMMENT_ID;
import static com.jxntv.base.Constant.BUNDLE_KEY.COMMENT_TYPE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.jxntv.android.video.api.VideoAPI;
import com.jxntv.android.video.repository.CommentRepository;
import com.jxntv.android.video.repository.MediaRepository;
import com.jxntv.android.video.ui.VideoActivity;
import com.jxntv.base.BaseViewModel;
import com.jxntv.base.Constant;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.MediaRepositoryInterface;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.network.repository.OneTimeNetworkData;
import com.jxntv.network.request.BaseGVideoRequest;
import com.jxntv.network.request.BaseRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;

public class VideoPluginImpl implements VideoPlugin {

    @Override
    public void startLongVideoActivity(@NonNull Context context, VideoModel videoModel, Bundle extras) {
        Intent intent = getDefaultIntent(context, videoModel, extras);
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.videoSuperFragment);
        context.startActivity(intent);
    }

    @Override
    public void startShortVideoActivity(@NonNull Context context, ShortVideoListModel shortVideoListModel, Bundle extras) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.videoShortFragment);
        intent.putExtra(Constant.EXTRA_VIDEO_SHORT_MODELS, shortVideoListModel);
        if (extras!=null) {
            intent.putExtra(Constants.EXTRA_COMMENT, extras.getInt(VideoPlugin.EXTRA_START) == VideoPlugin.START_COMMENT);
            intent.putExtra(COMMENT_ID, (extras.getInt(VideoPlugin.EXTRA_START) == VideoPlugin.START_COMMENT));
            intent.putExtra(Constants.EXTRA_VIDEO_EXTRAS, extras);
            intent.putExtra(Constant.EXTRA_FROM_PID, extras.getString(VideoPlugin.EXTRA_FROM_PID));
            intent.putExtra(Constant.EXTRA_FROM_CHANNEL_ID, extras.getString(VideoPlugin.EXTRA_FROM_CHANNEL_ID));
            intent.putExtra(COMMENT_ID, extras.getLong(VideoPlugin.EXTRA_START_COMMENT_ID));
            intent.putExtra(COMMENT_TYPE, extras.getInt(VideoPlugin.EXTRA_START_COMMENT_TYPE));
        }
        context.startActivity(intent);
    }

    @Override
    public void startLiveActivity(@NonNull Context context, VideoModel videoModel, Bundle extras) {
        Intent intent = getDefaultIntent(context, videoModel, extras);
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.videoLiveFragment);
        context.startActivity(intent);
    }

    @Override
    public void startImageTxtAudioActivity(@NonNull Context context, VideoModel videoModel, Bundle extras) {
        Intent intent = getDefaultIntent(context, videoModel, extras);
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.imageTxtAudioFragment);
        context.startActivity(intent);
    }

    @Override
    public void startNewsDetail(@NonNull Context context, VideoModel videoModel, Bundle extras) {
        Intent intent = getDefaultIntent(context, videoModel, extras);
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.newsDetailFragment);
        context.startActivity(intent);
    }

    @Override
    public void startNewsList(@NonNull Context context, VideoModel videoModel, Bundle extras) {
        Intent intent = getDefaultIntent(context, videoModel, extras);
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.newsListFragment);
        context.startActivity(intent);

    }

    @Override
    public void startSpecialList(@NonNull Context context, VideoModel videoModel, Bundle extras) {
        Intent intent = getDefaultIntent(context, videoModel, extras);
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.specialListFragment);
        intent.putExtra(Constants.EXTRA_MEDIA_ID, TextUtils.isEmpty(videoModel.getSpecialId()) ? videoModel.getId() : videoModel.getSpecialId());
        context.startActivity(intent);
    }

    @Override
    public void startQADetailActivity(@NonNull Context context, VideoModel videoModel, Bundle extras) {
        Intent intent = getDefaultIntent(context, videoModel, extras);
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.qaDetailFragment);
        context.startActivity(intent);
    }

    @Override
    public void startJXNewsActivity(@NonNull Context context, VideoModel videoModel, Bundle extras) {
        Intent intent = getDefaultIntent(context, videoModel, extras);
        intent.putExtra(Constants.INTENT_VIDEO_TYPE, R.id.jxxwNewsDetailFragment);
        context.startActivity(intent);
    }

    @Override
    public void commentPraise(String commentId, boolean isPrise, BaseViewModel.BaseGVideoResponseObserver<Boolean> observer) {
        if (!TextUtils.isEmpty(commentId)) {
            new CommentRepository().commentPraise(Long.parseLong(commentId), false, isPrise)
                    .subscribe(observer);
        }
    }

    /**
     * 在跳转之前，先对Intent做统一处理
     * 需要注意的是
     * 不仅仅是信息流的数据可以启动视频相关页面
     * 例如首页推荐的内容部分，点击也可以启动
     * 这个时候VideoModel就有可能为空，注意空指针判断
     *
     * @param context
     * @param videoModel
     * @param extras
     * @return
     */
    private Intent getDefaultIntent(Context context, VideoModel videoModel, Bundle extras) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(Constant.EXTRA_VIDEO_MODEL, videoModel);
        if (extras!=null) {
            intent.putExtra(Constants.EXTRA_COMMENT, extras.getInt(VideoPlugin.EXTRA_START) == VideoPlugin.START_COMMENT);
            intent.putExtra(COMMENT_ID, extras.getLong(VideoPlugin.EXTRA_START_COMMENT_ID));
            intent.putExtra(COMMENT_TYPE, extras.getInt(VideoPlugin.EXTRA_START_COMMENT_TYPE));
            intent.putExtra(Constants.EXTRA_VIDEO_EXTRAS, extras);
            intent.putExtra(Constant.EXTRA_FROM_PID, extras.getString(VideoPlugin.EXTRA_FROM_PID));
            intent.putExtra(Constant.EXTRA_FROM_CHANNEL_ID, extras.getString(VideoPlugin.EXTRA_FROM_CHANNEL_ID));
        }
        intent.putExtra(Constants.EXTRA_MEDIA_ID, videoModel.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public Observable<Object> deleteComment(String commentId) {
        return new CommentRepository().deleteComment(commentId);
    }


}
