package com.hzlz.aviation.kernel.media.template.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode;
import com.hzlz.aviation.kernel.media.BR;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.databind.MediaVideoDataBind;
import com.hzlz.aviation.kernel.media.databinding.MediaTplVideoBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.library.util.AsyncUtils;

/**
 * media 视频模板视图
 */
public class MediaVideoTemplate extends MediaBaseVideoTemplate {

    /**
     * 当前数据模型
     */
    private MediaTplVideoBinding mBinding;

    /**
     * 构造函数
     */
    public MediaVideoTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.media_tpl_video,
                parent, false);
        topBinding = mBinding.feedToolbarTop;
        bottomBinding = mBinding.feedToolbarInclude;
        bottomAudiovisualBinding = mBinding.feedAudiovisualBottom;
        mPlayerView = mBinding.videoInclude.playerView;
        playerViewLayout = mBinding.videoInclude.playerViewLayout;
        mPlayerView.setIsRecycler();
        initCommonView();
    }

    @Override
    public TextView getTitleView() {
        return mBinding.videoInclude.videoTopTitle.getContentText();
    }

    @Override
    public void update(@NonNull MediaModel feedModel, boolean isDarkMode, String fragmentId, int position) {
        if (!feedModel.isNormalMedia()) {
            return;
        }

        mBinding.videoInclude.setVariable(BR.feedModel, feedModel);
        if (mFeedVideoDataBind == null) {
            mFeedVideoDataBind = new MediaVideoDataBind(this, feedModel, fragmentId, position);
        }
        mBinding.videoInclude.setVariable(BR.videoBind, mFeedVideoDataBind);
        mBinding.videoInclude.executePendingBindings();
        super.update(feedModel, isDarkMode, fragmentId, position);

        mBinding.videoInclude.videoBigImg.setTag(mMediaModel.isLiveMedia());

        initLiveType(feedModel, mBinding.videoInclude.layoutLiveTag, mBinding.videoInclude.liveStartTime);

        if (mMediaModel.isLiveMedia()) {
            mBinding.videoInclude.layoutEnterLiveRoom.setOnClickListener(view -> handleNavigateToDetail(false));
            mBinding.videoInclude.layoutEnterLiveRoom.setVisibility(VISIBLE);
        } else {
            mBinding.videoInclude.layoutEnterLiveRoom.setVisibility(GONE);
        }

        if (mMediaModel.showMediaPageSource == MediaPageSource.PageSource.NEWS) {
            mBinding.feedNewsBottom.setFeedModel(mMediaModel);
            mBinding.feedNewsBottom.rootLayout.setVisibility(VISIBLE);
            setNewsSourceMaxWidth(mBinding.feedNewsBottom.source);
        } else {
            mBinding.feedNewsBottom.rootLayout.setVisibility(GONE);
        }

        if (mMediaModel.isStick() && mMediaModel.feedRecommendModelList != null
                && mMediaModel.feedRecommendModelList.size() > 0) {
            mBinding.line.setVisibility(GONE);
        }

        mBinding.videoInclude.playerView.setCanResize(false);
        mBinding.videoInclude.playerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_FILL_SCREEN);

        Resources resources = mContext.getResources();
        mBinding.videoInclude.videoTopTitle.setOnClickListener(v -> handleNavigateToDetail(false));
        mBinding.videoInclude.videoTopTitle.setContentTextSize(
                feedModel.showInNews() ? resources.getDimension(R.dimen.sp_17) : resources.getDimension(R.dimen.sp_15)
        );

        updateQuestionLayout(mBinding.videoInclude.feedLayoutQa.qaContent);
        updateQualityComment(feedModel);
        updateLinkLayout(feedModel);

        getRootLayout().requestLayout();
        getRootLayout().invalidate();
    }


    @Override
    public ViewDataBinding getDataBinding() {
        return mBinding;
    }

    @Override
    protected ViewGroup getRootLayout() {
        return mBinding.videoLayout;
    }

    @Override
    protected ViewGroup getPlayerViewParent() {
        return mBinding.videoInclude.playerViewLayout;
    }

    @Override
    protected TextView getTitleTextView() {
        return mBinding.videoInclude.videoTitle;
    }

    @Override
    protected boolean handleInterceptFullScreenEvent() {
        return false;
    }

    @Override
    public void play() {
        if (mMediaModel.isLiveMedia()) {
            handleNavigateToDetail(false);
            return;
        }
        super.play();
        mPlayerView.showCoverWithDefaultConner(mMediaModel.getCoverUrl());

        //延迟布局显示使播放过渡更顺滑
        AsyncUtils.runOnUIThread(() -> {
            if (isPlaying()) {
                mBinding.videoInclude.videoBackground.setVisibility(GONE);
                mBinding.videoInclude.videoBigImg.setVisibility(GONE);
                if (mMediaModel.showInsideVideoTitle()) {
                    mBinding.videoInclude.videoTitle.setVisibility(GONE);
                    mBinding.videoInclude.titleMask.setVisibility(GONE);
                }
                mBinding.videoInclude.layoutLiveTag.setVisibility(GONE);
            }
        }, 100);
    }

    @Override
    public void resume() {
        super.resume();
        mPlayerView.showCoverWithDefaultConner(mMediaModel.getCoverUrl());
        mBinding.videoInclude.videoBackground.setVisibility(GONE);
        mBinding.videoInclude.videoBigImg.setVisibility(GONE);
        if (mMediaModel.showInsideVideoTitle()) {
            mBinding.videoInclude.videoTitle.setVisibility(GONE);
            mBinding.videoInclude.titleMask.setVisibility(GONE);
        }
        mBinding.videoInclude.layoutLiveTag.setVisibility(GONE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBinding.videoInclude.videoBackground.setVisibility(VISIBLE);
        if (mMediaModel.showInsideVideoTitle()) {
            mBinding.videoInclude.videoTitle.setVisibility(VISIBLE);
            mBinding.videoInclude.titleMask.setVisibility(VISIBLE);
        }
        mBinding.videoInclude.layoutLiveTag.setVisibility(mMediaModel.isLiveMedia() ? VISIBLE : GONE);

        Object object = mBinding.videoInclude.videoBigImg.getTag();
        if (object == null || !(boolean) object) {
            mBinding.videoInclude.videoBigImg.setVisibility(VISIBLE);
        }

    }

    @Override
    public void stop() {
        super.stop();
        mBinding.videoInclude.videoBackground.setVisibility(VISIBLE);
        if (mMediaModel.showInsideVideoTitle()) {
            mBinding.videoInclude.videoTitle.setVisibility(VISIBLE);
            mBinding.videoInclude.titleMask.setVisibility(VISIBLE);
        }
        mBinding.videoInclude.layoutLiveTag.setVisibility(mMediaModel.isLiveMedia() ? VISIBLE : GONE);

        Object object = mBinding.videoInclude.videoBigImg.getTag();
        if (object == null || !(boolean) object) {
            mBinding.videoInclude.videoBigImg.setVisibility(VISIBLE);
        }

    }
}
