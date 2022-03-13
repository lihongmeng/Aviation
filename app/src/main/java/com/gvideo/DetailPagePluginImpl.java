package com.gvideo;

import static com.jxntv.base.Constant.BUNDLE_KEY.PLAY_TYPE;
import static com.jxntv.base.Constant.EXTRA_VIDEO_MODEL;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.jxntv.base.Constant;
import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.model.circle.Circle;
import com.jxntv.base.model.circle.TopicDetail;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.AccountPlugin;
import com.jxntv.base.plugin.CirclePlugin;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.LivePlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.base.plugin.WatchTvPlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.stat.StatPid;
import com.jxntv.utils.ResourcesUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author huangwei
 * date : 2021/3/25
 * desc : 详情页面分发
 **/
public class DetailPagePluginImpl implements DetailPagePlugin {

    @Override
    public void dispatchToDetail(@NonNull Context context, VideoModel videoModel, Bundle extras) {
        if (videoModel == null) {
            return;
        }

        if (extras == null) {
            extras = new Bundle();
        }
        extras.putParcelable(EXTRA_VIDEO_MODEL, videoModel);

        if (videoModel.getAnswerSquareId() > 0) {
            PluginManager.get(VideoPlugin.class).startQADetailActivity(context, videoModel, extras);
            return;
        }

        String linkTitle = videoModel.getLinkTitle();
        linkTitle = TextUtils.isEmpty(linkTitle) ? "" : linkTitle;

        String linkUrl = videoModel.getLinkUrl();
        linkUrl = TextUtils.isEmpty(linkUrl) ? "" : linkUrl;

        if (!TextUtils.isEmpty(linkTitle) && !TextUtils.isEmpty(linkUrl)) {
            PluginManager.get(WebViewPlugin.class).startWebViewActivity(context, linkUrl, linkTitle);
            return;
        }

        switch (videoModel.getMediaType()) {
            case MediaType.SHORT_VIDEO:
            case MediaType.SHORT_AUDIO:
            case MediaType.LONG_VIDEO:
            case MediaType.LONG_AUDIO:
                if (!isTVCollection(context, videoModel)) {
                    List<VideoModel> list = new ArrayList<>();
                    list.add(videoModel);
                    ShortVideoListModel listModel = ShortVideoListModel.Builder.aFeedModel().withList(list).build();
                    PluginManager.get(VideoPlugin.class).startShortVideoActivity(context, listModel, extras);
                }
                break;
            case MediaType.HORIZONTAL_LIVE:
            case MediaType.VERTICAL_LIVE:
                if (!TextUtils.isEmpty(linkUrl)) {
                    PluginManager.get(WebViewPlugin.class).startWebViewActivity(context, linkUrl, linkTitle);
                } else {
                    PluginManager.get(VideoPlugin.class).startLiveActivity(context, videoModel, extras);
                }
                break;
            case MediaType.IM_VERTICAL_LIVE:
            case MediaType.IM_HORIZONTAL_LIVE:
                if (!TextUtils.isEmpty(linkUrl)) {
                    PluginManager.get(WebViewPlugin.class).startWebViewActivity(context, linkUrl, linkTitle);
                } else {
                    PluginManager.get(LivePlugin.class).startAudienceActivity(context, videoModel, extras);
                }
                break;
            case MediaType.IMAGE_TXT:
            case MediaType.AUDIO_TXT:
                PluginManager.get(VideoPlugin.class).startImageTxtAudioActivity(context, videoModel, extras);
                break;
            case MediaType.NEWS_IMAGE:
            case MediaType.NEWS_RIGHT_IMAGE:
            case MediaType.NEWS_SCROLL:
                String specialId = videoModel.getSpecialId();
                if (!TextUtils.isEmpty(specialId) && specialId.equals(videoModel.getId())) {
                    //specialId = id 时进入某一专题列表页
                    PluginManager.get(VideoPlugin.class).startSpecialList(context, videoModel, extras);
                } else {
                    PluginManager.get(VideoPlugin.class).startNewsDetail(context, videoModel, extras);
                }
                break;
            case MediaType.NEWS_LINK:
                PluginManager.get(WebViewPlugin.class).startWebViewActivity(
                        context,
                        videoModel.getMediaUrls().get(0),
                        videoModel.getTitle());
                break;
            case MediaType.FXA_DETAIL:
                AccountPlugin accountPlugin = PluginManager.get(AccountPlugin.class);
                if (!accountPlugin.hasLoggedIn()) {
                    GVideoSensorDataManager.getInstance().enterRegister(
                            StatPid.getPageName(videoModel.getPid()),
                            ResourcesUtils.getString(R.string.click_dsfxa)
                    );
                    accountPlugin.startLoginActivity(context);
                    return;
                }
                PluginManager.get(CirclePlugin.class).startFXAActivity(context, videoModel.getId());
                break;
            case MediaType.TOPIC_DETAIL:
                TopicDetail topicDetail = new TopicDetail();
                topicDetail.id = Long.parseLong(videoModel.getId());
                PluginManager.get(CirclePlugin.class).startTopicDetailWithActivity(context, null, topicDetail);
                break;
            case MediaType.CIRCLE_DETAIL:
                Circle circle = new Circle();
                circle.setGroupId(Long.parseLong(videoModel.getId()));
                circle.setName(videoModel.getTitle());
                PluginManager.get(CirclePlugin.class).startCircleDetailWithActivity(context, circle, extras);
                break;
            case MediaType.WATCH_TV_CHANNEL_BUILD_SELF:
                try {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PLAY_TYPE, Constant.LIVE_TO_WATCH_TV_PLAY_TYPE.BUILD_SELF);
                    PluginManager.get(WatchTvPlugin.class).startWatchTvChannelDetailWithActivity(context, videoModel, bundle);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                break;
            case MediaType.WATCH_TV:
                PluginManager.get(WatchTvPlugin.class).startWatchTvChannelDetailWithActivity(context, videoModel, null);
                break;
            case MediaType.COLLECTION_DETAIL:
                try {
                    long id = Long.parseLong(videoModel.getId());
                    if (!TextUtils.isEmpty(videoModel.getColumnId())) {
                        long programId = Long.parseLong(videoModel.getColumnId());
                        PluginManager.get(WatchTvPlugin.class).startWatchTvWholePeriodDetailWithActivity(context, id, programId, videoModel.getPid());
                    } else {
                        PluginManager.get(WatchTvPlugin.class).startWatchTvWholePeriodDetailWithActivity(context, id, videoModel.getPid());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MediaType.QA_DETAIL:
                PluginManager.get(VideoPlugin.class).startQADetailActivity(context, videoModel, extras);
                break;
        }
    }

    /**
     * 是否是电视节目整期
     */
    private boolean isTVCollection(Context context, VideoModel videoModel) {
        boolean result = false;
        try {
            if (!TextUtils.isEmpty(videoModel.getColumnId()) && Long.parseLong(videoModel.getColumnId()) > 0) {
                PluginManager.get(WatchTvPlugin.class).startWatchTvWholePeriodDetailWithActivity(context,
                                Long.parseLong(videoModel.getColumnId()), Long.parseLong(videoModel.getId()),videoModel.getPid());
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
