package com.hzlz.aviation.kernel.media.template.view.news;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;

import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.databinding.NewsTplRightImageBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.media.template.MediaBaseTemplate;
import com.hzlz.aviation.kernel.stat.sensordata.GVideoSensorDataManager;
import com.hzlz.aviation.library.ioc.PluginManager;

import java.util.ArrayList;

/**
 * @author huangwei
 * date : 2021/5/21
 * desc : 新闻左文右图
 **/
public class NewsRightImageTemplate extends MediaBaseTemplate {

    private NewsTplRightImageBinding mBinding;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     */
    public NewsRightImageTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.news_tpl_right_image, parent, false);
    }

    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId, int position) {
        if (mediaModel.getImageUrls() == null) {
            //防止后台返回错误数据时造成空指针
            mediaModel.setImageUrls(new ArrayList<>());
        }
        super.update(mediaModel, isDarkMode, fragmentId, position);
        Context context = mBinding.getRoot().getContext();
        mBinding.setVariable(BR.feedModel, mMediaModel);
        mBinding.bottom.setVariable(BR.feedModel, mMediaModel);
        setNewsSourceMaxWidth(mBinding.bottom.source);
        mBinding.rootLayout.setOnClickListener(view -> {
            if (mMediaModel.showInSearch()){
               if (mMediaModel.getMediaUrls() != null && mMediaModel.getMediaUrls().size() > 0){
                   Fragment fragment = MediaFragmentManager.getInstance().getFragment(fragmentId);
                   Bundle extras = new Bundle();
                   if (fragment instanceof MediaPageFragment) {
                       String pid = ((MediaPageFragment) fragment).getPid();
                       String channelId = ((MediaPageFragment) fragment).getChannelId();
                       extras.putString(VideoPlugin.EXTRA_FROM_PID, pid);
                       extras.putString(VideoPlugin.EXTRA_FROM_CHANNEL_ID, channelId);
                   }

                   PluginManager.get(VideoPlugin.class).startJXNewsActivity(context, mMediaModel, extras);
               }else {
                   handleNavigateToDetail(view.getContext(), fragmentId);
               }
                GVideoSensorDataManager.getInstance().clickSearchResult(mediaModel.tabId,
                                mediaModel.getTitle(), context.getString(R.string.news),
                                mediaModel.searchWord, position,mediaModel.hintSearchWord);
            }else {
                handleNavigateToDetail(view.getContext(), fragmentId);
            }
        });

        if (mMediaModel.getMediaUrls() == null || mMediaModel.getMediaUrls().size() == 0) {
            mBinding.videoTag.setVisibility(View.GONE);
        } else {
            mBinding.videoTag.setVisibility(View.VISIBLE);
        }

        getRootLayout().requestLayout();
        getRootLayout().invalidate();

    }

    @Override
    protected ViewGroup getRootLayout() {
        return mBinding.rootLayout;
    }

    @Override
    protected View getPlayView() {
        return null;
    }

    @Override
    public TextView getTitleView() {
        return mBinding.title;
    }

    @Override
    public ViewDataBinding getDataBinding() {
        return mBinding;
    }

    @Override
    public void onChangeToDetail() {

    }

    @Override
    public void onBackFeed() {

    }

    @Override
    public void mute(boolean value) {
    }

    public void handleNavigateToDetail(Context context, String mFragmentId, VideoModel model){
        Fragment fragment = MediaFragmentManager.getInstance().getFragment(mFragmentId);
        Bundle extras = new Bundle();
        if (fragment instanceof MediaPageFragment) {
            String pid = ((MediaPageFragment) fragment).getPid();
            String channelId = ((MediaPageFragment) fragment).getChannelId();
            extras.putString(VideoPlugin.EXTRA_FROM_PID, pid);
            extras.putString(VideoPlugin.EXTRA_FROM_CHANNEL_ID, channelId);
        }
        PluginManager.get(DetailPagePlugin.class).dispatchToDetail(context, model, extras);
    }
}
