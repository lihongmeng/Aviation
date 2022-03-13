package com.jxntv.media.template.view.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.R;
import com.jxntv.media.databind.MediaVideoDataBind;
import com.jxntv.media.databinding.NewsTplNewsScrollBinding;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.template.MediaBaseTemplate;
import com.jxntv.media.template.view.news.adapter.NewsScrollAdapter;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.youth.banner.Banner;

/**
 * @author huangwei
 * date : 2021/5/24
 * desc : 滚动新闻
 **/
public class NewsScrollTemplate extends MediaBaseTemplate {

    private NewsTplNewsScrollBinding mBinding;
    protected MediaVideoDataBind mFeedVideoDataBind;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     */
    public NewsScrollTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.news_tpl_news_scroll, parent, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId, int position) {
        super.update(mediaModel, isDarkMode, fragmentId, position);

        int sliderTime = mediaModel.getSliderTime();
        mBinding.banner.setAdapter(new NewsScrollAdapter(mediaModel.getItems()))
                .setOrientation(Banner.VERTICAL)
                .setLoopTime(sliderTime > 0 ? sliderTime : 3000)
                .setOnBannerListener((data, p) -> {
                    PluginManager.get(DetailPagePlugin.class).dispatchToDetail(
                            mBinding.getRoot().getContext(),
                            (VideoModel) data,
                            null
                    );
                    GVideoSensorDataManager.getInstance().clickHotNews();
                });

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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFeedVideoDataBind = null;
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

    @Override
    public ViewDataBinding getDataBinding() {
        return mBinding;
    }

}
