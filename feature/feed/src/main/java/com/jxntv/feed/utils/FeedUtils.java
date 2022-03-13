package com.jxntv.feed.utils;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.fragment.app.Fragment;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.MediaFragmentManager;
import com.jxntv.media.recycler.MediaPageFragment;

/**
 * feed 工具类
 */
public class FeedUtils {
    /** 推广位或者挂件点击处理逻辑，跳转到外链、长视频、短视频 */
    public static void clickAdvert(View view, RecommendModel recommendModel, String mFragmentId) {
        if (!TextUtils.isEmpty(recommendModel.actUrl)) {
            Bundle arguments = new Bundle();
            arguments.putString("title", recommendModel.title);
            arguments.putString("url", recommendModel.actUrl);
            PluginManager.get(WebViewPlugin.class).startWebViewFragment(view, arguments);
        } else if (!TextUtils.isEmpty(recommendModel.mediaId)){
            Fragment fragment = MediaFragmentManager.getInstance().getFragment(mFragmentId);
            Bundle extras = new Bundle();
            if (fragment instanceof MediaPageFragment) {
                String pid = ((MediaPageFragment) fragment).getPid();
                String channelId = ((MediaPageFragment) fragment).getChannelId();
                extras.putString(VideoPlugin.EXTRA_FROM_PID, pid);
                extras.putString(VideoPlugin.EXTRA_FROM_CHANNEL_ID, channelId);
            }
            VideoModel videoModel = VideoModel.Builder.aVideoModel()
                    .withId(recommendModel.mediaId)
                    .withMediaType(recommendModel.mediaType)
                    .build();
            PluginManager.get(DetailPagePlugin.class).dispatchToDetail(view.getContext(),videoModel,extras);
//            if (recommendModel.isShortVideo()) {
//                List<VideoModel> l = new ArrayList<>();
//                l.add(videoModel);
//                ShortVideoListModel model = ShortVideoListModel.Builder.aFeedModel().withList(l).build();
//                PluginManager.get(VideoPlugin.class).startShortVideoActivity(view.getContext(), model, extras);
//            } else if (recommendModel.isLongVideo()){
//                PluginManager.get(VideoPlugin.class).startLongVideoActivity(view.getContext(), videoModel, extras);
//            }else if (recommendModel.isIMLive()){
//                PluginManager.get(LivePlugin.class).startAudienceActivity(view.getContext(),videoModel,extras);
//            }else {
//                PluginManager.get(VideoPlugin.class).startLiveActivity(view.getContext(), videoModel, extras);
//            }
        }
    }
}
