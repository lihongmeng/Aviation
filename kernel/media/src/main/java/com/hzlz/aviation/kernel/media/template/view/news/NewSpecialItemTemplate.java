package com.hzlz.aviation.kernel.media.template.view.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;

import com.hzlz.aviation.kernel.base.decoration.GapItemDecoration;
import com.hzlz.aviation.kernel.base.model.anotation.MediaType;
import com.hzlz.aviation.kernel.base.model.video.VideoModel;
import com.hzlz.aviation.kernel.base.plugin.DetailPagePlugin;
import com.hzlz.aviation.kernel.base.plugin.VideoPlugin;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.databind.MediaVideoDataBind;
import com.hzlz.aviation.kernel.media.databinding.NewsTplSpecialItemBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.template.MediaBaseTemplate;
import com.hzlz.aviation.kernel.media.template.view.news.adapter.NewSpecialTextAdapter;
import com.hzlz.aviation.library.ioc.PluginManager;
import com.hzlz.aviation.library.widget.image.ImageLoaderManager;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.List;

/**
 * @author huangwei
 * date : 2021/5/24
 * desc : 新闻特殊展示块
 **/
public class NewSpecialItemTemplate extends MediaBaseTemplate {

    private NewsTplSpecialItemBinding mBinding;
    protected MediaVideoDataBind mFeedVideoDataBind;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     */
    public NewSpecialItemTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.news_tpl_special_item, parent,
                false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId,int position) {
        super.update(mediaModel, isDarkMode, fragmentId, position);

        mBinding.setVariable(BR.feedModel, mMediaModel);
        Context mContext = mBinding.getRoot().getContext();

        NewSpecialTextAdapter adapter = new NewSpecialTextAdapter(mBinding.getRoot().getContext());
        GapItemDecoration gapItemDecoration = new GapItemDecoration();
        if (mBinding.recyclerView.getItemDecorationCount() == 0) {
            gapItemDecoration.setVerticalGap(mContext.getResources().getDimensionPixelOffset(R.dimen.DIMEN_2DP));
            mBinding.recyclerView.addItemDecoration(gapItemDecoration);
        }
        adapter.addMoreData(mediaModel.getHeadTop());
        mBinding.recyclerView.setAdapter(adapter);

        List<MediaModel> mediaModelList = mediaModel.getHeadBottom();
        int sliderTime = mediaModel.getSliderTime();
        mBinding.banner.setAdapter(new BannerImageAdapter(mediaModelList) {
            @Override
            public void onBindView(Object o, Object data, int position, int size) {
                ImageLoaderManager.loadImage(
                        ((BannerImageHolder) o).imageView,
                        ((MediaModel) data).getCoverUrl(),
                        true
                );
                // Glide.with(mContext)
                //         .asBitmap()
                //         .load(((MediaModel) data).getCoverUrl())
                //         .diskCacheStrategy(DiskCacheStrategy.ALL)
                //         .placeholder(mContext.getResources().getDrawable(R.drawable.shape_solid_e6e6e6_corners_4dp))
                //         .dontAnimate()
                //         .thumbnail(0.3f)
                //         .centerCrop()
                //         .into(holder.imageView);
            }
        }).setLoopTime(sliderTime > 0 ? sliderTime : 3000)
                .setOnBannerListener((data, p) -> {
                    VideoModel videoModel=(VideoModel) data;
                    if (!MediaType.MediaTypeCheck.isMediaTypeValid(videoModel.getMediaType())) {
                        PluginManager.get(VideoPlugin.class).startNewsList(mContext, videoModel, null);
                    } else {
                        PluginManager.get(DetailPagePlugin.class).dispatchToDetail(mContext, videoModel, null);
                    }
                });
        if (mediaModelList != null && mediaModelList.size() > 1) {
            mBinding.banner.setIndicator(new CircleIndicator(mContext))
                    .setIndicatorNormalColorRes(R.color.color_ffffff)
                    .setIndicatorSelectedColorRes(R.color.color_fc284d);
        }
        ViewGroup viewGroup = getRootLayout();
        viewGroup.requestLayout();
        viewGroup.invalidate();
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
