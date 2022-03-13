package com.jxntv.media.template.view.news;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;

import com.jxntv.image.ImageLoaderManager;
import com.jxntv.media.R;
import com.jxntv.media.databind.MediaVideoDataBind;
import com.jxntv.media.databinding.NewsTplNewsImageBinding;
import com.jxntv.media.model.MediaModel;
import com.jxntv.media.template.MediaBaseTemplate;
import com.jxntv.sensordata.GVideoSensorDataManager;
import com.jxntv.utils.DateUtils;

/**
 * @author huangwei
 * date : 2021/5/21
 * desc : 新闻大图、三图
 **/
public class NewsImageTemplate extends MediaBaseTemplate {

    private NewsTplNewsImageBinding mBinding;
    protected MediaVideoDataBind mFeedVideoDataBind;

    /**
     * 构造函数
     *
     * @param context 上下文环境
     */
    public NewsImageTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.news_tpl_news_image, parent, false);
    }

    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId,int position) {
        super.update(mediaModel, isDarkMode, fragmentId, position);

        mBinding.setVariable(BR.feedModel, mMediaModel);
        if (TextUtils.isEmpty(mMediaModel.getSource())
                && (mMediaModel.getAuthor() == null || TextUtils.isEmpty(mMediaModel.getAuthor().getName()))
                && mMediaModel.getCreateDate() == null
                && TextUtils.isEmpty(mMediaModel.getContentLabel())) {
            mBinding.bottom.rootLayout.setVisibility(View.GONE);
        } else {
            mBinding.bottom.rootLayout.setVisibility(View.VISIBLE);
            mBinding.bottom.setVariable(BR.feedModel, mMediaModel);
        }


        Context context = mBinding.getRoot().getContext();

        setNewsSourceMaxWidth(mBinding.bottom.source);

        mBinding.rootLayout.setOnClickListener(view -> {
            handleNavigateToDetail(view.getContext(), fragmentId);
            if (mediaModel.showInSearch()) {
                GVideoSensorDataManager.getInstance().clickSearchResult(mediaModel.tabId,
                                mediaModel.getTitle(), context.getString(R.string.news),
                                mediaModel.searchWord, position,mediaModel.hintSearchWord);
            }
        });


        if (mediaModel.getImageUrls() != null && mediaModel.getImageUrls().size() > 0) {
            if (mediaModel.getImageUrls().size() == 3) {
                mBinding.imageGroup.setVisibility(View.VISIBLE);
                mBinding.image.setVisibility(View.GONE);

                ImageLoaderManager.loadImage(mBinding.image1, mediaModel.getImageUrls().get(0), false);

                ImageLoaderManager.loadImage(mBinding.image2, mediaModel.getImageUrls().get(1), false);

                ImageLoaderManager.loadImage(mBinding.image3, mediaModel.getImageUrls().get(2), false);

            } else {
                mBinding.imageGroup.setVisibility(View.GONE);
                mBinding.image.setVisibility(View.VISIBLE);

                ImageLoaderManager.loadImage(mBinding.image, mediaModel.getImageUrls().get(0), true);
            }
        }else {
            mBinding.imageGroup.setVisibility(View.GONE);
            mBinding.image.setVisibility(View.GONE);
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
