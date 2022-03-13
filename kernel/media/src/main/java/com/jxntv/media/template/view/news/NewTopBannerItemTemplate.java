package com.jxntv.media.template.view.news;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;

import com.jxntv.base.model.anotation.MediaType;
import com.jxntv.base.model.video.VideoModel;
import com.jxntv.base.plugin.DetailPagePlugin;
import com.jxntv.base.plugin.VideoPlugin;
import com.jxntv.base.utils.BannerUtils;
import com.jxntv.image.ImageLoaderManager;
import com.jxntv.ioc.PluginManager;
import com.jxntv.media.R;
import com.jxntv.media.databind.MediaVideoDataBind;
import com.jxntv.media.databinding.NewsTplTopBannerItemBinding;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.template.MediaBaseTemplate;
import com.jxntv.media.template.view.news.adapter.NewsBannerImageAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;

/**
 * @author huangwei
 * date : 2021/5/24
 * desc : 新闻特殊展示块
 **/
public class NewTopBannerItemTemplate extends MediaBaseTemplate {

    protected MediaVideoDataBind mFeedVideoDataBind;
    private NewsTplTopBannerItemBinding mBinding;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     */
    public NewTopBannerItemTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.news_tpl_top_banner_item,
                        parent, false);
    }

    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId, int position) {
        super.update(mediaModel, isDarkMode, fragmentId, position);

        mBinding.setVariable(BR.feedModel, mMediaModel);
        Context mContext = mBinding.getRoot().getContext();

        mBinding.banner.setAdapter(new NewsBannerImageAdapter(mediaModel.getHeadTop())).setLoopTime(
                        mediaModel.getSliderTime() > 0 ? mediaModel.getSliderTime() : 3000);

        if (mediaModel.getHeadTop() != null && mediaModel.getHeadTop().size() > 1) {
            BannerUtils.initDefaultIndicator(mBinding.banner, true, IndicatorConfig.Direction.RIGHT, R.dimen.DIMEN_10DP, R.dimen.DIMEN_10DP);
        }
        if (mMediaModel.getHeadBottom() != null && mMediaModel.getHeadBottom().size() > 1) {
            initBottomImage(mBinding.imageLeft, mediaModel.getHeadBottom().get(0));
            initBottomImage(mBinding.imageRight, mediaModel.getHeadBottom().get(1));
        } else {
            mBinding.imageLeft.setVisibility(View.GONE);
            mBinding.imageRight.setVisibility(View.GONE);
        }
        getRootLayout().invalidate();

    }
    
    private void initBottomImage(ImageView imageView, MediaModel mediaModel){

        ImageLoaderManager.loadImage(imageView,mediaModel.getCoverUrl(), true);

        imageView.setOnClickListener(view -> {

            if (!MediaType.MediaTypeCheck.isMediaTypeValid(mediaModel.getMediaType())) {
                PluginManager.get(VideoPlugin.class).startNewsList(imageView.getContext(), mediaModel, null);
            } else {
                PluginManager.get(DetailPagePlugin.class).dispatchToDetail(imageView.getContext(), mediaModel, null);
            }
        });
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
