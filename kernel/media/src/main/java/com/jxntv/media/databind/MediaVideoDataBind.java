package com.jxntv.media.databind;

import static com.jxntv.base.model.anotation.MediaType.AUDIO_TXT;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.FeedPlugin;
import com.jxntv.base.plugin.LoadMorePlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.base.tag.TagHelper;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.MediaFragmentManager;
import com.jxntv.media.MediaPageSource;
import com.jxntv.media.R;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.player.MediaPlayManager;
import com.jxntv.media.recycler.MediaPageFragment;
import com.jxntv.media.template.MediaBaseTemplate;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.utils.ResourcesUtils;

/**
 * Media 音频 data bind
 */
public class MediaVideoDataBind {

    private MediaBaseTemplate mTemplate;
    private String mFragmentId;
    private MediaModel mediaModel;
    private int mPosition= -1;

    /*public MediaVideoDataBind(MediaBaseTemplate template, MediaModel feedModel, String fragmentId) {
        mTemplate = template;
        mFragmentId = fragmentId;
        mediaModel = feedModel;
    }*/

    public MediaVideoDataBind(MediaBaseTemplate template, MediaModel feedModel, String fragmentId ,int position) {
        mTemplate = template;
        mFragmentId = fragmentId;
        mediaModel = feedModel;
        mPosition = position;
    }

    public void release() {
        mTemplate = null;
    }

    /**
     * 处理媒体数据点击
     *
     * @param view       View
     * @param mediaModel 媒体数据
     */
    public void dealMediaClick(View view, MediaModel mediaModel) {
        if (mediaModel.isAudio() || mediaModel.getMediaType() == AUDIO_TXT) {
            MediaPlayManager.getInstance().play(mediaModel.tabId, mTemplate);
        } else {
            handleNavigateToDetail(view.getContext(), this.mediaModel);
        }
        // 滑动到顶部位置播放
        // Fragment fragment = MediaFragmentManager.getInstance().getFragment(mFragmentId);
        // if (mPosition >= 0 &&fragment instanceof MediaPageFragment){
        //     //目前首页推荐因布局问题首个视频可能无法滑动，这里特殊处理
        //     if (TextUtils.equals(StatPid.HOME_RECOMMEND,((MediaPageFragment) fragment).getPid())
        //             && TextUtils.equals(StatPid.HOME_FOLLOW,((MediaPageFragment) fragment).getPid())
        //             && mPosition == 0
        //             || !((MediaPageFragment) fragment).moveToPositionPlay(mPosition)) {
        //         MediaPlayManager.getInstance().play(mediaModel.tabId, mTemplate);
        //     }
        // }else {
        //     MediaPlayManager.getInstance().play(mediaModel.tabId, mTemplate);
        // }
    }

    public Drawable getSoundResource(MediaModel model) {
        if (model.getTagType() == TagHelper.FEED_TAG_LIVE) {
            return ResourcesUtils.getDrawable(R.drawable.liteav_fm_cover);
        }
        return ResourcesUtils.getDrawable(R.drawable.liteav_music_cover);
    }

    private void handleNavigateToDetail(Context context, MediaModel model) {
        if (model == null) {
            return;
        }
        if (mediaModel.showMediaPageSource == MediaPageSource.PageSource.SEARCH) {
            GVideoSensorDataManager.getInstance().clickSearchResult(
                    mediaModel.getId(),
                    mediaModel.getTitle(),
                    "动态",
                    mediaModel.searchWord,
                    mPosition,
                    mediaModel.hintSearchWord
            );
        }
        Fragment fragment = MediaFragmentManager.getInstance().getFragment(mFragmentId);
        Bundle extras = new Bundle();
        if (fragment instanceof MediaPageFragment) {
            String pid = ((MediaPageFragment) fragment).getPid();
            String channelId = ((MediaPageFragment) fragment).getChannelId();
            extras.putString(VideoPlugin.EXTRA_FROM_PID, pid);
            extras.putString(VideoPlugin.EXTRA_FROM_CHANNEL_ID, channelId);
        }
        extras.putString(LoadMorePlugin.KEY_MODULE, LoadMorePlugin.MODULE_FEED);
        extras.putString(FeedPlugin.KEY_FRAGMENT_UUID, mFragmentId);

        MediaPlayManager.getInstance().onChangeToDetail(model);
        PluginManager.get(DetailPagePlugin.class).dispatchToDetail(context, model, extras);
    }

    public void onItemClick(View view) {
        handleNavigateToDetail(view.getContext(), mediaModel);
    }

}
