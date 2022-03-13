package com.jxntv.android.video;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.navigation.Navigation;

import com.jxntv.base.Constant;
import com.jxntv.base.model.video.RecommendModel;
import com.jxntv.base.model.video.ShortVideoListModel;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.WebViewPlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.stat.StatPid;

import java.util.ArrayList;
import java.util.List;

public class VideoHelper {

    /**
     * 推广位或者挂件点击处理逻辑，跳转到外链、长视频、短视频
     */
    public static void handleAdvertClick(
            View view,
            RecommendModel recommendModel,
            boolean fromDetail
    ) {
        if (!TextUtils.isEmpty(recommendModel.actUrl)) {
            Bundle arguments = new Bundle();
            arguments.putString("title", recommendModel.title);
            arguments.putString("url", recommendModel.actUrl);
            PluginManager.get(WebViewPlugin.class).startWebViewFragment(view, arguments);
            return;
        }

        if (!TextUtils.isEmpty(recommendModel.mediaId)) {
            if (recommendModel.isShortVideo()) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.EXTRA_FROM_PID, StatPid.DETAIL);
                VideoModel videoModel = VideoModel.Builder
                        .aVideoModel()
                        .withId(recommendModel.mediaId)
                        .build();
                List<VideoModel> modelArrayList = new ArrayList<>();
                modelArrayList.add(videoModel);
                ShortVideoListModel model = ShortVideoListModel.Builder
                        .aFeedModel()
                        .withList(modelArrayList)
                        .build();
                bundle.putParcelable(Constant.EXTRA_VIDEO_SHORT_MODELS, model);
                Navigation.findNavController(view).navigate(R.id.videoShortFragment, bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.EXTRA_FROM_PID, StatPid.DETAIL);
                bundle.putParcelable(
                        Constant.EXTRA_VIDEO_MODEL,
                        VideoModel.Builder
                                .aVideoModel()
                                .withId(recommendModel.mediaId)
                                .build()
                );
                Navigation.findNavController(view).navigate(
                        fromDetail ? R.id.action_super_popup_self : R.id.action_super_new_self,
                        bundle
                );
            }
        }
    }

    /**
     * 点击剧集，根据视频类型进行跳转
     */
    public static void handleSeriesClick(
            View view,
            VideoModel videoModel,
            boolean fromDetail
    ) {
        if (videoModel.isShortMedia()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.EXTRA_FROM_PID, StatPid.DETAIL);
            List<VideoModel> modelList = new ArrayList<>();
            modelList.add(videoModel);
            ShortVideoListModel model = ShortVideoListModel.Builder
                    .aFeedModel()
                    .withList(modelList)
                    .build();
            bundle.putParcelable(Constant.EXTRA_VIDEO_SHORT_MODELS, model);
            Navigation.findNavController(view).navigate(R.id.videoShortFragment, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.EXTRA_FROM_PID, StatPid.DETAIL);
            bundle.putParcelable(Constant.EXTRA_VIDEO_MODEL, videoModel);
            Navigation.findNavController(view).navigate(
                    fromDetail ?  R.id.action_super_popup_self : R.id.action_super_new_self,
                    bundle
            );
        }
    }
}
