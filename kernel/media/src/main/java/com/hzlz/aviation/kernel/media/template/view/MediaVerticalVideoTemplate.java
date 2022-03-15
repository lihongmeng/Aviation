package com.hzlz.aviation.kernel.media.template.view;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.hzlz.aviation.kernel.base.Constant;
import com.hzlz.aviation.kernel.liteav.model.GVideoPlayerRenderMode;
import com.hzlz.aviation.kernel.media.BR;
import com.hzlz.aviation.kernel.media.MediaFragmentManager;
import com.hzlz.aviation.kernel.media.MediaPageSource;
import com.hzlz.aviation.kernel.media.R;
import com.hzlz.aviation.kernel.media.databind.MediaVideoDataBind;
import com.hzlz.aviation.kernel.media.databinding.MediaTplVideoVerticalBinding;
import com.hzlz.aviation.kernel.media.model.MediaModel;
import com.hzlz.aviation.kernel.media.recycler.MediaPageFragment;
import com.hzlz.aviation.kernel.runtime.GVideoRuntime;
import com.hzlz.aviation.library.util.AsyncUtils;
import com.hzlz.aviation.library.util.ScreenUtils;

/**
 * media 竖直视频视图
 */
public class MediaVerticalVideoTemplate extends MediaBaseVideoTemplate {

    /**
     * 当前数据模型
     */
    private MediaTplVideoVerticalBinding mBinding;

    /**
     * c
     * 构造函数
     */
    public MediaVerticalVideoTemplate(Context context, ViewGroup parent) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.media_tpl_video_vertical,
                parent, false);
        topBinding = mBinding.feedToolbarTop;
        bottomBinding = mBinding.feedToolbarInclude;
        bottomAudiovisualBinding = mBinding.feedAudiovisualBottom;
        mPlayerView = mBinding.verticalInclude.playerView;
        playerViewLayout = mBinding.verticalInclude.playerViewLayout;
        initCommonView();
    }

    @Override
    public TextView getTitleView() {
        return mBinding.verticalInclude.verticalTopTitle.getContentText();
    }

    @Override
    public void update(@NonNull MediaModel mediaModel, boolean isDarkMode, String fragmentId, int position) {
        if (!mediaModel.isVerticalMedia()) {
            return;
        }
        super.update(mediaModel, isDarkMode, fragmentId, position);

        mBinding.verticalInclude.setVariable(BR.feedModel, mediaModel);
        if (mFeedVideoDataBind == null) {
            mFeedVideoDataBind = new MediaVideoDataBind(this, mediaModel, fragmentId, position);
        }
        mBinding.verticalInclude.setVariable(BR.videoBind, mFeedVideoDataBind);
        mBinding.verticalInclude.executePendingBindings();

        mBinding.verticalInclude.verticalImg.setTag(mMediaModel.isLiveMedia());

        initLiveType(
                mediaModel,
                mBinding.verticalInclude.layoutLiveTag,
                mBinding.verticalInclude.liveStartTime
        );

        if (mMediaModel.showMediaPageSource == MediaPageSource.PageSource.NEWS) {
            mBinding.feedNewsBottom.setFeedModel(mMediaModel);
            mBinding.feedNewsBottom.rootLayout.setVisibility(VISIBLE);
        } else {
            mBinding.feedNewsBottom.rootLayout.setVisibility(GONE);
        }

        if (mMediaModel.isStick() && mMediaModel.feedRecommendModelList != null
                && mMediaModel.feedRecommendModelList.size() > 0) {
            mBinding.line.setVisibility(GONE);
        } else {
            mBinding.line.setVisibility(VISIBLE);
        }

        updateQuestionLayout(mBinding.verticalInclude.feedLayoutQa.qaContent);
        updateQualityComment(mediaModel);
        updateLinkLayout(mediaModel);

        Resources resources = mContext.getResources();
        mBinding.verticalInclude.verticalTopTitle.setOnClickListener(v -> handleNavigateToDetail(false));
        mBinding.verticalInclude.verticalTopTitle.setContentTextSize(
                mediaModel.showInNews() ? resources.getDimension(R.dimen.sp_17) : resources.getDimension(R.dimen.sp_15)
        );

        ViewGroup.LayoutParams layoutParams = mBinding.verticalInclude.playerViewLayout.getLayoutParams();
        Fragment fragment = MediaFragmentManager.getInstance().getFragment(fragmentId);

        int width, height;
        int margin = resources.getDimensionPixelSize(R.dimen.feed_layout_margin);

        if (fragment instanceof MediaPageFragment && !Constant.CHANNEL_ID.VIDEO.equals(((MediaPageFragment) fragment).getChannelId())
                || !TextUtils.isEmpty(mediaModel.searchWord)) {
            width = resources.getDimensionPixelSize(R.dimen.DIMEN_220DP);
            height = resources.getDimensionPixelSize(R.dimen.DIMEN_320DP);
            mBinding.verticalInclude.topClickView.setVisibility(GONE);
        } else {
            width = ScreenUtils.getScreenWidth(GVideoRuntime.getAppContext()) - margin * 2;
            height = width * 17 / 14;
        }
        layoutParams.width = width;
        layoutParams.height = height;
        mBinding.verticalInclude.playerViewLayout.setLayoutParams(layoutParams);
        mBinding.verticalInclude.playerView.setCanResize(false);
        mBinding.verticalInclude.playerView.setRenderMode(GVideoPlayerRenderMode.RENDER_MODE_FILL_SCREEN);

        getRootLayout().requestLayout();
        getRootLayout().invalidate();
    }

    @Override
    protected ViewGroup getPlayerViewParent() {
        return mBinding.verticalInclude.playerViewLayout;
    }

    @Override
    protected TextView getTitleTextView() {
        return mBinding.verticalInclude.verticalTitle;
    }

    @Override
    protected boolean handleInterceptFullScreenEvent() {
        handleNavigateToDetail(false);
        return true;
    }

    @Override
    public MediaModel getMediaModel() {
        return super.getMediaModel();
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBinding.verticalInclude.verticalBackground.setVisibility(VISIBLE);
        mBinding.verticalInclude.verticalImg.setVisibility(VISIBLE);
        if (mMediaModel.showInsideVideoTitle()) {
            mBinding.verticalInclude.titleMask.setVisibility(VISIBLE);
            mBinding.verticalInclude.verticalTitle.setVisibility(VISIBLE);
        }
        mBinding.verticalInclude.layoutLiveTag.setVisibility(mMediaModel.isLiveMedia() ? VISIBLE : GONE);
        mBinding.verticalInclude.topClickView.setVisibility(GONE);

        Object object = mBinding.verticalInclude.verticalImg.getTag();
        if (object == null || !(boolean) object) {
            mBinding.verticalInclude.verticalImg.setVisibility(VISIBLE);
        }
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
                mBinding.verticalInclude.verticalBackground.setVisibility(INVISIBLE);
                mBinding.verticalInclude.verticalImg.setVisibility(GONE);
                if (mMediaModel.showInsideVideoTitle()) {
                    mBinding.verticalInclude.titleMask.setVisibility(GONE);
                    mBinding.verticalInclude.verticalTitle.setVisibility(GONE);
                }
                mBinding.verticalInclude.layoutLiveTag.setVisibility(GONE);
                mBinding.verticalInclude.topClickView.setVisibility(VISIBLE);
            }
        }, 100);

    }

    @Override
    public void resume() {
        super.resume();
        mPlayerView.showCoverWithDefaultConner(mMediaModel.getCoverUrl());
        mBinding.verticalInclude.verticalBackground.setVisibility(INVISIBLE);
        mBinding.verticalInclude.verticalImg.setVisibility(GONE);
        if (mMediaModel.showInsideVideoTitle()) {
            mBinding.verticalInclude.titleMask.setVisibility(GONE);
            mBinding.verticalInclude.verticalTitle.setVisibility(GONE);
        }
        mBinding.verticalInclude.layoutLiveTag.setVisibility(GONE);
        mBinding.verticalInclude.topClickView.setVisibility(VISIBLE);
    }

    @Override
    public void stop() {
        super.stop();
        mBinding.verticalInclude.verticalBackground.setVisibility(VISIBLE);
        if (mMediaModel.showInsideVideoTitle()) {
            mBinding.verticalInclude.titleMask.setVisibility(VISIBLE);
            mBinding.verticalInclude.verticalTitle.setVisibility(VISIBLE);
        }
        mBinding.verticalInclude.layoutLiveTag.setVisibility(mMediaModel.isLiveMedia() ? VISIBLE : GONE);
        mBinding.verticalInclude.topClickView.setVisibility(GONE);

        Object object = mBinding.verticalInclude.verticalImg.getTag();
        if (object == null || !(boolean) object) {
            mBinding.verticalInclude.verticalImg.setVisibility(VISIBLE);
        }
    }
}
