package com.jxntv.feed.template.view;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.jxntv.feed.R;
import com.jxntv.feed.databinding.FeedOneImgTemplateLayoutBinding;
import com.jxntv.feed.databinding.FeedSlideLayoutBinding;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.template.view.MediaVideoTemplate;

/**
 * feed 视频模板视图
 */
public class FeedVideoTemplate extends MediaVideoTemplate {
    private StatHelper mStatHelper;
    private FeedRecommendTemplate mRecommendTemplate;
    public FeedVideoTemplate(Context context, ViewGroup parent) {
        super(context, parent);
        FeedSlideLayoutBinding mSlideRecommendBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.feed_slide_layout,
            getRootLayout(), true);
        FeedOneImgTemplateLayoutBinding mNormalRecommendBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.feed_one_img_template_layout,
            getRootLayout(), true);
        mRecommendTemplate = new FeedRecommendTemplate(context, parent,
            mSlideRecommendBinding, mNormalRecommendBinding);

    }

    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId,int position) {
        super.update(mediaModel, isDarkMode, fragmentId, position);
        mRecommendTemplate.initRecommendView(mediaModel, isDarkMode, fragmentId);
        mStatHelper = new StatHelper(fragmentId);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mStatHelper.statMedia(getMediaModel());
    }

    @Override
    public void onFragmentSwitchVisible() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
            || getRootLayout().isAttachedToWindow()) {
            mStatHelper.statMedia(getMediaModel());
        }
    }

    @Override
    protected void onVideoPlayBegin() {
        super.onVideoPlayBegin();
        mRecommendTemplate.onPlay();
    }

    @Override
    protected void onVideoPlayEnd() {
        super.onVideoPlayEnd();
        mRecommendTemplate.onStop();
    }
}
